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

package edu.cmu.cs.stage3.alice.core;

public class ClippingPlane extends Affector {
	private edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane m_sgClippingPlane;
	
	public edu.cmu.cs.stage3.alice.scenegraph.Affector getSceneGraphAffector() {
		return getSceneGraphClippingPlane();
	}
	public edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane getSceneGraphClippingPlane() {
		return m_sgClippingPlane;
	}
	public ClippingPlane() {
		super();
		m_sgClippingPlane = new edu.cmu.cs.stage3.alice.scenegraph.ClippingPlane();
		m_sgClippingPlane.setParent( getSceneGraphTransformable() );
		m_sgClippingPlane.setBonus( this );
	}
	
	protected void nameValueChanged( String value ) {
		super.nameValueChanged( value );
		String s = null;
		if( value!=null ) {
			s = value+".m_sgClippingPlane";
		}
		m_sgClippingPlane.setName( s );
	}
}