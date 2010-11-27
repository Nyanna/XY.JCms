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

import java.util.Map;

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
     * the main entry template for all usecases
     */
    private static final String ENTRY_TEMPLATE = "root";

    /**
     * aggregates the component configuration tree
     * 
     * @param configuration
     *            view applicable configurations
     * @return value
     */
    public static ComponentConfiguration runConfiguration(final Map<ConfigurationType, Configuration<?>> configurations) {
        final TemplateConfiguration tmplConfig = (TemplateConfiguration) configurations
                .get(ConfigurationType.TemplateConfiguration);
        if (tmplConfig != null) {
            final IFragment root = tmplConfig.get(ENTRY_TEMPLATE);
            if (root != null) {
                final ComponentConfiguration rootConfig = root.getConfiguration();
                if (rootConfig != null) {
                    return initializeConfigurations(rootConfig, configurations);
                }
                throw new IllegalArgumentException("Root template doesn't returns an configuration object");
            }
        }
        throw new IllegalArgumentException("At least an root template has to be specified.");
    }

    /**
     * builds the component tree and initializes its config
     * 
     * @param draftTree
     * @param model
     * @return value
     */
    private static ComponentConfiguration initializeConfigurations(final ComponentConfiguration rootConfig,
            final Map<ConfigurationType, Configuration<?>> model) {
        ComponentConfiguration
                .initialize(rootConfig,
                        (ContentRepository) model.get(ConfigurationType.ContentRepository),
                        (TemplateConfiguration) model.get(ConfigurationType.TemplateConfiguration),
                        (UIConfiguration) model.get(ConfigurationType.UIConfiguration),
                        (MessageConfiguration) model.get(ConfigurationType.MessageConfiguration),
                        (RenderKitConfiguration) model.get(ConfigurationType.RenderKitConfiguration));
        return rootConfig;
    }

    /**
     * runs the output rendering with an filled configuration tree
     * 
     * @param out
     * @param configuration
     */
    public static void runView(final IOutWriter out, final ComponentConfiguration configuration) {
        if (configuration == null || out == null) {
            throw new IllegalArgumentException(
                    "Runview has to be executed with an outwriter and an configuration tree.");
        }
        ComponentConfiguration.render(out, configuration);
    }

}
