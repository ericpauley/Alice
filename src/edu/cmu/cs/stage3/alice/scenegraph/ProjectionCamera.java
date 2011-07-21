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

/**
 * @author Dennis Cosgrove
 */
public class ProjectionCamera extends Camera {
	public static final Property PROJECTION_PROPERTY = new Property( ProjectionCamera.class, "PROJECTION" );
	private javax.vecmath.Matrix4d m_projection = null;

	/**
	 * @see #setProjection
	 */
	
	public javax.vecmath.Matrix4d getProjection() {
		return m_projection;
	}
	/**
	 * sets the projection property.<br>
	 * @see #getProjection
	 */
	public void setProjection( javax.vecmath.Matrix4d projection ) {
		if( notequal( m_projection, projection ) ) {
			m_projection = projection;
			onPropertyChange( PROJECTION_PROPERTY );
		}
	}
}