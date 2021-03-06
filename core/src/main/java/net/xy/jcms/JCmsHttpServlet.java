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
import java.net.URISyntaxException;
import java.text.DecimalFormat;

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
import net.xy.jcms.controller.ViewRunner;
import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.shared.adapter.HttpProtocolRequestAdapter;
import net.xy.jcms.shared.adapter.HttpProtocolResponseAdapter;
import net.xy.jcms.shared.adapter.HttpRequestDataAccessContext;
import net.xy.jcms.shared.adapter.ServletOutputStreamAdapter;
import net.xy.jcms.shared.types.Model;

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
            final long start = System.currentTimeMillis();
            // LOG.info("Execution started: " + start);
            service((HttpServletRequest) req, (HttpServletResponse) res);
            LOG.info("Execution succeeded in milliseconds "
                    + new DecimalFormat("###,###,### \u039C").format((System.currentTimeMillis() - start)));
        }
    }

    /**
     * usual http request cycle
     * 
     * @param request
     * @param response
     * @param dac
     *            if null an new one will be initialized
     * @throws ServletException
     * @throws IOException
     */
    protected void service(final HttpServletRequest request, final HttpServletResponse response,
            IDataAccessContext dac) throws ServletException, IOException {
        /**
         * first get portal configuration out from request information, or use
         * obmitted one
         */
        if (dac == null) {
            try {
                dac = new HttpRequestDataAccessContext(request);
            } catch (final URISyntaxException ex) {
                throw new ServletException("Data access context couldn't be initialized.", ex);
            }
        }

        /**
         * second convert the request to an navigation/usecasestruct
         */
        final NALKey firstForward = NavigationAbstractionLayer.translatePathToKey(dac);
        if (firstForward == null) {
            new ServletException("Request path could not be translated to an NALKey.");
        }

        // run the protocol adapter which fills the struct with parameters from
        // the request: cookie data, header data, post data
        NALKey forward = HttpProtocolRequestAdapter.apply(request, firstForward);
        NALKey cacheKey;
        Usecase usecase;
        Model configs;
        do {
            /**
             * find the corresponding usecase
             */
            try {
                usecase = UsecaseAgent.findUsecaseForStruct(forward, dac);
                cacheKey = UsecaseAgent.destinctCacheKey(usecase, forward);
            } catch (final NoUsecaseFound e) {
                LOG.error(e);
                throw new ServletException(e);
            }

            /**
             * run the controllers for the usecase, maybe redirect to another
             * usecase. there should also be an expiration contoller for http to
             * use client caching feature.
             */
            configs = usecase.getConfigurations(ConfigurationType.CONTROLLERAPPLICABLE);
            forward = UsecaseAgent
                    .executeController(usecase.getControllerList(), configs, dac, forward.getParameters());
        } while (forward != null);

        // run the protocol response adapter, which fills for http as an example
        // the headers, sets cookies
        HttpProtocolResponseAdapter.apply(response, configs.get(ConfigurationType.ControllerConfiguration));

        /**
         * at this point late caching takes effect by the safe asumption that
         * the same configuration leads to the same result. realized through
         * hashing and persistance.
         */
        final Model viewConfigs = JCmsHelper.getConfigurations(
                ConfigurationType.VIEWAPPLICABLE, configs);
        final String output = UsecaseAgent.applyCaching(viewConfigs, cacheKey, null);

        if (output != null) {
            response.getWriter().append(output);
        } else {
            /**
             * get the configurationtree for the usecase from an empty run
             * through the componenttree. give the run all view dependent
             * configs.
             */
            final ComponentConfiguration confTree = ViewRunner.runConfiguration(viewConfigs);

            /**
             * run and return the rendering tree through streamprocessing to the
             * client
             */
            final ServletOutputStreamAdapter out = new ServletOutputStreamAdapter(response.getWriter());
            ViewRunner.runView(out, confTree);

            /**
             * caches the ouput for future use
             */
            UsecaseAgent.applyCaching(viewConfigs, cacheKey, out.getBuffer().toString());
        }

    }
}
