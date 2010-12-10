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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;

import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.JCmsHelper;

import org.apache.log4j.Logger;

/**
 * All configuration not dedicated to the clients request instead supplied or
 * retrieved by other sources to name a few options, db, uiconfig, messages,
 * content, cms.
 * 
 * @author Xyan
 * 
 */
public abstract class Configuration<CONFIGURATION_OBJECT> {

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(Configuration.class);

    /**
     * typesation of config this triggers the appropriated parser and config
     * readers
     * 
     * @author Xyan
     * 
     */
    public static enum ConfigurationType {
        /**
         * the repository contains all to the view obmitted content would empty
         * initialized and is the result of the proccesing the
         * ControllerConfiguration
         */
        ContentRepository,
        /**
         * this configuration gots refilled in the components tree configuration
         * objects it will not be used but can be altered by controllers.
         */
        UIConfiguration,
        /**
         * this configuration gots refilled into configuration objects for the
         * including fragments components building the componenttree.
         */
        TemplateConfiguration,
        /**
         * the message configuration gots refilled into the component
         * configuration objects and also will be used by controllers
         */
        MessageConfiguration,
        /**
         * configuration only used in the controllers, would not be taken into
         * account when hashing the datamodel
         */
        ControllerConfiguration,
        /**
         * usually an renderfactory which provides the renderer for the
         * component configuration. will be refilled to each component.
         */
        RenderKitConfiguration,
        /**
         * just an triger to obmit usecase parameters to the handler
         */
        Parameters;

        /**
         * these configuration are obmittedt to the view and therefore will be
         * hashed to determine view changes.
         */
        public static final EnumSet<ConfigurationType> VIEWAPPLICABLE = EnumSet.of(ContentRepository, UIConfiguration,
                MessageConfiguration, RenderKitConfiguration, TemplateConfiguration);

        /**
         * all configuration will be additionally obmitted to the controllers
         */
        public static final EnumSet<ConfigurationType> CONTROLLERAPPLICABLE = EnumSet.of(ControllerConfiguration);
        static {
            CONTROLLERAPPLICABLE.addAll(VIEWAPPLICABLE);
        }
    }

    /**
     * what kind of configuration should only abstract view no jj models
     */
    private final ConfigurationType configurationType;

    /**
     * value of the configuration can be anything from an string to an dto or
     * complete db dump.
     */
    private final CONFIGURATION_OBJECT configurationValue;

    /**
     * alternatively an source can be specified to load an already existing
     * configuration.
     */
    private final String configurationSource;

    // TODO [LOW] write source from extern configuration in config i forgot, hm
    // think
    // about merging strategy

    /**
     * constructor with obmited config object
     * 
     * @param configurationType
     * @param configurationValue
     */
    public Configuration(final ConfigurationType configurationType, final CONFIGURATION_OBJECT configurationValue) {
        if (configurationType == null) {
            throw new IllegalArgumentException("Configuration must have an proper type set");
        }
        this.configurationType = configurationType;
        this.configurationValue = configurationValue;
        configurationSource = null;
    }

    /**
     * constructor with an additional source origin. note configuration will be
     * parsed and retrieved before object initiliazation.
     * 
     * @param configurationType
     * @param configurationValue
     * @param configurationSource
     */
    public Configuration(final ConfigurationType configurationType, final CONFIGURATION_OBJECT configurationValue,
            final String configurationSource) {
        if (configurationType == null) {
            throw new IllegalArgumentException("Configuration must have an proper type set");
        }
        this.configurationType = configurationType;
        this.configurationValue = configurationValue;
        this.configurationSource = configurationSource;
    }

    /**
     * returns configurationType
     * 
     * @return value
     */
    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    /**
     * return the real configuration holding object
     * 
     * @return value
     */
    protected CONFIGURATION_OBJECT getConfigurationValue() {
        return configurationValue;
    }

    /**
     * return the configurations origin
     * 
     * @return value
     */
    public String getConfigurationSource() {
        return configurationSource;
    }

    /**
     * implements joining of configurations
     * 
     * @param otherConfig2
     */
    public abstract Configuration<CONFIGURATION_OBJECT> mergeConfiguration(
            final Configuration<CONFIGURATION_OBJECT> otherConfig2);

    /**
     * implements joining of an config and an other config object
     * 
     * @param configObject
     * @return
     */
    public abstract Configuration<CONFIGURATION_OBJECT> mergeConfiguration(final CONFIGURATION_OBJECT configObject);

    /**
     * public method for merging two configs of the same type
     * 
     * @param otherConfig1
     * @param otherConfig2
     * @return an new config of 1 and 2
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Configuration mergeConfiguration(final Configuration otherConfig1,
            final Configuration otherConfig2) {
        return otherConfig1.mergeConfiguration(otherConfig2);
    }

    /**
     * for hashing each view configuration has to implement there own hashing
     * alghorhytm which returns the same hash for equal configuration based on
     * its own sight.
     * 
     * @return value
     */
    @Override
    public abstract int hashCode();

    /**
     * each configuration musst also implent equals to figure out if two
     * configurations express the same.
     */
    @Override
    public abstract boolean equals(Object object);

    @Override
    public String toString() {
        return "type=" + getConfigurationType() + " source=" + getConfigurationSource();
    }

    /**
     * initialises an configuration with an string. only for specific types
     * available.
     * 
     * @param type
     * @param in
     * @param loader
     *            needed to load configuration components like typeconverters,
     *            renderer and fragments
     * @return value
     */
    public static Configuration<?> initByString(final ConfigurationType type, final String in, final ClassLoader loader) {
        switch (type) {
        case TemplateConfiguration:
            return TemplateConfiguration.initByString(in, loader);
        case UIConfiguration:
            return UIConfiguration.initByString(in, loader);
        case MessageConfiguration:
            return MessageConfiguration.initByString(in);
        case RenderKitConfiguration:
            return RenderKitConfiguration.initByString(in, loader);
        case ControllerConfiguration:
            return ControllerConfiguration.initByString(in);
        case ContentRepository:
            return new ContentRepository(new HashMap<String, Object>());
        default:
            throw new UnsupportedOperationException(
                    "Configurationtype is not implemented to be initialized by stream. "
                            + DebugUtils.printFields(type));
        }
    }

    /**
     * initializes an configuration with an input stream resource. only for
     * certain configuration available.
     * 
     * @param type
     * @param stream
     * @return value
     */
    public static Configuration<?> initByStream(final ConfigurationType type, final InputStream stream,
            final ClassLoader loader) {
        final StringBuilder writer = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 1024);
        final char[] buffer = new char[1024];
        try {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.append(buffer, 0, n);
            }
        } catch (final IOException e) {
        }
        return initByString(type, writer.toString(), loader);
    }

    /**
     * inits the config from an included resource
     * 
     * @param type
     * @param include
     *            pseudo uri specifieing an resource e.g. class://lang.java.test
     * @return
     */
    public static Configuration<?> initConfigurationByInclude(final ConfigurationType type, final String include,
            final ClassLoader loader) {
        if (include.startsWith("class://")) {
            final String classpath = include.replaceFirst("^class://", "");
            InputStream st;
            try {
                st = JCmsHelper.loadResource(classpath, loader);
                if (st != null) {
                    return Configuration.initByStream(type, st, loader);
                }
            } catch (final IOException e) {
            }
        }
        throw new IllegalArgumentException("Include reffers to an invalid location!");
    }

    /**
     * by default caching is enabled but it can for optimization purpose
     * deactivated
     */
    protected boolean enableCache = true;

    /**
     * @return if this config is mutable
     */
    public boolean disableCache() {
        return true;
    }
}
