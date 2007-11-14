/**
 * 
 */
package com.quiotix.html.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.quiotix.html.parser.HtmlDocument.Attribute;

/**
 * A runnable class intended to produce readable, sparse html 
 * from formatted pages. 
 * 
 * @author timp
 */
public class HtmlStripper extends HtmlDumper {

  protected static Set html1BlockTags = new HashSet();
  protected static Set html1EmptyTags = new HashSet();
  protected static Set html1Tags = new HashSet();
  protected static String[] html1EmptyTagStrings
      = {"AREA", "BASE", "BASEFONT", "BR", "COL", "HR", "IMG", "INPUT",
         "ISINDEX", "LINK", "META", "PARAM", "NEXTID", "PLAINTEXT",};
  protected static String[] html1BlockTagStrings
  = {"A", "ADDRESS", "B", "BLOCKQUOTE", "BODY", "CITE", "CODE", 
     "DD", "DFN", "DIR", "DL", "DT", "EM", 
     "H1", "H2", "H3", "H4", "H5", "H6", 
     "HEAD", "HTML", "I", "KBD", "KEY", "LI", 
     "LISTING", "MENU", "OL", 
     "P",  "PRE", "SAMP", 
     "STRONG", "TITLE", "TT", "U", "UL", 
     "VAR", "XMP"}; 

  protected static String[] html4BlockTagStrings
  = {"A", "ADDRESS", "B", "BLOCKQUOTE", "BODY", "CITE", "CODE", 
     "DD", "DFN", "DIR", "DL", "DT", "EM", 
     "H1", "H2", "H3", "H4", "H5", "H6", 
     "HEAD", "HTML", "I", "KBD", "KEY", "LI", 
     "LISTING", "MENU", "OL", 
     "P",  "PRE", "SAMP", 
     "STRONG", "TITLE", "TABLE", "TR", "TH", "TD", "TT", "U", "UL", 
     "VAR", "XMP"}; 


  static {
    for (int i = 0; i < html1EmptyTagStrings.length; i++)
      html1BlockTags.add(html1EmptyTagStrings[i]);
  };
  static {
    for (int i = 0; i < html4BlockTagStrings.length; i++)
      html1BlockTags.add(html4BlockTagStrings[i]);
  };
  static {
    for (int i = 0; i < html1EmptyTagStrings.length; i++)
      html1Tags.add(html1EmptyTagStrings[i]);
    for (int i = 0; i < html4BlockTagStrings.length; i++)
      html1Tags.add(html4BlockTagStrings[i]);
  };


  /**
   * @param os
   */
  public HtmlStripper(OutputStream os) {
    super(os);
  }

  /**
   * @param os
   * @param encoding
   * @throws UnsupportedEncodingException
   */
  public HtmlStripper(OutputStream os, String encoding)
      throws UnsupportedEncodingException {
    super(os, encoding);
  }

  
  public void visit(HtmlDocument.TagBlock tagBlock)  {
    if(tagBlock.startTag.tagName.toUpperCase().equals("STYLE")) {
    } else if(noButSpace(tagBlock.text())) {
    } else if(!html1BlockTags.contains(tagBlock.startTag.tagName.toUpperCase())) { 
      visit(tagBlock.body);
    } else { 
      super.visit(tagBlock);
    }
  }
  
  public void visit(HtmlDocument.Tag t) {
    if(html1Tags.contains(t.tagName.toUpperCase())){
      StringBuffer s = new StringBuffer();
      s.append("<");
      s.append(t.tagName);
      if (!t.tagName.toUpperCase().equals("HTML")) {
        for (Iterator iterator = t.attributeList.attributes.iterator(); iterator.hasNext();) {
          Attribute attribute = (Attribute) iterator.next();
          if (!attribute.name.toUpperCase().equals("STYLE")) {
            if (!attribute.name.toUpperCase().equals("CLASS") && 
                !attribute.name.toUpperCase().equals("MSONORMAL")) {
              s.append(" ");
              s.append(attribute.toString());
            }
          }
        }
      }
      if (t.emptyTag) s.append("/");
      s.append(">");
      out.print(s.toString());
    }
  }

  public void visit(HtmlDocument.Comment comment)  {
    //System.err.println("in comment" + comment.comment);
  }
  
  boolean noButSpace(String in) {
    String s = in.toLowerCase();
    boolean plausible = true;
    while (plausible)  
      if (s.startsWith("&nbsp;"))
        s = s.substring(6);
      else if (s.startsWith(" ")) 
        s = s.substring(1);
      else
        plausible = false;
    return s.equals("");
  }

  /**
   * Runnable.
   */
  public static void main(String[] args) throws Exception {
    InputStream r = new FileInputStream(args[0]);

    try {
      HtmlDocument document = new HtmlParser(r).HtmlDocument();
        int scrubberFlags = HtmlScrubber.DEFAULT_OPTIONS 
                          | HtmlScrubber.TRIM_SPACES
                          | HtmlScrubber.QUOTE_ATTRS;

        document.accept(new HtmlScrubber(scrubberFlags));
        document.accept(new HtmlCollector());
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        document.accept(new HtmlStripper(out));
        InputStream r2 = new ByteArrayInputStream(out.toByteArray());
        HtmlDocument document2 = new HtmlParser(r2).HtmlDocument();
        document2.accept(new HtmlCollector());
        HtmlFormatter formatter = new HtmlFormatter(System.out);
        formatter.setRightMargin(60);
        formatter.setIndent(1);
        document2.accept(formatter);
        
        
    } finally {
        r.close();
    };
};
}
