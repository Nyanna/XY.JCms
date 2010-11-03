package net.xy.jcms;

import javax.servlet.ServletException;
import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.TranslationConfiguration;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * example servlet for initializing JCms
 * 
 * @author Xyan
 * 
 */
public class ExampleServlet extends JCmsHttpServlet {

    private static final long serialVersionUID = -4619593936279002424L;

    public void init() throws ServletException {
        super.init();
        TranslationConfiguration.setTranslationAdapter(new ITranslationConfigurationAdapter() {
            public TranslationRule[] getRuleList(final IDataAccessContext dac) {
                try {
                    return TranslationParser.parse(getClass().getResourceAsStream("ExampleTranslationRules.xml"));
                } catch (final XMLStreamException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        UsecaseConfiguration.setUsecaseAdapter(new IUsecaseConfigurationAdapter() {

            public Usecase[] getUsecaseList(final IDataAccessContext dac) {
                try {
                    return UsecaseParser.parse(getClass().getResourceAsStream("ExampleUsecases.xml"));
                } catch (final XMLStreamException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}