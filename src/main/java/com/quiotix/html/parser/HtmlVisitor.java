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
    /** Visit a Tag. */
    public void visit(HtmlDocument.Tag t) {
    }

    /** Visit an EndTag. */
    public void visit(HtmlDocument.EndTag t) {
    }

    /** Visit a Comment. */
    public void visit(HtmlDocument.Comment c) {
    }

    /** Visit Text. */
    public void visit(HtmlDocument.Text t) {
    }

    /** Visit a Newline. */
    public void visit(HtmlDocument.Newline n) {
    }

    /** Visit an Annotation. */
    public void visit(HtmlDocument.Annotation a) {
    }

    /** Visit a TagBlock. */
    public void visit(HtmlDocument.TagBlock bl) {
        bl.startTag.accept(this);
        visit(bl.body);
        bl.endTag.accept(this);
    }

    /** Visit an ElementSequence. */
    public void visit(HtmlDocument.ElementSequence s) {
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            htmlElement.accept(this);
        }
    }

    /** Visit an HtmlDocument. */
    public void visit(HtmlDocument d) {
        start();
        visit(d.elements);
        finish();
    }


    /** Start. */
    public void start() {
    }

    /** Finish. */
    public void finish() {
    }
}

