package net.xy.jcms;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.configurations.parser.TranslationParser;

import org.junit.Assert;
import org.junit.Test;

public class TranslationParserTest {

    @Test
    public void parseXml() {
        List<TranslationRule> rules = null;
        try {
            rules = TranslationParser.parse(this.getClass().getResourceAsStream("ExampleTranslationRules.xml"));
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(rules);
    }
}
