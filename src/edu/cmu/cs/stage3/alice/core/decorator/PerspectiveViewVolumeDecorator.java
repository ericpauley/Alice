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
import edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera;

public class PerspectiveViewVolumeDecorator extends ViewVolumeDecorator {
	private PerspectiveCamera m_perspectiveCamera = null;

	
	protected Camera getCamera() {
		return getPerspectiveCamera();
	}
	public PerspectiveCamera getPerspectiveCamera() {
		return m_perspectiveCamera;
	}
	public void setPerspectiveCamera( PerspectiveCamera perspectiveCamera ) {
		if( perspectiveCamera != m_perspectiveCamera ) {
			m_perspectiveCamera = perspectiveCamera;
            markDirty();
			updateIfShowing();
		}
	}
	
	protected double[] getXYNearAndXYFar( double zNear, double zFar ) {
		//todo
		double angle = 0.5;
		double aspect = 4.0/3.0;
		double yNear = zNear * Math.tan( angle );
		double yFar = zFar * Math.tan( angle );
		double xNear = aspect * yNear;
		double xFar = aspect * yFar;
		double[] r = { xNear, yNear, xFar, yFar };
		return r;
	}
}
