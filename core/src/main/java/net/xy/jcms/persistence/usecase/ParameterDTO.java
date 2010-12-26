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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO for an usecase parameter mapping for JPA and JAXB
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "param")
@XmlType(propOrder = { "parameterKey", "parameterType" })
@Table(name = "usecase_parameter")
@Entity
public class ParameterDTO implements Serializable {
    private static final long serialVersionUID = 4768427595499756335L;

    @Id
    @GeneratedValue
    protected int id = 0;
    private String parameterKey = null;
    private String parameterType = null;

    @XmlAttribute(name = "key", required = true)
    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(final String parameterKey) {
        this.parameterKey = parameterKey;
    }

    @XmlAttribute(name = "valueType", required = true)
    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(final String parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final ParameterDTO oo = (ParameterDTO) obj;
        return (parameterKey == oo.parameterKey || parameterKey != null && parameterKey.equals(oo.parameterKey)) &&
                   (parameterType == oo.parameterType || parameterType != null && parameterType.equals(oo.parameterType));
    }

    @Override
    public int hashCode() {
        int hash = 781;
        if (parameterKey != null) {
            hash = hash * 3 + parameterKey.hashCode();
        }
        if (parameterType != null) {
            hash = hash * 3 + parameterType.hashCode();
        }
        return hash;
    }
}
