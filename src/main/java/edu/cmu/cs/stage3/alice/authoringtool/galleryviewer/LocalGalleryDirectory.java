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

public class LocalGalleryDirectory extends LocalGalleryObject {

	protected GalleryViewer.DirectoryStructure directoryData;

	protected static java.awt.Color localDirColor = new java.awt.Color(189, 184, 139);

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
	public void setImage(javax.swing.ImageIcon imageIcon) {
		if (imageIcon == GalleryViewer.noImageIcon) {
			super.setImage(GalleryViewer.noFolderImageIcon);
		} else {
			super.setImage(imageIcon);
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
		setBackground(localDirColor);
		setDragEnabled(false);
		this.remove(grip);
	}

	@Override
	public void respondToMouse() {
		if (mainViewer != null) {
			mainViewer.changeDirectory(directoryData);
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