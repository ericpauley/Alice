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
public class CustomMouseAdapter extends java.awt.event.MouseAdapter {
	protected double clickDistMargin = 5.0;
	protected long clickTimeMargin = 300;
	protected long multipleClickTimeMargin = 600;

	protected java.awt.Point pressPoint = new java.awt.Point();
	protected long pressTime = 0;
	protected int clickCount = 0;

	public double getClickDistanceMargin() {
		return clickDistMargin;
	}

	public void setClickDistanceMargin( double dist ) {
		clickDistMargin = dist;
	}

	public long getClickTimeMargin() {
		return clickTimeMargin;
	}

	public void setClickTimeMargin( long time ) {
		clickTimeMargin = time;
	}

	public long getMultipleClickTimeMargin() {
		return multipleClickTimeMargin;
	}

	public void setMultipleClickTimeMargin( long time ) {
		multipleClickTimeMargin = time;
	}

	
	public void mousePressed( java.awt.event.MouseEvent ev ) {
		long dt = System.currentTimeMillis() - pressTime;
		if( dt > multipleClickTimeMargin ) {
			clickCount = 0;
		}

		pressPoint.setLocation( ev.getPoint() );
		pressTime = System.currentTimeMillis();
		if( ev.isPopupTrigger() ) {
			popupResponse( ev );
		} else {
			mouseDownResponse( ev );
		}
	}

	
	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( ev.isPopupTrigger() ) {
			popupResponse( ev );
		} else {
			mouseUpResponse( ev );
		}

		double dist = pressPoint.distance( ev.getPoint() );
		long dt = System.currentTimeMillis() - pressTime;
		if( (dist < clickDistMargin) && (dt < clickTimeMargin) ) {
			clickCount++;
			if( clickCount == 1 ) {
				singleClickResponse( ev );
			} else if( clickCount == 2 ) {
				doubleClickResponse( ev );
			} else if( clickCount == 3 ) {
				tripleClickResponse( ev );
			}
		}
	}

	protected void singleClickResponse( java.awt.event.MouseEvent ev ) {}
	protected void doubleClickResponse( java.awt.event.MouseEvent ev ) {}
	protected void tripleClickResponse( java.awt.event.MouseEvent ev ) {}
	protected void mouseUpResponse( java.awt.event.MouseEvent ev ) {}
	protected void mouseDownResponse( java.awt.event.MouseEvent ev ) {}
	protected void popupResponse( java.awt.event.MouseEvent ev ) {}
}
