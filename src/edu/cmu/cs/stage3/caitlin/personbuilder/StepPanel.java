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
import java.util.Vector;
import javax.swing.ImageIcon;
import org.w3c.dom.*;

public class StepPanel extends JPanel {
	ImageIcon backImage = null;
	ImageIcon nextImage = null;
	Vector choosers = new Vector();

	public StepPanel(Node stepNode, ImageIcon nextImage, ImageIcon backImage, ModelWrapper modelWrapper) {
		this.setBackground(new java.awt.Color(155, 159, 206));
		this.backImage = backImage;
		this.nextImage = nextImage;

		choosers = getChoosers(stepNode, modelWrapper);
		addChoosers(choosers);
	}

	public void resetDefaults() {
		for (int i = 0; i < choosers.size(); i++) {
			if (choosers.elementAt(i) instanceof ItemChooser) {
				ItemChooser chooser = (ItemChooser) choosers.elementAt(i);
				chooser.resetDefaults();
			}
		}
	}

	private Vector getChoosers(Node stepNode, ModelWrapper modelWrapper ) {

		Vector choosers = new Vector();

		Vector colorNodes = XMLDirectoryUtilities.getSetColorNodes(stepNode);
		for (int i = 0; i < colorNodes.size(); i++) {
			ColorSelector colorSelector = new ColorSelector(modelWrapper);
			choosers.addElement(colorSelector);
		}

		Vector chooserNodes = XMLDirectoryUtilities.getDirectories(stepNode);
		double incr = 3.0 / chooserNodes.size();
		for (int i = 0; i < chooserNodes.size(); i++) {
			Node chooserNode = (Node) chooserNodes.elementAt(i);
			if (chooserNode.getNodeName().equals("directory")) {
				ItemChooser chooser = new ItemChooser(chooserNode, nextImage, backImage, modelWrapper);
				choosers.addElement(chooser);
			}
		}
		return choosers;
	}

	private void addChoosers(Vector choosers) {
		this.setLayout(new java.awt.GridLayout(3, 1));
		for (int i = 0; i < choosers.size(); i++) {
			this.add((JPanel) choosers.elementAt(i));
		}
	}
}