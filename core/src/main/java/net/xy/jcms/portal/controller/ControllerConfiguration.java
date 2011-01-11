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
package net.xy.jcms.portal.controller;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IConverter;
import net.xy.jcms.shared.types.Model;

/**
 * is an aggreation and functional summary of all controller relevant
 * configuration included parameters and the model
 * 
 * @author Xyan
 * 
 */
public class ControllerConfiguration {

    /**
     * factory class no instance allowed
     */
    private ControllerConfiguration() {}

    /**
     * build an config instance for an desired controller with the desired
     * params
     * 
     * @param ctrl
     * @param model
     * @param parameters
     * @return config object
     */
    @SuppressWarnings("unchecked")
    public static Config build(final Controller ctrl, final Model model, final Map<Object, Object> parameters) {
        return new Config((Class<Controller>) ctrl.getClass(), model, parameters);
    }

    /**
     * configuration api object
     * 
     * @author Xyan
     * 
     */
    public static class Config {

        /**
         * holds the model
         */
        final Model model;

        /**
         * holds the configuration for this desired controller
         */
        final Map<String, Object> ctrlConfig;

        // sources
        /**
         * holds the global same as ctrlConfig
         */
        final Map<String, Object> globals;

        /**
         * holds the usecase params
         */
        final Map<Object, Object> parameters;

        /**
         * pattern for replacing parameters in configs
         */
        private static Pattern PARAMETER_PATTERN = Pattern.compile("\\$(.{3,32})\\$", Pattern.CASE_INSENSITIVE);

        /**
         * private constructor for the factory
         * 
         * @param clazz
         * @param model
         * @param parameters
         */
        private Config(final Class<Controller> clazz, final Model model, final Map<Object, Object> parameters) {
            this.model = model;
            this.parameters = parameters;
            final net.xy.jcms.controller.configurations.ControllerConfiguration cConfig = (net.xy.jcms.controller.configurations.ControllerConfiguration) model
                    .get(net.xy.jcms.controller.configurations.ControllerConfiguration.TYPE);
            if (cConfig != null) {
                ctrlConfig = cConfig.getControllerConfig(clazz);
                if (ctrlConfig != null) {
                    globals = ctrlConfig;
                } else {
                    globals = null;
                }
            } else {
                ctrlConfig = null;
                globals = null;
            }
        }

        /**
         * helper method to get config
         * always returns an object and never null if the default was not null
         * -replaces $ctrlparam$ in string values
         * -priority equals to param order
         * 
         * @param item
         * @param binding
         * @return value of item type or null
         */
        @SuppressWarnings("unchecked")
        public <T> T get(final Item<T> item, final Map<? extends Object, ? extends Object> binding) {
            Object ret = null;
            if (binding != null) {
                ret = binding.get(item.key);
            }
            if (ret == null && globals != null) {
                ret = globals.get(item.key);
            }
            if (ret == null && parameters != null) {
                ret = parameters.get(item.key);
            }
            if (ret == null) {
                ret = item.def;
            }
            ret = replaceParams(ret);
            if (item.converter != null) {
                return item.converter.valueOf(ret);
            } else if (item.def != null) {
                if (item.def instanceof String) {
                    return (T) ret.toString();
                } else if (item.def instanceof Integer) {
                    return (T) net.xy.jcms.shared.types.Integer.valueOfObject(ret);
                } else if (item.def instanceof Long) {
                    return (T) net.xy.jcms.shared.types.Long.valueOfObject(ret);
                } else {
                    return (T) ret;
                }
            } else {
                return (T) ret;
            }
        }

        /**
         * helper method to get config
         * always returns an object and never null if the default was not null
         * -replaces $ctrlparam$ in string values
         * 
         * @param item
         * @return value of type or null
         */
        public <T> T get(final Item<T> item) {
            return get(item, null);
        }

        /**
         * gets an raw value from globals or null
         * 
         * @param key
         * @return global value or null
         */
        public Object getGlobal(final String key) {
            if (globals != null) {
                return globals.get(key);
            }
            return null;
        }

        /**
         * gets an config directly from the parameters
         * 
         * @param key
         * @return param value
         */
        public Object getParameter(final Object key) {
            if (parameters == null) {
                return null;
            }
            return parameters.get(key);
        }

        /**
         * pathThrough for the configurations from model
         * 
         * @param type
         * @return config
         */
        public Configuration<?> getConfig(final ConfigurationType type) {
            return model.get(type);
        }

        /**
         * replaces usecase params of format $param$
         * 
         * @param ret
         * @return original value or replaced one
         */
        public Object replaceParams(final Object ret) {
            // replacements
            if (parameters != null && !parameters.isEmpty() && ret instanceof String) {
                final Matcher m = PARAMETER_PATTERN.matcher(((String) ret).trim());
                if (m.matches()) {
                    // replace parameter entirely
                    final Object param = parameters.get(m.group(1));
                    if (param == null) {
                        throw new IllegalArgumentException("Parameter replacement couldn't be replaced. "
                                + DebugUtils.printFields(ret, param));
                    }
                    return param;
                } else if (m.reset().find()) {
                    // replace params in string
                    final StringBuffer sb = new StringBuffer();
                    do {
                        final Object param = parameters.get(m.group(1));
                        if (param == null) {
                            throw new IllegalArgumentException("Parameter replacement couldn't be replaced. "
                                    + DebugUtils.printFields(ret, m.group(1), param));
                        }
                        m.appendReplacement(sb, Matcher.quoteReplacement(param.toString()));
                    } while (m.find());
                    return m.appendTail(sb).toString();
                }
            }
            return ret;
        }
    }

    /**
     * configuration item representation
     * 
     * @author Xyan
     * 
     */
    public static class Item<T> {
        /**
         * the items key
         */
        private final String key;

        /**
         * items default value if no other could be found
         */
        private final T def;

        /**
         * description to get read by humans with ussage instructions
         */
        @SuppressWarnings("unused")
        private final String description;

        /**
         * converter used for type conversion of multisource params
         */
        private final IConverter<T> converter;

        /**
         * default constructor to build item. if def supports converter it will be used as fallback.
         * 
         * @param key
         * @param description
         * @param def
         */
        public Item(final String key, final String description, final T def) {
            this(key, description, def, null);
        }

        /**
         * constructor with type converter support. if def supports converter it will be used as fallback.
         * 
         * @param key
         * @param description
         * @param def
         */
        @SuppressWarnings("unchecked")
        public Item(final String key, final String description, final T def, final IConverter<T> converter) {
            this.key = key;
            this.description = description;
            this.def = def;
            if (converter == null && def instanceof IConverter) {
                this.converter = (IConverter<T>) def;
            } else {
                this.converter = converter;
            }
        }
    }
}
