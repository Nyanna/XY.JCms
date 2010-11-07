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
import java.util.Properties;
import java.util.TreeMap;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class UIConfigurationProxy extends UIConfiguration {

    /**
     * stores the present requested ui config
     */
    private final Map<String, UI<?>> prepared = new TreeMap<String, UI<?>>(String.CASE_INSENSITIVE_ORDER);

    /**
     * stores the missing requested ui config
     */
    private final Map<String, UI<?>> missing = new TreeMap<String, UI<?>>(String.CASE_INSENSITIVE_ORDER);

    /**
     * default empty config
     */
    public UIConfigurationProxy() {
        super(new Properties());
    }

    /**
     * relays to the specific config
     * 
     * @param config
     */
    public UIConfigurationProxy(final UIConfiguration config) {
        super(config.getConfigurationValue());
    }

    @Override
    public Object getConfig(final UI<?> ui, final ComponentConfiguration config) {
        Match<String, Object> value = new Match<String, Object>(null, null);
        try {
            value = super.getConfigMatch(ui, config);
        } catch (final IllegalArgumentException ex) {
        }
        if (value.getValue() != null) {
            prepared.put(value.getPath(),
                    new UI<Object>(ui.getKey(), value.getValue(), ui.isIterate()));
            return value.getValue();
        } else {
            missing.put(ConfigurationIterationStrategy.fullPath(config, ui.getKey()), ui);
            return new Object();
        }
    }

    /**
     * returns all at collectec ui configs
     * 
     * @return
     */
    public Map<String, UI<?>> getPrepared() {
        final Map<String, UI<?>> merge = new TreeMap<String, UIConfiguration.UI<?>>(String.CASE_INSENSITIVE_ORDER);
        merge.putAll(prepared);
        merge.putAll(missing);
        return merge;
    }

    /**
     * returns the already defined configs
     * 
     * @return
     */
    public Map<String, UI<?>> getPresent() {
        return prepared;
    }

    /**
     * returns only the missing configs
     * 
     * @return
     */
    public Map<String, UI<?>> getMissing() {
        return missing;
    }

    /**
     * returns true if a config is missing
     * 
     * @return
     */
    public boolean isMissing() {
        return missing.isEmpty() ? false : true;
    }

}
