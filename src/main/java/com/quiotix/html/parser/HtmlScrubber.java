/*
 * HtmlScrubber.java -- cleans up HTML document tree.  
 * Copyright (C) 1999 Quiotix Corporation.  
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as 
 * published by the Free Software Foundation.  
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License (http://www.gnu.org/copyleft/gpl.txt)
 * for more details.
 */

package com.quiotix.html.parser;
import java.util.Iterator;

/**
 * HtmlScrubber is a Visitor which walks an HtmlDocument and cleans it up.
 * It can change tags and tag attributes to uppercase or lowercase, strip
 * out unnecessary quotes from attribute values, and strip trailing spaces
 * before a newline.
 *
 * @author Brian Goetz, Quiotix
 * Additional contributions by: Thorsten Weber
 */

public class HtmlScrubber extends HtmlVisitor {

    public static final int TAGS_UPCASE     = 1;
    public static final int TAGS_DOWNCASE   = 2;
    public static final int ATTR_UPCASE     = 4;
    public static final int ATTR_DOWNCASE   = 8;
    public static final int STRIP_QUOTES    = 16;
    public static final int TRIM_SPACES     = 32;
    public static final int QUOTE_ATTRS     = 64;
    public static final int DEFAULT_OPTIONS =
            TAGS_DOWNCASE | ATTR_DOWNCASE | STRIP_QUOTES;

    protected int flags;
    protected HtmlDocument.HtmlElement previousElement;
    protected boolean inPreBlock;

    /** Create an HtmlScrubber with the default options (downcase tags and
     * tag attributes, strip out unnecessary quotes.)
     */
    public HtmlScrubber() {
        this(DEFAULT_OPTIONS);
    };

    /** Create an HtmlScrubber with the desired set of options.
     * @param flags A bitmask representing the desired scrubbing options
     */

    public HtmlScrubber(int flags) {
        this.flags = flags;
    };

    private static boolean safeToUnquote(String qs) {
        int upperCount=0, lowerCount=0, idCount=0;

        for (int i=1; i < qs.length()-1; i++) {
            char c = qs.charAt(i);
            if (Character.isUnicodeIdentifierPart(c))
                ++idCount;
            if (Character.isUpperCase(c))
                ++upperCount;
            else if (Character.isLowerCase(c))
                ++lowerCount;
        };
        return (qs.length()-2 > 0
                && (qs.length()-2 == idCount
                && (upperCount == 0 || lowerCount == 0)));
    };

    private static boolean isSingleQuoted(String s) {
      if (s.charAt(0) =='\'' && s.charAt(s.length()-1) == '\'') {
        return true;
      }
      else return false;
        
    }
    private static boolean isDoubleQuoted(String s) {
      if (s.charAt(0) =='"' && s.charAt(s.length()-1) == '"') {
        return true;
      }
      else return false;
        
    }
    private static boolean isQuoted(String s) {
      return isDoubleQuoted(s) || isSingleQuoted(s);
    }

    public void start() {
        previousElement = null;
        inPreBlock = false;
    };

    public void visit(HtmlDocument.Tag t) {
        if ((flags & TAGS_UPCASE) != 0)
            t.tagName = t.tagName.toUpperCase();
        else if ((flags & TAGS_DOWNCASE) != 0)
            t.tagName = t.tagName.toLowerCase();
        for (Iterator it=t.attributeList.attributes.iterator(); it.hasNext(); ) {
            HtmlDocument.Attribute a = (HtmlDocument.Attribute) it.next();
            if ((flags & ATTR_UPCASE) != 0)
                a.name = a.name.toUpperCase();
            else if ((flags & ATTR_DOWNCASE) != 0)
                a.name = a.name.toLowerCase();
            if (((flags & STRIP_QUOTES) != 0)
                && a.hasValue
                && isQuoted(a.value)
                && safeToUnquote(a.value)) {
              a.value = a.value.substring(1, a.value.length()-1);
            }
            if (((flags & QUOTE_ATTRS) != 0)
                && a.hasValue) {
              if (!isDoubleQuoted(a.value)) {
                if (isSingleQuoted(a.value)) {
                  a.value = a.value.substring(1, a.value.length()-1);
                } 
                a.value = "\"" + a.value + "\"";
              }
              //System.err.println(a.value);
            }
        }

        previousElement = t;
    }

    public void visit(HtmlDocument.EndTag t) {
        if ((flags & TAGS_UPCASE) != 0)
            t.tagName = t.tagName.toUpperCase();
        else if ((flags & TAGS_DOWNCASE) != 0)
            t.tagName = t.tagName.toLowerCase();

        previousElement = t;
    }

    public void visit(HtmlDocument.Text t)        {
        if (((flags & TRIM_SPACES) != 0)
                && !inPreBlock
                && (previousElement instanceof HtmlDocument.Newline
                || previousElement instanceof HtmlDocument.Tag
                || previousElement instanceof HtmlDocument.EndTag
                || previousElement instanceof HtmlDocument.Comment)) {
            int i;
            for (i=0; i<t.text.length(); i++)
                if (t.text.charAt(i) != ' '
                        && t.text.charAt(i) != '\t')
                    break;
            if (i > 0)
                t.text = t.text.substring(i);
        };
        previousElement = t;
    }

    public void visit(HtmlDocument.Comment c)     { previousElement = c; }
    public void visit(HtmlDocument.Newline n)     { previousElement = n; }
    public void visit(HtmlDocument.Annotation a)  { previousElement = a; }
    public void visit(HtmlDocument.TagBlock bl) {
        if (bl.startTag.tagName.equalsIgnoreCase("PRE")
                || bl.startTag.tagName.equalsIgnoreCase("SCRIPT")
                || bl.startTag.tagName.equalsIgnoreCase("STYLE")) {
            inPreBlock = true;
            super.visit(bl);
            inPreBlock = false;
        }
        else
            super.visit(bl);
    }
}


