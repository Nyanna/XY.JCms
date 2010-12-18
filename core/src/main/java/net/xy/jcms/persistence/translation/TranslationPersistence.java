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
package net.xy.jcms.persistence.translation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.xy.jcms.controller.translation.TranslationRule;

/**
 * TRanslation config persistance helper for JAXB & JPA
 * 
 * @author Xyan
 * 
 */
public class TranslationPersistence {

    /**
     * store method dedicated to database persistance using JPA
     * 
     * @author Xyan
     * 
     */
    public static class DB {
        /**
         * stores the manager factory
         */
        private static EntityManagerFactory emf = null;

        /**
         * singleton factory
         * 
         * @return
         */
        private static EntityManagerFactory getEMF() {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("fw-web");
            }
            return emf;
        }

        /**
         * saves an translation rule to the db specified in persistence.xml
         * 
         * @param rule
         */
        public static void saveTranslation(final TranslationRule rule) {
            final EntityManager em = getEMF().createEntityManager();
            em.getTransaction().begin();
            final TranslationRuleDTO dto = rule.toDTO();
            em.persist(dto);
            em.getTransaction().commit();
            em.close();
        }
    }

    /**
     * stores methods helpfull for storing translations to xml via JAXB
     * 
     * @author Xyan
     * 
     */
    public static class XML {
        /**
         * stores the JAXB context
         */
        private static JAXBContext context = null;

        /**
         * context singleton
         * 
         * @return
         * @throws JAXBException
         */
        private static JAXBContext getContext() throws JAXBException {
            if (context == null) {
                context = JAXBContext.newInstance(TranslationRulesDTO.class);
            }
            return context;
        }

        /**
         * save an translation to an outfile
         * 
         * @param outFile
         * @param rule
         * @throws JAXBException
         */
        public static void saveTranslation(final File outFile, final TranslationRule rule) throws JAXBException {
            final Marshaller m = getContext().createMarshaller();
            m.marshal(rule.toDTO(), outFile);
        }

        /**
         * loads an translation from an xml file
         * 
         * @param infile
         * @return translation
         * @throws JAXBException
         */
        public static TranslationRuleDTO loadTranslation(final File infile) throws JAXBException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return (TranslationRuleDTO) m.unmarshal(infile);
        }

        /**
         * save an whole list of translation in an xml outfile
         * 
         * @param outFile
         * @param rules
         * @throws JAXBException
         */
        public static void saveTranslations(final File outFile, final List<TranslationRule> rules) throws JAXBException {
            final Marshaller m = getContext().createMarshaller();
            final List<TranslationRuleDTO> ruleDTOs = new ArrayList<TranslationRuleDTO>();
            for (final TranslationRule rule : rules) {
                ruleDTOs.add(rule.toDTO());
            }
            final TranslationRulesDTO dto = new TranslationRulesDTO();
            dto.setRules(ruleDTOs);
            m.marshal(dto, outFile);
        }

        /**
         * loads an complete translation set from an single xml file
         * 
         * @param infile
         * @return
         * @throws JAXBException
         */
        public static TranslationRulesDTO loadTranslations(final File infile) throws JAXBException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return (TranslationRulesDTO) m.unmarshal(infile);
        }
    }
}
