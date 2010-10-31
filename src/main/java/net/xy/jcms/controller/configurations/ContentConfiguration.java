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

/**
 * implements an default content configuration with properties
 * 
 * @author xyan
 * 
 */
public class ContentConfiguration extends AbstractPropertyBasedConfiguration {

    /**
     * default
     * 
     * @param configurationValue
     */
    public ContentConfiguration(final Properties configurationValue) {
        super(ConfigurationType.contentConfiguration, configurationValue);
    }

    /**
     * get an content property
     * 
     * @param key
     * @return
     */
    public String getConfig(final String key, final ComponentConfiguration config) {
        return getConfigurationValue().getProperty(key);
    }

    /**
     * creates an config based on parsing an string
     * 
     * @param configString
     * @return
     */
    public static ContentConfiguration initByString(final String configString) {
        return new ContentConfiguration(AbstractPropertyBasedConfiguration.initPropertiesByString(configString));
    }

}
