/*
 * HtmlDumper.java -- Dumps an HTML document tree. 
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

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Simple HtmlVisitor which dumps out the document to the specified
 * output stream.
 *
 * @author Brian Goetz, Quiotix
 */

public class HtmlDebugDumper extends HtmlVisitor {
    protected PrintWriter out;

    /** Constructor. */
    public HtmlDebugDumper(OutputStream os) {
        out = new PrintWriter(os);
    }

    public void finish() {
        out.flush();
    }

    public void visit(HtmlDocument.Tag t) {
        out.print("Tag(" + t + ")");
    }

    public void visit(HtmlDocument.EndTag t) {
        out.print("Tag(" + t + ")");
    }

    public void visit(HtmlDocument.Comment c) {
        out.print("Comment(" + c + ")");
    }

    public void visit(HtmlDocument.Text t) {
        out.print(t);
    }

    public void visit(HtmlDocument.Newline n) {
        out.println("-NL-");
    }

    public void visit(HtmlDocument.Annotation a) {
        out.print(a);
    }

    public void visit(HtmlDocument.TagBlock bl) {
        out.print("<BLOCK>");
        visit(bl.startTag);
        visit(bl.body);
        visit(bl.endTag);
        out.print("</BLOCK>");
    }

    /**
     * Runnable.
     */
    public static void main(String[] args) throws ParseException {
        HtmlParser parser = new HtmlParser(System.in);
        HtmlDocument doc = parser.HtmlDocument();
        doc.accept(new HtmlDebugDumper(System.out));
    }
}






