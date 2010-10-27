package net.xy.jcms;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.usecase.IController;
import net.xy.jcms.shared.IDataAccessContext;

import org.junit.Assert;

public class TestController implements IController {

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration) {
        Assert.assertTrue(configuration[0] instanceof MessageConfiguration);
        final MessageConfiguration mess = (MessageConfiguration) configuration[0];
        return new NALKey("subcategory");
    }
}
