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
package net.xy.jcms.mvn;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.persistence.PersistenceHelper;
import net.xy.jcms.shared.JCmsHelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.xeustechnologies.jcl.JarClassLoader;

/**
 * class for importing converting and exporting usecase and translation
 * configuration
 * 
 * @author Xyan
 * @goal export
 */
public class ConfigExporter extends AbstractMojo {
    /**
     * treat inputs as simple xml files
     * 
     * @parameter expression="${exporter.simpleXml}"
     */
    Boolean simpleXml = Boolean.TRUE;

    // Inputs
    /**
     * read from rootfile
     * 
     * @parameter expression="${exporter.in.translation.file}"
     */
    File inTranslationRootFile;

    /**
     * single files in dir
     * 
     * @parameter expression="${exporter.in.translation.dir}"
     */
    File inTranslationDir;

    /**
     * read from db via JPA
     * 
     * @parameter expression="${exporter.in.translation.jpa}"
     */
    String inTranslationScheme;

    /**
     * read from rootfile
     * 
     * @parameter expression="${exporter.in.usecase.file}"
     */
    File inUsecaseRootFile;

    /**
     * single files in dir
     * 
     * @parameter expression="${exporter.in.usecase.dir}"
     */
    File inUsecaseDir;

    /**
     * read from db via JPA
     * 
     * @parameter expression="${exporter.in.usecase.jpa}"
     */
    String inUsecaseScheme;

    // Outputs
    /**
     * write to rootfile
     * 
     * @parameter expression="${exporter.out.translation.file}"
     */
    File outTranslationRootFile;

    /**
     * single files in dir
     * 
     * @parameter expression="${exporter.out.translation.dir}"
     */
    File outTranslationDir;

    /**
     * write in db via JPA
     * 
     * @parameter expression="${exporter.out.translation.jpa}"
     */
    String outTranslationScheme;

    /**
     * write to rootfile
     * 
     * @parameter expression="${exporter.out.usecase.file}"
     */
    File outUsecaseRootFile;

    /**
     * single files in dir
     * 
     * @parameter expression="${exporter.out.usecase.dir}"
     */
    File outUsecaseDir;

    /**
     * write in db via JPA
     * 
     * @parameter expression="${exporter.out.usecase.jpa}"
     */
    String outUsecaseScheme;

    /**
     * dir in which additional librarys resides
     * 
     * @parameter expression="${basedir}/target/lib"
     */
    private File libdir;

    /**
     * dir in which additional classes resides
     * 
     * @parameter expression="${basedir}/target/classes"
     */
    private File classDir;

    /**
     * dir in which additional resources resides loaded before classDir
     * 
     * @parameter expression="${basedir}/src/main/java"
     */
    private File rscDir;

