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
public class GuiNavigator extends javax.swing.JPanel implements Runnable {
	public final static int SLIDE_MODE = 1;
	public final static int DRIVE_MODE = 2;
	public final static int TILT_MODE = 3;

	public final static int SMALL_IMAGES = 1;
	public final static int LARGE_IMAGES = 2;

	protected int mode = 0;
	protected int imageSize = LARGE_IMAGES;
	protected long lastTime;
	protected edu.cmu.cs.stage3.alice.core.Transformable objectToNavigate;
	protected edu.cmu.cs.stage3.alice.core.Transformable coreHelper = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected ImagePanel slidePanel = new ImagePanel();
	protected ImagePanel drivePanel = new ImagePanel();
	protected ImagePanel tiltPanel = new ImagePanel();
	protected NavMouseListener navMouseListener = new NavMouseListener();
	protected int buffer = 4;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

	protected java.awt.Image slide = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slide" );
	protected java.awt.Image slideDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDown" );
	protected java.awt.Image slideDownLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDownLeft" );
	protected java.awt.Image slideDownRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDownRight" );
	protected java.awt.Image slideHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideHighlight" );
	protected java.awt.Image slideLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideLeft" );
	protected java.awt.Image slideRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideRight" );
	protected java.awt.Image slideUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUp" );
	protected java.awt.Image slideUpLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUpLeft" );
	protected java.awt.Image slideUpRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUpRight" );
	protected java.awt.Image drive = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/drive" );
	protected java.awt.Image driveBack = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBack" );
	protected java.awt.Image driveBackLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBackLeft" );
	protected java.awt.Image driveBackRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBackRight" );
	protected java.awt.Image driveForward = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForward" );
	protected java.awt.Image driveForwardLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForwardLeft" );
	protected java.awt.Image driveForwardRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForwardRight" );
	protected java.awt.Image driveHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveHighlight" );
	protected java.awt.Image driveLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveLeft" );
	protected java.awt.Image driveRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveRight" );
	protected java.awt.Image tilt = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tilt" );
	protected java.awt.Image tiltDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltDown" );
	protected java.awt.Image tiltHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltHighlight" );
	protected java.awt.Image tiltUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltUp" );

	public GuiNavigator() {
		setOpaque( false );

		slidePanel.setImage( slide );
		drivePanel.setImage( drive );
		tiltPanel.setImage( tilt );

		setLayout( new java.awt.GridBagLayout() );
		add( slidePanel, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );
		add( drivePanel, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );
		add( tiltPanel, new java.awt.GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );

		slidePanel.addMouseListener( navMouseListener );
		drivePanel.addMouseListener( navMouseListener );
		tiltPanel.addMouseListener( navMouseListener );

		slidePanel.setToolTipText( "Move the Camera Up, Down, Left, and Right." );
		drivePanel.setToolTipText( "Drive the Camera Forward, Backward, Left, and Right." );
		tiltPanel.setToolTipText( "Tilt the Camera Forward and Backward." );
	}

