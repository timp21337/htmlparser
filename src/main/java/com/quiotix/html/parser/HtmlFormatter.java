/*
 * HtmlFormatter.java -- HTML document pretty-printer
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

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * HtmlFormatter is a Visitor which traverses an HtmlDocument, dumping the
 * contents of the document to a specified output stream.  It assumes that
 * the documents has been preprocessed by HtmlCollector (which matches up
 * beginning and end tags) and by HtmlScrubber (which formats tags in a
 * consistent way).  In particular, HtmlScrubber should be invoked with the
 * TRIM_SPACES option to remove trailing spaces, which can confuse the
 * formatting algorithm.
 * <p/>
 * <P>The right margin and indent increment can be specified as properties.
 *
 * @author Brian Goetz, Quiotix
 * @see com.quiotix.html.parser.HtmlVisitor
 * @see com.quiotix.html.parser.HtmlCollector
 * @see com.quiotix.html.parser.HtmlScrubber
 */

public class HtmlFormatter extends HtmlVisitor {
    protected MarginWriter out;
    protected int rightMargin = 80;
    protected int indentSize = 2;
    protected static Set tagsIndentBlock = new HashSet();
    protected static Set tagsNewlineBefore = new HashSet();
    protected static Set tagsPreformatted = new HashSet();
    protected static Set tagsTryMatch = new HashSet();
    protected static final String[] tagsIndentStrings
            = {"TABLE", "TR", "TD", "TH", "FORM", "HTML", "HEAD", "BODY", "SELECT"};
    protected static final String[] tagsNewlineBeforeStrings
            = {"P", "H1", "H2", "H3", "H4", "H5", "H6", "BR"};
    protected static final String[] tagsPreformattedStrings
            = {"PRE", "SCRIPT", "STYLE"};
    protected static final String[] tagsTryMatchStrings
            = {"A", "TD", "TH", "TR", "I", "B", "EM", "FONT", "TT", "UL"};

    static {
        for (int i = 0; i < tagsIndentStrings.length; i++)
            tagsIndentBlock.add(tagsIndentStrings[i]);
        for (int i = 0; i < tagsNewlineBeforeStrings.length; i++)
            tagsNewlineBefore.add(tagsNewlineBeforeStrings[i]);
        for (int i = 0; i < tagsPreformattedStrings.length; i++)
            tagsPreformatted.add(tagsPreformattedStrings[i]);
        for (int i = 0; i < tagsTryMatchStrings.length; i++)
            tagsTryMatch.add(tagsTryMatchStrings[i]);
    };
    protected TagBlockRenderer blockRenderer = new TagBlockRenderer();
    protected HtmlDocument.HtmlElement previousElement;
    protected boolean inPreBlock;

    public HtmlFormatter(OutputStream os) throws Exception {
        out = new MarginWriter(new PrintWriter(new BufferedOutputStream(os)));
        out.setRightMargin(rightMargin);
    };

    public void setRightMargin(int margin) {
        rightMargin = margin;
        out.setRightMargin(rightMargin);
    };

    public void setIndent(int indent) {
        indentSize = indent;
    };

    public void visit(HtmlDocument.TagBlock block) {
        boolean indent;
        boolean preformat;
        int wasMargin = 0;

        if (tagsTryMatch.contains(block.startTag.tagName.toUpperCase())) {
            blockRenderer.start();
            blockRenderer.setTargetWidth(out.getRightMargin() - out.getLeftMargin());
            blockRenderer.visit(block);
            blockRenderer.finish();
            if (!blockRenderer.hasBlownTarget()) {
                out.printAutoWrap(blockRenderer.getString());
                previousElement = block.endTag;
                return;
            };
        };

        // Only will get here if we've failed the try-block test
        indent = tagsIndentBlock.contains(block.startTag.tagName.toUpperCase());
        preformat = tagsPreformatted.contains(block.startTag.tagName.toUpperCase());
        if (preformat) {
            inPreBlock = true;
            visit(block.startTag);
            wasMargin = out.getLeftMargin();
            out.setLeftMargin(0);
            visit(block.body);
            out.setLeftMargin(wasMargin);
            visit(block.endTag);
        } else if (indent) {
            out.printlnSoft();
            visit(block.startTag);
            out.printlnSoft();
            out.setLeftMargin(out.getLeftMargin() + indentSize);
            visit(block.body);
            out.setLeftMargin(out.getLeftMargin() - indentSize);
            out.printlnSoft();
            visit(block.endTag);
            out.printlnSoft();
            inPreBlock = false;
        } else {
            visit(block.startTag);
            visit(block.body);
            visit(block.endTag);
        };
    }

    public void visit(HtmlDocument.Tag t) {
        String s = t.toString();
        int hanging;

        if (tagsNewlineBefore.contains(t.tagName.toUpperCase())
                || out.getCurPosition() + s.length() > out.getRightMargin())
            out.printlnSoft();

        out.print("<" + t.tagName);
        hanging = t.tagName.length() + 1;
        for (Iterator it = t.attributeList.attributes.iterator(); it.hasNext();) {
            HtmlDocument.Attribute a = (HtmlDocument.Attribute) it.next();
            out.printAutoWrap(" " + a.toString(), hanging);
        };
        if (t.emptyTag) out.print("/");
        out.print(">");
        previousElement = t;
    }

