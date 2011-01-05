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
import java.util.Properties;

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
    public AbstractPropertyBasedConfiguration(final ConfigurationType configurationType,
            final Properties configurationValue) {
        super(configurationType, configurationValue);
    }

    /**
     * merges two pproperties
     * 
     * @param config1
     * @param config2
     * @return
     */
    protected Properties mergeConfiguration(final Properties config1, final Properties config2) {
        final Properties result = new Properties();
        result.putAll(config1);
        result.putAll(config2);
        return result;
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
     * parses an string to an property object
     * 
     * @param configString
     * @param mounted
     *            where this configuration was inserted e.g. root.fragmentOne adjusts relative pathes of the form
     *            .comp4.comp5 with the given mountpoint to comp1.comp4.comp5
     * @return value
     */
    protected static Properties initPropertiesByString(final String configString, final String mount) {
        final Properties prop = new Properties();
        prop.putAll(initMapByString(configString, mount));
        return prop;
    }

    /**
     * parses an map out of the string in property style
     * 
     * @param configString
     * @param mounted
     *            where this configuration was inserted e.g. root.fragmentOne adjusts relative pathes of the form
     *            .comp4.comp5 with the given mountpoint to comp1.comp4.comp5
     * @return value
     */
    protected static Map<String, String> initMapByString(final String configString, final String mount) {
        final Map<String, String> properties = new HashMap<String, String>();
        final String[] lines = configString.split("\n");
        String lastValue = null; // to append additional data
        String lastKey = null; // to append additional data
        for (final String line : lines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }
            if (!line.contains("=")) {
                // append line to the last one
                lastValue = lastValue + line;
                properties.put(lastKey, lastValue);
            } else {
                // read an new key
                final String[] parsed = line.trim().split("=", 2);
                try {
                    lastKey = parsed[0].trim();
                    if (lastKey.startsWith(ComponentConfiguration.COMPONENT_PATH_SEPARATOR)) {
                        // prepend relative path with mount
                        lastKey = mount + lastKey;
                    }
                    lastValue = parsed[1].trim();
                    properties.put(lastKey, lastValue);
                } catch (final IndexOutOfBoundsException ex) {
                    throw new IllegalArgumentException("Error by parsing body configuration line. "
                            + DebugUtils.printFields(line));
                }
            }
        }
        return properties;
    }
}
