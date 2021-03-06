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

import java.util.EnumSet;
import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.persistence.usecase.ControllerDTO;
import net.xy.jcms.shared.IController;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.Model;

/**
 * represents an controller object
 * 
 * @author Xyan
 * 
 */
final public class Controller {
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
        return controllerInstance.invoke(dac, configuration, parameters);
    }

    @Override
    public String toString() {
        return "id=" + controllerInstance.getClass().getName() + " configurations=" + getObmitedConfigurations();
    }

    /**
     * this method converts this controller data to an transferable dto. note
     * just the classpath will be passed.
     * 
     * @return dto
     */
    public ControllerDTO toDTO() {
        final ControllerDTO dto = new ControllerDTO();
        dto.setControllerInstance(controllerInstance.getClass().getName());
        dto.setObmitedConfigurations(obmitedConfigurations);
        return dto;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final Controller oo = (Controller) obj;
        return controllerInstance.equals(oo.controllerInstance)
                && obmitedConfigurations.equals(oo.obmitedConfigurations);
    }

    @Override
    public int hashCode() {
        int hash = 578;
        hash = hash * 3 + controllerInstance.hashCode();
        hash = hash * 3 + obmitedConfigurations.hashCode();
        return hash;
    }
}