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
package net.xy.jcms;

import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.shared.IController;
import net.xy.jcms.shared.IDataAccessContext;

import org.junit.Assert;

public class MockController implements IController {

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration) {
        Assert.assertTrue(configuration[0] instanceof MessageConfiguration);
        final MessageConfiguration mess = (MessageConfiguration) configuration[0];
        if (mess != null) {

        }
        return new NALKey("subcategory");
    }

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration,
            final Map<Object, Object> parameters) {
        Assert.assertTrue(configuration[0] instanceof MessageConfiguration);
        final MessageConfiguration mess = (MessageConfiguration) configuration[0];
        if (mess != null) {

        }
        return new NALKey("subcategory");
    }
}
