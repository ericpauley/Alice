/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.util.HowMuch;

/**
 * @author caitlink
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FallDownAnimation extends AbstractBodyPositionAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.DirectionProperty sideToFallOn = new edu.cmu.cs.stage3.alice.core.property.DirectionProperty( this, "sideToFallOn", Direction.FORWARD );
	
	public class RuntimeFallDownAnimation extends RuntimeAbstractBodyPositionAnimation {
		
		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;
		
		private edu.cmu.cs.stage3.alice.core.Direction m_side = null;
		
		
		public void prologue( double t ) {
			super.prologue(t);	
			
			m_positionBegin = m_subject.getPosition(m_subject.getWorld());
			m_positionEnd = null;
			
			m_side = sideToFallOn.getDirectionValue();
			
			findLegs();
			findArms();
			getInitialOrientations();
			setFinalOrientations();
		}
		
		
		public void update( double t ) {
			super.update( t );
			if( m_positionEnd==null ) {
				m_positionEnd = getPositionEnd();
			}
			
			m_subject.setPositionRightNow( edu.cmu.cs.stage3.math.MathUtilities.interpolate( m_positionBegin, m_positionEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
			
			setOrientation(rightUpperArm, rightUpperArmInitialOrient, rightUpperArmFinalOrient, getPortion(t));
			setOrientation(rightLowerArm, rightLowerArmInitialOrient, rightLowerArmFinalOrient, getPortion(t));
			setOrientation(leftUpperArm, leftUpperArmInitialOrient, leftUpperArmFinalOrient, getPortion(t));
			setOrientation(leftLowerArm, leftLowerArmInitialOrient, leftLowerArmFinalOrient, getPortion(t));
			
			this.adjustHeight();
		}
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			javax.vecmath.Vector3d endPos = m_subject.getPosition(m_subject.getWorld());
			
			double pivotToGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
			double pivotToFront = 0.0;
			
			if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD) ) {
				pivotToFront = m_subject.getBoundingBox(m_subject).getCenterOfFrontFace().z;
				
				if ((leftUpper != null) && (leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getMinimum() != null) ) {
					pivotToFront = leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getCenterOfFrontFace().z;
				}
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD) ) {
				pivotToFront = m_subject.getBoundingBox(m_subject).getCenterOfBackFace().z;
				
				if ((leftUpper != null) && (leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getMinimum() != null) ) {
					pivotToFront = -1 * leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getCenterOfBackFace().z;
				}
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT) ) {
				pivotToFront =  -0.8 * m_subject.getBoundingBox(m_subject).getCenterOfLeftFace().x;			
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT) ) {
				pivotToFront =  -0.8 * m_subject.getBoundingBox(m_subject).getCenterOfLeftFace().x;			
			} 		
			endPos.y = endPos.y - pivotToGround + (0.9 * pivotToFront);
			
			return endPos;
		}
		
		
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			edu.cmu.cs.stage3.math.Matrix44 quat = m_subject.getTransformation(m_subject.getWorld());
			m_subject.standUpRightNow();
			
			if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) {
				m_subject.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, 0.25);
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
				m_subject.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD, 0.25);
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT)) {
				m_subject.rollRightNow(edu.cmu.cs.stage3.alice.core.Direction.LEFT, 0.25);
			} else if (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
				m_subject.rollRightNow(edu.cmu.cs.stage3.alice.core.Direction.RIGHT, 0.25);
			}
			edu.cmu.cs.stage3.math.Matrix33 orient =m_subject.getOrientationAsAxes(m_subject.getWorld());
			m_subject.setAbsoluteTransformationRightNow(quat);
			return orient.getQuaternion();
		}
		
		/*public void setInitialOrientations() {
			rightUpperArmInitialOrient = rightUpperArm.getOrientationAsAxes(rightUpperArm);
			leftUpperArmInitialOrient = leftUpperArm.getOrientationAsAxes(leftUpperArm);
			if (rightLowerArm != null) rightLowerArmInitialOrient = rightLowerArm.getOrientationAsAxes(rightLowerArm);
			if (leftLowerArm != null) leftLowerArmInitialOrient = leftLowerArm.getOrientationAsAxes(leftLowerArm);
		}*/
		
		public void setFinalOrientations() {
			if ((m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT)) || (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) ) {
				rightUpperArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				rightUpperArmFinalOrient.rotateX(Math.random() * -0.5 * Math.PI);
				
				leftUpperArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				leftUpperArmFinalOrient.rotateX(Math.random() * -0.5 * Math.PI);
				
				rightLowerArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				rightLowerArmFinalOrient.rotateX(Math.random() * -0.5 * Math.PI);
				
				leftLowerArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				leftLowerArmFinalOrient.rotateX(Math.random() * -0.5 * Math.PI);
			} else if ((m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) || (m_side.equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) ) {
				rightUpperArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				rightUpperArmFinalOrient.rotateZ(Math.random() * 0.5 * Math.PI);
				
				leftUpperArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				leftUpperArmFinalOrient.rotateZ(Math.random() * -0.5 * Math.PI);
				
				rightLowerArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				rightLowerArmFinalOrient.rotateZ(Math.random() * 0.5 * Math.PI);
				
				leftLowerArmFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
				leftLowerArmFinalOrient.rotateZ(Math.random() * -0.5 * Math.PI);
			}
			
		}
	}

}
