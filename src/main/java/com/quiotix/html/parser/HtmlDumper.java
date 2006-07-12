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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Simple HtmlVisitor which dumps out the document to the specified
 * output stream.
 *
 * @author Brian Goetz, Quiotix
 */

public class HtmlDumper extends HtmlVisitor {
    protected PrintWriter out;

    public HtmlDumper(OutputStream os) {
        out = new PrintWriter(os);
    }

    public HtmlDumper(OutputStream os, String encoding)
            throws UnsupportedEncodingException {
        out = new PrintWriter(new OutputStreamWriter(os, encoding));
    }

    public void finish() {
        out.flush();
    }

    public void visit(HtmlDocument.Tag t) {
        out.print(t);
    }

    public void visit(HtmlDocument.EndTag t) {
        out.print(t);
    }

    public void visit(HtmlDocument.Comment c) {
        out.print(c);
    }

    public void visit(HtmlDocument.Text t) {
        out.print(t);
    }

    public void visit(HtmlDocument.Newline n) {
        out.println();
    }

    public void visit(HtmlDocument.Annotation a) {
        out.print(a);
    }
}

