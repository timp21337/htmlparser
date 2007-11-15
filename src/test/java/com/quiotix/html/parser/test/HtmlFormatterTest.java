/**
 * 
 */
package com.quiotix.html.parser.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.quiotix.html.parser.HtmlCollector;
import com.quiotix.html.parser.HtmlDocument;
import com.quiotix.html.parser.HtmlFormatter;
import com.quiotix.html.parser.HtmlParser;
import com.quiotix.html.parser.HtmlScrubber;

/**
 * @author timp
 * @since 15 Nov 2007
 *
 */
public class HtmlFormatterTest extends TestCase {

    /**
     * @param name
     */
    public HtmlFormatterTest(String name) {
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
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.Tag)}.
     */
    public void testVisitTag() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.EndTag)}.
     */
    public void testVisitEndTag() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.Comment)}.
     */
    public void testVisitComment() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.Text)}.
     */
    public void testVisitText() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.Newline)}.
     */
    public void testVisitNewline() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#visit(com.quiotix.html.parser.HtmlDocument.TagBlock)}.
     */
    public void testVisitTagBlock() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#start()}.
     */
    public void testStart() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#finish()}.
     */
    public void testFinish() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#HtmlFormatter(java.io.OutputStream)}.
     */
    public void testHtmlFormatter() throws Exception {
        String testString = "<html><head><BODy><P class=unquoted>Hi test  " + 
        System.getProperty("line.separator") +
        "<p> Spaces preserved at back but not at front  ";
        InputStream r = new ByteArrayInputStream(testString.getBytes());
        OutputStream o = new ByteArrayOutputStream();
        HtmlDocument document;

        document = new HtmlParser(r).HtmlDocument();
        document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS
                    | HtmlScrubber.TRIM_SPACES));
        document.accept(new HtmlCollector());
        document.accept(new HtmlFormatter(o));
        assertEquals("<html><head><body>" + 
                System.getProperty("line.separator") + 
                "<p class=\"unquoted\">Hi test   " +
                System.getProperty("line.separator") + 
                "<p>Spaces preserved at back but not at front  " , o.toString());
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#setRightMargin(int)}.
     */
    public void testSetRightMargin() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#setIndent(int)}.
     */
    public void testSetIndent() {
        
    }

    /**
     * Test method for {@link com.quiotix.html.parser.HtmlFormatter#main(java.lang.String[])}.
     */
    public void testMain() {
    }

}
