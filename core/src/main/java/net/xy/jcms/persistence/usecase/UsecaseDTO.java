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
package net.xy.jcms.persistence.usecase;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * dto for an usecase compatible with JPA & JAXB
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "usecase")
@XmlType(propOrder = { "id", "description", "parameterList", "controllerList", "configurationList" })
@Table(name = "usecase")
@Entity
public class UsecaseDTO implements Serializable {
    private static final long serialVersionUID = -2772570863535833425L;

    // id consits of id and parameter hashcodes
    @Id
    private int id = 0;
    private String usecase = null;
    private String description = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<ParameterDTO> parameterList = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<ControllerDTO> controllerList = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<ConfigurationDTO> configurationList = null;

    @XmlTransient
    public int getPrimaryKey() {
        return id;
    }

    @XmlAttribute(required = true)
    public String getId() {
        return usecase;
    }

    public void setId(final String id) {
        usecase = id;
        this.id = (usecase != null ? usecase.hashCode() : 0) + (parameterList != null ? parameterList.hashCode() : 0);
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @XmlElementWrapper(name = "parameter", required = true)
    @XmlElement(name = "param")
    public List<ParameterDTO> getParameterList() {
        return parameterList;
    }

    public void setParameterList(final List<ParameterDTO> parameterList) {
        this.parameterList = parameterList;
        id = (usecase != null ? usecase.hashCode() : 0) + (parameterList != null ? parameterList.hashCode() : 0);
    }

    @XmlElementWrapper(name = "controller", required = true)
    @XmlElement(name = "class")
    public List<ControllerDTO> getControllerList() {
        return controllerList;
    }

    public void setControllerList(final List<ControllerDTO> controllerList) {
        this.controllerList = controllerList;
    }

    @XmlElementWrapper(name = "configurations", required = true)
    @XmlElement(name = "configuration")
    public List<ConfigurationDTO> getConfigurationList() {
        return configurationList;
    }

    public void setConfigurationList(final List<ConfigurationDTO> configurationList) {
        this.configurationList = configurationList;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final UsecaseDTO oo = (UsecaseDTO) obj;
        return (usecase == oo.usecase || usecase != null && usecase.equals(oo.usecase))
                && (description == oo.description || description != null && description.equals(oo.description))
                && (parameterList == oo.parameterList || parameterList != null && parameterList.equals(oo.parameterList))
                && (controllerList == oo.controllerList || controllerList != null
                        && controllerList.equals(oo.controllerList))
                && (configurationList == oo.configurationList || configurationList != null
                        && configurationList.equals(oo.configurationList));
    }

    @Override
    public int hashCode() {
        int hash = 975;
        if (usecase != null) {
            hash = hash * 3 + usecase.hashCode();
        }
        if (description != null) {
            hash = hash * 3 + description.hashCode();
        }
        if (parameterList != null) {
            hash = hash * 3 + parameterList.hashCode();
        }
        if (controllerList != null) {
            hash = hash * 3 + controllerList.hashCode();
        }
        if (configurationList != null) {
            hash = hash * 3 + configurationList.hashCode();
        }
        return hash;
    }
}
