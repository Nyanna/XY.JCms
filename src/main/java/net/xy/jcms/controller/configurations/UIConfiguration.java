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
import java.util.List;
import java.util.Properties;
import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.shared.DebugUtils;

import org.apache.commons.lang.StringUtils;

/**
 * implements an default property configuration for UI config
 * 
 * @author xyan
 * 
 */
public class UIConfiguration extends AbstractPropertyBasedConfiguration {

    /**
     * default
     * 
     * @param configurationValue
     */
    public UIConfiguration(final Properties configurationValue) {
        super(ConfigurationType.UIConfiguration, configurationValue);
    }

    /**
     * get an config value
     * 
     * @param key
     * @return value
     */
    public Object getConfig(final UI<?> ui, final ComponentConfiguration config) {
        Object value = null;
        final List<String> retrievalStack = new ArrayList<String>();
        if (ui.isIterate()) {
            final ClimbUp strategy = new ClimbUp(config, ui.getKey());
            for (final String pathKey : strategy) {
                retrievalStack.add(pathKey);
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
            final String pathKey = ConfigurationIterationStrategy.fullPath(config, ui.getKey());
            value = getConfigurationValue().get(pathKey);
            retrievalStack.add(pathKey);
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
            throw new IllegalArgumentException("An vital ui configuration is missing, check the configuration! "
                    + DebugUtils.printFields(retrievalStack));
        }
    }

    /**
     * checks the type of the retrieved value
     * 
     * @param ui
     * @param value
     * @return value
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

    /**
     * creates an config based on parsing an string
     * 
     * @param configString
     * @return value
     */
    public static UIConfiguration initByString(final String configString) {
        return new UIConfiguration(AbstractPropertyBasedConfiguration.initPropertiesByString(configString));
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
         * should the value retrieved using component path iteration as example
         * in case auf styleClass it should not so that it finds only exact
         * matching keys
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
         * @return value
         */
        public String getKey() {
            return key;
        }

        /**
         * get default value
         * 
         * @return value
         */
        public V getDefaultValue() {
            return defaultValue;
        }

        /**
         * iterate over path is true
         * 
         * @return value
         */
        public boolean isIterate() {
            return iterate;
        }

        /**
         * gets description
         * 
         * @return value
         */
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return DebugUtils.printFields(key, defaultValue);
        }
    }

}
