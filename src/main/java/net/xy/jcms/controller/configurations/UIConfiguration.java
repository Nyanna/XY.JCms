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
import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import org.apache.commons.lang.StringUtils;

/**
 * implements an default property configuration for UI config
 * 
 * @author xyan
 * 
 */
public class UIConfiguration extends Configuration<Properties> {

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public UIConfiguration(final Properties configurationValue) {
        super(ConfigurationType.UIConfiguration, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Properties> otherConfig) {
        for (final Entry<Object, Object> entry : otherConfig.getConfigurationValue().entrySet()) {
            getConfigurationValue().put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * get an config value
     * 
     * @param key
     * @return
     */
    public Object getConfig(final UI<?> ui, final ComponentConfiguration config) {
        Object value = null;
        if (ui.isIterate()) {
            final ClimbUp strategy = new ClimbUp(config, ui.getKey());
            for (final String pathKey : strategy) {
                value = getConfigurationValue().getProperty(pathKey);
                if (rightType(ui, value)) {
                    // type check based on class parameter
                    break;
                }
                if (value != null) {
                    throw new IllegalArgumentException("An vital ui configuration is not the right object type!");
                }
            }
        } else {
            value = getConfigurationValue().get(ConfigurationIterationStrategy.fullPath(config, ui.getKey()));
            // type check based on class parameter
            if (value != null && !rightType(ui, value)) {
                throw new IllegalArgumentException("An vital ui configuration is not the right object type!");
            }
        }
        if (value != null) {
            return value;
        } else if (value == null && ui.getDefaultValue() != null && !(ui.getDefaultValue() instanceof Class<?>)) {
            return ui.getDefaultValue();
        } else {
            throw new IllegalArgumentException("An vital ui configuration is missing, check the configuration!");
        }
    }

    /**
     * checks the type of the retrieved value
     * 
     * @param ui
     * @param value
     * @return
     */
    private boolean rightType(final UI<?> ui, final Object value) {
        if (value != null) {
            if (ui.getDefaultValue() != null && ui.getDefaultValue() instanceof Class<?>
                    && ((Class<?>) ui.getDefaultValue()).isInstance(value)) {
                return true;
            } else if (ui.getDefaultValue() != null && ui.getDefaultValue().getClass().isInstance(value)) {
                return true;
            } else if (ui.getDefaultValue() == null) {
                return true;
            }
        }
        return false;
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
     * configuration item specifieing property aggregation benavior
     * 
     * @author Xyan
     * 
     */
    public static class UI<V> {
        /**
         * must be filled, describes the key
         */
        private final String key;

        /**
         * default value, when set to null it must be configured
         */
        private final V defaultValue;

        /**
         * should the value retrieved using component path iteration as example in case auf styleClass it should not so
         * that it finds only exact matching keys
         */
        private final boolean iterate;

        /**
         * for live cms specifies an description for the user
         */
        // TODO [LOW] use and init description
        private final String description = "";

        /**
         * default constructor
         * 
         * @param key
         * @param defaultValue
         */
        public UI(final String key, final V defaultValue) {
            this(key, defaultValue, true);
        }

        /**
         * constructor with additional iterationflag
         * 
         * @param key
         * @param defaultValue
         * @param iteration
         *            turn component path iteration off
         */
        public UI(final String key, final V defaultValue, final boolean iterate) {
            if (StringUtils.isBlank(key)) {
                throw new IllegalArgumentException("UI config key can't be blank.");
            }
            this.key = key;
            this.defaultValue = defaultValue;
            this.iterate = iterate;
        }

        /**
         * get key
         * 
         * @return
         */
        public String getKey() {
            return key;
        }

        /**
         * get default value
         * 
         * @return
         */
        public V getDefaultValue() {
            return defaultValue;
        }

        /**
         * iterate over path is true
         * 
         * @return
         */
        public boolean isIterate() {
            return iterate;
        }
    }

}
