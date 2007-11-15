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
 * A Character sequence has a length. 
 * 
 * @author timp
 * @since 15 Nov 2007
 *
 */
public interface Sized {
    /**
     * @return the number of characters
     */
    int getLength();
}