    public void visit(HtmlDocument.EndTag t) {
        out.printAutoWrap(t.toString());
        if (tagsNewlineBefore.contains(t.tagName.toUpperCase())) {
            out.printlnSoft();
            out.println();
        };
        previousElement = t;
    }

    public void visit(HtmlDocument.Comment c) {
        out.print(c.toString());
        previousElement = c;
    }

    public void visit(HtmlDocument.Text t) {
        if (inPreBlock)
            out.print(t.text);
        else {
            int start = 0;
            while (start < t.text.length()) {
                int index = t.text.indexOf(' ', start) + 1;
                if (index == 0)
                    index = t.text.length();
                out.printAutoWrap(t.text.substring(start, index));
                start = index;
            };
        };
        previousElement = t;
    }

    public void visit(HtmlDocument.Newline n) {
        if (inPreBlock)
            out.println();
        else if (previousElement instanceof HtmlDocument.Tag
                || previousElement instanceof HtmlDocument.EndTag
                || previousElement instanceof HtmlDocument.Comment
                || previousElement instanceof HtmlDocument.Newline)
            out.printlnSoft();
        else if (previousElement instanceof HtmlDocument.Text)
            out.print(" ");
        previousElement = n;
    }

    public void start() {
        previousElement = null;
        inPreBlock = false;
    };

    public void finish() {
        out.flush();
    };

    public static void main(String[] args) throws Exception {
        InputStream r = new FileInputStream(args[0]);
        HtmlDocument document;

        try {
            document = new HtmlParser(r).HtmlDocument();
            document.accept(new HtmlCollector());
            document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS
                    | HtmlScrubber.TRIM_SPACES));
            document.accept(new HtmlFormatter(System.out));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            r.close();
        };
    };
}


/**
 * Utility class, used by HtmlFormatter, which adds some word-wrapping
 * and hanging indent functionality to a PrintWriter.
 */

class MarginWriter {
    protected int tabStop;
    protected int curPosition;
    protected int leftMargin;
    protected int rightMargin;
    protected java.io.PrintWriter out;
    protected char[] spaces = new char[256];

    public MarginWriter(java.io.PrintWriter out) {
        this.out = out;
        for (int i = 0; i < spaces.length; i++)
            spaces[i] = ' ';
    }

    public void flush() {
        out.flush();
    };

    public void close() {
        out.close();
    };

    public void print(String s) {
        if (curPosition == 0 && leftMargin > 0) {
            out.write(spaces, 0, leftMargin);
            curPosition = leftMargin;
        };
        out.print(s);
        curPosition += s.length();
    }

    public void printAutoWrap(String s) {
        if (curPosition > leftMargin
                && curPosition + s.length() > rightMargin)
            println();
        print(s);
    };

    public void printAutoWrap(String s, int hanging) {
        if (curPosition > leftMargin
                && curPosition + s.length() > rightMargin) {
            println();
            out.write(spaces, 0, hanging + leftMargin);
            curPosition = leftMargin + hanging;
        };
        print(s);
    };

    public void println() {
        curPosition = 0;
        out.println();
    };

    public void printlnSoft() {
        if (curPosition > 0)
            println();
    };

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    };

    public int getLeftMargin() {
        return leftMargin;
    };

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    };

    public int getRightMargin() {
        return rightMargin;
    };

    public int getCurPosition() {
        return (curPosition == 0 ? leftMargin : curPosition);
    };
}

/**
 * Utility class, used by HtmlFormatter, which tentatively tries to format
 * the contents of an HtmlDocument.TagBlock to see if the entire block can
 * fit on the rest of the line.  If it cannot, it gives up and indicates
 * failure through the hasBlownTarget method; if it can, the contents can
 * be retrieved through the getString method.
 */

class TagBlockRenderer extends HtmlVisitor {
    protected String s;
    protected boolean multiLine;
    protected boolean blownTarget;
    protected int targetWidth = 80;

    public void start() {
        s = "";
        multiLine = false;
        blownTarget = false;
    }

    public void finish() {
    };

    public void setTargetWidth(int w) {
        targetWidth = w;
    }

    public String getString() {
        return s;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public boolean hasBlownTarget() {
        return blownTarget;
    }

    public void visit(HtmlDocument.Tag t) {
        if (s.length() < targetWidth)
            s += t.toString();
        else
            blownTarget = true;
    }

    public void visit(HtmlDocument.EndTag t) {
        if (s.length() < targetWidth)
            s += t.toString();
        else
            blownTarget = true;
    }

    public void visit(HtmlDocument.Comment c) {
        if (s.length() < targetWidth)
            s += c.toString();
        else
            blownTarget = true;
    }

    public void visit(HtmlDocument.Text t) {
        if (s.length() < targetWidth)
            s += t.toString();
        else
            blownTarget = true;
    }

    public void visit(HtmlDocument.Newline n) {
        multiLine = true;
        s += " ";
    }
}


