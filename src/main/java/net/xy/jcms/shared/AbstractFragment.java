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

import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.FragmentConfiguration;

/**
 * common implementation of an fragment. is an child of ComponentConfiguration.
 * 
 * @author Xyan
 * 
 */
public abstract class AbstractFragment extends AbstractComponent implements IFragment {
    @Override
    public abstract FragmentConfiguration getConfiguration();

    @Override
    public void render(final IOutWriter out, final ComponentConfiguration config) {
        if (config instanceof FragmentConfiguration) {
            render(out, (FragmentConfiguration) config);
            return;
        }
        throw new IllegalArgumentException("Fragment was called with an wrong configuration type");
    }

    @Override
    public abstract void render(final IOutWriter out, final FragmentConfiguration config);

}
