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
package net.xy.jcms.persistence.translation;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.xy.jcms.persistence.XmlMapEntry;

/**
 * transfer object for translation parameters compatible with JAXB & JPA
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "parameter")
@Table(name = "translation_parameter")
@Entity
public class RuleParameterDTO implements Serializable {
    private static final long serialVersionUID = -7876306113985724234L;

    @Id
    @GeneratedValue
    protected int id = 0;
    private String parameterName = null;
    private Integer aplicatesToGroup = null;
    private String converter = null;
    @ElementCollection
    @CollectionTable(name = "translation_parameter_map")
    private List<XmlMapEntry> buildInMap = null;

    @XmlAttribute(name = "name", required = true)
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    @XmlAttribute(name = "group")
    public Integer getAplicatesToGroup() {
        return aplicatesToGroup;
    }

    public void setAplicatesToGroup(final Integer aplicatesToGroup) {
        this.aplicatesToGroup = aplicatesToGroup;
    }

    @XmlAttribute(name = "convert")
    public String getConverter() {
        return converter;
    }

    public void setConverter(final String converter) {
        this.converter = converter;
    }

    @XmlElement(name = "map")
    public List<XmlMapEntry> getBuildInMap() {
        return buildInMap;
    }

    public void setBuildInMap(final List<XmlMapEntry> buildInMap) {
        this.buildInMap = buildInMap;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final RuleParameterDTO oo = (RuleParameterDTO) obj;
        return (parameterName == oo.parameterName || parameterName != null && parameterName.equals(oo.parameterName))
                &&
                (aplicatesToGroup == oo.aplicatesToGroup || aplicatesToGroup != null
                        && aplicatesToGroup.equals(oo.aplicatesToGroup)) &&
                (converter == oo.converter || converter != null && converter.equals(oo.converter)) &&
                (buildInMap == oo.buildInMap || buildInMap != null && buildInMap.equals(oo.buildInMap));
    }

    @Override
    public int hashCode() {
        int hash = 234;
        if (parameterName != null) {
            hash = hash * 3 + parameterName.hashCode();
        }
        if (aplicatesToGroup != null) {
            hash = hash * 3 + aplicatesToGroup.hashCode();
        }
        if (converter != null) {
            hash = hash * 3 + converter.hashCode();
        }
        if (buildInMap != null) {
            hash = hash * 3 + buildInMap.hashCode();
        }
        return hash;
    }
}