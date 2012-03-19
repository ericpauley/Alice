/*
 * Created on Feb 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.math.Matrix33;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WalkToAnimation extends AbstractWalkAnimation {

	public final edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty spatialRelation = new edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty(this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF);
	public final edu.cmu.cs.stage3.alice.core.property.NumberProperty amount = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "amount", new Double(1));
	public final edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty asSeenBy = new edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty(this, "target", null);

	public class RuntimeWalkToAnimation extends RuntimeAbstractWalkAnimation {

		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;
		protected edu.cmu.cs.stage3.alice.core.ReferenceFrame m_asSeenBy;

		private edu.cmu.cs.stage3.math.Matrix44 m_transformationBegin;
		private edu.cmu.cs.stage3.math.Matrix44 m_transformationEnd;
		private edu.cmu.cs.stage3.math.HermiteCubic m_xHermite;
		private edu.cmu.cs.stage3.math.HermiteCubic m_yHermite;
		private edu.cmu.cs.stage3.math.HermiteCubic m_zHermite;

		private boolean m_affectPosition;
		private boolean m_affectQuaternion;

		private boolean beginEqualsEnd = false;

		double stepLength = -1.0;
		double numberOfSteps = -1.0;
		double currentPos = 0.0;
		double timePerStep = -1.0;

		private boolean done = false;

		protected javax.vecmath.Vector3d getPositionEnd() {
			if (m_subjectBoundingBox == null) {
				m_subjectBoundingBox = subject.getBoundingBox();

				if (m_subjectBoundingBox.getMaximum() == null) {
					m_subjectBoundingBox = new edu.cmu.cs.stage3.math.Box(subject.getPosition(subject), subject.getPosition(subject));
				}
			}
			if (m_asSeenByBoundingBox == null) {
				m_asSeenByBoundingBox = m_asSeenBy.getBoundingBox();

				if (m_asSeenByBoundingBox.getMaximum() == null) {
					m_asSeenByBoundingBox = new edu.cmu.cs.stage3.math.Box(m_asSeenBy.getPosition(m_asSeenBy), m_asSeenBy.getPosition(m_asSeenBy));
				}
			}
			edu.cmu.cs.stage3.alice.core.SpatialRelation sv = spatialRelation.getSpatialRelationValue();
			javax.vecmath.Vector3d v = sv.getPlaceVector(amount.doubleValue(), m_subjectBoundingBox, m_asSeenByBoundingBox);
			return v;
		}

		protected double getValueAtTime(double t) {
			double ft = m_xHermite.evaluateDerivative(t);
			double ht = m_zHermite.evaluateDerivative(t);

			return java.lang.Math.sqrt(ft * ft + ht * ht);
		}

		protected double getActualStepLength() {

			double distanceToMove = getPathLength();

			if (stepLength == -1) {
				stepLength = getStepLength();
				if (stepLength == 0.0) {
					stepLength = 1.0;
				}
			}

			if (numberOfSteps == -1) {
				numberOfSteps = java.lang.Math.round(distanceToMove / stepLength);
			}

			return distanceToMove / numberOfSteps;

		}

		@Override
		public double getTimeRemaining(double t) {
			double walkTime = duration.doubleValue();
			if (Double.isNaN(walkTime)) {
				walkTime = numberOfSteps / WalkToAnimation.this.stepSpeed.doubleValue();
			}
			return walkTime - getTimeElapsed(t);
		}

		protected double getPathLength() {
			double x1s = getValueAtTime(0.0) + getValueAtTime(1.0);

			double h = 0.1;

			double startT = h;
			double x4s = 0.0;

			while (startT < 1.0) {
				x4s += getValueAtTime(startT);
				startT += 2 * h;
			}

			startT = 2 * h;
			double x2s = 0.0;

			while (startT < 1.0) {
				x2s += getValueAtTime(startT);
				startT += 2 * h;
			}

			// System.out.println("distance between points: " +
			// java.lang.Math.sqrt((m_transformationEnd.m30 -
			// m_transformationBegin.m30) * (m_transformationEnd.m30 -
			// m_transformationBegin.m30) + (m_transformationEnd.m32 -
			// m_transformationBegin.m32) * (m_transformationEnd.m32 -
			// m_transformationBegin.m32)) );
			return (x1s + 4 * x4s + 2 * x2s) * (h / 3.0);
		}

		@Override
		public void prologue(double t) {

			beginEqualsEnd = false;
			done = false;

			subject = WalkToAnimation.this.subject.getTransformableValue();
			m_asSeenBy = asSeenBy.getReferenceFrameValue();

			edu.cmu.cs.stage3.math.Matrix44 asSeenByTrans = m_asSeenBy.getTransformation(subject.getWorld());
			((edu.cmu.cs.stage3.alice.core.Transformable) m_asSeenBy).standUpRightNow(subject.getWorld());

			m_transformationBegin = subject.getTransformation(m_asSeenBy);

			if (m_asSeenBy == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(subject.name.getStringValue() + " needs something or someone to walk to.", null, asSeenBy);
			}
			if (subject == m_asSeenBy) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(subject.name.getStringValue() + " can't walk to " + subject.name.getStringValue() + ".", getCurrentStack(), asSeenBy);
			}

			if (subject.isAncestorOf(m_asSeenBy)) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(subject.name.getStringValue() + " can't walk to a part of itself", getCurrentStack(), asSeenBy);
			}

			// find end transformation
			javax.vecmath.Vector3d posAbs = getPositionEnd();
			javax.vecmath.Vector3d curPos = subject.getPosition();
			subject.setPositionRightNow(posAbs, m_asSeenBy);

			// javax.vecmath.Matrix3d paMatrix =
			// subject.calculatePointAt(m_asSeenBy, null, new
			// javax.vecmath.Vector3d(0,1,0), null, true);
			javax.vecmath.Matrix3d paMatrix = subject.calculatePointAt(m_asSeenBy, null, new javax.vecmath.Vector3d(0, 1, 0), m_asSeenBy, true);
			subject.setPositionRightNow(curPos);

			javax.vecmath.Matrix4d pov = asSeenBy.getReferenceFrameValue().getPointOfView();
			pov.set(paMatrix);
			pov.setRow(3, posAbs.x, posAbs.y, posAbs.z, 1.0);

			m_transformationEnd = new edu.cmu.cs.stage3.math.Matrix44(pov);

			double dx = m_transformationBegin.m30 - m_transformationEnd.m30;
			double dy = m_transformationBegin.m31 - m_transformationEnd.m31;
			double dz = m_transformationBegin.m32 - m_transformationEnd.m32;
			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			double s = distance;

			m_xHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m_transformationBegin.m30, m_transformationEnd.m30, m_transformationBegin.m20 * s, m_transformationEnd.m20 * s);
			m_yHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m_transformationBegin.m31, m_transformationEnd.m31, m_transformationBegin.m21 * s, m_transformationEnd.m21 * s);
			m_zHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m_transformationBegin.m32, m_transformationEnd.m32, m_transformationBegin.m22 * s, m_transformationEnd.m22 * s);

			super.prologue(t);
			getActualStepLength();

			((edu.cmu.cs.stage3.alice.core.Transformable) m_asSeenBy).setTransformationRightNow(asSeenByTrans, subject.getWorld());

		}

		@Override
		public void update(double t) {
			if (getTimeRemaining(t) > 0) {

				edu.cmu.cs.stage3.math.Matrix44 asSeenByTrans = m_asSeenBy.getTransformation(subject.getWorld());
				((edu.cmu.cs.stage3.alice.core.Transformable) m_asSeenBy).standUpRightNow(subject.getWorld());

				double portion = getTimeElapsed(t) / (getTimeElapsed(t) + getTimeRemaining(t));

				if (portion <= 1.0) {
					double x;
					double y;
					double z;
					double dx;
					double dy;
					double dz;

					// get the appropriate position
					x = m_xHermite.evaluate(portion);
					y = m_yHermite.evaluate(portion);
					z = m_zHermite.evaluate(portion);

					subject.setPositionRightNow(x, y, z, m_asSeenBy);

					// face the direction you are moving
					dx = m_xHermite.evaluateDerivative(portion);
					// dy = m_yHermite.evaluateDerivative(portion);
					dy = 0.0;
					dz = m_zHermite.evaluateDerivative(portion);

					if (!(dx == 0 && dy == 0 && dz == 0)) {
						Matrix33 orient = new Matrix33();
						orient.setForwardUpGuide(new javax.vecmath.Vector3d(dx, dy, dz), new javax.vecmath.Vector3d(0, 1, 0));
						// System.out.println(m_asSeenBy);
						subject.setOrientationRightNow(orient, m_asSeenBy);
						// subject.s
					} else {
						// System.out.println("deriv 0");
					}

					if (timePerStep == -1) {
						if (!Double.isNaN(duration.doubleValue())) {
							timePerStep = duration.doubleValue() / numberOfSteps;
						} else {
							timePerStep = 1.0 / stepSpeed.doubleValue();
						}
					}

					// int stepNumber = (int)java.lang.Math.ceil(
					// getTimeElapsed(t) * stepSpeed.doubleValue()) - 1;
					int stepNumber = (int) java.lang.Math.ceil(getTimeElapsed(t) * (1.0 / timePerStep)) - 1;
					if (stepNumber == -1) {
						stepNumber = 0;
					}
					if (stepNumber == numberOfSteps) {
						stepNumber -= 1;
					}
					double portionOfStep = (getTimeElapsed(t) - stepNumber * timePerStep) / timePerStep;

					if (portionOfStep > 1.0) {
						portionOfStep = 1.0;
					}

					boolean lastStep = false;
					if (stepNumber == numberOfSteps - 1) {
						lastStep = true;
					}

					if (stepNumber % 2 == 0) {
						stepRight(portionOfStep, lastStep);
					} else {
						stepLeft(portionOfStep, lastStep);
					}

					super.update(t);
				}
				((edu.cmu.cs.stage3.alice.core.Transformable) m_asSeenBy).setTransformationRightNow(asSeenByTrans, subject.getWorld());
			}
		}

	}

}
