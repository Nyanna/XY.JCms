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
package net.xy.jcms.shared;

import java.util.HashMap;
import java.util.Map;

import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.configurations.RenderKitConfiguration;
import net.xy.jcms.controller.configurations.TemplateConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration.UI;

/**
 * an basic component configuration object containg all the main functionality
 * 
 * @author Xyan
 * 
 */
public abstract class ComponentConfiguration {
    /**
     * separates individual component path elements
     */
    public final static String COMPONENT_PATH_SEPARATOR = ".";

    /**
     * holds the mendatory component id
     */
    private final String id;

    /**
     * holds an id stacked component path
     */
    private String componentPath;

    /**
     * holds the reference to the parent
     */
    private ComponentConfiguration parent = null;

    /**
     * holds the appropriated components instance, which requests and renders
     * these configuration
     */
    private final IComponent compInstance;

    /**
     * constructor
     * 
     * @param id
     *            of this component
     * @param parent
     *            the parent configuration object
     */
    public ComponentConfiguration(final String id, final IComponent compInstance, final ComponentConfiguration parent) {
        if (compInstance == null) {
            throw new IllegalArgumentException(
                    "Can't instantiate an component configuration without an appropriated component instance");
        }
        this.id = id;
        this.compInstance = compInstance;
        setParent(parent);
        if (parent != null) {
            componentPath = parent.getComponentPath() + COMPONENT_PATH_SEPARATOR + id;
            parent.addChildren(this);
        } else {
            componentPath = id;
        }
    }

    /**
     * initializes the complete component configuration in case of an
     * missconfiguration it throws an exception
     * 
     * @param cmpConfig
     *            ComponentConfiguration to be initialized
     * @param tmplConf
     *            TemplateConfiguration
     * @param uiConf
     *            UIConfiguration
     * @param messConf
     *            MessageConfiguration
     * @param renderConf
     *            RenderKitConfiguration
     */
    public static void initialize(final ComponentConfiguration cmpConfig, final ContentRepository repository,
            final TemplateConfiguration tmplConf, final UIConfiguration uiConf, final MessageConfiguration messConf,
            final RenderKitConfiguration renderConf) {
        cmpConfig.initConfiguration(tmplConf, repository);
        cmpConfig.initConfiguration(uiConf);
        cmpConfig.initConfiguration(messConf);
        cmpConfig.initConfiguration(renderConf);
        cmpConfig.getChildConfiguration(repository);
        for (final ComponentConfiguration child : cmpConfig.getChildren().values()) {
            ComponentConfiguration.initialize(child, repository, tmplConf, uiConf, messConf, renderConf);
        }
        for (final FragmentConfiguration fragment : cmpConfig.getTemplates().values()) {
            ComponentConfiguration.initialize(fragment, repository, tmplConf, uiConf, messConf, renderConf);
        }
    }

    /**
     * get the component path
     * 
     * @return
     */
    final public String getComponentPath() {
        return componentPath;
    }

    /**
     * returns the components id
     * 
     * @return
     */
    final public String getId() {
        return id;
    }

    /**
     * returns the component instance
     * 
     * @return
     */
    private IComponent getCompInstance() {
        return compInstance;
    }

    /**
     * get the parent
     * 
     * @return
     */
    final public ComponentConfiguration getParent() {
        return parent;
    }

    /**
     * set the parent, an an new component path
     * 
     * @param parent
     */
    final private void setParent(final ComponentConfiguration parent) {
        this.parent = parent;
        if (parent != null) {
            componentPath = parent.getComponentPath() + COMPONENT_PATH_SEPARATOR + id;
        } else {
            componentPath = id;
        }
        // don't forget to trigger child component path to be reseted
        for (final ComponentConfiguration child : getChildren().values()) {
            child.setParent(this);
        }
    }

    /**
     * 
     * Child management
     * 
     */

    /**
     * holds the references to the children
     */
    private final Map<String, ComponentConfiguration> children = new HashMap<String, ComponentConfiguration>();

    /**
     * gets all childrens
     * 
     * @return
     */
    private Map<String, ComponentConfiguration> getChildren() {
        return children;
    }

    /**
     * add an children
     * 
     * @param id
     * @param config
     */
    final protected void addChildren(final ComponentConfiguration child) {
        child.setParent(this);
        children.put(child.getId(), child);
    }

    /**
     * adds an component as child and returns it configuration object
     * 
     * @param id
     * @param component
     * @return
     */
    public ComponentConfiguration addComponent(final String id, final IComponent component) {
        final ComponentConfiguration config = component.getConfiguration(id, this);
        addChildren(config);
        return config;
    }

