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
package net.xy.jcms.shared.adapter;

import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.stores.ClientStore;

/**
 * protocol specific response adapter which alters http response headers
 * 
 * @author xyan
 * 
 */
public class HttpProtocolResponseAdapter {

    /**
     * alters the response headers based on the obmitted configuration maybe an
     * dedicated response configuration should be introduced
     * 
     * @param response
     * @param model
     */
    public static void apply(final HttpServletResponse response, final Configuration<?>[] model) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
    }

    /**
     * appends the clientstore back to the http response. for http it should
     * only send changed dataas cookies.
     * 
     * @param store
     */
    public static void saveClientStore(final HttpServletResponse response, final ClientStore store) {
        if (store == null) {
            return;
        }
        if (ClientStore.Type.ONCLIENT.equals(store.getType())) {
            // TODO [LOW] implement store object with expiration time
            for (final Entry<String, Object> entry : store.getAll().entrySet()) {
                response.addCookie(new Cookie(entry.getKey(), ClientStore.objectToString(entry.getValue())));
            }
        } else if (ClientStore.Type.ONSERVER.equals(store.getType())) {
            // nothing todo for http session
        }
        return;
    }
}
