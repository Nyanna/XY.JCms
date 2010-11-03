package net.xy.jcms;

import java.util.Map;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseAgent.NoUsecaseFound;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.IDataAccessContext;

import org.apache.log4j.Logger;

public class JavaConfigCollector {

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(JCmsHttpServlet.class);

    /**
     * does an configuration aggregation only run and returns an dummy config list containing all requested configs
     * 
     * @param request
     * @param params
     * @param dac
     * @return
     * @throws ExecutionException
     */
    public static Configuration<?>[] getConfig(final String request, final Map<String, Object> params,
            final IDataAccessContext dac)
            throws ExecutionException {
        /**
         * DAC would be obmitted
         */

        /**
         * first convert the call string to an navigation/usecasestruct
         */
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(request, dac);

        // run the protocol adapter which fills the struct with parameters from
        // console parameters & environment vars
        // NALKey forward = fillWithParams(firstForward,parrams);
        NALKey forward = firstForward;

        Usecase usecase;
        do {
            /**
             * find the corresponding usecase
             */
            try {
                usecase = UsecaseAgent.findUsecaseForStruct(forward, dac);
            } catch (final NoUsecaseFound e) {
                LOG.error(e);
                throw new ExecutionException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another usecase.
             */
            try {
                forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
            } catch (final ClassNotFoundException ex) {
                LOG.error(ex);
                throw new ExecutionException("Couldn't load an Usecase controller");
            }
        } while (forward != null);

        /**
         * get the configurationtree for the usecase from an empty run through the componenttree
         */
        final Configuration<?>[] viewConfig = usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE);
        final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfig);
        return viewConfig;
    }

    public static void printConfigs2Console(final String request, final Map<String, Object> params,
            final IDataAccessContext dac) {
        try {
            final Configuration<?>[] configs = getConfig(request, params, dac);
            for (final Configuration<?> config : configs) {
                // TODO [HIGH] loop over dummy configs printing out the collection inforamtion
            }
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * simple wrapped exception
     * 
     * @author xyan
     * 
     */
    public static class ExecutionException extends Exception {
        public ExecutionException(final Exception e) {
            super(e);
        }

        public ExecutionException(final String string) {
            super(string);
        }

        private static final long serialVersionUID = -2856402276029349588L;

    }
}
