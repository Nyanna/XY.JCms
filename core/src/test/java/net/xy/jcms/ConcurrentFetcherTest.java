package net.xy.jcms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import net.xy.jcms.portal.controller.ContentInstructionProcessor;
import net.xy.jcms.portal.controller.ContentInstructionProcessor.IContentCaller;
import net.xy.jcms.portal.controller.ContentInstructionProcessor.Instruction;

/**
 * tests the
 * 
 * @author xyan
 * 
 */
public class ConcurrentFetcherTest {

    @Test
    @SuppressWarnings("serial")
    public void testFetch() throws Exception {
        final List<Instruction> ins = new ArrayList<ContentInstructionProcessor.Instruction>();
        ins.add(new Instruction("Henne", new ArrayList<String>() {
            {
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Kücken", new ArrayList<String>() {
            {
                add("Henne");
                add("Hahn");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Hahn", new ArrayList<String>() {
            {
                add("Henne");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Bauer", new ArrayList<String>() {
            {
                add("Henne");
                add("Hahn");
                add("Kücken");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Ei", new ArrayList<String>() {
            {
                add("Henne");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Xyan", new ArrayList<String>() {
            {
                add("Ei");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Marder", new ArrayList<String>() {
            {
                add("Henne");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Züchter", new ArrayList<String>() {
            {
                add("Hahn");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Viehzucht", new ArrayList<String>() {
            {
                add("Züchter");
            }
        }, new HashMap<String, String>()));
        ins.add(new Instruction("Handel", new ArrayList<String>() {
            {
                add("Viehzucht");
                add("Xyan");
            }
        }, new HashMap<String, String>()));
        ContentInstructionProcessor.processInstructions(ins, new MockCaller());
        for (final Instruction instr : ins) {
            while (!instr.isDone()) {
                // wait until all is done
            }
        }
    }

    public static class MockCaller implements IContentCaller {

        @Override
        public Object call(final Instruction instruction, final Map<String, Instruction> resultSet) {
            final StringBuilder deps = new StringBuilder();
            for (final String depend : instruction.depends) {
                try {
                    deps.append(depend).append("=").append(resultSet.get(depend).getResult(1L, TimeUnit.SECONDS))
                            .append(" ");
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (final ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (final TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.append("\nFetched: " + instruction.id + "|" + instruction.depends + "\n"
                        + "It was depending on: " + deps);
            return instruction.id;
        }

        @Override
        public Object getFromCache(final Instruction instruction, final Map<String, Instruction> resultSet) {
            return null;
        }
    }
}
