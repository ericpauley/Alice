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

public abstract class AbstractPositionAnimation extends TransformAnimation {
	public abstract class RuntimeAbstractPositionAnimation extends RuntimeTransformAnimation implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener {
		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;

		protected abstract javax.vecmath.Vector3d getPositionBegin();
		protected abstract javax.vecmath.Vector3d getPositionEnd();

		public void absoluteTransformationChanged( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
			m_positionEnd = null;
		}
		
		public void prologue( double t ) {
			super.prologue( t );
			if( m_asSeenBy == null ) {
				m_asSeenBy = m_subject.vehicle.getReferenceFrameValue();
			}
			m_positionBegin = getPositionBegin();
			m_positionEnd = null;
			m_asSeenBy.addAbsoluteTransformationListener( this );
		}
		
		public void update( double t ) {
			super.update( t );
			if( m_positionEnd==null ) {
				m_positionEnd = getPositionEnd();
			}
			m_subject.setPositionRightNow( edu.cmu.cs.stage3.math.MathUtilities.interpolate( m_positionBegin, m_positionEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		public void epilogue( double t ) {
			super.epilogue( t );
			if (m_asSeenBy != null) m_asSeenBy.removeAbsoluteTransformationListener( this );
		}
	}
}
