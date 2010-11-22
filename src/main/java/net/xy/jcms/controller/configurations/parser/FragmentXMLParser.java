/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.pool.ComponentPool;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.compiler.DynamicFragment;

/**
 * parses an dynamic fragment xml to an fragment instance
 * 
 * @author Xyan
 * 
 */
public class FragmentXMLParser {
    /**
     * mask instructions ass sgml/xml comments to not influence diverse dialekts
     */
    private final static String BEGIN = "<!--";
    private final static String END = "-->";

    /**
     * pattern for an component instruction
     */
    private final static Pattern COMPONENT = Pattern
            .compile("^component name=\"([a-z0-9._\\-]{3,})\" class=\"([a-z0-9._\\-]{3,})\"", Pattern.CASE_INSENSITIVE);

    /**
     * pattern for an template inclusion
     */
    private final static Pattern TEMPLATE = Pattern.compile("^template name=\"([a-z0-9._\\-]{3,})\"",
            Pattern.CASE_INSENSITIVE);

    /**
     * reads an input stream as texdata and converts to an string object
     * 
     * @param stream
     * @return
     */
    private static String getStringFromStream(final InputStream stream) {
        final StringBuilder writer = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 1024);
        final char[] buffer = new char[1024];
        try {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.append(buffer, 0, n);
            }
        } catch (final IOException e) {
        }
        return writer.toString();
    }

    /**
     * parses an dynamic fragment from an inputstream
     * 
     * @param st
     * @param loader
     *            for loading component instances
     * @return
     * @throws ClassNotFoundException
     */
    public static IFragment parse(final InputStream st, final ClassLoader loader) throws ClassNotFoundException {
        String stream = null;
        if (st != null) {
            stream = getStringFromStream(st);
        }
        if (StringUtils.isBlank(stream)) {
            throw new IllegalArgumentException("Stream could not be read or is empty");
        }

        final DynamicFragment ret = new DynamicFragment();
        int pointer = 0; // where every run begins
        do {

            final int beginPos = stream.indexOf(BEGIN, pointer);
            if (beginPos == -1) {
                // not more found
                ret.addStatic(stream.substring(pointer));
                break;
            }

            final int endPos = stream.indexOf(END, beginPos);
            if (endPos == -1) {
                // not end found
                ret.addStatic(stream.substring(pointer));
                break;
            }
            ret.addStatic(stream.substring(pointer, beginPos));
            pointer = endPos + END.length();

            final String comment = stream.substring(beginPos + BEGIN.length(), endPos).trim();
            Matcher match = COMPONENT.matcher(comment);
            if (match.matches()) {
                // found component
                ret.addChild(match.group(1).trim(), ComponentPool.get(match.group(2).trim(), loader));
            } else {
                match = TEMPLATE.matcher(comment);
                if (match.matches()) {
                    // found template
                    ret.addFragment(match.group(1).trim());
                }
            }
        } while (true);
        return ret;
    }
}
