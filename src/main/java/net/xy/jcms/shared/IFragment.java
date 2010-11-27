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

import net.xy.jcms.controller.configurations.FragmentConfiguration;

/**
 * general fragment interface. in general fragments got compiled from xml files
 * and are so editable as WYSIWYG editors.
 * 
 * @author Xyan
 * 
 */
public interface IFragment extends IComponent {
    /**
     * returns the fragments configuration preparing alghorhythms
     * 
     * @param name
     *            the fragments name
     * @return the fragments requested configuration
     */
    @Override
    public FragmentConfiguration getConfiguration();

    /**
     * starts the rendering run
     * 
     * @param out
     * @param config
     *            prior requested evaluated config
     */
    public void render(final IOutWriter out, final FragmentConfiguration config);
}
