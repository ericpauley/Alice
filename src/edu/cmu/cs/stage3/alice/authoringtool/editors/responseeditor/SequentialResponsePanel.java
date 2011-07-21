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
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SequentialResponsePanel extends CompositeResponsePanel {

/*
    public SequentialResponsePanel(edu.cmu.cs.stage3.alice.core.response.DoInOrder r, int depth, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        super(r, depth, authoringToolIn);
    }
*/
    public SequentialResponsePanel(){
        super();
        headerText = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.DoInOrder.class);
    }

    public void set(edu.cmu.cs.stage3.alice.core.response.DoInOrder r, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn){
        super.set(r, authoringToolIn);
    }

    
	protected java.awt.Color getCustomBackgroundColor(){
        return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("DoInOrder");
    }

    
	protected void updateGUI(){
        super.updateGUI();
    }

    //Texture stuff

    protected static java.awt.image.BufferedImage sequentialBackgroundImage;
	protected static java.awt.Dimension sequentialBackgroundImageSize = new java.awt.Dimension( -1, -1 );

    protected void createBackgroundImage( int width, int height ) {
        sequentialBackgroundImageSize.setSize( width, height );
		sequentialBackgroundImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = (java.awt.Graphics2D)sequentialBackgroundImage.getGraphics();
		g.addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) );
        g.setColor( backgroundColor );
		g.fillRect( 0, 0, width, height );
     /*   g.setColor( backgroundLineColor );
		int spacing = 10;
		for( int x = 0; x <= width; x += spacing ) {
			g.drawLine( x, 0, x, height );
		}*/
	}

	protected void paintTextureEffect( java.awt.Graphics g, java.awt.Rectangle bounds ) {
		if( (bounds.width > sequentialBackgroundImageSize.width) || (bounds.height > sequentialBackgroundImageSize.height) ) {
			createBackgroundImage( bounds.width, bounds.height );
		}
		g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
		g.drawImage( sequentialBackgroundImage, bounds.x, bounds.y, this );
	}


}