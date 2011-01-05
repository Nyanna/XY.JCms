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
package net.xy.jcms.controller.configurations.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.xy.jcms.shared.DebugUtils;

import org.apache.log4j.Logger;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * validated xml accordingly to their dtd loaded from classpath dtd directory. Needed because some stax implementations
 * don't support validation.
 * 
 * @author Xyan
 * 
 */
public class XMLValidator {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(XMLValidator.class);

    /**
     * delegate which loads the inputstream from string via given loader
     * 
     * @param xml
     * @throws XMLValidationException
     */
    public static void validate(final String xml, final ClassLoader loader) throws XMLValidationException {
        validate(loader.getResourceAsStream(xml));
    }

    /**
     * validates the xml
     * 
     * @param xml
     * @throws XMLValidationException
     */
    public static void validate(final InputStream xml) throws XMLValidationException {
        try {
            final XMLReader parser = XMLReaderFactory.createXMLReader();
            // org.apache.xerces.parsers.SAXParser -- with JCL
            // com.sun.org.apache.xerces.internal.parsers.SAXParser -- without
            // -Dorg.xml.sax.driver=
            LOG.info("XMLReader loaded: " + parser.getClass().getName());
            final SAXReactor handler = new SAXReactor();
            parser.setFeature("http://xml.org/sax/features/validation", Boolean.TRUE);
            parser.setDTDHandler(handler);
            parser.setEntityResolver(handler);
            parser.setErrorHandler(handler);
            parser.parse(new InputSource(xml));
        } catch (final SAXException e) {
            throw new XMLValidationException("Error processing the XMl", e);
        } catch (final IOException e) {
            throw new XMLValidationException("XML couldn't be read", e);
        }
    }

    /**
     * simpla wrapper
     * 
     * @author Xyan
     * 
     */
    public static class XMLValidationException extends SAXParseException {
        private static final long serialVersionUID = -7367344028891878212L;

        /**
         * default constructor with message
         * 
         * @param message
         */
        public XMLValidationException(final String message) {
            super(message, new Locator() {

                @Override
                public String getSystemId() {
                    return null;
                }

                @Override
                public String getPublicId() {
                    return null;
                }

                @Override
                public int getLineNumber() {
                    return 0;
                }

                @Override
                public int getColumnNumber() {
                    return 0;
                }
            });
        }

        /**
         * encapsulation constructor
         * 
         * @param message
         * @param ex
         */
        public XMLValidationException(final String message, final Exception ex) {
            super(message, "", "", 0, 0, ex);
        }
    }

    /**
     * handler for the sax parser, implements handler and resolver.
     * 
     * @author Xyan
     * 
     */
    private static class SAXReactor implements DTDHandler, EntityResolver, ErrorHandler {

        @Override
        public void warning(final SAXParseException exception) throws SAXException {
            throw new XMLValidationException("Error on validating the XML", exception);
        }

        @Override
        public void error(final SAXParseException exception) throws SAXException {
            throw new XMLValidationException("Error on validating the XML", exception);
        }

        @Override
        public void fatalError(final SAXParseException exception) throws SAXException {
            throw new XMLValidationException("Error on validating the XML", exception);
        }

        @Override
        public InputSource resolveEntity(final String publicId, String systemId) throws SAXException, IOException {
            if (systemId.startsWith("file:///")) {
                systemId = systemId.replace("file:///", "").replace("\\", File.separator).replace("/", File.separator);
                // will opel X:/matrice/root/"systemid"
                try {
                    return new InputSource(new FileInputStream(systemId));
                } catch (final FileNotFoundException ex) {
                    // get systemId only name for classpath retrieval
                    if (systemId.lastIndexOf(File.separator) >= -1
                            && systemId.lastIndexOf(File.separator) + 1 < systemId.length()) {
                        systemId = systemId.substring(systemId.lastIndexOf(File.separator) + 1);
                    }
                }
            }
            try {
                final InputStream st = this.getClass().getClassLoader()
                        .getResourceAsStream(systemId);
                st.available();
                return new InputSource(st);
            } catch (final Exception ex) {
                try {
                    final InputStream st = this.getClass().getClassLoader()
                            .getResourceAsStream("dtd/" + systemId);
                    st.available();
                    return new InputSource(st);
                } catch (final Exception ex2) {
                }
            }
            throw new XMLValidationException("An mendatory entity couln't be resolved. "
                    + DebugUtils.printFields(publicId, systemId));
        }

        @Override
        public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {}

        @Override
        public void unparsedEntityDecl(final String name, final String publicId, final String systemId,
                final String notationName)
                throws SAXException {}
    }
}
