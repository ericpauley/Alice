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
public class OrthographicScrollMode extends RenderTargetManipulatorMode {
	protected double minScrollFactor = .3;
	protected double maxScrollFactor = 4.0;
	protected double scrollRampUpDistance = 10.0;

	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.core.Camera camera = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable identity = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d zeroVec = new javax.vecmath.Vector3d( 0.0, 0.0, 0.0 );
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected javax.vecmath.Vector3d cameraForward = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d cameraUp = new javax.vecmath.Vector3d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected UndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected java.awt.Point pressPoint = new java.awt.Point();

	public OrthographicScrollMode() {
		this( null, null );
	}

	public OrthographicScrollMode( UndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	
	public boolean requiresPickedObject() {
		return false;
	}

	
	public boolean hideCursorOnDrag() {
		return false;
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo ) {
		camera = (edu.cmu.cs.stage3.alice.core.Camera)pickInfo.getSource().getBonus();
		if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
			world = camera.getWorld();
			oldTransformation = camera.getLocalTransformation();
			identity.vehicle.set( world );
			pressPoint.setLocation( ev.getPoint() );
		}
	}

	
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( (camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera) && (undoRedoStack != null) && (scheduler != null) ) {
			if( ! ev.isPopupTrigger() ) { // TODO: this is a hack.  this method should never be called if the popup is triggered
				undoRedoStack.push( new PointOfViewUndoableRedoable( camera, oldTransformation, camera.getLocalTransformation(), scheduler ) );
			}
		}
	}

	
	public void mouseDragged( java.awt.event.MouseEvent ev, int dx, int dy ) {
		if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
			boolean controlDown = ev.isControlDown();
			boolean shiftDown = ev.isShiftDown();

			edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera)camera;
			double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();  //TODO: should be viewport, but not working right now
			double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
			double deltaFactor = nearClipHeightInWorld/nearClipHeightInScreen; // height of a single pixel in world space
			double offsetDist = Math.min( Math.sqrt( dx*dx + dy*dy ), scrollRampUpDistance ); // mouse offset distance
//			deltaFactor *= minScrollFactor + (maxScrollFactor - minScrollFactor)*(offsetDist/scrollRampUpDistance);  // non-linear scrolling: faster mouse == more than proportionally faster scolling

			if( controlDown ) {
				if( shiftDown ) {
					deltaFactor *= 2*maxScrollFactor;
				} else {
					deltaFactor *= maxScrollFactor;
				}
			} else if( shiftDown ) {
				deltaFactor *= minScrollFactor;
			}

			tempVec.x = -dx*deltaFactor;
			tempVec.y = dy*deltaFactor;
			tempVec.z = 0.0;
			camera.moveRightNow( tempVec );
//			orthoCamera.minimumX.set( new Double( orthoCamera.minimumX.getNumberValue().doubleValue() - ((double)dx)*deltaFactor ) );
//			orthoCamera.minimumY.set( new Double( orthoCamera.minimumY.getNumberValue().doubleValue() - ((double)dy)*deltaFactor ) );
//			orthoCamera.maximumX.set( new Double( orthoCamera.maximumX.getNumberValue().doubleValue() - ((double)dx)*deltaFactor ) );
//			orthoCamera.maximumY.set( new Double( orthoCamera.maximumY.getNumberValue().doubleValue() - ((double)dy)*deltaFactor ) );
		}
	}
}