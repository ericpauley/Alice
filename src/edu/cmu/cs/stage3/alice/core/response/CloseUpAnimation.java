/*
 * Created on Mar 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;

public class CloseUpAnimation extends AbstractPositionAnimation {
	public final Vector3Property position = new Vector3Property( this, "position", new javax.vecmath.Vector3d( 0,0,0 ) );
	public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty( this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF );
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 1 ) );
	
	private edu.cmu.cs.stage3.math.Matrix33 m_cameraEndOrientation;
	private edu.cmu.cs.stage3.math.Matrix33 m_cameraBeginOrientation;

	public static CloseUpAnimation createCloseUpAnimation( Object subject, Object spatialRelation, Object amount, Object asSeenBy ) {
		CloseUpAnimation closeUpAnimation = new CloseUpAnimation();
		closeUpAnimation.subject.set( subject );
		closeUpAnimation.spatialRelation.set( spatialRelation );
		closeUpAnimation.amount.set( amount );
		closeUpAnimation.asSeenBy.set( asSeenBy );
		return closeUpAnimation;
	}
	
	
	public class RuntimeCloseUpAnimation extends RuntimeAbstractPositionAnimation {
		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;
		private double m_amount;
		private double m_cameraHeight;
		
		protected javax.vecmath.Vector3d getPositionBegin() {
			m_cameraBeginOrientation = m_subject.getOrientationAsAxes(m_asSeenBy);
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
			
			edu.cmu.cs.stage3.alice.core.SpatialRelation sv = CloseUpAnimation.this.spatialRelation.getSpatialRelationValue();
			edu.cmu.cs.stage3.math.Matrix33 cameraEndOrientation = m_asSeenBy.getOrientationAsAxes(m_asSeenBy);	

//			get the initial position and orientation for the camera
			 if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(1, 0, 0), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(-1, 0, 0), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( -0.7071068, 0, 0.7071068), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_LEFT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( 0.7071068, 0, 0.7071068), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( -0.7071068, 0, -0.7071068), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_LEFT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( 0.7071068, 0, -0.7071068), cameraEndOrientation.getRow(1));
	
			 // these are the cases that I don't think are useful for this animation, but shouldn't break	
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, -1), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, 1), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, -1, 0), new javax.vecmath.Vector3d(-1,0,0));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 1, 0), new javax.vecmath.Vector3d(-1,0,0));
			 } 
				
				
			m_cameraEndOrientation = cameraEndOrientation;		 
			m_amount = CloseUpAnimation.this.amount.doubleValue() * m_asSeenBy.getHeight() * 3;
			javax.vecmath.Vector3d v = sv.getPlaceVector( m_amount, m_subjectBoundingBox, m_asSeenByBoundingBox );
			//1.25 is fudge factor to leave space for speech bubbles
			double halfViewable = m_asSeenBy.getHeight() * 1.5 * CloseUpAnimation.this.amount.doubleValue() / 2.0;
			double startingHeight = (1.0 - CloseUpAnimation.this.amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
			m_cameraHeight = startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y;
			v.y = m_cameraHeight;
			return m_asSeenBy.getPosition( v, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		
		public void prologue( double t ) {
			m_asSeenBy = CloseUpAnimation.this.asSeenBy.getReferenceFrameValue();
			m_amount = CloseUpAnimation.this.amount.getNumberValue().doubleValue();
			if( m_asSeenBy == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "character value must not be null.", getCurrentStack(), CloseUpAnimation.this.asSeenBy );            
			}
			
			super.prologue(t);
			
		    if( m_subject == null ) {
			    throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "subject value must not be null.", getCurrentStack(), CloseUpAnimation.this.subject );            
		    }
		    if (m_subject == m_asSeenBy) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "subject and character values must not be the same.", getCurrentStack(), CloseUpAnimation.this.subject );            
		    }
			if (m_amount < 0) {
			    throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "amount must be greater than 0", getCurrentStack(), CloseUpAnimation.this.amount );            
		    }
		}
		
		
		public void update( double t ) {
			super.update(t);
			
			edu.cmu.cs.stage3.math.Matrix33 nextOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate(m_cameraBeginOrientation, m_cameraEndOrientation, getPortion( t ));
			m_subject.setOrientationRightNow(nextOrient, m_asSeenBy);
			//m_subject.pointAtRightNow(m_asSeenBy, new javax.vecmath.Vector3d(0,m_cameraHeight,0), new javax.vecmath.Vector3d(0,1,0), m_asSeenBy, false);
			
		}
	}
}
