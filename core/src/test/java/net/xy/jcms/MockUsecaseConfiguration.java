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
package net.xy.jcms;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.usecase.Controller;
import net.xy.jcms.controller.usecase.Parameter;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * mock in lack of an configuration reader
 * 
 * @author xyan
 * 
 */
public class MockUsecaseConfiguration implements IUsecaseConfigurationAdapter {

    /**
     * mock configuration
     * 
     * @return value
     */
    @Override
    public Usecase[] getUsecaseList(final IDataAccessContext dac) {
        return new Usecase[] {
                new Usecase("contentgroup", "first test with an very long and precise description",
                        new Parameter[] { new Parameter("contentgroup",
                                "de.jamba.ContentGroup") }, new Controller[] {

                        }, new Configuration[] {

                        }),
                new Usecase("subcategory", "first test first test with an very long and precise description",
                        new Parameter[] { new Parameter("contentgroup",
                                "de.jamba.ContentGroup") }, new Controller[] {

                        }, new Configuration[] {

                        }) };
    }
}
