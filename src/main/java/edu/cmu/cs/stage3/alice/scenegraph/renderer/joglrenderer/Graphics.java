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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import javax.media.opengl.GL;

public class Graphics extends java.awt.Graphics {
	private RenderContext m_renderContext;
	private java.awt.Color m_color = java.awt.Color.black;
	private static int SINE_AND_COSINE_CACHE_LENGTH = 8;
	private static double[] s_cosines = null;
	private static double[] s_sines = null;

	private static void cacheSinesAndCosinesIfNecessary() {
		if (s_cosines == null) {
			s_cosines = new double[SINE_AND_COSINE_CACHE_LENGTH];
			s_sines = new double[SINE_AND_COSINE_CACHE_LENGTH];
			double theta = 0;
			double dtheta = Math.PI / 2.0 / s_cosines.length;
			for (int i = 0; i < s_cosines.length; i++) {
				s_cosines[i] = Math.cos(theta);
				s_sines[i] = Math.sin(theta);
				theta += dtheta;
			}
		}
	}
	protected Graphics(RenderContext renderContext) {
		m_renderContext = renderContext;
		setColor(m_color);

		int width = m_renderContext.getWidth();
		int height = m_renderContext.getHeight();
		m_renderContext.gl.glMatrixMode(GL.GL_PROJECTION);
		m_renderContext.gl.glLoadIdentity();
		m_renderContext.gl.glOrtho(0, width - 1, height - 1, 0, -1, 1);
		// m_renderContext.gl.glViewport( 0, 0, width, height );
		m_renderContext.gl.glMatrixMode(GL.GL_MODELVIEW);
		m_renderContext.gl.glLoadIdentity();

		m_renderContext.gl.glDisable(GL.GL_DEPTH_TEST);
		m_renderContext.gl.glDisable(GL.GL_LIGHTING);
		m_renderContext.gl.glDisable(GL.GL_CULL_FACE);

		m_renderContext.setTextureMapProxy(null);
	}

	@Override
	public void dispose() {
		m_renderContext.gl.glFlush();
		m_renderContext = null;
	}

	@Override
	public java.awt.Graphics create() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void translate(int x, int y) {
		m_renderContext.gl.glTranslatef(x, y, 0);
	}

	@Override
	public java.awt.Color getColor() {
		return m_color;
	}

	@Override
	public void setColor(java.awt.Color c) {
		m_color = c;
		m_renderContext.gl.glColor3ub((byte) m_color.getRed(), (byte) m_color.getGreen(), (byte) m_color.getBlue());
	}

	@Override
	public void setPaintMode() {
		// todo
	}

	@Override
	public void setXORMode(java.awt.Color c1) {
		// todo
	}

	private java.awt.Font m_font = new java.awt.Font(null, java.awt.Font.PLAIN, 12);

	@Override
	public java.awt.Font getFont() {
		return m_font;
	}

	@Override
	public void setFont(java.awt.Font font) {
		m_font = font;
	}

	@Override
	public java.awt.FontMetrics getFontMetrics(java.awt.Font f) {
		return java.awt.Toolkit.getDefaultToolkit().getFontMetrics(f);
	}

