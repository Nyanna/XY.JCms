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
package net.xy.jcms.shared.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.FragmentConfiguration;
import net.xy.jcms.shared.AbstractFragment;
import net.xy.jcms.shared.IComponent;
import net.xy.jcms.shared.IOutWriter;

/**
 * an dynamic constructed fragment class. !Beware! never save component
 * configurations outside of the component configuration tree. this fragment got
 * staticly cached.
 * 
 * @author Xyan
 * 
 */
public class DynamicFragment extends AbstractFragment {

    /**
     * defines the behavior to process the element
     * 
     * @author Xyan
     * 
     */
    private static enum ElementType {
        Template, Static, Child;
    }

    /**
     * element container
     * 
     * @author Xyan
     * 
     */
    private static class Element {
        /**
         * stores the type
         */
        final ElementType type;

        /**
         * stores the value
         */
        final String value;

        /**
         * only constructor
         * 
         * @param type
         * @param value
         */
        public Element(final ElementType type, final String value) {
            if (type == null || value == null) {
                throw new IllegalArgumentException("Field can't be null.");
            }
            this.type = type;
            this.value = value;
        }

        /**
         * gets the type
         * 
         * @return
         */
        public ElementType getType() {
            return type;
        }

        /**
         * gets the value
         * 
         * @return
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * holds an ordered list of all the fragment parts, static, childs,
     * templates, this is also the rendering queue
     */
    private final LinkedList<Element> struct = new LinkedList<Element>();

    /**
     * children got also separately saved in this list
     */
    private final Map<String, IComponent> children = new HashMap<String, IComponent>();

    /**
     * sub fragments gets also stored here
     */
    private final List<String> fragments = new ArrayList<String>();

    @Override
    public FragmentConfiguration getConfiguration() {
        return new FragmentConfiguration(this) {

            @Override
            protected ComponentConfiguration[] prepareChildren(final Map<String, Object> content) {
                for (final Entry<String, IComponent> entry : children.entrySet()) {
                    addChildren(entry.getKey(), entry.getValue().getConfiguration());
                }
                return null;
            }

            @Override
            protected String[] prepareTemplates(final Map<String, Object> content) {
                return fragments.toArray(new String[fragments.size()]);
            }

            @Override
            protected Map<String, Class<?>> prepareContent() {
                return null;
            }
        };
    }

    @Override
    public void render(final IOutWriter out, final FragmentConfiguration config) {
        // beware of mutable operation in an pseudo static fragment instance
        for (final Element elem : struct) {
            switch (elem.getType()) {
            case Child:
                config.renderChild(elem.getValue(), out);
                break;
            case Static:
                out.append(elem.getValue());
                break;
            case Template:
                config.renderTemplate(elem.getValue(), out);
                break;
            }
        }
    }

    /**
     * adds an child component to the fragments stack
     * 
     * @param name
     * @param compInstance
     */
    public void addChild(final String name, final IComponent compInstance) {
        children.put(name, compInstance);
        struct.add(new Element(ElementType.Child, name));
    }

    /**
     * adds an fragment/template slot to this fragments stack
     * 
     * @param name
     * @param compInstance
     */
    public void addFragment(final String name) {
        fragments.add(name);
        struct.add(new Element(ElementType.Template, name));
    }

    /**
     * adds static charackter to this fragments stack
     * 
     * @param charachters
     */
    public void addStatic(final String charachters) {
        if (StringUtils.isNotBlank(charachters)) {
            struct.add(new Element(ElementType.Static, charachters));
        }
    }
}
