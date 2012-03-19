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

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class ElementPrototypeDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected static TilePool tilePool = new TilePool();

	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype;
	protected javax.swing.JPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected String elementName;
	protected java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0);

	public ElementPrototypeDnDPanel() {
		subPanel.setLayout(new java.awt.GridBagLayout());
		subPanel.setOpaque(false);
		subPanel.setBorder(null);
		this.add(subPanel, java.awt.BorderLayout.CENTER);
		addDragSourceComponent(subPanel); // hack for stencils
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype) {
		this.elementPrototype = elementPrototype;
		setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(elementPrototype));

		if (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class.isAssignableFrom(elementPrototype.getElementClass())) {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponse"));
		} else if (edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(elementPrototype.getElementClass())) {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("response"));
		} else if (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestion"));
		} else if (edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom(elementPrototype.getElementClass())) {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("question"));
		} else if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(elementPrototype.getElementClass())) {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestionComponent"));
		} else {
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("elementPrototypeDnDPanel"));
		}

		elementName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(elementPrototype.getElementClass());

		refreshGUI();
	}

	@Override
	public void goToSleep() {
	}
	@Override
	public void wakeUp() {
	}

	@Override
	public void clean() {
		elementPrototype = null;
		setTransferable(null);
		refreshGUI();
	}

	@Override
	public void die() {
		clean();
		subPanel.removeAll();
		removeAll();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	public void refreshGUI() {
		java.awt.Component[] components = subPanel.getComponents();
		for (Component component2 : components) {
			if (component2 instanceof Tile) {
				removeDragSourceComponent(component2);
				tilePool.releaseTile(((Tile) component2).getText(), (Tile) component2);
			}
		}

		subPanel.removeAll();

		if (elementPrototype != null) {
			edu.cmu.cs.stage3.util.StringObjectPair[] propertyValues = elementPrototype.getKnownPropertyValues();
			java.util.Vector keys = new java.util.Vector();
			java.util.HashMap propertyMap = new java.util.HashMap();
			for (StringObjectPair propertyValue : propertyValues) {
				keys.add(propertyValue.getString());
				propertyMap.put(propertyValue.getString(), propertyValue.getObject());
			}

			constraints.gridx = 0;
			String format = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormat(elementPrototype.getElementClass());
			edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.startsWith("<<<") && token.endsWith(">>>")) {
					String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
					if (keys.contains(propertyName)) {
						addTile(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(propertyMap.get(propertyName), false), true);
						constraints.gridx++;
						keys.remove(propertyName);
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("no value available for " + token, null);
						addTile(token, true);
						constraints.gridx++;
					}
				} else if (token.startsWith("<<") && token.endsWith(">>")) {
					// skip this one
				} else if (token.startsWith("<") && token.endsWith(">")) {
					token = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
					addTile(token, true);
					constraints.gridx++;
				} else {
					while (token.indexOf("&lt;") > -1) {
						token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
					}
					addTile(token, false);
					constraints.gridx++;
				}
			}
		}

		revalidate();
		repaint();
	}

	public void addTile(String text, boolean opaque) {
		Tile tile = tilePool.getTile(text);
		tile.setOpaque(opaque);
		tile.setBorderEnabled(opaque);
		subPanel.add(tile, constraints);
		addDragSourceComponent(tile); // hack for stencils
	}

	public static class Tile extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel {
		protected String text;

		public Tile(String text) {
			this.text = text;
			setLayout(new java.awt.BorderLayout());
			setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("prototypeParameter"));
			javax.swing.JLabel tileLabel = new javax.swing.JLabel(text);
			tileLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 2));
			add(tileLabel, java.awt.BorderLayout.CENTER);
		}

		public String getText() {
			return text;
		}

		public void setBorderEnabled(boolean enabled) {
			if (enabled) {
				setBorder(border);
			} else {
				setBorder(null);
			}
		}

		@Override
		public void release() {
			// don't remove anything
		}
	}

	static class TilePool {
		protected java.util.HashMap tileListMap = new java.util.HashMap();

		public Tile getTile(String text) {
			java.util.LinkedList tileList = (java.util.LinkedList) tileListMap.get(text);
			if (tileList != null && !tileList.isEmpty()) {
				return (Tile) tileList.removeFirst();
			} else {
				Tile tilePanel = new Tile(text);
				return tilePanel;
			}
		}

		public void releaseTile(String text, Tile tile) {
			java.util.LinkedList tileList = (java.util.LinkedList) tileListMap.get(text);
			if (tileList == null) {
				tileList = new java.util.LinkedList();
				tileListMap.put(text, tileList);
			}
			tileList.addFirst(tile);
		}
	}
}
