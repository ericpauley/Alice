/*
 * Created on Aug 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;

/**
 * @author caitlink
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class StraightenAnimation extends TransformAnimation {

	public class RuntimeStraightenAnimation extends RuntimeTransformAnimation {
		java.util.Vector bodyPartInitialOrientations = null;
		java.util.Vector bodyParts = null;

		edu.cmu.cs.stage3.math.Matrix33 normalOrientation = new edu.cmu.cs.stage3.math.Matrix33();

		@Override
		public void prologue(double t) {
			super.prologue(t);
			bodyPartInitialOrientations = new java.util.Vector();
			bodyParts = new java.util.Vector();
			normalOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(0, 0, 1), new javax.vecmath.Vector3d(0, 1, 0));

			if (m_subject != null) {
				if (!(m_subject.getParent() instanceof edu.cmu.cs.stage3.alice.core.World)) {
					addBodyPart(m_subject); // we want to straighten top level
											// object too.
				}
				findChildren(m_subject);
			}

		}

		@Override
		public void update(double t) {
			for (int i = 0; i < bodyPartInitialOrientations.size(); i++) {
				setOrientation((edu.cmu.cs.stage3.alice.core.Transformable) bodyParts.elementAt(i), (edu.cmu.cs.stage3.math.Matrix33) bodyPartInitialOrientations.elementAt(i), normalOrientation, getPortion(t));
			}

			super.update(t);
		}

		private void findChildren(edu.cmu.cs.stage3.alice.core.Transformable part) {
			edu.cmu.cs.stage3.alice.core.Element[] kids = part.getChildren(edu.cmu.cs.stage3.alice.core.Transformable.class);
			for (Element kid : kids) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable) kid;
				addBodyPart(trans);

				if (trans.getChildCount() > 0) {
					findChildren(trans);
				}
			}
		}

		private void addBodyPart(edu.cmu.cs.stage3.alice.core.Transformable partToAdd) {
			bodyPartInitialOrientations.addElement(partToAdd.getOrientationAsAxes((ReferenceFrame) partToAdd.getParent()));
			bodyParts.addElement(partToAdd);
		}

		private void setOrientation(edu.cmu.cs.stage3.alice.core.Transformable part, edu.cmu.cs.stage3.math.Matrix33 initialOrient, edu.cmu.cs.stage3.math.Matrix33 finalOrient, double portion) {
			// System.out.println(portion);
			edu.cmu.cs.stage3.math.Matrix33 currentOrient = edu.cmu.cs.stage3.math.Matrix33.interpolate(initialOrient, finalOrient, portion);
			if (part != null) {

				part.setOrientationRightNow(currentOrient, (ReferenceFrame) part.getParent());
			}
		}
	}

}
