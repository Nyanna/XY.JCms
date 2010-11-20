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

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.ControllerConfiguration;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.shared.types.StringList;
import net.xy.jcms.shared.types.StringMap;

abstract public class StaticsInclusion extends Controller {

    /**
     * instruction section in config
     */
    private static final String INSTRUCTION_SECTION = "include";

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration) {
        return invoke(dac, configuration, new HashMap<Object, Object>());
    }

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration,
            final Map<Object, Object> parameters) {
        final ControllerConfiguration configK = (ControllerConfiguration) JCmsHelper.getConfigurationByType(
                ControllerConfiguration.TYPE, configuration);
        final ContentRepository configC = (ContentRepository) JCmsHelper.getConfigurationByType(
                ContentRepository.TYPE, configuration);
        if (configC == null || configK == null) {
            throw new IllegalArgumentException("Missing configurations");
        }
        proccess(configK, configC, dac);
        return null;
    }

    /**
     * proccesses the instructionset
     * 
     * @param configK
     * @param configC
     */
    @SuppressWarnings("unchecked")
    private void proccess(final ControllerConfiguration configK, final ContentRepository configC,
            final IDataAccessContext dac) {
        final Map<String, Object> ownC = getControllerConfig(configK);
        if (ownC.get(INSTRUCTION_SECTION) instanceof List) {
            for (final Map<Object, String> instruction : (List<Map<Object, String>>) ownC.get(INSTRUCTION_SECTION)) {
                // get domain in case of one exists
                final String domain = (String) getPreciseOrGlobal("domain", ownC, instruction);
                // get path prefix
                final String prefix;
                if (domain != null) {
                    prefix = domain + (String) getPreciseOrGlobal("prefix", ownC, instruction);
                } else {
                    prefix = (String) getPreciseOrGlobal("prefix", ownC, instruction);
                }
                // get type
                final String type = (String) getPreciseOrGlobal("type", ownC, instruction);

                Object finalContent = null;
                final Object firstContent = instruction.get("content");
                if (String.class.isInstance(firstContent)) {
                    final String strContent = (String) firstContent;
                    // get content in right object
                    if ("StringList".equalsIgnoreCase(type)) {
                        // get url list
                        final StringList contentList = new StringList(strContent);
                        // prefix if configured
                        if (StringUtils.isNotBlank(prefix)) {
                            final ListIterator<String> it = contentList.listIterator();
                            while (it.hasNext()) {
                                final String content = it.next();
                                it.set(prefix + content.trim());
                            }
                        }
                        finalContent = contentList;
                    } else if ("StringMap".equalsIgnoreCase(type)) {
                        finalContent = new StringMap(strContent);
                    } else if ("String".equalsIgnoreCase(type)) {
                        finalContent = prefix + strContent;
                    } else {
                        // call abstract
                        finalContent = processType(type, instruction, ownC, prefix, dac);
                    }
                } else {
                    finalContent = firstContent;
                }

                // put in targets
                if (finalContent != null) {
                    for (final String target : new StringList(instruction.get("target"))) {
                        configC.putContent(target, finalContent);
                    }
                }
            }
        }
    }

    /**
     * processes one to the StaticInclusion unknown portal dependent type
     * 
     * @param type
     *            an string representing the type like StringMap, FlashTeaser
     * @param sectionParams
     * @param globals
     * @param prefix
     *            path prefix, can be null
     * @param domain
     *            an domain to set, can be null
     * @return
     */
    protected abstract Object processType(final String type, final Map<Object, String> sectionParams,
            final Map<String, Object> globals, final String prefix, final IDataAccessContext dac);

}
