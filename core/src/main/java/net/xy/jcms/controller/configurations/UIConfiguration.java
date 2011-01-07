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
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.controller.configurations.pool.ConverterPool;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.persistence.usecase.UIEntryDTO;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IConverter;

import org.apache.commons.lang.StringUtils;

/**
 * implements an default property configuration for UI config
 * 
 * @author xyan
 * 
 */
public class UIConfiguration extends Configuration<Map<String, Object>> {
    /**
     * global type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.UIConfiguration;

    /**
     * these map saves used type converters for back translation of config
     */
    private final Map<Class<?>, IConverter<?>> converterMap = new HashMap<Class<?>, IConverter<?>>();

    /**
     * default
     * 
     * @param configurationValue
     */
    public UIConfiguration(final Map<String, Object> configurationValue) {
        super(TYPE, configurationValue);
    }

    /**
     * converts special string notation like true:Boolean, 22:Integer to native
     * java types.
     * 
     * @param loader
     *            needed for IConverter type converters
     * @throws ClassNotFoundException
     */
    public void stringTypeConversion(final ClassLoader loader) throws ClassNotFoundException {
        for (final Entry<String, Object> entry : getConfigurationValue().entrySet()) {
            if (String.class.isInstance(entry.getValue())) {
                final String val = (String) entry.getValue();
                final Matcher integer = Pattern.compile("([0-9]+):Integer", Pattern.CASE_INSENSITIVE).matcher(val);
                if (integer.matches()) {
                    entry.setValue(Integer.valueOf(integer.group(1)));
                    continue;
                }
                final Matcher bool = Pattern.compile("(true|false):Boolean", Pattern.CASE_INSENSITIVE).matcher(val);
                if (bool.matches()) {
                    entry.setValue(Boolean.valueOf(integer.group(1)));
                    continue;
                }
                final Matcher along = Pattern.compile("([0-9]+):Long", Pattern.CASE_INSENSITIVE).matcher(val);
                if (along.matches()) {
                    entry.setValue(Long.valueOf(integer.group(1)));
                    continue;
                }
                final Matcher adouble = Pattern.compile("([0-9,]+):Double", Pattern.CASE_INSENSITIVE).matcher(val);
                if (adouble.matches()) {
                    entry.setValue(Double.valueOf(integer.group(1)));
                    continue;
                }
                // matches an custom typeconverter
                final Matcher converter = Pattern.compile("\\[(.*)\\]:([a-zA-Z0-9.$]+)", Pattern.CASE_INSENSITIVE)
                        .matcher(val);
                if (converter.matches()) {
                    final IConverter<?> typeConverter = ConverterPool.get(converter.group(2), loader);
                    final Object value = typeConverter.convert(converter.group(1));
                    converterMap.put(value.getClass(), typeConverter);
                    entry.setValue(value);
                    continue;
                }
            }
        }
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
                final Object found = getConfigurationValue().get(pathKey);
                if (rightType(ui, found)) {
                    // type check based on class parameter
                    value = new Match<String, Object>(pathKey, found);
                    break;
                }
                if (found != null) {
                    throw new IllegalArgumentException("An vital ui configuration is not the right object type! "
                            + DebugUtils.printFields(ui.getDefaultValue(), found));
                }
            }
        } else {
            final Object found = getConfigurationValue().get(fullPathKey);
            retrievalStack.add(fullPathKey);
            // type check based on class parameter
            if (found != null && !rightType(ui, found)) {
                throw new IllegalArgumentException("An vital ui configuration is not the right object type! "
                        + DebugUtils.printFields(ui.getDefaultValue(), found));
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
     * @param mounted
     *            where this configuration was inserted e.g. root.fragmentOne adjusts relative pathes of the form
     *            .comp4.comp5 with the given mountpoint to comp1.comp4.comp5
     * @return value
     * @throws ClassNotFoundException
     */
    public static UIConfiguration initByString(final String configString, final ClassLoader loader, final String mount) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final UIConfiguration config = new UIConfiguration(
                (Map) AbstractPropertyBasedConfiguration.initMapByString(configString, mount));
        try {
            config.stringTypeConversion(loader);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("An referenced typeconverter couldn't be loaded.", e);
        }
        return config;
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
    public UIConfiguration mergeConfiguration(final Configuration<Map<String, Object>> otherConfig2) {
        final UIConfiguration newC = mergeConfiguration(otherConfig2.getConfigurationValue());
        if (otherConfig2 instanceof UIConfiguration) {
            newC.converterMap.putAll(((UIConfiguration) otherConfig2).converterMap);
        }
        return newC;
    }

    @Override
    public UIConfiguration mergeConfiguration(final Map<String, Object> otherConfig2) {
        final UIConfiguration newC = new UIConfiguration(mergeConfiguration(getConfigurationValue(), otherConfig2));
        newC.converterMap.putAll(converterMap);
        return newC;
    }

    /**
     * merges two config objects of this type
     * 
     * @param config1
     * @param config2
     * @return
     */
    protected Map<String, Object> mergeConfiguration(final Map<String, Object> config1,
            final Map<String, Object> config2) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.putAll(config1);
        result.putAll(config2);
        return result;
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
     * converts this configuration into an dto
     * 
     * @return dto
     */
    public ConfigurationDTO toDTO() {
        final ConfigurationDTO ret = new ConfigurationDTO();
        ret.setConfigurationType(TYPE);
        final List<UIEntryDTO> conf = new ArrayList<UIEntryDTO>();
        for (final Entry<String, Object> entry : getConfigurationValue().entrySet()) {
            final UIEntryDTO val = new UIEntryDTO();
            val.setKey(entry.getKey());
            if (entry.getValue() == null
                    || entry.getValue() instanceof String && StringUtils.isBlank((String) entry.getValue())) {
                // Do nothing
            } else if (entry.getValue() instanceof String) {
                val.setValue((String) entry.getValue());
                val.setType("String");
            } else if (entry.getValue() instanceof Integer) {
                val.setValue(entry.getValue().toString());
                val.setType("Integer");
            } else if (entry.getValue() instanceof Boolean) {
                val.setValue(entry.getValue().toString());
                val.setType("Boolean");
            } else if (entry.getValue() instanceof Long) {
                val.setValue(entry.getValue().toString());
                val.setType("Long");
            } else if (entry.getValue() instanceof Double) {
                val.setValue(entry.getValue().toString());
                val.setType("Double");
            } else {
                final IConverter<?> converter = converterMap.get(entry.getValue().getClass());
                if (converter != null) {
                    val.setValue(converter.convert(entry.getValue()));
                    val.setType(converter.getClass().getName());
                } else {
                    throw new IllegalArgumentException(
                            "Cant destinguish the converter type to convert into string representation.");
                }
            }
            conf.add(val);
        }
        ret.setUiconfig(conf);
        return ret;
    }

    /**
     * method for converting an entry list back to an usable config
     * 
     * @param entries
     * @return value
     * @throws ClassNotFoundException
     */
    public static UIConfiguration fromEntryList(final List<UIEntryDTO> entries, final ClassLoader loader)
            throws ClassNotFoundException {
        final UIConfiguration ret = new UIConfiguration(new HashMap<String, Object>(entries.size()));
        for (final UIEntryDTO entry : entries) {
            if (StringUtils.isBlank(entry.getType()) || entry.getType().equalsIgnoreCase("String")) {
                ret.getConfigurationValue().put(entry.getKey(), entry.getValue());
            } else if (entry.getType().equalsIgnoreCase("Integer")) {
                ret.getConfigurationValue().put(entry.getKey(), Integer.valueOf(entry.getValue()));
            } else if (entry.getType().equalsIgnoreCase("Boolean")) {
                ret.getConfigurationValue().put(entry.getKey(), Boolean.valueOf(entry.getValue()));
            } else if (entry.getType().equalsIgnoreCase("Long")) {
                ret.getConfigurationValue().put(entry.getKey(), Long.valueOf(entry.getValue()));
            } else if (entry.getType().equalsIgnoreCase("Double")) {
                ret.getConfigurationValue().put(entry.getKey(), Double.valueOf(entry.getValue()));
            } else {
                final IConverter<?> converter = ConverterPool.get(entry.getType(), loader);
                if (converter != null) {
                    ret.converterMap.put(converter.getClass(), converter);
                    ret.getConfigurationValue().put(entry.getKey(), converter.convert(entry.getValue()));
                } else {
                    throw new IllegalArgumentException(
                            "Cant destinguish the converter type to convert into string representation.");
                }
            }
        }
        return ret;
    }
}
