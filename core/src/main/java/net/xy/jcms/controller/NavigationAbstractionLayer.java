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
package net.xy.jcms.controller;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import net.xy.jcms.controller.TranslationConfiguration.GroupCouldNotBeFilled;
import net.xy.jcms.controller.TranslationConfiguration.InvalidBuildRule;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * Abstraction Layer for Navigation<->Usecase<->Requests<->URL's
 * $Path - is an human language uri like /search_for_artist_shakira
 * $Key - is an semantic description of the path via independent id's and must not be human
 * readable. Example /Contentgroup.
 * 
 * @author xyan
 */
public class NavigationAbstractionLayer {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(NavigationAbstractionLayer.class);

    /**
     * Each Key korresponds to one usecase. Not immutable
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
        Map<Object, Object> parameters = new TreeMap<Object, Object>();

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
            setParameters(new TreeMap<Object, Object>(old.getParameters()));
        }

        /**
         * copy constructor
         * 
         * @param old
         */
        public NALKey(final NALKey old) {
            this(old.id, old);
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
            if (parameters == null) {
                this.parameters = new TreeMap<Object, Object>();
            } else {
                this.parameters = parameters;
            }
        }

        @Override
        public String toString() {
            return "NALKey[ id=" + id + " parameters=" + parameters.toString() + "]";
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!NALKey.class.isInstance(obj)) {
                return false;
            }
            final NALKey otherObj = (NALKey) obj;
            return id.equals(otherObj.id) && parameters.equals(otherObj.parameters);
        }

        @Override
        public int hashCode() {
            int hash = 25;
            hash = hash * 3 + id.hashCode();
            hash = hash * 3 + parameters.hashCode();
            return hash;
        }
    }

    /**
     * translates
     * 
     * @param key
     *            [Subcategory]
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
     * @return Key[Subcategory]
     */
    public static NALKey translatePathToKey(final IDataAccessContext dac) {
        return TranslationConfiguration.find(dac.getRequestPath(), dac);
    }

    /**
     * generates an path with an rule out from an key. protected for unit
     * testing
     * 
     * @param key
     * @param rule
     * @return the ready translated path elsewhere it rises an
     *         GroupCouldNotBeFilled
     * @throws GroupCouldNotBeFilled
     *             in case parameter replacement failures
     * @throws InvalidBuildRule
     *             in case the buildrule can't be applied
     */
    public static String translateKeyWithRule(final NALKey key, final TranslationRule rule)
            throws GroupCouldNotBeFilled,
            InvalidBuildRule {
        return TranslationConfiguration.translateKeyWithRule(key, rule);
    }

    /**
     * finds the to an key corresponding rule
     * 
     * @param struct
     * @return null or the rule
     */
    public static TranslationRule findRuleForKey(final NALKey struct, final IDataAccessContext dac) {
        return TranslationConfiguration.findRuleForKey(struct, dac);
    }
}
