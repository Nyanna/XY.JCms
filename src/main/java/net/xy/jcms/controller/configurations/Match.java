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
package net.xy.jcms.controller.configurations;

/**
 * simple closure object, for returning two values
 * 
 * @author Xyan
 * 
 * @param <PATH>
 *            type of path key on which the value was found
 * @param <VALUE>
 *            type of value which caould be retrieved
 */
class Match<PATH, VALUE> {
    /**
     * path
     */
    final PATH path;

    /**
     * value
     */
    final VALUE value;

    /**
     * default
     * 
     * @param path
     * @param value
     */
    public Match(final PATH path, final VALUE value) {
        this.path = path;
        this.value = value;
    }

    /**
     * returns the parh
     * 
     * @return value
     */
    public PATH getPath() {
        return path;
    }

    /**
     * returns the value
     * 
     * @return value
     */
    public VALUE getValue() {
        return value;
    }

}
