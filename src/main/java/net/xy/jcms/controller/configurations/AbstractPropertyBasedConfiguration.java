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

import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.shared.DebugUtils;

/**
 * abstract implementation for all property based configuration types
 * 
 * @author Xyan
 * 
 */
public abstract class AbstractPropertyBasedConfiguration extends Configuration<Properties> {

    /**
     * default
     * 
     * @param configurationType
     * @param configurationValue
     */
    public AbstractPropertyBasedConfiguration(final ConfigurationType configurationType, final Properties configurationValue) {
        super(configurationType, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Properties> otherConfig) {
        for (final Entry<Object, Object> entry : otherConfig.getConfigurationValue().entrySet()) {
            getConfigurationValue().put(entry.getKey(), entry.getValue());
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

    /**
     * parses an string to an property
     * 
     * @param configString
     * @return value
     */
    protected static Properties initPropertiesByString(final String configString) {
        final Properties properties = new Properties();
        final String[] lines = configString.split("\n");
        for (final String line : lines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }
            final String[] parsed = line.trim().split("=", 2);
            try {
                final String key = parsed[0].trim();
                final String value = parsed[1].trim();
                properties.setProperty(key, value);
            } catch (final IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException("Error by parsing body configuration line. "
                        + DebugUtils.printFields(line));
            }
        }
        return properties;
    }

}
