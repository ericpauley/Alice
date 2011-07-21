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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import java.awt.*;
import javax.swing.*;

/**
 * @author culyba, dennisc
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class GenericBuilderButton extends GalleryObject {
	public void set(GalleryViewer.ObjectXmlData dataIn, ImageIcon icon) throws IllegalArgumentException {
		if (dataIn != null) {
			clean();
			super.data = dataIn;
			super.image = icon;
			super.mainViewer = super.data.mainViewer;
			if (super.data.transferable != null)
				setTransferable(super.data.transferable);
			updateGUI();
			wakeUp();
		} else {
			clean();
			super.data = null;
			super.mainViewer = null;
		}
	}
	
	public String getUniqueIdentifier() {
		if (super.data != null)
			return super.data.name;
		else
			return "GenericBuilder";
	}
	
	public void loadImage() {
		super.imageLabel.setText(null);
		super.imageLabel.setIcon(super.image);
	}
	
	protected String getClassName() {
		return " ";
	}
	
	protected void updateGUI() {
		containingPanel.removeAll();
		classLabel.setText(getClassName());
		containingPanel.add(classLabel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		containingPanel.add(nameLabel, new GridBagConstraints(0, 1, 2, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		loadImage();
		containingPanel.add(imageLabel, new GridBagConstraints(0, 2, 2, 1, 0.0D, 0.0D, 11, 0, new Insets(0, 0, 0, 0), 0, 0));
		nameLabel.setText(data.name);
		locationLabel.setText(" ");
		containingPanel.add(locationLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		containingPanel.revalidate();
		containingPanel.repaint();
		add(containingPanel, "Center");
	}
	
	protected void guiInit() {
		super.guiInit();
		setCursor(new Cursor(12));
		setDragEnabled(false);
		remove(super.grip);
		loadImage();
	}
}
