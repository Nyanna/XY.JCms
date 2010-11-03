package net.xy.jcms;

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.configurations.parser.TranslationParser;

import org.junit.Assert;
import org.junit.Test;

public class TranslationParserTest {

    @Test
    public void parseXml() {
        TranslationRule[] rules = null;
        try {
            rules = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("net/xy/jcms/ExampleTranslationRules.xml"));
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(rules);
    }
}
