package net.xy.jcms.persistence.usecase;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.PrivateOwned;

import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.BodyEntry;

/**
 * main container for an usecase configuration holding its type and real data
 * object compatible with JPA and JAXB
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "configuration")
@XmlType(propOrder = { "configurationType", "mapping", "uiconfig", "containment", "content" })
@Table(name = "usecase_configuration")
@Entity
public class ConfigurationDTO implements Serializable {
    private static final long serialVersionUID = -3300722814635778406L;

    @Id
    @GeneratedValue
    protected int id = 0;
    @Enumerated(EnumType.STRING)
    private ConfigurationType configurationType = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<MapEntry> mapping = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<UIEntryDTO> uiconfig = null;
    // for generic text body like inline fragments
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<BodyEntry> containment = null;
    @Column(columnDefinition = "TEXT")
    private String content = null;

    @XmlAttribute(name = "type", required = true)
    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(final ConfigurationType configurationType) {
        this.configurationType = configurationType;
    }

    @XmlElement(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final ConfigurationDTO oo = (ConfigurationDTO) obj;
        return configurationType == oo.configurationType || configurationType != null
                && configurationType.equals(oo.configurationType);
    }

    @Override
    public int hashCode() {
        int hash = 348;
        if (configurationType != null) {
            hash = hash * 3 + configurationType.hashCode();
        }
        return hash;
    }

    @XmlElement(name = "entry")
    public List<MapEntry> getMapping() {
        return mapping;
    }

    public void setMapping(final List<MapEntry> mapping) {
        this.mapping = mapping;
    }

    @XmlElement(name = "ui")
    public List<UIEntryDTO> getUiconfig() {
        return uiconfig;
    }

    public void setUiconfig(final List<UIEntryDTO> uiconfig) {
        this.uiconfig = uiconfig;
    }

    @XmlElement(name = "element")
    public List<BodyEntry> getContainment() {
        return containment;
    }

    public void setContainment(final List<BodyEntry> containment) {
        this.containment = containment;
    }

}
