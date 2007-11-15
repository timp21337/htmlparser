/*
 * Copyright (C) 2007 Tim Pizey.  
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

/**
 * An Object which can be visited by an HtmlVisitor as per the Visitor Pattern.
 * 
 * This interface is redundant as the requirement to support an 
 * <tt>accept</tt> method is already enforced by {@link HtmlElement}; however 
 * {@link HtmlDocument} is visitable but is not an {@link HtmlDocument}.  
 * 
 * @author timp
 * @since 15 Nov 2007
 *
 */
public interface Visitable {
  
  /**
   * Allow the Visitor to visit. 
   * 
   * @param v the visitor which has come to call
   */
  void accept(HtmlVisitor v);
}
