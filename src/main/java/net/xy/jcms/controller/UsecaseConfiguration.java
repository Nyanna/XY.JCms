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
package net.xy.jcms.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.shared.IController;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.Model;

/**
 * Configuration object describing the usecases and its behavior
 * 
 * @author xyan
 * 
 */
public class UsecaseConfiguration {
    /**
     * main usecase object describing all and anything. After these usecase
     * comes only singleton and static code, concern this.
     * 
     * @author Xyan
     * 
     */
    final public static class Usecase {
        /**
         * id for the usecase
         */
        private final String id;

        /**
         * an description, mendatory
         */
        private final String description;

        /**
         * the request/input parameters, KeyChain, Cookies, Post etc... All
         * these parameters are mendatory and will be validated.
         */
        private final Parameter[] parameterList;

        /**
         * an list of applied controllers
         */
        private final Controller[] controllerList;

        /**
         * an list of connected configurations. note: these list is the whole
         * and only configuration no other configuration would be delivered to
         * all involved code.
         */
        private final Model configurationList;

        /**
         * default constructor
         * 
         * @param id
         * @param description
         * @param parameterList
         * @param controllerList
         * @param configurationList
         */
        public Usecase(final String id, final String description, final Parameter[] parameterList,
                final Controller[] controllerList, final Configuration<?>[] configurationList) {
            this.parameterList = parameterList;
            this.controllerList = controllerList;
            this.configurationList = mergeConfigurations(configurationList);
            this.id = id;
            this.description = description;
        }

        /**
         * merges an list of configuration to an list were alls configuration of
         * the same type are merged
         * 
         * @param mergeList
         * @return value
         */
        @SuppressWarnings({ "rawtypes" })
        private static Model mergeConfigurations(final Configuration<?>[] mergeList) {
            final Model returnedConfig = new Model();
            for (final Configuration config : mergeList) {
                if (returnedConfig.containsKey(config.getConfigurationType())) {
                    // merge
                    returnedConfig.put(config.getConfigurationType(),
                            Configuration.mergeConfiguration(returnedConfig.get(config.getConfigurationType()), config));
                } else {
                    // insert
                    returnedConfig.put(config.getConfigurationType(), config);
                }
            }
            return returnedConfig;
        }

        /**
         * returns the id
         * 
         * @return value
         */
        public String getId() {
            return id;
        }

        /**
         * returns the parameter list
         * 
         * @return value
         */
        public Parameter[] getParameterList() {
            return parameterList;
        }

        /**
         * returns the controllerlist
         * 
         * @return value
         */
        public Controller[] getControllerList() {
            return controllerList;
        }

        /**
         * return all available configuration
         * 
         * @return an copy of the inertanl config map
         */
        public Model getConfigurations() {
            return new Model(configurationList);
        }

        /**
         * gets only specific types of configuration
         * 
         * @param types
         * @return n copy of the inertanl config map
         */
        public Model getConfigurations(final EnumSet<ConfigurationType> types) {
            final Model returnedConfig = new Model();
            for (final ConfigurationType type : types) {
                if (!configurationList.containsKey(type)) {
                    final Configuration<?> emptyConf = Configuration.initByString(type, StringUtils.EMPTY, null);
                    configurationList.put(type, emptyConf);
                }
                returnedConfig.put(type, configurationList.get(type));
            }
            return returnedConfig;
        }

        /**
         * get one specific configuration or inits en empty one
         * 
         * @param types
         * @return never null instead it creates an empty configuration of the
         *         requested type.
         */
        public Configuration<?> getConfiguration(final ConfigurationType type) {
            return getConfigurations(EnumSet.of(type)).get(type);
        }

