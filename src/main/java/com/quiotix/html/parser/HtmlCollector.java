/*
 * HtmlCollector.java -- structures an HTML document tree.  
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * An HtmlVisitor which modifies the structure of the document so that
 * begin tags are matched properly with end tags and placed in TagBlock
 * elements.  Typically, an HtmlDocument is created by the parser, which
 * simply returns a flat list of elements.  The HtmlCollector takes this
 * flat list and gives it the structure that is implied by the HTML content.
 *
 * @author Brian Goetz, Quiotix
 */

public class HtmlCollector extends HtmlVisitor {

    protected ElementStack tagStack = new ElementStack();
    protected ElementStack elements;
    protected boolean collected;
    protected static Set dontMatch = new HashSet();
    protected static String[] dontMatchStrings
            = {"AREA", "BASE", "BASEFONT", "BR", "COL", "HR", "IMG", "INPUT",
               "ISINDEX", "LINK", "META", "P", "PARAM"};

    static {
        for (int i = 0; i < dontMatchStrings.length; i++)
            dontMatch.add(dontMatchStrings[i]);
    };

    private static class TagStackEntry {
        String tagName;
        int index;
    };

    private static class ElementStack extends Vector {
        ElementStack() {
            super();
        }

        ElementStack(int n) {
            super(n);
        }

        public void popN(int n) {
            elementCount -= n;
        }
    };

    protected int pushNode(HtmlDocument.HtmlElement e) {
        elements.addElement(e);
        return elements.size() - 1;
    };

    public void visit(HtmlDocument.Comment c) {
        pushNode(c);
    };

    public void visit(HtmlDocument.Text t) {
        pushNode(t);
    };

    public void visit(HtmlDocument.Newline n) {
        pushNode(n);
    };

    public void visit(HtmlDocument.Tag t) {
        TagStackEntry ts = new TagStackEntry();
        int index;

        // Push the tag onto the element stack, and push an entry on the tag
        // stack if it's a tag we care about matching
        index = pushNode(t);
        if (!t.emptyTag
                && !dontMatch.contains(t.tagName.toUpperCase())) {
            ts.tagName = t.tagName;
            ts.index = index;
            tagStack.addElement(ts);
        };
    };

    public void visit(HtmlDocument.EndTag t) {
        int i;
        for (i = tagStack.size() - 1; i >= 0; i--) {
            TagStackEntry ts = (TagStackEntry) tagStack.elementAt(i);
            if (t.tagName.equalsIgnoreCase(ts.tagName)) {
                HtmlDocument.TagBlock block;
                HtmlDocument.ElementSequence blockElements;
                HtmlDocument.Tag tag;

                // Create a new ElementSequence and copy the elements to it
                blockElements =
                        new HtmlDocument.ElementSequence(elements.size() - ts.index - 1);
                for (int j = ts.index + 1; j < elements.size(); j++)
                    blockElements.addElement((HtmlDocument.HtmlElement)
                            elements.elementAt(j));
                tag = (HtmlDocument.Tag) elements.elementAt(ts.index);
                block = new HtmlDocument.TagBlock(tag.tagName,
                        tag.attributeList, blockElements);

                // Pop the elements off the stack, push the new block
                elements.popN(elements.size() - ts.index);
                elements.addElement(block);

                // Pop the matched tag and intervening unmatched tags
                tagStack.popN(tagStack.size() - i);

                collected = true;
                break;
            };
        };

        // If we didn't find a match, just push the end tag
        if (i < 0)
            pushNode(t);
    };

    public void visit(HtmlDocument.TagBlock bl) {
        HtmlCollector c = new HtmlCollector();

        c.start();
        c.visit(bl.body);
        c.finish();
        pushNode(bl);
    }

    public void visit(HtmlDocument.ElementSequence s) {
        elements = new ElementStack(s.size());
        collected = false;

        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            htmlElement.accept(this);
        }
        if (collected)
            s.setElements(elements);
    }

    public static void main(String[] args) throws Exception {
        InputStream r = new FileInputStream(args[0]);

        try {
            HtmlDocument document = new HtmlParser(r).HtmlDocument();
            document.accept(new HtmlScrubber());
            document.accept(new HtmlCollector());
            document.accept(new HtmlDumper(System.out));
        } finally {
            r.close();
        };
    };
}

