/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

public class PickParameters {
    private int m_x;
    private int m_y;
    private boolean m_isSubElementRequired;
    private boolean m_isOnlyFrontMostRequired;

    public PickParameters( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
	    m_x = x;
	    m_y = y;
	    m_isSubElementRequired = isSubElementRequired;
	    m_isOnlyFrontMostRequired = isOnlyFrontMostRequired;
	}

    public int getX() {
	    return m_x;
	}
	public int getY() {
	    return m_y;
	}
	public boolean isSubElementRequired() {
	    return m_isSubElementRequired;
	}
	public boolean isOnlyFrontMostRequired() {
	    return m_isOnlyFrontMostRequired;
	}
}
