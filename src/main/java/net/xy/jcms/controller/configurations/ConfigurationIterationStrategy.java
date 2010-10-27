package net.xy.jcms.controller.configurations;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;


/**
 * provides helper methods for iterating ofer component configuration tree's
 * 
 * @author xyan
 * 
 */
public class ConfigurationIterationStrategy {

    /**
     * climbs up the component tree comp1.comp2.comp3.key, comp1.comp2.key, comp1.key. Can be used in foreach.
     * 
     * @author xyan
     * 
     */
    public static class ClimbUp implements Iterator<String>, Iterable<String> {

        /**
         * holds the actual configuration element
         */
        private ComponentConfiguration actual;

        /**
         * holds the requested key
         */
        private final String requestedKey;

        /**
         * on the first run get the current path instead of the parent path
         */
        private boolean firstRun = true;

        /**
         * default constructor
         * 
         * @param actual
         * @param requestedKey
         */
        public ClimbUp(final ComponentConfiguration actual, final String requestedKey) {
            if (actual == null || StringUtils.isBlank(requestedKey)) {
                throw new IllegalArgumentException("Null values obmitted");
            }
            this.actual = actual;
            this.requestedKey = requestedKey;
        }

        @Override
        public boolean hasNext() {
            return firstRun || actual.getParent() != null;
        }

        @Override
        public String next() {
            if (firstRun) {
                firstRun = false;
            } else {
                actual = actual.getParent();
            }
            final String key = actual.getComponentPath() + ComponentConfiguration.COMPONENT_PATH_SEPARATOR
                    + requestedKey;
            return key;
        }

        @Override
        public void remove() {
            return;
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }

    }

    /**
     * checks the actual path comp1.comp2.comp3.key and the root comp1.key
     * 
     * @author xyan
     * 
     */
    public static class FullPathOrRoot implements Iterator<String>, Iterable<String> {

        /**
         * holds the actual configuration element
         */
        private ComponentConfiguration actual;

        /**
         * holds the requested key
         */
        private final String requestedKey;

        /**
         * on the first run get the current path instead of the root path
         */
        private boolean firstRun = true;

        /**
         * default constructor
         * 
         * @param actual
         * @param requestedKey
         */
        public FullPathOrRoot(final ComponentConfiguration actual, final String requestedKey) {
            if (actual == null || StringUtils.isBlank(requestedKey)) {
                throw new IllegalArgumentException("Null values obmitted");
            }
            this.actual = actual;
            this.requestedKey = requestedKey;
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return firstRun || actual.getParent() != null;
        }

        @Override
        public String next() {
            if (firstRun) {
                firstRun = false;
            } else {
                while (actual.getParent() != null) {
                    actual = actual.getParent();
                }
            }
            return actual.getComponentPath() + ComponentConfiguration.COMPONENT_PATH_SEPARATOR + requestedKey;
        }

        @Override
        public void remove() {
            return;
        }

    }

    /**
     * returns the full qualified component path
     * 
     * @param actual
     * @param requestedKey
     * @return
     */
    public static String fullPath(final ComponentConfiguration actual, final String requestedKey) {
        return actual.getComponentPath() + ComponentConfiguration.COMPONENT_PATH_SEPARATOR + requestedKey;
    }
}
