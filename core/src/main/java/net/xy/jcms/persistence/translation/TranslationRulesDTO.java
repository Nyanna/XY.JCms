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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for transfering an list of translation rules compatible with JAXB & JPA
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "rules")
@Table(name = "rulesets")
@Entity
public class TranslationRulesDTO implements Serializable {
    private static final long serialVersionUID = 6188508602872138934L;

    @Id
    protected int id = 0;
    List<TranslationRuleDTO> rules = null;

    @XmlElement(name = "rule")
    @OneToMany
    public List<TranslationRuleDTO> getRules() {
        return rules;
    }

    public void setRules(final List<TranslationRuleDTO> rules) {
        this.rules = rules;
        id = hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final TranslationRulesDTO oo = (TranslationRulesDTO) obj;
        return rules == oo.rules || rules != null && rules.equals(oo.rules);
    }

    @Override
    public int hashCode() {
        int hash = 202;
        if (rules != null) {
            hash = hash * 3 + rules.hashCode();
        }
        return hash;
    }
}
