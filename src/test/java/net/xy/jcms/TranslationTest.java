package net.xy.jcms;

import static org.junit.Assert.*;

import net.xy.jcms.controller.NavigationAbstractionLayer;
import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;

import org.junit.Test;

public class TranslationTest {

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
