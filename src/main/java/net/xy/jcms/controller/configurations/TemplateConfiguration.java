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
package net.xy.jcms.controller.configurations;

import java.util.Map;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.shared.IFragment;

/**
 * describes the connection of differend page components/fragements/templates. Template inclusion is performed to an
 * simple stacking of subsequent component tree's or component configuration trees.
 * 
 * @author xyan
 * 
 */
public class TemplateConfiguration extends Configuration<Map<String, IFragment>> {

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public TemplateConfiguration(final Map<String, IFragment> configurationValue) {
        super(ConfigurationType.templateconfiguration, configurationValue);
    }

    /**
     * returns an key associated template deffinition
     * 
     * @param tmplName
     * @return
     */
    public IFragment get(final String tmplName, final ComponentConfiguration config) {
        IFragment value = null;
        final ClimbUp strategy = new ClimbUp(config, tmplName);
        for (final String pathKey : strategy) {
            value = getConfigurationValue().get(pathKey);
        }
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory fragment/template definition was not found!");
        }
    }

    /**
     * retrieves an global non component template configuration mainly used to retrieve the root fragment.
     * 
     * @param tmplName
     * @return
     */
    public IFragment get(final String tmplName) {
        return getConfigurationValue().get(tmplName);
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<String, IFragment>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

}
