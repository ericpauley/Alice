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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class WorldFileChooser extends javax.swing.JFileChooser implements java.beans.PropertyChangeListener {
	public WorldFileChooser() {
		this.addPropertyChangeListener(javax.swing.JFileChooser.DIRECTORY_CHANGED_PROPERTY, this);
	}

	@Override
	public void approveSelection() {
		if (getDialogType() == javax.swing.JFileChooser.OPEN_DIALOG) {
			java.io.File worldDir = getSelectedFile();
			java.io.File worldFile = new java.io.File(worldDir, "elementData.xml");
			if (!worldFile.exists()) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Your selection is not a valid world folder.");
			} else if (!worldFile.canRead()) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Cannot read world from disk.  Access denied.");
			} else if (!isWorld(worldFile)) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Your selection is not a valid world folder.");
			} else {
				super.approveSelection();
			}
		} else if (getDialogType() == javax.swing.JFileChooser.SAVE_DIALOG) {
			java.io.File worldDir = getSelectedFile();
			java.io.File worldFile = new java.io.File(worldDir, "elementData.xml");
			if (worldFile.exists()) {
				int retVal = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog("A World already exists in " + worldDir.getAbsolutePath() + ".\nWould you like to replace it?", "Replace World?", javax.swing.JOptionPane.YES_NO_OPTION);
				if (retVal == javax.swing.JOptionPane.YES_OPTION) {
					super.approveSelection();
				}
			} else {
				super.approveSelection();
			}
		} else {
			super.approveSelection();
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent ev) {
		java.io.File currentDirectory = (java.io.File) ev.getNewValue();
		if (currentDirectory != null && currentDirectory.canRead()) {
			java.io.File worldFile = new java.io.File(currentDirectory, "elementData.xml");
			if (worldFile.exists() && worldFile.canRead() && isWorld(worldFile)) {
				setCurrentDirectory(currentDirectory.getParentFile());
				setSelectedFile(currentDirectory);
				approveSelection();
			}
		}
	}

	protected boolean isWorld(java.io.File xmlFile) {
		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		try {
			javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(xmlFile);

			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.normalize();
			String className = rootElement.getAttribute("class");
			return className != null && className.trim().equals("edu.cmu.cs.stage3.alice.core.World");
			// org.w3c.dom.NodeList classList =
			// rootElement.getElementsByTagName( "class" );
			// org.w3c.dom.Node classNode = classList.item( 0 );
			// org.w3c.dom.Text classNameElement =
			// (org.w3c.dom.Text)classNode.getFirstChild();
			// String className = classNameElement.getData().trim();
			// return className.equals( "edu.cmu.cs.stage3.alice.core.World" );
		} catch (org.xml.sax.SAXParseException spe) {} catch (org.xml.sax.SAXException sxe) {} catch (javax.xml.parsers.ParserConfigurationException pce) {} catch (java.io.IOException ioe) {}

		return false;
	}
}