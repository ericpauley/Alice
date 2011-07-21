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

import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundListener;

/**
 * @author Dennis Cosgrove
 */
public abstract class Geometry extends Element {
	public abstract void transform( javax.vecmath.Matrix4d trans );

	protected abstract void updateBoundingBox();
	protected abstract void updateBoundingSphere();
	protected edu.cmu.cs.stage3.math.Box m_boundingBox = null;
	protected edu.cmu.cs.stage3.math.Sphere m_boundingSphere = null;

	public edu.cmu.cs.stage3.math.Box getBoundingBox() {
		if( m_boundingBox == null ) {
			updateBoundingBox();
		}
		if( m_boundingBox != null ) {
			return (edu.cmu.cs.stage3.math.Box)m_boundingBox.clone();
		} else {
			return null;
		}
	}
	public edu.cmu.cs.stage3.math.Sphere getBoundingSphere() {
		if( m_boundingSphere == null ) {
			updateBoundingSphere();
		}
		if( m_boundingSphere != null ) {
			return (edu.cmu.cs.stage3.math.Sphere)m_boundingSphere.clone();
		} else {
			return null;
		}
	}

	private java.util.Vector m_boundListeners = new java.util.Vector();
	public void addBoundListener( BoundListener boundListener ) {
		m_boundListeners.addElement( boundListener );
	}
	public void removeBoundListener( BoundListener boundListener ) {
		m_boundListeners.removeElement( boundListener );
	}
	public BoundListener[] getBoundListeners() {
		BoundListener[] array = new BoundListener[m_boundListeners.size()];
		m_boundListeners.copyInto( array );
		return array;
	}
	private void onBoundsChange( BoundEvent boundEvent ) {
		java.util.Enumeration enum0 = m_boundListeners.elements();
		while( enum0.hasMoreElements() ) {
			((BoundListener)enum0.nextElement()).boundChanged( boundEvent );
		}
	}
	protected void onBoundsChange() {
		m_boundingBox = null;
		m_boundingSphere = null;
		onBoundsChange( new BoundEvent( this ) );
	}
}