    /**
     * init child retrieval proccess recursively
     */
    private void getChildConfiguration(final ContentRepository repository) {
        final ComponentConfiguration[] childs = prepareChildren(repository);
        if (childs != null) {
            for (final ComponentConfiguration child : childs) {
                addChildren(child);
            }
        }
    }

    /**
     * prepare and collect the child configuration
     * 
     * @return
     */
    protected abstract ComponentConfiguration[] prepareChildren(final ContentRepository repository);

    /**
     * get an single child configuration
     * 
     * @param id
     * @return
     */
    private ComponentConfiguration getChild(final String id) {
        final ComponentConfiguration child = children.get(id);
        if (child == null) {
            throw new IllegalArgumentException("An not configured child component were requested!");
        }
        return child;
    }

    /**
     * render one child
     * 
     * @param id
     * @param out
     */
    public void renderChild(final String id, final OutWriterImplementationAdapter out) {
        final ComponentConfiguration child = getChild(id);
        renderChild(child, out);
    }

    /**
     * private delegate method
     * 
     * @param child
     * @param out
     */
    private void renderChild(final ComponentConfiguration child, final OutWriterImplementationAdapter out) {
        child.getCompInstance().render(out, child);
    }

    /**
     * renders all childs at once
     * 
     * @param out
     */
    public void renderChilds(final OutWriterImplementationAdapter out) {
        for (final ComponentConfiguration child : getChildren().values()) {
            renderChild(child, out);
        }
    }

    /**
     * Messages configuration proccessing
     */
    private final Map<String, String> messages = new HashMap<String, String>();

    private void initConfiguration(final MessageConfiguration config) {
        final String[] prepare = prepareMessages();
        if (prepare != null) {
            for (final String key : prepare) {
                if (!messages.containsKey(key)) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered configuration was missing");
                    }
                    messages.put(key, config.getMessage(key));
                }
            }
        }
    }

    protected abstract String[] prepareMessages();

    public String getMessage(final String key) {
        final String message = messages.get(key);
        if (message == null) {
            throw new IllegalArgumentException("An not configured message key were requested!");
        }
        return message;
    }

    /**
     * UIConfiguration processing
     */
    private final Map<String, Object> uiconfig = new HashMap<String, Object>();

    @SuppressWarnings("rawtypes")
    private void initConfiguration(final UIConfiguration config) {
        final UI[] prepare = prepareUIConfig();
        if (prepare != null) {
            for (final UI ui : prepare) {
                if (!uiconfig.containsKey(ui.getKey())) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered configuration was missing");
                    }
                    uiconfig.put(ui.getKey(), config.getConfig(ui, this));
                    // TODO [LOW] add type safty through separate get boolean
                    // get
                    // integer
                    // methods
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected abstract UI[] prepareUIConfig();

    public Object getUIConfig(final String key) {
        final Object config = uiconfig.get(key);
        if (config == null) {
            throw new IllegalArgumentException("An not configured ui configuration were requested!");
        }
        return config;
    }

    /**
     * Renderconfiguration proccessing
     */
    private final Map<Class<? extends IRenderer>, IRenderer> renderers = new HashMap<Class<? extends IRenderer>, IRenderer>();

    private void initConfiguration(final RenderKitConfiguration config) {
        final Class<? extends IRenderer>[] prepare = prepareRenderers();
        if (prepare != null) {
            for (final Class<? extends IRenderer> iface : prepare) {
                if (!renderers.containsKey(iface)) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered configuration was missing");
                    }
                    renderers.put(iface, config.get(iface));
                }
            }
        }
    }

    protected abstract Class<? extends IRenderer>[] prepareRenderers();

    public Object getRenderer(final Class<? extends IRenderer> rIface) {
        final Object renderer = renderers.get(rIface);
        if (renderer == null) {
            throw new IllegalArgumentException("An not configured rendererwere requested!");
        }
        return renderer;
    }

    /**
     * Template configuration proccessing
     */
    private final Map<String, FragmentConfiguration> templates = new HashMap<String, FragmentConfiguration>();

    private void initConfiguration(final TemplateConfiguration config, final ContentRepository repository) {
        final String[] prepare = prepareTemplates(repository);
        if (prepare != null) {
            for (final String name : prepare) {
                if (!templates.containsKey(name)) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered configuration was missing");
                    }
                    templates.put(name, config.get(name).getConfiguration(name, this));
                }
            }
        }
    }

    protected abstract String[] prepareTemplates(final ContentRepository repository);

    public FragmentConfiguration getTemplate(final String name) {
        final FragmentConfiguration tmpl = templates.get(name);
        if (tmpl == null) {
            throw new IllegalArgumentException("An not configured template requested!");
        }
        return tmpl;
    }

    /**
     * return the requested template list
     * 
     * @return
     */
    private Map<String, FragmentConfiguration> getTemplates() {
        return templates;
    }
}
