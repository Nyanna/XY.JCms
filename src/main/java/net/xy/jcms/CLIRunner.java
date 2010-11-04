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
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.IOutWriter;

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

    /**
     * main entry poitn for the console
     * 
     * @param args
     */
    public static void main(final String[] args) {
        /**
         * first get portal configuration out from call information
         */
        // final IDataAccessContext dac = new CLIDataAccessContext(request);
        final IDataAccessContext dac = new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        /**
         * first convert the call string to an navigation/usecasestruct
         */
        if (args.length < 1 || StringUtils.isBlank(args[0])) {
            System.out.append("You have to specify at least one request String.");
            return;
        }
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(args[0], dac);

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
        final String output = UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE));

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
            ViewRunner.runView(new ConsoleOutWriter(), confTree);
        }

    }

    /**
     * output wrapper for the console
     * 
     * @author xyan
     * 
     */
    public static class ConsoleOutWriter implements IOutWriter {

        @Override
        public void append(final StringBuilder buffer) {
            System.out.append(buffer);
            System.out.append("\n");
        }

        @Override
        public void append(final String buffer) {
            System.out.append(buffer);
            System.out.append("\n");
        }

    }

}
