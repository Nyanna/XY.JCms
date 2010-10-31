/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;

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
         * the datamodel is an simple container for contexts information.it
         * triggers alteration and would be altered it will be passed through
         * the controllers only.
         */
        dataModel,
        /**
         * contentconfiguration will be used for the most content aggregating
         * controllers. there output is added to the datamodel and it will be
         * droped after controllerphase.
         */
        contentConfiguration,
        /**
         * the repository contains all to the view obmitted content would empty
         * initialized and is the result of the proccesing the
         * contentConfiguration
         */
        contentRepository,
        /**
         * this configuration gots refilled in the components tree configuration
         * objects it will not be used but can be altered by controllers.
         */
        UIConfiguration,
        /**
         * this configuration gots refilled into cinfiguration objects for the
         * include fragments components building the componenttree.
         */
        templateconfiguration,
        /**
         * the message configuration gots refilled into the component
         * configuration objects
         */
        messageConfiguration,
        /**
         * descripen the orderflow configuration
         */
        orderFlowConfiguration,
        /**
         * configuration only used in the controllers, would not be taken into
         * account when hashing the datamodel
         */
        controllerConfiguration,
        /**
         * usually an renderfactory which provides the renderer for the
         * component configuration
         */
        renderKitConfiguration,
        /**
         * just an triger to obmit usecase parameters to the handler
         */
        parameters;

        /**
         * these configuration are obmittedt to the view and therefore will be
         * hashed to determine view changes.
         */
        public static final EnumSet<ConfigurationType> VIEWAPPLICABLE = EnumSet.of(contentRepository, UIConfiguration,
                messageConfiguration, renderKitConfiguration);

        /**
         * all configuration will be additionally obmitted to the controllers
         */
        public static final EnumSet<ConfigurationType> CONTROLLERAPPLICABLE = EnumSet.of(dataModel);
    }

    /**
     * what kind of configuration should only n abstract view no jj models
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
     * @return
     */
    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    /**
     * return the real configuration holding object
     * 
     * @return
     */
    protected CONFIGURATION_OBJECT getConfigurationValue() {
        return configurationValue;
    }

    /**
     * return the configurations origin
     * 
     * @return
     */
    public String getConfigurationSource() {
        return configurationSource;
    }

    /**
     * implements joining of configurations
     * 
     * @param otherConfig
     */
    public abstract void mergeConfiguration(final Configuration<CONFIGURATION_OBJECT> otherConfig);

    /**
     * for hashing each view configuration has to implement there own hashing
     * alghorhytm which returns the same hash for equal configuration based on
     * its own sight.
     * 
     * @return
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
     * @return
     */
    public static Configuration<?> initByString(final ConfigurationType type, final String in) {
        switch (type) {

        }
        return new TemplateConfiguration(new HashMap());
    }

    /**
     * initializes an configuration with an input stream resource. only for
     * certain configuration available.
     * 
     * @param type
     * @param stream
     * @return
     */
    public static Configuration<?> initByStream(final ConfigurationType type, final InputStream stream) {
        switch (type) {

        }
        return new TemplateConfiguration(new HashMap());
    }
}