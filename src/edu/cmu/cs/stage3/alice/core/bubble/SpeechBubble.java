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

package edu.cmu.cs.stage3.alice.core.bubble;

public class SpeechBubble extends Bubble  {
	
	protected void paintBackground( java.awt.Graphics g ) {
		java.awt.geom.Rectangle2D totalBound = getTotalBound();
		if( totalBound != null ) {
			java.awt.Point origin = getOrigin();
			java.awt.Point pixelOffset = getPixelOffset();

			int x = (int)( totalBound.getX()+pixelOffset.x - PAD_X );
			int y = (int)( totalBound.getY()+pixelOffset.y - PAD_Y );
			int width = (int)totalBound.getWidth() + PAD_X + PAD_X;
			int height = (int)totalBound.getHeight() + PAD_Y + PAD_Y;
	
			g.setColor( getBackgroundColor() );
			g.fillRoundRect( x, y, width, height, 20, 20 );
			g.setColor( java.awt.Color.black );
			g.drawRoundRect( x, y, width, height, 20, 20 );
	
			java.awt.Point connect = new java.awt.Point( x + (int)((totalBound.getWidth() + PAD_X + PAD_X)*0.333), y );
			int dy;
			if( origin.y > y ) {
				connect.translate( 0, height );
				dy = -1;
			} else {
				dy = 1;
			}
			java.awt.Polygon polygon = new java.awt.Polygon();
			polygon.addPoint( origin.x, origin.y );
			polygon.addPoint( connect.x-8, connect.y+dy );
			polygon.addPoint( connect.x+8, connect.y+dy );
	
			g.setColor( getBackgroundColor() );
			g.fillPolygon( polygon );
			g.setColor( java.awt.Color.black );
			g.drawLine( origin.x, origin.y, connect.x-8, connect.y+dy );
			g.drawLine( origin.x, origin.y, connect.x+8, connect.y+dy );
		}
	}
}