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
package net.xy.jcms.controller.configurations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IComponent;
import net.xy.jcms.shared.IOutWriter;
import net.xy.jcms.shared.IRenderer;

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
     * holds the mendatory component id. On every added component id got set on
     * every fragment not.
     */
    private String id = "";

    /**
     * holds an id stacked component path. On every added component path got set
     * on every fragment not.
     */
    private String componentPath = "";

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
    public ComponentConfiguration(final IComponent compInstance) {
        if (compInstance == null) {
            throw new IllegalArgumentException(
                    "Can't instantiate an component configuration without an appropriated component instance");
        }
        this.compInstance = compInstance;
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
        cmpConfig.initConfiguration(repository);
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
     * initializes tree rendering
     * 
     * @param out
     * @param cmpConfig
     */
    public static void render(final IOutWriter out, final ComponentConfiguration cmpConfig) {
        cmpConfig.getCompInstance().render(out, cmpConfig);
    }

    /**
     * get the component path
     * 
     * @return value
     */
    final protected String getComponentPath() {
        return componentPath;
    }

    /**
     * returns the components id
     * 
     * @return value
     */
    final public String getId() {
        return id;
    }

    /**
     * private setter for internally setting id
     * 
     * @param id
     */
    private void setId(final String id) {
        this.id = id;
        updateComponentPath();
    }

    /**
     * returns the component instance
     * 
     * @return value
     */
    private IComponent getCompInstance() {
        return compInstance;
    }

    /**
     * get the parent
     * 
     * @return value
     */
    final protected ComponentConfiguration getParent() {
        return parent;
    }

    /**
     * set the parent, an an new component path
     * 
     * @param parent
     */
    final private void setParent(final ComponentConfiguration parent) {
        this.parent = parent;
        updateComponentPath();
    }

    /**
     * triggers an component path update
     */
    final protected void updateComponentPath() {
        if (parent != null) {
            final StringBuilder key = new StringBuilder(parent.getComponentPath());
            if (StringUtils.isNotEmpty(key.toString())) {
                key.append(COMPONENT_PATH_SEPARATOR);
            }
            key.append(id);
            componentPath = key.toString();
        } else {
            componentPath = id;
        }
        // don't forget to trigger child component path to be reseted
        for (final ComponentConfiguration child : getChildren().values()) {
            child.setParent(this);
        }
        for (final FragmentConfiguration fragment : getTemplates().values()) {
            fragment.updateComponentPath();
        }
    }

    /**
     * ####################
     * 
     * Child management
     * 
     * ####################
     */

    /**
     * holds the references to the children
     */
    private final Map<String, ComponentConfiguration> children = new HashMap<String, ComponentConfiguration>();

    /**
     * gets all childrens
     * 
     * @return value
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
    final protected void addChildren(final String id, final ComponentConfiguration child) {
        child.setParent(this);
        child.setId(id);
        children.put(id, child);
    }

    /**
     * adds an component as child and returns it configuration object
     * 
     * @param id
     * @param component
     * @return value
     */
    public ComponentConfiguration addComponent(final String id, final IComponent component) {
        final ComponentConfiguration config = component.getConfiguration();
        addChildren(id, config);
        return config;
    }

    /**
     * init child retrieval proccess recursively
     */
    private void getChildConfiguration(final ContentRepository repository) {
        final ComponentConfiguration[] childs = prepareChildren(repository);
        if (childs != null) {
            for (final ComponentConfiguration child : childs) {
                child.getId();
                // TODO [LOW] remove or change to mapp
                // addChildren(id, child);
            }
        }
    }

    /**
     * prepare and collect the child configuration
     * 
     * @return value
     */
    protected abstract ComponentConfiguration[] prepareChildren(final ContentRepository repository);

    /**
     * get an single child configuration
     * 
     * @param id
     * @return value
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
    public void renderChild(final String id, final IOutWriter out) {
        final ComponentConfiguration child = getChild(id);
        renderChild(child, out);
    }

    /**
     * private delegate method
     * 
     * @param child
     * @param out
     */
    private void renderChild(final ComponentConfiguration child, final IOutWriter out) {
        child.getCompInstance().render(out, child);
    }

    /**
     * renders all childs at once
     * 
     * @param out
     */
    public void renderChilds(final IOutWriter out) {
        for (final ComponentConfiguration child : getChildren().values()) {
            renderChild(child, out);
        }
    }

    /**
     * ####################
     * 
     * Messages configuration proccessing
     * 
     * ####################
     */
    private final Map<String, String> messages = new HashMap<String, String>();

    private void initConfiguration(final MessageConfiguration config) {
        final String[] prepare = prepareMessages();
        if (prepare != null) {
            for (final String key : prepare) {
                if (!messages.containsKey(key)) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered message configuration was missing");
                    }
                    messages.put(key, config.getMessage(key, this));
                }
            }
        }
    }

    protected abstract String[] prepareMessages();

    public String getMessage(final String key) {
        final String message = messages.get(key);
        if (message == null) {
            throw new IllegalArgumentException("An not configured message key were requested! "
                    + DebugUtils.printFields(key));
        }
        return message;
    }

    /**
     * ####################
     * 
     * UIConfiguration processing
     * 
     * ####################
     */
    private final Map<String, Object> uiconfig = new HashMap<String, Object>();

    private void initConfiguration(final UIConfiguration config) {
        final UI<?>[] prepare = prepareUIConfig();
        if (prepare != null) {
            for (final UI<?> ui : prepare) {
                if (!uiconfig.containsKey(ui.getKey())) {
                    if (config == null) {
                        if (ui.getDefaultValue() != null) {
                            uiconfig.put(ui.getKey(), ui.getDefaultValue());
                            continue;
                        }
                        throw new IllegalArgumentException("An requiered configuration was missing. "
                                + DebugUtils.printFields(ui));
                    }
                    uiconfig.put(ui.getKey(), config.getConfig(ui, this));
                    // TODO [LOW] add type safty through separate get boolean
                }
            }
        }
    }

    protected abstract UI<?>[] prepareUIConfig();

    public Object getUIConfig(final String key) {
        final Object config = uiconfig.get(key);
        if (config == null) {
            throw new IllegalArgumentException("An not configured ui configuration were requested! "
                    + DebugUtils.printFields(key));
        }
        return config;
    }

    /**
     * method to configure childs ui config aggregation. Only usefull in the
     * aggregation phase.
     * 
     * @param key
     * @param value
     */
    public void setUIConfig(final String key, final Object value) {
        uiconfig.put(key, value);
    }

    /**
     * ####################
     * 
     * Renderconfiguration proccessing
     * 
     * ####################
     */
    private final Map<Class<? extends IRenderer>, IRenderer> renderers = new HashMap<Class<? extends IRenderer>, IRenderer>();

    private void initConfiguration(final RenderKitConfiguration config) {
        final Class<? extends IRenderer>[] prepare = prepareRenderers();
        if (prepare != null) {
            for (final Class<? extends IRenderer> iface : prepare) {
                if (!renderers.containsKey(iface)) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered renderer was missing. "
                                + DebugUtils.printFields(iface));
                    }
                    renderers.put(iface, config.get(iface, this));
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
     * ####################
     * 
     * Template configuration proccessing
     * 
     * ####################
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
                    final FragmentConfiguration tmplConfig = config.get(name, this).getConfiguration();
                    ((ComponentConfiguration) tmplConfig).setId(name);
                    ((ComponentConfiguration) tmplConfig).setParent(this);
                    templates.put(name, tmplConfig);
                }
            }
        }
    }

    protected abstract String[] prepareTemplates(final ContentRepository repository);

    private FragmentConfiguration getTemplate(final String name) {
        final FragmentConfiguration tmpl = templates.get(name);
        if (tmpl == null) {
            throw new IllegalArgumentException("An not configured template requested!");
        }
        return tmpl;
    }

    /**
     * renders an prior retrieved template with its config.
     * 
     * @param name
     * @param out
     */
    public void renderTemplate(final String name, final IOutWriter out) {
        if (StringUtils.isNotBlank(name)) {
            getTemplate(name).render(out);
        }
    }

    /**
     * return the requested template list
     * 
     * @return value
     */
    private Map<String, FragmentConfiguration> getTemplates() {
        return templates;
    }

    /**
     * ####################
     * 
     * content repositoryconfiguration
     * 
     * ####################
     */
    private final Map<String, Object> content = new HashMap<String, Object>();

    /**
     * initializes content preparation
     * 
     * @param config
     */
    private void initConfiguration(final ContentRepository config) {
        final Map<String, Class<?>> prepare = prepareContent();
        if (prepare != null) {
            for (final Entry<String, Class<?>> entry : prepare.entrySet()) {
                if (!content.containsKey(entry.getKey())) {
                    if (config == null) {
                        throw new IllegalArgumentException("An requiered content object was missing");
                    }
                    final Object got = config.getContent(entry.getKey(), entry.getValue(), this);
                    content.put(entry.getKey(), got);
                }
            }
        }
    }

    protected abstract Map<String, Class<?>> prepareContent();

    /**
     * returns an prepared content object
     * 
     * @param key
     * @return value
     */
    public Object getContent(final String key) {
        final Object contentObj = content.get(key);
        if (contentObj == null) {
            throw new IllegalArgumentException("An not cprepared content object were requested!");
        }
        return contentObj;
    }

}
