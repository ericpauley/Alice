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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * @author Jason Pratt
 */
public class StatusBar extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	public StatusBar(AuthoringTool authoringTool) {
		jbInit();
		authoringTool.addAuthoringToolStateListener(this);
	}

	// /////////////////////////////////////////////
	// AuthoringToolStateListener interface
	// /////////////////////////////////////////////

	@Override
	public void worldLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		updateWorldStats(ev.getWorld());
	}

	@Override
	public void worldUnLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
		updateWorldStats(null);
	}

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

	private void updateWorldStats(edu.cmu.cs.stage3.alice.core.World world) {
		javax.vecmath.Vector3d goodHSB = new javax.vecmath.Vector3d(1.0 / 3.0, 1.0, .85); // green
		javax.vecmath.Vector3d badHSB = new javax.vecmath.Vector3d(0.0, 1.0, 1.0); // red

		java.awt.Color goodColor = new java.awt.Color(java.awt.Color.HSBtoRGB((float) goodHSB.x, (float) goodHSB.y, (float) goodHSB.z));
		java.awt.Color badColor = new java.awt.Color(java.awt.Color.HSBtoRGB((float) badHSB.x, (float) badHSB.y, (float) badHSB.z));

		// TODO: make these preferences, or even better, base them on the
		// strength of the machine
		int minDangerObjectCount = 1000;
		int maxDangerObjectCount = 2000;
		int minDangerFaceCount = 10000;
		int maxDangerFaceCount = 50000;
		int minDangerTextureMemory = 33554432;
		int maxDangerTextureMemory = 67108864;

		if (world != null) {
			edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

			world.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			world.visit(textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);

			int objectCount = itaCounter.getShownIndexedTriangleArrayCount();
			int faceCount = itaCounter.getShownIndexCount() / 3;
			double textureMemory = textureMapCounter.getTextureMapMemoryCount();

			if (objectCount <= minDangerObjectCount) {
				objectCountLabel.setBackground(goodColor);
			} else if (objectCount >= maxDangerObjectCount) {
				objectCountLabel.setBackground(badColor);
			} else {
				float portion = (float) (objectCount - minDangerObjectCount) / (maxDangerObjectCount - minDangerObjectCount);
				javax.vecmath.Vector3d hsb = edu.cmu.cs.stage3.math.MathUtilities.interpolate(goodHSB, badHSB, portion);
				objectCountLabel.setBackground(new java.awt.Color(java.awt.Color.HSBtoRGB((float) hsb.x, (float) hsb.y, (float) hsb.z)));
			}

			if (faceCount <= minDangerFaceCount) {
				faceCountLabel.setBackground(goodColor);
			} else if (faceCount >= maxDangerFaceCount) {
				faceCountLabel.setBackground(badColor);
			} else {
				float portion = (float) (faceCount - minDangerFaceCount) / (maxDangerFaceCount - minDangerFaceCount);
				javax.vecmath.Vector3d hsb = edu.cmu.cs.stage3.math.MathUtilities.interpolate(goodHSB, badHSB, portion);
				faceCountLabel.setBackground(new java.awt.Color(java.awt.Color.HSBtoRGB((float) hsb.x, (float) hsb.y, (float) hsb.z)));
			}

			if (textureMemory <= minDangerTextureMemory) {
				textureMemoryLabel.setBackground(goodColor);
			} else if (textureMemory >= maxDangerTextureMemory) {
				textureMemoryLabel.setBackground(badColor);
			} else {
				float portion = (float) (textureMemory - minDangerTextureMemory) / (maxDangerTextureMemory - minDangerTextureMemory);
				javax.vecmath.Vector3d hsb = edu.cmu.cs.stage3.math.MathUtilities.interpolate(goodHSB, badHSB, portion);
				textureMemoryLabel.setBackground(new java.awt.Color(java.awt.Color.HSBtoRGB((float) hsb.x, (float) hsb.y, (float) hsb.z)));
			}

			String textureSuffix = "";
			if (textureMemory > 1 << 19) {
				textureMemory = textureMemory / (1 << 20);
				textureSuffix = " MB";
			} else {
				textureMemory = textureMemory / (1 << 10);
				textureSuffix = " KB";
			}

			java.text.DecimalFormat memoryFormat = new java.text.DecimalFormat("#0.#");

			objectCountLabel.setText("object count: " + objectCount);
			faceCountLabel.setText("face count: " + faceCount);
			textureMemoryLabel.setText("texture memory: " + memoryFormat.format(textureMemory) + textureSuffix);
		} else {
			objectCountLabel.setText("object count: 0");
			faceCountLabel.setText("face count: 0");
			textureMemoryLabel.setText("texture memory: 0 bytes");

			objectCountLabel.setBackground(goodColor);
			faceCountLabel.setBackground(goodColor);
			textureMemoryLabel.setBackground(goodColor);
		}
	}

	// ////////////////
	// Autogenerated
	// ////////////////

	Border border1;
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel worldStatsPanel = new JPanel();
	JLabel worldStatsLabel = new JLabel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JLabel objectCountLabel = new JLabel();
	JLabel faceCountLabel = new JLabel();
	JLabel textureMemoryLabel = new JLabel();
	Component filler1;
	Border border2;

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		filler1 = Box.createGlue();
		border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, Color.white, new Color(142, 142, 142), new Color(99, 99, 99));
		setBackground(new Color(204, 204, 204));
		setBorder(border1);
		setLayout(gridBagLayout1);
		worldStatsLabel.setText("World Statistics:");
		worldStatsPanel.setLayout(gridBagLayout2);
		objectCountLabel.setBackground(new Color(0, 204, 0));
		objectCountLabel.setForeground(Color.black);
		objectCountLabel.setOpaque(true);
		objectCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		objectCountLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		objectCountLabel.setText("object count:");
		faceCountLabel.setBackground(new Color(0, 204, 0));
		faceCountLabel.setForeground(Color.black);
		faceCountLabel.setOpaque(true);
		faceCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		faceCountLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		faceCountLabel.setText("face count:");
		textureMemoryLabel.setBackground(new Color(0, 204, 0));
		textureMemoryLabel.setForeground(Color.black);
		textureMemoryLabel.setOpaque(true);
		textureMemoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		textureMemoryLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		textureMemoryLabel.setText("texture memory:");
		worldStatsPanel.setBorder(border2);
		this.add(worldStatsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 4));
		worldStatsPanel.add(worldStatsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 6), 0, 0));
		worldStatsPanel.add(objectCountLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 10, 0));
		worldStatsPanel.add(faceCountLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 10, 0));
		worldStatsPanel.add(textureMemoryLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 10, 0));
		worldStatsPanel.add(filler1, new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}
}