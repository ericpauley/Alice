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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class RaiseLowerMode extends DefaultMoveMode {
	public RaiseLowerMode(edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler) {
		super(undoRedoStack, scheduler);
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent ev, int dx, int dy) {
		if (pickedTransformable != null) {
			double deltaFactor;
			if (camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera) {
				edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera) camera;
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight(); // TODO:
																							// should
																							// be
																							// viewport,
																							// but
																							// not
																							// working
																							// right
																							// now
				double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
				deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
			} else {
				double projectionMatrix11 = renderTarget.getProjectionMatrix(camera.getSceneGraphCamera()).getElement(1, 1);
				double nearClipDist = camera.nearClippingPlaneDistance.doubleValue();
				double nearClipHeightInWorld = 2 * (nearClipDist / projectionMatrix11);
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight(); // TODO:
																							// should
																							// be
																							// viewport,
																							// but
																							// not
																							// working
																							// right
																							// now
				double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
				double objectDist = pickedTransformable.getPosition(camera).getLength();
				deltaFactor = objectDist * pixelHeight / nearClipDist;
			}

			helper.setTransformationRightNow(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), world);
			helper.setPositionRightNow(zeroVec, pickedTransformable);
			tempVec.x = 0.0;
			tempVec.y = -dy * deltaFactor;
			tempVec.z = 0.0;
			pickedTransformable.moveRightNow(tempVec, helper);
		}
	}
}