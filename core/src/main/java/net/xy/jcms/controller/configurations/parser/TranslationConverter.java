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
package net.xy.jcms.controller.configurations.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.xy.jcms.controller.configurations.pool.ConverterPool;
import net.xy.jcms.controller.translation.RuleParameter;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.translation.RuleParameterDTO;
import net.xy.jcms.persistence.translation.TranslationRuleDTO;
import net.xy.jcms.persistence.translation.TranslationRulesDTO;
import net.xy.jcms.shared.IConverter;
import net.xy.jcms.shared.InitializableController;

/**
 * these class helps to instanciates translation out from its DTO counterpart
 * 
 * @author Xyan
 * 
 */
public class TranslationConverter {

    /**
     * converts an parsed xml back from its root element
     * 
     * @param rules
     * @param loader
     *            needed for typeconverters
     * @return rule list
     * @throws ClassNotFoundException
     */
    public static List<TranslationRule> convert(final TranslationRulesDTO rules, final ClassLoader loader)
            throws ClassNotFoundException {
        return convert(rules.getRules(), loader);
    }

    /**
     * converts an list of rules back
     * 
     * @param rules
     * @param loader
     *            needed for typeconverters
     * @return dto list
     * @throws ClassNotFoundException
     */
    public static List<TranslationRule> convert(final List<TranslationRuleDTO> rules, final ClassLoader loader)
            throws ClassNotFoundException {
        final List<TranslationRule> ret = new LinkedList<TranslationRule>();
        for (final TranslationRuleDTO rule : rules) {
            ret.add(convert(rule, loader));
        }
        return ret;
    }

    /**
     * converts an single dto back
     * 
     * @param rules
     * @param loader
     *            needed for typeconverters
     * @return dto
     * @throws ClassNotFoundException
     */
    public static TranslationRule convert(final TranslationRuleDTO rule, final ClassLoader loader)
            throws ClassNotFoundException {
        final List<RuleParameter> params = new ArrayList<RuleParameter>();
        if (rule.getParameters() != null) {
            for (final RuleParameterDTO param : rule.getParameters()) {
                IConverter<?> convt = ConverterPool.get(param.getConverter(), loader);
                if (convt instanceof InitializableController) {
                    convt = ((InitializableController<?>) convt).initialize(MapEntry.convert(param.getBuildInMap()));
                }
                params.add(new RuleParameter(param.getParameterName(), param.getAplicatesToGroup(), convt));
            }
        }
        return new TranslationRule(rule.getReactOn(), rule.getBuildOff(), rule.getUsecase(), params);
    }
}
