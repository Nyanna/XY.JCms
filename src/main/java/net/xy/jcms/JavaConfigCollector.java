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

import java.util.ArrayList;
import java.util.List;
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
import net.xy.jcms.controller.configurations.ContentRepositoryDummy;
import net.xy.jcms.controller.configurations.MessageConfigurationDummy;
import net.xy.jcms.controller.configurations.RenderKitConfigurationDummy;
import net.xy.jcms.controller.configurations.TemplateConfiguration;
import net.xy.jcms.controller.configurations.TemplateConfigurationDummy;
import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.controller.configurations.UIConfigurationDummy;
import net.xy.jcms.shared.IDataAccessContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JavaConfigCollector {

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(JCmsHttpServlet.class);

    /**
     * does an configuration aggregation only run and returns an dummy config list containing all requested configs
     * 
     * @param request
     * @param params
     * @param dac
     * @return
     * @throws ExecutionException
     */
    public static Configuration<?>[] getConfig(final String request, final Map<String, Object> params,
            final IDataAccessContext dac)
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
             * run the controllers for the usecase, maybe redirect to another usecase.
             */
            try {
                forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
            } catch (final ClassNotFoundException ex) {
                LOG.error(ex);
                throw new ExecutionException("Couldn't load an Usecase controller");
            }
        } while (forward != null);

        /**
         * get the configurationtree for the usecase from an empty run through the componenttree
         */
        final Configuration<?>[] viewConfig = getDummyConfigs(usecase);
        final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfig);
        return viewConfig;
    }

    /**
     * converts the dummy config to an copyable string with linebreaks
     * 
     * @param request
     * @param params
     * @param dac
     */
    public static String getConsoleConfigString(final String request, final Map<String, Object> params,
            final IDataAccessContext dac) {
        final StringBuilder ret = new StringBuilder();
        try {
            final Configuration<?>[] configs = getConfig(request, params, dac);
            for (final Configuration<?> config : configs) {
                switch (config.getConfigurationType()) {
                case UIConfiguration:
                    ret.append("##### UIconfiguration:\n\n");
                    for (final Entry<String, UI<?>> entry : ((UIConfigurationDummy) config).getPrepared().entrySet()) {
                        ret.append(entry.getKey()).append(" = ").append(entry.getValue().getDefaultValue())
                                .append(" # ").append(entry.getValue().getDescription()).append("\n");
                    }
                    ret.append("\n");
                    break;
                case contentRepository:
                    ret.append("##### Content configuration:\n\n");
                    for (final Entry<String, Class<?>> entry : ((ContentRepositoryDummy) config).getReqContent()
                            .entrySet()) {
                        ret.append(entry.getKey()).append(" - ").append(entry.getValue().getSimpleName()).append("\n");
                    }
                    ret.append("\n");
                    break;
                case messageConfiguration:
                    ret.append("##### Message Configuration:\n\n");
                    if (!((MessageConfigurationDummy) config).getKeys().isEmpty()) {
                        ret.append(StringUtils.join(((MessageConfigurationDummy) config).getKeys(), " = \n")).append(
                                " = \n");
                        ret.append("\n");
                    }
                    break;
                case renderKitConfiguration:
                    ret.append("##### RenderKit Configuration:\n\n");
                    if (!((RenderKitConfigurationDummy) config).getInterfaceNames().isEmpty()) {
                        ret.append(
                                StringUtils.join(((RenderKitConfigurationDummy) config).getInterfaceNames(), " = \n"))
                                .append(" = \n");
                        ret.append("\n");
                    }
                    break;
                case templateconfiguration:
                    ret.append("##### Template Configuration:\n\n");
                    if (!((TemplateConfigurationDummy) config).getTemplateNames().isEmpty()) {
                        ret.append(StringUtils.join(((TemplateConfigurationDummy) config).getTemplateNames(), " = \n"))
                                .append(" = \n");
                        ret.append("\n");
                    }
                    break;
                default:
                    break;
                }
            }
            return ret.toString();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * inits the dummy configuration objects
     * 
     * @param usecase
     * @return
     */
    private static Configuration<?>[] getDummyConfigs(final Usecase usecase) {
        final List<Configuration<?>> configs = new ArrayList<Configuration<?>>();
        configs.add(new UIConfigurationDummy());
        configs.add(new ContentRepositoryDummy());
        configs.add(new MessageConfigurationDummy());
        configs.add(new RenderKitConfigurationDummy());
        configs.add(new TemplateConfigurationDummy(
                (TemplateConfiguration) usecase.getConfiguration(ConfigurationType.templateconfiguration)
                ));
        return configs.toArray(new Configuration<?>[configs.size()]);
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
}
