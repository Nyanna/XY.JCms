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
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import org.apache.log4j.Logger;
import net.xy.jcms.shared.IOutWriter;

/**
 * adapts stream processing to an servlet context
 * 
 * @author xyan
 * 
 */
public class ServletOutputStreamAdapter implements IOutWriter {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(ServletOutputStreamAdapter.class);

    /**
     * outstream reference
     */
    private final ServletOutputStream outStream;

    /**
     * outwriter reference
     */
    private final PrintWriter writer;

    /**
     * internal buffer needed for caching of the complete output
     */
    private final StringBuilder internalBuffer = new StringBuilder();

    /**
     * constructor for binary output
     * 
     * @param outStream
     */
    public ServletOutputStreamAdapter(final ServletOutputStream outStream) {
        this.outStream = outStream;
        writer = null;
    }

    /**
     * constructor for writter/charachter output
     * 
     * @param writer
     */
    public ServletOutputStreamAdapter(final PrintWriter writer) {
        this.writer = writer;
        outStream = null;
    }

    @Override
    public void append(final StringBuilder buffer) {
        try {
            if (outStream != null) {
                outStream.print(buffer.toString());
                outStream.print("\n");
            }
            if (writer != null) {
                writer.append(buffer);
                writer.append("\n");
            }
            internalBuffer.append(buffer);
            internalBuffer.append("\n");
        } catch (final IOException e) {
            LOG.equals(e);
        }
    }

    @Override
    public void append(final String buffer) {
        try {
            if (outStream != null) {
                outStream.print(buffer);
                outStream.print("\n");
            }
            if (writer != null) {
                writer.append(buffer);
                writer.append("\n");
            }
            internalBuffer.append(buffer);
            internalBuffer.append("\n");
        } catch (final IOException e) {
            LOG.equals(e);
        }
    }

    /**
     * returns the buffer stored for output caching
     * 
     * @return value
     */
    public StringBuilder getBuffer() {
        return internalBuffer;
    }
}
