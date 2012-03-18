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
public class OrthographicZoomMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected java.awt.Point pressPoint = new java.awt.Point();
	protected java.awt.Dimension renderSize = new java.awt.Dimension();
	protected edu.cmu.cs.stage3.alice.core.Camera camera = null;

	public OrthographicZoomMode( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		this.authoringTool = authoringTool;
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	
	public boolean requiresPickedObject() {
		return false;
	}

	
	public boolean hideCursorOnDrag() {
		return true;
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo ) {
		camera = (edu.cmu.cs.stage3.alice.core.Camera)pickInfo.getSource().getBonus();
		pressPoint.setLocation( ev.getPoint() );
	}

	
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( ev.getPoint().equals( pressPoint ) ) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "Click and drag to zoom.", "Zoom Message", javax.swing.JOptionPane.INFORMATION_MESSAGE );
		} else if( undoRedoStack != null ) {
			if( ! ev.isPopupTrigger() ) { // TODO: this is a hack.  this method should never be called if the popup is triggered
				//undoRedoStack.push( new SizeUndoableRedoable( pickedTransformable, oldSize, pickedTransformable.getSize(), scheduler ) );
			}
		}
	}

	
	public void mouseDragged( java.awt.event.MouseEvent ev, int dx, int dy ) {
		if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
			if( (dx != 0) || (dy != 0) ) {
				double divisor = ev.isShiftDown() ? 1000.0 : 50.0;
				double scaleFactor;
				if( Math.abs( dx ) > Math.abs( dy ) ) {
					scaleFactor = 1.0 - (dx)/divisor;
				} else {
					scaleFactor = 1.0 - (dy)/divisor;
				}

				edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera)camera;
				renderTarget.getAWTComponent().getSize( renderSize );

				double oldMinY = orthoCamera.minimumY.getNumberValue().doubleValue();
				double oldMaxY = orthoCamera.maximumY.getNumberValue().doubleValue();
				double oldPosX = orthoCamera.getPosition().x;
				double oldPosY = orthoCamera.getPosition().y;
				double oldHeight = oldMaxY - oldMinY;
				double pixelHeight = oldHeight/renderSize.getHeight();

				// (pressDX,pressDY) is vector from camera position to clicked point in world space
				double pressDX = (pressPoint.getX() - (renderSize.getWidth()/2.0))*pixelHeight;
				double pressDY = -(pressPoint.getY() - (renderSize.getHeight()/2.0))*pixelHeight;
				double pressX = oldPosX + pressDX;
				double pressY = oldPosY + pressDY;

				double newPosX = pressX - scaleFactor*pressDX;
				double newPosY = pressY - scaleFactor*pressDY;

				double newHeight = oldHeight*scaleFactor;
				double newMinY = -newHeight/2.0;
				double newMaxY = -newMinY;

				orthoCamera.setPositionRightNow( newPosX, newPosY, 0.0 );
				orthoCamera.minimumY.set( new Double( newMinY ) );
				orthoCamera.maximumY.set( new Double( newMaxY ) );
			}
		}
	}

	/*
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected int zoomDirection;
	protected java.awt.Rectangle zoomRect = new java.awt.Rectangle();
	protected java.awt.Point pressPoint = new java.awt.Point();
	protected java.awt.Dimension renderSize = new java.awt.Dimension();
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.core.Camera camera = null;
	protected boolean mouseIsDown = false;
	protected RectDrawer rectDrawer = new RectDrawer();

	public OrthographicZoomMode( int zoomDirection, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.zoomDirection = zoomDirection;
		this.authoringTool = authoringTool;
	}

	public void setRenderTarget( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		super.setRenderTarget( renderTarget );
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
			pressPoint.setLocation( ev.getPoint() );
			updateRect( ev );
			mouseIsDown = true;
			if( renderTarget != null ) {
				renderTarget.addRenderTargetListener( rectDrawer );
			}
		}
	}

	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
			if( ! ev.isPopupTrigger() ) { // TODO: this is a hack.  this method should never be called if the popup is triggered
				updateRect( ev );

				edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera)camera;
				renderTarget.getAWTComponent().getSize( renderSize );

				double oldMinY = orthoCamera.minimumY.getNumberValue().doubleValue();
				double oldMaxY = orthoCamera.maximumY.getNumberValue().doubleValue();
				double oldHeight = oldMaxY - oldMinY;
				double aspect = renderSize.getWidth()/renderSize.getHeight();
				double pixelHeight = oldHeight/renderSize.getHeight();

				double newMinY, newMaxY, cameraOffsetX, cameraOffsetY;
				if( zoomDirection == ZOOM_IN ) {
					double newHeight = oldHeight*(zoomRect.getHeight()/renderSize.getHeight());
					newMinY = -newHeight/2.0;
					newMaxY = -newMinY;
					cameraOffsetX = (zoomRect.getCenterX() - (renderSize.getWidth()/2.0))*pixelHeight;
					cameraOffsetY = -(zoomRect.getCenterY() - (renderSize.getHeight()/2.0))*pixelHeight;
				} else if( zoomDirection == ZOOM_OUT ) {
					double newHeight = oldHeight*(renderSize.getHeight()/zoomRect.getHeight());
					double newWidth = aspect*newHeight;
					newMinY = -newHeight/2.0;
					newMaxY = -newMinY;
					cameraOffsetX = -((zoomRect.getCenterX() - (renderSize.getWidth()/2.0))/renderSize.getWidth())*newWidth;
					cameraOffsetY = ((zoomRect.getCenterY() - (renderSize.getHeight()/2.0))/renderSize.getHeight())*newHeight;
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.util.ErrorDialog.showErrorDialog( "Illegal zoomDirection: " + zoomDirection, null );
					mouseIsDown = false;
					return;
				}

				edu.cmu.cs.stage3.alice.core.response.DoTogether zoomResponse = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation minYAnim = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				minYAnim.element.set( orthoCamera );
				minYAnim.propertyName.set( "minimumY" );
				minYAnim.value.set( new Double( newMinY ) );
				zoomResponse.componentResponses.add( minYAnim );
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation maxYAnim = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				maxYAnim.element.set( orthoCamera );
				maxYAnim.propertyName.set( "maximumY" );
				maxYAnim.value.set( new Double( newMaxY ) );
				zoomResponse.componentResponses.add( maxYAnim );
				edu.cmu.cs.stage3.alice.core.response.MoveVectorAnimation moveAnim = new edu.cmu.cs.stage3.alice.core.response.MoveVectorAnimation();
				moveAnim.subject.set( orthoCamera );
				moveAnim.vector.set( new edu.cmu.cs.stage3.math.Vector3( cameraOffsetX, cameraOffsetY, 0.0 ) );
				zoomResponse.componentResponses.add( moveAnim );

				edu.cmu.cs.stage3.alice.core.response.DoTogether undoZoomResponse = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoMinYAnim = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				undoMinYAnim.element.set( orthoCamera );
				undoMinYAnim.propertyName.set( "minimumY" );
				undoMinYAnim.value.set( new Double( oldMinY ) );
				undoZoomResponse.componentResponses.add( undoMinYAnim );
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoMaxYAnim = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				undoMaxYAnim.element.set( orthoCamera );
				undoMaxYAnim.propertyName.set( "maximumY" );
				undoMaxYAnim.value.set( new Double( oldMaxY ) );
				undoZoomResponse.componentResponses.add( undoMaxYAnim );
				edu.cmu.cs.stage3.alice.core.response.MoveVectorAnimation undoMoveAnim = new edu.cmu.cs.stage3.alice.core.response.MoveVectorAnimation();
				undoMoveAnim.subject.set( orthoCamera );
				undoMoveAnim.vector.set( new edu.cmu.cs.stage3.math.Vector3( -cameraOffsetX, -cameraOffsetY, 0.0 ) );
				undoZoomResponse.componentResponses.add( undoMoveAnim );

				edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { orthoCamera.minimumY, orthoCamera.maximumY, orthoCamera.localTransformation };

				authoringTool.performOneShot( zoomResponse, undoZoomResponse, affectedProperties );

				mouseIsDown = false;

				if( renderTarget != null ) {
					renderTarget.removeRenderTargetListener( rectDrawer );
				}
			}
		}
	}

	public void mouseDragged( java.awt.event.MouseEvent ev, int dx, int dy ) {
		updateRect( ev );
	}

	public void updateRect( java.awt.event.MouseEvent ev ) {
		zoomRect.setLocation( Math.min( pressPoint.x, ev.getX() ), Math.min( pressPoint.y, ev.getY() ) );
		zoomRect.setSize( Math.abs( pressPoint.x - ev.getX() ), Math.abs( pressPoint.y - ev.getY() ) );

		// maintain aspect ratio
		renderTarget.getAWTComponent().getSize( renderSize );
		if( (renderSize.getWidth() > 0.0) && (renderSize.getHeight() > 0.0) ) {
			double aspect = renderSize.getWidth()/renderSize.getHeight();

			if( (zoomRect.width < 1) && (zoomRect.height < 1) ) {
				//do nothing
			} else if( (zoomRect.height < 1) || ((zoomRect.getWidth()/zoomRect.getHeight()) > aspect) ) {
				zoomRect.width = (int)(zoomRect.getHeight()*aspect);
			} else if( (zoomRect.getWidth()/zoomRect.getHeight()) < aspect ) {
				zoomRect.height = (int)(zoomRect.getWidth()/aspect);
			}

			if( (zoomRect.x + zoomRect.width) < pressPoint.x ) {
				zoomRect.translate( pressPoint.x - (zoomRect.x + zoomRect.width), 0 );
			}
			if( (zoomRect.y + zoomRect.height) < pressPoint.y ) {
				zoomRect.translate( 0, pressPoint.y - (zoomRect.y + zoomRect.height) );
			}
		}

		if( zoomRect.width == 0 ) {
			zoomRect.width = 1;
		}
		if( zoomRect.height == 0 ) {
			zoomRect.height = 1;
		}

		renderTarget.markDirty();
	}

	class RectDrawer implements edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener {
		public void cleared( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent ev ) {}
		public void rendered( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent ev ) {
			java.awt.Graphics g = ev.getRenderTarget().getOffscreenGraphics();
			g.setColor( java.awt.Color.white );
			g.setXORMode( java.awt.Color.black );
			g.drawRect( zoomRect.x, zoomRect.y, zoomRect.width, zoomRect.height );
			g.dispose();
		}
	}
	*/
}