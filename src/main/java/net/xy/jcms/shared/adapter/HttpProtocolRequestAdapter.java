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
package net.xy.jcms.shared.adapter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;

/**
 * protocol specific adaption of the navigation key
 * 
 * @author xyan
 * 
 */
public class HttpProtocolRequestAdapter {

    /**
     * extracts requiered information from the protocol and adds them to the key
     * 
     * @param request
     * @param key
     * @return always an key
     */
    public static NALKey apply(final HttpServletRequest request, final NALKey key) {
        // TODO [LOW] cookies, get data, post data, request headers
        return key;
    }

    /**
     * appends http protocol request parameters in ?key=val&... style
     * 
     * @param path
     *            an human readable already translated path
     * @param parameters
     * @return
     */
    public static String appendParametersToPath(final String path, final Map<Object, Object> parameters) {
        // TODO [LOW] implement logic to append ?name=value&...
        return path;
    }
}
