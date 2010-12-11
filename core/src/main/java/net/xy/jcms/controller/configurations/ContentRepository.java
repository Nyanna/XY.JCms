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

import net.xy.jcms.shared.DebugUtils;

import org.apache.log4j.Logger;

/**
 * an content repository for bindvariables and objects mainly from type IContent
 * 
 * @author Xyan
 * 
 */
public class ContentRepository extends Configuration<Map<String, Object>> {
    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(ContentRepository.class);

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

    /**
     * creates an new content repository based upon an old repository and given
     * content
     * 
     * @param config
     * @param configurationValue
     */
    public ContentRepository(final ContentRepository config, final Map<String, Object> configurationValue) {
        super(TYPE, mergeConfig(config, configurationValue));
    }

    /**
     * copies an content config and overwrites it with given values
     * 
     * @param config
     * @param configurationValue
     * @return
     */
    private static Map<String, Object> mergeConfig(final ContentRepository config,
            final Map<String, Object> configurationValue) {
        final Map<String, Object> result = new HashMap<String, Object>(config.getConfigurationValue());
        result.putAll(configurationValue);
        return result;
    }

    @Override
    public ContentRepository mergeConfiguration(final Configuration<Map<String, Object>> otherConfig) {
        return mergeConfiguration(otherConfig.getConfigurationValue());
    }

    @Override
    public ContentRepository mergeConfiguration(final Map<String, Object> otherConfig) {
        final Map<String, Object> result = new HashMap<String, Object>(getConfigurationValue());
        result.putAll(otherConfig);
        return new ContentRepository(result);
    }

    /**
     * get an key based content object:
     * -full component path comp1.comp2.comp3.key
     * -global component deffinition, comp3.key
     * 
     * @param key
     * @param type
     * @param config
     * @return value
     */
    public Object getContent(final String key, final Class<?> type, final ComponentConfiguration config) {
        return getContentMatch(key, type, config).getValue();
    }

    /**
     * stores found config requests to save iteration strategies
     */
    private final Map<String, Match<String, Object>> cache = enableCache ? new HashMap<String, Match<String, Object>>()
            : null;

    /**
     * gets an key based content object and where it was found, closure
     * -full component path comp1.comp2.comp3.key
     * -global component deffinition, comp3.key
     * 
     * @param key
     * @param type
     * @param config
     * @return value
     */
    public Match<String, Object> getContentMatch(final String key, final Class<?> type,
            final ComponentConfiguration config) {
        final String fullPathKey = ConfigurationIterationStrategy.fullPath(config, key);
        if (enableCache) {
            final Match<String, Object> cached = cache.get(fullPathKey);
            if (cached != null) {
                return cached;
            }
        }
        // 1. try full path
        String pathKey = ConfigurationIterationStrategy.fullPath(config, key);
        Object found = getConfigurationValue().get(pathKey);
        if (found != null && !type.isInstance(found)) {
            LOG.error("Content was found but has not the right type. " + DebugUtils.printFields(key, type));
            throw new IllegalArgumentException("An mendatory content object was found but hasn't the exspected type."
                    + DebugUtils.printFields(key, fullPathKey));
        }

        if (found == null) {
            // 2. global component deffinition comp3.key
            pathKey = ConfigurationIterationStrategy.componentId(config, key);
            found = getConfigurationValue().get(pathKey);
            if (!type.isInstance(found)) {
                throw new IllegalArgumentException("An mendatory content object was not found."
                        + DebugUtils.printFields(key, fullPathKey));
            }
        }
        final Match<String, Object> mFound = new Match<String, Object>(pathKey, found);
        if (enableCache) {
            cache.put(fullPathKey, mFound);
        }
        return mFound;
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
