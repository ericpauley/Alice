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

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.Vector;
import java.awt.Color;
import org.w3c.dom.*;

//TODO: disable the next and previous buttons for first/last step

public class NavigationPanel extends JPanel implements java.awt.event.ActionListener {
	protected Vector stepButtons = new Vector();
	protected Vector stepImages = new Vector();
	protected Vector selStepImages = new Vector();
	protected JButton backButton = null; //new JButton("back");
	protected JButton nextButton = null; //new JButton("next");
	protected ImageIcon spacerIcon = null;
	protected ImageIcon noBackImage = null;
	protected ImageIcon noNextImage = null;
	protected ImageIcon backImage = null;
	protected ImageIcon nextImage = null;
	protected int stepIndex = 0;
	protected AllStepsPanel allStepsPanel;

	public NavigationPanel(Node root, AllStepsPanel allStepsPanel) {
		createGuiElements(root);
		this.allStepsPanel = allStepsPanel;
		addGuiElements();
		setSelectedStep(0, 1);
	}

	public void setFirstStep() {
		int index = allStepsPanel.getSelected();
		allStepsPanel.setSelected(0);
		setSelectedStep(0, index - 1);
	}

	public void actionPerformed(java.awt.event.ActionEvent ae) {
		Object actionObj = ae.getSource();
		int index = stepButtons.indexOf(actionObj);
		if ((index > 0) && (index < stepButtons.size() - 1)) {
		} else if (index == 0) {
			index = allStepsPanel.getSelected();
			index--;
		} else if (index == stepButtons.size() - 1) {
			index = allStepsPanel.getSelected();
			index++;
		}

		// make this set the appropriate step image.
		int prevStep = allStepsPanel.getSelected() - 1;
		allStepsPanel.setSelected(index);
		int curStep = allStepsPanel.getSelected() - 1;

		setSelectedStep(curStep, prevStep);
	}

	protected void setSelectedStep(int curStep, int prevStep) {
		if ((prevStep >= 0) && (curStep != prevStep)) {
			JButton curButton = (JButton) stepButtons.elementAt(prevStep + 1);
			ImageIcon curImage = (ImageIcon) stepImages.elementAt(prevStep);
			curButton.setIcon(curImage);
		}

		if ((prevStep < selStepImages.size()) && (curStep != prevStep)) {
			JButton newButton = (JButton) stepButtons.elementAt(curStep + 1);
			ImageIcon newImage = (ImageIcon) selStepImages.elementAt(curStep);
			newButton.setIcon(newImage);
		}

		if (curStep == 0) {
			backButton.setIcon(noBackImage);
		} else {
			backButton.setIcon(backImage);
		}

		if (curStep == selStepImages.size() - 1) {
			nextButton.setIcon(noNextImage);
		} else {
			nextButton.setIcon(nextImage);
		}
	}

	private void addGuiElements() {
		//this.setLayout(new java.awt.GridLayout(1, stepButtons.size() + stepButtons.size()-1, 0,0));
		this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
		this.setBackground(new Color(155, 159, 206));
		for (int i = 0; i < stepButtons.size(); i++) {
			this.add(((JButton) stepButtons.elementAt(i)));
			if (i != stepButtons.size() - 1) {
				JLabel spLabel = new JLabel(spacerIcon);
				this.add(spLabel);
			}
		}
	}

	private void createGuiElements(Node root) {
		Vector imageURLs = XMLDirectoryUtilities.getImageURLs(root);
		for (int i = 0; i < imageURLs.size(); i++) {
			java.net.URL url = (java.net.URL) imageURLs.elementAt(i);
			if (url.toString().indexOf("nextBtn.jpg") != -1) {
				nextImage = new ImageIcon(url);
				nextButton = new JButton(nextImage);
				nextButton.setBorderPainted(false);
				nextButton.setBorder(null);
				nextButton.addActionListener(this);
			} else if (url.toString().indexOf("backBtn.jpg") != -1) {
				backImage = new ImageIcon(url);
				backButton = new JButton(backImage);
				backButton.setBorderPainted(false);
				backButton.setBorder(null);
				backButton.addActionListener(this);
				stepButtons.addElement(backButton);
			} else if (url.toString().indexOf("noBackBtn.jpg") != -1) {
				noBackImage = new ImageIcon(url);
			} else if (url.toString().indexOf("noNextBtn.jpg") != -1) {
				noNextImage = new ImageIcon(url);
			} else if (url.toString().indexOf("spacer.jpg") != -1) {
				spacerIcon = new ImageIcon(url);
			}
		}

		Vector stepNodes = XMLDirectoryUtilities.getDirectories(root);
		for (int i = 0; i < stepNodes.size(); i++) {
			Node currentStepNode = (Node) stepNodes.elementAt(i);
			Vector currentStepImages = XMLDirectoryUtilities.getImageURLs(currentStepNode);
			if ((currentStepImages != null) && (currentStepImages.size() > 1)) {
				ImageIcon icon = new ImageIcon((java.net.URL) currentStepImages.elementAt(0));
				ImageIcon selIcon = new ImageIcon((java.net.URL) currentStepImages.elementAt(1));
				stepImages.addElement(icon);
				selStepImages.addElement(selIcon);
				JButton stepBtn = new JButton(icon);
				stepBtn.setBorderPainted(false);
				stepBtn.addActionListener(this);
				stepBtn.setBorder(null);
				stepButtons.addElement(stepBtn);
			}
		}

		stepButtons.addElement(nextButton);
	}
}