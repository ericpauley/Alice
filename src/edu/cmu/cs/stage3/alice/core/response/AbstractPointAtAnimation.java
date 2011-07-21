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

import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public abstract class AbstractPointAtAnimation extends OrientationAnimation {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty( this, "target", null );
	public final Vector3Property offset = new Vector3Property( this, "offset", null );
	public final Vector3Property upGuide = new Vector3Property( this, "upGuide", null );

	public abstract class RuntimeAbstractPointAtAnimation extends RuntimeOrientationAnimation {
		protected edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		protected javax.vecmath.Vector3d m_offset;
		protected javax.vecmath.Vector3d m_upGuide;
		protected boolean m_onlyAffectYaw;
		protected abstract boolean onlyAffectYaw();
		
		//added to allow overriding in subclasses
		 protected edu.cmu.cs.stage3.alice.core.ReferenceFrame getTarget() {
			 return AbstractPointAtAnimation.this.target.getReferenceFrameValue();
		 }
		 protected edu.cmu.cs.stage3.alice.core.Transformable getSubject() {
			 return AbstractPointAtAnimation.this.subject.getTransformableValue();
		 }
		 protected javax.vecmath.Vector3d getOffset() {
			 return AbstractPointAtAnimation.this.offset.getVector3Value();
		 }
		 protected javax.vecmath.Vector3d getUpguide() {
			 return AbstractPointAtAnimation.this.upGuide.getVector3Value();
		 }


		
		public void prologue( double t ) {
			super.prologue( t );
			//setSubject(getSubject());
			m_target = getTarget();
			m_offset = getOffset();
			m_upGuide = getUpguide();
			m_onlyAffectYaw = onlyAffectYaw();
            if( m_target == null ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "target value must not be null.", null, AbstractPointAtAnimation.this.target );
            }
            if( m_target == m_subject ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "target value must not be equal to the subject value.", getCurrentStack(), AbstractPointAtAnimation.this.target );            
            }
            if ( (m_onlyAffectYaw) && (m_subject.isAncestorOf(m_target))) {
            	throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( m_subject.name.getStringValue() + " can't turn to face or turn away from a part of itself.", getCurrentStack(), AbstractPointAtAnimation.this.target );            
            }
		}
		protected edu.cmu.cs.stage3.math.Matrix33 getTargetMatrix33() {
			return m_subject.calculatePointAt( m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw );
		}
		
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return getTargetMatrix33().getQuaternion();
		}
		
		public void update( double t ) {
			//for now we will need to calculate target quaternion every frame
			markTargetQuaternionDirty();
			super.update( t );
		}
	}
}
