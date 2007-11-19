/**
 * 
 */
package com.quiotix.html.example.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.quiotix.html.example.DumpLinks;
import com.quiotix.html.parser.HtmlDocument;
import com.quiotix.html.parser.HtmlParser;

import junit.framework.TestCase;

/**
 * @author timp
 * @since 19 Nov 2007
 *
 */
public class DumpLinksTest extends TestCase {

    /**
     * @param name
     */
    public DumpLinksTest(String name) {
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
     * Test method for {@link com.quiotix.html.example.DumpLinks#visit(com.quiotix.html.parser.HtmlDocument.Tag)}.
     */
    public void testVisitTag() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.example.DumpLinks#finish()}.
     */
    public void testFinish() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.example.DumpLinks#DumpLinks(java.io.OutputStream)}.
     */
    public void testDumpLinksOutputStream() throws Exception {
        HtmlDocument document;

        String testString = "<html><head><BODy><P class=unquoted>Hi test  " + 
        System.getProperty("line.separator") +
        "<a href='r1'>ref1<a>" +
        System.getProperty("line.separator") +
        "<a href='r2'>ref2<a>";
        InputStream r = new ByteArrayInputStream(testString.getBytes());
        OutputStream o = new ByteArrayOutputStream();

        document = new HtmlParser(r).HtmlDocument();
        document.accept(new DumpLinks(o));
        //System.err.println(o.toString());
        assertEquals("r1" + 
                System.getProperty("line.separator") + 
                "r2" +
                System.getProperty("line.separator") , 
                o.toString());
        
    }

    /**
     * Test method for {@link com.quiotix.html.example.DumpLinks#DumpLinks(java.io.OutputStream, java.lang.String)}.
     */
    public void testDumpLinksOutputStreamString() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.example.DumpLinks#main(java.lang.String[])}.
     */
    public void testMain() {
        
    }

}
