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
 * @author David Culyba
 * 
 */

public abstract class GalleryObject extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement {

	protected GalleryViewer.ObjectXmlData data;
	protected javax.swing.JLabel nameLabel;
	protected javax.swing.JLabel classLabel;
	protected javax.swing.JLabel imageLabel;
	protected javax.swing.JLabel sizeLabel;
	protected javax.swing.JLabel locationLabel;
	protected javax.swing.JPanel containingPanel;
	protected javax.swing.ImageIcon image;
	protected GalleryViewer mainViewer;
	protected String displayName;
	protected String location;
	protected boolean hasAttribution = true;
	protected String rootPath;
	protected GalleryMouseAdapter mouseAdapter = new GalleryMouseAdapter();

	protected class GalleryMouseAdapter extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {

		@Override
		public void mouseExited(java.awt.event.MouseEvent m) {
			galleryMouseExited();
		}

		@Override
		public void mouseEntered(java.awt.event.MouseEvent m) {
			galleryMouseEntered();
		}

		@Override
		protected void singleClickResponse(java.awt.event.MouseEvent ev) {
			respondToMouse();
		}
	}

	protected static final java.awt.Color HIGHLITE = new java.awt.Color(255, 255, 255);
	protected static final java.awt.Color BACKGROUND = new java.awt.Color(128, 128, 128);

	protected static final java.awt.Color FASTEST_COLOR = new java.awt.Color(0, 255, 0);
	protected static final java.awt.Color MIDDLE_COLOR = new java.awt.Color(255, 255, 0);
	protected static final java.awt.Color SLOWEST_COLOR = new java.awt.Color(255, 0, 0);

	protected static final int FASTEST_SIZE = 0;
	protected static final int SLOWEST_SIZE = 1000;

	protected java.awt.Color sizeColor;

	protected boolean mouseOver = false;

	protected java.awt.datatransfer.Transferable transferable;

