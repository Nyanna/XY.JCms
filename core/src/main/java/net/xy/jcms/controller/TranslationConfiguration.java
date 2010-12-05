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
package net.xy.jcms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IConverter;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.StringMap;

/**
 * describes the configuration used to translate path's in Keys
 * 
 * @author xyan
 * 
 */
public abstract class TranslationConfiguration {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(TranslationConfiguration.class);

    /**
     * describes an path translation rule to convert human readable path's in an
     * semantic navigation key. Rule is immutable.
     * 
     * @author xyan
     * 
     */
    final public static class TranslationRule {
        /**
         * holds the pattern
         */
        private final Pattern reactOn;

        /**
         * holds the path build pattern for backtranslation
         */
        private final String buildOff;

        /**
         * holds the usecase for which the rule is applied
         */
        private final String usecase;

        /**
         * holds tha parameter list describing the parameter transformation
         */
        private final List<RuleParameter> parameters;

        /**
         * default constructor
         * 
         * @param reacton
         * @param level
         * @param usecase
         * @param parameters
         */
        public TranslationRule(final String reactOn, final String buildOff, final String usecase,
                final List<RuleParameter> parameters) {
            this.reactOn = Pattern.compile(reactOn);
            if (!this.reactOn.matcher(buildOff).matches()) {
                throw new IllegalArgumentException(
                        "BuildOff rules doesn't match on reactsOn! Check Translation rule configuration. "
                                + DebugUtils.printFields(reactOn, buildOff));
            }
            this.buildOff = buildOff;
            this.usecase = usecase;
            if (parameters == null) {
                this.parameters = new ArrayList<RuleParameter>();
            } else {
                this.parameters = parameters;
            }
        }

        /**
         * returns the pattern
         * 
         * @return value
         */
        public Pattern getReacton() {
            return reactOn;
        }

        /**
         * returns the usecaseid
         * 
         * @return value
         */
        public String getUsecase() {
            return usecase;
        }

        /**
         * returns the parameter transformationrule list
         * 
         * @return value
         */
        public List<RuleParameter> getParameters() {
            return parameters;
        }

        /**
         * gets the reactOn matching build pattern
         * 
         * @return value
         */
        public String getBuildOff() {
            return buildOff;
        }
    }

    /**
     * specifies an parameter to which regexp group it belongs and the
     * typeconverter used.
     * 
     * @author Xyan
     * 
     */
    final public static class RuleParameter {
        /**
         * holds the name of the parameter applied for
         */
        private final String parameterName;

        /**
         * if an regexp substitution is needed stores the pattern subgroup which
         * will be transformed to an parameter
         */
        private final Integer aplicatesToGroup;

        /**
         * hold the typeconverter which converts the string value to an
         * programatic type
         */
        private final IConverter converter;

        /**
         * default constructor
         * 
         * @param parameterName
         * @param aplicatesToGroup
         * @param converter
         */
        public RuleParameter(final String parameterName, final int aplicatesToGroup, final IConverter converter) {
            if (StringUtils.isBlank(parameterName)) {
                throw new IllegalArgumentException("Parameter name can't be blank.");
            }
            this.parameterName = parameterName;
            this.aplicatesToGroup = aplicatesToGroup;
            this.converter = converter;
            // TODO [LOW] implement usecase params without regexp
        }

        /**
         * returns the parameters name which should be set
         * 
         * @return value
         */
        public String getParameterName() {
            return parameterName;
        }

        /**
         * returns the substitution group or null
         * 
         * @return value
         */
        public Integer getAplicatesToGroup() {
            return aplicatesToGroup;
        }

        /**
         * returns the type converter or null
         * 
         * @return value
         */
        public IConverter getConverter() {
            return converter;
        }
    }

