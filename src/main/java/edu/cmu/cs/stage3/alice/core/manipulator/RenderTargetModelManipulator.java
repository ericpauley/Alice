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

package edu.cmu.cs.stage3.alice.core.manipulator;

public class RenderTargetModelManipulator extends RenderTargetPickManipulator {
	public final static int GROUND_PLANE_MODE = 1;
	public final static int CAMERA_PLANE_MODE = 2;
	public final static int DEFAULT_MODE = GROUND_PLANE_MODE;

	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d zeroVec = new javax.vecmath.Vector3d( 0.0, 0.0, 0.0 );
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected javax.vecmath.Vector3d cameraForward = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d cameraUp = new javax.vecmath.Vector3d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected int mode = DEFAULT_MODE;
	protected boolean popupEnabled = false;

	public RenderTargetModelManipulator( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		super( renderTarget );
		setMode( DEFAULT_MODE );
		helper.setName( "helper" );

		//setPopupEnabled( true );
	}

	public void setMode( int mode ) {
		this.mode = mode;
		if( mode == GROUND_PLANE_MODE ) {
			this.setHideCursorOnDrag( true );
		} else if( mode == CAMERA_PLANE_MODE ) {
			this.setHideCursorOnDrag( false );
		}
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev ) {
		if( enabled ) {
			super.mousePressed( ev );

			if( (ePickedTransformable != null) &&  (! ePickedTransformable.doEventsStopAscending()) ) {
				abortAction();
			} else {
				if( sgPickedTransformable != null ) {
					sgCamera = renderTarget.getCameras()[0];  //TODO: handle multiple viewports?
					sgCameraTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable)sgCamera.getParent();
					sgScene = (edu.cmu.cs.stage3.alice.scenegraph.Scene)sgCamera.getRoot();

					oldTransformation = new edu.cmu.cs.stage3.math.Matrix44( sgPickedTransformable.getLocalTransformation() );
					//DEBUG System.out.println( "picked: " + sgPickedTransformable );
					helper.setParent( sgScene );
					sgIdentity.setParent( sgScene );
				}
			}
		}
	}

	//commented out by dennisc
	/*
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( (ePickedTransformable != null) && (! isActionAborted()) ) {
			if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null ) {
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack() != null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().push( new PointOfViewUndoableRedoable( ePickedTransformable, oldTransformation, new edu.cmu.cs.stage3.math.Matrix44( sgPickedTransformable.getLocalTransformation() ), edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getOneShotScheduler() ) );
				}
			}
		}

		super.mouseReleased( ev );
	}
	*/

	
	public void mouseDragged( java.awt.event.MouseEvent ev ) {
		if( enabled ) {
			super.mouseDragged( ev );

			if( mouseIsDown ) {
				if( sgPickedTransformable != null ) {
					double deltaFactor;
					if( sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera ) {
						edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera)sgCamera;
						double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();  //TODO: should be viewport, but not working right now
						double nearClipHeightInWorld = orthoCamera.getPlane()[3] - orthoCamera.getPlane()[1];
						deltaFactor = nearClipHeightInWorld/nearClipHeightInScreen;
					} else {
						double projectionMatrix11 = renderTarget.getProjectionMatrix( sgCamera ).getElement( 1, 1 );
						double nearClipDist = sgCamera.getNearClippingPlaneDistance();
						double nearClipHeightInWorld = 2*(nearClipDist/projectionMatrix11);
						double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();  //TODO: should be viewport, but not working right now
						double pixelHeight = nearClipHeightInWorld/nearClipHeightInScreen;
						double objectDist = sgPickedTransformable.getPosition( sgCameraTransformable ).getLength();
						deltaFactor = (objectDist*pixelHeight)/nearClipDist;
					}

					boolean controlDown = ev.isControlDown();
					boolean shiftDown = ev.isShiftDown();

					if( mode == GROUND_PLANE_MODE ) {
						if( controlDown ) {
							if( shiftDown ) {
								helper.setTransformation( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgCameraTransformable );
								helper.setPosition( zeroVec, sgPickedTransformable );
								sgPickedTransformable.rotate( edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -dy*.01, helper );
								sgPickedTransformable.rotate( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx*.01, sgPickedTransformable );
							} else {
								helper.setTransformation( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgScene );
								helper.setPosition( zeroVec, sgPickedTransformable );
								sgPickedTransformable.rotate( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx*.01, helper );
							}
						} else if( shiftDown ) {
							helper.setTransformation( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgScene );
							helper.setPosition( zeroVec, sgPickedTransformable );
							tempVec.x = 0.0;
							tempVec.y = -dy*deltaFactor;
							tempVec.z = 0.0;
							sgPickedTransformable.translate( tempVec, helper );
						} else {
							javax.vecmath.Matrix4d cameraTransformation = sgCameraTransformable.getAbsoluteTransformation();
							cameraUp.x = cameraTransformation.m10;
							cameraUp.y = cameraTransformation.m11;
							cameraUp.z = cameraTransformation.m12;
							cameraForward.x = cameraTransformation.m20;
							cameraForward.y = cameraTransformation.m21;
							cameraForward.z = cameraTransformation.m22;

							helper.setPosition( zeroVec, sgPickedTransformable );
							if( Math.abs( cameraForward.y ) < Math.abs( cameraUp.y ) ) { // if we're looking mostly level
								cameraForward.y = 0.0;
								helper.setOrientation( cameraForward, cameraUp, sgScene );
							} else { // if we're looking mostly up or down
								cameraUp.y = 0.0;
								cameraForward.negate();
								helper.setOrientation( cameraUp, cameraForward, sgScene );
							}

							tempVec.x = dx*deltaFactor;
							tempVec.y = 0.0;
							tempVec.z = -dy*deltaFactor;
							sgPickedTransformable.translate( tempVec, helper );
						}
					} else if( mode == CAMERA_PLANE_MODE ) {
						if( controlDown ) {
							if( shiftDown ) {
								//TODO?
							} else {
								helper.setTransformation( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgCameraTransformable );
								helper.setPosition( zeroVec, sgPickedTransformable );
								sgPickedTransformable.rotate( edu.cmu.cs.stage3.math.MathUtilities.getZAxis(), -dx*.01, helper );
							}
						} else if( shiftDown ) {
							java.awt.Point p = ev.getPoint();
							int bigdx = p.x - originalMousePoint.x;
							int bigdy = p.y - originalMousePoint.y;
							sgPickedTransformable.setLocalTransformation( oldTransformation );
							if( Math.abs( bigdx ) > Math.abs( bigdy ) ) {
								tempVec.x = bigdx*deltaFactor;
								tempVec.y = 0.0;
							} else {
								tempVec.x = 0.0;
								tempVec.y = -bigdy*deltaFactor;
							}
							tempVec.z = 0.0;
							sgPickedTransformable.translate( tempVec, sgCameraTransformable );
						} else {
							tempVec.x = dx*deltaFactor;
							tempVec.y = -dy*deltaFactor;
							tempVec.z = 0.0;
							sgPickedTransformable.translate( tempVec, sgCameraTransformable );
						}
					}

					if( ePickedTransformable != null ) {
						ePickedTransformable.localTransformation.set( sgPickedTransformable.getLocalTransformation() );
					}
				}
			}
		}
	}
}
