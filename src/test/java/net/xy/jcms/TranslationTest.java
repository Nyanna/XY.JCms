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

import static org.junit.Assert.*;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.TranslationConfiguration;

import org.junit.Before;
import org.junit.Test;

public class TranslationTest {

    @Before
    public void setup() {
        TranslationConfiguration.setTranslationAdapter(new MockTranslationConfiguration());
    }

    @Test
    public void testFind() {
        final NALKey exspected = new NALKey("contentgroup");
        exspected.addParameter("contentgroup", "Ringtones");
        final NALKey result = NavigationAbstractionLayer.translatePathToKey("du willst wohl zu Ringtones", null);
        assertEquals(exspected.toString(), result.toString());
    }

    @Test
    public void testFind1() {
        final NALKey exspected = new NALKey("contentgroup");
        exspected.addParameter("contentgroup", "Funsounds");
        final NALKey result = NavigationAbstractionLayer.translatePathToKey("du willst wohl zu Funsounds", null);
        assertEquals(exspected.toString(), result.toString());
    }

    @Test
    public void testFind2() {
        final NALKey exspected = new NALKey("contentgroup");
        exspected.addParameter("contentgroup", "Funsounds");
        final NALKey result = NavigationAbstractionLayer.translatePathToKey("du willst wohl zu Nirgendwo", null);
        assertNull(result);
    }

    @Test
    public void testFindReverse() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Ringtones");
        final String result = NavigationAbstractionLayer.translateKeyToPath(key, null);
        assertEquals("du willst wohl zu Ringtones", result.toString());
    }

    @Test
    public void testFindReverse1() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Funsounds");
        final String result = NavigationAbstractionLayer.translateKeyToPath(key, null);
        assertEquals("du willst wohl zu Funsounds", result.toString());
    }

    @Test
    public void testFindFailure() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroup", "Videos");
        final String result = NavigationAbstractionLayer.translateKeyToPath(key, null);
        assertNull(result);
    }

    @Test
    public void testFindFailure1() {
        final NALKey key = new NALKey("contentgroup");
        key.addParameter("contentgroupNot", "Videos");
        final String result = NavigationAbstractionLayer.translateKeyToPath(key, null);
        assertNull(result);
    }

    @Test
    public void testFindFailure2() {
        final NALKey key = new NALKey("contentgroupTTT");
        key.addParameter("contentgroupNot", "Videos");
        final String result = NavigationAbstractionLayer.translateKeyToPath(key, null);
        assertNull(result);
    }

}
