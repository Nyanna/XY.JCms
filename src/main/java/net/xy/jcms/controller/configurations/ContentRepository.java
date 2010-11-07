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

/**
 * an content repository for bindvariables and objects mainly from type IContent
 * 
 * @author Xyan
 * 
 */
public class ContentRepository extends Configuration<Map<String, Object>> {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.ContentRepository;

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public ContentRepository(final Map<String, Object> configurationValue) {
        super(TYPE, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<String, Object>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    /**
     * get an key based content object
     * 
     * @param key
     * @param type
     * @param config
     * @return
     */
    public Object getContent(final String key, final Class<?> type, final ComponentConfiguration config) {
        return getContentMatch(key, type, config).getValue();
    }

    /**
     * inserts content to these key
     * 
     * @param key
     * @param content
     */
    public void putContent(final String key, final Object content) {
        getConfigurationValue().put(key, content);
    }

    /**
     * gets an key based content object and where it was found, closure
     * 
     * @param key
     * @param type
     * @param config
     * @return
     */
    public Match<String, Object> getContentMatch(final String key, final Class<?> type, final ComponentConfiguration config) {
        final Match<String, Object> got = new Match<String, Object>(null, null);
        final String pathKey = ConfigurationIterationStrategy.fullPath(config, key);
        final Object found = getConfigurationValue().get(pathKey);
        if (!type.isInstance(found)) {
            return got;
        }
        return new Match<String, Object>(pathKey, found);
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
