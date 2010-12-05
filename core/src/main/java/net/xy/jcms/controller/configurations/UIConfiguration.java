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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * global type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.UIConfiguration;

    /**
     * default
     * 
     * @param configurationValue
     */
    public UIConfiguration(final Properties configurationValue) {
        super(TYPE, configurationValue);
    }

    /**
     * get an config value
     * 
     * @param key
     * @return value
     */
    public Object getConfig(final UI<?> ui, final ComponentConfiguration config) {
        return getConfigMatch(ui, config).getValue();
    }

    /**
     * stores found config requests to save iteration strategies
     */
    private final Map<String, Match<String, Object>> cache = enableCache ? new HashMap<String, Match<String, Object>>()
            : null;

    /**
     * retrieves an key:
     * iterable true:
     * -check full qualified path key, comp1.comp2.comp3.key
     * -move up to its parents and check these configs, comp1.comp2.key,
     * comp1.key
     * iterable false:
     * -check full qualified path key only, comp1.comp2.comp3.key
     * 
     * @param ui
     * @param config
     * @return value
     */
    public Match<String, Object> getConfigMatch(final UI<?> ui, final ComponentConfiguration config) {
        final String fullPathKey = ConfigurationIterationStrategy.fullPath(config, ui.getKey());
        if (enableCache) {
            final Match<String, Object> cached = cache.get(fullPathKey);
            if (cached != null) {
                return cached;
            }
        }

        // retrieval
        Match<String, Object> value = new Match<String, Object>(null, null);
        final List<String> retrievalStack = new ArrayList<String>();
        if (ui.isIterate()) {
            final ClimbUp strategy = new ClimbUp(config, ui.getKey());
            for (final String pathKey : strategy) {
                retrievalStack.add(pathKey);
                final Object found = getConfigurationValue().getProperty(pathKey);
                if (rightType(ui, found)) {
                    // type check based on class parameter
                    value = new Match<String, Object>(pathKey, found);
                    break;
                }
                if (found != null) {
                    throw new IllegalArgumentException("An vital ui configuration is not the right object type!");
                }
            }
        } else {
            final Object found = getConfigurationValue().get(fullPathKey);
            retrievalStack.add(fullPathKey);
            // type check based on class parameter
            if (found != null && !rightType(ui, found)) {
                throw new IllegalArgumentException("An vital ui configuration is not the right object type!");
            }
            value = new Match<String, Object>(fullPathKey, found);
        }
        if (value.getValue() != null) {
            if (enableCache) {
                cache.put(fullPathKey, value);
            }
            return value;
        } else if (value.getValue() == null && ui.getDefaultValue() != null
                && !(ui.getDefaultValue() instanceof Class<?>)) {
            final Match<String, Object> mFound = new Match<String, Object>("#" + fullPathKey, ui.getDefaultValue());
            if (enableCache) {
                cache.put(fullPathKey, mFound);
            }
            return mFound;
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
        private String description = "";

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
         * constructor with additional description
         * 
         * @param key
         * @param defaultValue
         * @param description
         */
        public UI(final String key, final V defaultValue, final String description) {
            this(key, defaultValue, true, description);
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
            this(key, defaultValue, iterate, null);
        }

        /**
         * constructor with additional description
         * 
         * @param key
         * @param defaultValue
         * @param iterate
         * @param description
         */
        public UI(final String key, final V defaultValue, final boolean iterate, final String description) {
            if (StringUtils.isBlank(key)) {
                throw new IllegalArgumentException("UI config key can't be blank.");
            }
            this.key = key;
            this.defaultValue = defaultValue;
            this.iterate = iterate;
            if (description != null) {
                this.description = description;
            }
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

    @Override
    public UIConfiguration mergeConfiguration(final Configuration<Properties> otherConfig2) {
        return mergeConfiguration(otherConfig2.getConfigurationValue());
    }

    @Override
    public UIConfiguration mergeConfiguration(final Properties otherConfig2) {
        return new UIConfiguration(mergeConfiguration(getConfigurationValue(), otherConfig2));
    }

}