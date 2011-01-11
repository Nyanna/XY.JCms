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
package net.xy.jcms.controller.translation;

import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.translation.RuleParameterDTO;
import net.xy.jcms.shared.IConverter;
import net.xy.jcms.shared.InitializableController;

import org.apache.commons.lang.StringUtils;

/**
 * specifies an parameter to which regexp group it belongs and the
 * typeconverter used.
 * 
 * @author Xyan
 * 
 */
final public class RuleParameter implements Comparable<RuleParameter> {
    /**
     * holds the name of the parameter applied for
     */
    private final String parameterName;

    /**
     * if an regexp substitution is needed stores the pattern subgroup which
     * will be transformed to an parameter
     */
    private final Integer aplicatesToGroup;

    /**
     * hold the typeconverter which converts the string value to an
     * programatic type
     */
    private final IConverter<?> converter;

    /**
     * default constructor
     * 
     * @param parameterName
     * @param aplicatesToGroup
     * @param converter
     */
    public RuleParameter(final String parameterName, final int aplicatesToGroup, final IConverter<?> converter) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("Parameter name can't be blank.");
        }
        this.parameterName = parameterName;
        this.aplicatesToGroup = aplicatesToGroup;
        this.converter = converter;
        // TODO [LOW] implement usecase params without regexp
    }

    /**
     * returns the parameters name which should be set
     * 
     * @return value
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * returns the substitution group or null
     * 
     * @return value
     */
    public Integer getAplicatesToGroup() {
        return aplicatesToGroup;
    }

    /**
     * returns the type converter or null
     * 
     * @return value
     */
    public IConverter<?> getConverter() {
        return converter;
    }

    /**
     * method to convert an ruleparam to an transfer object
     * 
     * @return dto
     */
    public RuleParameterDTO toDTO() {
        final RuleParameterDTO dto = new RuleParameterDTO();
        dto.setAplicatesToGroup(aplicatesToGroup);
        dto.setConverter(converter.getClass().getName());
        if (converter instanceof InitializableController) {
            dto.setBuildInMap(MapEntry.convert(((InitializableController<?>) converter).store()));
        }
        dto.setParameterName(parameterName);
        return dto;
    }

    /**
     * converter equals and hash is based upon classpath
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final RuleParameter oo = (RuleParameter) obj;
        return (parameterName == oo.parameterName || parameterName != null && parameterName.equals(oo.parameterName))
                &&
                (aplicatesToGroup == oo.aplicatesToGroup || aplicatesToGroup != null
                        && aplicatesToGroup.equals(oo.aplicatesToGroup))
                &&
                (converter == oo.converter || converter != null
                        && converter.getClass().getName().equals(oo.converter.getClass().getName()));
    }

    /**
     * converter equals and hash is based upon classpath
     */
    @Override
    public int hashCode() {
        int hash = 741;
        if (parameterName != null) {
            hash = hash * 3 + parameterName.hashCode();
        }
        if (aplicatesToGroup != null) {
            hash = hash * 3 + aplicatesToGroup.hashCode();
        }
        if (converter != null) {
            hash = hash * 3 + converter.getClass().getName().hashCode();
        }
        return hash;
    }

    @Override
    public int compareTo(final RuleParameter o) {
        return parameterName.compareTo(o.parameterName);
    }
}