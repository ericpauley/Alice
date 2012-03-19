/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;

public class SymmetricPerspectiveViewVolumeDecorator extends ViewVolumeDecorator {
	private SymmetricPerspectiveCamera m_symmetricPerspectiveCamera = null;

	@Override
	protected Camera getCamera() {
		return getSymmetricPerspectiveCamera();
	}
	public SymmetricPerspectiveCamera getSymmetricPerspectiveCamera() {
		return m_symmetricPerspectiveCamera;
	}
	public void setSymmetricPerspectiveCamera(SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
		if (symmetricPerspectiveCamera != m_symmetricPerspectiveCamera) {
			m_symmetricPerspectiveCamera = symmetricPerspectiveCamera;
			markDirty();
			updateIfShowing();
		}
	}

	@Override
	protected double[] getXYNearAndXYFar(double zNear, double zFar) {
		/*
		 * Number verticalViewingAngleValue =
		 * SymmetricPerspectiveCamera.this.verticalViewingAngle
		 * .getNumberValue(); Number horizontalViewingAngleValue =
		 * SymmetricPerspectiveCamera
		 * .this.horizontalViewingAngle.getNumberValue(); double angle = 0.5;
		 * if( verticalViewingAngleValue!=null ) { angle =
		 * verticalViewingAngleValue.doubleValue()*0.5; }
		 */
		double angle = m_symmetricPerspectiveCamera.verticalViewingAngle.doubleValue(0.5);

		// angle = self._object._camera.VerticalViewingAngle
		// x, y = self._object._camera.ViewportSize
		// aspect = float(x)/float(y)
		double aspect = 4.0 / 3.0;

		double yNear = zNear * Math.tan(angle);
		double yFar = zFar * Math.tan(angle);
		double xNear = aspect * yNear;
		double xFar = aspect * yFar;
		double[] r = {xNear, yNear, xFar, yFar};
		return r;
	}
}
