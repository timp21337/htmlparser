package com.quiotix.html.example;
import java.io.*;

import com.quiotix.html.parser.*;


/**
 * Example program to pretty-print an HTML document.
 * Part of the Quiotix Html Parser package.  
 * See http://www.quiotix.com/opensource/html-parser for more information
 */

public class HtmlFormat {
  public static void main (String args[]) throws IOException {
    boolean compress=false, format=false;
    int i, rightMargin=-1, indentIncrement=-1;
    InputStream r;
    HtmlDocument document;
    HtmlFormatter v;

    for (i=0; i<args.length; i++) {
      if (!args[i].startsWith("-"))
        break;
      if (args[i].equals("-compress")) { 
        compress = true;
        format = false;
      }
      else if (args[i].equals("-format")) {
        compress = false;
        format = true;
      }
      else if (args[i].equals("-indent")
               && i+1 < args.length) {
        compress = false;
        format = true;
        indentIncrement = (int) Integer.parseInt(args[i+1]);
        i++;
      }
      else if (args[i].equals("-margin")
               && i+1 < args.length) {
        rightMargin = (int) Integer.parseInt(args[i+1]);
        i++;
      };
    };

    for (; i < args.length; i++) { 
      r = new FileInputStream(args[i]);
    
      try { 
        document = new HtmlParser(r).HtmlDocument();
        if (compress) {
          document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS 
                                           | HtmlScrubber.TRIM_SPACES));
          document.accept(new HtmlDumper(System.out));
        }
        else if (format) {
          document.accept(new HtmlCollector());
          document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS 
                                           | HtmlScrubber.TRIM_SPACES));
          v = new HtmlFormatter(System.out);
          if (rightMargin != -1)     v.setRightMargin(rightMargin);
          if (indentIncrement != -1) v.setIndent(indentIncrement);
          document.accept(v);
        }
        else {
          document.accept(new HtmlCollector());
          document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS 
                                           | HtmlScrubber.TRIM_SPACES));
          v = new HtmlFormatter(System.out);
          v.setRightMargin(1024);
          v.setIndent(0);
          document.accept(v);
        };
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
