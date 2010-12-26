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
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Test;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

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
public class PersistingCheckTest {
    protected String transConfig = "net/xy/jcms/ExampleTranslationRules.xml";
    protected String useConfig = "net/xy/jcms/ExampleUsecases.xml";

    @Test
    public void testTranslationXMLTransfer() throws ClassNotFoundException, JAXBException {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(transConfig), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final TranslationRule rule : rules) {
            final File out = new File(rule.getUsecase() + "_" + rule.getParameters().size() + ".xml");
            PersistenceHelper.XML.saveTranslation(out, rule);
            final TranslationRule result = PersistenceHelper.XML.loadTranslation(out, Thread.currentThread()
                    .getContextClassLoader());
            Assert.assertTrue(rule.equals(result));
            out.deleteOnExit();
        }
        final File all = new File("All.xml");
        PersistenceHelper.XML.saveTranslations(all, Arrays.asList(rules));
        PersistenceHelper.XML.loadTranslations(all, Thread.currentThread().getContextClassLoader());
        all.deleteOnExit();
    }

    @Test
    public void testUsecaseXMLTransfer() throws ClassNotFoundException, JAXBException {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(useConfig), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        for (final Usecase acase : cases) {
            final File out = new File(acase.getId() + "_" + acase.getParameterList().length + ".xml");
            PersistenceHelper.XML.saveUsecase(out, acase);
            final Usecase result = PersistenceHelper.XML.loadUsecase(out, Thread.currentThread().getContextClassLoader());
            Assert.assertTrue(acase.equals(result));
            out.deleteOnExit();
        }
        final File all = new File("AllUse.xml");
        PersistenceHelper.XML.saveUsecases(all, Arrays.asList(cases));
        PersistenceHelper.XML.loadUsecases(all, Thread.currentThread().getContextClassLoader());
        // all.deleteOnExit();
    }

    @Test
    public void testTranslationDBTransfer() throws ClassNotFoundException {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(transConfig), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        try {
            for (final TranslationRule rule : rules) {
                final int id = PersistenceHelper.DB.saveTranslation(rule);
                final TranslationRule back = PersistenceHelper.DB.loadTranslation(id, Thread.currentThread()
                        .getContextClassLoader());
                Assert.assertTrue("Rule is not the same " + rule.getReacton(), rule.equals(back));
            }
        } catch (final PersistenceException t) {
            // catch no db present or can't connect
            if (!(t.getCause() instanceof DatabaseException) ||
                    !(t.getCause().getCause() instanceof CommunicationsException) ||
                    !(t.getCause().getCause().getCause() instanceof ConnectException)) {
                throw t;
            }
        }
    }

    @Test
    public void testUsecaseDBTransfer() throws ClassNotFoundException {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(useConfig), Thread.currentThread()
                    .getContextClassLoader());
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        try {
            for (final Usecase acase : cases) {
                final int id = PersistenceHelper.DB.saveUsecase(acase);
                final Usecase back = PersistenceHelper.DB.loadUsecase(id, Thread.currentThread()
                        .getContextClassLoader());
                Assert.assertTrue("Usecase is not the same " + acase.getId(), acase.equals(back));
            }
        } catch (final PersistenceException t) {
            // catch no db present or can't connect
            if (!(t.getCause() instanceof DatabaseException) ||
                    !(t.getCause().getCause() instanceof CommunicationsException) ||
                    !(t.getCause().getCause().getCause() instanceof ConnectException)) {
                throw t;
            }
        }
    }

    @Test
    public void testDBLoadAll() {
        try {
            final List<TranslationRule> back = PersistenceHelper.DB.loadAllTranslation(Thread.currentThread()
                    .getContextClassLoader());
            Assert.assertNotNull(back);
            Assert.assertTrue(!back.isEmpty());
            final List<Usecase> back2 = PersistenceHelper.DB.loadAllUsecases(Thread.currentThread()
                    .getContextClassLoader());
            Assert.assertNotNull(back2);
            Assert.assertTrue(!back2.isEmpty());
        } catch (final ClassNotFoundException e) {
            // in case db is polluted wwith foreign data
        }
    }
}
