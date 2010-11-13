/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Test;

import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.adapter.HttpRequestDataAccessContext;

public class URLTransformationTest {

    @Test
    public void testThis() throws MalformedURLException, URISyntaxException {
        final IDataAccessContext dac = new HttpRequestDataAccessContext(new HttpServletRequest() {

            @Override
            public void setCharacterEncoding(final String s) throws UnsupportedEncodingException {
            }

            @Override
            public void setAttribute(final String s, final Object obj) {
            }

            @Override
            public void removeAttribute(final String s) {
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final String s) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRealPath(final String s) {
                return null;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getProtocol() {
                return "http/ 1.1";
            }

            @Override
            public String[] getParameterValues(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public Map<String, String> getParameterMap() {
                return null;
            }

            @Override
            public String getParameter(final String s) {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 80;
            }

            @Override
            public String getLocalName() {
                return "www.testus.com";
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public Object getAttribute(final String s) {
                return null;
            }

            @Override
            public boolean isUserInRole(final String s) {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public HttpSession getSession(final boolean flag) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return "/this/got/something.do";
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return "";
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public int getIntHeader(final String s) {
                return 0;
            }

            @Override
            public Enumeration<String> getHeaders(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public String getHeader(final String s) {
                return null;
            }

            @Override
            public long getDateHeader(final String s) {
                return 0;
            }

            @Override
            public Cookie[] getCookies() {
                return null;
            }

            @Override
            public String getContextPath() {
                return "/test-web";
            }

            @Override
            public String getAuthType() {
                return null;
            }
        });

        String res;

        res = dac.buildUriWithParams("sdfhjsdfhsdh", null);
        Assert.assertEquals(res, "/test-web/sdfhjsdfhsdh");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("/sdfhjsdfhsdh", null);
        Assert.assertEquals(res, "/test-web/sdfhjsdfhsdh");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("sdfhjs dfh sdh", null);
        Assert.assertEquals(res, "/test-web/sdfhjs+dfh+sdh");
        System.out.append(res + "\n");

        final Map<Object, Object> param = new HashMap<Object, Object>();
        param.put("test", "this");
        param.put("test1", "this2");
        param.put("test", "this");

        res = dac.buildUriWithParams("sdfh jsdf hsdf h", param);
        Assert.assertEquals(res, "/test-web/sdfh+jsdf+hsdf+h?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("www.google.de", param);
        Assert.assertEquals(res, "/test-web/www.google.de?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("http://www.google.de", param);
        Assert.assertEquals(res, "http://www.google.de/?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("http://www.google.de/test", param);
        Assert.assertEquals(res, "http://www.google.de/test?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("test?pest=hoho", param);
        Assert.assertEquals(res, "/test-web/test?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("www.testus.com/test", param);
        Assert.assertEquals(res, "/test-web/www.testus.com/test?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("http://www.testus.com/test", param);
        Assert.assertEquals(res, "http://www.testus.com/test?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("http://www.testus.com/test/index.doo", param);
        Assert.assertEquals(res, "http://www.testus.com/test/index.doo?test1=this2&test=this");
        System.out.append(res + "\n");

        res = dac.buildUriWithParams("http://www.testus.com/test/index.doo?hihi", param);
        Assert.assertEquals(res, "http://www.testus.com/test/index.doo?test1=this2&test=this");
        System.out.append(res + "\n");
    }
}
