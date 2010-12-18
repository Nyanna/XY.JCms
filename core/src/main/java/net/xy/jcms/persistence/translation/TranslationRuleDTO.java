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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * transfer object for translation rules compatible with JAXB & JPA
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "rule")
@Table(name = "translation")
@Entity
public class TranslationRuleDTO implements Serializable {
    private static final long serialVersionUID = 5724775552674336096L;

    // consists of usecase and parameter hash
    @Id
    protected int id = 0;
    private String reactOn = null;
    private String buildOff = null;
    private String usecase = null;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RuleParameterDTO> parameters = null;

    @XmlAttribute(name = "reactOn", required = true)
    public String getReactOn() {
        return reactOn;
    }

    public void setReactOn(final String reactOn) {
        this.reactOn = reactOn;
    }

    @XmlAttribute(name = "buildOff", required = true)
    public String getBuildOff() {
        return buildOff;
    }

    public void setBuildOff(final String buildOff) {
        this.buildOff = buildOff;
    }

    @XmlAttribute(name = "usecase", required = true)
    public String getUsecase() {
        return usecase;
    }

    public void setUsecase(final String usecase) {
        this.usecase = usecase;
        id = (usecase != null ? usecase.hashCode() : 0) + (parameters != null ? parameters.hashCode() : 0);
    }

    @XmlElement(name = "parameter")
    public List<RuleParameterDTO> getParameters() {
        return parameters;
    }

    public void setParameters(final List<RuleParameterDTO> parameters) {
        this.parameters = parameters;
        id = (usecase != null ? usecase.hashCode() : 0) + (parameters != null ? parameters.hashCode() : 0);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final TranslationRuleDTO oo = (TranslationRuleDTO) obj;
        return (reactOn == oo.reactOn || reactOn != null && reactOn.equals(oo.reactOn)) &&
                (buildOff == oo.buildOff || buildOff != null && buildOff.equals(oo.buildOff)) &&
                (usecase == oo.usecase || usecase != null && usecase.equals(oo.usecase)) &&
                (parameters == oo.parameters || parameters != null && parameters.equals(oo.parameters));
    }

    @Override
    public int hashCode() {
        int hash = 293;
        if (reactOn != null) {
            hash = hash * 3 + reactOn.hashCode();
        }
        if (buildOff != null) {
            hash = hash * 3 + buildOff.hashCode();
        }
        if (usecase != null) {
            hash = hash * 3 + usecase.hashCode();
        }
        if (parameters != null) {
            hash = hash * 3 + parameters.hashCode();
        }
        return hash;
    }
}
