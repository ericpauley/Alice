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

package edu.cmu.cs.stage3.caitlin.personbuilder;


public class ColorSelector extends javax.swing.JPanel implements java.awt.event.ActionListener, javax.swing.event.ChangeListener {
	protected javax.swing.JButton otherColorsButton = null;

	protected javax.swing.ButtonGroup whichColorChooserGroup = null;
	protected javax.swing.JRadioButton otherColorsRadio = null;
	protected javax.swing.JRadioButton humanColorsRadio = null;
	protected javax.swing.JSlider colorSlider = null;
	protected java.awt.Color skinColor;
	
	protected javax.swing.JColorChooser colorChooser;

	protected ModelWrapper modelWrapper = null;

	public ColorSelector(ModelWrapper modelWrapper) {
		this.modelWrapper = modelWrapper;
		this.setBackground(new java.awt.Color(155, 159, 206));

		init();
	}

	protected void init() {
		this.setLayout(new java.awt.BorderLayout());

		//make radio buttons
		humanColorsRadio = new javax.swing.JRadioButton();
		humanColorsRadio.setSelected(true);
		humanColorsRadio.setBackground(new java.awt.Color(155, 159, 206));
		otherColorsRadio = new javax.swing.JRadioButton();
		otherColorsRadio.setSelected(false);
		otherColorsRadio.setBackground(new java.awt.Color(155, 159, 206));

		//group them
		whichColorChooserGroup = new javax.swing.ButtonGroup();
		whichColorChooserGroup.add(humanColorsRadio);
		whichColorChooserGroup.add(otherColorsRadio);

		//add listeners
		humanColorsRadio.addActionListener(this);
		otherColorsRadio.addActionListener(this);

		javax.swing.JPanel humanColorPanel = new javax.swing.JPanel();
		humanColorPanel.setBackground(new java.awt.Color(155, 159, 206));
		humanColorPanel.setLayout(new java.awt.FlowLayout());
		colorSlider = new javax.swing.JSlider();
		skinColor = java.awt.Color.getHSBColor(25.0f / 359.0f, .61f, .665f);
		colorSlider.setBackground(skinColor);
		colorSlider.addChangeListener(this);

		humanColorPanel.add(humanColorsRadio);
		humanColorPanel.add(colorSlider);

		javax.swing.JPanel otherColorPanel = new javax.swing.JPanel();
		otherColorPanel.setBackground(new java.awt.Color(155, 159, 206));
		otherColorPanel.setLayout(new java.awt.FlowLayout());

		otherColorsButton = new javax.swing.JButton("Choose skin color...");
		otherColorsButton.addActionListener(this);
		otherColorsButton.setEnabled(false);

		otherColorPanel.add(otherColorsRadio);
		otherColorPanel.add(otherColorsButton);

		this.add(humanColorPanel, java.awt.BorderLayout.NORTH);
		this.add(otherColorPanel, java.awt.BorderLayout.CENTER);
	}

	public void actionPerformed(java.awt.event.ActionEvent ae) {
		if (ae.getSource() == otherColorsButton) {
			if( colorChooser == null ) {
				colorChooser = new javax.swing.JColorChooser();
			}
			java.awt.Color selectedColor = edu.cmu.cs.stage3.swing.DialogManager.showDialog( colorChooser, "more colors...", skinColor );
			if (selectedColor != null)
				modelWrapper.setColor(selectedColor);
		} else if (ae.getSource() == otherColorsRadio) {
			colorSlider.setEnabled(false);
			otherColorsButton.setEnabled(true);
		} else if (ae.getSource() == humanColorsRadio) {
			colorSlider.setEnabled(true);
			modelWrapper.setColor(skinColor);
			otherColorsButton.setEnabled(false);
		}
	}

	public void stateChanged(javax.swing.event.ChangeEvent ce) {
		int position = colorSlider.getValue();
		float s = .89f + (-.56f * position / 100.0f);
		float b = .33f + (.67f * position / 100.0f);
		skinColor = java.awt.Color.getHSBColor(25.0f / 359.0f, s, b);
		colorSlider.setBackground(skinColor);
		if (!(colorSlider.getValueIsAdjusting()))
			modelWrapper.setColor(skinColor);
	}
}