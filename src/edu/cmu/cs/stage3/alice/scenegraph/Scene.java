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

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Matrix44;

/**
 * the root of the scenegraph
 * @author Dennis Cosgrove
 */
public class Scene extends ReferenceFrame {
	public static final Property BACKGROUND_PROPERTY = new Property( Scene.class, "BACKGROUND" );
	private Background m_background = null;
	
	protected void releasePass1() {
		if( m_background != null ) {
			warnln( "WARNING: released scene " + this + " still has background " + m_background + "." );
			setBackground( null );
		}
		super.releasePass1();
	}
	
	public javax.vecmath.Matrix4d getAbsoluteTransformation() {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		m.setIdentity();
		return m;
	}
	
	public javax.vecmath.Matrix4d getInverseAbsoluteTransformation() {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		m.setIdentity();
		return m;
	}
	
	public Container getRoot() {
		return this;
	}

	/**
	 * @see #setBackground
	 */
	public Background getBackground() {
		return m_background;
	}
	/**
	 *
	 * @param Background (default: null)
	 * @see #getBackground
	 */
	public void setBackground( Background background ) {
		if( m_background != background ) {
			m_background = background;
			onPropertyChange( BACKGROUND_PROPERTY );
		}
	}

	
	public Matrix44 getTransformation( ReferenceFrame asSeenBy ) {
		return new Matrix44( asSeenBy.getInverseAbsoluteTransformation() );
	}
}
