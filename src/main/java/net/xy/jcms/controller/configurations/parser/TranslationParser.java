package net.xy.jcms.controller.configurations.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.TranslationConfiguration.RuleParameter;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;

/**
 * parser for translation rules xml configuration files
 * 
 * @author Xyan
 * 
 */
public class TranslationParser {

    /**
     * parses an xml configuration from an input streams. throwes
     * IllegalArgumentExceptions in case of syntax error.
     * 
     * @param in
     * @return
     * @throws XMLStreamException
     */
    public static List<TranslationRule> parse(final InputStream in) throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        final XMLStreamReader parser = factory.createXMLStreamReader(in);
        while (parser.hasNext()) {
            final int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT && parser.getName().getLocalPart().equals("rules")) {
                return parseRules(parser);
            }
        }
        throw new IllegalArgumentException("No rules section found.");
    }

    /**
     * goes over any <rule reactOn="^du" buildOff="du" usecase="contentgroup">
     * 
     * @param parser
     * @return
     * @throws XMLStreamException
     */
    private static List<TranslationRule> parseRules(final XMLStreamReader parser) throws XMLStreamException {
        final List<TranslationRule> rules = new ArrayList<TranslationRule>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("rule")) {
                rules.add(parseRule(parser));
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between rule sections.");
            }
        }
        return rules;
    }

    /**
     * parses the attributes <rule reactOn="^du" buildOff="du"
     * usecase="contentgroup">
     * 
     * @param parser
     * @return
     * @throws XMLStreamException
     */
    private static TranslationRule parseRule(final XMLStreamReader parser) throws XMLStreamException {
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
        parameters = parseParameter(parser);

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
     * @return
     * @throws XMLStreamException
     */
    private static List<RuleParameter> parseParameter(final XMLStreamReader parser) throws XMLStreamException {
        // TODO: [LOW] type conversion maybe via callback
        final List<RuleParameter> params = new ArrayList<RuleParameter>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            String parameterName = null, converter = null;
            Integer aplicatesToGroup = null;
            if (parser.getAttributeCount() != 3) {
                throw new IllegalArgumentException("There are to much or few attributes specified for parameter.");
            }
            for (int i = 0; i < 3; i++) {
                if (parser.getAttributeLocalName(i).equals("name")) {
                    parameterName = parser.getAttributeValue(i);
                } else if (parser.getAttributeLocalName(i).equals("convert")) {
                    converter = parser.getAttributeValue(i);
                } else if (parser.getAttributeLocalName(i).equals("group")) {
                    aplicatesToGroup = new Integer(parser.getAttributeValue(i));
                }
            }

            if (StringUtils.isBlank(parameterName)) {
                throw new IllegalArgumentException("Parameter name has to be set");
            } else if (StringUtils.isBlank(converter)) {
                throw new IllegalArgumentException("Parameter Converter has to be set");
            } else if (aplicatesToGroup == null) {
                throw new IllegalArgumentException("Applicates to regex group has to be set");
            } else {
                params.add(new RuleParameter(parameterName, aplicatesToGroup, converter));
            }
            parser.nextTag(); // gets endtag
        }
        return params;
    }
}
