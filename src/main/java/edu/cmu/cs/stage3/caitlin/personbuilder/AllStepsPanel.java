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
import java.util.Vector;
import org.w3c.dom.*;

public class AllStepsPanel extends JPanel {
	protected java.awt.CardLayout cLayout = null;
	protected int selectedPanel = 0;
	protected int numSteps = 0;
	protected ImageIcon nextImage = null;
	protected ImageIcon backImage = null;
	protected Vector stepPanels = new Vector();

	public AllStepsPanel(Node root, ModelWrapper modelWrapper, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, int progressOffset ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		createGuiElements(root, modelWrapper, progressObserver, progressOffset);
		setSelected(1);
	}

	private void createGuiElements(Node root, ModelWrapper modelWrapper, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, int progressOffset ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		Vector imageURLs = XMLDirectoryUtilities.getImageURLs(root);
		for (int i = 0; i < imageURLs.size(); i++) {
			java.net.URL url = (java.net.URL) imageURLs.elementAt(i);
			if (url.toString().indexOf("stepNext.jpg") != -1) {
				nextImage = new ImageIcon(url);
			} else if (url.toString().indexOf("stepBack.jpg") != -1) {
				backImage = new ImageIcon(url);
			}
		}
		Vector stepNodes = XMLDirectoryUtilities.getDirectories(root);
		if (stepNodes != null) {
			cLayout = new java.awt.CardLayout();
			this.setLayout(cLayout);
			numSteps = stepNodes.size();
			progressObserver.progressUpdateTotal( progressOffset+stepNodes.size() );
			for (int i = 0; i < stepNodes.size(); i++) {
				Node currentStepNode = (Node) stepNodes.elementAt(i);
				Vector stepImages = XMLDirectoryUtilities.getImages(currentStepNode);
				if ((stepImages != null) && (stepImages.size() > 0)) {
					StepPanel stepPanel = new StepPanel(currentStepNode, nextImage, backImage, modelWrapper);
					stepPanels.addElement(stepPanel);
					this.add(stepPanel, "Step " + (i + 1) + " Panel");
				}
				progressObserver.progressUpdate( progressOffset++, null );
			}
		}
	}

	public void resetDefaults() {
		for (int i = 0; i < stepPanels.size(); i++) {
			StepPanel stepPanel = (StepPanel) stepPanels.elementAt(i);
			stepPanel.resetDefaults();
		}
	}

	public int getSelected() {
		return selectedPanel;
	}

	public void setSelected(int i) {
		if (i < 1)
			i = 1;
		if (i > numSteps)
			i = numSteps;
		selectedPanel = i;
		cLayout.show(this, "Step " + i + " Panel");
	}
}
