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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.TranslationConfiguration.GroupCouldNotBeFilled;
import net.xy.jcms.controller.TranslationConfiguration.InvalidBuildRule;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.ControllerConfiguration;
import net.xy.jcms.controller.usecase.IController;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.shared.types.StringList;

/**
 * implements aggregation of navigation content
 * 
 * @author Xyan
 * 
 */
public abstract class AbstractNavigationAggregationController<LINKOBJECT> implements IController {

    /**
     * specifies the instruction section name
     */
    private static final String INSTRUCTION_SECTION = "navigation";

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration) {
        return invoke(dac, configuration, null);
    }

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration,
            final Map<Object, Object> parameters) {
        try {
            proccess(dac, configuration, parameters);
        } catch (final GroupCouldNotBeFilled e) {
            throw new IllegalArgumentException(
                    "NavigationAggregationController configuration is invalid. Parameter replacement failure.", e);
        } catch (final InvalidBuildRule e) {
            throw new IllegalArgumentException(
                    "NavigationAggregationController configuration is invalid. Rule not found or can't be applied.", e);
        }
        return null;
    }

    /**
     * proccesses the navigation aggregation
     * 
     * @param configK
     * @param configC
     * @throws InvalidBuildRule
     * @throws GroupCouldNotBeFilled
     */
    @SuppressWarnings("unchecked")
    private void proccess(final IDataAccessContext dac, final Configuration<?>[] configuration,
            final Map<Object, Object> parameters)
            throws GroupCouldNotBeFilled, InvalidBuildRule {

        final ControllerConfiguration configK = (ControllerConfiguration) JCmsHelper.getConfigurationByType(
                ControllerConfiguration.TYPE, configuration);
        final ContentRepository configC = (ContentRepository) JCmsHelper.getConfigurationByType(
                ContentRepository.TYPE, configuration);

        if (configC == null || configK == null) {
            throw new IllegalArgumentException("Missing configurations");
        }

        final Map<String, Object> ownC = getControllerConfig(configK);
        if (ownC.get(INSTRUCTION_SECTION) instanceof List) {
            for (final Map<String, String> instruction : (List<Map<String, String>>) ownC.get(INSTRUCTION_SECTION)) {
                final List<LINKOBJECT> contents = new LinkedList<LINKOBJECT>();
                for (final Entry<String, String> navItem : instruction.entrySet()) {
                    if (navItem.getKey().startsWith("key")) {
                        String targetUsecase = navItem.getValue().trim();
                        String messageKey = targetUsecase;
                        if (targetUsecase.contains(":")) {
                            final String[] split = targetUsecase.split(":", 2);
                            targetUsecase = split[0];
                            messageKey = split[1];
                        }
                        final String url = buildUriForUsecase(targetUsecase, dac);
                        contents.add(getLinkDTO(url, messageKey, configuration, parameters));
                    } else if (navItem.getKey().startsWith("url")) {
                        String url = navItem.getValue().trim();
                        url = dac.buildUriWithParams(url, null);
                        contents.add(getLinkDTO(url, navItem.getKey(), configuration, parameters));
                    }
                }
                final StringList targets = new StringList(instruction.get("target"));
                saveLinkDTOs(configC, targets, contents, configuration, parameters);
            }
        }
    }

    /**
     * parses an NALKey definition from string in form usecaseid[param = value, ...]
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
     * Builds an uri out from an parsed usecase string representation in format of #UsecaseId[param=value]:messageKey
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
     * provides a way to convert into an domain object
     * 
     * @param href
     * @param usecaseId
     * @return
     */
    protected abstract LINKOBJECT getLinkDTO(final String href, final String usecaseId,
            final Configuration<?>[] configuration,
            final Map<Object, Object> parameters);

    /**
     * provides an way to store the links in an domain container
     * 
     * @param configC
     * @param targets
     * @param dtos
     */
    protected abstract void saveLinkDTOs(final ContentRepository configC, final StringList targets,
            final List<LINKOBJECT> dtos,
            final Configuration<?>[] configuration,
            final Map<Object, Object> parameters);

    /**
     * provides a way to get the final class's config
     * 
     * @param configK
     * @return
     */
    protected abstract Map<String, Object> getControllerConfig(final ControllerConfiguration configK);
}
