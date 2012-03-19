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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import java.awt.Component;

/**
 * @author Jason Pratt
 */
public class ResponsePrintViewController extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement {
	protected edu.cmu.cs.stage3.alice.core.response.Print printStatement;
	protected javax.swing.JPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected javax.swing.JLabel printPrefixLabel = new javax.swing.JLabel("Print: ");
	protected javax.swing.JLabel printSuffixLabel = new javax.swing.JLabel("");
	protected java.util.HashMap guiMap = new java.util.HashMap();
	protected MouseListener mouseListener = new MouseListener();
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener commentedListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			if (ev.getValue().equals(Boolean.TRUE)) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
			ResponsePrintViewController.this.revalidate();
			ResponsePrintViewController.this.repaint();
		}
	};
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener updateListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			ResponsePrintViewController.this.refreshGUI();
		}
	};
	protected boolean sleeping = false;

	public ResponsePrintViewController() {
		setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 3, 2));

		subPanel.setLayout(new java.awt.GridBagLayout());
		subPanel.setOpaque(false);
		subPanel.setBorder(null);

		addMouseListener(mouseListener);
		grip.addMouseListener(mouseListener);
		subPanel.addMouseListener(mouseListener); // this didn't used to be
													// necessary. I don't know
													// what's going on
	}

	public edu.cmu.cs.stage3.alice.core.response.Print get() {
		return printStatement;
	}

	public void set(edu.cmu.cs.stage3.alice.core.response.Print printStatement) {
		super.reset();

		stopListening();

		this.printStatement = printStatement;
		if (this.printStatement != null) {
			setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(printStatement));
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Print"));

			this.add(subPanel, java.awt.BorderLayout.CENTER);
			addDragSourceComponent(subPanel);

			startListening();
		}

		refreshGUI();
	}

	protected void startListening() {
		if (printStatement != null) {
			printStatement.isCommentedOut.addPropertyListener(commentedListener);
			printStatement.text.addPropertyListener(updateListener);
			printStatement.object.addPropertyListener(updateListener);
		}
	}

	protected void stopListening() {
		if (printStatement != null) {
			printStatement.isCommentedOut.removePropertyListener(commentedListener);
			printStatement.text.removePropertyListener(updateListener);
			printStatement.object.removePropertyListener(updateListener);
		}
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		java.awt.Component[] children = subPanel.getComponents();
		for (Component element : children) {
			element.setEnabled(b);
		}
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);
		if (printStatement.isCommentedOut.booleanValue()) {
			edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, getBounds());
		}
	}

	@Override
	public void goToSleep() {
		stopListening();
		sleeping = true;
	}

	@Override
	public void wakeUp() {
		startListening();
		sleeping = false;
	}

	@Override
	public void clean() {
		stopListening();
		printStatement = null;
		setTransferable(null);
		removeAll();
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		super.release();
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	public void refreshGUI() {
		subPanel.removeAll();

		if (printStatement != null) {
			String format = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormat(printStatement.getClass());
			edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer formatTokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
			if (formatTokenizer.hasMoreTokens()) {
				printPrefixLabel.setText(formatTokenizer.nextToken());
			} else {
				printPrefixLabel.setText("");
			}
			String token = null;
			while (formatTokenizer.hasMoreTokens()) {
				token = formatTokenizer.nextToken();
			}
			if (token != null && !(token.startsWith("<") && token.endsWith(">"))) {
				printSuffixLabel.setText(token);
			} else {
				printSuffixLabel.setText("");
			}

			int i = 0;
			if (printStatement.text.get() == null) {
				boolean omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.object);
				javax.swing.JComponent objectPropertyGui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(printStatement.object, true, true, omitName, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(printStatement.object));

				subPanel.add(printPrefixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(objectPropertyGui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(printSuffixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			} else if (printStatement.object.get() == null) {
				boolean omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
				javax.swing.JComponent textPropertyGui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(printStatement.text, true, true, omitName, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(printStatement.text));

				subPanel.add(printPrefixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(textPropertyGui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(printSuffixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			} else {
				boolean omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
				javax.swing.JComponent textPropertyGui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(printStatement.text, true, true, omitName, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(printStatement.text));
				omitName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
				javax.swing.JComponent objectPropertyGui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(printStatement.object, true, true, omitName, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(printStatement.object));

				subPanel.add(printPrefixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(textPropertyGui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 2), 0, 0));
				subPanel.add(objectPropertyGui, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				subPanel.add(printSuffixLabel, new java.awt.GridBagConstraints(i++, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			}

			subPanel.add(javax.swing.Box.createGlue(), new java.awt.GridBagConstraints(i++, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		}

		revalidate();
		repaint();
	}

	class MouseListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {

		@Override
		public void popupResponse(java.awt.event.MouseEvent ev) {
			java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(printStatement);
			if (structure != null && !structure.isEmpty()) {
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.createAndShowElementPopupMenu(printStatement, structure, ResponsePrintViewController.this, ev.getX(), ev.getY());
			}
		}
	}
}
