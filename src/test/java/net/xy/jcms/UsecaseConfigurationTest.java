package net.xy.jcms;

import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;

import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Parameter;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.shared.IDataAccessContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UsecaseConfigurationTest {

    @Before
    public void setup() {
        UsecaseConfiguration.setUsecaseAdapter(new MockUsecaseConfiguration());
    }

    @Test
    public void testFindUsecase() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                return null;
            }
        });
        Assert.assertNotNull(uCase);
        System.out.append(uCase.toString());
    }

    @Test
    public void testFindUsecaseNotId() {
        final NALKey key = new NALKey("contentgroupNot");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                return null;
            }
        });
        Assert.assertNull(uCase);
    }

    @Test
    public void testFindUsecaseNotParameter() {
        final NALKey key = new NALKey("contentgroupt");
        key.addParameter("contentgroupNot", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                return null;
            }
        });
        Assert.assertNull(uCase);
    }

    @Test
    public void testForwarding() throws ClassNotFoundException {
        UsecaseConfiguration.setUsecaseAdapter(new IUsecaseConfigurationAdapter() {
            @Override
            public Usecase[] getUsecaseList(final IDataAccessContext dac) {
                return new Usecase[] {
                        new Usecase("contentgroup", "first test", new Parameter[] { new Parameter("contentgroup",
                                "de.jamba.ContentGroup") }, new Controller[] { new Controller(
                                "net.xy.jcms.MockController",
                                EnumSet.of(ConfigurationType.messageConfiguration))

                        }, new Configuration[] { new MessageConfiguration(new Properties() {
                            private static final long serialVersionUID = 4127043651680566300L;
                            {
                                put("test", "test");
                            }

                        }) }), new Usecase("subcategory", "first test", new Parameter[] {}, new Controller[] {

                        }, new Configuration[] {

                        }) };
            }

        });

        // begin test
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Cars");
        final Usecase uCase = UsecaseConfiguration.findUsecaseForStruct(key, new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                return null;
            }
        });
        Assert.assertNotNull(uCase);
        final NALKey forward = UsecaseAgent.executeController(uCase, new IDataAccessContext() {

            @Override
            public String buildUriWithParams(final String path, final Map<Object, Object> parameters) {
                return null;
            }
        }, null);
        Assert.assertNotNull(forward);
        System.out.append(forward.toString());
    }

}
