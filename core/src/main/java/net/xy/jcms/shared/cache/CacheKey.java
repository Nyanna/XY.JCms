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
package net.xy.jcms.shared.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * an uniform cachekey with an intern sorted list of its parts.
 * all keys of the same components have the same value not regarding insertion
 * order.
 * 
 * @author Xyan
 * 
 */
public class CacheKey {

    /**
     * internal store for components
     */
    private List<Object> store = new LinkedList<Object>();

    /**
     * object comparator
     */
    private static final Comparator<Object> COMPARATOR = new ObjectComparator();

    /**
     * flag to prevent multiple sorting runs
     */
    private final boolean isSorted = false;

    /**
     * default, initializes empty
     */
    public CacheKey() {
    }

    /**
     * sets start components of cache key
     * 
     * @param objs
     */
    public CacheKey(final Object... objs) {
        add(objs);
    }

    /**
     * adds components to cache key
     * 
     * @param objs
     */
    public void add(final Object... objs) {
        for (final Object object : objs) {
            store.add(object);
        }
    }

    /**
     * method wrapper to be cpmatible with StringBuilder
     * 
     * @param objs
     */
    public void append(final Object... objs) {
        add(objs);
    }

    /**
     * removes objects from cache key
     * 
     * @param objs
     */
    public void remove(final Object... objs) {
        for (final Object object : objs) {
            store.remove(object);
        }
    }

    /**
     * clears all components in this key
     */
    public void clear() {
        store = new LinkedList<Object>();
    }

    @Override
    public int hashCode() {
        sort();
        return store.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        sort();
        return store.equals(obj);
    }

    @Override
    public String toString() {
        sort();
        final StringBuilder result = new StringBuilder();
        for (final Object object : store) {
            result.append(object);
        }
        return result.toString();
    }

    /**
     * sorts the components if needed
     */
    private void sort() {
        if (!isSorted) {
            Collections.sort(store, COMPARATOR);
        }
    }

    /**
     * compares object by using compareTo or an toString compareTo
     * 
     * @author Xyan
     * 
     */
    private static class ObjectComparator implements Comparator<Object> {

        /**
         * compares two objects of any given type
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare(final Object o1, final Object o2) {
            if (o1 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2);
            } else {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }
}
