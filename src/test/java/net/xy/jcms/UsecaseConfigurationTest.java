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

import java.util.EnumSet;
import java.util.Properties;

import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Parameter;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.CLIRunner.CLIDataAccessContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UsecaseConfigurationTest {

    @Before
    public void setup() {
        UsecaseConfiguration.setUsecaseAdapter(new MockUsecaseConfiguration());
    }

    @Test
    public void testFindUsecase() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new CLIDataAccessContext("test"));
        Assert.assertNotNull(uCase);
        System.out.append(uCase.toString());
    }

    @Test
    public void testFindUsecaseNotId() {
        final NALKey key = new NALKey("contentgroupNot");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new CLIDataAccessContext("test"));
        Assert.assertNull(uCase);
    }

    @Test
    public void testFindUsecaseNotParameter() {
        final NALKey key = new NALKey("contentgroupt");
        key.addParameter("contentgroupNot", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new CLIDataAccessContext("test"));
        Assert.assertNull(uCase);
    }

    @Test
    public void testForwarding() throws ClassNotFoundException {
        UsecaseConfiguration.setUsecaseAdapter(new IUsecaseConfigurationAdapter() {
            @Override
            public Usecase[] getUsecaseList(final IDataAccessContext dac) {
                return new Usecase[] {
                        new Usecase("contentgroup", "first test", new Parameter[] { new Parameter("contentgroup",
                                "de.jamba.ContentGroup") }, new Controller[] { new Controller(
                                new MockController(),
                                EnumSet.of(ConfigurationType.MessageConfiguration))

                        }, new Configuration[] { new MessageConfiguration(new Properties() {
                            private static final long serialVersionUID = 4127043651680566300L;
                            {
                                put("test", "test");
                            }

                        }) }), new Usecase("subcategory", "first test", new Parameter[] {}, new Controller[] {

                        }, new Configuration[] {

                        }) };
            }

        });

        // begin test
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new CLIDataAccessContext("test"));
        Assert.assertNotNull(uCase);
        final NALKey forward = UsecaseAgent.executeController(uCase, new CLIDataAccessContext("test"), null);
        Assert.assertNotNull(forward);
        System.out.append(forward.toString());
    }

}
