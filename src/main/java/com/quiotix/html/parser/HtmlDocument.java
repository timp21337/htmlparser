/*
 * HtmlDocument.java -- classes to represent HTML documents as parse trees.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an HTML document as a sequence of elements.  The defined
 * element types are: Tag, EndTag, TagBlock (matched tag..end tag, with the
 * intervening elements), Comment, Text, Newline, and Annotation.
 * <P>
 * The various element types are defined as nested classes within
 * HtmlDocument.
 * </p>
 * @author Brian Goetz, Quiotix
 * @see com.quiotix.html.parser.HtmlVisitor
 */

public class HtmlDocument implements Visitable {
    ElementSequence elements;

    /** Constructor. */
    public HtmlDocument(ElementSequence s) {
        elements = s;
    }

    public void accept(HtmlVisitor v) {
        v.visit(this);
    }

    private static String dequote(String s) {
        if (s == null)
            return "";
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            return s.substring(1, s.length()-1);
        else
            return s;
    }

    // The various elements of the HtmlDocument (Tag, EndTag, etc) are included
    // as nested subclasses largely for reasons of namespace control.
    // The following subclasses of HtmlElement exist: Tag, EndTag, Text, Comment,
    // Newline, Annotation, TagBlock.  Also, the additional classes
    // ElementSequence, Attribute, and AttributeList are defined here as well.

    // Each subclass of HtmlElement should have a visit() method in the
    // HtmlVisitor class.

    /**
     * Abstract class for HTML elements.  Enforces support for Visitors.
     */
    public static abstract class HtmlElement implements Visitable, Sized {
        public abstract void accept(HtmlVisitor v);
    }

    /**
     * HTML start tag.  Stores the tag name and a list of tag attributes.
     */
    public static class Tag extends HtmlElement {
        /** The name of the tag. */
        public String tagName;
        /** A List of the tags Attributes. */
        public AttributeList attributeList;

        /** 
         * Whether the tag has an empty content model  
         * eg the BR and HR tags.
         */
        public boolean emptyTag = false;

        /** Constructor. */
        public Tag(String t, AttributeList a) {
            tagName = t;
            attributeList = a;
        }

        /** Set Tag type to Empty. */
        public void setEmpty(boolean b) {
            emptyTag = b;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        /** Whether Tag has an Attribute with given name. */
        public boolean hasAttribute(String name) {
            return attributeList.contains(name);
        }

        /** 
         * Whether Tag has an Attribute with given name 
         * and that Attribute has a non-null value. 
         */
        public boolean hasAttributeValue(String name) {
            return attributeList.hasValue(name);
        }

        /**
         * @return the value of the Attribute with the given name or null
         */
        public String getAttributeValue(String name) {
            return attributeList.getValue(name);
        }

        public int getLength() {
            int length = 0;
            for (Iterator iterator = attributeList.attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                length += 1 + (attribute.getLength());
            }
            return length + tagName.length() + 2 + (emptyTag ? 1 : 0);
        }

        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("<");
            s.append(tagName);
            for (Iterator iterator = attributeList.attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                s.append(" ");
                s.append(attribute.toString());
            }
            if (emptyTag) s.append("/");
            s.append(">");
            return s.toString();
        }
    }

    /**
     * Html end tag.  Stores only the tag name.
     */
    public static class EndTag extends HtmlElement {

        /** The name of the Tag. */
        public String tagName;

        /** Constructor. */
        public EndTag(String t) {
            tagName = t;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 3 + tagName.length();
        }

        public String toString() {
            return "</" + tagName + ">";
        }
    }

    /**
     * A tag block is a composite structure consisting of a start tag
     * a sequence of HTML elements, and a matching end tag.
     */
    public static class TagBlock extends HtmlElement {
        /** Tag at start of Block.*/
        public Tag startTag;
        /** Tag at end of Block.*/
        public EndTag endTag;
        /** The sequance of elements which make up the body.*/
        public ElementSequence body;

        /** Constructor. */
        public TagBlock(String name, AttributeList aList, ElementSequence b) {
            startTag = new Tag(name, aList);
            endTag = new EndTag(name);
            body = b;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }
        
        public int getLength() { 
            int bodyLength = 0;
            for (Iterator iterator = body.iterator(); iterator.hasNext();) {
                HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
                bodyLength += htmlElement.getLength();    
            }
            return startTag.getLength() + bodyLength + endTag.getLength();
        }
        
