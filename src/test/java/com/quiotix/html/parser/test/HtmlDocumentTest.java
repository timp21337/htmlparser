/**
 * 
 */
package com.quiotix.html.parser.test;

import com.quiotix.html.parser.HtmlDocument;

import junit.framework.TestCase;

/**
 * @author timp
 * @since 19 Nov 2007
 *
 */
public class HtmlDocumentTest extends TestCase {

    /**
     * @param name
     */
    public HtmlDocumentTest(String name) {
        super(name);
    }

    /** 
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** 
     * {@inheritDoc}
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlDocument#HtmlDocument(com.quiotix.html.parser.HtmlDocument.ElementSequence)}.
     */
    public void testHtmlDocument() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlDocument#accept(com.quiotix.html.parser.HtmlVisitor)}.
     */
    public void testAccept() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlDocument.Attribute#Attribute(String)}.
     */
    public void testAttribute() { 
        HtmlDocument.Attribute a = new HtmlDocument.Attribute("Att");
        assertEquals("Att", a.name);
        assertNull(a.value);
        assertFalse(a.hasValue);
        assertEquals(3,a.getLength());
        assertEquals("Att", a.toString());
        assertEquals("", a.getValue());
        
        a.setValue(null);
        
        assertEquals("Att", a.name);
        assertNull(a.value);
        assertFalse(a.hasValue);
        assertEquals(3,a.getLength());
        assertEquals("Att", a.toString());
        assertEquals("", a.getValue());
        
        a.setValue("1");
        
        assertEquals("Att", a.name);
        assertEquals("1",a.value);
        assertTrue(a.hasValue);
        assertEquals(5,a.getLength());
        assertEquals("Att=1", a.toString());
        assertEquals("1", a.getValue());

        a.setValue(null);
        
        assertEquals("Att", a.name);
        assertNull(a.value);
        assertFalse(a.hasValue);
        assertEquals(3,a.getLength());
        assertEquals("Att", a.toString());
        assertEquals("", a.getValue());

        a.setValue("'1'");
        
        assertEquals("Att", a.name);
        assertEquals("'1'",a.value);
        assertTrue(a.hasValue);
        assertEquals(7,a.getLength());
        assertEquals("Att='1'", a.toString());
        assertEquals("1", a.getValue());
        
        a.setValue("\"1\"");
        
        assertEquals("Att", a.name);
        assertEquals("\"1\"",a.value);
        assertTrue(a.hasValue);
        assertEquals(7,a.getLength());
        assertEquals("Att=\"1\"", a.toString());
        assertEquals("1", a.getValue());
        
    }
    
    
}
