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

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseAgent.NoUsecaseFound;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.IOutWriter;
import net.xy.jcms.shared.cache.XYCache;

/**
 * adaption of JCms to run on an console
 * 
 * @author xyan
 * 
 */
public class CLIRunner {

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(CLIRunner.class);
    static {
        LOG.info("CLIRunner was loaded by " + CLIRunner.class.getClassLoader().getClass().getName());
    }

    /**
     * main entry point for the console
     * 
     * @param args
     */
    public static void main(final String[] args) {
        new CLIRunner(args);
    }

    /**
     * sole constructor
     * 
     * @throws IOException
     */
    public CLIRunner() {}

    /**
     * constructor automaticly initiates the programm
     * 
     * @param args
     * @throws IOException
     */
    public CLIRunner(final String[] args) {
        final long start = System.nanoTime();
        // LOG.info("Execution started: " + start);
        try {
            Main(args);
        } finally {
            XYCache.destroy();
        }
        LOG.info("Execution succeeded in nanoseconds "
                + new DecimalFormat("###,###,### \u039C").format((System.nanoTime() - start) / 1000));
    }

    /**
     * instance main method
     */
    protected void Main(final String[] args) {
        /**
         * first get portal configuration out from call information
         */
        if (args.length < 1 || StringUtils.isBlank(args[0])) {
            System.out.append("You have to specify at least one request String.");
            return;
        }
        final IDataAccessContext dac = new CLIDataAccessContext(args[0]);

        /**
         * first convert the call string to an navigation/usecasestruct
         */
        LOG.info("Run on Console: " + DebugUtils.printFields(args[0]));
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(dac);
        if (firstForward == null) {
            new IllegalArgumentException("Request path could not be translated to an NALKey.");
        }
        @SuppressWarnings("unchecked")
        final long cacheTimeout = firstForward.getParameter("cache") != null ? new Long(
                ((List<String>) firstForward.getParameter("cache")).get(0)) : -1;

        // run the protocol adapter which fills the struct with parameters from
        // console parameters & environment vars
        // NALKey forward = CLIConsoleArgumentAndEvironmentAdapter.apply();
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
                throw new RuntimeException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another usecase.
             */
            try {
                forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
            } catch (final ClassNotFoundException ex) {
                LOG.error(ex);
                throw new RuntimeException("Couldn't load an Usecase controller");
            }
        } while (forward != null);

        // no response adapter for the console is needed

        /**
         * at this point caching takes effect by the safe asumption that the same configuration leads to the same
         * result. realized through hashing and persistance.
         */
        final String output = UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE),
                firstForward, null, cacheTimeout);

        if (output != null) {
            System.out.append(output);
        } else {
            /**
             * get the configurationtree for the usecase from an empty run through the componenttree
             */
            final Configuration<?>[] viewConfig = usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE);
            final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfig);

            /**
             * run and return the rendering tree through streamprocessing to the client
             */
            final ConsoleOutWriter out = new ConsoleOutWriter();
            ViewRunner.runView(out, confTree);

            /**
             * caches the ouput for the future
             */
            UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), firstForward, out
                    .getBuffer().toString(), cacheTimeout);
        }

    }

    /**
     * output wrapper for the console
     * 
     * @author xyan
     * 
     */
    public static class ConsoleOutWriter implements IOutWriter {
        /**
         * internal buffer needed for caching of the complete output
         */
        private final StringBuilder internalBuffer = new StringBuilder();

        @Override
        public void append(final StringBuilder buffer) {
            System.out.append(buffer);
            System.out.append("\n");
            internalBuffer.append(buffer);
            internalBuffer.append("\n");
        }

        @Override
        public void append(final String buffer) {
            System.out.append(buffer);
            System.out.append("\n");
            internalBuffer.append(buffer);
            internalBuffer.append("\n");
        }

        /**
         * returns the buffer stored for putput caching
         * 
         * @return value
         */
        public StringBuilder getBuffer() {
            return internalBuffer;
        }

    }

    /**
     * implements an simple access contest from the console
     * 
     * @author Xyan
     * 
     */
    public static class CLIDataAccessContext implements IDataAccessContext {
        /**
         * the initial request
         */
        private final String request;

        /**
         * default constructor
         * 
         * @param request
         */
        public CLIDataAccessContext(final String request) {
            this.request = request;
        }

        @Override
        public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
            final StringBuilder cli = new StringBuilder(path);
            if (parameters != null && !parameters.isEmpty()) {
                for (final Entry<Object, Object> entry : parameters.entrySet()) {
                    cli.append(" ").append(entry.getKey()).append("=").append("\"").append(entry.getValue())
                            .append("\"");
                }
            }
            return cli.toString();
        }

        @Override
        public String getRequestPath() {
            return request;
        }

        @Override
        public Object getProperty(final Object key) {
            // TODO [LOW] parse and save setting from cmdline, param should not be saved in NAL or usecase
            return null;
        }

    };

}
