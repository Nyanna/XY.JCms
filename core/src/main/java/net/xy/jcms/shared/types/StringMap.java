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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.jcms.shared.IConverter;
import net.xy.jcms.shared.InitializableController;

/**
 * simple typesation of an stringmap
 * 
 * @author xyan
 * 
 */
public class StringMap extends HashMap<String, String> implements InitializableController<StringMap> {
    private static final long serialVersionUID = 6126913995340768165L;

    /**
     * initializes an empty instance
     */
    public StringMap() {}

    /**
     * accepts an map and copies it values
     * 
     * @param map
     */
    public StringMap(final Map<String, String> map) {
        putAll(map);
    }

    /**
     * creates an instance based on an string
     */
    public StringMap(final String str) {
        fromString(str);
    }

    @Override
    public void fromString(final String str) {
        if (str != null) {
            final String[] pairs = str.trim().split(",");
            for (final String pair : pairs) {
                final String[] ppair = pair.split(":", 2);
                put(ppair[0], ppair[1]);
            }
        }
    }

    @Override
    public StringMap convert(final String str) {
        return new StringMap(str);
    }

    @Override
    public String convert(final Object obj) {
        final StringBuilder ret = new StringBuilder();
        @SuppressWarnings("unchecked")
        final Iterator<Entry<String, String>> i = ((Map<String, String>) obj).entrySet().iterator();
        while (i.hasNext()) {
            final Entry<String, String> entry = i.next();
            ret.append(entry.getKey()).append(":").append(entry.getValue());
            if (i.hasNext()) {
                ret.append(",");
            }
        }
        return ret.toString();
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public StringMap valueOf(final Object obj) {
        if (obj instanceof StringMap) {
            return (StringMap) obj;
        } else if (obj instanceof String) {
            return convert((String) obj);
        } else if (obj instanceof Map) {
            final StringMap ret = new StringMap();
            for (final Entry<Object, Object> entry : ((Map<Object, Object>) obj).entrySet()) {
                ret.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return ret;
        }
        return null;
    }

    @Override
    public IConverter<StringMap> initialize(final Map<String, String> options) {
        return new StringMap(options);
    }

    @Override
    public Map<String, String> store() {
        return this;
    }
}
