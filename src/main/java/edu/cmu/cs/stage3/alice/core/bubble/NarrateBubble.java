/*
 * Created on May 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * @author caitlin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NarrateBubble extends Bubble {

	private boolean m_displayTopOfScreen = false;
	private java.awt.Rectangle actualViewport = null;

	public NarrateBubble() {
		setCharactersPerLine(60);
	}

	@Override
	public void calculateOrigin(edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt) {

	}

	// narrate doesn't really have an origin, this is more to calculate a
	// reasonable set of offsets

	@Override
	public void calculateBounds(edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget rt) {

		edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = rt.getCameras();
		if (sgCameras.length > 0) {
			edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[0];
			actualViewport = rt.getActualViewport(sgCamera);

			java.awt.Font font = getFont();
			FontMetrics fontMetrics = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);
			// System.out.println(fontMetrics.charWidth('W'));

			double charCnt = actualViewport.getWidth() / fontMetrics.charWidth('W');
			// System.out.println("charCnt: " + charCnt + " " + font.getSize());
			setCharactersPerLine((int) charCnt * 2);
		}

		super.calculateBounds(rt);

		java.awt.geom.Rectangle2D totalBound = getTotalBound();

		if (sgCameras.length > 0) {
			edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[0];
			actualViewport = rt.getActualViewport(sgCamera);

			double x = actualViewport.getX() + actualViewport.getWidth() / 2.0 - totalBound.getWidth() / 2.0;
			double y = 0;
			if (m_displayTopOfScreen) {
				y = PAD_Y;
				if (totalBound.getY() < 0) {
					y = -1.0 * totalBound.getY() + PAD_Y;
				}
			} else {
				y = actualViewport.y + actualViewport.getHeight() - totalBound.getHeight() + PAD_Y;
			}

			setPixelOffset(new java.awt.Point((int) x, (int) y));
		}
	}

	@Override
	protected void paintBackground(Graphics g) {
		java.awt.geom.Rectangle2D totalBound = getTotalBound();
		java.awt.Point origin = getOrigin();
		java.awt.Point pixelOffset = getPixelOffset();

		int x = actualViewport.x;
		int y = (int) (totalBound.getY() + pixelOffset.y - PAD_Y);

		int width = actualViewport.width;
		int height = (int) totalBound.getHeight() + PAD_Y + PAD_Y;

		g.setColor(getBackgroundColor());
		g.fillRoundRect(x, y, width, height, 5, 5);
		g.setColor(java.awt.Color.black);
		g.drawRoundRect(x, y, width, height, 5, 5);
	}

}
