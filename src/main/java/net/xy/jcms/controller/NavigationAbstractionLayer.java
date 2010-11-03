/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller;

import java.util.HashMap;
import java.util.Map;

import net.xy.jcms.shared.IDataAccessContext;

/**
 * Abstraction Layer for Navigation<->Usecase<->Requests<->URL's $Path - is an
 * human language uri like /search_for_artist_shakira $Key/Keychain - is an
 * semantic description of the path via independent id's and must not be human
 * readable. Usually an Chain or list of Keys. Example /Contentgroup/Subcategory
 * 
 * @author xyan
 */
public class NavigationAbstractionLayer {

    /**
     * Each Key korresponds to one usecase
     * 
     * @author xyan
     * 
     */
    public static class NALKey {
        /**
         * id corresponding to an usecase e.g. "Contentgroup"
         */
        final private String id;

        /**
         * holds the parameter list
         */
        Map<Object, Object> parameters = new HashMap<Object, Object>();

        /**
         * default constructor
         * 
         * @param id
         */
        public NALKey(final String id) {
            this.id = id;
        }

        /**
         * copy constructor for relabeling to another id
         * 
         * @param id
         * @param old
         */
        public NALKey(final String id, final NALKey old) {
            this(id);
            setParameters(old.getParameters());
        }

        /**
         * sets an parameter to the Key like ContenTypes,DoctreeIds or
         * CatalogIds
         * 
         * @param key
         * @param value
         */
        public void addParameter(final Object key, final Object value) {
            parameters.put(key, value);
        }

        /**
         * returns an parameter
         * 
         * @param key
         * @return value parameter
         */
        public Object getParameter(final Object key) {
            return parameters.get(key);
        }

        /**
         * returns the key id
         * 
         * @return value
         */
        public String getId() {
            return id;
        }

        /**
         * get the parametermap
         * 
         * @return value
         */
        public Map<Object, Object> getParameters() {
            return parameters;
        }

        /**
         * adds an parameterlist
         * 
         * @param parameters
         */
        public void setParameters(final Map<Object, Object> parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "NALKey[ id=" + id + " parameters=" + parameters.toString() + "]";
        }
    }

    /**
     * translates
     * 
     * @param key
     *            [Contentgroup,Subcategory]
     * @return path "you_wanna_got_to_ringtones?"
     */
    public static String translateKeyToPath(final NALKey key, final IDataAccessContext dac) {
        return TranslationConfiguration.find(key, dac);
    }

    /**
     * translates
     * 
     * @param path
     *            "you_wanna_got_to_ringtones?"
     * @return Key[Contentgroup,Subcategory]
     */
    public static NALKey translatePathToKey(final String path, final IDataAccessContext dac) {
        return TranslationConfiguration.find(path, dac);
    }
}
