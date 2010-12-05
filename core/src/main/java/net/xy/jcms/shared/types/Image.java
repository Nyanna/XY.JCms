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
package net.xy.jcms.shared.types;

import org.apache.commons.lang.StringUtils;

/**
 * specifies an image transfer object
 * 
 * @author xyan
 * 
 */
public class Image {

    /**
     * stores the request string or uri to retrieve the image
     */
    private final String reqStr;

    /**
     * stores the image width
     */
    private final int width;

    /**
     * stores the image height
     */
    private final int height;

    /**
     * stores an optional image title
     */
    private final String title;

    /**
     * default constructor, object is immutable
     * 
     * @param reqStr
     * @param width
     * @param height
     * @param title
     */
    public Image(final String reqStr, final int width, final int height, final String title) {
        if (StringUtils.isBlank(reqStr)) {
            throw new IllegalArgumentException("Request string can't be empty");
        }
        this.reqStr = reqStr;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    /**
     * get the request string to get the image
     * 
     * @return value
     */
    public String getReqStr() {
        return reqStr;
    }

    /**
     * gets the estimated width of the image
     * 
     * @return value
     */
    public int getWidth() {
        return width;
    }

    /**
     * gets the estimated height of the image
     * 
     * @return value
     */
    public int getHeight() {
        return height;
    }

    /**
     * gets an optional title of the image
     * 
     * @return value
     */
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!Image.class.isInstance(obj)) {
            return false;
        }
        final Image oo = (Image) obj;
        return reqStr.equals(reqStr) && width == oo.width && height == oo.height && StringUtils.equals(title, oo.title);
    }

    @Override
    public int hashCode() {
        int hash = 32;
        hash = hash * 3 + reqStr.hashCode();
        hash = hash * 3 + width;
        hash = hash * 3 + height;
        if (title != null) {
            hash = hash * 3 + title.hashCode();
        }
        return hash;
    }
}
