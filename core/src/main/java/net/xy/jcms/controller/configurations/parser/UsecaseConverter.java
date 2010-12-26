package net.xy.jcms.controller.configurations.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.pool.ControllerPool;
import net.xy.jcms.controller.usecase.Controller;
import net.xy.jcms.controller.usecase.Parameter;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.persistence.usecase.ControllerDTO;
import net.xy.jcms.persistence.usecase.ParameterDTO;
import net.xy.jcms.persistence.usecase.UsecaseDTO;
import net.xy.jcms.persistence.usecase.UsecasesDTO;

/**
 * helper class to convert usecase dto into runtime instances
 * 
 * @author Xyan
 * 
 */
public class UsecaseConverter {

    /**
     * usual converts an xml root config back into an usecase lit
     * 
     * @param cases
     * @param loader
     * @return usecases
     * @throws ClassNotFoundException
     */
    public static List<Usecase> convert(final UsecasesDTO cases, final ClassLoader loader)
            throws ClassNotFoundException {
        return convert(cases.getUsecases(), loader);
    }

    /**
     * converts an list of dto back to usecases
     * 
     * @param cases
     * @param loader
     * @return usecases
     * @throws ClassNotFoundException
     */
    public static List<Usecase> convert(final List<UsecaseDTO> cases, final ClassLoader loader)
            throws ClassNotFoundException {
        final List<Usecase> ret = new LinkedList<Usecase>();
        for (final UsecaseDTO acase : cases) {
            ret.add(convert(acase, loader));
        }
        return ret;
    }

    /**
     * converts an single dto back to its runtime counterpart
     * 
     * @param acase
     * @param loader
     * @return usecase
     * @throws ClassNotFoundException
     */
    public static Usecase convert(final UsecaseDTO acase, final ClassLoader loader)
            throws ClassNotFoundException {
        final List<Parameter> params = new ArrayList<Parameter>();
        for (final ParameterDTO param : acase.getParameterList()) {
            params.add(new Parameter(param.getParameterKey(), param.getParameterType()));
        }
        final List<Controller> controller = new LinkedList<Controller>();
        final List<ControllerDTO> ctrlList = acase.getControllerList();
        Collections.sort(ctrlList);
        for (final ControllerDTO ctrl : ctrlList) {
            final EnumSet<ConfigurationType> set = EnumSet.noneOf(ConfigurationType.class);
            set.addAll(ctrl.getObmitedConfigurations());
            controller.add(new Controller(ControllerPool.get(ctrl.getControllerInstance(), loader), set));
        }
        final ArrayList<Configuration<?>> configs = new ArrayList<Configuration<?>>();
        for (final ConfigurationDTO conf : acase.getConfigurationList()) {
            // TODO [LOW] config dto model
            configs.add(Configuration.initByString(conf.getConfigurationType(), "", loader));
        }
        return new Usecase(acase.getId(), acase.getDescription(), params.toArray(new Parameter[params.size()]),
                controller.toArray(new Controller[controller.size()]), configs.toArray(new Configuration<?>[configs.size()]));
    }
}
