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
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.portal.controller.ControllerConfiguration.Config;
import net.xy.jcms.portal.controller.ControllerConfiguration.Item;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.Model;
import net.xy.jcms.shared.types.StringList;
import net.xy.jcms.shared.types.StringMap;

/**
 * abstract handler for including images,css,js mainly static resources
 * 
 * @author xyan
 * 
 */
abstract public class StaticsInclusion extends Controller {

    /**
     * instruction section in config
     */
    private static final String INSTRUCTION_SECTION = "include";

    /**
     * configuration item usable in section and as global
     */
    protected static Item<String> DOMAIN = new Item<String>("domain", "Domain which got prefixed.", null);

    /**
     * configuration item usable in section and as global
     */
    protected static Item<String> PREFIX = new Item<String>("prefix", "Additional path prefix", null);

    /**
     * configuration item usable in section and as global
     */
    protected static Item<String> TYPE = new Item<String>("type", "Type on which processing decidement is taken", null);

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Model configuration, final Config config) {
        final ContentRepository configC = (ContentRepository) configuration.get(ContentRepository.TYPE);
        if (configC == null) {
            throw new IllegalArgumentException("Missing configurations");
        }
        proccess(config, configC, configuration, dac);
        return null;
    }

    /**
     * proccesses the instructionset
     * 
     * @param configK
     * @param configC
     */
    @SuppressWarnings("unchecked")
    private void proccess(final Config config, final ContentRepository configC, final Model configuration,
            final IDataAccessContext dac) {
        final Map<String, Object> aggregatedContent = new HashMap<String, Object>();
        if (config.getGlobal(INSTRUCTION_SECTION) instanceof List) {
            for (final Map<Object, String> instruction : (List<Map<Object, String>>) config
                    .getGlobal(INSTRUCTION_SECTION)) {
                // get domain in case of one exists
                final String domain = config.get(DOMAIN, instruction);
                // get path prefix
                final String prefix;
                if (domain != null) {
                    prefix = domain + config.get(PREFIX, instruction);
                } else {
                    prefix = config.get(PREFIX, instruction);
                }
                // get type
                final String type = config.get(TYPE, instruction);

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
                        finalContent = processType(type, instruction, config, prefix, dac);
                    }
                } else {
                    finalContent = firstContent;
                }

                // put in targets
                if (finalContent != null) {
                    for (final String target : new StringList(instruction.get("target"))) {
                        aggregatedContent.put(target, finalContent);
                    }
                }
            }
        }
        configuration.put(ContentRepository.TYPE, configC.mergeConfiguration(aggregatedContent));
    }

    /**
     * processes one to the StaticInclusion unknown portal dependent type
     * 
     * @param type
     *            an string representing the type like StringMap, FlashTeaser
     * @param instruction
     * @param config
     * @param prefix
     *            path prefix, can be null
     * @param domain
     *            an domain to set, can be null
     * @return
     */
    protected abstract Object processType(final String type, final Map<Object, String> instruction,
            final Config config, final String prefix, final IDataAccessContext dac);

}
