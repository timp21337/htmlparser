package com.quiotix.html.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.quiotix.html.parser.HtmlDocument;
import com.quiotix.html.parser.HtmlDumper;
import com.quiotix.html.parser.HtmlParser;
import com.quiotix.html.parser.HtmlScrubber;

/**
 * Simple example class which parses an HTML document, cleans it up a little
 * bit, and dumps it to standard out.  Demonstrates use of the parser and 
 * parser utilities.  
 *
 * Syntax: HtmlParse file
 *
 * Part of the Quiotix Html Parser package.  
 * See http://www.quiotix.com/opensource/html-parser for more information
 */

public class HtmlParse {

  /**
   * Runnable.
   */
  public static void main (String args[]) throws IOException {
    InputStream r;
    HtmlDocument document;

    for (int i=0; i < args.length; i++) { 
      r = new FileInputStream(args[i]);
    
      try { 
        document = new HtmlParser(r).HtmlDocument();
        document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS 
                                         | HtmlScrubber.TRIM_SPACES));
        document.accept(new HtmlDumper(System.out));
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        r.close();
      };
    };
    
  }
}
