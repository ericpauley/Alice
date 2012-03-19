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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class WorldInfoContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementUsageGraph responseUsageGraph;

	public WorldInfoContentPane() {
		jbInit();
		guiInit();
	}

	@Override
	public String getTitle() {
		return "World Statistics";
	}

	private void guiInit() {
		setSize(500, 600);
		responseUsageGraph = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementUsageGraph();
		responseUsageGraph.setElementCriterion(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.Response.class));
		responseUsagePanel.add(responseUsageGraph, java.awt.BorderLayout.CENTER);
	}

	public void setWorld(edu.cmu.cs.stage3.alice.core.World world) {
		this.world = world;
		responseUsageGraph.setRoot(world);
		refresh();
	}

	private void refresh() {
		if (world != null) {
			edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

			world.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			world.visit(textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);

			int playCount = 0;
			int saveCount = 0;
			long worldOpenTime = 0;

			if (world.data.get("edu.cmu.cs.stage3.alice.authoringtool.playCount") != null) {
				playCount = Integer.parseInt((String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.playCount"));
			}
			if (world.data.get("edu.cmu.cs.stage3.alice.authoringtool.saveCount") != null) {
				saveCount = Integer.parseInt((String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.saveCount"));
			}
			if (world.data.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime") != null) {
				worldOpenTime = Long.parseLong((String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime"));
			}
			worldOpenTime /= 60000L;

			objectCountLabel.setText("Number of 3D objects: " + itaCounter.getShownIndexedTriangleArrayCount());
			polyCountLabel.setText("Number of polygons: " + itaCounter.getShownIndexCount() / 3);
			textureCountLabel.setText("Number of textures: " + textureMapCounter.getTextureMapCount());
			textureMemoryLabel.setText("Texture memory used: " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.formatMemorySize(textureMapCounter.getTextureMapMemoryCount()));
			userDefinedResponseCountLabel.setText("Number of user-defined animations: " + world.getDescendants(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class).length);
			userDefinedQuestionCountLabel.setText("Number of user-defined questions: " + world.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class).length);
			behaviorCountLabel.setText("Number of behaviors: " + world.getDescendants(edu.cmu.cs.stage3.alice.core.Behavior.class).length);
			playCountLabel.setText("Number of times the world has been run: " + playCount);
			saveCountLabel.setText("Number of times the world has been saved: " + saveCount);
			worldOpenTimeLabel.setText("Amount of time the world has been open: " + worldOpenTime + (worldOpenTime == 1 ? " minute" : " minutes"));
			currentRendererLabel.setText("Current renderer: " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getCurrentRendererText());

			responseUsageGraph.refresh();
		}
	}

	// /////////////////
	// Autogenerated
	// /////////////////

	Border border1;
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel buttonPanel = new JPanel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JButton doneButton = new JButton();
	Border border2;
	JScrollPane mainScrollPane = new JScrollPane();
	JLabel textureMemoryLabel = new JLabel();
	JPanel mainPanel = new JPanel();
	JLabel responseUsageLabel = new JLabel();
	JLabel objectCountLabel = new JLabel();
	JLabel polyCountLabel = new JLabel();
	JLabel currentRendererLabel = new JLabel();
	Component component1;
	JPanel responseUsagePanel = new JPanel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel behaviorCountLabel = new JLabel();
	JLabel userDefinedResponseCountLabel = new JLabel();
	JLabel textureCountLabel = new JLabel();
	JLabel playCountLabel = new JLabel();
	JLabel saveCountLabel = new JLabel();
	JLabel worldOpenTimeLabel = new JLabel();
	JLabel userDefinedQuestionCountLabel = new JLabel();

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border2 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		component1 = Box.createGlue();
		setBackground(new Color(232, 230, 255));
		setLayout(borderLayout1);
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(gridBagLayout1);
		doneButton.setText("Done");
		textureMemoryLabel.setText("Texture memory used:");
		mainPanel.setBorder(border2);
		mainPanel.setOpaque(false);
		mainPanel.setLayout(gridBagLayout2);
		responseUsageLabel.setText("Animation usage:");
		objectCountLabel.setText("Number of 3D objects:");
		polyCountLabel.setText("Number of polygons:");
		responseUsagePanel.setOpaque(false);
		responseUsagePanel.setLayout(borderLayout2);
		behaviorCountLabel.setText("Number of behaviors:");
		userDefinedResponseCountLabel.setText("Number of user-defined animations:");
		mainScrollPane.getViewport().setBackground(new Color(232, 230, 255));
		mainScrollPane.setOpaque(false);
		textureCountLabel.setText("Number of textures:");
		playCountLabel.setText("Number of times the world has been run:");
		saveCountLabel.setText("Number of times the world has been saved:");
		worldOpenTimeLabel.setText("Amount of time the world has been open:");
		userDefinedQuestionCountLabel.setText("Number of user-defined questions:");
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(doneButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
		add(mainScrollPane, BorderLayout.CENTER);
		mainScrollPane.getViewport().add(mainPanel, null);
		mainPanel.add(objectCountLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(polyCountLabel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(textureMemoryLabel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(userDefinedResponseCountLabel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(behaviorCountLabel, new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(responseUsageLabel, new GridBagConstraints(0, 11, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 0, 2, 0), 0, 0));
		mainPanel.add(responseUsagePanel, new GridBagConstraints(0, 12, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		mainPanel.add(component1, new GridBagConstraints(0, 13, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(textureCountLabel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(playCountLabel, new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(saveCountLabel, new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(worldOpenTimeLabel, new GridBagConstraints(0, 9, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(currentRendererLabel, new GridBagConstraints(0, 10, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));

		mainPanel.add(userDefinedQuestionCountLabel, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
	}

	@Override
	public void addOKActionListener(java.awt.event.ActionListener l) {
		doneButton.addActionListener(l);
	}

	@Override
	public void removeOKActionListener(java.awt.event.ActionListener l) {
		doneButton.removeActionListener(l);
	}
}