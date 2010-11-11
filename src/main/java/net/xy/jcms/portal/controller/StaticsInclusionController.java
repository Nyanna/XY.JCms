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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.ControllerConfiguration;
import net.xy.jcms.controller.usecase.IController;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.shared.types.StringList;
import net.xy.jcms.shared.types.StringMap;

public class StaticsInclusionController implements IController {

    private static final Pattern instruction = Pattern.compile("^include:([a-zA-Z0-9]*)$");

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
        proccess(configK, configC);
        return null;
    }

    /**
     * proccesses the instructionset
     * 
     * @param configK
     * @param configC
     */
    private static void proccess(final ControllerConfiguration configK, final ContentRepository configC) {
        final Map<String, String> ownC = configK.getControllerConfig(StaticsInclusionController.class);
        // check for instructions
        final List<String> binVars = new ArrayList<String>();
        for (final String key : ownC.keySet()) {
            final Matcher match = instruction.matcher(key);
            if (match.matches()) {
                binVars.add(match.group(1));
            }
        }
        proccessBinVars(binVars, ownC, configC);
    }

    /**
     * proccesses each single binvar and fills the content
     * 
     * @param binVars
     * @param ownC
     * @param configC
     */
    private static void proccessBinVars(final List<String> binVars, final Map<String, String> ownC,
            final ContentRepository configC) {
        for (final String binVar : binVars) {
            // get domain in case of one
            final String domain = ownC.containsKey(binVar + "." + "domain") ? ownC.get(binVar + "." + "domain") : ownC
                    .get("domain");

            // get path prefix
            final String prefix = domain
                    + (ownC.containsKey(binVar + "." + "prefix") ? ownC.get(binVar + "." + "prefix") : ownC
                            .get("prefix"));
            // get type
            final String type = (ownC.containsKey(binVar + "." + "type") ? ownC.get(binVar + "." + "type") : ownC
                    .get("type")).trim();

            Object finalContent = null;
            // get content in right object
            if ("StringList".equalsIgnoreCase(type)) {
                // get url list
                final StringList contentList = new StringList(ownC.get(binVar + "." + "content"));
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
                finalContent = new StringMap(ownC.get(binVar + "." + "content"));
            } else if ("String".equalsIgnoreCase(type)) {
                finalContent = prefix + ownC.get(binVar + "." + "content");
            }

            // put in targets
            if (finalContent != null) {
                for (final String target : new StringList(ownC.get(binVar + "." + "target"))) {
                    configC.putContent(target, finalContent);
                }
            }
        }
    }
}
