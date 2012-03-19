/*
 * Created on May 18, 2004
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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class LookAtAnimation extends AbstractPointAtAnimation {

	public final BooleanProperty onlyAffectYaw = new BooleanProperty(this, "onlyAffectYaw", Boolean.FALSE);

	private edu.cmu.cs.stage3.alice.core.Model getHead(edu.cmu.cs.stage3.alice.core.Transformable subject) {
		edu.cmu.cs.stage3.alice.core.Element[] heads = subject.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion("head", true));
		if (heads.length > 0 && heads[0] instanceof edu.cmu.cs.stage3.alice.core.Model) {
			return (edu.cmu.cs.stage3.alice.core.Model) heads[0];
		} else {
			return null;
		}
	}

	public class RuntimeLookAtAnimation extends RuntimeAbstractPointAtAnimation {
		edu.cmu.cs.stage3.alice.core.Direction m_direction = null;
		double m_turnAmount = 0;
		double m_turnAmountPrev = 0;
		edu.cmu.cs.stage3.alice.core.Transformable m_subjectTrans = null;

		@Override
		public void prologue(double t) {
			m_target = LookAtAnimation.this.target.getReferenceFrameValue();
			if (m_target == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("character value must not be null.", getCurrentStack(), LookAtAnimation.this.target);
			}
			super.prologue(t);

			m_turnAmountPrev = 0;
			if (m_subject == m_target) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("subject and character values must not be the same.", getCurrentStack(), LookAtAnimation.this.subject);
			}

			if (m_subject.equals(LookAtAnimation.this.subject.getTransformableValue())) {
				// subject doesn't have a head, so we don't need to do anything
				// special
			} else {

				// figure out how far we need to turn the whole character
				m_subjectTrans = LookAtAnimation.this.subject.getTransformableValue();

				edu.cmu.cs.stage3.math.Matrix33 targetMatrix = m_subjectTrans.calculatePointAt(m_target, m_offset, m_upGuide, m_asSeenBy, true);
				edu.cmu.cs.stage3.math.Matrix33 subjectMatrix = m_subjectTrans.getOrientationAsAxes();

				javax.vecmath.Vector3d targetForward = targetMatrix.getRow(2);
				javax.vecmath.Vector3d subjectForward = subjectMatrix.getRow(2);

				double cosAngle = subjectForward.dot(targetForward) / (targetForward.length() * subjectForward.length());
				cosAngle = java.lang.Math.acos(cosAngle);

				// convert to revolutions
				cosAngle /= 2 * java.lang.Math.PI;

				// System.out.println("cosangle: " + cosAngle);

				// if the character has to turn more than 1/4, then
				if (cosAngle > 0.25) {
					m_turnAmount = cosAngle - 0.25;

					// set the direction to turn
					javax.vecmath.Vector3d targetPos = m_target.getPosition(m_subjectTrans);
					if (targetPos.x < 0) {
						m_direction = edu.cmu.cs.stage3.alice.core.Direction.LEFT;
					} else {
						m_direction = edu.cmu.cs.stage3.alice.core.Direction.LEFT;
					}

				} else {
					m_turnAmount = 0;
				}

				// System.out.println(m_turnAmount + " " + m_direction);

			}

		}

		@Override
		public void update(double t) {
			super.update(t);

			if (m_turnAmount > 0) {
				double delta = m_turnAmount * getPortion(t) - m_turnAmountPrev;
				m_subjectTrans.rotateRightNow(m_direction.getTurnAxis(), delta, m_subjectTrans);
				m_turnAmountPrev += delta;
			}
		}

		@Override
		protected boolean onlyAffectYaw() {
			edu.cmu.cs.stage3.alice.core.Model head = getHead(LookAtAnimation.this.subject.getTransformableValue());
			if (head != null) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		protected edu.cmu.cs.stage3.alice.core.ReferenceFrame getTarget() {
			edu.cmu.cs.stage3.alice.core.ReferenceFrame targetRef = LookAtAnimation.this.target.getReferenceFrameValue();
			if (targetRef instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				edu.cmu.cs.stage3.alice.core.Model head = getHead((edu.cmu.cs.stage3.alice.core.Transformable) targetRef);
				if (head != null) {
					return head;
				} else {
					return LookAtAnimation.this.target.getReferenceFrameValue();
				}
			} else {
				return LookAtAnimation.this.target.getReferenceFrameValue();
			}
		}

		@Override
		protected edu.cmu.cs.stage3.alice.core.Transformable getSubject() {
			edu.cmu.cs.stage3.alice.core.Model head = getHead(LookAtAnimation.this.subject.getTransformableValue());
			if (head != null) {
				return head;
			} else {
				return LookAtAnimation.this.subject.getTransformableValue();
			}
		}

		@Override
		protected javax.vecmath.Vector3d getOffset() {
			edu.cmu.cs.stage3.alice.core.ReferenceFrame targetRef = LookAtAnimation.this.target.getReferenceFrameValue();
			if (targetRef instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				edu.cmu.cs.stage3.alice.core.Model head = getHead((edu.cmu.cs.stage3.alice.core.Transformable) targetRef);
				if (head != null) {
					return head.getBoundingBox().getCenter();
				} else {
					return LookAtAnimation.this.target.getReferenceFrameValue().getBoundingBox().getCenter();
				}
			} else {
				return LookAtAnimation.this.target.getReferenceFrameValue().getBoundingBox().getCenter();
			}
		}
	}

}