    static {
        System.setProperty("jcl.isolateLogging", "false");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Import/Export operations.");
        try {
            final JarClassLoader jcl = new JarClassLoader(new URL[] {
                    rscDir.toURI().toURL(),
                    classDir.toURI().toURL(),
                    libdir.toURI().toURL() });
            final ClassLoader loader = jcl;
            final List<Usecase> uc = new LinkedList<Usecase>();
            final List<TranslationRule> tr = new LinkedList<TranslationRule>();
            // import
            // translations
            if (inTranslationRootFile != null) {
                getLog().info("Importing translations from rootfile.");
                if (simpleXml) {
                    tr.addAll(Arrays.asList(TranslationParser.parse(
                            JCmsHelper.loadResource(inTranslationRootFile.toURI().toURL(), loader), loader)));
                } else {
                    tr.addAll(PersistenceHelper.XML.loadTranslations(inTranslationRootFile, loader));
                }
            }
            if (inTranslationDir != null) {
                getLog().info("Importing translations from directory.");
                final List<File> xmls = scanForXML(inTranslationDir);
                for (final File xml : xmls) {
                    getLog().info(xml.toString());
                    if (simpleXml) {
                        tr.add(TranslationParser.parseSingle(
                                JCmsHelper.loadResource(xml.toURI().toURL(), loader),
                                loader));
                    } else {
                        tr.add(PersistenceHelper.XML.loadTranslation(xml, loader));
                    }
                }
            }
            if (inTranslationScheme != null) {
                getLog().info("Importing translations from DB via JPA.");
                PersistenceHelper.DB.setContext(inTranslationScheme);
                tr.addAll(PersistenceHelper.DB.loadAllTranslation(loader));
            }

            // usecases
            if (inUsecaseRootFile != null) {
                getLog().info("Importing usecases from rootfile.");
                if (simpleXml) {
                    uc.addAll(Arrays.asList(UsecaseParser.parse(
                            JCmsHelper.loadResource(inUsecaseRootFile.toURI().toURL(), loader), loader)));
                } else {
                    uc.addAll(PersistenceHelper.XML.loadUsecases(inUsecaseRootFile, loader));
                }
            }
            if (inUsecaseDir != null) {
                getLog().info("Importing usecases from directory.");
                final List<File> xmls = scanForXML(inUsecaseDir);
                for (final File xml : xmls) {
                    getLog().info(xml.toString());
                    if (simpleXml) {
                        uc.add(UsecaseParser.parseSingle(
                                JCmsHelper.loadResource(xml.toURI().toURL(), loader), loader));
                    } else {
                        uc.add(PersistenceHelper.XML.loadUsecase(xml, loader));
                    }
                }
            }
            if (inUsecaseScheme != null) {
                getLog().info("Importing usecases from DB via JPA.");
                PersistenceHelper.DB.setContext(inUsecaseScheme);
                uc.addAll(PersistenceHelper.DB.loadAllUsecases(loader));
            }

            // Exports
            if (outTranslationRootFile != null) {
                getLog().info("Saving translations to rootfile");
                PersistenceHelper.XML.saveTranslations(outTranslationRootFile, tr);
            }
            if (outTranslationDir != null) {
                getLog().info("Saving translations to sole files");
                for (final TranslationRule translationRule : tr) {
                    final File outfile = new File(outTranslationDir.getAbsolutePath() + File.separator
                            + "tr_" + translationRule.getUsecase() + translationRule.getParameters().size() + ".xml");
                    getLog().info(outfile.toString());
                    PersistenceHelper.XML.saveTranslation(outfile, translationRule);
                }
            }
            if (outTranslationScheme != null) {
                getLog().info("Saving translations to DB via JPA");
                PersistenceHelper.DB.setContext(outTranslationScheme);
                for (final TranslationRule translationRule : tr) {
                    PersistenceHelper.DB.saveTranslation(translationRule);
                }
            }

            // usecases
            if (outUsecaseRootFile != null) {
                getLog().info("Saving usecases to rootfile");
                PersistenceHelper.XML.saveUsecases(outUsecaseRootFile, uc);
            }
            if (outUsecaseDir != null) {
                getLog().info("Saving usecases to sole files");
                for (final Usecase usecase : uc) {
                    final File outfile = new File(outUsecaseDir.getAbsolutePath() + File.separator + "uc_" + usecase.getId()
                            + "_" + usecase.getParameterList().length + ".xml");
                    getLog().info(outfile.toString());
                    PersistenceHelper.XML.saveUsecase(outfile, usecase);
                }
            }
            if (outUsecaseScheme != null) {
                getLog().info("Saving usecases to DB via JPA");
                PersistenceHelper.DB.setContext(outUsecaseScheme);
                for (final Usecase usecase : uc) {
                    PersistenceHelper.DB.saveUsecase(usecase);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException("Critical error occured.", e);
        }
    }

    /**
     * recursively scans for xml files under an root
     * 
     * @param dir
     * @return
     */
    private List<File> scanForXML(final File dir) {
        final List<File> fileList = new ArrayList<File>();
        for (final File file : dir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".xml")) {
                    fileList.add(file);
                }
            } else if (file.isDirectory()) {
                fileList.addAll(scanForXML(file));
            }
        }
        return fileList;
    }
}
