/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.shared.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.shared.IConverter;

/**
 * typesation of an stringlist
 * 
 * @author xyan
 * 
 */
public class StringList extends ArrayList<String> implements IConverter<StringList> {

    private static final long serialVersionUID = -8117979043461375581L;

    @Override
    public void fromString(final String str) {
        if (StringUtils.isNotBlank(str)) {
            final String[] list = str.trim().split(",");
            if (list.length > 0) {
                addAll(Arrays.asList(list));
            }
        }
    }

    /**
     * creates an instance based on an string
     */
    public StringList(final String str) {
        fromString(str);
    }

    /**
     * sole constructor
     */
    public StringList() {

    }

    @Override
    public StringList convert(final String str) {
        return new StringList(str);
    }

    @Override
    public String convert(final Object obj) {
        final StringBuilder ret = new StringBuilder();
        @SuppressWarnings("unchecked")
        final Iterator<String> i = ((List<String>) obj).iterator();
        while (i.hasNext()) {
            ret.append(i.next());
            if (i.hasNext()) {
                ret.append(",");
            }
        }
        return ret.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public StringList valueOf(final Object obj) {
        if (obj instanceof StringList) {
            return (StringList) obj;
        } else if (obj instanceof String) {
            return convert((String) obj);
        } else if (obj instanceof List) {
            final StringList ret = new StringList();
            for (final Object ob : (List) obj) {
                if (ob instanceof String) {
                    ret.add((String) ob);
                } else {
                    ret.add(ob.toString());
                }
            }
            return ret;
        }
        return null;
    }
}
