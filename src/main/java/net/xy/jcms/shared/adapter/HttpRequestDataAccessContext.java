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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
    private final URL rootUrl;

    public HttpRequestDataAccessContext(final HttpServletRequest request) throws MalformedURLException {
        // gets cappsubrand default locale and various other jj related
        // informations mendatory to retrieve jj configuration
        rootUrl = new URL(request.getProtocol().split("/", 2)[0], request.getLocalName(), request.getLocalPort(), "");
    }

    @Override
    public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
        // TODO [LOW] write test an check transforming
        URL build;
        try {
            build = new URL(rootUrl, path);
            return build.toString();
        } catch (final MalformedURLException e) {
        }
        return null;
    }
}
