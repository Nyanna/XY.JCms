package net.xy.jcms;

import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.MessageConfiguration;
import net.xy.jcms.controller.usecase.IController;
import net.xy.jcms.shared.IDataAccessContext;

import org.junit.Assert;

public class MockController implements IController {

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration) {
        Assert.assertTrue(configuration[0] instanceof MessageConfiguration);
        final MessageConfiguration mess = (MessageConfiguration) configuration[0];
        if (mess != null) {

        }
        return new NALKey("subcategory");
    }

    @Override
    public NALKey invoke(final IDataAccessContext dac, final Configuration<?>[] configuration,
            final Map<Object, Object> parameters) {
        Assert.assertTrue(configuration[0] instanceof MessageConfiguration);
        final MessageConfiguration mess = (MessageConfiguration) configuration[0];
        if (mess != null) {

        }
        return new NALKey("subcategory");
    }
}
