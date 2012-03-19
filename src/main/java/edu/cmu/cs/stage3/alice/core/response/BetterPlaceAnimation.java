/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BetterPlaceAnimation extends PointOfViewAnimation {
	public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF);
	public final NumberProperty amount = new NumberProperty(this, "amount", new Double(0));
	// public final ReferenceFrameProperty target = new ReferenceFrameProperty(
	// this, "target", null );

	public class RuntimeBetterPlaceAnimation extends RuntimePointOfViewAnimation {

		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;

		private boolean beginEqualsEnd = false;

		protected javax.vecmath.Vector3d getPositionEnd() {
			if (m_subjectBoundingBox == null) {
				m_subjectBoundingBox = m_subject.getBoundingBox();

				if (m_subjectBoundingBox.getMaximum() == null) {
					m_subjectBoundingBox = new edu.cmu.cs.stage3.math.Box(m_subject.getPosition(m_subject), m_subject.getPosition(m_subject));
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

		@Override
		public void prologue(double t) {

			beginEqualsEnd = false;

			m_subject = BetterPlaceAnimation.this.subject.getTransformableValue();
			m_asSeenBy = BetterPlaceAnimation.this.asSeenBy.getReferenceFrameValue();

			if (m_asSeenBy == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " needs something or someone to move to.", null, BetterPlaceAnimation.this.asSeenBy);
			}
			if (m_subject == m_asSeenBy) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " can't move to " + m_subject.name.getStringValue() + ".", getCurrentStack(), BetterPlaceAnimation.this.asSeenBy);
			}

			if (m_subject.isAncestorOf(m_asSeenBy)) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException(m_subject.name.getStringValue() + " can't move to a part of itself", getCurrentStack(), BetterPlaceAnimation.this.asSeenBy);
			}

			javax.vecmath.Matrix4d pov = asSeenBy.getReferenceFrameValue().getPointOfView();

			javax.vecmath.Vector3d posAbs = getPositionEnd();
			javax.vecmath.Vector3d curPos = m_subject.getPosition();
			m_subject.setPositionRightNow(posAbs, m_asSeenBy);
			javax.vecmath.Matrix3d paMatrix = m_subject.calculatePointAt(m_asSeenBy, null, new javax.vecmath.Vector3d(0, 1, 0), null, true);
			m_subject.setPositionRightNow(curPos);

			pov.set(paMatrix);
			pov.setRow(3, posAbs.x, posAbs.y, posAbs.z, 1.0);

			pointOfView.set(pov);

			if (curPos.equals(posAbs)) {
				beginEqualsEnd = true;
			}

			super.prologue(t);
		}

		@Override
		public void update(double t) {
			if (!beginEqualsEnd) {
				super.update(t);
			}
		}

		@Override
		protected boolean affectQuaternion() {
			return false;
		}

		protected boolean followHermiteCubic() {
			return true;
		}

		protected boolean followHermiteCubicOrientation() {
			return true;
		}
	}

}
