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

package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class ParallelResponsePanel extends CompositeResponsePanel {

	public ParallelResponsePanel() {
		super();
		headerText = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.DoTogether.class);
	}

	public void set(edu.cmu.cs.stage3.alice.core.response.DoTogether r, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
		super.set(r, authoringToolIn);
	}

	/*
	 * public
	 * ParallelResponsePanel(edu.cmu.cs.stage3.alice.core.response.DoTogether r,
	 * int depth, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
	 * authoringToolIn){ super(r, depth, authoringToolIn); }
	 */

	@Override
	protected void updateGUI() {
		super.updateGUI();
	}

	@Override
	protected java.awt.Color getCustomBackgroundColor() {
		return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("DoTogether");
	}

	// Texture stuff

	protected static java.awt.image.BufferedImage parallelBackgroundImage;
	protected static java.awt.Dimension parallelBackgroundImageSize = new java.awt.Dimension(-1, -1);

	protected void createBackgroundImage(int width, int height) {
		parallelBackgroundImageSize.setSize(width, height);
		parallelBackgroundImage = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = (java.awt.Graphics2D) parallelBackgroundImage.getGraphics();
		g.addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		/*
		 * g.setColor( backgroundLineColor ); int spacing = 10; for( int y = 0;
		 * y <= height; y += spacing ) { g.drawLine( 0, y, width, y ); }
		 */
	}

	protected void paintTextureEffect(java.awt.Graphics g, java.awt.Rectangle bounds) {
		if (bounds.width > parallelBackgroundImageSize.width || bounds.height > parallelBackgroundImageSize.height) {
			createBackgroundImage(bounds.width, bounds.height);
		}
		g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
		g.drawImage(parallelBackgroundImage, bounds.x, bounds.y, this);
	}

}