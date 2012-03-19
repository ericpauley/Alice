/*
 * Created on Mar 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SitAnimation extends AbstractBodyPositionAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty target = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty(this, "target", null);
	public final edu.cmu.cs.stage3.alice.core.property.DirectionProperty side = new edu.cmu.cs.stage3.alice.core.property.DirectionProperty(this, "side", Direction.FORWARD);

	public class RuntimeSitAnimation extends RuntimeAbstractBodyPositionAnimation {
		edu.cmu.cs.stage3.alice.core.Transformable m_target;

		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;

		@Override
		public void prologue(double t) {
			super.prologue(t);

			m_target = target.getTransformableValue();
			m_positionBegin = m_subject.getPosition(m_subject.getWorld());
			m_positionEnd = null;

			if (m_target == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " needs something or someone to sit on.", null, target);
			}
			if (m_target == m_subject) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " can't sit on " + m_target.name.getStringValue() + ".", getCurrentStack(), target);
			}

			if (m_subject.isAncestorOf(m_target)) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " can't sit on a part of itself", getCurrentStack(), target);
			}

			findLegs();
			getInitialOrientations();
			setFinalOrientations();
		}

		@Override
		public void update(double t) {
			super.update(t);
			if (m_positionEnd == null) {
				m_positionEnd = getPositionEnd();
			}
			m_subject.setPositionRightNow(edu.cmu.cs.stage3.math.MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);

			setOrientation(rightUpper, rightUpperInitialOrient, rightUpperFinalOrient, getPortion(t));
			if (!m_target.name.getStringValue().equals("ground")) {
				setOrientation(rightLower, rightLowerInitialOrient, rightLowerFinalOrient, getPortion(t));
			}

			setOrientation(leftUpper, leftUpperInitialOrient, leftUpperFinalOrient, getPortion(t));
			if (!m_target.name.getStringValue().equals("ground")) {
				setOrientation(leftLower, leftLowerInitialOrient, leftLowerFinalOrient, getPortion(t));
			}

			adjustHeight();
		}

		@Override
		protected void adjustHeight() {
			if (m_target != null && !m_target.name.getStringValue().equals("ground")) {

			} else {
				super.adjustHeight();
			}
		}

		@Override
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {

			edu.cmu.cs.stage3.math.Matrix33 orient = m_target.getOrientationAsAxes(m_target.getWorld());

			if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
				orient.rotateY(Math.PI);
			} else if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT)) {
				orient.rotateY(-1.0 * Math.PI / 2.0);
			} else if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
				orient.rotateY(Math.PI / 2.0);
			}

			if (m_target.name.getStringValue().equals("ground")) {
				edu.cmu.cs.stage3.math.Matrix44 currentTrans = m_subject.getTransformation(m_subject.getWorld());
				m_subject.standUpRightNow();
				orient = m_subject.getOrientationAsAxes(m_subject.getWorld());
				m_subject.setTransformationRightNow(currentTrans, m_subject.getWorld());
			}
			return orient.getQuaternion();
		}

		protected javax.vecmath.Vector3d getPositionEnd() {
			if (m_target != null) {
				javax.vecmath.Vector3d centerTopFace = m_target.getBoundingBox(m_target).getCenterOfTopFace();
				javax.vecmath.Vector3d endPos = m_target.getBoundingBox(m_target.getWorld()).getCenterOfTopFace();
				javax.vecmath.Vector3d[] forwardAndUp = m_target.getOrientationAsForwardAndUpGuide(m_target.getWorld());

				if (leftUpper != null && leftLower == null || m_target.name.getStringValue().equals("ground")) {
					double xOffset = m_subject.getBoundingBox().getCenter().x;
					double yOffset = leftUpper.getPosition(m_subject).y;
					double zStart = 0.0;

					double depthSeat = leftUpper.getBoundingBox(leftUpper).getHeight() * 2.0 / 3.0;

					if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD) || side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) {

						if (depthSeat > m_target.getBoundingBox(m_target).getDepth()) {
							depthSeat = m_target.getBoundingBox(m_target).getDepth();
						}

						if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
							zStart = m_target.getBoundingBox(m_target).getCenterOfBackFace().z;
							depthSeat *= -1.0;
						} else {
							zStart = m_target.getBoundingBox(m_target).getCenterOfFrontFace().z;
						}

						endPos = new javax.vecmath.Vector3d(centerTopFace.x - xOffset, centerTopFace.y - yOffset, zStart - depthSeat);
						endPos = m_target.getPosition(endPos, m_target.getWorld());
					} else if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT) || side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {

						if (depthSeat > m_target.getBoundingBox(m_target).getWidth()) {
							depthSeat = m_target.getBoundingBox(m_target).getWidth();
						}

						if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
							zStart = m_target.getBoundingBox(m_target).getCenterOfRightFace().x;
						} else {
							zStart = m_target.getBoundingBox(m_target).getCenterOfLeftFace().x;
							depthSeat *= -1.0;
						}

						endPos = new javax.vecmath.Vector3d(zStart - depthSeat, centerTopFace.y - yOffset, centerTopFace.z - xOffset);
						endPos = m_target.getPosition(endPos, m_target.getWorld());

					}
					if (m_target.name.getStringValue().equals("ground")) {
						endPos = m_subject.getPosition(m_subject.getWorld());
						endPos.y -= depthSeat;
					}
					return endPos;
				} else if (leftUpper != null && leftLower != null) {
					double depthSeat = leftUpper.getBoundingBox(leftUpper, edu.cmu.cs.stage3.util.HowMuch.INSTANCE).getHeight() - leftLower.getBoundingBox(leftLower, edu.cmu.cs.stage3.util.HowMuch.INSTANCE).getDepth() / 2.0;
					depthSeat *= 2.0 / 3.0;

					double xOffset = m_subject.getBoundingBox().getCenter().x;
					double yOffset = leftUpper.getPosition(m_subject).y;
					double zStart = 0.0;

					if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD) || side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) {

						if (depthSeat > m_target.getBoundingBox(m_target).getDepth()) {
							depthSeat = m_target.getBoundingBox(m_target).getDepth();
						}

						if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
							zStart = m_target.getBoundingBox(m_target).getCenterOfBackFace().z;
							depthSeat *= -1.0;
						} else {
							zStart = m_target.getBoundingBox(m_target).getCenterOfFrontFace().z;
						}

						endPos = new javax.vecmath.Vector3d(centerTopFace.x - xOffset, centerTopFace.y - yOffset, zStart - depthSeat);
						endPos = m_target.getPosition(endPos, m_target.getWorld());
					} else if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT) || side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {

						if (depthSeat > m_target.getBoundingBox(m_target).getWidth()) {
							depthSeat = m_target.getBoundingBox(m_target).getWidth();
						}

						if (side.getDirectionValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
							zStart = m_target.getBoundingBox(m_target).getCenterOfRightFace().x;
						} else {
							zStart = m_target.getBoundingBox(m_target).getCenterOfLeftFace().x;
							depthSeat *= -1.0;
						}

						endPos = new javax.vecmath.Vector3d(zStart - depthSeat, centerTopFace.y - yOffset, centerTopFace.z - xOffset);
						endPos = m_target.getPosition(endPos, m_target.getWorld());
					}

					return endPos;

				}
				return endPos;
			} else {
				return m_positionBegin;
			}
		}

		public void setFinalOrientations() {
			rightUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			leftUpperFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			rightLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();
			leftLowerFinalOrient = new edu.cmu.cs.stage3.math.Matrix33();

			rightUpperFinalOrient.rotateX(-0.5 * java.lang.Math.PI);
			leftUpperFinalOrient.rotateX(-0.5 * java.lang.Math.PI);

			rightLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI);
			leftLowerFinalOrient.rotateX(0.5 * java.lang.Math.PI);
		}
	}

}
