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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class SaveForWebContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	private edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	private boolean ignoreSizeChange = false;
	private java.util.Vector m_okActionListeners = new java.util.Vector();

	private static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	public SaveForWebContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		jbInit();
		guiInit();
		authorNameTextField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveButton.doClick();
			}
		});
	}

	@Override
	public String getTitle() {
		return "Save World for the Web";
	}

	@Override
	public void addOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.addElement(l);
	}

	@Override
	public void removeOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.removeElement(l);
	}

	@Override
	public void addCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	@Override
	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}
	private void fireOKActionListeners() {
		java.awt.event.ActionEvent e = new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "OK");
		for (int i = 0; i < m_okActionListeners.size(); i++) {
			java.awt.event.ActionListener l = (java.awt.event.ActionListener) m_okActionListeners.elementAt(i);
			l.actionPerformed(e);
		}
	}

	public java.io.File getExportDirectory() {
		return new java.io.File(directoryPath.getText());
	}
	public String getExportFileName() {
		return htmlFileName.getText();
	}
	public String getExportAuthorName() {
		return authorNameTextField.getText();
	}
	public int getExportWidth() {
		return Integer.parseInt(widthTextField.getText());
	}
	public int getExportHeight() {
		return Integer.parseInt(heightTextField.getText());
	}
	public boolean isCodeToBeExported() {
		return saveCodeCheckBox.isSelected();
	}

	private void guiInit() {
		setSize(575, 510);
		widthTextField.setText("320");
		heightTextField.setText("240");
		constrainAspectRatioCheckBox.setSelected(true);

		widthTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent ev) {
				updateHeightTextField();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent ev) {
				updateHeightTextField();
			}
			private void updateHeightTextField() {
				if (constrainAspectRatioCheckBox.isSelected() && !ignoreSizeChange) {
					ignoreSizeChange = true;
					double aspectRatio = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
					int width = -1;
					try {
						width = Integer.parseInt(widthTextField.getText());
					} catch (NumberFormatException e) {}
					if (width > 0) {
						heightTextField.setText(Integer.toString((int) (width / aspectRatio)));
					}
					ignoreSizeChange = false;
				}
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			}
		});

		heightTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent ev) {
				updateWidthTextField();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent ev) {
				updateWidthTextField();
			}
			private void updateWidthTextField() {
				if (constrainAspectRatioCheckBox.isSelected() && !ignoreSizeChange) {
					ignoreSizeChange = true;
					double aspectRatio = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
					int height = -1;
					try {
						height = Integer.parseInt(heightTextField.getText());
					} catch (NumberFormatException e) {}
					if (height > 0) {
						widthTextField.setText(Integer.toString((int) (aspectRatio * height)));
					}
					ignoreSizeChange = false;
				}
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			}
		});

		setTitleTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent ev) {
				updateTextFields();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent ev) {
				updateTextFields();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			}
		});

		saveDirectoryCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				updateDirectory();
			}
		});

		saveCodeCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				updateTextFields();
			}
		});

		browseDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showDialog(htmlFileChooser, "Set directory");
			}
		});

		saveButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String authorName = authorNameTextField.getText();
				if (authorName.length() > 0) {
					int width;
					try {
						width = Integer.parseInt(widthTextField.getText());
					} catch (NumberFormatException nfe) {
						width = -1;
					}
					if (width > 0) {
						int height;
						try {
							height = Integer.parseInt(heightTextField.getText());
						} catch (NumberFormatException nfe) {
							height = -1;
						}
						if (height > 0) {
							SaveForWebContentPane.this.fireOKActionListeners();
						} else {
							edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("You must enter a valid height (a number greater than 0) before proceeding.", "You have not entered a valid height.", javax.swing.JOptionPane.WARNING_MESSAGE);
						}
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("You must enter a valid width (a number greater than 0) before proceeding.", "You have not entered a valid width.", javax.swing.JOptionPane.WARNING_MESSAGE);
					}
				} else {
					edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("You must enter the author's name before proceeding.", "You have not entered the author's name", javax.swing.JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		java.io.File currentDir = new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory"));
		try {
			if (currentDir.exists()) {
				rootDirectoryPath = currentDir.getAbsolutePath() + java.io.File.separator;
				htmlFileChooser.setCurrentDirectory(currentDir);
			} else {
				rootDirectoryPath = edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory().getAbsolutePath() + java.io.File.separator;
				htmlFileChooser.setCurrentDirectory(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory());
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			// For some reason this can potentially fail.
			// If an error occurs, it defaults to My Document as the current
			// directory.
			// System.err.println("Error in SaveForWebContentPane");
		}
		updateDirectory();

		// htmlFileChooser.setFileFilter( new
		// javax.swing.filechooser.FileFilter() {
		// public boolean accept( java.io.File file ) {
		// return file.isDirectory();
		// }
		// public String getDescription() {
		// return "Directories";
		// }
		// } );

		htmlFileChooser.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				if (ev.getActionCommand().equals(javax.swing.JFileChooser.APPROVE_SELECTION)) {
					java.io.File file = htmlFileChooser.getSelectedFile();
					rootDirectoryPath = file.getAbsolutePath() + java.io.File.separator;
					updateDirectory();
				} else if (ev.getActionCommand().equals(javax.swing.JFileChooser.CANCEL_SELECTION)) {
					// pass
				}
			}
		});
	}

	@Override
	public void setVisible(boolean visibility) {
		super.setVisible(visibility);
		if (visibility) {
			if (authoringTool != null) {
				java.io.File currentName = authoringTool.getCurrentWorldLocation();
				if (currentName != null) {
					String newTitle = currentName.getName();
					newTitle = newTitle.substring(0, newTitle.lastIndexOf("."));
					setTitleTextField.setText(newTitle);
					updateDirectory();
					updateTextFields();
					updateRatio();
				}
			}
		}
	}

	private String getValidFilename(String newValue) {
		// String toReturn = newValue;
		// if (newValue.indexOf("*") > -1) toReturn = newValue.replace('*',
		// '_');
		// if (newValue.indexOf("\\") > -1) toReturn = newValue.replace('\\',
		// '_');
		// if (newValue.indexOf("/") > -1) toReturn = newValue.replace('/',
		// '_');
		// if (newValue.indexOf("|") > -1) toReturn = newValue.replace('|',
		// '_');
		// if (newValue.indexOf(":") > -1) toReturn = newValue.replace(':',
		// '_');
		// if (newValue.indexOf("?") > -1) toReturn = newValue.replace('?',
		// '_');
		// if (newValue.indexOf("\"") > -1) toReturn = newValue.replace('\"',
		// '_');
		// if (newValue.indexOf(">") > -1) toReturn = newValue.replace('>',
		// '_');
		// if (newValue.indexOf("<") > -1) toReturn = newValue.replace('<',
		// '_');
		// return toReturn;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < newValue.length(); i++) {
			char c = newValue.charAt(i);
			switch (c) {
				case '*' :
				case '\\' :
				case '/' :
				case '|' :
				case ':' :
				case '?' :
				case '"' :
				case '>' :
				case '<' :
				case ' ' :
					sb.append('_');
					break;
				default :
					sb.append(c);
			}
		}
		return sb.toString();
	}

	private void updateDirectory() {
		if (saveDirectoryCheckBox.isSelected()) {
			localDirectoryPath = getValidFilename(setTitleTextField.getText());
			directoryPath.setText(rootDirectoryPath + localDirectoryPath + java.io.File.separator);
		} else {
			directoryPath.setText(rootDirectoryPath);
		}
	}

	private void updateTextFields() {
		String newValue = getValidFilename(setTitleTextField.getText());

		if (saveDirectoryCheckBox.isSelected()) {
			updateDirectory();
		}
		a2wFileName.setText(newValue + ".a2w");
		htmlFileName.setText(newValue + ".html");
	}

	private void updateRatio() {
		if (constrainAspectRatioCheckBox.isSelected()) {
			double aspectRatio = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
			int width = -1;
			int height = -1;
			try {
				width = Integer.parseInt(widthTextField.getText());
			} catch (NumberFormatException e) {}

			try {
				height = Integer.parseInt(heightTextField.getText());
			} catch (NumberFormatException e) {}

			if (width > 0) {
				height = (int) (width / aspectRatio);
				heightTextField.setText(Integer.toString(height));
			} else if (height > 0) {
				width = (int) (aspectRatio * height);
				widthTextField.setText(Integer.toString(width));
			}
		}
	}

	// /////////////
	// Callbacks
	// /////////////
	void constrainAspectRatioCheckBox_actionPerformed(java.awt.event.ActionEvent ev) {
		updateRatio();
	}

	// ////////////////
	// Autogenerated
	// ////////////////

	private java.awt.BorderLayout borderLayout1 = new java.awt.BorderLayout();
	private javax.swing.border.Border border1;
	private javax.swing.border.Border setTitleBorder;
	private javax.swing.border.Border border2;
	private javax.swing.JPanel mainPanel = new javax.swing.JPanel();
	private javax.swing.JFileChooser htmlFileChooser = new javax.swing.JFileChooser();
	private javax.swing.JPanel fileChooserPanel = new javax.swing.JPanel();
	private javax.swing.border.Border border3;
	private javax.swing.JTextField heightTextField = new javax.swing.JTextField();
	private java.awt.GridBagLayout gridBagLayout1 = new java.awt.GridBagLayout();
	private java.awt.GridBagLayout setTitleGridBagLayout = new java.awt.GridBagLayout();
	private javax.swing.JTextField widthTextField = new javax.swing.JTextField();
	private javax.swing.JLabel heightLabel = new javax.swing.JLabel();
	private javax.swing.JLabel widthLabel = new javax.swing.JLabel();
	private javax.swing.JCheckBox constrainAspectRatioCheckBox = new javax.swing.JCheckBox();
	private javax.swing.JPanel controlsPanel = new javax.swing.JPanel();
	private javax.swing.JPanel setTitlePanel = new javax.swing.JPanel();
	private javax.swing.JTextField setTitleTextField = new javax.swing.JTextField("My Alice World");
	private java.awt.GridBagLayout gridBagLayout2 = new java.awt.GridBagLayout();
	private javax.swing.border.Border border4;
	private javax.swing.border.TitledBorder titledBorder1;
	private java.awt.GridBagLayout gridBagLayout3 = new java.awt.GridBagLayout();
	private javax.swing.JPanel directoryPathPanel = new javax.swing.JPanel();
	private String rootDirectoryPath = "c:\\";
	private String localDirectoryPath = "My_Alice_World";
	private javax.swing.JLabel directoryPath = new javax.swing.JLabel(rootDirectoryPath + localDirectoryPath + java.io.File.separator);
	private javax.swing.JButton browseDirectoryButton = new javax.swing.JButton("browse");
	private javax.swing.JLabel a2wFileName = new javax.swing.JLabel("My_Alice_World.a2w");
	private javax.swing.JLabel htmlFileName = new javax.swing.JLabel("My_Alice_World.html");
	private javax.swing.JLabel appletLabel = new javax.swing.JLabel("aliceapplet.jar");
	private javax.swing.JTextField authorNameTextField = new javax.swing.JTextField();
	private javax.swing.JLabel authorLabel = new javax.swing.JLabel("Author's name");

	private String a2wTitle = "Your world";
	private String htmlTitle = "The web page";
	private String appletTitle = "The Alice applet";

	private javax.swing.JCheckBox saveDirectoryCheckBox = new javax.swing.JCheckBox();
	private javax.swing.JCheckBox saveCodeCheckBox = new javax.swing.JCheckBox();
	// private JLabel makeDirectoryLabel = new
	// JLabel("Use title to create new directory for the files below");
	private javax.swing.JLabel filesToSaveLabel = new javax.swing.JLabel("These files will be saved in this directory:");
	private javax.swing.JButton saveButton = new javax.swing.JButton("Save");
	private javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");

	private void jbInit() {
		border1 = new javax.swing.border.TitledBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, new java.awt.Color(142, 142, 142)), "Size in browser");
		setTitleBorder = new javax.swing.border.TitledBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, new java.awt.Color(142, 142, 142)), "Title");
		border2 = javax.swing.BorderFactory.createEmptyBorder(8, 8, 0, 8);
		border3 = javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12);
		border4 = javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, new java.awt.Color(142, 142, 142));
		titledBorder1 = new javax.swing.border.TitledBorder(border4, "Save Location");
		fileChooserPanel.setLayout(gridBagLayout3);
		setLayout(borderLayout1);
		mainPanel.setLayout(gridBagLayout2);
		mainPanel.setBorder(border3);
		heightTextField.setColumns(6);
		widthTextField.setColumns(6);
		heightLabel.setText("height:");
		widthLabel.setText("width:");
		setTitleTextField.setColumns(36);
		authorNameTextField.setColumns(28);
		constrainAspectRatioCheckBox.setText("constrain aspect ratio");
		constrainAspectRatioCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				constrainAspectRatioCheckBox_actionPerformed(e);
			}
		});
		controlsPanel.setLayout(gridBagLayout1);
		controlsPanel.setBorder(border1);

		setTitlePanel.setLayout(setTitleGridBagLayout);
		setTitlePanel.setBorder(setTitleBorder);

		fileChooserPanel.setBorder(titledBorder1);
		htmlFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
		// browseDirectoryButton.setPreferredSize(new java.awt.Dimension(80,
		// 21));
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		directoryPathPanel.setPreferredSize(new java.awt.Dimension(300, fontSize * 2 - 5));
		directoryPathPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black, 1));
		directoryPathPanel.setLayout(new java.awt.BorderLayout());
		directoryPathPanel.add(directoryPath, java.awt.BorderLayout.CENTER);
		add(mainPanel, java.awt.BorderLayout.CENTER);
		filesToSaveLabel.setForeground(java.awt.Color.black);
		saveDirectoryCheckBox.setSelected(true);
		saveDirectoryCheckBox.setText("Use title to create new directory for the files below");
		saveCodeCheckBox.setSelected(false);
		saveCodeCheckBox.setText("Add the code for this world to this page");

		a2wFileName.setToolTipText(a2wTitle);
		a2wFileName.setBounds(2, 2, 2, 2);
		htmlFileName.setToolTipText(htmlTitle);
		htmlFileName.setBounds(2, 2, 2, 2);
		appletLabel.setToolTipText(appletTitle);
		appletLabel.setBounds(2, 2, 2, 2);

		setTitlePanel.add(setTitleTextField, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		setTitlePanel.add(authorNameTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		setTitlePanel.add(authorLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));

		controlsPanel.add(widthLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 20, 4, 4), 0, 0));
		controlsPanel.add(widthTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 4, 0), 0, 0));
		controlsPanel.add(heightLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 20, 4, 4), 0, 0));
		controlsPanel.add(heightTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 4, 0), 0, 0));
		controlsPanel.add(constrainAspectRatioCheckBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 4, 0), 0, 0));

		fileChooserPanel.add(directoryPathPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 0, 8), 0, 0));
		fileChooserPanel.add(browseDirectoryButton, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 0, 0, 8), 0, 0));

		fileChooserPanel.add(saveDirectoryCheckBox, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 8, 4, 0), 0, 0));
		fileChooserPanel.add(saveCodeCheckBox, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 8, 4, 0), 0, 0));

		// fileChooserPanel.add(makeDirectoryLabel, new GridBagConstraints(1, 1,
		// 1, 1, 1.0, 1.0
		// ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0,
		// 4, 0), 0, 0));

		fileChooserPanel.add(filesToSaveLabel, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 10, 0, 0), 0, 0));

		fileChooserPanel.add(a2wFileName, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 20, 0, 0), 0, 0));
		fileChooserPanel.add(htmlFileName, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 20, 0, 0), 0, 0));
		fileChooserPanel.add(appletLabel, new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 20, 0, 0), 0, 0));

		mainPanel.add(setTitlePanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 12, 0), 0, 0));
		mainPanel.add(controlsPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 12, 0), 0, 0));
		mainPanel.add(fileChooserPanel, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(saveButton, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 0, 4, 0), 0, 0));
		mainPanel.add(cancelButton, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4, 4, 0), 0, 0));

	}
}