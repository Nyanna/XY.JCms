/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations;

import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * adapter which collects the usecases based on the dac. Adapter can use caching, read it from db, xml or filesystem.
 * 
 * @author xyan
 * 
 */
public interface IUsecaseConfigurationAdapter {

    /**
     * returns the usecase list
     * 
     * @param dac
     * @return value
     * @throws ClassNotFoundException
     */
    public Usecase[] getUsecaseList(final IDataAccessContext dac);
}
