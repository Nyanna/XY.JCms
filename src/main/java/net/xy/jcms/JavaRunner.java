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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
 * adaption from JCms to get called via java. This represents the cycle typical
 * for an java client.
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
        return execute(request, null, new JavaDataAccessContext(request, null));
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
        return execute(request, params, new JavaDataAccessContext(request, null));
    }

    /**
     * entrypoint returns the rendered output and executes the cycle.
     * 
     * @param request
     * @param params
     * @param dac
     *            if null an new one will be created
     * @return value
     * @throws ExecutionException
     */
    public static String execute(final String request, final Map<String, Object> params, IDataAccessContext dac)
            throws ExecutionException {
        /**
         * DAC would be obmitted
         */
        if (dac == null) {
            dac = new JavaDataAccessContext(request, null);
        }

        /**
         * first convert the call string to an navigation/usecasestruct
         */
        NALKey forward = NavigationAbstractionLayer.translatePathToKey(dac);
        if (forward == null) {
            new IllegalArgumentException("Request path could not be translated to an NALKey.");
        }

        // run the protocol adapter which fills the struct with parameters from
        // parameters or java properties
        // NALKey forward = fillWithParams(firstForward,parrams);

        Usecase usecase;
        NALKey cacheKey;
        do {
            /**
             * find the corresponding usecase
             */
            try {
                usecase = UsecaseAgent.findUsecaseForStruct(forward, dac);
                cacheKey = UsecaseAgent.destinctCacheKey(usecase, forward);
            } catch (final NoUsecaseFound e) {
                LOG.error(e);
                throw new ExecutionException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another
             * usecase.
             */
            forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
        } while (forward != null);

        // no response adapter for the console is needed

        /**
         * at this point late caching takes effect by the safe asumption that
         * the same configuration leads to the same result. realized through
         * hashing and persistance.
         */
        final String output = UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE),
                cacheKey, null);

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
             * client. fo java clients by default an stringbuilder is used.
             */
            final BufferAppender buffer = new BufferAppender();
            ViewRunner.runView(buffer, confTree);

            /**
             * use caching for later usage
             */
            final String strBuffer = buffer.toString();
            UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), cacheKey,
                    strBuffer);
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

    /**
     * simple context for java calls
     * 
     * @author Xyan
     * 
     */
    public static class JavaDataAccessContext implements IDataAccessContext {
        /**
         * the initial request
         */
        private final String request;

        /**
         * stores properties for java
         */
        private final Map<Object, Object> properties;

        /**
         * default constructor
         * 
         * @param request
         */
        JavaDataAccessContext(final String request, final Map<Object, Object> properties) {
            this.request = request;
            if (properties != null && !properties.isEmpty()) {
                this.properties = properties;
            } else {
                this.properties = new HashMap<Object, Object>();
            }
        }

        @Override
        public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
            final StringBuilder cmd = new StringBuilder(path);
            if (parameters != null && !parameters.isEmpty()) {
                for (final Entry<Object, Object> entry : parameters.entrySet()) {
                    cmd.append(" ").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            return cmd.toString();
        }

        @Override
        public String getRequestPath() {
            return request;
        }

        @Override
        public Object getProperty(final Object key) {
            return properties.get(key);
        }

    }
}
