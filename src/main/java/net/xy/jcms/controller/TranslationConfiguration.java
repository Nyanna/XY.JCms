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
package net.xy.jcms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.shared.dao.IDataAccessContext;

/**
 * describes the configuration used to translate pathes in KeyChains
 * 
 * @author xyan
 * 
 */
public abstract class TranslationConfiguration {

    /**
     * describes an path translation rule to convert human readable pathes in an
     * semantic navigation hierarchy
     * 
     * @author xyan
     * 
     */
    protected static class TranslationRule {
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
            this.buildOff = buildOff;
            this.usecase = usecase;
            this.parameters = parameters;
        }

        /**
         * returns the pattern
         * 
         * @return
         */
        public Pattern getReacton() {
            return reactOn;
        }

        /**
         * returns the usecaseid
         * 
         * @return
         */
        public String getUsecase() {
            return usecase;
        }

        /**
         * returns the parameter transformationrule list
         * 
         * @return
         */
        public List<RuleParameter> getParameters() {
            return parameters;
        }

        /**
         * gets the reactOn matching build pattern
         * 
         * @return
         */
        public String getBuildOff() {
            return buildOff;
        }
    }

    protected static class RuleParameter {
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
        private final String converter;

        /**
         * default constructor
         * 
         * @param parameterName
         * @param aplicatesToGroup
         * @param converter
         */
        public RuleParameter(final String parameterName, final int aplicatesToGroup, final String converter) {
            this.parameterName = parameterName;
            this.aplicatesToGroup = aplicatesToGroup;
            this.converter = converter;
        }

        /**
         * returns the parameters name which should be set
         * 
         * @return
         */
        public String getParameterName() {
            return parameterName;
        }

        /**
         * returns the substitution group
         * 
         * @return
         */
        public Integer getAplicatesToGroup() {
            return aplicatesToGroup;
        }

        /**
         * returns the type converter
         * 
         * @return
         */
        public String getConverter() {
            return converter;
        }
    }

    /**
     * uses the key to find an translationrule and builds an path
     * 
     * @param key
     * @return
     */
    public static String find(final NALKey key, final IDataAccessContext dac) {
        if (key != null) {
            final TranslationRule rule = findRuleForKey(key, dac);
            if (rule != null) {
                try {
                    return translateKeyWithRule(key, rule);
                } catch (final GroupCouldNotBeFilled e) {
                    return null;
                } catch (final InvalidBuildRule e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * generates an patch with an rule out from an key. protected for unit
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
    protected static String translateKeyWithRule(final NALKey key, final TranslationRule rule) throws GroupCouldNotBeFilled,
            InvalidBuildRule {
        String buildKey = rule.getBuildOff();
        Matcher matcher = rule.getReacton().matcher(buildKey);
        final List<Integer> alreadyFilled = new ArrayList<Integer>();
        if (matcher.matches()) {
            // check if buildrule is matching the reacton rule
            for (final RuleParameter ruleParam : rule.getParameters()) {
                // for each defined parameter in the rule check if it is
                // obmitted
                final Object paramFromKey = key.getParameter(ruleParam.getParameterName());
                if (paramFromKey != null) {
                    final int group = ruleParam.getAplicatesToGroup();
                    try {
                        if (!alreadyFilled.contains(new Integer(group))) {
                            // group not already filled, then fills with param
                            // and reevaluate
                            buildKey = new StringBuilder(buildKey.substring(0, matcher.start(group)))
                                    .append(paramFromKey.toString()).append(buildKey.substring(matcher.end(group)))
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
     * error handling
     * 
     * @author xyan
     * 
     */
    public static class GroupCouldNotBeFilled extends Exception {
        public GroupCouldNotBeFilled(final String string) {
            super(string);
        }
    }

    /**
     * error handling
     * 
     * @author xyan
     * 
     */
    public static class InvalidBuildRule extends Exception {
    }

    /**
     * finds the to an key corresponding rule
     * 
     * @param struct
     * @return null or the rule
     */
    public static TranslationRule findRuleForKey(final NALKey struct, final IDataAccessContext dac) {
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
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     * @return
     */
    private static NALKey createKey(final TranslationRule rule, final Matcher matcher) {
        final NALKey key = new NALKey(rule.getUsecase());
        for (final RuleParameter parameterRule : rule.getParameters()) {
            final String buildOff = matcher.group(parameterRule.getAplicatesToGroup());
            /**
             * TODO [LOW] here also take the typeconversion place
             */
            key.addParameter(parameterRule.getParameterName(), buildOff);
        }
        return key;
    }

    /**
     * actually only used for the mock configuration.
     * 
     * @param level
     * @return
     */
    protected static TranslationRule[] getRuleList(final IDataAccessContext dac) {
        return MockTranslationConfiguration.getRuleList();
    }
}
