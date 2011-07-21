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

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class MoveAtSpeed extends DirectionSpeedTransformResponse {
	public final BooleanProperty isScaledBySize = new BooleanProperty( this, "isScaledBySize", Boolean.FALSE );
	
	protected Direction getDefaultDirection() {
		return Direction.FORWARD;
	}
	
	protected boolean acceptsDirection( Direction direction ) {
		return direction.getMoveAxis()!=null;
	}
	public class RuntimeMoveAtSpeed extends RuntimeDirectionSpeedTransformResponse {
		private javax.vecmath.Vector3d m_directionVector;
		
		public void prologue( double t ) {
			super.prologue( t );
			Direction directionValue = MoveAtSpeed.this.direction.getDirectionValue();
			if( directionValue!=null ) {
				m_directionVector = directionValue.getMoveAxis();
				if( MoveAtSpeed.this.isScaledBySize.booleanValue() ) {
					javax.vecmath.Vector3d subjectSize = m_subject.getSize();
					m_directionVector.x *= subjectSize.x;
					m_directionVector.y *= subjectSize.y;
					m_directionVector.z *= subjectSize.z;
				}
			} else {
				m_directionVector = new javax.vecmath.Vector3d();
			}
		}
		
		public void update( double t ) {
			super.update( t );
            double delta = getDT()*getSpeed();
			m_subject.moveRightNow( edu.cmu.cs.stage3.math.MathUtilities.multiply( m_directionVector, delta ), m_asSeenBy );
		}
	}
}
