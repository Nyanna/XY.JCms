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
package net.xy.jcms.shared.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.shared.IConverter;

/**
 * simple typesation of an longlist
 * 
 * @author xyan
 * 
 */
public class LongList extends LinkedList<java.lang.Long> implements IConverter {
    private static final long serialVersionUID = -2982736336731356218L;

    @Override
    public void fromString(final String str) {
        if (StringUtils.isNotBlank(str)) {
            final String[] list = str.trim().split(",");
            for (final String strg : list) {
                add(java.lang.Long.valueOf(strg));
            }
        }
    }

    /**
     * creates an instance based on an string
     */
    public LongList(final String str) {
        fromString(str);
    }

    @Override
    public LongList convert(final String str) {
        return new LongList(str);
    }

    @Override
    public String convert(final Object obj) {
        final StringBuilder ret = new StringBuilder();
        @SuppressWarnings("unchecked")
        final Iterator<Long> i = ((List<Long>) obj).iterator();
        while (i.hasNext()) {
            ret.append(i.next());
            if (i.hasNext()) {
                ret.append(",");
            }
        }
        return ret.toString();
    }
}
