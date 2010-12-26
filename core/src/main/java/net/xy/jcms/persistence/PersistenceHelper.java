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
package net.xy.jcms.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.xy.jcms.controller.configurations.parser.TranslationConverter;
import net.xy.jcms.controller.configurations.parser.UsecaseConverter;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.persistence.translation.TranslationRuleDTO;
import net.xy.jcms.persistence.translation.TranslationRulesDTO;
import net.xy.jcms.persistence.usecase.UsecaseDTO;
import net.xy.jcms.persistence.usecase.UsecasesDTO;

/**
 * TRanslation config persistance helper for JAXB & JPA
 * 
 * @author Xyan
 * 
 */
public class PersistenceHelper {

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
         * JPA context for creating entities
         */
        private static String context = "fw-web";

        /**
         * singleton factory
         * 
         * @return
         */
        private static EntityManagerFactory getEMF() {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory(context);
            }
            return emf;
        }

        /**
         * sets another context and clears emf
         * 
         * @param context
         */
        public static void setContext(final String context) {
            DB.context = context;
            DB.emf = null;
        }

        /**
         * saves an translation rule to the db specified in persistence.xml
         * 
         * @param rule
         * @return id of inserted dto
         */
        public static int saveTranslation(final TranslationRule rule) {
            final EntityManager em = getEMF().createEntityManager();
            em.getTransaction().begin();
            final TranslationRuleDTO dto = rule.toDTO();
            em.merge(dto);
            em.getTransaction().commit();
            em.close();
            return dto.id;
        }

        /**
         * method for loading an translation by its id from db
         * 
         * @param id
         * @param loader
         *            for laoding converters and dependencies
         * @return rule
         * @throws ClassNotFoundException
         */
        public static TranslationRule loadTranslation(final int id, final ClassLoader loader) throws ClassNotFoundException {
            final EntityManager em = getEMF().createEntityManager();
            em.getTransaction().begin();
            final TranslationRuleDTO result = em.find(TranslationRuleDTO.class, id);
            em.close();
            return TranslationConverter.convert(result, loader);
        }

        /**
         * loads all translations from db
         * 
         * @param loader
         *            to load converetrs and dependencies
         * @return rulelist
         * @throws ClassNotFoundException
         */
        public static List<TranslationRule> loadAllTranslation(final ClassLoader loader) throws ClassNotFoundException {
            final EntityManager em = getEMF().createEntityManager();
            final TypedQuery<TranslationRuleDTO> query = em.createQuery("SELECT r FROM TranslationRuleDTO r",
                    TranslationRuleDTO.class);
            final List<TranslationRule> result = new LinkedList<TranslationRule>();
            for (final TranslationRuleDTO dto : query.getResultList()) {
                result.add(TranslationConverter.convert(dto, loader));
            }
            em.close();
            return result;
        }

        /**
         * saves an usecase in the db
         * 
         * @param acase
         * @return id of the usecase
         */
        public static int saveUsecase(final Usecase acase) {
            final EntityManager em = getEMF().createEntityManager();
            em.getTransaction().begin();
            final UsecaseDTO dto = acase.toDTO();
            em.merge(dto);
            em.getTransaction().commit();
            em.close();
            return dto.getPrimaryKey();
        }

        /**
         * method for loading an usecase by its id from db
         * 
         * @param id
         * @param loader
         *            for laoding converters and dependencies
         * @return case
         * @throws ClassNotFoundException
         */
        public static Usecase loadUsecase(final int id, final ClassLoader loader) throws ClassNotFoundException {
            final EntityManager em = getEMF().createEntityManager();
            em.getTransaction().begin();
            final UsecaseDTO result = em.find(UsecaseDTO.class, id);
            em.close();
            return UsecaseConverter.convert(result, loader);
        }

        /**
         * loads all usecases from db
         * 
         * @param loader
         *            to load converetrs and dependencies
         * @return case list
         * @throws ClassNotFoundException
         */
        public static List<Usecase> loadAllUsecases(final ClassLoader loader) throws ClassNotFoundException {
            final EntityManager em = getEMF().createEntityManager();
            final TypedQuery<UsecaseDTO> query = em.createQuery("SELECT r FROM UsecaseDTO r", UsecaseDTO.class);
            final List<Usecase> result = new ArrayList<Usecase>();
            for (final UsecaseDTO dto : query.getResultList()) {
                result.add(UsecaseConverter.convert(dto, loader));
            }
            em.close();
            return result;
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
                context = JAXBContext.newInstance(new Class[] { TranslationRulesDTO.class, UsecasesDTO.class });
            }
            return context;
        }

        /**
         * init only one marshaLer instance
         */
        private static Marshaller marshaller = null;

        /**
         * gets an singleton marshaller instance
         * 
         * @return marshaller
         * @throws JAXBException
         */
        private static Marshaller getMarshaller() throws JAXBException {
            if (marshaller == null) {
                marshaller = getContext().createMarshaller();
                marshaller.setProperty("jaxb.encoding", "UTF-8");
                marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            }
            return marshaller;
        }

        /**
         * save an translation to an outfile
         * 
         * @param outFile
         * @param rule
         * @throws JAXBException
         */
        public static void saveTranslation(final File outFile, final TranslationRule rule) throws JAXBException {
            getMarshaller().marshal(rule.toDTO(), outFile);
        }

        /**
         * loads an translation from an xml file
         * 
         * @param infile
         * @return translation
         * @throws JAXBException
         * @throws ClassNotFoundException
         *             in case of an type converter couldn't be loaded
         */
        public static TranslationRule loadTranslation(final File infile, final ClassLoader loader) throws JAXBException,
                ClassNotFoundException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return TranslationConverter.convert((TranslationRuleDTO) m.unmarshal(infile), loader);
        }

        /**
         * save an whole list of translation in an xml outfile
         * 
         * @param outFile
         * @param rules
         * @throws JAXBException
         */
        public static void saveTranslations(final File outFile, final List<TranslationRule> rules) throws JAXBException {
            final List<TranslationRuleDTO> ruleDTOs = new ArrayList<TranslationRuleDTO>();
            for (final TranslationRule rule : rules) {
                ruleDTOs.add(rule.toDTO());
            }
            final TranslationRulesDTO dto = new TranslationRulesDTO();
            dto.setRules(ruleDTOs);
            getMarshaller().marshal(dto, outFile);
        }

        /**
         * loads an complete translation set from an single xml file
         * 
         * @param infile
         * @return
         * @throws JAXBException
         * @throws ClassNotFoundException
         *             in case of an type converter couldn't be loaded
         */
        public static List<TranslationRule> loadTranslations(final File infile, final ClassLoader loader)
                throws JAXBException, ClassNotFoundException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return TranslationConverter.convert((TranslationRulesDTO) m.unmarshal(infile), loader);
        }

        /**
         * method saves an usecase to an xml outfile
         * 
         * @param out
         * @param acase
         * @throws JAXBException
         */
        public static void saveUsecase(final File out, final Usecase acase) throws JAXBException {
            getMarshaller().marshal(acase.toDTO(), out);
        }

        /**
         * loads an single usecase from an xml
         * 
         * @param infile
         * @param loader
         * @return usecase
         * @throws JAXBException
         * @throws ClassNotFoundException
         *             in case of an usecase dependency could not be loaded
         */
        public static Usecase loadUsecase(final File infile, final ClassLoader loader) throws JAXBException,
                ClassNotFoundException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return UsecaseConverter.convert((UsecaseDTO) m.unmarshal(infile), loader);
        }

        /**
         * method saves an whole list of usecases to an xml outfile
         * 
         * @param outfile
         * @param usecases
         * @throws JAXBException
         */
        public static void saveUsecases(final File outfile, final List<Usecase> usecases) throws JAXBException {
            final List<UsecaseDTO> caseDTOs = new ArrayList<UsecaseDTO>();
            for (final Usecase acase : usecases) {
                caseDTOs.add(acase.toDTO());
            }
            final UsecasesDTO dto = new UsecasesDTO();
            dto.setUsecases(caseDTOs);
            getMarshaller().marshal(dto, outfile);
        }

        /**
         * converts an usecase config xml back to an list of usecases
         * 
         * @param infile
         * @param loader
         * @return usecases
         * @throws JAXBException
         * @throws ClassNotFoundException
         *             in case of an usecase dependency could not be loaded
         */
        public static List<Usecase> loadUsecases(final File infile, final ClassLoader loader)
                throws JAXBException, ClassNotFoundException {
            final Unmarshaller m = getContext().createUnmarshaller();
            return UsecaseConverter.convert((UsecasesDTO) m.unmarshal(infile), loader);
        }
    }
}
