package net.xy.jcms.controller.configurations.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Parameter;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;

/**
 * parses an usecase xml configuration file
 * 
 * @author Xyan
 * 
 */
public class UsecaseParser {

    public static Usecase[] parse(final InputStream in) throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        final XMLStreamReader parser = factory.createXMLStreamReader(in);
        while (parser.hasNext()) {
            final int event = parser.next();
            if (event == XMLStreamConstants.START_ELEMENT && parser.getName().getLocalPart().equals("usecases")) {
                return parseUsecases(parser);
            }
        }
        throw new IllegalArgumentException("No usecases section found. [" + parser.getLocation() + "]");
    }

    private static Usecase[] parseUsecases(final XMLStreamReader parser) throws XMLStreamException {
        final List<Usecase> cases = new ArrayList<Usecase>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("usecase")) {
                cases.add(parseUsecase(parser));
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between Usecase sections. ["
                        + parser.getLocation() + "]");
            }
        }
        return cases.toArray(new Usecase[cases.size()]);
    }

    private static Usecase parseUsecase(final XMLStreamReader parser) throws XMLStreamException {
        if (parser.getAttributeCount() != 1) {
            throw new IllegalArgumentException("There are to much or few attributes specified for usecase. ["
                    + parser.getLocation() + "]");
        }
        final String id = parser.getAttributeValue(0);
        String description = null;
        Parameter[] parameterList = {};
        Controller[] controllerList = {};
        Configuration<?>[] configurationList = {};
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("description")) {
                description = parseDescription(parser);
            } else if (parser.getLocalName().equals("parameter")) {
                parameterList = parseParameters(parser);
            } else if (parser.getLocalName().equals("controller")) {
                controllerList = parseControllers(parser);
            } else if (parser.getLocalName().equals("configurations")) {
                configurationList = parseConfigurations(parser);
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between Usecase sections. ["
                        + parser.getLocation() + "]");
            }
        }
        if (StringUtils.isBlank(description) || description.length() < 32) {
            throw new IllegalArgumentException("Description is empty or insufficient please add more details. ["
                    + parser.getLocation() + "]");
        }
        return new Usecase(id, description, parameterList, controllerList, configurationList);
    }

    private static String parseDescription(final XMLStreamReader parser) throws XMLStreamException {
        parser.next();
        final String text = parser.getText();
        parser.nextTag();
        return text;
    }

    private static Parameter[] parseParameters(final XMLStreamReader parser) throws XMLStreamException {
        final List<Parameter> params = new ArrayList<Parameter>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("param")) {
                params.add(parseParameter(parser));
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between param deffinitions. ["
                        + parser.getLocation() + "]");
            }
        }
        return params.toArray(new Parameter[params.size()]);
    }

    private static Parameter parseParameter(final XMLStreamReader parser) throws XMLStreamException {
        if (parser.getAttributeCount() != 2) {
            throw new IllegalArgumentException("There are to much or few attributes specified for param. ["
                    + parser.getLocation() + "]");
        }
        String key = null, valueType = null;
        for (int i = 0; i < 2; i++) {
            if (parser.getAttributeLocalName(i).equals("key")) {
                key = parser.getAttributeValue(i);
            } else if (parser.getAttributeLocalName(i).equals("valueType")) {
                valueType = parser.getAttributeValue(i);
            }
        }
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Param key name is missing [" + parser.getLocation() + "]");
        } else if (StringUtils.isBlank(valueType)) {
            throw new IllegalArgumentException("Param value type is missing [" + parser.getLocation() + "]");
        }
        parser.nextTag();
        return new Parameter(key, valueType);
    }

    private static Controller[] parseControllers(final XMLStreamReader parser) throws XMLStreamException {
        final List<Controller> controller = new ArrayList<Controller>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("class")) {
                controller.add(parseController(parser));
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between controller deffinitions. ["
                        + parser.getLocation() + "]");
            }
        }
        return controller.toArray(new Controller[controller.size()]);
    }

    private static Controller parseController(final XMLStreamReader parser) throws XMLStreamException {
        if (parser.getAttributeCount() != 2 && parser.getAttributeCount() != 1) {
            throw new IllegalArgumentException("There are to much or few attributes specified for class. ["
                    + parser.getLocation() + "]");
        }
        String path = null;
        final EnumSet<ConfigurationType> obmitedConfigurations = EnumSet.noneOf(ConfigurationType.class);
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeLocalName(i).equals("path")) {
                path = parser.getAttributeValue(i);
            } else if (parser.getAttributeLocalName(i).equals("obmitConfig")) {
                final String[] configs = parser.getAttributeValue(i).split(",");
                for (final String config : configs) {
                    obmitedConfigurations.add(ConfigurationType.valueOf(config.trim()));
                }
            }
        }
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Classpath must be set [" + parser.getLocation() + "]");
        }
        parser.nextTag();
        return new Controller(path, obmitedConfigurations);
    }

    private static Configuration<?>[] parseConfigurations(final XMLStreamReader parser) throws XMLStreamException {
        final List<Configuration<?>> configs = new ArrayList<Configuration<?>>();
        while (parser.nextTag() == XMLStreamConstants.START_ELEMENT) {
            if (parser.getLocalName().equals("configuration")) {
                final Configuration<?> config = parseConfiguration(parser);
                if (config != null) {
                    configs.add(config);
                }
            } else {
                throw new IllegalArgumentException("Syntax error nothing allowed between configuration deffinitions. ["
                        + parser.getLocation() + "]");
            }
        }
        return configs.toArray(new Configuration[configs.size()]);
    }

    private static Configuration<?> parseConfiguration(final XMLStreamReader parser) throws XMLStreamException {
        Configuration<?> config = null;
        if (parser.getAttributeCount() != 1 && parser.getAttributeCount() != 2) {
            throw new IllegalArgumentException("There are to much or few attributes specified for configuration. ["
                    + parser.getLocation() + "]");
        }
        ConfigurationType type = null;
        String include = null;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeLocalName(i).equals("type")) {
                type = ConfigurationType.valueOf(parser.getAttributeValue(i).trim());
            } else if (parser.getAttributeLocalName(i).equals("include")) {
                include = parser.getAttributeValue(i);
            }
        }
        if (!ConfigurationType.dataModel.equals(type)) {
            if (type == null) {
                throw new IllegalArgumentException("Configuration type must be set [" + parser.getLocation() + "]");
            }
            if (include != null) {
                config = getConfigurationByInclude(type, include);
            } else {
                config = getConfigurationByBody(type, parser);
            }

            if (config == null) {
                throw new IllegalArgumentException("Configuration could not be retrieved [" + parser.getLocation() + "]");
            }
        }
        parser.nextTag();
        return config;
    }

    private static Configuration<?> getConfigurationByBody(final ConfigurationType type, final XMLStreamReader parser)
            throws XMLStreamException {
        parser.next();
        final String text = parser.getText();
        return Configuration.initByString(type, text);
    }

    private static Configuration<?> getConfigurationByInclude(final ConfigurationType type, final String include) {
        if (include.startsWith("class://")) {
            final String classpath = include.replaceFirst("^class://", "");
            return Configuration.initByStream(type, type.getClass().getResourceAsStream(classpath));
        }
        throw new IllegalArgumentException("Include reffers to an invalid location!");
    }
}
