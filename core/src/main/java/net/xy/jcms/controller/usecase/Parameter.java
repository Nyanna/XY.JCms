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
package net.xy.jcms.controller.usecase;

import net.xy.jcms.persistence.usecase.ParameterDTO;

/**
 * object describing the input parameters originated to the clients request.
 * 
 * @author Xyan
 * 
 */
final public class Parameter implements Comparable<Parameter> {
    /**
     * id or key of the parameter
     */
    private final String parameterKey;

    /**
     * the type of this parameters value, can be an primitive or an complex
     * type like contentType as an classpath.
     */
    private final String parameterType;

    /**
     * default constructor
     * 
     * @param parameterKey
     * @param parameterType
     * @param mendatory
     */
    public Parameter(final String parameterKey, final String parameterType) {
        if (parameterKey == null || parameterType == null) {
            throw new IllegalArgumentException("Parameters can't be null.");
        }
        this.parameterKey = parameterKey;
        this.parameterType = parameterType;
    }

    /**
     * returns the parameter name
     * 
     * @return value
     */
    public String getParameterKey() {
        return parameterKey;
    }

    /**
     * returns the type of the parameters value as classpath
     * 
     * @return value
     */
    public String getParameterType() {
        return parameterType;
    }

    @Override
    public String toString() {
        return "key=" + getParameterKey() + " type=" + getParameterType();
    }

    /**
     * method converting this parameter to an dto
     * 
     * @return dto
     */
    public ParameterDTO toDTO() {
        final ParameterDTO dto = new ParameterDTO();
        dto.setParameterKey(parameterKey);
        dto.setParameterType(parameterType);
        return dto;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        final Parameter oo = (Parameter) obj;
        return parameterKey.equals(oo.parameterKey) &&
                   parameterType.equals(oo.parameterType);
    }

    @Override
    public int hashCode() {
        int hash = 348;
        hash = hash * 3 + parameterKey.hashCode();
        hash = hash * 3 + parameterType.hashCode();
        return hash;
    }

    @Override
    public int compareTo(final Parameter o) {
        return parameterKey.compareTo(o.parameterKey);
    }
}