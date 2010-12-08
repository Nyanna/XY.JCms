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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.controller.configurations.pool.ComponentPool;
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
     * separates individual component path elements, actually its not needed cuz of the iteration handling
     */
    public final static String COMPONENT_PATH_SEPARATOR = ".";

    /**
     * holds the mendatory component id. On every added component id got set on.
     */
    private String id = "";

    /**
     * holds an id stacked component path. On every added component path got set.
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
     * flag stores if the config were already initialized and if further configs
     * are not possible
     */
    private boolean ready = false;

    /**
     * only constructor
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
     * missconfiguration it throws an exception. recursively initializes children.
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
        cmpConfig.changeValid();
        cmpConfig.initConfiguration(repository);
        cmpConfig.initConfiguration(tmplConf);
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
        cmpConfig.setReady();
    }

    /**
     * only used by initialization routine finalizes the configuration
     */
    private void setReady() {
        ready = true;
    }

    /**
     * method checks if the config already is finalized and throws an exception
     */
    private void changeValid() {
        if (ready) {
            throw new IllegalStateException("Component already finalized no further changes possible");
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
        changeValid();
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
     * set the parent, and an new component path
     * 
     * @param parent
     */
    private void setParent(final ComponentConfiguration parent) {
        changeValid();
        this.parent = parent;
        updateComponentPath();
    }

    /**
     * triggers an component path update
     */
    final protected void updateComponentPath() {
        changeValid();
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
    private final Map<String, ComponentConfiguration> children = new LinkedHashMap<String, ComponentConfiguration>();

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
        changeValid();
        child.setParent(this);
        child.setId(id);
        children.put(id, child);
    }

    /**
     * add an anonymous child (hashCode).
     * 
     * @param child
     */
    final protected void addChildren(final ComponentConfiguration child) {
        changeValid();
        child.setParent(this);
        children.put(new Integer(child.hashCode()).toString(), child);
    }

    /**
     * adds an component as child and returns it configuration object
     * 
     * @param id
     * @param component
     * @return value
     */
    public ComponentConfiguration addComponent(final String id, final Class<? extends IComponent> component) {
        changeValid();
        IComponent compInst;
        try {
            compInst = ComponentPool.get(component);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Given class couldn't be loaded.", e);
        }
        final ComponentConfiguration config = compInst.getConfiguration();
        addChildren(id, config);
        return config;
    }

    /**
     * init child retrieval proccess recursively
     */
    private void getChildConfiguration(final ContentRepository repository) {
        final ComponentConfiguration[] childs = prepareChildren(Collections.unmodifiableMap(content));
        if (childs != null) {
            for (final ComponentConfiguration child : childs) {
                addChildren(child);
            }
        }
    }

    /**
     * returns un unmodifiable list of the childrens
     * 
     * @return unmodifiable value
     */
    public List<String> getChildList() {
        return Collections.unmodifiableList(new ArrayList<String>(children.keySet()));
    }

    /**
     * prepare and collect the child configuration. Add named children with
     * addComponent returned list will add anonymous childrens.
     * 
     * @return value
     */
    protected abstract ComponentConfiguration[] prepareChildren(final Map<String, Object> content);

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

    /**
     * gets an list of requested message keys. No message available which got not prepared.
     * 
     * @return
     */
    protected abstract String[] prepareMessages();

    /**
     * returns message or throws an exception
     * 
     * @param key
     * @return message string
     */
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
                }
            }
        }
    }

    /**
     * returns an list of prepared ui objects with its description and defaults..
     * 
     * @return
     */
    protected abstract UI<?>[] prepareUIConfig();

    /**
     * gets an ui config object via key
     * 
     * @param key
     * @return value
     */
    public Object getUIConfig(final String key) {
        final Object config = uiconfig.get(key);
        if (config == null) {
            throw new IllegalArgumentException("An not configured ui configuration were requested! "
                    + DebugUtils.printFields(key));
        }
        return config;
    }

    /**
     * simple castign wrapper for an ui config object
     * 
     * @param key
     * @return value
     */
    public String getUIConfigString(final String key) {
        return (String) getUIConfig(key);
    }

    /**
     * simple castign wrapper for an ui config object
     * 
     * @param key
     * @return value
     */
    public Integer getUIConfigInteger(final String key) {
        return (Integer) getUIConfig(key);
    }

    /**
     * simple castign wrapper for an ui config object
     * 
     * @param key
     * @return value
     */
    public Long getUIConfigLong(final String key) {
        return (Long) getUIConfig(key);
    }

    /**
     * simple castign wrapper for an ui config object
     * 
     * @param key
     * @return value
     */
    public Double getUIConfigDouble(final String key) {
        return (Double) getUIConfig(key);
    }

    /**
     * simple castign wrapper for an ui config object
     * 
     * @param key
     * @return value
     */
    public Boolean getUIConfigBoolean(final String key) {
        return (Boolean) getUIConfig(key);
    }

    /**
     * method to configure childs ui config aggregation. Only usefull in the
     * aggregation phase.
     * 
     * @param key
     * @param value
     */
    public void setUIConfig(final String key, final Object value) {
        changeValid();
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

    /**
     * gets an list of renderer by its interface class.
     * 
     * @return
     */
    protected abstract Class<? extends IRenderer>[] prepareRenderers();

    /**
     * returns an prepared renderer ot the requested type.
     * 
     * @param rIface
     * @return value
     */
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

    private void initConfiguration(final TemplateConfiguration config) {
        final String[] prepare = prepareTemplates(Collections.unmodifiableMap(content));
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

    /**
     * prepares template deffinitions by its name.
     * 
     * @param content
     * @return value
     */
    protected abstract String[] prepareTemplates(final Map<String, Object> content);

    /**
     * gets an prepared template
     * 
     * @param name
     * @return vlaue
     */
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
            ((ComponentConfiguration) getTemplate(name)).getCompInstance().render(out, getTemplate(name));
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

    /**
     * sets an content object to this child
     */
    public void setContent(final String key, final Object obj) {
        changeValid();
        content.put(key, obj);
    }

    /**
     * returns an list of requested content by its binding and the class it should be an instance of.
     * 
     * @return
     */
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
            throw new IllegalArgumentException("An not prepared content object were requested! "
                    + DebugUtils.printFields(key, componentPath, compInstance));
        }
        return contentObj;
    }
}
