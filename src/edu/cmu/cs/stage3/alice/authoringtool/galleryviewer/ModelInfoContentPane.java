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

/**
 * @author culyba, dennisc
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

// Referenced classes of package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer:
//			  GalleryViewer, GalleryObject

public class ModelInfoContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	public ModelInfoContentPane() {
		guiInit();
	}

	
	public String getTitle() {
		return GalleryObject.getDisplayName(data.name);
	}
	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		addObjectButton.addActionListener( l );
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		addObjectButton.removeActionListener( l );
	}
	
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
		cancelButton.addActionListener( l );
	}
	
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
		cancelButton.removeActionListener( l );
	}

	public void set(GalleryViewer.ObjectXmlData data, javax.swing.ImageIcon image) {
		this.data = data;
		this.image = image;
		if (data.directoryData != null)
			filename = String.valueOf(data.directoryData.rootNode.rootPath) + String.valueOf(data.objectFilename);
		else if (data.parentDirectory != null)
			filename = String.valueOf(data.parentDirectory.rootNode.rootPath) + String.valueOf(data.objectFilename);
		imageLabel.setIcon(image);
		setName(GalleryObject.getDisplayName(data.name));
		nameLabel.setText(GalleryObject.getDisplayName(data.name));
		buildDetails();
	}

	private void buildDetails() {
		int count = 0;
		detailsPanel.removeAll();
		javax.swing.JLabel size = new javax.swing.JLabel("size:");
		size.setForeground(GalleryViewer.textColor);
		javax.swing.JLabel sizeDetail = new javax.swing.JLabel(String.valueOf(String.valueOf(String.valueOf(data.size))).concat(" kb"));
		sizeDetail.setForeground(GalleryViewer.textColor);
		detailsPanel.add(size, new java.awt.GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 0, 0, 0), 0, 0));
		detailsPanel.add(sizeDetail, new java.awt.GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 4, 0, 0), 0, 0));
		count++;
		for (int i = 0; i < data.details.size(); i++) {
			edu.cmu.cs.stage3.util.StringObjectPair current = (edu.cmu.cs.stage3.util.StringObjectPair) data.details.get(i);
			String currentString = current.getString();
			if (currentString.equalsIgnoreCase("modeledby"))
				currentString = "modeled by";
			else if (currentString.equalsIgnoreCase("paintedby"))
				currentString = "painted by";
			else if (currentString.equalsIgnoreCase("physicalsize"))
				currentString = "physical size";
			javax.swing.JLabel title = new javax.swing.JLabel(String.valueOf(String.valueOf(currentString)).concat(":"));
			javax.swing.JLabel detail = new javax.swing.JLabel();
			title.setForeground(GalleryViewer.textColor);
			detail.setForeground(GalleryViewer.textColor);
			if (current.getObject() == null || current.getObject().equals(""))
				continue;
			if (current.getObject() instanceof String) {
				detail.setText(current.getObject().toString());
				detailsPanel.add(title, new java.awt.GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 0, 0, 0), 0, 0));
				detailsPanel.add(detail, new java.awt.GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 4, 0, 0), 0, 0));
				count++;
				continue;
			}
			if (!(current.getObject() instanceof java.util.Vector))
				continue;
			java.util.Vector detailVector = (java.util.Vector) current.getObject();
			javax.swing.JPanel detailContainer = new javax.swing.JPanel();
			detailContainer.setOpaque(false);
			detailContainer.setBorder(null);
			detailContainer.setLayout(new javax.swing.BoxLayout(detailContainer, 1));
			for (int index = 0; index < detailVector.size(); index++) {
				if (detailVector.get(index) == null || !(detailVector.get(index) instanceof String))
					continue;
				String currentDetail = (String) detailVector.get(index);
				if (!currentDetail.equalsIgnoreCase("")) {
					javax.swing.JLabel detailLabel = new javax.swing.JLabel(currentDetail);
					detailLabel.setForeground(GalleryViewer.textColor);
					detailContainer.add(detailLabel);
				}
			}

			detailsPanel.add(title, new java.awt.GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 0, 0, 0), 0, 0));
			detailsPanel.add(detailContainer, new java.awt.GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 4, 0, 0), 0, 0));
			count++;
		}

		detailsPanel.add(javax.swing.Box.createVerticalGlue(), new java.awt.GridBagConstraints(0, count, 1, 1, 1.0D, 1.0D, 18, 1, new java.awt.Insets(0, 0, 0, 0), 0, 0));
	}

	private void guiInit() {
		setBackground(GalleryObject.BACKGROUND);
		imageLabel = new javax.swing.JLabel();
		nameLabel = new javax.swing.JLabel();
		nameLabel.setForeground(GalleryViewer.textColor);
		detailsPanel = new javax.swing.JPanel();
		detailsPanel.setOpaque(false);
		detailsPanel.setLayout(new java.awt.GridBagLayout());
		addObjectButton = new javax.swing.JButton("Add instance to world");
		cancelButton = new javax.swing.JButton("Cancel");
		setLayout(new java.awt.GridBagLayout());
		add(nameLabel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new java.awt.Insets(2, 0, 0, 0), 0, 0));
		add(imageLabel, new java.awt.GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 10, 0, new java.awt.Insets(0, 20, 20, 20), 0, 0));
		add(addObjectButton, new java.awt.GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 18, 0, new java.awt.Insets(2, 6, 6, 2), 0, 0));
		add(cancelButton, new java.awt.GridBagConstraints(1, 2, 1, 1, 0.0D, 0.0D, 12, 0, new java.awt.Insets(2, 2, 6, 6), 0, 0));
		add(detailsPanel, new java.awt.GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 12, 0, new java.awt.Insets(2, 2, 2, 6), 0, 0));
	}
	GalleryViewer.ObjectXmlData data;
	javax.swing.ImageIcon image;
	javax.swing.JLabel imageLabel;
	javax.swing.JLabel nameLabel;
	javax.swing.JButton addObjectButton;
	javax.swing.JButton cancelButton;
	javax.swing.JPanel detailsPanel;
	String filename;
	
	public String getFilename() {
		return filename;
	}
}
