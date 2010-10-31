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
package net.xy.jcms.controller;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.configurations.RenderKitConfiguration;
import net.xy.jcms.controller.configurations.TemplateConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.IOutWriter;

/**
 * these runner gets the configuration tree and performs the output rendering
 * 
 * @author xyan
 * 
 */
public class ViewRunner {
    /**
     * the main entry class for all usecases
     */
    private static final String ENTRY_TEMPLATE = "root";

    /**
     * aggregates the component configuration tree
     * 
     * @param configuration
     * @return
     */
    public static ComponentConfiguration runConfiguration(final Configuration<?>[] configuration) {
        final TemplateConfiguration tmplConfig = (TemplateConfiguration) getConfigurationByType(
                ConfigurationType.templateconfiguration, configuration);
        if (tmplConfig != null) {
            final IFragment root = tmplConfig.get(ENTRY_TEMPLATE);
            if (root != null) {
                final ComponentConfiguration rootConfig = root.getConfiguration();
                if (rootConfig != null) {
                    return initializeConfigurations(rootConfig, configuration);
                }
                throw new IllegalArgumentException("Root template doesn't returns an configuration object");
            }
        }
        throw new IllegalArgumentException("At least an root template has to be specified.");
    }

    /**
     * fills and prepares an empty or default component configuration problem
     * 
     * @param draftTree
     * @param model
     * @return
     */
    private static ComponentConfiguration initializeConfigurations(final ComponentConfiguration rootConfig,
            final Configuration<?>[] model) {
        ComponentConfiguration.initialize(rootConfig,
                (ContentRepository) getConfigurationByType(ConfigurationType.contentRepository, model),
                (TemplateConfiguration) getConfigurationByType(ConfigurationType.templateconfiguration, model),
                (UIConfiguration) getConfigurationByType(ConfigurationType.UIConfiguration, model),
                (MessageConfiguration) getConfigurationByType(ConfigurationType.messageConfiguration, model),
                (RenderKitConfiguration) getConfigurationByType(ConfigurationType.renderKitConfiguration, model));
        return rootConfig;
    }

    /**
     * gets an configuration out of an configurationlist
     * 
     * @param configuration
     * @return
     */
    private static Configuration<?> getConfigurationByType(final ConfigurationType type,
            final Configuration<?>[] configuration) {
        for (final Configuration<?> config : configuration) {
            if (type.equals(config.getConfigurationType())) {
                return config;
            }
        }
        return null;
    }

    /**
     * runs the output rendering with an filled configuration tree
     * 
     * @param out
     * @param configuration
     */
    public static void runView(final IOutWriter out, final ComponentConfiguration configuration) {
        if (configuration == null || out == null) {
            throw new IllegalArgumentException("Runview has to be executed with an outwriter and an configuration tree.");
        }
        ComponentConfiguration.render(out, configuration);
    }

}
