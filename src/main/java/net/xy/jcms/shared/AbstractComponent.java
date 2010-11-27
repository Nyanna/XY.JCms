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

import net.xy.jcms.controller.configurations.UIConfiguration.UI;

/**
 * abstract component functionality. singleton pattern trough an cachepool.
 * 
 * @author Xyan
 * 
 */
public abstract class AbstractComponent implements IComponent {
    /**
     * place for static and widespread used ui configs
     */
    public static final UI<String> UI_STYLECLASS = new UI<String>("styleClass", "", false,
            "Sets an optional styleclass for html used in the class attribute");

    public static final UI<String> UI_CONTAINER = new UI<String>("container", "", false,
            "Specify an optional container surrounding the text (not used by renderkits)");

    public static final UI<String> UI_STYLEID = new UI<String>("id", "", false,
            "Sets an optional style id in an present container (not used by all renderkits)");
}
