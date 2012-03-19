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

public class RenderTargetOrbitManipulator extends RenderTargetPickManipulator {
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable eCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected double orbitRotationFactor;
	protected double orbitZoomFactor;
	protected double sizeFactor;
	protected boolean clippingPlaneAdjustmentEnabled = false;

	protected UndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;

	private Configuration orbitConfig = Configuration.getLocalConfiguration(RenderTargetOrbitManipulator.class.getPackage());

	public RenderTargetOrbitManipulator(edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget, UndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler) {
		super(renderTarget);
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
		helper.setName("helper");
		configInit();
	}

	public void setClippingPlaneAdjustmentEnabled(boolean enabled) {
		clippingPlaneAdjustmentEnabled = enabled;
	}

	private void configInit() {
		if (orbitConfig.getValue("renderTargetOrbitManipulator.orbitRotationFactor") == null) {
			orbitConfig.setValue("renderTargetOrbitManipulator.orbitRotationFactor", Double.toString(.02));
		}
		if (orbitConfig.getValue("renderTargetOrbitManipulator.orbitZoomFactor") == null) {
			orbitConfig.setValue("renderTargetOrbitManipulator.orbitZoomFactor", Double.toString(.05));
		}
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent ev) {
		// DEBUG System.out.println( "mousePressed" );
		if (enabled) {
			super.mousePressed(ev);

			orbitRotationFactor = Double.parseDouble(orbitConfig.getValue("renderTargetOrbitManipulator.orbitRotationFactor"));
			orbitZoomFactor = Double.parseDouble(orbitConfig.getValue("renderTargetOrbitManipulator.orbitZoomFactor"));

			if (sgPickedTransformable == null && objectsOfInterest.size() == 1) {
				ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) objectsOfInterest.iterator().next();
				sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().setIsListening(false);
				mouseIsDown = true;
			}

			if (sgPickedTransformable != null) {
				sizeFactor = Math.max(.1, ePickedTransformable.getBoundingSphere().getRadius());
				sgCamera = renderTarget.getCameras()[0];
				sgCameraTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable) sgCamera.getParent();
				eCameraTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) sgCameraTransformable.getBonus();
				sgScene = (edu.cmu.cs.stage3.alice.scenegraph.Scene) sgCamera.getRoot();

				oldTransformation = new edu.cmu.cs.stage3.math.Matrix44(sgCameraTransformable.getLocalTransformation());
				// DEBUG System.out.println( "picked: " + sgPickedTransformable
				// );
				helper.setParent(sgScene);
				sgIdentity.setParent(sgScene);
			}
		}
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent ev) {
		// DEBUG System.out.println( "mouseReleased" );
		if (mouseIsDown) {
			if (eCameraTransformable != null) {
				undoRedoStack.push(new PointOfViewUndoableRedoable(eCameraTransformable, oldTransformation, new edu.cmu.cs.stage3.math.Matrix44(sgCameraTransformable.getLocalTransformation()), scheduler));
			}
		}

		super.mouseReleased(ev);
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent ev) {
		// DEBUG System.out.println( "mouseDragged" );
		if (enabled) {
			super.mouseDragged(ev);

			if (mouseIsDown) {
				// DEBUG System.out.println( "mouseIsDown" );
				if (sgPickedTransformable != null) {
					// DEBUG System.out.println( "sgPickedTransformable: " +
					// sgPickedTransformable );

					if (clippingPlaneAdjustmentEnabled) {
						double objectRadius = ePickedTransformable.getBoundingSphere().getRadius();
						double objectDist = sgPickedTransformable.getPosition(sgCameraTransformable).getLength();
						double farDist = Math.max(objectDist * 3, objectDist + objectRadius);
						double nearDist = Math.max((objectDist - objectRadius) * .01, .0001);
						// System.out.println( "farDist: " + farDist +
						// ",  nearDist: " + nearDist );
						sgCamera.setFarClippingPlaneDistance(farDist);
					}

					boolean controlDown = ev.isControlDown();
					boolean shiftDown = ev.isShiftDown();

					tempVec.x = 0.0;
					tempVec.y = 0.0;
					tempVec.z = 0.0;
					helper.setPosition(tempVec, sgPickedTransformable);
					// TODO: handle singularity (waiting for Dennis)
					helper.pointAt(sgCameraTransformable, null, edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), null);
					helper.standUp(sgScene);

					// TODO: this shouldn't be necessary
					sgCameraTransformable.pointAt(helper, null, edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), null);

					if (controlDown) {
						if (shiftDown) {
							// TODO: handle this modifier?
						}
						// TODO: handle this modifier?
					}

					// TODO: avoid singularities
					if (shiftDown) {
						sgCameraTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), dx * orbitRotationFactor, helper);
						sgCameraTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -dy * orbitRotationFactor, helper);
					} else {
						sgCameraTransformable.translate(edu.cmu.cs.stage3.math.MathUtilities.multiply(edu.cmu.cs.stage3.math.MathUtilities.getZAxis(), dy * orbitZoomFactor * sizeFactor), sgCameraTransformable);
						sgCameraTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), dx * orbitRotationFactor, helper);
					}

					if (eCameraTransformable != null) {
						eCameraTransformable.localTransformation.set(sgCameraTransformable.getLocalTransformation());
					}
				}
			}
		}
	}
}