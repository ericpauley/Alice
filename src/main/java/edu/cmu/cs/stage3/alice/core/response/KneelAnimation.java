/*
 * Created on Feb 24, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.math.Vector3;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class KneelAnimation extends AbstractBodyPositionAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.BooleanProperty oneKnee = new edu.cmu.cs.stage3.alice.core.property.BooleanProperty(this, "one knee", Boolean.TRUE);

	public class RuntimeKneelAnimation extends RuntimeAbstractBodyPositionAnimation {

		@Override
		public void prologue(double t) {
			super.prologue(t);

			findLegs();
			getInitialOrientations();
			setFinalOrientations();
		}

		@Override
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return m_subject.calculateStandUp(m_subject.getWorld()).getQuaternion();
		}

		@Override
		public void update(double t) {

			super.update(t);

			setOrientation(rightUpper, rightUpperInitialOrient, rightUpperFinalOrient, getPortion(t));
			setOrientation(rightLower, rightLowerInitialOrient, rightLowerFinalOrient, getPortion(t));
			if (rightFoot != null) {
				setOrientation(rightFoot, rightFootInitialOrient, rightFootFinalOrient, getPortion(t));
			}

			setOrientation(leftUpper, leftUpperInitialOrient, leftUpperFinalOrient, getPortion(t));
			setOrientation(leftLower, leftLowerInitialOrient, leftLowerFinalOrient, getPortion(t));
			if (leftFoot != null) {
				setOrientation(leftFoot, leftFootInitialOrient, leftFootFinalOrient, getPortion(t));
			}

			adjustHeight();
		}

		public void setFinalOrientations() {
			// blah blah blah.
			rightUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			rightLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			rightLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI);
			rightFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			rightFootFinalOrient.rotateX(0.5 * java.lang.Math.PI);

			if (oneKnee.booleanValue()) {
				double lengthSupportLeg = 0.0;

				if (rightLower == null) {
					leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftUpperFinalOrient.rotateX(0.25 * java.lang.Math.PI);
					leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftFootFinalOrient.rotateX(-0.25 * java.lang.Math.PI);

					rightUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					rightUpperFinalOrient.rotateX(-0.25 * java.lang.Math.PI);
					rightFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					rightFootFinalOrient.rotateX(0.25 * java.lang.Math.PI);

				}

				if (rightUpper != null && rightLower != null) {
					Vector3 posLower = rightLower.getPosition(rightUpper);
					edu.cmu.cs.stage3.math.Box boxLower = rightLower.getBoundingBox(rightLower);

					lengthSupportLeg = java.lang.Math.abs(posLower.y) + java.lang.Math.abs(boxLower.getMinimum().z);

					double lengthLowerLeg = 0.0;
					if (leftFoot != null) {
						lengthLowerLeg = java.lang.Math.abs(leftFoot.getPosition(leftLower).y) + java.lang.Math.abs(leftFoot.getBoundingBox(leftFoot).getMinimum().z);
					} else {
						lengthLowerLeg = java.lang.Math.abs(leftLower.getBoundingBox(leftLower).getMinimum().y);
					}

					double diff = lengthSupportLeg - lengthLowerLeg;
					double angle = java.lang.Math.asin(java.lang.Math.abs(diff) / java.lang.Math.abs(posLower.y));

					if (lengthSupportLeg * 2.0 < lengthLowerLeg) {
						leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftLowerFinalOrient.rotateX(0.25 * java.lang.Math.PI);
						leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftFootFinalOrient.rotateX(-0.25 * java.lang.Math.PI);

						rightUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						rightLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						rightLowerFinalOrient.rotateX(-0.25 * java.lang.Math.PI);
						rightFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						rightFootFinalOrient.rotateX(0.25 * java.lang.Math.PI);
					} else if (diff < 0) {
						leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftUpperFinalOrient.rotateX(-0.5 * java.lang.Math.PI - angle);
						leftLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI + angle);
						leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();

					} else {
						leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftUpperFinalOrient.rotateX(-0.5 * java.lang.Math.PI + angle);
						leftLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
						leftLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI - angle);
						leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();

					}
				}
			} else {
				if (rightLower == null) {
					leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftUpperFinalOrient.rotateX(0.5 * java.lang.Math.PI);
					leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftFootFinalOrient.rotateX(0.5 * java.lang.Math.PI);

					rightUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					rightUpperFinalOrient.rotateX(0.5 * java.lang.Math.PI);
					rightFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					rightFootFinalOrient.rotateX(0.5 * java.lang.Math.PI);

				} else {
					leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI);
					leftFootFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
					leftFootFinalOrient.rotateX(0.5 * java.lang.Math.PI);
				}
			}
		}
	}

}
