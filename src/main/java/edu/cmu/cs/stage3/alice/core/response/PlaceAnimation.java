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

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;

public class PlaceAnimation extends AbstractPositionAnimation {
	public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty( this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF );
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 0 ) );
	public class RuntimePlaceAnimation extends RuntimeAbstractPositionAnimation {
		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;
		
		protected javax.vecmath.Vector3d getPositionBegin() {
			return m_subject.getPosition( edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			if( m_subjectBoundingBox==null ) {
				m_subjectBoundingBox = m_subject.getBoundingBox();
				
				if (m_subjectBoundingBox.getMaximum() == null) {
					m_subjectBoundingBox = new edu.cmu.cs.stage3.math.Box(m_subject.getPosition( m_subject ), m_subject.getPosition( m_subject ));
				}
			}
			if( m_asSeenByBoundingBox ==null ) {
				m_asSeenByBoundingBox = m_asSeenBy.getBoundingBox();
				
				if (m_asSeenByBoundingBox.getMaximum() == null) {
					m_asSeenByBoundingBox = new edu.cmu.cs.stage3.math.Box(m_asSeenBy.getPosition( m_asSeenBy ), m_asSeenBy.getPosition( m_asSeenBy ));
				}
			}

			edu.cmu.cs.stage3.alice.core.SpatialRelation sv = PlaceAnimation.this.spatialRelation.getSpatialRelationValue();
			javax.vecmath.Vector3d v = sv.getPlaceVector( PlaceAnimation.this.amount.doubleValue(), m_subjectBoundingBox, m_asSeenByBoundingBox );
			return m_asSeenBy.getPosition( v, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		public void prologue( double t ) {
			super.prologue( t );
			if( PlaceAnimation.this.spatialRelation.getSpatialRelationValue() == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "spatial relation value must not be null.", null, PlaceAnimation.this.spatialRelation );
			}
			if( PlaceAnimation.this.amount.getValue() == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "amount value must not be null.", null, PlaceAnimation.this.amount );
			}
		}
	}
}
