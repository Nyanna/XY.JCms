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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.configurations.pool.ConverterPool;
import net.xy.jcms.controller.translation.RuleParameter;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.shared.types.StringMap;

/**
 * parser for translation rules xml configuration files
 * 
 * @author Xyan
 * 
 */
public class TranslationParser {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(TranslationParser.class);

    /**
     * parses an xml configuration from an input streams. throwes
     * IllegalArgumentExceptions in case of syntax error.
     * 
     * @param in
     * @return value
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     *             in case there are problems with an params type converter
     */
    public static TranslationRule[] parse(final InputStream in, final ClassLoader loader) throws XMLStreamException,
             ClassNotFoundException {
        @SuppressWarnings("deprecation")
        final XMLInputFactory factory = XMLInputFactory.newInstance("com.sun.xml.internal.stream.XMLInputFactoryImpl",
                TranslationParser.class.getClassLoader());
        LOG.info("XMLInputFactory loaded: " + factory.getClass().getName());
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        // not supported be the reference implementation
        // factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
        final XMLStreamReader parser = factory.createXMLStreamReader(in);
        while (parser.hasNext()) {
            final int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT && parser.getName().getLocalPart().equals("rules")) {
                return parseRules(parser, loader);
            }
        }
        throw new IllegalArgumentException("No rules section found.");
    }

    /**
     * parses an single file translation
     * 
     * @param in
     * @param loader
     * @return
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     *             in case there are problems with an params type converter
     */
    public static TranslationRule parseSingle(final InputStream in, final ClassLoader loader) throws XMLStreamException,
            ClassNotFoundException {
        @SuppressWarnings("deprecation")
        final XMLInputFactory factory = XMLInputFactory.newInstance("com.sun.xml.internal.stream.XMLInputFactoryImpl",
                TranslationParser.class.getClassLoader());
        LOG.info("XMLInputFactory loaded: " + factory.getClass().getName());
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        final XMLStreamReader parser = factory.createXMLStreamReader(in);
        while (parser.hasNext()) {
            final int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT && parser.getName().getLocalPart().equals("rule")) {
                return parseRule(parser, loader);
            }
        }
        throw new IllegalArgumentException("No rules section found.");
    }

    /**
     * goes over any <rule reactOn="^du" buildOff="du" usecase="contentgroup">
     * 
     * @param parser
     * @return value
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static TranslationRule[] parseRules(final XMLStreamReader parser, final ClassLoader loader)
            throws XMLStreamException, ClassNotFoundException {
        final List<TranslationRule> rules = new LinkedList<TranslationRule>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("rule")) {
                rules.add(parseRule(parser, loader));
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between rule sections.");
            }
        }
        return rules.toArray(new TranslationRule[rules.size()]);
    }

    /**
     * parses the attributes <rule reactOn="^du" buildOff="du"
     * usecase="contentgroup">
     * 
     * @param parser
     * @return value
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static TranslationRule parseRule(final XMLStreamReader parser, final ClassLoader loader)
            throws XMLStreamException, ClassNotFoundException {
        String reactOn = null, buildOff = null, usecase = null;
        List<RuleParameter> parameters = null;
        if (parser.getAttributeCount() != 3) {
            throw new IllegalArgumentException("There are to much or few attributes specified for rule.");
        }
        for (int i = 0; i < 3; i++) {
            if (parser.getAttributeLocalName(i).equals("reactOn")) {
                reactOn = parser.getAttributeValue(i);
            } else if (parser.getAttributeLocalName(i).equals("buildOff")) {
                buildOff = parser.getAttributeValue(i);
            } else if (parser.getAttributeLocalName(i).equals("usecase")) {
                usecase = parser.getAttributeValue(i);
            }
        }
        parameters = parseParameter(parser, loader);

        if (StringUtils.isBlank(reactOn)) {
            throw new IllegalArgumentException("ReactOn has to be set");
        } else if (StringUtils.isBlank(buildOff)) {
            throw new IllegalArgumentException("BuildOff has to be set");
        } else if (StringUtils.isBlank(usecase)) {
            throw new IllegalArgumentException("UsecaseId has to be set");
        } else if (parameters == null) {
            throw new IllegalArgumentException("Error on parsing parameters");
        } else {
            return new TranslationRule(reactOn, buildOff, usecase, parameters);
        }
    }

    /**
     * checks for parameters <parameter name="contentgroup" group="1"
     * convert="de.jamba.ContentGroupConverter"/>
     * 
     * @param parser
     * @return value
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static List<RuleParameter> parseParameter(final XMLStreamReader parser, final ClassLoader loader)
            throws XMLStreamException, ClassNotFoundException {
        final List<RuleParameter> params = new ArrayList<RuleParameter>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            String parameterName = null, converter = null;
            Integer aplicatesToGroup = null;
            if (parser.getAttributeCount() != 3 && parser.getAttributeCount() != 2) {
                throw new IllegalArgumentException("There are to much or few attributes specified for parameter.");
            }
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                if (parser.getAttributeLocalName(i).equals("name")) {
                    parameterName = parser.getAttributeValue(i);
                } else if (parser.getAttributeLocalName(i).equals("convert")) {
                    converter = parser.getAttributeValue(i);
                } else if (parser.getAttributeLocalName(i).equals("group")) {
                    aplicatesToGroup = new Integer(parser.getAttributeValue(i));
                }
            }

            boolean goEnd = true;
            Map<String, String> mappings = null;
            if (parser.next() == XMLStreamConstants.CHARACTERS) {
                final String mappingStr = parser.getText();
                // get integrated mapping body
                converter = "net.xy.jcms.shared.types.StringMap";
                if (StringUtils.isNotBlank(mappingStr)) {
                    mappings = new HashMap<String, String>();
                    final String[] lines = mappingStr.split("\n");
                    for (String line : lines) {
                        line = line.trim();
                        if (StringUtils.isBlank(line) || line.startsWith("#")) {
                            continue;
                        }
                        final String[] pair = line.split("=", 2);
                        mappings.put(pair[0].trim(), pair[1].trim());
                    }
                }
            } else {
                goEnd = false; // allready on end
            }

            if (StringUtils.isBlank(parameterName)) {
                throw new IllegalArgumentException("Parameter name has to be set");
            } else if (StringUtils.isBlank(converter)) {
                throw new IllegalArgumentException("Parameter Converter has to be set");
            } else if (aplicatesToGroup == null) {
                throw new IllegalArgumentException("Applicates to regex group has to be set");
            } else {
                if (mappings != null) {
                    // get special mapping converter
                    params.add(new RuleParameter(parameterName, aplicatesToGroup, new StringMap(mappings)));
                } else {
                    // get normal converter
                    params.add(new RuleParameter(parameterName, aplicatesToGroup, ConverterPool.get(converter, loader)));
                }
            }
            if (goEnd) {
                parser.nextTag(); // gets endtag
            }
        }
        return params;
    }
}
