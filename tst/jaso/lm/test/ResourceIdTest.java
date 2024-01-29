package jaso.lm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jaso.db.ResourceId;

class ResourceIdTest {

    @Test
    void testConstruction() {
        ResourceId rid1 = new ResourceId("foo");
        assertEquals("foo", rid1.name);
        
        ResourceId rid2 = new ResourceId(null);
        assertEquals("", rid2.name);
        assertEquals(rid1.id+1, rid2.id);
    }
    
    @Test
    void testToString1() {
        ResourceId rid = new ResourceId("testToString1");
        assertEquals("testToString1", rid.name);
        String s = rid.toString();
        assertTrue(s.startsWith("ResourceId [name='testToString1', id="));
        assertTrue(s.endsWith("]"));
    }
    
    @Test
    void testToString2() {
        ResourceId rid = new ResourceId(null);
        assertEquals("", rid.name);
        String s = rid.toString();
        assertTrue(s.startsWith("ResourceId [name='', id="+rid.id+"]"));
    }


    class ResourceIdSubclass extends ResourceId {
        public ResourceIdSubclass(String name, long id) {
            super(name, id);
        }
    }


    @Test
    void testHashAndEquals() {
        ResourceId rid1 = new ResourceId("hashEquals");
        assertEquals("hashEquals", rid1.name);
        
        // same object.
        assertEquals(rid1, rid1);
        
        // does equal a null object
        assertFalse(rid1.equals(null));
        
        // does not equal an subclass
        assertFalse(rid1.equals(new ResourceIdSubclass(rid1.name, rid1.id)));
        
        
        ResourceId rid2 = new ResourceId(rid1.name, rid1.id);
        
        assertEquals(rid1, rid2);
        assertEquals(rid1.hashCode(), rid2.hashCode());       
    }
    
    @Test
    void testNotEquals() {
        ResourceId rid = new ResourceId("testNotEquals");
        assertEquals("testNotEquals", rid.name);
        
        assertFalse(rid.equals(new ResourceId("foo", rid.id)));
        assertFalse(rid.equals(new ResourceId(null,  rid.id)));
        assertFalse(rid.equals(new ResourceId(rid.name, rid.id+1)));
    }               
}
