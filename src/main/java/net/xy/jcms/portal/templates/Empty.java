package net.xy.jcms.portal.templates;

import java.util.Map;

import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.FragmentConfiguration;
import net.xy.jcms.shared.AbstractFragment;
import net.xy.jcms.shared.IOutWriter;

/**
 * an simple empty fragment as an implicite null value for filling template
 * slots
 * 
 * @author xyan
 * 
 */
public class Empty extends AbstractFragment {

    @Override
    public FragmentConfiguration getConfiguration() {
        return new FragmentConfiguration(this) {

            @Override
            protected ComponentConfiguration[] prepareChildren(final ContentRepository repository) {
                return null;
            }

            @Override
            protected String[] prepareTemplates(final ContentRepository repository) {
                return null;
            }

            @Override
            protected Map<String, Class<?>> prepareContent() {
                return null;
            }

        };
    }

    @Override
    public void render(final IOutWriter out, final FragmentConfiguration config) {
        // do nothing
    }

    /**
     * singleton pattern
     */
    private final static Empty INSTANCE = new Empty();

    public static Empty getInstance() {
        return INSTANCE;
    }

}
