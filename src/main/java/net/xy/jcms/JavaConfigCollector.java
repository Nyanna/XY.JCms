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
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.ContentRepositoryProxy;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.configurations.MessageConfigurationProxy;
import net.xy.jcms.controller.configurations.RenderKitConfiguration;
import net.xy.jcms.controller.configurations.RenderKitConfigurationProxy;
import net.xy.jcms.controller.configurations.TemplateConfiguration;
import net.xy.jcms.controller.configurations.TemplateConfigurationProxy;
import net.xy.jcms.controller.configurations.UIConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.controller.configurations.UIConfigurationProxy;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.JavaRunner.JavaDataAccessContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * processing for java clients managing the configurations.
 * 
 * @author Xyan
 * 
 */
public class JavaConfigCollector {

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(JavaConfigCollector.class);

    /**
     * does an configuration aggregation only run and returns an dummy config
     * list containing all requested configs missing and present
     * 
     * @param request
     * @param params
     * @param dac
     *            if null an new one will be created
     * @return value
     * @throws ExecutionException
     */
    public static Map<ConfigurationType, Configuration<?>> getConfig(final String request, final Map<String, Object> params,
            IDataAccessContext dac) throws ExecutionException {
        /**
         * DAC would be obmitted, or fresh created
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
        // params java properties
        // NALKey forward = fillWithParams(firstForward,parrams);

        Usecase usecase;
        Map<ConfigurationType, Configuration<?>> configs;
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
             * inject Proxy configs
             */
            usecase = new Usecase(usecase.getId(), usecase.getDescription(), usecase.getParameterList(),
                    usecase.getControllerList(), getProxyConfigs(usecase));

            /**
             * run the controllers for the usecase, maybe redirect to another
             * usecase.
             */
            configs = usecase.getConfigurations(ConfigurationType.CONTROLLERAPPLICABLE);
            forward = UsecaseAgent.executeController(usecase.getControllerList(), configs, dac, forward.getParameters());
        } while (forward != null);

