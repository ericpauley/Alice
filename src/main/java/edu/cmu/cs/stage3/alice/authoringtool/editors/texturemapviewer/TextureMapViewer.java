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

package edu.cmu.cs.stage3.alice.authoringtool.editors.texturemapviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Jason Pratt
 */
public class TextureMapViewer extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor {
	public String editorName = "TextureMap Viewer";

	protected edu.cmu.cs.stage3.alice.core.TextureMap textureMap;
	protected ImagePanel texturePanel = new ImagePanel();

	public TextureMapViewer() {
		jbInit();
	}

	@Override
	public javax.swing.JComponent getJComponent() {
		return this;
	}

	@Override
	public Object getObject() {
		return textureMap;
	}

	@Override
	public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
	}

	public void setObject(edu.cmu.cs.stage3.alice.core.TextureMap textureMap) {
		this.textureMap = textureMap;
		if (textureMap != null) {
			texturePanel.setImage(textureMap.image.getImageValue());
		} else {
			texturePanel.setImage(null);
		}
		texturePanel.revalidate();
		texturePanel.repaint();
	}

	protected class ImagePanel extends JPanel {
		Image image;
		int buffer = 5;
		int imageWidth = 0;
		int imageHeight = 0;

		public ImagePanel() {
			setBackground(java.awt.Color.black);
		}

		public void setImage(Image image) {
			this.image = image;
			if (image != null) {
				java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
				tracker.addImage(image, 0);
				try {
					tracker.waitForAll(1000); // wait a second max.
					imageWidth = image.getWidth(this);
					imageHeight = image.getHeight(this);
					java.awt.Dimension size = new java.awt.Dimension(imageWidth + buffer * 2, imageHeight + buffer * 2);
					setMinimumSize(size);
					setPreferredSize(size);
				} catch (java.lang.InterruptedException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Interrupted while loading image.", e);
				}
			} else {
				java.awt.Dimension size = new java.awt.Dimension(buffer * 2, buffer * 2);
				setMinimumSize(size);
				setPreferredSize(size);
			}
		}

		@Override
		public void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.setColor(java.awt.Color.white);
				g.drawRect(buffer, buffer, imageWidth + 1, imageHeight + 1);
				g.drawImage(image, buffer + 1, buffer + 1, this);
			}
		}
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////

	@Override
	public void stateChanging(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldLoading(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldUnLoading(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldStarting(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldStopping(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldPausing(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldSaving(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	@Override
	public void stateChanged(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldUnLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldStarted(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldStopped(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldPaused(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}
	@Override
	public void worldSaved(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
	}

	// ////////////////////
	// Autogenerated
	// ////////////////////
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane textureScrollPane = new JScrollPane();

	private void jbInit() {
		setLayout(borderLayout1);
		setBackground(Color.black);
		textureScrollPane.getViewport().setBackground(Color.black);
		textureScrollPane.setBorder(null);
		this.add(textureScrollPane, BorderLayout.CENTER);
		textureScrollPane.getViewport().add(texturePanel, null);
	}
}