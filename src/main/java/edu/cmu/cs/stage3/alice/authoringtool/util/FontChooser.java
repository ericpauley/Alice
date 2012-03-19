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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

public class FontChooser extends javax.swing.JPanel {
	protected java.util.HashSet changeListeners = new java.util.HashSet();

	public FontChooser(boolean sizeVisible, boolean previewVisible) {
		init(sizeVisible, previewVisible);
	}

	private void init(boolean sizeVisible, boolean previewVisible) {
		jbInit();

		fontSizeCombo.setVisible(sizeVisible);
		previewScrollPane.setVisible(previewVisible);

		String[] families = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String familie : families) {
			fontFaceCombo.addItem(familie);
		}
		fontFaceCombo.setSelectedIndex(0);

		fontSizeCombo.addItem(" 8");
		fontSizeCombo.addItem(" 9");
		fontSizeCombo.addItem("10");
		fontSizeCombo.addItem("11");
		fontSizeCombo.addItem("12");
		fontSizeCombo.addItem("14");
		fontSizeCombo.addItem("16");
		fontSizeCombo.addItem("18");
		fontSizeCombo.addItem("20");
		fontSizeCombo.addItem("22");
		fontSizeCombo.addItem("24");
		fontSizeCombo.addItem("26");
		fontSizeCombo.addItem("28");
		fontSizeCombo.addItem("36");
		fontSizeCombo.addItem("48");
		fontSizeCombo.addItem("72");
		fontSizeCombo.setSelectedIndex(0);
		fontSizeCombo.setInputVerifier( // TODO: should be more user-friendly
		new javax.swing.InputVerifier() {

			@Override
			public boolean verify(javax.swing.JComponent c) {
				try {
					String text = (String) ((javax.swing.JComboBox) c).getSelectedItem();
					Integer.parseInt(text.trim());
					return true;
				} catch (NumberFormatException e) {
					return false;
				} catch (Exception e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(e.getMessage(), e);
					return true; // unexpected error. inform the user and let it
									// slide.
				}
			}
		});

		java.awt.event.ActionListener actionListener = new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				FontChooser.this.fireChange(ev.getSource());
			}
		};

		fontFaceCombo.addActionListener(actionListener);
		fontSizeCombo.addActionListener(actionListener);
		boldToggle.addActionListener(actionListener);
		italicToggle.addActionListener(actionListener);
	}

	public java.awt.Font getChosenFont() {
		String familyName = (String) fontFaceCombo.getSelectedItem();
		int size = Integer.parseInt(((String) fontSizeCombo.getSelectedItem()).trim());
		int style = (boldToggle.isSelected() ? java.awt.Font.BOLD : 0) | (italicToggle.isSelected() ? java.awt.Font.ITALIC : 0);

		return new java.awt.Font(familyName, style, size);
	}

	public void setSizeVisible(boolean sizeVisible) {
		fontSizeCombo.setVisible(sizeVisible);
	}

	public void setPreviewVisible(boolean previewVisible) {
		previewScrollPane.setVisible(previewVisible);
	}

	public void setFontFace(String fontName) {
		fontFaceCombo.setSelectedItem(fontName);
		fireChange(fontFaceCombo);
	}

	public void setFontSize(int size) {
		fontSizeCombo.setSelectedItem(Integer.toString(size));
		fireChange(fontSizeCombo);
	}

	public void setFontStyle(int bitmask) {
		boolean bold, italic;

		bold = (bitmask & java.awt.Font.BOLD) > 0;
		italic = (bitmask & java.awt.Font.ITALIC) > 0;

		if (bold != boldToggle.isSelected()) {
			boldToggle.setSelected(bold);
			fireChange(boldToggle);
		}
		if (italic != italicToggle.isSelected()) {
			italicToggle.setSelected(italic);
			fireChange(italicToggle);
		}
	}

	public void setSampleText(String text) {
		if (text != null) {
			previewTextArea.setText(text);
		} else {
			previewTextArea.setText("The quick brown fox jumped over the lazy dog\'s back.");
		}
	}

	public void addChangeListener(javax.swing.event.ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(javax.swing.event.ChangeListener listener) {
		changeListeners.remove(listener);
	}

	protected void fireChange(Object source) {
		java.awt.Font font = getChosenFont();
		previewTextArea.setFont(font);
		javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent(source);

		for (java.util.Iterator iter = changeListeners.iterator(); iter.hasNext();) {
			((javax.swing.event.ChangeListener) iter.next()).stateChanged(ev);
		}
	}

	// //////////////////
	// Autogenerated
	// //////////////////

	JComboBox fontFaceCombo = new JComboBox();
	JComboBox fontSizeCombo = new JComboBox();
	JToggleButton boldToggle = new JToggleButton();
	JToggleButton italicToggle = new JToggleButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	Border border1;
	Component component1;
	JScrollPane previewScrollPane = new JScrollPane();
	JTextArea previewTextArea = new JTextArea();

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		component1 = Box.createGlue();
		// int fontSize =
		// Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		boldToggle.setFont(new java.awt.Font("SansSerif", 1, 14));
		boldToggle.setAlignmentX((float) 0.5);
		boldToggle.setMaximumSize(new Dimension(27, 27));
		boldToggle.setMinimumSize(new Dimension(27, 27));
		boldToggle.setPreferredSize(new Dimension(27, 27));
		boldToggle.setMargin(new Insets(2, 3, 2, 2));
		boldToggle.setText("B");
		italicToggle.setFont(new java.awt.Font("Serif", 3, 14));
		italicToggle.setMaximumSize(new Dimension(27, 27));
		italicToggle.setMinimumSize(new Dimension(27, 27));
		italicToggle.setPreferredSize(new Dimension(27, 27));
		italicToggle.setMargin(new Insets(2, 1, 2, 3));
		italicToggle.setText("I");
		setLayout(gridBagLayout1);
		setBorder(border1);
		fontSizeCombo.setMinimumSize(new Dimension(60, 26));
		fontSizeCombo.setPreferredSize(new Dimension(60, 26));
		fontSizeCombo.setEditable(true);
		previewTextArea.setText("The quick brown fox jumped over the lazy dog\'s back.");
		this.add(fontFaceCombo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(fontSizeCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		this.add(boldToggle, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		this.add(italicToggle, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(component1, new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(previewScrollPane, new GridBagConstraints(0, 1, 5, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(8, 0, 0, 0), 0, 0));
		previewScrollPane.getViewport().add(previewTextArea, null);
	}
}
