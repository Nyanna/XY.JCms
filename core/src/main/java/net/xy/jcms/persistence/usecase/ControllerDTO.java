package net.xy.jcms.persistence.usecase;

import java.io.Serializable;
import java.util.Set;

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
public class ControllerDTO implements Serializable {
    private static final long serialVersionUID = -5283033861736997720L;

    @Id
    @GeneratedValue
    protected int id = 0;
    private String controllerInstance = null;
    private Set<ConfigurationType> obmitedConfigurations = null;

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
}
