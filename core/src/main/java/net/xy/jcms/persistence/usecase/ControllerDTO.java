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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;

/**
 * usecase controller dto compatible with JPA and JAXB
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "class")
@XmlType(propOrder = { "controllerInstance", "obmitedConfigurations" })
@Table(name = "usecase_controller")
@Entity
public class ControllerDTO implements Serializable, Comparable<ControllerDTO> {
    private static final long serialVersionUID = -5283033861736997720L;

    @Id
    @GeneratedValue
    protected int id = 0;
    private String controllerInstance = null;
    private Set<ConfigurationType> obmitedConfigurations = null;
    @Column(name = "ordering")
    private int order = 0;

    @XmlAttribute(name = "path", required = true)
    public String getControllerInstance() {
        return controllerInstance;
    }

    public void setControllerInstance(final String controllerInstance) {
        this.controllerInstance = controllerInstance;
    }

    @XmlAttribute(name = "obmitConfig")
    public Set<ConfigurationType> getObmitedConfigurations() {
        return obmitedConfigurations;
    }

    public void setObmitedConfigurations(final Set<ConfigurationType> obmitedConfigurations) {
        this.obmitedConfigurations = obmitedConfigurations;
    }

    @XmlAttribute(name = "order")
    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final ControllerDTO oo = (ControllerDTO) obj;
        return (controllerInstance == oo.controllerInstance || controllerInstance != null
                && controllerInstance.equals(oo.controllerInstance))
                && (obmitedConfigurations == oo.obmitedConfigurations || obmitedConfigurations != null
                        && obmitedConfigurations.equals(oo.obmitedConfigurations));
    }

    @Override
    public int hashCode() {
        int hash = 264;
        if (controllerInstance != null) {
            hash = hash * 3 + controllerInstance.hashCode();
        }
        if (obmitedConfigurations != null) {
            hash = hash * 3 + obmitedConfigurations.hashCode();
        }
        return hash;
    }

    @Override
    public int compareTo(final ControllerDTO o) {
        return Integer.valueOf(order).compareTo(o.order);
    }
}
