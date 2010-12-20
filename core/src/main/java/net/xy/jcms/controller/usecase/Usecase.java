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
package net.xy.jcms.controller.usecase;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.persistence.usecase.ControllerDTO;
import net.xy.jcms.persistence.usecase.ParameterDTO;
import net.xy.jcms.persistence.usecase.UsecaseDTO;
import net.xy.jcms.shared.types.Model;

import org.apache.commons.lang.StringUtils;

/**
 * main usecase object describing all and anything. After these usecase
 * comes only singleton and static code, concern this.
 * 
 * @author Xyan
 * 
 */
final public class Usecase {
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
        if (parameterList != null) {
            this.parameterList = parameterList;
        } else {
            this.parameterList = new Parameter[] {};
        }
        if (controllerList != null) {
            this.controllerList = controllerList;
        } else {
            this.controllerList = new Controller[] {};
        }
        this.configurationList = mergeConfigurations(configurationList);
        if (StringUtils.isBlank(id) || StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Either id or description were empty initialized.");
        }
        this.id = id;
        if (description.length() < 32) {
            throw new IllegalArgumentException("Usecase description have to be at least 32 chars long.");
        }
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

    /**
     * method converts this usecase to an tranferable dto
     * 
     * @return dto
     */
    public UsecaseDTO toDTO() {
        final UsecaseDTO dto = new UsecaseDTO();
        dto.setId(id);
        dto.setDescription(description);
        final List<ParameterDTO> params = new ArrayList<ParameterDTO>();
        for (final Parameter param : getParameterList()) {
            params.add(param.toDTO());
        }
        dto.setParameterList(params);
        final List<ControllerDTO> controller = new ArrayList<ControllerDTO>();
        for (final Controller ctrl : getControllerList()) {
            controller.add(ctrl.toDTO());
        }
        dto.setControllerList(controller);
        final List<ConfigurationDTO> configs = new ArrayList<ConfigurationDTO>();
        for (final Entry<ConfigurationType, Configuration<?>> conf : configurationList.entrySet()) {
            final ConfigurationDTO cdto;
            switch (conf.getKey()) {
            case MessageConfiguration:
                cdto = ((MessageConfiguration) conf.getValue()).toDTO();
                break;
            default:
                cdto = new ConfigurationDTO();
                cdto.setConfigurationType(conf.getKey());
                break;
            }
            configs.add(cdto);
            // TODO [LOW] config dto model
        }
        dto.setConfigurationList(configs);
        return dto;
    }
}