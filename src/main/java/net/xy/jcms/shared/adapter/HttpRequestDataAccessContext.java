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
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * http protocol adapter for DataAccessContext
 * 
 * @author Xyan
 * 
 */
public class HttpRequestDataAccessContext implements IDataAccessContext {
    private final static Logger LOG = Logger.getLogger(IDataAccessContext.class);

    /**
     * represents the root url of this app
     */
    private final URI rootUrl;

    /**
     * if an session id should be stored on every link
     */
    private String sessionId = null;

    public HttpRequestDataAccessContext(final HttpServletRequest request) throws MalformedURLException,
            URISyntaxException {
        // gets cappsubrand default locale and various other jj related
        // informations mendatory to retrieve jj configuration
        // request.getRequestURI().substring(0,
        // request.getRequestURI().lastIndexOf("/") + 1)
        rootUrl = new URI(request.getProtocol().split("/", 2)[0], null, request.getLocalName(), request.getLocalPort(),
                "/", null, null);
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
            if (!relBuild.isAbsolute()) {
                // because we relativate it always to docroot
                return "/" + relBuild.toASCIIString();
            } else {
                return relBuild.toASCIIString();
            }
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

    /**
     * gets the session id string
     * 
     * @return value
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * sets an session id
     * 
     * @param sessionId
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