	protected String getToolTipString() {
		return "";
	}

	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		// nameLabel.setToolTipText(text);
		// imageLabel.setToolTipText(text);
		// sizeLabel.setToolTipText(text);
		// locationLabel.setToolTipText(text);
		containingPanel.setToolTipText(text);
		grip.setToolTipText(text);
		// image.setToolTipText(text);
	}

	public GalleryObject() {
		guiInit();
	}

	public javax.vecmath.Vector3d getBoundingBox() {
		return data.dimensions;
	}

	@Override
	public java.awt.Image getImage() {
		return super.getImage();
	}

	public String getUniqueIdentifier() {
		String root = "";
		String path = GalleryViewer.reverseWebReady(data.objectFilename.replace(':', '_'));
		if (data.type == GalleryViewer.WEB) {
			root = GalleryViewer.webGalleryName;
		}
		if (data.type == GalleryViewer.CD) {
			root = GalleryViewer.cdGalleryName;
		}
		if (data.type == GalleryViewer.LOCAL) {
			root = GalleryViewer.localGalleryName;
		}
		int index = path.lastIndexOf(".xml");
		if (index > -1 && index < path.length()) {
			int dirIndex = path.lastIndexOf(java.io.File.separator);
			if (dirIndex > -1 && dirIndex < path.length()) {
				path = path.substring(0, dirIndex);
			} else {
				path = "";
			}
		}
		if (path != "") {
			path = java.io.File.separator + path;
		}
		String toReturn = root + path;
		return toReturn;
	}

	public void set(GalleryViewer.ObjectXmlData dataIn) throws java.lang.IllegalArgumentException {
		if (dataIn != null) {
			clean();
			data = dataIn;
			mainViewer = data.mainViewer;
			if (data.transferable != null) {
				setTransferable(data.transferable);
			}
			if (data.directoryData != null) {
				rootPath = data.directoryData.rootNode.rootPath;
			} else if (data.parentDirectory != null) {
				rootPath = data.parentDirectory.rootNode.rootPath;
			} else {}
			displayName = GalleryViewer.cleanUpName(dataIn.name);
			updateGUI();
			wakeUp();
		} else {
			clean();
			data = null;
			mainViewer = null;
		}
	}

	@Override
	public void goToSleep() {
		removeMouseListener(mouseAdapter);
		containingPanel.removeMouseListener(mouseAdapter);
		grip.removeMouseListener(mouseAdapter);
		removeDragSourceComponent(containingPanel);
	}

	@Override
	public void wakeUp() {
		addMouseListener(mouseAdapter);
		containingPanel.addMouseListener(mouseAdapter);
		grip.addMouseListener(mouseAdapter);
		addDragSourceComponent(containingPanel);
	}

	@Override
	public void clean() {
		goToSleep();
		data = null;
		nameLabel.setText(null);
		classLabel.setText(null);
		imageLabel.setIcon(null);
		imageLabel.setText(null);
		sizeLabel.setText(null);
		image = null;
		mainViewer = null;
	}

	@Override
	public void die() {
		clean();
	}

	public abstract void loadImage();

	public static java.awt.Image retrieveImage(String root, String filename, long timestamp) {
		return null;
	}

	protected void setGalleryViewer(GalleryViewer viewer) {
		mainViewer = viewer;
	}

	public void setImage(javax.swing.ImageIcon imageIcon) {
		image = imageIcon;
		imageLabel.setText(null);
		imageLabel.setIcon(image);
		revalidate();
		this.repaint();
	}

	protected String getClassName() {
		return "Class";
	}

	public static void storeThumbnail(String thumbFilename, java.awt.Image toStore, long timeStamp) {
		if (toStore != null) {
			String codec = "png";
			java.io.File thumbFile = GalleryViewer.createFile(thumbFilename);
			if (thumbFile != null) {
				try {
					java.io.FileOutputStream fos = new java.io.FileOutputStream(thumbFile);
					java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fos);
					java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);
					edu.cmu.cs.stage3.image.ImageIO.store(codec, dos, toStore);
					dos.flush();
					fos.close();
					thumbFile.setLastModified(timeStamp);
				} catch (InterruptedException ie) {} catch (java.io.IOException ioe) {}
			}
		}
	}

	protected java.awt.Color interpolateColor(int point, int low, int high, java.awt.Color lowColor, java.awt.Color highColor) {
		float dLow = (float) (point - low) / (high - low);
		float dHigh = (float) (high - point) / (high - low);
		int newR = (int) (highColor.getRed() * dLow) + (int) (lowColor.getRed() * dHigh);
		if (newR > 255) {
			newR = 255;
		}
		int newG = (int) (highColor.getGreen() * dLow) + (int) (lowColor.getGreen() * dHigh);
		if (newG > 255) {
			newG = 255;
		}
		int newB = (int) (highColor.getBlue() * dLow) + (int) (lowColor.getBlue() * dHigh);
		if (newB > 255) {
			newB = 255;
		}
		return new java.awt.Color(newR, newG, newB);
	}

	protected java.awt.Color getSizeColor(int toScale) {
		if (toScale > SLOWEST_SIZE) {
			return SLOWEST_COLOR;
		}
		if (toScale < FASTEST_SIZE) {
			return FASTEST_COLOR;
		}
		int middle = (SLOWEST_SIZE - FASTEST_SIZE) / 2;
		if (toScale <= middle) {
			return interpolateColor(toScale, FASTEST_SIZE, middle, FASTEST_COLOR, MIDDLE_COLOR);
		} else {
			return interpolateColor(toScale, middle, SLOWEST_SIZE, MIDDLE_COLOR, SLOWEST_COLOR);
		}
	}

	protected void guiInit() {
		setBackground(BACKGROUND);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		containingPanel = new javax.swing.JPanel();
		containingPanel.setLayout(new java.awt.GridBagLayout());
		containingPanel.setBorder(null);
		containingPanel.setOpaque(false);
		nameLabel = new javax.swing.JLabel();
		nameLabel.setForeground(GalleryViewer.textColor);
		nameLabel.setOpaque(false);
		classLabel = new javax.swing.JLabel();
		classLabel.setForeground(GalleryViewer.textColor);
		classLabel.setOpaque(false);
		classLabel.setText(getClassName());
		imageLabel = new javax.swing.JLabel();
		imageLabel.setOpaque(false);
		sizeLabel = new javax.swing.JLabel();
		sizeLabel.setOpaque(false);
		locationLabel = new javax.swing.JLabel();
		locationLabel.setForeground(GalleryViewer.textColor);
		locationLabel.setOpaque(false);
		image = new javax.swing.ImageIcon();
		this.add(containingPanel, java.awt.BorderLayout.CENTER);
		setToolTipText(getToolTipString());
	}

	public static String getDisplayName(String toDisplay) {
		String displayNameToReturn = new String(toDisplay);
		if (Character.isLowerCase(displayNameToReturn.charAt(0))) {
			displayNameToReturn = Character.toUpperCase(displayNameToReturn.charAt(0)) + displayNameToReturn.substring(1);
		}
		return displayNameToReturn;
	}

	protected void updateGUI() {
		containingPanel.removeAll();
		containingPanel.add(classLabel, new java.awt.GridBagConstraints(1, 0, 2, 1, 0, 0, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		containingPanel.add(nameLabel, new java.awt.GridBagConstraints(1, 1, 2, 1, 0, 0, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		containingPanel.add(imageLabel, new java.awt.GridBagConstraints(1, 2, 2, 1, 0, 0, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		// this.add(grip, new
		// java.awt.GridBagConstraints(0,0,1,3,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		classLabel.setText(getClassName());
		nameLabel.setText(getDisplayName(displayName));
		sizeColor = getSizeColor(data.size);
		sizeLabel.setForeground(sizeColor);
		imageLabel.setIcon(GalleryViewer.loadingImageIcon);
		sizeLabel.setText(String.valueOf(data.size) + " kb");
		locationLabel.setText(" on " + location);
		if (data.size > 0 && data.type == GalleryViewer.WEB) {
			sizeLabel.setText(String.valueOf(data.size) + "kb");
			sizeLabel.setForeground(sizeColor);
			containingPanel.add(sizeLabel, new java.awt.GridBagConstraints(1, 3, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		}
		containingPanel.add(locationLabel, new java.awt.GridBagConstraints(2, 3, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		containingPanel.revalidate();
		containingPanel.repaint();
	}

	public void respondToMouse() {
		if (mainViewer != null) {
			mainViewer.displayModelDialog(data, image);
		}
	}

	public void galleryMouseExited() {
		if (mouseOver) {
			mouseOver = false;
			if (hasAttribution) {
				mainViewer.removeAttribution();
			}
			GalleryObject.this.repaint();
		}
	}

	public void galleryMouseEntered() {
		if (!mouseOver) {
			mouseOver = true;
			if (hasAttribution) {
				mainViewer.diplayAttribution(data);
			}
			GalleryObject.this.repaint();
		}
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);
		if (mouseOver) {
			Object oldAntialiasing = null;
			if (g instanceof java.awt.Graphics2D) {
				oldAntialiasing = ((java.awt.Graphics2D) g).getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
			}
			java.awt.Rectangle bounds = getBounds();
			for (int i = 1; i <= 2; i++) {
				g.setColor(new java.awt.Color(HIGHLITE.getRed(), HIGHLITE.getGreen(), HIGHLITE.getBlue(), 255 - (i - 1) * 60));
				g.drawRoundRect(i, i, bounds.width - 2 * i, bounds.height - 2 * i, arcWidth, arcHeight);
			}
			if (g instanceof java.awt.Graphics2D) {
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
			}
		}
	}

}