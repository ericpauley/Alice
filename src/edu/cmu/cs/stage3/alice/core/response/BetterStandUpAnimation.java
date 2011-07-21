/*
 * Created on Feb 23, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BetterStandUpAnimation extends StandUpAnimation {
	//public final edu.cmu.cs.stage3.alice.core.property.NumberProperty moveAmount = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "move forward", new Double(0.5));
	public final edu.cmu.cs.stage3.alice.core.property.BooleanProperty scootForward = new BooleanProperty(this, "scoot forward", Boolean.TRUE);
	
	public class RuntimeBetterStandUpAnimation extends RuntimeStandUpAnimation {
		java.util.Vector bodyPartInitialOrientations = null;
		java.util.Vector bodyParts = null;
		
		edu.cmu.cs.stage3.math.Matrix33 normalOrientation = new edu.cmu.cs.stage3.math.Matrix33();
		
		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;
		
	
		
		public void prologue( double t ) {
			super.prologue(t);
			bodyPartInitialOrientations = new java.util.Vector();
			bodyParts = new java.util.Vector();
			normalOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0,0,1), new javax.vecmath.Vector3d(0,1,0));
			
			m_positionBegin = m_subject.getPosition(m_subject.getWorld());
			
			if (m_subject != null) {
				findChildren(m_subject);				
			}
					
		}
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			if (m_subject != null) {
				int level = (int)java.lang.Math.round(m_positionBegin.y/256);
				
				javax.vecmath.Vector3d forward = getTargetQuaternion().getMatrix33().getRow(2);
				
				double moveAmount = 0.0;
				
				edu.cmu.cs.stage3.alice.core.Element[] legs = m_subject.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion("UpperLeg"));
				if (legs.length > 0) {
					Model upperLeg = (Model)legs[0];
					moveAmount = upperLeg.getBoundingBox(upperLeg).getHeight();
				}
				/*if (BetterStandUpAnimation.this.moveAmount != null) {
					moveAmount = BetterStandUpAnimation.this.moveAmount.doubleValue();
				}*/
				if (scootForward.booleanValue() == true) {
					
					m_positionEnd = new javax.vecmath.Vector3d(m_positionBegin.x + (forward.x * moveAmount), 256.0 * level, m_positionBegin.z + (forward.z * moveAmount));
				} else {
					m_positionEnd = new javax.vecmath.Vector3d(m_positionBegin.x, 256.0 * level, m_positionBegin.z);
				}
				
				return m_positionEnd;
			} else {
				return m_positionBegin;
			}
		}
		
		
		public void update( double t ) {
			for (int i = 0; i < bodyPartInitialOrientations.size(); i++) {
				setOrientation((edu.cmu.cs.stage3.alice.core.Transformable)bodyParts.elementAt(i), (edu.cmu.cs.stage3.math.Matrix33)bodyPartInitialOrientations.elementAt(i), normalOrientation, getPortion(t));
			}
			
			if (m_positionEnd == null) {
				m_positionEnd = getPositionEnd();
			}
			
			m_subject.setPositionRightNow( edu.cmu.cs.stage3.math.MathUtilities.interpolate( m_positionBegin, m_positionEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
			
			this.adjustHeight();
	
			super.update(t);			
		}
		
		
		public void epilogue (double t) {
			super.epilogue(t);
			
			m_positionEnd = null;
		}
		
		private void findChildren(edu.cmu.cs.stage3.alice.core.Transformable part) {
			edu.cmu.cs.stage3.alice.core.Element[] kids = part.getChildren(edu.cmu.cs.stage3.alice.core.Transformable.class);
			for (int i = 0; i < kids.length; i++) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)kids[i];
				bodyPartInitialOrientations.addElement(trans.getOrientationAsAxes((ReferenceFrame)trans.getParent()));
				bodyParts.addElement(trans);
				
				if (trans.getChildCount() > 0) {
					findChildren(trans);	
				}
			}
		}
		
		protected void adjustHeight() {
			double distanceAboveGround = 0.0;
			
			if (m_subject != null) {
				distanceAboveGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
			
				double roundHeight = java.lang.Math.round(m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y);
				int level = (int)java.lang.Math.round(roundHeight/256);
				m_subject.moveRightNow(edu.cmu.cs.stage3.alice.core.Direction.DOWN,(distanceAboveGround - (256.0 * level)), m_subject.getWorld() );
			}
		}
	
		private void setOrientation(edu.cmu.cs.stage3.alice.core.Transformable part, edu.cmu.cs.stage3.math.Matrix33 initialOrient, edu.cmu.cs.stage3.math.Matrix33 finalOrient, double portion){
			//System.out.println(portion);
			edu.cmu.cs.stage3.math.Matrix33 currentOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate( initialOrient, finalOrient, portion );
			if (part != null) {
	
				part.setOrientationRightNow( currentOrient, (ReferenceFrame)part.getParent() );
			} 
		}
	}

}
