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

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;

import org.junit.Test;
import org.junit.Assert;

public class UsecaseParserTest {

    @Test
    public void tesParser() throws ClassNotFoundException {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleUsecases.xml"), Thread.currentThread().getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(cases);
    }
}
