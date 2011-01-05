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
package net.xy.jcms.portal.controller;

import java.util.Map;
import java.util.Map.Entry;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.TranslationConfiguration.GroupCouldNotBeFilled;
import net.xy.jcms.controller.TranslationConfiguration.InvalidBuildRule;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.portal.controller.ControllerConfiguration.Config;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IController;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.Model;

/**
 * abstract controller to share common code
 * 
 * @author Xyan
 * 
 */
abstract public class Controller implements IController {

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Model configuration, final Map<Object, Object> parameters) {
        return invoke(dac, configuration, ControllerConfiguration.build(this, configuration, parameters));
    }

    /**
     * instead of raw parameters u get and config object ready to retrieve item
     * from it.
     * 
     * @param dac
     * @param configuration
     * @param config
     * @return same as invoke by IController
     */
    protected abstract NALKey invoke(IDataAccessContext dac, Model configuration, Config config);

    /**
     * parses an NALKey definition from string in form usecaseid[param = value,
     * ...]
     * 
     * @param str
     * @return value
     */
    public static NALKey parseKeyString(String str) {
        str = str.trim();
        // first get id
        NALKey key;
        if (str.contains("[")) {
            final String id = str.substring(0, str.indexOf("["));
            key = new NALKey(id);
            final String[] params = str.substring(str.indexOf("[") + 1, str.lastIndexOf("]")).split(",");
            for (final String param : params) {
                final String[] pair = param.split("=");
                key.addParameter(pair[0], pair[1]);
            }
        } else {
            key = new NALKey(str);
        }
        return key;
    }

    /**
     * Builds an uri out from an parsed usecase string representation in format
     * of #UsecaseId[param=value]:messageKey
     * 
     * @param targetUsecaseStr
     *            #UsecaseId[param=value]:messageKey
     * @param dac
     * @return the ready parameter appended URI/URL
     * @throws GroupCouldNotBeFilled
     * @throws InvalidBuildRule
     */
    public static String buildUriForUsecase(final String targetUsecaseStr, final IDataAccessContext dac)
            throws GroupCouldNotBeFilled, InvalidBuildRule {
        final NALKey key = parseKeyString(targetUsecaseStr);
        return buildUriForKey(key, dac);
    }

    /**
     * Build an url path out from an NALKey
     * 
     * @param key
     * @param dac
     * @return the ready parameter appended URI/URL
     * @throws GroupCouldNotBeFilled
     * @throws InvalidBuildRule
     */
    public static String buildUriForKey(final NALKey key, final IDataAccessContext dac)
            throws GroupCouldNotBeFilled, InvalidBuildRule {
        final TranslationRule rule = NavigationAbstractionLayer.findRuleForKey(key, dac);
        if (rule == null) {
            throw new IllegalArgumentException("No usecase could be found for the configured key. "
                    + DebugUtils.printFields(key));
        }
        final String path = NavigationAbstractionLayer.translateKeyWithRule(key, rule);
        final String url = dac.buildUriWithParams(path, null);
        return url;
    }

    /**
     * fills ? in an NALKey parameter list with its according obmitted
     * replacement:
     * -first ? maps to first replacement and so on
     * 
     * @param origin
     * @param replacements
     */
    public static void fillReplacements(final NALKey origin, final Object... replacements) {
        int counter = 0;
        for (final Entry<Object, Object> param : origin.getParameters().entrySet()) {
            if (param.getValue() instanceof String && ((String) param.getValue()).equals("?")) {
                if (counter <= replacements.length) {
                    param.setValue(replacements[counter]);
                }
                ++counter;
            }
        }
    }
}
