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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * http protocol adapter for DataAccessContext
 * 
 * @author Xyan
 * 
 */
public class HttpRequestDataAccessContext implements IDataAccessContext {

    /**
     * represents the root url of this app
     */
    private final URI rootUrl;

    /**
     * holds the servletcontainer path
     */
    private String contextPath = "";

    /**
     * hold the initial independent request path
     */
    private final String requestPath;

    /**
     * stores DAC properties only
     */
    private final Map<Object, Object> properties = new HashMap<Object, Object>();

    public HttpRequestDataAccessContext(final HttpServletRequest request) throws MalformedURLException,
            URISyntaxException {
        // gets cappsubrand default locale and various other jj related
        // informations mendatory to retrieve jj configuration
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (final UnsupportedEncodingException e) {
        }
        contextPath = request.getContextPath() + "/";
        requestPath = request.getPathInfo().length() > 0 && request.getPathInfo().charAt(0) == '/' ? request
                .getPathInfo()
                .substring(1) : request.getPathInfo();
        rootUrl = new URI(request.getProtocol().split("/", 2)[0], null, request.getLocalName(), request.getLocalPort(),
                "/", null, null);

        // properties
        if (request.getParameter("flushConfig") != null) {
            properties.put("flushConfig", true);
        }
    }

    @Override
    public String buildUriWithParams(final String requestString, final Map<Object, Object> parameters) {
        // 1. should not alter external links
        // 2. should convert absolute uris to relatives if possible
        try {
            URI build; // the initial request
            try {
                // first asume its already an proper url
                build = new URI(requestString);
            } catch (final URISyntaxException ex) {
                // second encode to an proper url
                final String requestUri = URLEncoder.encode(requestString, "UTF-8");
                build = new URI(requestUri);
            }
            if (!build.isAbsolute()) {
                build = rootUrl.resolve(build); // make it absolute
            }
            final String path = StringUtils.isNotBlank(build.getPath()) ? build.getPath() : "/";
            build = new URI(build.getScheme(), build.getUserInfo(), build.getHost(), build.getPort(),
                    path, buildQuery(parameters), build.getFragment());
            final URI relBuild = rootUrl.relativize(build);
            final String ret;
            if (!relBuild.isAbsolute()) {
                // because we relativate it always to docroot, which is the
                // servlet container in JEE
                ret = contextPath + relBuild.toASCIIString().replace("+", "%20");
            } else {
                ret = relBuild.toASCIIString();
            }
            return ret;
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("URL couldn't be build from given parameters. "
                    + DebugUtils.printFields(requestString, parameters), e);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(" Encoding is not supported "
                    + DebugUtils.printFields(requestString, parameters), e);
        }
    }

    /**
     * helper method for constructing the query part
     * 
     * @param parameters
     * @return
     */
    private String buildQuery(final Map<Object, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        final StringBuilder query = new StringBuilder();
        for (final Entry<Object, Object> entry : parameters.entrySet()) {
            if (query.length() > 0) {
                query.append("&");
            }
            query.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return query.toString();
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public Object getProperty(final Object key) {
        return properties.get(key);
    }
}
