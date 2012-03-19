/*
 * Created on Mar 23, 2004
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

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;

public class TwoShotAnimation extends AbstractPositionAnimation {
	public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF);
	public final ReferenceFrameProperty asSeenBy2 = new ReferenceFrameProperty(this, "asSeenBy2", null);
	public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1));

	private double angleToRotate = java.lang.Math.PI / 6; // 5 degrees, for now

	protected ReferenceFrame m_asSeenBy2;

	public class RuntimeTwoShotAnimation extends RuntimeAbstractPositionAnimation {
		protected ReferenceFrame m_asSeenBy2;

		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenBy2BoundingBox;
		private double m_amount;

		private edu.cmu.cs.stage3.math.Matrix33 m_cameraEndOrientation;
		private edu.cmu.cs.stage3.math.Matrix33 m_cameraBeginOrientation;

		private double m_cameraHeight;

		@Override
		public void prologue(double t) {

			m_asSeenBy = TwoShotAnimation.this.asSeenBy.getReferenceFrameValue();
			if (m_asSeenBy == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("first character value must not be null.", null, TwoShotAnimation.this.asSeenBy);
			}

			super.prologue(t);
			m_asSeenBy2 = asSeenBy2.getReferenceFrameValue();
			m_amount = amount.getNumberValue().doubleValue();

			// CLK - begin
			if (m_asSeenBy2 == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("second character value must not be null.", getCurrentStack(), asSeenBy2);
			}
			if (m_subject == null) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("subject value must not be null.", getCurrentStack(), TwoShotAnimation.this.subject);
			}
			if (m_asSeenBy2 == m_asSeenBy) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("first and second characters must be different", getCurrentStack(), asSeenBy2);
			}
			if (m_subject == m_asSeenBy || m_subject == m_asSeenBy2) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("subject value cannot be the same as either character", getCurrentStack(), TwoShotAnimation.this.subject);
			}
			if (m_amount < 0) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException("amount must be greater than 0", getCurrentStack(), amount);
			}
			// CLK - end

			m_asSeenBy2.addAbsoluteTransformationListener(this);
		}

		protected edu.cmu.cs.stage3.math.Box getBoundingBox(ReferenceFrame ref, ReferenceFrame asSeenBy) {
			edu.cmu.cs.stage3.math.Box bbox = ref.getBoundingBox(asSeenBy);
			if (bbox.getMaximum() == null) {
				bbox = new edu.cmu.cs.stage3.math.Box(ref.getPosition(asSeenBy), ref.getPosition(asSeenBy));
			}
			return bbox;
		}

		// b as in y = mx + b (although z is what matters in this case)
		protected double calculateB(double x0, double z0, double dx, double dz) {
			return z0 - dz / dx * x0;
		}

		// calculate x intersection of camera left and forward lines (b's based
		// on intersection with
		// a bounding box min or max)
		protected double calculateXIntersect(double dxLeft, double dzLeft, double bLeft, double bForward) {
			return (bForward - bLeft) / (dzLeft / dxLeft + dxLeft / dzLeft);
		}

		// calculate z intersection of camera left and forward lines (b's based
		// on intersection with
		// a bounding box min or max)
		protected double calculateZIntersect(double dxLeft, double dzLeft, double bLeft, double bForward) {
			return (dxLeft / dzLeft * bLeft + dzLeft / dxLeft * bForward) / (dxLeft / dzLeft + dzLeft / dxLeft);
		}

		protected double calculateDistance(double x1, double z1, double x2, double z2) {
			return java.lang.Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
		}

		@Override
		protected javax.vecmath.Vector3d getPositionBegin() {
			m_cameraBeginOrientation = m_subject.getOrientationAsAxes(m_asSeenBy);
			return m_subject.getPosition(edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
		}

		@Override
		protected javax.vecmath.Vector3d getPositionEnd() {

			// get the bounding boxes to set the spatial relations
			if (m_subjectBoundingBox == null) {
				m_subjectBoundingBox = getBoundingBox(m_subject, m_subject);
			}
			if (m_asSeenByBoundingBox == null) {
				m_asSeenByBoundingBox = getBoundingBox(m_asSeenBy, m_asSeenBy);
			}
			if (m_asSeenBy2BoundingBox == null) {
				m_asSeenBy2BoundingBox = getBoundingBox(m_asSeenBy2, m_asSeenBy);
			}
			m_asSeenByBoundingBox.union(m_asSeenBy2BoundingBox);

			edu.cmu.cs.stage3.alice.core.SpatialRelation sv = spatialRelation.getSpatialRelationValue();
			edu.cmu.cs.stage3.math.Matrix33 cameraEndOrientation = m_asSeenBy.getOrientationAsAxes(m_asSeenBy);
			javax.vecmath.Vector3d cameraEndPos = new javax.vecmath.Vector3d();

			// get the initial position and orientation for the camera
			if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(1, 0, 0), cameraEndOrientation.getRow(1));
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(-1, 0, 0), cameraEndOrientation.getRow(1));
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_RIGHT_OF)) {
				cameraEndPos = edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, 1), cameraEndOrientation.getRow(1));
				cameraEndOrientation.rotateY(-1.0 * angleToRotate);
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_LEFT_OF)) {
				cameraEndPos = edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, 1), cameraEndOrientation.getRow(1));
				cameraEndOrientation.rotateY(angleToRotate);
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_RIGHT_OF)) {
				cameraEndPos = edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, -1), cameraEndOrientation.getRow(1));
				cameraEndOrientation.rotateY(angleToRotate);
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_LEFT_OF)) {
				cameraEndPos = edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, -1), cameraEndOrientation.getRow(1));
				cameraEndOrientation.rotateY(-1.0 * angleToRotate);

				// these are the cases that I don't think are useful for this
				// animation, but shouldn't break
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, -1), cameraEndOrientation.getRow(1));
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, 1), cameraEndOrientation.getRow(1));
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, -1, 0), new javax.vecmath.Vector3d(-1, 0, 0));
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW)) {
				cameraEndPos = sv.getPlaceVector(1, m_subjectBoundingBox, m_asSeenByBoundingBox);
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 1, 0), new javax.vecmath.Vector3d(-1, 0, 0));
			}

			// ok, at this point, I have a camera orientation and an initial
			// position.
			// for the offset positions (front-right etc), I need to calculate
			// the correct initial camera
			// positions. For the others, I just need to figure out how far back
			// to move the camera
			// such that I can see both characters.

			// if the camera is supposed to end up above or below, this doesn't
			// all hold anymore, so
			// I'll exclude those cases for now.
			if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_LEFT_OF) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_RIGHT_OF) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_LEFT_OF) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_RIGHT_OF)) {
				// if
				// (!sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE)
				// &&
				// !sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW))
				// {
				javax.vecmath.Vector3d leftVector = cameraEndOrientation.getRow(0);
				javax.vecmath.Vector3d forwardVector = cameraEndOrientation.getRow(2);

				double bLeft = calculateB(cameraEndPos.x, cameraEndPos.z, leftVector.x, leftVector.z);

				// min bounds for bounding box
				javax.vecmath.Vector3d boxMin = m_asSeenByBoundingBox.getMinimum();
				double bForward = calculateB(boxMin.x, boxMin.z, forwardVector.x, forwardVector.z);
				double minX = calculateXIntersect(leftVector.x, leftVector.z, bLeft, bForward);
				double minZ = calculateZIntersect(leftVector.x, leftVector.z, bLeft, bForward);

				// max bounds for bounding box
				javax.vecmath.Vector3d boxMax = m_asSeenByBoundingBox.getMaximum();
				bForward = calculateB(boxMax.x, boxMax.z, forwardVector.x, forwardVector.z);
				double maxX = calculateXIntersect(leftVector.x, leftVector.z, bLeft, bForward);
				double maxZ = calculateZIntersect(leftVector.x, leftVector.z, bLeft, bForward);

				// update camera end position - NOTE: camera height is not set
				// yet
				cameraEndPos.x = minX + (maxX - minX) / 2.0;
				cameraEndPos.z = minZ + (maxZ - minZ) / 2.0;

				// figure out how far the camera should be away and place it
				double distance = calculateDistance(minX, minZ, maxX, maxZ);
				double largestDimSize = 0.0;
				largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
				if (distance > largestDimSize) {
					largestDimSize = distance;
				}

				cameraEndPos.x += -2.5 * largestDimSize * forwardVector.x;
				cameraEndPos.z += -2.5 * largestDimSize * forwardVector.z;

				// set the camera to the appropriate height
				double halfViewable = m_asSeenByBoundingBox.getHeight() * amount.doubleValue() * 1.5 / 2.0;
				double startingHeight = (1.0 - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
				m_cameraHeight = startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y;
				cameraEndPos.y = m_cameraHeight;

			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF)) {

				double largestDimSize = m_asSeenByBoundingBox.getDepth();
				if (amount.doubleValue() * m_asSeenByBoundingBox.getHeight() > largestDimSize) {
					largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
				}
				cameraEndPos = sv.getPlaceVector(largestDimSize * 2.5, m_subjectBoundingBox, m_asSeenByBoundingBox);

				double halfViewable = m_asSeenByBoundingBox.getHeight() * amount.doubleValue() * 1.5 / 2.0;
				double startingHeight = (1.0 - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
				m_cameraHeight = startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y;

				cameraEndPos.y = m_cameraHeight;
				cameraEndPos.z = m_asSeenByBoundingBox.getCenter().z;
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND)) {

				double largestDimSize = m_asSeenByBoundingBox.getWidth();
				if (amount.doubleValue() * m_asSeenByBoundingBox.getHeight() > largestDimSize) {
					largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
				}
				cameraEndPos = sv.getPlaceVector(largestDimSize * 3.0, m_subjectBoundingBox, m_asSeenByBoundingBox);

				double halfViewable = m_asSeenByBoundingBox.getHeight() * 1.5 * amount.doubleValue() / 2.0;
				double startingHeight = (1.0 - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
				m_cameraHeight = startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y;

				cameraEndPos.y = m_cameraHeight;
				cameraEndPos.x = m_asSeenByBoundingBox.getCenter().x;
			} else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE) || sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW)) {

				double largestDimSize = m_asSeenByBoundingBox.getDepth();
				if (m_asSeenByBoundingBox.getWidth() > largestDimSize) {
					largestDimSize = m_asSeenByBoundingBox.getWidth();
				}
				cameraEndPos = sv.getPlaceVector(largestDimSize * 2.2, m_subjectBoundingBox, m_asSeenByBoundingBox);

				double halfViewable = m_asSeenByBoundingBox.getDepth() / 2.0;
				m_cameraHeight = halfViewable + m_asSeenByBoundingBox.getMinimum().y;

				cameraEndPos.z = m_cameraHeight;
				cameraEndPos.x = m_asSeenByBoundingBox.getCenter().x;
			}

			m_cameraEndOrientation = cameraEndOrientation;
			return m_asSeenBy.getPosition(cameraEndPos, edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);

			/*
			 * double largestDimSize = 0.0; double offsetDistance = 0.0;
			 * javax.vecmath.Vector3d v = null;
			 * 
			 * if (
			 * (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF))
			 * ||
			 * (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF
			 * )) ) { if ( TwoShotAnimation.this.amount.doubleValue() *
			 * m_asSeenByBoundingBox.getHeight() >
			 * m_asSeenByBoundingBox.getDepth()) { largestDimSize =
			 * m_asSeenByBoundingBox.getHeight(); v = sv.getPlaceVector(
			 * largestDimSize * 2.0 *
			 * TwoShotAnimation.this.amount.doubleValue(), m_subjectBoundingBox,
			 * m_asSeenByBoundingBox ); } else { largestDimSize =
			 * m_asSeenByBoundingBox.getDepth(); v = sv.getPlaceVector(
			 * largestDimSize * 2.0, m_subjectBoundingBox, m_asSeenByBoundingBox
			 * ); } } else { if ( TwoShotAnimation.this.amount.doubleValue()*
			 * m_asSeenByBoundingBox.getHeight() >
			 * m_asSeenByBoundingBox.getWidth()) { largestDimSize =
			 * m_asSeenByBoundingBox.getHeight(); v = sv.getPlaceVector(
			 * largestDimSize * 2.0 *
			 * TwoShotAnimation.this.amount.doubleValue(), m_subjectBoundingBox,
			 * m_asSeenByBoundingBox ); } else { largestDimSize =
			 * m_asSeenByBoundingBox.getWidth(); v = sv.getPlaceVector(
			 * largestDimSize * 2.0, m_subjectBoundingBox, m_asSeenByBoundingBox
			 * ); } }
			 * 
			 * 
			 * double halfViewable = (m_asSeenByBoundingBox.getHeight() *
			 * TwoShotAnimation.this.amount.doubleValue())/ 2.0; double
			 * startingHeight = (1.0 -
			 * TwoShotAnimation.this.amount.doubleValue()) *
			 * m_asSeenByBoundingBox.getHeight(); m_cameraHeight =
			 * startingHeight + halfViewable +
			 * m_asSeenByBoundingBox.getMinimum().y;
			 * 
			 * v.y = m_cameraHeight;
			 * 
			 * javax.vecmath.Vector3d center =
			 * m_asSeenByBoundingBox.getCenter();
			 * 
			 * if (
			 * (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF))
			 * ||
			 * (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF
			 * )) ) { v.z = center.z; } else { v.x = center.x; }
			 * 
			 * return m_asSeenBy.getPosition( v,
			 * edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
			 */
		}

		@Override
		public void update(double t) {
			// todo: this probably just wants to be interpolating the quaternion
			// and shouldn't
			// be extending position animation.
			super.update(t);
			// need to interpolate the orientation as well.
			edu.cmu.cs.stage3.math.Matrix33 nextOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate(m_cameraBeginOrientation, m_cameraEndOrientation, getPortion(t));
			m_subject.setOrientationRightNow(nextOrient, m_asSeenBy);
			// javax.vecmath.Vector3d center =
			// m_asSeenByBoundingBox.getCenter();
			// m_subject.pointAtRightNow(m_asSeenBy, new
			// javax.vecmath.Vector3d(center.x,m_cameraHeight,center.z), new
			// javax.vecmath.Vector3d(0,1,0), m_asSeenBy, false);

		}

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			if (m_asSeenBy2 != null) {
				m_asSeenBy2.removeAbsoluteTransformationListener(this);
			}
		}
	}
}