        public String toString() {
          StringBuffer sb = new StringBuffer();
          sb.append(startTag.toString());
          for (Iterator iterator = body.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            sb.append(htmlElement.toString());
          }
          sb.append(endTag.toString());
          return sb.toString();
        }
        
        /**
         * @return the text within a tag block
         */
        public String text() {
          StringBuffer sb = new StringBuffer();
          for (Iterator iterator = body.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            if (htmlElement instanceof Text) {
              sb.append(htmlElement.toString());
            } else if(htmlElement instanceof TagBlock)
              sb.append(((TagBlock)htmlElement).text());
          }
          return sb.toString();
        }
    }

    /**
     * HTML comments.
     */
    public static class Comment extends HtmlElement {
        /**
         * Note that a Comment starts and ends with two hyphen characters. 
         */
        public String comment;

        /** Constructor. */
        public Comment(String c) {
            comment = c;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 3 + comment.length();
        }

        public String toString() {
            return "<!" + comment + ">";
        }
    }

    /**
     * Plain text
     */
    public static class Text extends HtmlElement {
        /** The text. */
        public String text;

        /** Constructor. */
        public Text(String t) {
            text = t;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return text.length();
        }

        public String toString() {
            return text;
        }
    }

    /**
     * End of line indicator.
     */
    public static class Newline extends HtmlElement {
        /** The system specific newline String. */
        public static final String NL = System.getProperty("line.separator");

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return NL.length();
        }

        public String toString() {
            return NL;
        }
    }

    /**
     * A sequence of HTML elements.
     */
    public static class ElementSequence {
        private List elements;

        /** Constructor. */
        public ElementSequence(int n) {
            elements = new ArrayList(n);
        }

        /** Constructor. */
        public ElementSequence() {
            elements = new ArrayList();
        }

        /** Add element to list. */
        public void addElement(HtmlElement o) {
            elements.add(o);
        }

        /**
         * @return the number of elements in this list.
         */
        public int size() {
            return elements.size();
        }

        /**
         * @return an iterator over the elements in this list in proper sequence.
         */
        public Iterator iterator() {
            return elements.iterator();
        }

        /**
         * Clear current elements and replace with given Collection.
         * 
         * @param collection to replace elements with
         */
        public void setElements(List collection) {
            elements.clear();
            elements.addAll(collection);
        }
    }

    /**
     * Annotations.  These are not part of the HTML document, but
     * provide a way for HTML-processing applications to insert
     * annotations into the document.  These annotations can be used by
     * other programs or can be brought to the user's attention at a
     * later time.  For example, the HtmlCollector might insert an
     * annotation to indicate that there is no corresponding start tag
     * for an end tag.
     */
    public static class Annotation extends HtmlElement {
        String type, text;

        /** Constructor. */
        public Annotation(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 14 + type.length() + text.length();
        }

        public String toString() {
            return "<!--NOTE(" + type + ") " + text + "-->";
        }
    }

    /**
     * A Tag Attribute.
     */
    public static class Attribute implements Sized {
        /** The name of this Attribute. */
        public String name;
        /** The value of this Attribute. */
        public String value;
        /** Whether the Attribute has a value. */
        public boolean hasValue;

        /** Constructor. */
        public Attribute(String n) {
            name = n;
            hasValue = false;
        }

        /** Constructor. */
        public Attribute(String n, String v) {
            name = n;
            value = v;
            hasValue = true;
        }

        public int getLength() {
            return (hasValue ? name.length() + 1 + value.length() : name.length());
        }

        public String toString() {
            return (hasValue ? name + "=" + value : name);
        }
    }

    /**
     * A List of Attributes.
     */
    public static class AttributeList {
        /** The backing List. */
        public List attributes = new ArrayList();

        /** Add. */
        public void addAttribute(Attribute a) {
            attributes.add(a);
        }

        /** Whether the List contains an Attribute with the given name. */
        public boolean contains(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name))
                    return true;
            }
            return false;
        }

        /** 
         * Whether the List contains an Attribute with the given name 
         * and that Attribute has a non-null value. 
         */
        public boolean hasValue(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name) && attribute.hasValue)
                    return true;
            }
            return false;
        }

        /**
         * @param name the name of the Attribute
         * @return the value of the Attribute with the given name or null
         */
        public String getValue(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name) && attribute.hasValue)
                    return dequote(attribute.value);
            }
            return null;
        }
    }
}