	public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
	}

	public ImagePanel getSlidePanel() {
		return slidePanel;
	}

	public ImagePanel getDrivePanel() {
		return drivePanel;
	}

	public ImagePanel getTiltPanel() {
		return tiltPanel;
	}

	public void setObjectToNavigate( edu.cmu.cs.stage3.alice.core.Transformable objectToNavigate ) {
		this.objectToNavigate = objectToNavigate;
		if( objectToNavigate != null ) {
			world = objectToNavigate.getWorld();
		} else {
			world = null;
		}
	}

	public void setImageSize( int size ) {
		if( size == SMALL_IMAGES ) {
			imageSize = size;
			slide = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slide" );
			slideDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideDown" );
			slideDownLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideDownLeft" );
			slideDownRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideDownRight" );
			slideHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideHighlight" );
			slideLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideLeft" );
			slideRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideRight" );
			slideUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideUp" );
			slideUpLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideUpLeft" );
			slideUpRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/slideUpRight" );
			drive = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/drive" );
			driveBack = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveBack" );
			driveBackLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveBackLeft" );
			driveBackRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveBackRight" );
			driveForward = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveForward" );
			driveForwardLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveForwardLeft" );
			driveForwardRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveForwardRight" );
			driveHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveHighlight" );
			driveLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveLeft" );
			driveRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/driveRight" );
			tilt = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/tilt" );
			tiltDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/tiltDown" );
			tiltHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/tiltHighlight" );
			tiltUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "smallGuiNavigator/tiltUp" );
		} else if( size == LARGE_IMAGES ) {
			imageSize = size;
			slide = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slide" );
			slideDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDown" );
			slideDownLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDownLeft" );
			slideDownRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideDownRight" );
			slideHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideHighlight" );
			slideLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideLeft" );
			slideRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideRight" );
			slideUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUp" );
			slideUpLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUpLeft" );
			slideUpRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/slideUpRight" );
			drive = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/drive" );
			driveBack = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBack" );
			driveBackLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBackLeft" );
			driveBackRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveBackRight" );
			driveForward = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForward" );
			driveForwardLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForwardLeft" );
			driveForwardRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveForwardRight" );
			driveHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveHighlight" );
			driveLeft = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveLeft" );
			driveRight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/driveRight" );
			tilt = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tilt" );
			tiltDown = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltDown" );
			tiltHighlight = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltHighlight" );
			tiltUp = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "guiNavigator/tiltUp" );
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Invalid image size constant in setImageSize: " + imageSize, null );
		}
		updateImages();
	}

	public void updateImages() {
		if( navMouseListener.isMouseDown() ) {
			int offsetx = navMouseListener.getOffsetX();
			int offsety = navMouseListener.getOffsetY();
			if( mode == SLIDE_MODE ) {
				if( offsety > buffer ) {
					if( offsetx > buffer ) {
						slidePanel.setImage( slideDownRight );
					} else if( Math.abs( offsetx ) > buffer ) {
						slidePanel.setImage( slideDownLeft );
					} else {
						slidePanel.setImage( slideDown );
					}
				} else if( Math.abs( offsety ) > buffer ) {
					if( offsetx > buffer ) {
						slidePanel.setImage( slideUpRight );
					} else if( Math.abs( offsetx ) > buffer ){
						slidePanel.setImage( slideUpLeft );
					} else {
						slidePanel.setImage( slideUp );
					}
				} else {
					if( offsetx > buffer ) {
						slidePanel.setImage( slideRight );
					} else if( Math.abs( offsetx ) > buffer ) {
						slidePanel.setImage( slideLeft );
					} else {
						slidePanel.setImage( slideHighlight );
					}
				}
				drivePanel.setImage( drive );
				tiltPanel.setImage( tilt );
			} else if( mode == DRIVE_MODE ) {
				if( offsety > buffer ) {
					if( offsetx > buffer ) {
						drivePanel.setImage( driveBackRight );
					} else if( Math.abs( offsetx ) > buffer ) {
						drivePanel.setImage( driveBackLeft );
					} else {
						drivePanel.setImage( driveBack );
					}
				} else if( Math.abs( offsety ) > buffer ) {
					if( offsetx > buffer ) {
						drivePanel.setImage( driveForwardRight );
					} else if( Math.abs( offsetx ) > buffer ){
						drivePanel.setImage( driveForwardLeft );
					} else {
						drivePanel.setImage( driveForward );
					}
				} else {
					if( offsetx > buffer ) {
						drivePanel.setImage( driveRight );
					} else if( Math.abs( offsetx ) > buffer ) {
						drivePanel.setImage( driveLeft );
					} else {
						drivePanel.setImage( driveHighlight );
					}
				}
				slidePanel.setImage( slide );
				tiltPanel.setImage( tilt );
			} else if( mode == TILT_MODE ) {
				if( offsety > buffer ) {
					tiltPanel.setImage( tiltUp );
				} else if( Math.abs( offsety ) > buffer ) {
					tiltPanel.setImage( tiltDown );
				} else {
					tiltPanel.setImage( tiltHighlight );
				}
				slidePanel.setImage( slide );
				drivePanel.setImage( drive );
			} else {
				slidePanel.setImage( slide );
				drivePanel.setImage( drive );
				tiltPanel.setImage( tilt );
			}
		} else {
			if( mode == SLIDE_MODE ) {
				slidePanel.setImage( slideHighlight );
				drivePanel.setImage( drive );
				tiltPanel.setImage( tilt );
			} else if( mode == DRIVE_MODE ) {
				slidePanel.setImage( slide );
				drivePanel.setImage( driveHighlight );
				tiltPanel.setImage( tilt );
			} else if( mode == TILT_MODE ) {
				slidePanel.setImage( slide );
				drivePanel.setImage( drive );
				tiltPanel.setImage( tiltHighlight );
			} else {
				slidePanel.setImage( slide );
				drivePanel.setImage( drive );
				tiltPanel.setImage( tilt );
			}
		}
	}

	public void run() {
		int offsetx = navMouseListener.getOffsetX();
		int offsety = navMouseListener.getOffsetY();

		long time = System.currentTimeMillis();
		long deltaTime = time - lastTime;
//		//TODO: Take this out
//		if (true){
//				throw new NullPointerException("AAARGH");
//		}
		
		
		if( deltaTime > 0 ) {
			double dt = deltaTime*0.001;
			tempVec.x = 0.0;
			tempVec.y = 0.0;
			tempVec.z = 0.0;
			javax.vecmath.Matrix3d m;

			coreHelper.setPositionRightNow( tempVec, objectToNavigate );
			m = objectToNavigate.getOrientationAsAxes( GuiNavigator.this.world );

			tempVec.x = m.m20;
			tempVec.y = 0.0;
			tempVec.z = m.m22;

			coreHelper.setOrientationRightNow( tempVec, edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), GuiNavigator.this.world );

			switch( mode ) {
				case SLIDE_MODE:
					tempVec.x = 0.0;
					tempVec.y = -0.1*offsety*dt;
					tempVec.z = 0.0;
					objectToNavigate.moveRightNow( tempVec, GuiNavigator.this.world );

					tempVec.x = 0.05*offsetx*dt;
					tempVec.y = 0.0;
					tempVec.z = 0.0;
					objectToNavigate.moveRightNow( tempVec, objectToNavigate );
					break;
				case DRIVE_MODE:
					if( objectToNavigate.getOrientationAsAxes( GuiNavigator.this.world ).m11 < 0.0 ) { // if we're upside down, we need to adjust the helper to do the right thing
						tempVec.x = -1.0;
						tempVec.y = -1.0;
						tempVec.z = 1.0;
						coreHelper.scaleSpaceRightNow( tempVec, null );
					}

					if( navMouseListener.shiftIsDown ) {
						tempVec.x = 0.0; tempVec.y = -0.05*offsety*dt; tempVec.z = 0.0;
						objectToNavigate.moveRightNow( tempVec, GuiNavigator.this.world );
					} else if( navMouseListener.controlIsDown ) {
						objectToNavigate.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), .001*offsetx*dt, coreHelper );
					} else {
						tempVec.x = 0.0; tempVec.y = 0.0; tempVec.z = -0.05*offsety*dt;
						objectToNavigate.moveRightNow( tempVec, coreHelper );
						objectToNavigate.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), .0006*offsetx*dt, coreHelper );
					}
					break;
				case TILT_MODE:
					objectToNavigate.rotateRightNow( edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -.001*offsety*dt, objectToNavigate );
					break;
			}
		}
		lastTime = time;
	}

	class NavMouseListener extends edu.cmu.cs.stage3.alice.authoringtool.util.ScreenWrappingMouseListener {
		protected java.awt.Component source = null;
		public boolean shiftIsDown = false;
		public boolean controlIsDown = false;

		protected void setMode( java.awt.event.MouseEvent ev ) {
			if( ev == null ) {
				mode = 0;
			} else {
				if( ev.getComponent() == GuiNavigator.this.slidePanel ) {
					mode = SLIDE_MODE;
				} else if( ev.getComponent() == GuiNavigator.this.drivePanel ) {
					mode = DRIVE_MODE;
				} else if( ev.getComponent() == GuiNavigator.this.tiltPanel ) {
					mode = TILT_MODE;
				}
			}
			//System.out.println( "setMode( " + ev + " ): " + mode );
		}

		
		public void mousePressed( java.awt.event.MouseEvent ev ) {
			super.mousePressed( ev );

			//povTree.clearSelection();

			GuiNavigator.this.lastTime = System.currentTimeMillis();

			authoringTool.getUndoRedoStack().setIsListening( false );
			oldTransformation = objectToNavigate.getLocalTransformation();
			setMode( ev );
			if( imageSize == LARGE_IMAGES ) {
				if( mode == SLIDE_MODE ) {
					pressedx = 39;
					pressedy = 38;
				} else if( mode == DRIVE_MODE ) {
					pressedx = 58;
					pressedy = 28;
				} else if( mode == TILT_MODE ) {
					pressedx = 31;
					pressedy = 42;
				}
			} else if( imageSize == SMALL_IMAGES ) {
				if( mode == SLIDE_MODE ) {
					pressedx = 19;
					pressedy = 18;
				} else if( mode == DRIVE_MODE ) {
					pressedx = 29;
					pressedy = 13;
				} else if( mode == TILT_MODE ) {
					pressedx = 14;
					pressedy = 20;
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Invalid image size constant in NavMouseListener: " + imageSize, null );
			}

			super.mouseDragged( ev );

			edu.cmu.cs.stage3.alice.scenegraph.Container root = GuiNavigator.this.world.getSceneGraphScene();

			coreHelper.vehicle.set( GuiNavigator.this.world );
			coreHelper.setTransformationRightNow( GuiNavigator.this.objectToNavigate.getTransformation( GuiNavigator.this.world ), GuiNavigator.this.world );

			shiftIsDown = ev.isShiftDown();
			controlIsDown = ev.isControlDown();

			authoringTool.getScheduler().addEachFrameRunnable( GuiNavigator.this );
			GuiNavigator.this.updateImages();
		}

		
		public void mouseReleased( java.awt.event.MouseEvent ev ) {
			super.mouseReleased( ev );

			authoringTool.getScheduler().removeEachFrameRunnable( GuiNavigator.this );

			authoringTool.getUndoRedoStack().push( new edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable( GuiNavigator.this.objectToNavigate, oldTransformation, GuiNavigator.this.objectToNavigate.getLocalTransformation(), authoringTool.getOneShotScheduler() ) );
			authoringTool.getUndoRedoStack().setIsListening( true );
			if( ev.getComponent().contains( ev.getPoint() ) ) {
				setMode( ev );
			} else {
				setMode( null );
			}
			GuiNavigator.this.updateImages();
		}

		
		public void mouseDragged( java.awt.event.MouseEvent ev ) {
			super.mouseDragged( ev );

			shiftIsDown = ev.isShiftDown();
			controlIsDown = ev.isControlDown();
			setMode( ev );
			GuiNavigator.this.updateImages();
		}

		
		public void mouseEntered( java.awt.event.MouseEvent ev ) {
			setMode( ev );
			GuiNavigator.this.updateImages();
		}
		
		public void mouseExited( java.awt.event.MouseEvent ev ) {
			setMode( null );
			GuiNavigator.this.updateImages();
		}
		
		public void mouseMoved( java.awt.event.MouseEvent ev ) {
			setMode( ev );
			GuiNavigator.this.updateImages();
		}
		
		public void mouseClicked( java.awt.event.MouseEvent ev ) {}
	}
}