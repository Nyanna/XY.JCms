/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.portal.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.JCmsHelper;

import org.apache.commons.lang.StringUtils;

/**
 * proccessor for concurrent proccessing of content retrieval instructions
 * 
 * @author xyan
 * 
 */
public class ContentInstructionProcessor {

    /**
     * an dynamic threadpool is used to fullfill requests
     */
    private static final ExecutorService THREADPOOL = JCmsHelper.getThreadPool(50, 1000, 1200l);

    /**
     * usual entry method which preconstructs three lists:
     * 1. list of instruction without dependencies which the method first runs
     * 2. dependency list x depends upon y,w,v which got dynamicly called when
     * all dependencies are solved
     * 3. glossary of all instructions
     * Error behavior, simple invalidity error will be thrown as exception.
     * Complex errors are resulting in
     * being not setted into the results.
     * After this method returns all valid instruction are prepared with an
     * Future object from which u will get the result.
     * 
     * @param instructions
     *            set of to be processed
     * @param cCaller
     *            the handler which should deliver the content upon call
     * @throws DependencyValidityError
     *             in case simple error are present in dependencies
     */
    public static void processInstructions(final List<Instruction> instructions, final IContentCaller cCaller)
            throws DependencyValidityError, Exception {
        // instruction which can be started directly
        final List<Instruction> nonDepInstrs = new ArrayList<ContentInstructionProcessor.Instruction>();
        // map representing dependencies a => c,d,e
        final Map<String, List<String>> depInstrs = new HashMap<String, List<String>>();
        // overall list of all instruction for better performance stored in an RAM struct
        final Map<String, Instruction> instructionSet = new HashMap<String, ContentInstructionProcessor.Instruction>();

        // fill the instructions
        for (final Instruction instruction : instructions) {
            // instruction.checkDone(deps)
            if (instruction.depends.isEmpty() || instruction.isDone()) {
                nonDepInstrs.add(instruction);
            } else {
                depInstrs.put(instruction.id, new ArrayList<String>(instruction.depends));
            }
            // pre init result future to instruction if not already done
            if (!instruction.isDone()) {
                final Callable<Object> task = new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return processInstruction(instruction, depInstrs, instructionSet, cCaller);
                    }
                };
                instruction.initFuture(task);
            }
            // fill to overall set
            instructionSet.put(instruction.id, instruction);
        }

        // check if all depends exists or if an instruction depends itself
        for (final Entry<String, List<String>> depInstr : depInstrs.entrySet()) {
            if (depInstr.getValue().contains(depInstr.getKey())) {
                throw new DependencyValidityError("An instruction depends on itself causing an recursion.");
            }
            if (!instructionSet.containsKey(depInstr.getKey())) {
                throw new DependencyValidityError("An instruction depends upon an non existent instruction.");
            }
        }

        for (final Instruction instruction : nonDepInstrs) {
            dispacthInstruction(instruction, depInstrs, instructionSet, cCaller);
        }
    }

    /**
     * exception wrapper for invalid instruction dependencies.
     * 
     * @author xyan
     * 
     */
    public static class DependencyValidityError extends Exception {

        private static final long serialVersionUID = 6085545467979350374L;

        /**
         * default with description
         * 
         * @param string
         */
        public DependencyValidityError(final String string) {
            super(string);
        }
    }

    /**
     * recursive method for proccessing interdependent instructions.
     * 
     * @param instruction
     *            actual
     * @param dependencys
     *            for all connected instructions
     * @param instructionSet
     *            to get the call data from
     * @param cCaller
     *            handler to get the content
     * @param resultSet
     *            which got constantly filled with the results
     * @return result of the actual instruction
     * @throws Exception
     */
    public static Object processInstruction(final Instruction instruction, final Map<String, List<String>> dependencys,
            final Map<String, Instruction> instructionSet, final IContentCaller cCaller) throws Exception {
        // call instruction get result
        final Object result = cCaller.call(instruction, getAll(instruction.depends, instructionSet));

        final String id = instruction.id;
        removeDependentAndDispatch(id, dependencys, instructionSet, cCaller);
        return result;
    }

    /**
     * removes all dependencies and dispatches instruction which have nore more dependencies left
     * 
     * @param id
     * @param dependencys
     * @param instructionSet
     * @param cCaller
     * @throws Exception
     */
    private static void removeDependentAndDispatch(final String id, final Map<String, List<String>> dependencys,
            final Map<String, Instruction> instructionSet, final IContentCaller cCaller) throws Exception {
        for (final Entry<String, List<String>> depInstr : dependencys.entrySet()) {
            // list of instruction which requiere the result of the actual
            // proceesed instruction
            final List<String> upon = depInstr.getValue();
            final boolean causeCall = removeDependent(upon, id);

            if (causeCall) {
                // create thread, call instruction
                final Instruction instr = instructionSet.get(depInstr.getKey());
                dispacthInstruction(instr, dependencys, instructionSet, cCaller);
            }
        }
    }

    /**
     * method decides if an new thread should dispatched or if the task is ready and can be removed from depList
     * 
     * @param instruction
     * @param depInstrs
     * @param instructionSet
     * @param cCaller
     * @throws Exception
     */
    private static void dispacthInstruction(final Instruction instruction, final Map<String, List<String>> depInstrs,
            final Map<String, Instruction> instructionSet, final IContentCaller cCaller) throws Exception {
        // check if an new thread is requiered or is the content already cached
        if (!instruction.isDone()) {
            final Map<String, Instruction> deps = getAll(instruction.depends, instructionSet);
            boolean allDepsReady = true;
            // if an dep is not ready skip cache check and preprare thread
            for (final Entry<String, Instruction> depResult : deps.entrySet()) {
                if (!depResult.getValue().isDone()) {
                    allDepsReady = false;
                    break;
                }
            }
            if (allDepsReady) {
                final Object result = cCaller.getFromCache(instruction, deps);
                if (result != null) {
                    instruction.setResultContent(result);
                }
            }
        }

        if (instruction.isDone()) {
            // if already done just remove dependencies
            removeDependentAndDispatch(instruction.id, depInstrs, instructionSet, cCaller);
        } else {
            // if not done dispatch an new thread
            THREADPOOL.execute(instruction.getFutureTask());
        }
    }

    /**
     * removes an dependency (more an id) from an dependency list (Stringlist). This method is internally synchronized
     * only the last thread gets an true.
     * 
     * @param upon
     *            the dependency list
     * @param id
     *            the id to be removed
     * @return true when the list afterwards is empty
     */
    private static boolean removeDependent(final List<String> upon, final String id) {
        final boolean causeCall;
        synchronized (upon) {
            // if an dependency is solved romove it
            if (upon.contains(id)) {
                upon.remove(id);
                // if this was the last dependency call the instruction in
                // an new thread
                causeCall = upon.isEmpty();
            } else {
                // i havn't removed the last dependency
                causeCall = false;
            }
        }
        return causeCall;
    }

    /**
     * helper for getting only the dependent instructions out of an map
     * 
     * @param depends
     * @param instructionSet
     * @return
     */
    private static Map<String, Instruction> getAll(final List<String> depends,
            final Map<String, Instruction> instructionSet) {
        final Map<String, Instruction> sub = new HashMap<String, Instruction>();
        for (final String dep : depends) {
            sub.put(dep, instructionSet.get(dep));
        }
        return sub;
    }

    /**
     * deffinition of an instruction usually and wrapped mapp with special
     * fields. this object is from outward sight immutable and can be without
     * risk given out.
     * 
     * @author xyan
     * 
     */
    public static class Instruction extends HashMap<String, String> {
        private static final long serialVersionUID = 913149022974225478L;

        /**
         * id of this instruction
         */
        public final String id;

        /**
         * list of dependent instruction id's, immutable list.
         */
        public final List<String> depends;

        /**
         * creates an future object to represent the results
         */
        private FutureTask<Object> result = null;

        /**
         * from cache retrieved result content
         */
        private Object resultContent = null;

        /**
         * default, null values are permited also empty strings
         * 
         * @param id
         * @param depends
         */
        public Instruction(final String id, final List<String> depends, final Map<String, String> config) {
            super(config);
            if (StringUtils.isBlank(id) || depends == null || config == null) {
                throw new IllegalArgumentException("Can't initialize with null values.");
            }
            this.id = id;
            this.depends = Collections.unmodifiableList(depends);
        }

        @Override
        public String toString() {
            return DebugUtils.printFields(id, depends);
        }

        /**
         * public method for retrieving the result
         * 
         * @return content
         * @throws ExecutionException
         * @throws InterruptedException
         */
        public Object getResult() throws InterruptedException, ExecutionException {
            if (resultContent != null) {
                return resultContent;
            }
            return result.get();
        }

        /**
         * public method for retrieving the result, timout in seconds
         * 
         * @param timeout
         * @return content
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws TimeoutException
         */
        public Object getResult(final int timeout) throws InterruptedException, ExecutionException,
                TimeoutException {
            if (resultContent != null) {
                return resultContent;
            }
            return result.get(Long.valueOf(timeout), TimeUnit.SECONDS);
        }

        /**
         * public method for retrieving the result
         * 
         * @param timeout
         * @param unit
         * @return content
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws TimeoutException
         */
        public Object getResult(final long timeout, final TimeUnit unit) throws InterruptedException,
                ExecutionException,
                TimeoutException {
            if (resultContent != null) {
                return resultContent;
            }
            return result.get(timeout, unit);
        }

        /**
         * wrapps isDone from future
         * 
         * @return if the future already done
         */
        public boolean isDone() {
            if (resultContent != null) {
                return Boolean.TRUE;
            }
            if (result == null) {
                return Boolean.FALSE;
            }
            return result.isDone();
        }

        /**
         * the result future to get executed
         * 
         * @return value
         */
        FutureTask<Object> getFutureTask() {
            return result;
        }

        /**
         * internal setter for the future
         * 
         * @param result
         */
        void initFuture(final Callable<Object> task) {
            result = new FutureTask<Object>(task);
        }

        /**
         * overwrites future result handling with concrete object mostly from cache
         * 
         * @param content
         */
        void setResultContent(final Object content) {
            resultContent = content;
        }
    }

    /**
     * interface needed
     * 
     * @author xyan
     * 
     */
    public static interface IContentCaller {

        /**
         * calls an content handler to requiere the content object.
         * 
         * @param instruction
         *            the actual instruction for which the content should be
         *            retrieved
         * @param resultSet
         *            an set of declared dependent instructions
         * @return content
         */
        public Object call(final Instruction instruction, final Map<String, Instruction> resultSet) throws Exception;

        /**
         * this method should return the content from cache only if not cached it should return null
         * 
         * @param instruction
         * @param resultSet
         * @return content if it was cached otherwise null
         */
        public Object getFromCache(final Instruction instruction, final Map<String, Instruction> resultSet)
                throws Exception;
    }
}
