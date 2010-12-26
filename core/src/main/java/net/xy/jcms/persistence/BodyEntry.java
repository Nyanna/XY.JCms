package net.xy.jcms.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * an element to proper display string containments
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "element")
@Table(name = "bodyentry")
@Entity
public class BodyEntry implements Serializable {
    private static final long serialVersionUID = 165666666643545893L;

    @Id
    @GeneratedValue
    protected int id = 0;
    @Column(name = "mkey")
    private String key = null;
    @Column(name = "mvalue")
    private String value = null;
    @Column(name = "mcontent", columnDefinition = "TEXT")
    private String content = null;

    @XmlAttribute(required = false)
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

    @XmlValue
    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