        /**
         * get the configurationtree for the usecase from an empty run through
         * the componenttree
         */
        final Map<ConfigurationType, Configuration<?>> viewConfigs = JCmsHelper.getConfigurations(
                ConfigurationType.VIEWAPPLICABLE, configs);
        @SuppressWarnings("unused")
        final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfigs);
        return viewConfigs;
    }

    /**
     * converts the dummy config to an copyable string with linebreaks. calls
     * implicit getConfig.
     * 
     * @param request
     * @param params
     * @param dac
     * @return an string ready to get posted on the console
     */
    public static String getConsoleConfigString(final String request, final Map<String, Object> params,
            final IDataAccessContext dac) {
        // get all requested configs or only the missing ones ?
        final boolean getAll = params != null && params.containsKey("getAll") ? true : false;
        final StringBuilder ret = new StringBuilder();

        // is there missing config ?
        boolean isMissingConfig = false;
        try {
            final long start = System.currentTimeMillis();
            // LOG.info("Configuration aggregation started: " + start);
            final Map<ConfigurationType, Configuration<?>> configs = getConfig(request, params, dac);
            LOG.info("Execution succeeded in milliseconds "
                    + new DecimalFormat("###,###,### \u039C").format((System.currentTimeMillis() - start)));

            for (final Entry<ConfigurationType, Configuration<?>> mEntry : configs.entrySet()) {
                final Configuration<?> config = mEntry.getValue();
                switch (config.getConfigurationType()) {
                case UIConfiguration:
                    final Map<String, UI<?>> uis;
                    if (getAll) {
                        uis = ((UIConfigurationProxy) config).getPrepared();
                    } else {
                        uis = ((UIConfigurationProxy) config).getMissing();
                    }
                    if (((UIConfigurationProxy) config).isMissing()) {
                        isMissingConfig = true;
                        ret.append("!!");
                    }
                    ret.append("##### UIconfiguration:\n\n");
                    if (!uis.isEmpty()) {
                        for (final Entry<String, UI<?>> entry : uis.entrySet()) {
                            if (StringUtils.isNotBlank(entry.getValue().getDescription())) {
                                ret.append(" # ").append(entry.getValue().getDescription()).append("\n");
                            }
                            ret.append(entry.getKey()).append(" = ").append(entry.getValue().getDefaultValue())
                                    .append("\n");
                        }
                        ret.append("\n");
                    }
                    break;
                case ContentRepository:
                    final Map<String, Class<?>> content;
                    if (getAll) {
                        content = ((ContentRepositoryProxy) config).getReqContent();
                    } else {
                        content = ((ContentRepositoryProxy) config).getMissingContent();
                    }
                    if (((ContentRepositoryProxy) config).isMissing()) {
                        isMissingConfig = true;
                        ret.append("!!");
                    }
                    ret.append("##### Content configuration:\n\n");
                    if (!content.isEmpty()) {
                        for (final Entry<String, Class<?>> entry : content.entrySet()) {
                            ret.append(entry.getKey()).append(" - ").append(entry.getValue().getSimpleName())
                                    .append("\n");
                        }
                        ret.append("\n");
                    }
                    break;
                case MessageConfiguration:
                    if (((MessageConfigurationProxy) config).isMissing()) {
                        isMissingConfig = true;
                        ret.append("!!");
                    }
                    ret.append("##### Message Configuration:\n\n");
                    if (getAll) {
                        printMapHelper(ret, ((MessageConfigurationProxy) config).getKeys());
                    } else {
                        printListHelper(ret, ((MessageConfigurationProxy) config).getMissingKeys());
                    }
                    break;
                case RenderKitConfiguration:
                    if (((RenderKitConfigurationProxy) config).isMissing()) {
                        isMissingConfig = true;
                        ret.append("!!");
                    }
                    ret.append("##### RenderKit Configuration:\n\n");
                    if (getAll) {
                        printMapHelper(ret, ((RenderKitConfigurationProxy) config).getInterfaceNames());
                    } else {
                        printListHelper(ret, ((RenderKitConfigurationProxy) config).getMissingInterfaceNames());
                    }
                    break;
                case TemplateConfiguration:
                    if (((TemplateConfigurationProxy) config).isMissing()) {
                        isMissingConfig = true;
                        ret.append("!!");
                    }
                    ret.append("##### Template Configuration:\n\n");
                    if (getAll) {
                        printMapHelper(ret, ((TemplateConfigurationProxy) config).getTemplateNames());
                    } else {
                        printListHelper(ret, ((TemplateConfigurationProxy) config).getMissingTemplateNames());
                    }
                    break;
                default:
                    break;
                }
            }
            if (!isMissingConfig) {
                ret.append("\n ## No configuration is missing try running the View ##\n");
            }
            return ret.toString();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * helper for handling lists and printing it out nicely
     * 
     * @param ret
     * @param list
     */
    private static void printListHelper(final StringBuilder ret, final List<String> list) {
        if (!list.isEmpty()) {
            ret.append(StringUtils.join(list, " = \n")).append(" = \n");
            ret.append("\n");
        }
    }

    /**
     * helper for handling an printing string maps nicely
     * 
     * @param ret
     * @param map
     */
    private static void printMapHelper(final StringBuilder ret, final Map<String, String> map) {
        if (!map.isEmpty()) {
            for (final Entry<String, String> entry : map.entrySet()) {
                ret.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }
            ret.append("\n");
        }
    }

    /**
     * inits the dummy configuration objects
     * 
     * @param usecase
     * @return
     */
    private static Configuration<?>[] getProxyConfigs(final Usecase usecase) {
        final List<Configuration<?>> configs = new ArrayList<Configuration<?>>();
        configs.add(new UIConfigurationProxy((UIConfiguration) usecase
                .getConfiguration(ConfigurationType.UIConfiguration)));
        try {
            configs.add(new ContentRepositoryProxy((ContentRepository) usecase
                    .getConfiguration(ConfigurationType.ContentRepository)));
        } catch (final UnsupportedOperationException ex) {
            configs.add(new ContentRepositoryProxy());
        }
        configs.add(new MessageConfigurationProxy((MessageConfiguration) usecase
                .getConfiguration(ConfigurationType.MessageConfiguration)));
        configs.add(new RenderKitConfigurationProxy((RenderKitConfiguration) usecase
                .getConfiguration(ConfigurationType.RenderKitConfiguration)));
        configs.add(new TemplateConfigurationProxy((TemplateConfiguration) usecase
                .getConfiguration(ConfigurationType.TemplateConfiguration)));
        configs.add(usecase
                .getConfiguration(ConfigurationType.ControllerConfiguration));
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
