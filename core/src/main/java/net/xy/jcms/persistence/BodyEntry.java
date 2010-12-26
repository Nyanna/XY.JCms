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
