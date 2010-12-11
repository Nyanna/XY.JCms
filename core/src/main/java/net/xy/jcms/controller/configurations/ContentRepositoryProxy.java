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
import java.util.TreeMap;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class ContentRepositoryProxy extends ContentRepository {

    /**
     * collects the desired but missing content
     */
    private Map<String, Class<?>> missingContent = new TreeMap<String, Class<?>>(String.CASE_INSENSITIVE_ORDER);

    /**
     * collects the desired and present content
     */
    private Map<String, Class<?>> presentContent = new TreeMap<String, Class<?>>(String.CASE_INSENSITIVE_ORDER);

    /**
     * default empty content
     */
    public ContentRepositoryProxy() {
        super(new HashMap<String, Object>());
    }

    /**
     * proxy for obmitedt content
     * 
     * @param config
     */
    public ContentRepositoryProxy(final ContentRepository config) {
        super(config.getConfigurationValue());
    }

    /**
     * initializes proxy by its configuration value
     * 
     * @param configValue
     */
    public ContentRepositoryProxy(final Map<String, Object> configValue) {
        super(configValue);
    }

    @Override
    public Object getContent(final String key, final Class<?> type, final ComponentConfiguration config) {
        Match<String, Object> value = null;
        try {
            value = super.getContentMatch(key, type, config);
        } catch (final IllegalArgumentException ex) {
        }

        if (value != null && value.getValue() != null) {
            presentContent.put(value.getPath(), value.getValue().getClass());
            return value.getValue();
        } else {
            missingContent.put(ConfigurationIterationStrategy.fullPath(config, key), type);
            if (type != null) {
                try {
                    return type.newInstance();
                } catch (final InstantiationException e) {
                } catch (final IllegalAccessException e) {
                }
            }
            return null;
        }
    }

    /**
     * gets the map of requested content
     * 
     * @return value
     */
    public Map<String, Class<?>> getReqContent() {
        final Map<String, Class<?>> merge = new TreeMap<String, Class<?>>(String.CASE_INSENSITIVE_ORDER);
        merge.putAll(missingContent);
        merge.putAll(presentContent);
        return merge;
    }

    /**
     * returns only missing content
     * 
     * @return value
     */
    public Map<String, Class<?>> getMissingContent() {
        return missingContent;
    }

    /**
     * returns present content
     * 
     * @return value
     */
    public Map<String, Class<?>> getPresentContent() {
        return presentContent;
    }

    /**
     * returns true if a config is missing
     * 
     * @return value
     */
    public boolean isMissing() {
        return missingContent.isEmpty() ? false : true;
    }

    @Override
    public ContentRepositoryProxy mergeConfiguration(final Configuration<Map<String, Object>> otherConfig) {
        return mergeConfiguration(otherConfig.getConfigurationValue());
    }

    @Override
    public ContentRepositoryProxy mergeConfiguration(final Map<String, Object> otherConfig) {
        final Map<String, Object> result = new HashMap<String, Object>(getConfigurationValue());
        result.putAll(otherConfig);
        final ContentRepositoryProxy newOne = new ContentRepositoryProxy(result);
        newOne.missingContent = missingContent;
        newOne.presentContent = presentContent;
        return newOne;
    }
}
