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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class CameraProxy extends ComponentProxy {
	protected abstract void onNearClippingPlaneDistanceChange(double value);
	protected abstract void onFarClippingPlaneDistanceChange(double value);
	protected abstract void onBackgroundChange(BackgroundProxy value);

	@Override
	protected void changed(edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value) {
		if (property == edu.cmu.cs.stage3.alice.scenegraph.Camera.NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
			onNearClippingPlaneDistanceChange(((Double) value).doubleValue());
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Camera.FAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
			onFarClippingPlaneDistanceChange(((Double) value).doubleValue());
		} else if (property == edu.cmu.cs.stage3.alice.scenegraph.Camera.BACKGROUND_PROPERTY) {
			onBackgroundChange((BackgroundProxy) getProxyFor((edu.cmu.cs.stage3.alice.scenegraph.Background) value));
		} else {
			super.changed(property, value);
		}
	}
}
