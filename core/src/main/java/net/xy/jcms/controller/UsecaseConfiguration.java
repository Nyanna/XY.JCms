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
package net.xy.jcms.controller;

import java.util.ArrayList;
import java.util.List;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.usecase.Parameter;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * Configuration object describing the usecases and its behavior
 * 
 * @author xyan
 * 
 */
public class UsecaseConfiguration {
    /**
     * finds the most matching usecase for an given struct by comparing its id
     * and parameters
     * 
     * @param struct
     * @return value
     */
    public static Usecase findUsecaseForStruct(final NALKey struct, final IDataAccessContext dac) {
        if (struct == null) {
            return null;
        }
        final Usecase[] byIdSelected = getUsecasesById(struct.getId(), getUsecaseList(dac));
        if (byIdSelected.length > 1) {
            return findMostMatchingParams(byIdSelected, struct);
        } else if (byIdSelected.length == 1) {
            return byIdSelected[0];
        } else {
            return null;
        }
    }

    /**
     * retrieves the usecase list
     * 
     * @return value
     */
    private static Usecase[] getUsecaseList(final IDataAccessContext dac) {
        if (adapter == null) {
            throw new IllegalArgumentException("Usecase configuration adapter was not injected");
        }
        final Usecase[] list = adapter.getUsecaseList(dac);
        if (list == null) {
            throw new IllegalArgumentException("Usecase list can't be retrieved.");
        }
        return list;
    }

    /**
     * stores the usecase configuration adapter
     */
    private static IUsecaseConfigurationAdapter adapter;

    /**
     * sets the usecase configuration adapter
     * 
     * @param adapter
     */
    public static void setUsecaseAdapter(final IUsecaseConfigurationAdapter adapter) {
        UsecaseConfiguration.adapter = adapter;
    }

    /**
     * reduces caselist to the ones matching by id
     * 
     * @param id
     * @param list
     * @return value
     */
    private static Usecase[] getUsecasesById(final String id, final Usecase[] list) {
        final List<Usecase> retList = new ArrayList<Usecase>();
        for (final Usecase ucase : list) {
            if (ucase.getId().equalsIgnoreCase(id)) {
                retList.add(ucase);
            }
        }
        return retList.toArray(new Usecase[retList.size()]);
    }

    /**
     * find the usecase with the most matching parameter count
     * 
     * @param list
     * @param struct
     * @return value
     */
    private static Usecase findMostMatchingParams(final Usecase[] list, final NALKey struct) {
        Usecase foundCase = null;
        int reachedMatches = -1;
        // for each usecase
        for (final Usecase ucase : list) {
            final int matches = countMatchingParams(ucase, struct);
            if (matches > reachedMatches) {
                reachedMatches = matches;
                foundCase = ucase;
            }
        }
        return foundCase;
    }

    /**
     * counts the parameter presence matches of NALKey against an usecase
     * 
     * @param ucase
     * @param struct
     * @return value
     */
    private static int countMatchingParams(final Usecase ucase, final NALKey struct) {
        int counter = 0;
        for (final Parameter param : ucase.getParameterList()) {
            if (struct.getParameters().containsKey(param.getParameterKey())) {
                counter++;
            }
        }
        return counter;
    }
}
