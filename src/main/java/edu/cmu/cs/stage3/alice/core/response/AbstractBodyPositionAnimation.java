/*
 * Created on Feb 24, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractBodyPositionAnimation extends OrientationAnimation {

	public abstract class RuntimeAbstractBodyPositionAnimation extends RuntimeOrientationAnimation {

		// legs
		protected edu.cmu.cs.stage3.alice.core.Transformable rightUpper = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightLower = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightFoot = null;

		protected edu.cmu.cs.stage3.alice.core.Transformable leftUpper = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftLower = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftFoot = null;

		// arms
		protected edu.cmu.cs.stage3.alice.core.Transformable rightUpperArm = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable rightLowerArm = null;

		protected edu.cmu.cs.stage3.alice.core.Transformable leftUpperArm = null;
		protected edu.cmu.cs.stage3.alice.core.Transformable leftLowerArm = null;

		// leg orientations
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightFootInitialOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftFootInitialOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightFootFinalOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftFootFinalOrient = null;

		// arm orientations
		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperArmInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerArmInitialOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperArmInitialOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerArmInitialOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 rightUpperArmFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 rightLowerArmFinalOrient = null;

		protected edu.cmu.cs.stage3.math.Matrix33 leftUpperArmFinalOrient = null;
		protected edu.cmu.cs.stage3.math.Matrix33 leftLowerArmFinalOrient = null;

		protected void adjustHeight() {
			double distanceAboveGround = 0.0;

			if (m_subject != null) {
				distanceAboveGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;

				double roundHeight = java.lang.Math.round(m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y);
				int level = (int) java.lang.Math.round(roundHeight / 256);
				m_subject.moveRightNow(edu.cmu.cs.stage3.alice.core.Direction.DOWN, distanceAboveGround - 256.0 * level, m_subject.getWorld());
			}
		}

		public edu.cmu.cs.stage3.alice.core.Transformable getTransformableChild(edu.cmu.cs.stage3.alice.core.Transformable parent) {
			if (parent == null) {
				return null;
			}
			edu.cmu.cs.stage3.alice.core.Element[] legBits = parent.getChildren(edu.cmu.cs.stage3.alice.core.Transformable.class);

			// if this leg has more than one part, we've got a problem
			if (legBits.length == 1) {
				return (edu.cmu.cs.stage3.alice.core.Transformable) legBits[0];
			} else {
				return null;
			}
		}

		protected void setOrientation(edu.cmu.cs.stage3.alice.core.Transformable part, edu.cmu.cs.stage3.math.Matrix33 initialOrient, edu.cmu.cs.stage3.math.Matrix33 finalOrient, double portion) {
			if (part != null) {
				edu.cmu.cs.stage3.math.Matrix33 currentOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate(initialOrient, finalOrient, portion);

				part.setOrientationRightNow(currentOrient, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
			}
		}

		public void getInitialOrientations() {

			if (rightUpper != null) {
				rightUpperInitialOrient = rightUpper.getOrientationAsAxes((ReferenceFrame) rightUpper.getParent());
			}
			if (rightLower != null) {
				rightLowerInitialOrient = rightLower.getOrientationAsAxes((ReferenceFrame) rightLower.getParent());
			}
			if (rightFoot != null) {
				rightFootInitialOrient = rightFoot.getOrientationAsAxes((ReferenceFrame) rightFoot.getParent());
			}

			if (leftUpper != null) {
				leftUpperInitialOrient = leftUpper.getOrientationAsAxes((ReferenceFrame) leftUpper.getParent());
			}
			if (leftLower != null) {
				leftLowerInitialOrient = leftLower.getOrientationAsAxes((ReferenceFrame) leftLower.getParent());
			}
			if (leftFoot != null) {
				leftFootInitialOrient = leftFoot.getOrientationAsAxes((ReferenceFrame) leftFoot.getParent());
			}

			if (rightUpperArm != null) {
				rightUpperArmInitialOrient = rightUpperArm.getOrientationAsAxes((ReferenceFrame) rightUpperArm.getParent());
			}
			// System.out.println(rightUpperArmInitialOrient);
			if (rightLowerArm != null) {
				rightLowerArmInitialOrient = rightLowerArm.getOrientationAsAxes((ReferenceFrame) rightLowerArm.getParent());
			}

			if (leftUpperArm != null) {
				leftUpperArmInitialOrient = leftUpperArm.getOrientationAsAxes((ReferenceFrame) leftUpperArm.getParent());
			}
			if (leftLowerArm != null) {
				leftLowerArmInitialOrient = leftLowerArm.getOrientationAsAxes((ReferenceFrame) leftLowerArm.getParent());
			}

		}

		public void findArms() {
			edu.cmu.cs.stage3.alice.core.Element[] arms = m_subject.search(new ElementNameContainsCriterion("Arm"));
			for (Element arm : arms) {
				if (arm.getKey().indexOf("left") != -1 && arm instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					leftUpperArm = (edu.cmu.cs.stage3.alice.core.Transformable) arm;
					leftLowerArm = getTransformableChild(leftUpperArm);
					if (leftLowerArm != null && leftLowerArm.name.getStringValue().indexOf("Hand") != -1) {
						leftLowerArm = null;
					}
				} else if (arm.getKey().indexOf("right") != -1 && arm instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					rightUpperArm = (edu.cmu.cs.stage3.alice.core.Transformable) arm;
					rightLowerArm = getTransformableChild(rightUpperArm);
					if (rightLowerArm != null && rightLowerArm.name.getStringValue().indexOf("Hand") != -1) {
						rightLowerArm = null;
					}
				}
			}
		}

		// search model to find the legs
		public void findLegs() {
			edu.cmu.cs.stage3.alice.core.Element[] legs = m_subject.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion("UpperLeg"));
			for (Element leg : legs) {
				if (leg.getKey().indexOf("left") != -1 && leg instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					leftUpper = (edu.cmu.cs.stage3.alice.core.Transformable) leg;
					leftLower = getTransformableChild(leftUpper);
					if (leftLower.name.getStringValue().indexOf("Foot") != -1) {
						leftFoot = leftLower;
						leftLower = null;
					}
					if (leftLower != null) {
						leftFoot = getTransformableChild(leftLower);
					} else {
						leftFoot = getTransformableChild(leftUpper);
					}
				} else if (leg.getKey().indexOf("right") != -1 && leg instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					rightUpper = (edu.cmu.cs.stage3.alice.core.Transformable) leg;
					// System.out.println("right upper " + rightUpper);
					rightLower = getTransformableChild(rightUpper);
					if (rightLower.name.getStringValue().indexOf("Foot") != -1) {
						rightFoot = rightLower;
						rightLower = null;
					}
					if (rightLower != null) {
						rightFoot = getTransformableChild(rightLower);
					} else {
						rightFoot = getTransformableChild(rightUpper);
					}
				}
			}
		}
	}

}
