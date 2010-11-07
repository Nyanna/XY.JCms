/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with XY.JCms. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseAgent;
import net.xy.jcms.controller.UsecaseAgent.NoUsecaseFound;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.ControllerConfiguration;
import net.xy.jcms.controller.configurations.stores.ClientStore;
import net.xy.jcms.shared.adapter.HttpProtocolRequestAdapter;
import net.xy.jcms.shared.adapter.HttpProtocolResponseAdapter;
import net.xy.jcms.shared.adapter.HttpRequestDataAccessContext;
import net.xy.jcms.shared.adapter.ServletOutputStreamAdapter;

/**
 * The following injections have to be made: ITranslationConfigurationAdapter -
 * to get translations based on dac, IUsecaseConfigurationAdapter - to get the
 * usecases based on dac
 * 
 * @author xyan
 * 
 */
public class JCmsHttpServlet extends HttpServlet {
    private static final long serialVersionUID = 8620296669723265576L;

    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(JCmsHttpServlet.class);

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            final long start = System.nanoTime();
            LOG.info("Execution started: " + start);
            service((HttpServletRequest) req, (HttpServletResponse) res);
            LOG.info("Execution succeeded in nanoseconds " + (System.nanoTime() - start));
        }
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException,
            IOException {
        /**
         * first get portal configuration out from request information
         */
        final HttpRequestDataAccessContext dac = new HttpRequestDataAccessContext(request);

        /**
         * first convert the request to an navigation/usecasestruct
         */
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(request.getRequestURI(), dac);

        // run the protocol adapter which fills the struct with parameters from
        // the request: cookie data, header data,
        // post data
        NALKey forward = HttpProtocolRequestAdapter.apply(request, firstForward);
        final ClientStore store = HttpProtocolRequestAdapter.initClientStore(request, dac);

        Usecase usecase;
        do {
            /**
             * find the corresponding usecase
             */
            try {
                usecase = UsecaseAgent.findUsecaseForStruct(forward, dac);
            } catch (final NoUsecaseFound e) {
                LOG.error(e);
                throw new ServletException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another
             * usecase. there should also be an expiration contoller for http tu
             * use client caching feature.
             */
            try {
                // sets the clientstore retrieved from protocol adapter
                final ControllerConfiguration cConfig = (ControllerConfiguration) usecase
                        .getConfiguration(ConfigurationType.ControllerConfiguration);
                cConfig.setClientStore(store);
                forward = UsecaseAgent.executeController(usecase, dac, forward.getParameters());
            } catch (final ClassNotFoundException ex) {
                LOG.error(ex);
                throw new ServletException("Couldn't load an Usecase controller");
            }
        } while (forward != null);

        // run the protocol response adapter, which fills for http as an example
        // the headers
        HttpProtocolResponseAdapter.apply(response,
                usecase.getConfigurationList(ConfigurationType.CONTROLLERAPPLICABLE));

        /**
         * at this point caching takes effect by the safe asumption that the
         * same configuration leads to the same result. realized through hashing
         * and persistance.
         */
        final String output = UsecaseAgent
                .applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), null);

        if (output != null) {
            response.getWriter().append(output);
        } else {
            /**
             * get the configurationtree for the usecase from an empty run
             * through the componenttree
             */
            final Configuration<?>[] viewConfig = usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE);
            final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfig);

            /**
             * run and return the rendering tree through streamprocessing to the
             * client
             */
            final ServletOutputStreamAdapter out = new ServletOutputStreamAdapter(response.getOutputStream());
            ViewRunner.runView(out, confTree);

            /**
             * caches the ouput for the future
             */
            UsecaseAgent.applyCaching(usecase.getConfigurationList(ConfigurationType.VIEWAPPLICABLE), out.getBuffer()
                    .toString());
        }

    }
}
