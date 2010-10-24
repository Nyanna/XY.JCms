/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations;

import java.util.Map;

import net.xy.jcms.shared.IRenderer;

/**
 * is like the baserenderfactory an configuration delivering renderer instances
 * described by an interface
 * 
 * @author Xyan
 * 
 */
public class RenderKitConfiguration extends Configuration<Map<Class, Object>> {

    public RenderKitConfiguration(final ConfigurationType configurationType, final Map<Class, Object> configurationValue) {
        super(ConfigurationType.renderKitConfiguration, configurationValue);
    }

    /**
     * return the singleton renderer instance
     * 
     * @param rInterface
     * @return
     */
    public IRenderer get(final Class rInterface) {
        final IRenderer value = (IRenderer) getConfigurationValue().get(rInterface);
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory message configuration was missing!");
        }
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<Class, Object>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }
}
