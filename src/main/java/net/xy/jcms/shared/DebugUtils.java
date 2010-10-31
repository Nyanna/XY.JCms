package net.xy.jcms.shared;

/**
 * helper class with various methods
 * 
 * @author xyan
 * 
 */
public class DebugUtils {

    /**
     * concatenates various object to be proper displayed on console or vice versa
     * 
     * @param args
     * @return
     */
    public static String printFields(final Object... args) {
        final StringBuilder ret = new StringBuilder();
        for (final Object entry : args) {
            ret.append("[").append(entry.getClass().getSimpleName()).append("=").append(entry.toString()).append("]");
        }
        return ret.toString();
    }
}
