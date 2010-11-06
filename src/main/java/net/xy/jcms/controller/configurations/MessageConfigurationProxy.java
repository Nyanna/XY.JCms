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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class MessageConfigurationProxy extends MessageConfiguration {
    /**
     * stores missing requested keys
     */
    private final List<String> missingKeys = new ArrayList<String>();

    /**
     * stores present requested keys
     */
    private final Map<String, String> presentKeys = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * default empty config
     */
    public MessageConfigurationProxy() {
        super(new Properties());
    }

    /**
     * proxy for obmitted config
     * 
     * @param config
     */
    public MessageConfigurationProxy(final MessageConfiguration config) {
        super(config.getConfigurationValue());
    }

    @Override
    public String getMessage(final String key, final ComponentConfiguration config) {
        String value = null;
        try {
            value = super.getMessage(key, config);
        } catch (final IllegalArgumentException ex) {
        }
        if (value != null) {
            presentKeys.put(ConfigurationIterationStrategy.fullPath(config, key), value);
            return value;
        } else {
            missingKeys.add(ConfigurationIterationStrategy.fullPath(config, key));
            return "dummy";
        }
    }

    /**
     * get the collected keys
     * 
     * @return
     */
    public Map<String, String> getKeys() {
        final Map<String, String> merge = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        merge.putAll(presentKeys);
        for (final String key : missingKeys) {
            merge.put(key, "");
        }
        return merge;
    }

    /**
     * returns only missing keys
     * 
     * @return
     */
    public List<String> getMissingKeys() {
        Collections.sort(missingKeys, String.CASE_INSENSITIVE_ORDER);
        return missingKeys;
    }

    /**
     * returns only present keys
     * 
     * @return
     */
    public Map<String, String> getPresentKeys() {
        return presentKeys;
    }
}
