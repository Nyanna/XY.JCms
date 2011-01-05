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
package net.xy.jcms.controller.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.xy.jcms.persistence.translation.RuleParameterDTO;
import net.xy.jcms.persistence.translation.TranslationRuleDTO;
import net.xy.jcms.shared.DebugUtils;

/**
 * describes an path translation rule to convert human readable path's in an
 * semantic navigation key. Rule is immutable.
 * 
 * @author xyan
 * 
 */
final public class TranslationRule {
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
    private final List<RuleParameter> parameters = new ArrayList<RuleParameter>();

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
        if (parameters != null) {
            this.parameters.addAll(parameters);
            Collections.sort(this.parameters);
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

    /**
     * method converting this rule to an transfer struct
     * 
     * @return value
     */
    public TranslationRuleDTO toDTO() {
        final TranslationRuleDTO dto = new TranslationRuleDTO();
        dto.setReactOn(reactOn.pattern());
        dto.setBuildOff(buildOff);
        dto.setUsecase(usecase);
        final List<RuleParameterDTO> params = new ArrayList<RuleParameterDTO>();
        for (final RuleParameter param : parameters) {
            params.add(param.toDTO());
        }
        dto.setParameters(params);
        return dto;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final TranslationRule oo = (TranslationRule) obj;
        return (reactOn == oo.reactOn || reactOn != null && reactOn.pattern().equals(oo.reactOn.pattern())) &&
                (buildOff == oo.buildOff || buildOff != null && buildOff.equals(oo.buildOff)) &&
                (usecase == oo.usecase || usecase != null && usecase.equals(oo.usecase)) &&
                (parameters == oo.parameters || parameters != null && parameters.equals(oo.parameters));
    }

    @Override
    public int hashCode() {
        int hash = 768;
        if (reactOn != null) {
            hash = hash * 3 + reactOn.pattern().hashCode();
        }
        if (buildOff != null) {
            hash = hash * 3 + buildOff.hashCode();
        }
        if (usecase != null) {
            hash = hash * 3 + usecase.hashCode();
        }
        if (parameters != null) {
            hash = hash * 3 + parameters.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder("TranslationRule[usecase=").append(usecase).append(",buildOff=").append(buildOff)
                .append(",reactOn=").append(reactOn).append("]").toString();
    }
}