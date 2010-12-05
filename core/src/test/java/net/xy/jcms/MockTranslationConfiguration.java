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
package net.xy.jcms;

import java.util.ArrayList;

import net.xy.jcms.controller.TranslationConfiguration.RuleParameter;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.types.StringWrapper;

/**
 * Mock translation configuration in lack of an configuration reader
 * 
 * @author xyan
 * 
 */
public class MockTranslationConfiguration implements ITranslationConfigurationAdapter {

    @Override
    public TranslationRule[] getRuleList(final IDataAccessContext dac) {
        final TranslationRule[] con0 = new TranslationRule[3];
        con0[0] = new TranslationRule("^du willst wohl zu (Ringtones|Funsounds)", "du willst wohl zu Ringtones",
                "contentgroup", new ArrayList<RuleParameter>() {
                    private static final long serialVersionUID = 5628166066449993901L;

                    {
                        add(new RuleParameter("contentgroup", 1, new StringWrapper()));
                    }
                });
        con0[1] = new TranslationRule("^Hm du willst dich also Einloggen", "Hm du willst dich also Einloggen",
                "userLogin",
                new ArrayList<RuleParameter>());
        con0[2] = new TranslationRule("^du willst wohl zu (Ringtones|Funsounds) zur Unterkategorie ([0-9]+)",
                "du willst wohl zu Funsounds zur Unterkategorie 1270", "subcategory", new ArrayList<RuleParameter>() {
                    private static final long serialVersionUID = -5034726752873057109L;

                    {
                        add(new RuleParameter("contentgroup", 1, new StringWrapper()));
                        add(new RuleParameter("catalogid", 2, new StringWrapper()));
                    }
                });
        return con0;
    }
}
