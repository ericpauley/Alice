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

package edu.cmu.cs.stage3.alice.core.response;

public abstract class OrientationAnimation extends TransformAnimation {
	public abstract class RuntimeOrientationAnimation extends RuntimeTransformAnimation implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener {
		protected edu.cmu.cs.stage3.math.Quaternion m_quaternion0;
		private edu.cmu.cs.stage3.math.Quaternion m_quaternion1;
		protected abstract edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion();

		public void absoluteTransformationChanged( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
			markTargetQuaternionDirty();
		}
		protected void markTargetQuaternionDirty() {
			m_quaternion1 = null;
		}
		protected boolean affectQuaternion() {
			return true;
		}
		
		protected void setSubject(edu.cmu.cs.stage3.alice.core.Transformable newSubject) {
			m_subject = newSubject;
			if (m_subject != null) m_quaternion0 = m_subject.getOrientationAsQuaternion( m_asSeenBy );
		}

		
		public void prologue( double t ) {
			super.prologue( t );
			if( m_asSeenBy == null ) {
				m_asSeenBy = m_subject.vehicle.getReferenceFrameValue();
			}
			if( affectQuaternion() ) {
				m_quaternion0 = m_subject.getOrientationAsQuaternion( m_asSeenBy );
				markTargetQuaternionDirty();
			}
			m_asSeenBy.addAbsoluteTransformationListener( this );
		}
		
		public void update( double t ) {
			super.update( t );
			if( affectQuaternion() ) {
				if( m_quaternion1==null ) {
					m_quaternion1 = getTargetQuaternion();
				}
				edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate( m_quaternion0, m_quaternion1, getPortion( t ) );
				m_subject.setOrientationRightNow( q, m_asSeenBy );
			}
		}
		
		public void epilogue( double t ) {
			super.epilogue( t );
			if( m_asSeenBy != null ) {
				m_asSeenBy.removeAbsoluteTransformationListener( this );
			}
		}
	}
}
