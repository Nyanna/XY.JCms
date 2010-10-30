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
package net.xy.jcms.shared.adapter;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.apache.log4j.Logger;
import net.xy.jcms.shared.IOutWriter;

public class ServletOutputStreamAdapter implements IOutWriter {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(ServletOutputStreamAdapter.class);

    private final ServletOutputStream outStream;

    @Override
    public void append(final StringBuilder buffer) {
        try {
            outStream.print(buffer.toString());
        } catch (final IOException e) {
            LOG.equals(e);
        }
    }

    public ServletOutputStreamAdapter(final ServletOutputStream outStream) {
        this.outStream = outStream;
    }

}
