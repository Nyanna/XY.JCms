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
package net.xy.jcms.shared;

import java.util.Map;

/**
 * a context in which all information are provided who are vital to access external resources. Formaly known as portal
 * context, request context, portal configuration, cappsubrand and vice versa. It will be filled by an factory injected
 * and configured with spring or/and with request information. Access to the containing data is only made in the
 * aggregator layer who knows which underlying service need what for information to provide data.
 * 
 * @author xyan
 * 
 */
public interface IDataAccessContext {

    /**
     * builds protocol dependent references for the client, URL for http, commandlines with args for CLI
     * 
     * @param path
     * @param parameters
     * @return value
     */
    public String buildUriWithParams(final String path, Map<Object, Object> parameters);

    /**
     * returns the protocoll independent request path to be proccessed by NAL
     * 
     * @return value
     */
    public String getRequestPath();

    /**
     * retrieves an option or setting from dac
     * 
     * @param key
     * @return value
     */
    public Object getProperty(final Object key);
}
