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

import java.io.File;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import net.xy.jcms.controller.configurations.parser.TranslationConverter;
import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.persistence.translation.TranslationPersistence;

/**
 * simple test that imports translation rules and usecases to db via JPA
 * 
 * @author Xyan
 * 
 */
public class TranslationJAXB {

    // @Test
    public void testThis() throws ClassNotFoundException, JAXBException {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleTranslationRules.xml"), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final TranslationRule rule : rules) {
            final File out = new File(rule.getUsecase() + "_" + rule.getParameters().size() + ".xml");
            TranslationPersistence.XML.saveTranslation(out, rule);
            TranslationConverter.convert(TranslationPersistence.XML.loadTranslation(out), Thread.currentThread()
                    .getContextClassLoader());
            out.deleteOnExit();

        }
        final File all = new File("All.xml");
        TranslationPersistence.XML.saveTranslations(all, Arrays.asList(rules));
        TranslationConverter.convert(TranslationPersistence.XML.loadTranslations(all), Thread.currentThread()
                .getContextClassLoader());
        all.deleteOnExit();
    }

    @Test
    public void testDB() throws ClassNotFoundException {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleTranslationRules.xml"), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final TranslationRule rule : rules) {
            TranslationPersistence.DB.saveTranslation(rule);

        }
    }
}