	@Override
	public java.awt.Rectangle getClipBounds() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public java.awt.Shape getClip() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setClip(java.awt.Shape clip) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		m_renderContext.gl.glBegin(GL.GL_LINES);
		m_renderContext.gl.glVertex2i(x1, y1);
		m_renderContext.gl.glVertex2i(x2, y2);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		m_renderContext.gl.glBegin(GL.GL_POLYGON);
		m_renderContext.gl.glVertex2i(x, y);
		m_renderContext.gl.glVertex2i(x + width, y);
		m_renderContext.gl.glVertex2i(x + width, y + height);
		m_renderContext.gl.glVertex2i(x, y + height);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		throw new RuntimeException("not implemented");
	}

	private void glQuarterOval(double centerX, double centerY, double radiusX, double radiusY, int whichQuarter) {
		int n = s_cosines.length;
		int max = n - 1;
		for (int i = 0; i < n; i++) {
			double cos;
			double sin;
			switch (whichQuarter) {
				case 0 :
					cos = s_cosines[i];
					sin = s_sines[i];
					break;
				case 1 :
					cos = -s_cosines[max - i];
					sin = s_sines[max - i];
					break;
				case 2 :
					cos = -s_cosines[i];
					sin = -s_sines[i];
					break;
				case 3 :
					cos = s_cosines[max - i];
					sin = -s_sines[max - i];
					break;
				default :
					throw new IllegalArgumentException();
			}
			m_renderContext.gl.glVertex2d(centerX + cos * radiusX, centerY + sin * radiusY);
		}
	}

	private void glRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		cacheSinesAndCosinesIfNecessary();

		// int x0 = x;
		int x1 = x + arcWidth;
		int x2 = x + width - arcWidth;
		// int x3 = x+width;

		// int y0 = y;
		int y1 = y + arcHeight;
		int y2 = y + height - arcHeight;
		// int y3 = y+height;

		glQuarterOval(x1, y1, arcWidth, arcHeight, 2);
		// m_renderContext.gl.glVertex2d( x1, y0 );
		glQuarterOval(x2, y1, arcWidth, arcHeight, 3);
		// m_renderContext.gl.glVertex2d( x3, y1 );
		glQuarterOval(x2, y2, arcWidth, arcHeight, 0);
		// m_renderContext.gl.glVertex2d( x2, y3 );
		glQuarterOval(x1, y2, arcWidth, arcHeight, 1);
		// m_renderContext.gl.glVertex2d( x0, y2 );
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		m_renderContext.gl.glBegin(GL.GL_LINE_LOOP);
		glRoundRect(x, y, width, height, arcWidth, arcHeight);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		m_renderContext.gl.glBegin(GL.GL_TRIANGLE_FAN);
		glRoundRect(x, y, width, height, arcWidth, arcHeight);
		m_renderContext.gl.glEnd();
	}

	private void glOval(int x, int y, int width, int height) {
		double radiusX = width * 0.5;
		double radiusY = height * 0.5;
		double centerX = x + radiusX;
		double centerY = y + radiusY;
		cacheSinesAndCosinesIfNecessary();
		glQuarterOval(centerX, centerY, radiusX, radiusY, 0);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 1);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 2);
		glQuarterOval(centerX, centerY, radiusX, radiusY, 3);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		m_renderContext.gl.glBegin(GL.GL_LINE_LOOP);
		glOval(x, y, width, height);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		m_renderContext.gl.glBegin(GL.GL_TRIANGLE_FAN);
		glOval(x, y, width, height);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new RuntimeException("not implemented");
	}
	private void glPoly(int xPoints[], int yPoints[], int nPoints) {
		for (int i = 0; i < nPoints; i++) {
			m_renderContext.gl.glVertex2i(xPoints[i], yPoints[i]);
		}
	}

	@Override
	public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {
		m_renderContext.gl.glBegin(GL.GL_LINE_STRIP);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
		m_renderContext.gl.glBegin(GL.GL_LINE_LOOP);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
		m_renderContext.gl.glBegin(GL.GL_POLYGON);
		glPoly(xPoints, yPoints, nPoints);
		m_renderContext.gl.glEnd();
	}

	@Override
	public void drawString(String str, int x, int y) {
		float scale = m_font.getSize() / 170.0f;
		m_renderContext.gl.glPushMatrix();
		m_renderContext.gl.glTranslatef(x, y, 0);
		m_renderContext.gl.glScalef(scale, -scale, 1.0f);
		m_renderContext.glut.glutStrokeString(com.sun.opengl.util.GLUT.STROKE_ROMAN, str);
		m_renderContext.gl.glPopMatrix();
	}

	@Override
	public void drawString(java.text.AttributedCharacterIterator iterator, int x, int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, java.awt.Color bgcolor, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, java.awt.Color bgcolor, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, java.awt.Color bgcolor, java.awt.image.ImageObserver observer) {
		throw new RuntimeException("not implemented");
	}
}
