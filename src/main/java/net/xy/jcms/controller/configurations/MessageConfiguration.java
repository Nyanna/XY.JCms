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

import java.util.Map.Entry;
import java.util.Properties;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.FullPathOrRoot;

/**
 * implements an default property configuration for messages
 * 
 * @author xyan
 * 
 */
public class MessageConfiguration extends Configuration<Properties> {

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public MessageConfiguration(final Properties configurationValue) {
        super(ConfigurationType.messageConfiguration, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Properties> otherConfig) {
        for (final Entry<Object, Object> entry : otherConfig.getConfigurationValue().entrySet()) {
            getConfigurationValue().put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * get an message text
     * 
     * @param key
     * @return
     */
    public String getMessage(final String key, final ComponentConfiguration config) {
        String value = null;
        final FullPathOrRoot strategy = new FullPathOrRoot(config, key);
        for (final String pathKey : strategy) {
            value = getConfigurationValue().getProperty(pathKey);
        }
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory message configuration was missing!");
        }
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
