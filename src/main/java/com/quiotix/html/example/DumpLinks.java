package com.quiotix.html.example;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import com.quiotix.html.parser.HtmlDocument;
import com.quiotix.html.parser.HtmlParser;
import com.quiotix.html.parser.HtmlVisitor;
import com.quiotix.html.parser.ParseException;
import com.quiotix.html.parser.HtmlDocument.Attribute;

/** 
 * Example visitor to dump out the links from an HTML document.
 *
 * @author Brian Goetz, Quiotix
 */

public class DumpLinks extends HtmlVisitor {

  protected PrintWriter out;

  /**
   * Constructor.
   * 
   * @param os OutputStream to dump to
   */
  public DumpLinks(OutputStream os)     { out = new PrintWriter(os); }

  /**
   * Constructor.
   * 
   * @param os OutputStream to dump to
   */
  public DumpLinks(OutputStream os, String encoding)
    throws UnsupportedEncodingException {
    out = new PrintWriter( new OutputStreamWriter(os, encoding) );
  }

  public void finish()                   { out.flush();               }

  public void visit(HtmlDocument.Tag t) { 
    if (t.tagName.equalsIgnoreCase("A")) {
      for (Iterator i=t.attributeList.attributes.iterator(); i.hasNext(); ) {
        Attribute a = (Attribute) i.next();
        if (a.name.equalsIgnoreCase("HREF"))
          System.out.println(deQuote(a.value));
      }
    }
  }

  /**
   * Remove quotes from a String if it starts and ends with one. 
   * 
   * @param s String to remove quotes from
   * @return the input with starting and ending quotes removed
   */
  public static String deQuote(String s) {
    if (s.startsWith("\"") && s.endsWith("\""))
      return s.substring(1, s.length() - 1);
    else
      return s;
  }

  /**
   * Runnable.
   */
  public static void main (String args[]) throws ParseException, IOException {
    HtmlDocument document;

    document = new HtmlParser(System.in).HtmlDocument();
    document.accept(new DumpLinks(System.out));
  }
}

