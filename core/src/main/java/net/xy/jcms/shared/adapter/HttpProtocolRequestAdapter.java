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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.stores.ClientStore;

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
     * @return value always an key
     */
    @SuppressWarnings("unchecked")
    public static NALKey apply(final HttpServletRequest request, final NALKey key) {
        if (request == null || key == null) {
            throw new IllegalArgumentException("Either request or NALKey object is null.");
        }
        final Map<Object, Object> params = new HashMap<Object, Object>();
        params.putAll(getCookieParams(request));
        params.putAll(key.getParameters()); // get old ones
        for (final Entry<String, String[]> entry : ((Map<String, String[]>) request.getParameterMap()).entrySet()) {
            if (entry.getValue() == null || entry.getValue().length < 1) {
                params.put(entry.getKey(), null);
            } else {
                params.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        }
        params.put(ClientStore.class.getName(), initClientStore(request));
        // dac.setSessionId(request.getSession().getId());
        key.setParameters(params);
        return key;
    }

    /**
     * extracts an clientstore out from the http request, for http it fills the
     * store always with all cookies
     * 
     * @param request
     * @return returns an -1 limited store if the client don't supports an store
     */
    private static ClientStore initClientStore(final HttpServletRequest request) {
        if (supportsCookies(request)) {
            // 20 per domain * 4 kb each calculated from the headerline RFC 2109
            return new ClientStore(4000, ClientStore.Type.ONCLIENT);
        } else if (request.getSession() != null) {
            // session present read from there
            return new ClientStore(-1, ClientStore.Type.ONSERVER);
        } else {
            return new ClientStore(0, ClientStore.Type.NONE);
        }
    }

    /**
     * returns all cookie params from the request
     * 
     * @param request
     * @return value
     */
    private static final Map<String, Object> getCookieParams(final HttpServletRequest request) {
        final Map<String, Object> params = new HashMap<String, Object>();
        if (request.getCookies() != null && request.getCookies().length > 0) {
            for (final Cookie cookie : request.getCookies()) {
                params.put(cookie.getName(), cookie.getValue());
                params.put(cookie.getName() + ".maxage", cookie.getMaxAge());
                params.put(cookie.getName() + ".domain", cookie.getDomain());
                params.put(cookie.getName() + ".path", cookie.getPath());
                params.put(cookie.getName() + ".comment", cookie.getComment());
                params.put(cookie.getName() + ".version", cookie.getVersion());
                params.put(cookie.getName() + ".secure", cookie.getSecure());
            }
        }
        return params;
    }

    /**
     * checks if client supports cookies based on if he has send at least one
     * 
     * @param request
     * @return value
     */
    public static boolean supportsCookies(final HttpServletRequest request) {
        return request.getCookies() != null && request.getCookies().length > 0;
    }
}
