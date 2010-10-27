package net.xy.jcms;

import net.xy.jcms.controller.UsecaseConfiguration;

import org.junit.Before;

public class UsecaseConfigurationTest {

    @Before
    public void setup() {
        UsecaseConfiguration.setUsecaseAdapter(new MockUsecaseConfiguration());
    }

}
