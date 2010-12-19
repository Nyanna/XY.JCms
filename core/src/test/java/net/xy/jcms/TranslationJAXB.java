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

import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.persistence.PersistenceHelper;

/**
 * simple test that imports translation rules and usecases to db via JPA
 * 
 * @author Xyan
 * 
 */
public class TranslationJAXB {

    // @Test
    public void testTranslationXMLTransfer() throws ClassNotFoundException, JAXBException {
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
            PersistenceHelper.XML.saveTranslation(out, rule);
            PersistenceHelper.XML.loadTranslation(out, Thread.currentThread().getContextClassLoader());
            out.deleteOnExit();
        }
        final File all = new File("All.xml");
        PersistenceHelper.XML.saveTranslations(all, Arrays.asList(rules));
        PersistenceHelper.XML.loadTranslations(all, Thread.currentThread().getContextClassLoader());
        all.deleteOnExit();
    }

    // @Test
    public void testTranslationDBTransfer() throws ClassNotFoundException {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleTranslationRules.xml"), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final TranslationRule rule : rules) {
            PersistenceHelper.DB.saveTranslation(rule);
        }
    }

    // @Test
    public void testUsecaseXMLTransfer() throws ClassNotFoundException, JAXBException {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleUsecases.xml"), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final Usecase acase : cases) {
            final File out = new File(acase.getId() + "_" + acase.getParameterList().length + ".xml");
            PersistenceHelper.XML.saveUsecase(out, acase);
            PersistenceHelper.XML.loadUsecase(out, Thread.currentThread().getContextClassLoader());
            out.deleteOnExit();
        }
        final File all = new File("All.xml");
        PersistenceHelper.XML.saveUsecases(all, Arrays.asList(cases));
        PersistenceHelper.XML.loadUsecases(all, Thread.currentThread().getContextClassLoader());
        all.deleteOnExit();
    }

    @Test
    public void testUsecaseDBTransfer() throws ClassNotFoundException {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleUsecases.xml"), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final Usecase acase : cases) {
            PersistenceHelper.DB.saveUsecase(acase);
        }
    }
}
