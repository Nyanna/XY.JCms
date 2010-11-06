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
     * default constructor
     * 
     * @param configurationValue
     */
    public ContentRepository(final Map<String, Object> configurationValue) {
        super(ConfigurationType.ContentRepository, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<String, Object>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    public Object getContent(final String key, final Class<?> type, final ComponentConfiguration config) {
        final Object got = getConfigurationValue().get(ConfigurationIterationStrategy.fullPath(config, key));
        if (!type.isInstance(got)) {
            return null;
        }
        return got;
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
