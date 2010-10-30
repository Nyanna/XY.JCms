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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IRenderer;

/**
 * is like the baserenderfactory an configuration delivering renderer instances described by an interface
 * 
 * @author Xyan
 * 
 */
public class RenderKitConfiguration extends Configuration<Map<?, IRenderer>> {

    public RenderKitConfiguration(final Map<Class<? extends IRenderer>, IRenderer> configurationValue) {
        super(ConfigurationType.renderKitConfiguration, convert(configurationValue));
    }

    /**
     * return the singleton renderer instance
     * 
     * @param rInterface
     * @return
     */
    public IRenderer get(final Class<? extends IRenderer> rInterface, final ComponentConfiguration config) {
        IRenderer value = null;
        final ClimbUp strategy = new ClimbUp(config, rInterface.getName());
        for (final String pathKey : strategy) {
            value = getConfigurationValue().get(pathKey);
            if (value != null) {
                break;
            }
        }
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory renderer was missing! "
                    + DebugUtils.printFields(rInterface));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void mergeConfiguration(final Configuration<Map<?, IRenderer>> otherConfig) {
        getConfigurationValue().putAll((Map) otherConfig.getConfigurationValue());
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }

    /**
     * converts an incoming config to simple class names
     * 
     * @param config
     * @return
     */
    private static Map<String, IRenderer> convert(final Map<?, IRenderer> config) {
        final Map<String, IRenderer> result = new HashMap<String, IRenderer>();
        for (final Entry<?, IRenderer> entry : config.entrySet()) {
            if (entry.getKey() instanceof Class<?>) {
                result.put(((Class<?>) entry.getKey()).getName(), entry.getValue());
            } else {
                result.put((String) entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
