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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * transfer data object for usecase collections compatible with JPA and JAXB
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "usecases")
@Table(name = "usecasesets")
@Entity
public class UsecasesDTO implements Serializable {
    private static final long serialVersionUID = 7454455613989345568L;

    @Id
    protected int id = 0;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UsecaseDTO> usecases = null;

    @XmlElement(name = "usecase")
    public List<UsecaseDTO> getUsecases() {
        return usecases;
    }

    public void setUsecases(final List<UsecaseDTO> usecases) {
        this.usecases = usecases;
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
        final UsecasesDTO oo = (UsecasesDTO) obj;
        return usecases == oo.usecases || usecases != null && usecases.equals(oo.usecases);
    }

    @Override
    public int hashCode() {
        int hash = 794;
        if (usecases != null) {
            hash = hash * 3 + usecases.hashCode();
        }
        return hash;
    }
}
