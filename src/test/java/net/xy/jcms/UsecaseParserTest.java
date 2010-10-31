package net.xy.jcms;

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;

import org.junit.Test;
import org.junit.Assert;

public class UsecaseParserTest {

    @Test
    public void tesParser() {
        Usecase[] cases = null;
        try {
            cases = UsecaseParser.parse(this.getClass().getResourceAsStream("ExampleUsecases.xml"));
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(cases);
    }
}
