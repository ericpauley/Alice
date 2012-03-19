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

public class WebGalleryDirectory extends WebGalleryObject {

	protected GalleryViewer.DirectoryStructure directoryData;
	protected boolean isTopLevelDirectory = false;

	protected static java.awt.Color webDirColor = new java.awt.Color(189, 184, 139);

	@Override
	protected String getToolTipString() {
		return "<html><body><p>Group of Objects</p><p>Click to open this group.</p></body></html>";
	}

	@Override
	public void set(GalleryViewer.ObjectXmlData dataIn) throws java.lang.IllegalArgumentException {
		if (dataIn != null) {
			directoryData = dataIn.directoryData;
			super.set(dataIn);
		}
	}

	@Override
	protected String getClassName() {
		return " ";
	}

	@Override
	protected void guiInit() {
		super.guiInit();
		setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		setBackground(webDirColor);
		setDragEnabled(false);
		this.remove(grip);
	}

	@Override
	protected void updateGUI() {
		super.updateGUI();
	}

	@Override
	public void setImage(javax.swing.ImageIcon imageIcon) {
		if (imageIcon == GalleryViewer.noImageIcon) {
			super.setImage(GalleryViewer.noFolderImageIcon);
		} else {
			super.setImage(imageIcon);
		}
	}

	@Override
	public void respondToMouse() {
		if (mainViewer != null) {
			int dialogVal = -1;
			if (!GalleryViewer.alreadyEnteredWebGallery && mainViewer.shouldShowWebWarning()) {
				dialogVal = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog("You are about to enter the online gallery. This is accessed through the internet\n" + " and is potentially slow depending on your connection.", "Web gallery may be slow", javax.swing.JOptionPane.WARNING_MESSAGE);
				if (dialogVal == javax.swing.JOptionPane.YES_OPTION) {
					GalleryViewer.enteredWebGallery();
					mainViewer.changeDirectory(directoryData);
				}
			} else {
				GalleryViewer.enteredWebGallery();
				mainViewer.changeDirectory(directoryData);
			}
		}
	}

	@Override
	public void galleryMouseExited() {
		/*
		 * if (mouseOver){ mouseOver = false; this.repaint(); }
		 */
	}

	@Override
	public void galleryMouseEntered() {
		/*
		 * if (!mouseOver){ mouseOver = true; this.repaint(); }
		 */
	}

}