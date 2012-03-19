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

package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sandbox;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company: Stage3
 * </p>
 * 
 * @author Ben Buchwald
 * @version 1.0
 */

public class MocapImporterOptionsDialog extends javax.swing.JDialog {
	class ElementOrNullWrapper {
		public Element obj = null;
		public String text = null;

		public ElementOrNullWrapper(Element o, String t) {
			obj = o;
			text = t;
		}

		public ElementOrNullWrapper(Element o) {
			obj = o;
		}

		@Override
		public String toString() {
			if (text != null) {
				return text;
			} else {
				return obj.name.getStringValue();
			}
		}
	}

	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JList partsList = new JList();
	JLabel promptLabel = new JLabel();
	JScrollPane jScrollPane1 = new JScrollPane();
	JComboBox nativeFPSCombo = new JComboBox();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JComboBox aliceFPSCombo = new JComboBox();
	JButton okButton = new JButton();
	JLabel jLabel3 = new JLabel();
	JTextField jSkelFile = new JTextField();
	JButton jFileBoxButton = new JButton();
	JButton cancelButton = new JButton();

	public Element selectedPart = null;
	public double fps = 30;
	public int nativeFPS = 60;
	public String asfFile = "etc/Skeleton.asf";
	public File ASFPath = new File("");
	public boolean ok = false;

	public MocapImporterOptionsDialog() {
		try {
			setContentPane(new JPanel());
			jbInit();
			guiInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void guiInit() {
		DefaultListModel listOfStuff = new DefaultListModel();
		partsList.setModel(listOfStuff);

		listOfStuff.addElement(new ElementOrNullWrapper(null, "Build Model from skeleton file"));

		Sandbox[] possibilities = (Sandbox[]) AuthoringTool.getHack().getWorld().sandboxes.getArrayValue();

		for (Sandbox possibilitie : possibilities) {
			listOfStuff.addElement(new ElementOrNullWrapper(possibilitie));
		}
		partsList.setSelectedIndex(0);

		aliceFPSCombo.insertItemAt("15", 0);
		aliceFPSCombo.insertItemAt("30", 1);
		aliceFPSCombo.setSelectedIndex(1);

		nativeFPSCombo.insertItemAt("60", 0);
		nativeFPSCombo.insertItemAt("120", 1);
		nativeFPSCombo.setSelectedIndex(0);
	}

	private void jbInit() throws Exception {
		getContentPane().setLayout(gridBagLayout1);
		partsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		promptLabel.setText("Which character shall I animate?");
		setModal(true);
		setTitle("Mocap Importer Options");
		jLabel1.setText("Motion Captured FPS:");
		jLabel2.setText("Alice KeyFrames/sec:");
		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		jLabel3.setText("Skeleton File:");
		jSkelFile.setPreferredSize(new Dimension(100, 21));
		jSkelFile.setText("etc/Skeleton.asf");
		jFileBoxButton.setText("...");
		jFileBoxButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jFileBoxButton_actionPerformed(e);
			}
		});
		nativeFPSCombo.setEditable(true);
		aliceFPSCombo.setEditable(true);
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		getContentPane().add(promptLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		getContentPane().add(jScrollPane1, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 60));
		getContentPane().add(jLabel1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		getContentPane().add(jLabel2, new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		getContentPane().add(nativeFPSCombo, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		getContentPane().add(aliceFPSCombo, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		getContentPane().add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		getContentPane().add(jSkelFile, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		jScrollPane1.getViewport().add(partsList, null);
		getContentPane().add(jFileBoxButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 5), -19, -8));
		getContentPane().add(okButton, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		getContentPane().add(cancelButton, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	void okButton_actionPerformed(ActionEvent e) {
		selectedPart = ((ElementOrNullWrapper) partsList.getSelectedValue()).obj;
		asfFile = jSkelFile.getText();
		fps = Double.parseDouble((String) aliceFPSCombo.getSelectedItem());
		nativeFPS = Integer.parseInt((String) nativeFPSCombo.getSelectedItem());
		ok = true;
		setVisible(false);
	}

	public Element getSelectedPart() {
		return selectedPart;
	}

	public double getFPS() {
		return fps;
	}

	public int getNativeFPS() {
		return nativeFPS;
	}

	public String getASFFile() {
		return asfFile;
	}

	public void setNativeFPS(int nfps) {
		nativeFPS = nfps;
		if (nfps == 60) {
			nativeFPSCombo.setSelectedIndex(0);
		} else if (nfps == 120) {
			nativeFPSCombo.setSelectedIndex(1);
		} else {
			nativeFPSCombo.insertItemAt(String.valueOf(nativeFPS), 2);
			nativeFPSCombo.setSelectedIndex(2);
		}
	}

	public void setASFFile(String filename) {
		jSkelFile.setText(filename);
	}

	public void setASFPath(File path) {
		ASFPath = path;
	}

	void jFileBoxButton_actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();

		class ASFFileFilter extends FileFilter {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.toString().toLowerCase().endsWith(".asf")) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "ASF - Acclaim Skeleton File";
			}
		}
		chooser.setFileFilter(new ASFFileFilter());
		try {
			chooser.setCurrentDirectory(ASFPath);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			// for some reason this can potentially fail in jdk1.4.2_04
		}
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			jSkelFile.setText(chooser.getSelectedFile().getPath());
		}
	}

	void cancelButton_actionPerformed(ActionEvent e) {
		ok = false;
		setVisible(false);
	}

}