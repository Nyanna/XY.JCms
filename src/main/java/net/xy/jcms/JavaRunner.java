/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with XY.JCms. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms;

import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseAgent.NoUsecaseFound;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.IOutWriter;

import org.apache.log4j.Logger;

/**
 * adaption from JCms to get called via java
 * 
 * @author xyan
 * 
 */
public class JavaRunner {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(JCmsHttpServlet.class);

    /**
     * simple null delegate
     * 
     * @param request
     * @return value
     * @throws ExecutionException
     */
    public static String execute(final String request) throws ExecutionException {
        return execute(request, null, null);
    }

    /**
     * simple null delegate
     * 
     * @param request
     * @param params
     * @return value
     * @throws ExecutionException
     */
    public static String execute(final String request, final Map<String, Object> params) throws ExecutionException {
        return execute(request, params, null);
    }

    /**
     * entrypoint returns the rendered output
     * 
     * @param request
     * @param params
     * @param dac
     * @return value
     * @throws ExecutionException
     */
    public static String execute(final String request, final Map<String, Object> params, final IDataAccessContext dac)
            throws ExecutionException {
        /**
         * DAC would be obmitted
         */

        /**
         * first convert the call string to an navigation/usecasestruct
         */
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(request, dac);

        // run the protocol adapter which fills the struct with parameters from
        // console parameters & environment vars
        // NALKey forward = fillWithParams(firstForward,parrams);
        NALKey forward = firstForward;

        Usecase usecase;
        do {
            /**
             * find the corresponding usecase
             */
            try {
                usecase = UsecaseAgent.findUsecaseForStruct(forward, dac);
            } catch (final NoUsecaseFound e) {
                LOG.error(e);
                throw new ExecutionException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another
             * usecase.
             */
            try {
                forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
            } catch (final ClassNotFoundException ex) {
                LOG.error(ex);
                throw new ExecutionException("Couldn't load an Usecase controller");
            }
        } while (forward != null);

        // no response adapter for the console is needed

        /**
         * at this point caching takes effect by the safe asumption that the
         * same configuration leads to the same result. realized through hashing
         * and persistance.
         */
        final String output = UsecaseAgent
                .applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), null);

        if (output != null) {
            return output;
        } else {
            /**
             * get the configurationtree for the usecase from an empty run
             * through the componenttree
             */
            final Configuration<?>[] viewConfig = usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE);
            final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfig);

            /**
             * run and return the rendering tree through streamprocessing to the
             * client
             */
            final BufferAppender buffer = new BufferAppender();
            ViewRunner.runView(buffer, confTree);

            final String strBuffer = buffer.toString();
            UsecaseAgent
                    .applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), strBuffer);
            return strBuffer;
        }
    }

    /**
     * simple wrapped exception
     * 
     * @author xyan
     * 
     */
    public static class ExecutionException extends Exception {
        public ExecutionException(final Exception e) {
            super(e);
        }

        public ExecutionException(final String string) {
            super(string);
        }

        private static final long serialVersionUID = -2856402276029349588L;

    }

    /**
     * simple buffer container
     * 
     * @author xyan
     * 
     */
    public static class BufferAppender implements IOutWriter {
        /**
         * hold the output
         */
        private final StringBuilder hold = new StringBuilder();

        @Override
        public void append(final StringBuilder buffer) {
            hold.append(buffer);
            hold.append("\n");
        }

        @Override
        public void append(final String buffer) {
            hold.append(buffer);
            hold.append("\n");
        }

        @Override
        public String toString() {
            return hold.toString();
        }
    }
}
