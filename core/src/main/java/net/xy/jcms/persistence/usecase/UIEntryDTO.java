package net.xy.jcms.persistence.usecase;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uiconf")
@Table(name = "usecase_configuration_ui")
@Entity
public class UIEntryDTO implements Serializable {
    private static final long serialVersionUID = 4086242066442527851L;

    @Id
    @GeneratedValue
    protected int id = 0;
    @Column(name = "mkey")
    private String key;
    @Column(name = "mval")
    private String value;
    @Column(name = "mtype")
    private String type;

    @XmlAttribute(required = true)
    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    @XmlAttribute(required = false)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @XmlAttribute(name = "valueType", required = false)
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final UIEntryDTO oo = (UIEntryDTO) obj;
        return (key == oo.key || key != null && key.equals(oo.key)) &&
                (value == oo.value || value != null && value.equals(oo.value)) &&
                (type == oo.type || type != null && type.equals(oo.type));
    }

    @Override
    public int hashCode() {
        int hash = 294;
        if (key != null) {
            hash = hash * 3 + key.hashCode();
        }
        if (value != null) {
            hash = hash * 3 + value.hashCode();
        }
        if (type != null) {
            hash = hash * 3 + type.hashCode();
        }
        return hash;
    }
}