        /**
         * returns the description
         * 
         * @return value
         */
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "id=" + getId() + " description=\"" + getDescription() + "\" parameter=" + getParameterList()
                    + " controller=" + getControllerList() + " configuration=" + getConfigurations();
        }
    }

    /**
     * object describing the input parameters originated to the clients request.
     * 
     * @author Xyan
     * 
     */
    final public static class Parameter {
        /**
         * id or key of the parameter
         */
        private final String parameterKey;

        /**
         * the type of this parameters value, can be an primitive or an complex
         * type like contentType as an classpath.
         */
        private final String parameterType;

        /**
         * default constructor
         * 
         * @param parameterKey
         * @param parameterType
         * @param mendatory
         */
        public Parameter(final String parameterKey, final String parameterType) {
            this.parameterKey = parameterKey;
            this.parameterType = parameterType;
        }

        /**
         * returns the parameter name
         * 
         * @return value
         */
        public String getParameterKey() {
            return parameterKey;
        }

        /**
         * returns the type of the parameters value as classpath
         * 
         * @return value
         */
        public String getParameterType() {
            return parameterType;
        }

        @Override
        public String toString() {
            return "key=" + getParameterKey() + " type=" + getParameterType();
        }
    }

    /**
     * represents an controller object
     * 
     * @author Xyan
     * 
     */
    final public static class Controller {
        /**
         * an controler instance
         */
        private final IController controllerInstance;

        /**
         * configuration types wich should be obmitted to the controller
         */
        private final EnumSet<ConfigurationType> obmitedConfigurations;

        /**
         * default constructor
         * 
         * @param controllerInstance
         * @param obmitedConfigurations
         */
        public Controller(final IController controllerInstance, final EnumSet<ConfigurationType> obmitedConfigurations) {
            if (controllerInstance == null || obmitedConfigurations == null) {
                throw new IllegalArgumentException("Parameters can't be null.");
            }
            this.controllerInstance = controllerInstance;
            this.obmitedConfigurations = obmitedConfigurations;
        }

        /**
         * returns the list of configuration types to obmit
         * 
         * @return value
         */
        public EnumSet<ConfigurationType> getObmitedConfigurations() {
            return obmitedConfigurations;
        }

        /**
         * calls an controller with given parameterset
         * 
         * @param dac
         * @param configuration
         * @param parameters
         * @return value
         * @throws ClassNotFoundException
         */
        public NALKey invoke(final IDataAccessContext dac, final Model configuration,
                final Map<Object, Object> parameters) {
            if (parameters != null) {
                return controllerInstance.invoke(dac, configuration, parameters);
            } else {
                return controllerInstance.invoke(dac, configuration);
            }
        }

        @Override
        public String toString() {
            return "id=" + controllerInstance.getClass().getName() + " configurations=" + getObmitedConfigurations();
        }
    }

    /**
     * finds the most matching usecase for an given struct by comparing its id
     * and parameters
     * 
     * @param struct
     * @return value
     */
    public static Usecase findUsecaseForStruct(final NALKey struct, final IDataAccessContext dac) {
        if (struct == null) {
            return null;
        }
        final Usecase[] byIdSelected = getUsecasesById(struct.getId(), getUsecaseList(dac));
        if (byIdSelected.length > 1) {
            return findMostMatchingParams(byIdSelected, struct);
        } else if (byIdSelected.length == 1) {
            return byIdSelected[0];
        } else {
            return null;
        }
    }

    /**
     * retrieves the usecase list
     * 
     * @return value
     */
    private static Usecase[] getUsecaseList(final IDataAccessContext dac) {
        if (adapter == null) {
            throw new IllegalArgumentException("Usecase configuration adapter was not injected");
        }
        final Usecase[] list = adapter.getUsecaseList(dac);
        if (list == null) {
            throw new IllegalArgumentException("Usecase list can't be retrieved.");
        }
        return list;
    }

    /**
     * stores the usecase configuration adapter
     */
    private static IUsecaseConfigurationAdapter adapter;

    /**
     * sets the usecase configuration adapter
     * 
     * @param adapter
     */
    public static void setUsecaseAdapter(final IUsecaseConfigurationAdapter adapter) {
        UsecaseConfiguration.adapter = adapter;
    }

    /**
     * reduces caselist to the ones matching by id
     * 
     * @param id
     * @param list
     * @return value
     */
    private static Usecase[] getUsecasesById(final String id, final Usecase[] list) {
        final List<Usecase> retList = new ArrayList<Usecase>();
        for (final Usecase ucase : list) {
            if (ucase.getId().equalsIgnoreCase(id)) {
                retList.add(ucase);
            }
        }
        return retList.toArray(new Usecase[retList.size()]);
    }

    /**
     * find the usecase with the most matching parameter count
     * 
     * @param list
     * @param struct
     * @return value
     */
    private static Usecase findMostMatchingParams(final Usecase[] list, final NALKey struct) {
        Usecase foundCase = null;
        int reachedMatches = -1;
        // for each usecase
        for (final Usecase ucase : list) {
            final int matches = countMatchingParams(ucase, struct);
            if (matches > reachedMatches) {
                reachedMatches = matches;
                foundCase = ucase;
            }
        }
        return foundCase;
    }

    /**
     * counts the parameter presence matches of NALKey against an usecase
     * 
     * @param ucase
     * @param struct
     * @return value
     */
    private static int countMatchingParams(final Usecase ucase, final NALKey struct) {
        int counter = 0;
        for (final Parameter param : ucase.getParameterList()) {
            if (struct.getParameters().containsKey(param.getParameterKey())) {
                counter++;
            }
        }
        return counter;
    }
}
