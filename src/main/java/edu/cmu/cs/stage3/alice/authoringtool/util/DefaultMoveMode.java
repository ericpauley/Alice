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
public class DefaultMoveMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable;
	protected edu.cmu.cs.stage3.alice.core.Transformable helper = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.core.Camera camera = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable identity = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d zeroVec = new javax.vecmath.Vector3d( 0.0, 0.0, 0.0 );
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected javax.vecmath.Vector3d cameraForward = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d cameraUp = new javax.vecmath.Vector3d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;

	public DefaultMoveMode() {
		this( null, null );
	}

	public DefaultMoveMode( edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler  ) {
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
		init();
	}

	private void init() {
		helper.name.set( "helper" );
	}

	
	public boolean requiresPickedObject() {
		return true;
	}

	
	public boolean hideCursorOnDrag() {
		return true;
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo ) {
		this.pickedTransformable = pickedTransformable;
		if( pickedTransformable != null ) {
			camera = (edu.cmu.cs.stage3.alice.core.Camera)pickInfo.getSource().getBonus();
			world = (edu.cmu.cs.stage3.alice.core.World)camera.getSceneGraphCamera().getRoot().getBonus();
			oldTransformation = pickedTransformable.getLocalTransformation();
			helper.vehicle.set( world );
			identity.vehicle.set( world );
		}
	}

	
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( (pickedTransformable != null) && (undoRedoStack != null)  ) {
			if( ! ev.isPopupTrigger() ) { // TODO: this is a hack.  this method should never be called if the popup is triggered
				undoRedoStack.push( new PointOfViewUndoableRedoable( pickedTransformable, oldTransformation, pickedTransformable.getLocalTransformation(), scheduler ) );
			}
		}
	}

	
	public void mouseDragged( java.awt.event.MouseEvent ev, int dx, int dy ) {
		if( pickedTransformable != null ) {
			boolean controlDown = ev.isControlDown();
			boolean shiftDown = ev.isShiftDown();

			double deltaFactor;
			if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
				edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera)camera;
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();  //TODO: should be viewport, but not working right now
				double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
				deltaFactor = nearClipHeightInWorld/nearClipHeightInScreen;

				if( controlDown ) {
					if( shiftDown ) {
						helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), camera );
						helper.setPositionRightNow( zeroVec, pickedTransformable );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -dy*.01, helper );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx*.01, pickedTransformable );
					} else {
						helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), camera );
						helper.setPositionRightNow( zeroVec, pickedTransformable );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getZAxis(), -dx*.01, helper );
					}
				} else if( shiftDown ) {
					helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), camera );
					helper.setPositionRightNow( zeroVec, pickedTransformable );
					tempVec.x = 0.0;
					tempVec.y = -dy*deltaFactor;
					tempVec.z = 0.0;
					pickedTransformable.moveRightNow( tempVec, helper );
				} else {
					helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), camera );
					helper.setPositionRightNow( zeroVec, pickedTransformable );
					tempVec.x = dx*deltaFactor;
					tempVec.y = -dy*deltaFactor;
					tempVec.z = 0.0;
					pickedTransformable.moveRightNow( tempVec, helper );
				}
			} else {
				double projectionMatrix11 = renderTarget.getProjectionMatrix( camera.getSceneGraphCamera() ).getElement( 1, 1 );
				double nearClipDist = camera.nearClippingPlaneDistance.doubleValue();
				double nearClipHeightInWorld = 2*(nearClipDist/projectionMatrix11);
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();  //TODO: should be viewport, but not working right now
				double pixelHeight = nearClipHeightInWorld/nearClipHeightInScreen;
//				double pixelHeight = nearClipHeightInWorld/300;
				double objectDist = pickedTransformable.getPosition( camera ).getLength();
				deltaFactor = (objectDist*pixelHeight)/nearClipDist;

				if( controlDown ) {
					if( shiftDown ) {
						helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), camera );
						helper.setPositionRightNow( zeroVec, pickedTransformable );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -dy*.01, helper );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx*.01, pickedTransformable );
					} else {
						helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), world );
						helper.setPositionRightNow( zeroVec, pickedTransformable );
						pickedTransformable.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx*.01, helper );
					}
				} else if( shiftDown ) {
					helper.setTransformationRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), world );
					helper.setPositionRightNow( zeroVec, pickedTransformable );
					tempVec.x = 0.0;
					tempVec.y = -dy*deltaFactor;
					tempVec.z = 0.0;
					pickedTransformable.moveRightNow( tempVec, helper );
				} else {
					javax.vecmath.Matrix4d cameraTransformation = camera.getSceneGraphTransformable().getAbsoluteTransformation();
					cameraUp.x = cameraTransformation.m10;
					cameraUp.y = cameraTransformation.m11;
					cameraUp.z = cameraTransformation.m12;
					cameraForward.x = cameraTransformation.m20;
					cameraForward.y = cameraTransformation.m21;
					cameraForward.z = cameraTransformation.m22;

					helper.setPositionRightNow( zeroVec, pickedTransformable );
					if( Math.abs( cameraForward.y ) < Math.abs( cameraUp.y ) ) { // if we're looking mostly level
						cameraForward.y = 0.0;
						helper.setOrientationRightNow( cameraForward, cameraUp, world );
					} else { // if we're looking mostly up or down
						cameraUp.y = 0.0;
						cameraForward.negate();
						helper.setOrientationRightNow( cameraUp, cameraForward, world );
					}

					tempVec.x = dx*deltaFactor;
					tempVec.y = 0.0;
					tempVec.z = -dy*deltaFactor;
					pickedTransformable.moveRightNow( tempVec, helper );
				}
			}

		}
	}
}