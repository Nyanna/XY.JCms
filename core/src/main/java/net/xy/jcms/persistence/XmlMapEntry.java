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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.IRenderer;

/**
 * map adapter for an more correct xml layout
 * 
 * @author Xyan
 * 
 */
@XmlRootElement(name = "map")
@Entity
public class XmlMapEntry implements Serializable {
    private static final long serialVersionUID = 5726643070784185212L;

    @XmlAttribute
    public String key;

    @XmlAttribute
    public String value;

    /**
     * converts an XmlMapEntry list
     * 
     * @param value
     * @return
     */
    public static Map<String, String> convert(final List<XmlMapEntry> value) {
        if (value == null) {
            return null;
        }
        final Map<String, String> ret = new HashMap<String, String>();
        for (final XmlMapEntry entry : value) {
            ret.put(entry.key, entry.value);
        }
        return ret;
    }

    /**
     * converts back to an string map
     * 
     * @param value
     * @return
     */
    public static List<XmlMapEntry> convert(final Map<?, ?> value) {
        if (value == null) {
            return null;
        }
        final List<XmlMapEntry> list = new ArrayList<XmlMapEntry>();
        for (final Entry<?, ?> entry : value.entrySet()) {
            final XmlMapEntry ent = new XmlMapEntry();
            ent.key = (String) entry.getKey();
            if (entry.getValue() instanceof Class<?>) {
                ent.value = ((Class<?>) entry.getValue()).getName();
            } else if (entry.getValue() instanceof IFragment) {
                ent.value = ((IFragment) entry.getValue()).getClass().getName();
            } else if (entry.getValue() instanceof IRenderer) {
                ent.value = ((IRenderer) entry.getValue()).getClass().getName();
            } else {
                ent.value = (String) entry.getValue();
            }
            list.add(ent);
        }
        return list;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final XmlMapEntry oo = (XmlMapEntry) obj;
        return (key == oo.key || key != null && key.equals(oo.key)) &&
                (value == oo.value || value != null && value.equals(oo.value));
    }

    @Override
    public int hashCode() {
        int hash = 837;
        if (key != null) {
            hash = hash * 3 + key.hashCode();
        }
        if (value != null) {
            hash = hash * 3 + value.hashCode();
        }
        return hash;
    }
}