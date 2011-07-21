/*
 * Created on Jul 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core.response;

import javax.vecmath.Vector3d;

import edu.cmu.cs.stage3.alice.core.Limb;
import edu.cmu.cs.stage3.alice.core.property.LimbProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;

/**
 * @author caitlink
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KeepTouchingAnimation extends AbstractBodyPositionAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty target = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty( this, "target", null );
	public final LimbProperty limb = new LimbProperty(this, "limb", Limb.rightArm);
	public final edu.cmu.cs.stage3.alice.core.property.DirectionProperty sideToTouch = new edu.cmu.cs.stage3.alice.core.property.DirectionProperty(this, "side", edu.cmu.cs.stage3.alice.core.Direction.FORWARD);
	public final NumberProperty offset = new NumberProperty(this, "offset", new Double(0.1));
	//public final edu.cmu.cs.stage3.alice.core.property.DirectionProperty offsetDirection = new edu.cmu.cs.stage3.alice.core.property.DirectionProperty(this, "offsetDirection", null);
	
	public class RuntimeTouchAnimation extends RuntimeAbstractBodyPositionAnimation {
		edu.cmu.cs.stage3.alice.core.Transformable m_target;
		
		Quaternion initialQuat = null;
		Quaternion initialLowerQuat = null;
		Quaternion upperTargetQuat = null;
		Quaternion lowerTargetQuat = null;
		
		double upperLimbLength = -1.0;
		double lowerLimbLength = -1.0;
		double limbAngle = 0;
		
		edu.cmu.cs.stage3.alice.core.Transformable upperLimb = null;
		edu.cmu.cs.stage3.alice.core.Transformable lowerLimb = null;
		
		Vector3 initialVector = null;
		Vector3 targetVector = null;
		//Vector3 goalVector = null;
		
		
		public void prologue( double t ) {
			super.prologue(t);
			
			if ( (limb.getLimbValue().equals(Limb.leftArm)) || (limb.getLimbValue().equals(Limb.rightArm)) ) {
				this.findArms();
				if (limb.getLimbValue().equals(Limb.leftArm)) {
					upperLimb = leftUpperArm;
					lowerLimb = leftLowerArm;
				} else {
					upperLimb = rightUpperArm;
					lowerLimb = rightLowerArm;					
				}
			} else {
				this.findLegs();
				if (limb.getLimbValue().equals(Limb.leftLeg)) {
					upperLimb = leftUpper;
					lowerLimb = leftLower;
				} else {
					upperLimb = rightUpper;
					lowerLimb = rightLower;					
				}
			}
			
			m_target = target.getTransformableValue();
			m_asSeenBy = asSeenBy.getReferenceFrameValue();
			//System.out.println("as seen by: " + m_asSeenBy);
			
			initialQuat = upperLimb.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			initialLowerQuat = null;
			if (lowerLimb != null) initialLowerQuat = lowerLimb.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
			upperTargetQuat = null;
			lowerTargetQuat = null;
			
			limbAngle = 0.0;
			upperLimbLength = -1.0;
			lowerLimbLength = -1.0;
			
			initialVector = null;
			javax.vecmath.Vector3d tmp = null;
			
			/*if (lowerLimb != null) {
				tmp = lowerLimb.getBoundingBox(lowerLimb).getCenterOfBottomFace();
				tmp = lowerLimb.getPosition(tmp, m_target);
			} else {
				tmp = upperLimb.getBoundingBox(upperLimb).getCenterOfBottomFace();
				tmp = upperLimb.getPosition(tmp, m_target);
			}
					
			goalVector = new edu.cmu.cs.stage3.math.Vector3(tmp.x, tmp.y, tmp.z);*/
			
			//System.out.println("target Vector: " + goalVector);
			//targetVector = null;
		}
		
		edu.cmu.cs.stage3.math.Vector3 getTargetPosition() {	

			edu.cmu.cs.stage3.alice.core.ReferenceFrame asb = m_asSeenBy;
			if (asb == null) asb = upperLimb;
			
			javax.vecmath.Vector3d targPos = m_target.getBoundingBox(asb).getCenter();
			Vector3d offsetDir = null;
			
			//calculate offset from the center of the bounding box
			if (sideToTouch.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
				targPos = m_target.getBoundingBox(m_target).getCenterOfBackFace();
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(2);
				offsetDir.negate();
			} else if (sideToTouch.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.UP)) {
				targPos = m_target.getBoundingBox(m_target).getCenterOfTopFace();
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(1);
			} else if (sideToTouch.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.DOWN)) {
				targPos = m_target.getBoundingBox(m_target).getCenterOfBottomFace();
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(1);
				offsetDir.negate();
			} else if (sideToTouch.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT)) {
				targPos = m_target.getBoundingBox(m_target).getCenterOfLeftFace();
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(0);
			} else if (sideToTouch.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
				targPos = m_target.getBoundingBox(m_target).getCenterOfRightFace();
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(0);
				offsetDir.negate();
			} else {
				targPos = m_target.getBoundingBox(m_target).getCenterOfFrontFace();	
				offsetDir = m_target.getOrientationAsAxes(asb).getRow(2);
			}
			
			targPos = m_target.getPosition(targPos, upperLimb);
			
			if (offset.doubleValue() != 0) {
				offsetDir.scale(offset.doubleValue());
				targPos.add(offsetDir);
			}
			
			edu.cmu.cs.stage3.math.Vector3 targPos2 = new edu.cmu.cs.stage3.math.Vector3(targPos.x, targPos.y, targPos.z);		
			
			//System.out.println("touch targPos: " + targPos2);
			targPos.normalize();
			//System.out.println(targPos);
			
			return targPos2;			
		}
		
		
		
		public void update( double t ) {	
			if (getPortion(t) <= 1.0) {
				
				setTargetQuaternions();
				upperTargetQuat = getTargetQuaternion();
				//System.out.println("upperTargetQuat: " + upperTargetQuat);
				lowerTargetQuat = getLowerTargetQuaternion();
			
				//edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate( initialQuat, upperTargetQuat, getPortion( t ) );
				upperLimb.setOrientationRightNow( upperTargetQuat, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent()  );
				
				//edu.cmu.cs.stage3.math.Quaternion r = edu.cmu.cs.stage3.math.Quaternion.interpolate( initialLowerQuat, lowerTargetQuat, getPortion( t ) );
				if (lowerLimb != null) lowerLimb.setOrientationRightNow( lowerTargetQuat, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent()  );
			}
		}
		
		double getLength(Vector3 vec) {
			return Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
		}
		
		double getAngle(Vector3 a, Vector3 b) {	
			// calculate angle
			Vector3 c = Vector3.subtract(a, b);
			double cLength = getLength(c);
			double aLength = getLength(a);
			double bLength = getLength(b);		
			double cosC = ((cLength * cLength) - (aLength * aLength) - (bLength * bLength)) / (-2.0 * aLength * bLength);
			
			return java.lang.Math.acos(cosC);
		}
		
		protected void setArmLengths() {
			if ((upperLimb != null) && (lowerLimb != null)) {
				edu.cmu.cs.stage3.math.Vector3 tmp = edu.cmu.cs.stage3.math.Vector3.subtract(lowerLimb.getPosition(lowerLimb), lowerLimb.getBoundingBox(lowerLimb).getCenterOfBottomFace());
				lowerLimbLength = tmp.getLength();

				upperLimbLength = upperLimb.getPosition(upperLimb).y - lowerLimb.getPosition(upperLimb).y;
			} else {
				upperLimbLength = upperLimb.getPosition(upperLimb).y - upperLimb.getBoundingBox(upperLimb).getCenterOfBottomFace().y;
				lowerLimbLength = 0.0;
			}
		}
		
		/*edu.cmu.cs.stage3.math.Vector3 getTargetPosition() {	
			// this is the goal position in the target's space - we need to know where it is from the upperlimb
			javax.vecmath.Vector3d tmp = new javax.vecmath.Vector3d(goalVector.x, goalVector.y, goalVector.z);
			System.out.println("goalVector: " + goalVector);
						
			tmp = m_target.getPosition(tmp, upperLimb);
			System.out.println("targetPos: " + tmp);
			
			return new edu.cmu.cs.stage3.math.Vector3(tmp.x, tmp.y, tmp.z);
				
		}*/
		
		/*protected Vector3 getHandForward() {
			return null;
		}*/
		
		protected void setVectors() {
			Vector3 rightUpperPos = upperLimb.getPosition(upperLimb);
			Vector3 targetPos = getTargetPosition(); //m_target.getPosition(upperLimb);	
			targetVector = Vector3.subtract(targetPos,rightUpperPos);
			//targetVector.normalize();
			
			javax.vecmath.Vector3d currentDir = null;
			if (lowerLimb == null) {
				currentDir = upperLimb.getBoundingBox(upperLimb).getCenterOfBottomFace(); 
			} else {
				currentDir = lowerLimb.getBoundingBox(lowerLimb).getCenterOfBottomFace();
				currentDir = lowerLimb.getPosition(currentDir, upperLimb);
			}

			initialVector = new edu.cmu.cs.stage3.math.Vector3(currentDir.x, currentDir.y, currentDir.z);
			
		}
		

		
		
		protected void setTargetQuaternions() {
			// save upper and lower current transformations 
			edu.cmu.cs.stage3.math.Matrix44 initialTrans = null;
			if (lowerLimb != null) initialTrans = lowerLimb.getTransformation((edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
			edu.cmu.cs.stage3.math.Matrix44 initialUpperTrans = upperLimb.getTransformation((edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			
			// set upper and lower limbs to their default orientations as a starting point.
			if (lowerLimb != null) lowerLimb.setOrientationRightNow(new edu.cmu.cs.stage3.math.Matrix33(), (edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
			upperLimb.setOrientationRightNow(new edu.cmu.cs.stage3.math.Matrix33(), (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			
			// set initial data
			setArmLengths();
			setVectors();	
			
			// calculate rotation of lower limb
			double targetDistance = targetVector.length();
			limbAngle = 0;
			
			// if it's target too far to reach, make the lowerLimb not bend relative to the upper limb
			if (targetDistance < upperLimbLength + lowerLimbLength) {
			
				// calculate appropriate turn angle
				double cosAngle = ((targetDistance * targetDistance) - (upperLimbLength * upperLimbLength) - (lowerLimbLength * lowerLimbLength))/(-2.0 * upperLimbLength * lowerLimbLength);
				//System.out.println(cosAngle);
				limbAngle = (java.lang.Math.PI - java.lang.Math.acos(cosAngle))/ (2.0 * java.lang.Math.PI);	
				//System.out.println("limb angle " + limbAngle);
				
				if (! (Double.isNaN(limbAngle))) {
				
	//				arms turn backwards, legs turn forwards so as not to break people
					// upper limbs turn 1/2 limbAngle so that effective arm is still along y axis
					if ( (limb.getLimbValue().equals(Limb.leftArm)) || (limb.getLimbValue().equals(Limb.rightArm)) ) {
						if (lowerLimb != null) {
							lowerLimb.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD, limbAngle);	
							upperLimb.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, (limbAngle / 2.0));
						}
					} else {
						if (lowerLimb != null) {
							lowerLimb.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, limbAngle);
							upperLimb.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD, (limbAngle / 2.0));
						}
					}
				} 
			}
			
//			calculate rotation and rotate
			double angle = getAngle(initialVector, targetVector);
			edu.cmu.cs.stage3.math.Vector3 cross = Vector3.crossProduct(initialVector, targetVector);
			cross.normalize();		
			upperLimb.rotateRightNow(cross, angle /(2.0 * java.lang.Math.PI), (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			
			//nudge rotations to make arms look a tab more reasonable
			targetVector.normalize();
			double turnAmt = 0;

			if (limb.getLimbValue().equals(Limb.rightArm)) {
				if ((targetVector.x < 0) ) { turnAmt = .25 + (-.1 * targetVector.x);}	
				if (targetVector.z < .3) { turnAmt += 1 * (.3 - targetVector.z);}
				if (turnAmt > 0) upperLimb.rotateRightNow(targetVector, turnAmt, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			} else if (limb.getLimbValue().equals(Limb.leftArm )) {
				if ((targetVector.x > 0) ) { turnAmt = .25 + (-.1 * targetVector.x);}	
				if (targetVector.z < .5) { turnAmt += 1 * (.3 - targetVector.z);}
				if (Math.abs(turnAmt) > 0) upperLimb.rotateRightNow(targetVector, -1.0 * turnAmt, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			}
			
			// save target quaternions
			lowerTargetQuat = null;
			if (lowerLimb != null) {
				lowerTargetQuat = lowerLimb.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
				if (Double.isNaN(limbAngle)) {
					lowerLimb.setTransformationRightNow(initialTrans, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
					lowerTargetQuat = lowerLimb.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
				}
											  
			}
			upperTargetQuat = upperLimb.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			
			// set limbs back to initial transformations
			if (lowerLimb != null) lowerLimb.setTransformationRightNow(initialTrans, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)lowerLimb.getParent());
			upperLimb.setTransformationRightNow(initialUpperTrans, (edu.cmu.cs.stage3.alice.core.ReferenceFrame)upperLimb.getParent());
			
		}
		
		public Quaternion getLowerTargetQuaternion() {
			if (lowerTargetQuat == null) {
				setTargetQuaternions();
			}
			return lowerTargetQuat;
		}
		
		
		public edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {		
			if (upperTargetQuat == null) {
				setTargetQuaternions();
			}
			return upperTargetQuat;
		}
	}
}

