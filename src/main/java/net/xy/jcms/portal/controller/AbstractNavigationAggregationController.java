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
                    "NavigationAggregationController configuration is invalid. Parameter replacement failure.");
        } catch (final InvalidBuildRule e) {
            throw new IllegalArgumentException(
                    "NavigationAggregationController configuration is invalid. Rule not found or can't be applied.");
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
                    if (!navItem.getKey().startsWith("key")) {
                        continue;
                    }
                    final String targetUsecase = navItem.getValue().trim();
                    final NALKey key = new NALKey(targetUsecase);
                    final TranslationRule rule = NavigationAbstractionLayer.findRuleForKey(key, dac);
                    final String path = NavigationAbstractionLayer.translateKeyWithRule(key, rule);
                    final String url = dac.buildUriWithParams(path, null);
                    contents.add(getLinkDTO(url, targetUsecase, configuration, parameters));
                }
                final StringList targets = new StringList(instruction.get("target"));
                saveLinkDTOs(configC, targets, contents, configuration, parameters);
            }
        }
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
