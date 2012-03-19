/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.math.Quaternion;

/**
 * @author caitlink
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class LookDirectionAnimation extends OrientationAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.DirectionProperty lookDirection = new edu.cmu.cs.stage3.alice.core.property.DirectionProperty(this, "lookDirection", Direction.FORWARD);
	public final edu.cmu.cs.stage3.alice.core.property.NumberProperty amount = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "amount", new Double(0.2));

	public class RuntimeLookDirectionAnimation extends RuntimeOrientationAnimation {
		edu.cmu.cs.stage3.alice.core.Transformable head = null;
		Direction m_lookDirection = null;

		Quaternion initialQuat = null;
		Quaternion targetQuat = null;

		@Override
		public void prologue(double t) {
			super.prologue(t);
			m_lookDirection = lookDirection.getDirectionValue();
			findHead();
			if (head != null) {
				m_subject = head;
			}
			initialQuat = head.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame) head.getParent());
			targetQuat = null;

		}

		@Override
		public void update(double t) {
			if (targetQuat == null) {
				targetQuat = getTargetQuaternion();
			}

			edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate(initialQuat, targetQuat, getPortion(t));
			head.setOrientationRightNow(q, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) head.getParent());
		}

		@Override
		public edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {

			edu.cmu.cs.stage3.math.Quaternion quat = head.getOrientationAsQuaternion((edu.cmu.cs.stage3.alice.core.ReferenceFrame) head.getParent());
			edu.cmu.cs.stage3.math.Matrix33 orient = new edu.cmu.cs.stage3.math.Matrix33();

			double amt = amount.doubleValue();
			if (m_lookDirection.equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) {} else if (m_lookDirection.equals(edu.cmu.cs.stage3.alice.core.Direction.UP)) {
				orient.rotateX(-1.0 * amt * java.lang.Math.PI);
			} else if (m_lookDirection.equals(edu.cmu.cs.stage3.alice.core.Direction.DOWN)) {
				orient.rotateX(amt * java.lang.Math.PI);
			} else if (m_lookDirection.equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT)) {
				orient.rotateY(amt * java.lang.Math.PI);
			} else if (m_lookDirection.equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT)) {
				orient.rotateY(-1.0 * amt * java.lang.Math.PI);
			}
			quat.setMatrix33(orient);
			return quat;
		}

		public void findHead() {
			edu.cmu.cs.stage3.alice.core.Element[] heads = m_subject.search(new ElementNameContainsCriterion("head"));

			if (heads.length > 0) {
				head = (edu.cmu.cs.stage3.alice.core.Transformable) heads[0];
			}
		}
	}
}
