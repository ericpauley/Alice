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
public class GUIEffects {
	public static java.awt.image.ImageObserver allBitsObserver = new java.awt.image.ImageObserver() {
		public boolean imageUpdate( java.awt.Image image, int infoflags, int x, int y, int width, int height ) {
			if( (infoflags & java.awt.image.ImageObserver.ALLBITS) > 0 ) {
				return false;
			} else {
				return true;
			}
		}
	};
	public static java.awt.image.ImageObserver sizeObserver = new java.awt.image.ImageObserver() {
		public boolean imageUpdate( java.awt.Image img, int infoflags, int x, int y, int width, int height ) {
			return (infoflags & java.awt.image.ImageObserver.WIDTH & java.awt.image.ImageObserver.HEIGHT) > 0;
		}
	};

	private static java.awt.Color disabledBackgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsDisabledBackground" );
	private static java.awt.Color disabledLineColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsDisabledLine" );
	private static java.awt.image.BufferedImage disabledImage;
	private static java.awt.Dimension disabledImageSize = new java.awt.Dimension( -1, -1 );
	private static java.awt.Color shadowColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsShadow" );
	private static java.awt.Color edgeColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsEdge" );
	private static int shadowSteps = 4;
	private static double dr = edgeColor.getRed() - shadowColor.getRed();
	private static double dg = edgeColor.getGreen() - shadowColor.getGreen();
	private static double db = edgeColor.getBlue() - shadowColor.getBlue();
	private static double da = edgeColor.getAlpha() - shadowColor.getAlpha();
	private static java.awt.Color troughHighlightColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsTroughHighlight" );
	private static java.awt.Color troughShadowColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "guiEffectsTroughShadow" );

	private static void createDisabledImage( int width, int height ) {
		disabledImageSize.setSize( width, height );
		disabledImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = (java.awt.Graphics2D)disabledImage.getGraphics();
		g.addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) );
		g.setColor( disabledBackgroundColor );
		g.fillRect( 0, 0, width, height );
		g.setColor( disabledLineColor );
		double slope = 2.0;
		int xOffset = (int)(height/slope);
		int spacing = 10;
		for( int x = 0; x <= width + xOffset; x += spacing ) {
			g.drawLine( x, 0, x - xOffset, height );
		}
	}

	public static void paintDisabledEffect( java.awt.Graphics g, java.awt.Rectangle bounds ) {
		if( (bounds.width > disabledImageSize.width) || (bounds.height > disabledImageSize.height) ) {
			createDisabledImage( bounds.width, bounds.height );
		}
		g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
		g.drawImage( disabledImage, bounds.x, bounds.y, allBitsObserver );
	}

	public static java.awt.image.BufferedImage getImageScaledAndCropped( java.awt.Image inputImage, double scaleFactor, java.awt.Rectangle cropRect ) {
		java.awt.image.BufferedImage inputBufferedImage = new java.awt.image.BufferedImage( inputImage.getWidth( sizeObserver ), inputImage.getHeight( sizeObserver ), java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = inputBufferedImage.createGraphics();
		g.drawImage( inputImage, 0, 0, allBitsObserver );

		java.awt.image.AffineTransformOp scaleOp = new java.awt.image.AffineTransformOp( java.awt.geom.AffineTransform.getScaleInstance( scaleFactor, scaleFactor ), java.awt.image.AffineTransformOp.TYPE_BILINEAR );
		java.awt.image.BufferedImage scaledImage = scaleOp.filter( inputBufferedImage, null );
		scaledImage = scaledImage.getSubimage( cropRect.x, cropRect.y, Math.min( cropRect.width, scaledImage.getWidth() - cropRect.x ), Math.min( cropRect.height, scaledImage.getHeight() - cropRect.y ) );

		java.awt.image.BufferedImage outputImage;
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null ) {
			outputImage = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage( scaledImage.getWidth( sizeObserver ), scaledImage.getHeight( sizeObserver ), java.awt.Transparency.TRANSLUCENT );
		} else {
			outputImage = new java.awt.image.BufferedImage( scaledImage.getWidth( sizeObserver ), scaledImage.getHeight( sizeObserver ), java.awt.image.BufferedImage.TYPE_INT_ARGB );
		}
		g = outputImage.createGraphics();
		g.drawImage( scaledImage, 0, 0, allBitsObserver );

		return outputImage;
	}

	public static java.awt.image.BufferedImage getImageScaledToLongestDimension( java.awt.Image inputImage, int longestDimension ) {
		int width = inputImage.getWidth( GUIEffects.sizeObserver );
		int height = inputImage.getHeight( GUIEffects.sizeObserver );

		double scaleFactor = (width > height) ? (((double)longestDimension)/((double)width)) : (((double)longestDimension)/((double)height));

		java.awt.image.BufferedImage inputBufferedImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		java.awt.Graphics2D g = inputBufferedImage.createGraphics();
		g.drawImage( inputImage, 0, 0, GUIEffects.allBitsObserver );

		java.awt.image.AffineTransformOp scaleOp = new java.awt.image.AffineTransformOp( java.awt.geom.AffineTransform.getScaleInstance( scaleFactor, scaleFactor ), java.awt.image.AffineTransformOp.TYPE_BILINEAR );
		java.awt.image.BufferedImage scaledImage = scaleOp.filter( inputBufferedImage, null );

		return scaledImage;
	}

	public static java.awt.image.BufferedImage getImageWithDropShadow( java.awt.Image inputImage, int xOffset, int yOffset, int arcWidth, int arcHeight ) {
		int width = inputImage.getWidth( sizeObserver );
		int height = inputImage.getHeight( sizeObserver );
		java.awt.Rectangle imageBounds = new java.awt.Rectangle( (xOffset > 0) ? 0 : -xOffset + shadowSteps, (yOffset > 0) ? 0 : -yOffset + shadowSteps, width, height );
		java.awt.Rectangle shadowBounds = new java.awt.Rectangle( (xOffset > 0) ? xOffset - shadowSteps: 0, (yOffset > 0) ? yOffset - shadowSteps: 0, width + (shadowSteps*2), height + (shadowSteps*2) );

		java.awt.image.BufferedImage outputImage;
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null ) {
			outputImage = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage( width + Math.abs( xOffset ) + shadowSteps, height + Math.abs( yOffset ) + shadowSteps, java.awt.Transparency.TRANSLUCENT );
		} else {
			outputImage = new java.awt.image.BufferedImage( width + Math.abs( xOffset ) + shadowSteps, height + Math.abs( yOffset ) + shadowSteps, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		}
		java.awt.Graphics2D g = outputImage.createGraphics();
		paintDropShadow( g, shadowBounds, arcWidth, arcHeight );
		g.drawImage( inputImage, imageBounds.x, imageBounds.y, allBitsObserver );

		return outputImage;
	}

	public static void paintDropShadow( java.awt.Graphics g, java.awt.Rectangle bounds, int arcWidth, int arcHeight ) {
		if( g instanceof java.awt.Graphics2D ) {
			((java.awt.Graphics2D)g).setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
		}

		java.awt.Color c = new java.awt.Color( edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), edgeColor.getAlpha() );
		for( int i = 0; i < shadowSteps; i++ ) {
			double portion = ((double)i)/((double)shadowSteps);
			c = new java.awt.Color( edgeColor.getRed() - (int)(portion*dr),
									edgeColor.getGreen() - (int)(portion*dg),
									edgeColor.getBlue() - (int)(portion*db),
									edgeColor.getAlpha() - (int)(portion*da) );
			g.setColor( c );
			g.drawRoundRect( bounds.x, bounds.y, bounds.width - 1, bounds.height -1, arcWidth, arcHeight );
			bounds.x++;
			bounds.y++;
			bounds.width -= 2;
			bounds.height -= 2;
		}

		g.setColor( shadowColor );
		g.fillRoundRect( bounds.x, bounds.y, bounds.width, bounds.height, arcWidth, arcHeight );
	}

	public static java.awt.image.BufferedImage getImageWithColoredBorder( java.awt.Image inputImage, java.awt.Color color ) {
		int width = inputImage.getWidth( sizeObserver );
		int height = inputImage.getHeight( sizeObserver );

		java.awt.image.BufferedImage outputImage;
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null ) {
			outputImage = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame().getGraphicsConfiguration().createCompatibleImage( width, height, java.awt.Transparency.TRANSLUCENT );
		} else {
			outputImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		}
		java.awt.Graphics2D g = outputImage.createGraphics();
		g.drawImage( inputImage, 0, 0, allBitsObserver );
		paintColoredBorder( g, color, width, height );

		return outputImage;
	}

	public static void paintColoredBorder( java.awt.Graphics g, java.awt.Color color, int width, int height ) {
		g.setColor( color );
		g.drawRect( 0, 0, width - 1, height - 1 );
		g.drawRect( 1, 1, width - 3, height - 3 );
	}

	public static void paintTrough( java.awt.Graphics g, java.awt.Rectangle bounds, int arcWidth, int arcHeight ) {
		Object oldAntialiasing = null;
		java.awt.Shape oldClip = g.getClip();
		if( g instanceof java.awt.Graphics2D ) {
			oldAntialiasing = ((java.awt.Graphics2D)g).getRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING );
			((java.awt.Graphics2D)g).addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON ) );

			int halfHeight = bounds.height/2;
			java.awt.Polygon highlightClip = new java.awt.Polygon();
			highlightClip.addPoint( bounds.x, bounds.y + bounds.height );
			highlightClip.addPoint( bounds.x + halfHeight, bounds.y + halfHeight );
			highlightClip.addPoint( bounds.x + bounds.width - halfHeight, bounds.y + halfHeight );
			highlightClip.addPoint( bounds.x + bounds.width, bounds.y );
			highlightClip.addPoint( bounds.x + bounds.width, bounds.y + bounds.height );
			java.awt.Polygon shadowClip = new java.awt.Polygon();
			shadowClip.addPoint( bounds.x, bounds.y + bounds.height );
			shadowClip.addPoint( bounds.x, bounds.y );
			shadowClip.addPoint( bounds.x + bounds.width, bounds.y );
			shadowClip.addPoint( bounds.x + bounds.width - halfHeight, bounds.y + halfHeight );
			shadowClip.addPoint( bounds.x + halfHeight, bounds.y + halfHeight );

			Object edgeTreatment = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMiscItem( "tileEdgeTreatment" );
			if( edgeTreatment.equals( "square" ) ) {
				((java.awt.Graphics2D)g).clip( highlightClip );
//				g.setClip( highlightClip );
				g.setColor( troughHighlightColor );
				g.drawRect( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1 );

				g.setClip( oldClip );
				((java.awt.Graphics2D)g).clip( shadowClip );
//				g.setClip( shadowClip );
				g.setColor( troughShadowColor );
				g.drawRect( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1 );
			} else {
				((java.awt.Graphics2D)g).clip( highlightClip );
//				g.setClip( highlightClip );
				g.setColor( troughHighlightColor );
				g.drawRoundRect( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, arcWidth, arcHeight );

				g.setClip( oldClip );
				((java.awt.Graphics2D)g).clip( shadowClip );
//				g.setClip( shadowClip );
				g.setColor( troughShadowColor );
				g.drawRoundRect( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, arcWidth, arcHeight );
			}

			g.setClip( oldClip );

			((java.awt.Graphics2D)g).addRenderingHints( new java.awt.RenderingHints( java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing ) );
		}
	}
}