    /**
     * uses the key to find an translationrule and builds an path
     * 
     * @param key
     * @return value
     */
    public static String find(final NALKey key, final IDataAccessContext dac) {
        if (key != null) {
            final TranslationRule rule = findRuleForKey(key, dac);
            if (rule != null) {
                try {
                    return translateKeyWithRule(key, rule);
                } catch (final GroupCouldNotBeFilled e) {
                    LOG.error(e);
                    return null;
                } catch (final InvalidBuildRule e) {
                    LOG.error(e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * generates an path with an rule out from an key. protected for unit
     * testing
     * 
     * @param key
     * @param rule
     * @return the ready translated path elsewhere it rises an
     *         GroupCouldNotBeFilled
     * @throws GroupCouldNotBeFilled
     *             in case parameter replacement failures
     * @throws InvalidBuildRule
     *             in case the buildrule can't be applied
     */
    protected static String translateKeyWithRule(final NALKey key, final TranslationRule rule)
            throws GroupCouldNotBeFilled,
            InvalidBuildRule {
        String buildKey = rule.getBuildOff();
        Matcher matcher = rule.getReacton().matcher(buildKey);
        final List<Integer> alreadyFilled = new ArrayList<Integer>();
        if (matcher.matches()) {
            // check if buildrule is matching the reacton rule
            for (final RuleParameter ruleParam : rule.getParameters()) {
                // for each defined parameter in the rule check if it is
                // obmitted

                // type reversion
                final String paramFromKey = convertType2String(key.getParameter(ruleParam.getParameterName()),
                        ruleParam.getConverter());
                if (paramFromKey != null) {
                    final int group = ruleParam.getAplicatesToGroup();
                    try {
                        if (!alreadyFilled.contains(new Integer(group))) {
                            // group not already filled, then fills with param
                            // and reevaluate
                            buildKey = new StringBuilder(buildKey.substring(0, matcher.start(group)))
                                    .append(paramFromKey).append(buildKey.substring(matcher.end(group)))
                                    .toString();
                            // reevaluate the actual replacement and check if
                            // the replaced param still triggers an
                            // reacton
                            matcher = rule.getReacton().matcher(buildKey);
                            if (!matcher.matches()) {
                                // refilled param has an invalid param it will
                                // not more recognized
                                throw new GroupCouldNotBeFilled(
                                        "an refilled parameter contains an invalid value and get not more recognized");
                            }
                            // don't process an group twice
                            alreadyFilled.add(group);
                        }
                    } catch (final IndexOutOfBoundsException e) {
                        // in case group is not defined or found
                        LOG.error(e);
                        throw new GroupCouldNotBeFilled(
                                "An mendatory group could not be filled with parameters from key, rule describes an invalid group or group was not found in buildKey");
                    }
                } else {
                    // param is not set in key
                    throw new GroupCouldNotBeFilled(
                            "An mendatory group could not be filled with parameters from key, param doesn't exist in key");
                }
                // proceed to next parameter
            }
            // after all parameters are properly filled and replaced
            return buildKey;
        }
        throw new InvalidBuildRule();
    }

    /**
     * error handling, simple exception marker
     * 
     * @author xyan
     * 
     */
    public static class GroupCouldNotBeFilled extends Exception {
        private static final long serialVersionUID = 203300011416376084L;

        public GroupCouldNotBeFilled(final String string) {
            super(string);
        }
    }

    /**
     * error handling, simple exception marker
     * 
     * @author xyan
     * 
     */
    public static class InvalidBuildRule extends Exception {
        private static final long serialVersionUID = -6749410543654961955L;
    }

    /**
     * finds the to an key corresponding rule
     * 
     * @param struct
     * @return null or the rule
     */
    protected static TranslationRule findRuleForKey(final NALKey struct, final IDataAccessContext dac) {
        if (struct == null) {
            return null;
        }
        final TranslationRule[] byIdSelected = getRulesById(struct.getId(), getRuleList(dac));
        if (byIdSelected.length > 1) {
            return findMostMatchingParams(byIdSelected, struct);
        } else if (byIdSelected.length == 1) {
            return byIdSelected[0];
        } else {
            return null;
        }
    }

    /**
     * filters the rules for an given id
     * 
     * @param id
     * @param list
     * @return value
     */
    private static TranslationRule[] getRulesById(final String id, final TranslationRule[] list) {
        final List<TranslationRule> retList = new ArrayList<TranslationRule>();
        for (final TranslationRule rule : list) {
            if (rule.getUsecase().equalsIgnoreCase(id)) {
                retList.add(rule);
            }
        }
        return retList.toArray(new TranslationRule[retList.size()]);
    }

    /**
     * compares the params of an key against the rules and returns the most
     * matching one
     * 
     * @param list
     * @param struct
     * @return value
     */
    private static TranslationRule findMostMatchingParams(final TranslationRule[] list, final NALKey struct) {
        TranslationRule foundRule = null;
        int reachedMatches = -1;
        // for each usecase
        for (final TranslationRule rule : list) {
            final int matches = countMatchingParams(rule.getParameters(), struct);
            if (matches > reachedMatches) {
                reachedMatches = matches;
                foundRule = rule;
            }
        }
        return foundRule;
    }

    /**
     * counts to an key matching rule parameters
     * 
     * @param ruleParameters
     * @param struct
     * @return value
     */
    private static int countMatchingParams(final List<RuleParameter> ruleParameters, final NALKey struct) {
        int counter = 0;
        for (final Entry<Object, Object> param : struct.getParameters().entrySet()) {
            for (final RuleParameter ruleParam : ruleParameters) {
                if (ruleParam.getParameterName().equals(param.getKey())) {
                    counter++;
                }
            }
        }
        return counter;
    }

    /**
     * creates an key based on the translation configuration
     * 
     * @param level
     *            navigation level
     * @param path
     * @return value
     */
    public static NALKey find(final String path, final IDataAccessContext dac) {
        final NALKey key = null;
        for (final TranslationRule rule : getRuleList(dac)) {
            final Matcher match = rule.getReacton().matcher(path);
            if (match.find()) {
                return createKey(rule, match);
            }
        }
        return key;
    }

    /**
     * creates an key for the found rule
     * 
     * @param rule
     * @param matcher
     * @return value
     */
    private static NALKey createKey(final TranslationRule rule, final Matcher matcher) {
        final NALKey key = new NALKey(rule.getUsecase());
        for (final RuleParameter parameterRule : rule.getParameters()) {
            final String paramValue = matcher.group(parameterRule.getAplicatesToGroup());
            final IConverter type = parameterRule.getConverter();
            key.addParameter(parameterRule.getParameterName(),
                    convertParam2Type(paramValue, type));
        }
        return key;
    }

    /**
     * helper which converts an string with an type specifier to an object maybe
     * with the usage of an mapping
     * 
     * @param paramValue
     * @param type
     * @param simpleMapping
     * @return never null
     */
    private static Object convertParam2Type(final String paramValue, final IConverter type) {
        Object converted = null; // aka final
        if (type instanceof StringMap) {
            final StringMap strMap = (StringMap) type;
            if (strMap.containsValue(paramValue)) {
                for (final Entry<String, String> entry : strMap.entrySet()) {
                    if (entry.getValue().equals(paramValue)) {
                        converted = entry.getKey();
                        break;
                    }
                }
            } else {
                converted = paramValue;
            }
        } else {
            converted = type.convert(paramValue);
        }
        return converted;
    }

    /**
     * reverse operation as convertParam2Type
     * 
     * @param paramType
     * @param type
     * @param simpleMapping
     * @return is null when paramType is null
     */
    private static String convertType2String(final Object paramType, final IConverter type) {
        if (paramType == null) {
            return null;
        }
        final String converted;
        if (type instanceof StringMap) {
            final StringMap strMap = (StringMap) type;
            if (strMap.containsKey(paramType)) {
                converted = strMap.get(paramType);
            } else {
                converted = paramType.toString();
            }
        } else {
            converted = paramType.toString();
        }
        return converted;
    }

    /**
     * gets rulelist from adapter
     * 
     * @param level
     * @return value
     */
    protected static TranslationRule[] getRuleList(final IDataAccessContext dac) {
        if (adapter == null) {
            throw new IllegalArgumentException("Translation configuration adapter was not injected");
        }
        final TranslationRule[] list = adapter.getRuleList(dac);
        if (list == null) {
            throw new IllegalArgumentException("Rulelist can't be retrieved.");
        }
        return list;
    }

    /**
     * hold the configuration retrieval adapter
     */
    private static ITranslationConfigurationAdapter adapter;

    /**
     * injects the translation configuration adapter
     * 
     * @param adapter
     */
    public static void setTranslationAdapter(final ITranslationConfigurationAdapter adapter) {
        TranslationConfiguration.adapter = adapter;
    }
}