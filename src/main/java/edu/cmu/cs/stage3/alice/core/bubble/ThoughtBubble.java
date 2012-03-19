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

public class ThoughtBubble extends Bubble {

	private void paintOval(java.awt.Graphics g, int centerX, int centerY, int width, int height) {
		int x = centerX - width / 2;
		int y = centerY - width / 2;
		g.setColor(getBackgroundColor());
		g.fillOval(x, y, width, height);
		g.setColor(java.awt.Color.black);
		g.drawOval(x, y, width, height);
	}

	@Override
	protected void paintBackground(java.awt.Graphics g) {
		java.awt.geom.Rectangle2D totalBound = getTotalBound();
		java.awt.Point origin = getOrigin();
		java.awt.Point pixelOffset = getPixelOffset();

		if (totalBound != null) {

			int x = (int) (totalBound.getX() + pixelOffset.x - PAD_X);
			int y = (int) (totalBound.getY() + pixelOffset.y - PAD_Y);
			int width = (int) totalBound.getWidth() + PAD_X + PAD_X;
			int height = (int) totalBound.getHeight() + PAD_Y + PAD_Y;

			g.setColor(getBackgroundColor());
			g.fillRoundRect(x, y, width, height, 20, 20);
			g.setColor(java.awt.Color.black);
			g.drawRoundRect(x, y, width, height, 20, 20);

			java.awt.Point connect = new java.awt.Point(x + (int) ((totalBound.getWidth() + PAD_X + PAD_X) * 0.33), y);

			if (origin.y > y) {
				connect.translate(0, height + 6);
			}

			width = 32;
			height = 24;

			paintOval(g, connect.x, connect.y, width, height);

			width -= 6;
			height -= 6;

			paintOval(g, (connect.x + origin.x) / 2, (connect.y + origin.y) / 2, width, height);

			width -= 6;
			height -= 6;

			paintOval(g, origin.x, origin.y, width, height);
		}
	}
}