package net.xy.jcms;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.FragmentConfiguration;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.configurations.RenderKitConfiguration;
import net.xy.jcms.controller.configurations.TemplateConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration;
import net.xy.jcms.portal.components.TextComponent;
import net.xy.jcms.portal.renderer.ITextRenderer;
import net.xy.jcms.portal.renderer.TextRenderer;
import net.xy.jcms.shared.AbstractFragment;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.IOutWriter;
import net.xy.jcms.shared.IRenderer;

public class ViewRunnerTest {

    public static class TestFragement extends AbstractFragment {

        @Override
        public void render(final IOutWriter out, final FragmentConfiguration config) {
            out.append(new StringBuilder("first test: "));
            config.renderChild("testus", out);
        }

        @Override
        public FragmentConfiguration getConfiguration() {
            return new FragmentConfiguration(this) {

                @Override
                protected String[] prepareTemplates(final ContentRepository repository) {
                    return null;
                }

                @Override
                protected Map<String, Class<?>> prepareContent() {
                    return null;
                }

                @Override
                protected ComponentConfiguration[] prepareChildren(final ContentRepository repository) {
                    addComponent("testus", TextComponent.getInstance());
                    return null;
                }
            };
        }
    }

    public static class ConsoleWriter implements IOutWriter {

        @Override
        public void append(final StringBuilder buffer) {
            System.out.append(buffer);
        }

    }

    @Test
    public void testViewRunner() {
        final Configuration<?>[] test = new Configuration<?>[] { new TemplateConfiguration(new HashMap<String, IFragment>() {
            private static final long serialVersionUID = -4045340286970438413L;

            {
                put("root", new TestFragement());
            }
        }), new UIConfiguration(new Properties() {
            private static final long serialVersionUID = -1203791194278285716L;

            {
                put("testus", "steronus");
            }
        }), new MessageConfiguration(new Properties() {
            private static final long serialVersionUID = -9001849818985539140L;

            {
                put("text", "steronus1");
            }
        }), new RenderKitConfiguration(new HashMap<String, IRenderer>() {
            private static final long serialVersionUID = 8311301144389878165L;

            {
                put(ITextRenderer.class.getSimpleName(), new TextRenderer());
            }
        }) };

        final ComponentConfiguration result = ViewRunner.runConfiguration(test);
        System.out.append(result.toString() + "\n");
        ViewRunner.runView(new ConsoleWriter(), result);
    }
}
