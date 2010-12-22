package net.xy.jcms.portal.controller;

import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.TranslationConfiguration.GroupCouldNotBeFilled;
import net.xy.jcms.controller.TranslationConfiguration.InvalidBuildRule;
import net.xy.jcms.controller.configurations.ControllerConfiguration;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IController;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * abstract controller to share common code
 * 
 * @author Xyan
 * 
 */
abstract public class Controller implements IController {

    /**
     * gets an config either from the section or instruction config or from the
     * controller globals
     * 
     * @param key
     * @param globals
     *            an map of the global properties
     * @param section
     *            an map of the section properties
     * @return value can be null
     */
    protected static Object getPreciseOrGlobal(final Object key, final Map<String, Object> globals,
            final Map<Object, String> section) {
        final Object fromSection = section.get(key);
        if (fromSection != null) {
            return fromSection;
        }
        return globals.get(key);
    }

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
     * provides a way to get the final class's config object. gets also an way
     * to hook in the config
     * 
     * @param configK
     * @return
     */
    protected abstract Map<String, Object> getControllerConfig(final ControllerConfiguration configK);

    /**
     * helper method to get config
     * always returns an object and never null if the default was not null
     * 
     * @param indentifier
     * @param binding
     *            can be null
     * @param parameters
     *            can be null
     * @param def
     *            default if not found in configs
     * @return value
     */
    static public Object getConfig(final String indentifier, final Map<String, String> binding,
            final Map<Object, Object> parameters, final Object def) {
        Object ret;
        if (binding != null && binding.containsKey(indentifier)) {
            ret = binding.get(indentifier);
        } else if (parameters != null && parameters.containsKey(indentifier)) {
            ret = parameters.get(indentifier);
        } else {
            ret = def;
        }
        return ret;
    }
}
