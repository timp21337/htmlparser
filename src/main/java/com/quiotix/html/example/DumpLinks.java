package com.quiotix.html.example;

import java.util.*;
import java.io.*;
import com.quiotix.html.parser.*;
import com.quiotix.html.parser.HtmlDocument.Attribute;
import com.quiotix.html.parser.HtmlDocument.AttributeList;

/** 
 * Example visitor to dump out the links from an HTML document.
 *
 * @author Brian Goetz, Quiotix
 */

public class DumpLinks extends HtmlVisitor {

  protected PrintWriter out;

  public DumpLinks(OutputStream os)     { out = new PrintWriter(os); }

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

  public static String deQuote(String s) {
    if (s.startsWith("\"") && s.endsWith("\""))
      return s.substring(1, s.length() - 1);
    else
      return s;
  }

  public static void main (String args[]) throws ParseException, IOException {
    HtmlDocument document;

    document = new HtmlParser(System.in).HtmlDocument();
    document.accept(new DumpLinks(System.out));
  }
}

