/*
 * HtmlVisitor.java
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
 * Abstract class implementing Visitor pattern for HtmlDocument objects.
 *
 * @author Brian Goetz, Quiotix
 */

public abstract class HtmlVisitor {
    public void visit(HtmlDocument.Tag t) {
    }

    public void visit(HtmlDocument.EndTag t) {
    }

    public void visit(HtmlDocument.Comment c) {
    }

    public void visit(HtmlDocument.Text t) {
    }

    public void visit(HtmlDocument.Newline n) {
    }

    public void visit(HtmlDocument.Annotation a) {
    }

    public void visit(HtmlDocument.TagBlock bl) {
        bl.startTag.accept(this);
        visit(bl.body);
        bl.endTag.accept(this);
    }

    public void visit(HtmlDocument.ElementSequence s) {
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            htmlElement.accept(this);
        }
    }

    public void visit(HtmlDocument d) {
        start();
        visit(d.elements);
        finish();
    }

    public void start() {
    }

    public void finish() {
    }
}

