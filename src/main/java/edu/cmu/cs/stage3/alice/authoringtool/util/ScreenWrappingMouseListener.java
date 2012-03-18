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

public class ScreenWrappingMouseListener implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
	protected int pressedx = 0;
	protected int pressedy = 0;
	protected int lastx = 0;
	protected int lasty = 0;
	protected int offsetx = 0;
	protected int offsety = 0;
	protected int dx = 0;
	protected int dy = 0;
	protected boolean mouseIsDown = false;
	private int leftEdge;
	private int rightEdge;
	private int topEdge;
	private int bottomEdge;
	protected boolean doWrap = false;
	private java.awt.Point tempPoint = new java.awt.Point();
	private boolean actionAborted = false;
	protected java.awt.Component component;

	synchronized public boolean isMouseDown() {
		return mouseIsDown;
	}

	synchronized public int getPressedX() {
		return pressedx;
	}

	synchronized public int getPressedY() {
		return pressedy;
	}

	synchronized public int getOffsetX() {
		return offsetx;
	}

	synchronized public int getOffsetY() {
		return offsety;
	}

	synchronized public int getDX() {
		return dx;
	}

	synchronized public int getDY() {
		return dy;
	}

	public boolean isActionAborted() {
		return actionAborted;
	}

	synchronized public void abortAction() {
		actionAborted = true;
		mouseIsDown = false;
		component.removeMouseMotionListener( this );
	}

	synchronized public void mousePressed( java.awt.event.MouseEvent ev ) {
		component = ev.getComponent();

		if( edu.cmu.cs.stage3.awt.AWTUtilities.isSetCursorLocationSupported() ) {
			doWrap = true;
		} else {
			doWrap = false;
		}

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();
		leftEdge = 0;
		rightEdge = screenWidth - 1;
		topEdge = 0;
		bottomEdge = screenHeight - 1;
		//DEBUG System.out.println( "rightEdge: " + rightEdge + ",  bottomEdge: " + bottomEdge );

		pressedx = lastx = ev.getX();
		pressedy = lasty = ev.getY();
		offsetx = 0;
		offsety = 0;

		mouseIsDown = true;

		ev.getComponent().addMouseMotionListener( this );
	}

	synchronized public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( ! actionAborted ) {
			mouseIsDown = false;
			ev.getComponent().removeMouseMotionListener( this );
		} else {
			actionAborted = false;
		}
	}

	synchronized public void mouseDragged( java.awt.event.MouseEvent ev ) {
		if( mouseIsDown  ) {
			int x = ev.getX();
			int y = ev.getY();

			offsetx = x - pressedx;
			offsety = y - pressedy;

			dx = x - lastx;
			dy = y - lasty;

			lastx = x;
			lasty = y;

			if( doWrap ) {
				tempPoint.setLocation( x, y );
				javax.swing.SwingUtilities.convertPointToScreen( tempPoint, ev.getComponent() );
				//DEBUG System.out.println( "location: " + tempPoint.x + ", " + tempPoint.y );
				if( tempPoint.x <= leftEdge ) {
					tempPoint.x = (rightEdge - 1) - (leftEdge - tempPoint.x);
					lastx += rightEdge - leftEdge;
					pressedx += rightEdge - leftEdge;
					edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
					//DEBUG System.out.println( "moved to: " + tempPoint.x + ", " + tempPoint.y );
				} else if ( tempPoint.x >= rightEdge ) {
					tempPoint.x = (leftEdge + 1) + (tempPoint.x - rightEdge);
					lastx -= rightEdge - leftEdge;
					pressedx -= rightEdge - leftEdge;
					edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
					//DEBUG System.out.println( "moved to: " + tempPoint.x + ", " + tempPoint.y );
				}
				if( tempPoint.y <= topEdge ) {
					tempPoint.y = (bottomEdge - 1) - (topEdge - tempPoint.y);
					lasty += bottomEdge - topEdge;
					pressedy += bottomEdge - topEdge;
					edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
					//DEBUG System.out.println( "moved to: " + tempPoint.x + ", " + tempPoint.y );
				} else if ( tempPoint.y >= bottomEdge ) {
					tempPoint.y = (topEdge + 1) + (tempPoint.y - bottomEdge);
					lasty -= bottomEdge - topEdge;
					pressedy -= bottomEdge - topEdge;
					edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
					//DEBUG System.out.println( "moved to: " + tempPoint.x + ", " + tempPoint.y );
				}
			}
		}
	}

	public void mouseClicked( java.awt.event.MouseEvent ev ) {}
	public void mouseEntered( java.awt.event.MouseEvent ev ) {}
	public void mouseExited( java.awt.event.MouseEvent ev ) {}
	public void mouseMoved( java.awt.event.MouseEvent ev ) {}
}