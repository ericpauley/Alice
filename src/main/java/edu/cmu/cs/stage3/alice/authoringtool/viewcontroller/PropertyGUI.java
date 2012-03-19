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

/**
 * @author Jason Pratt
 */
public class PropertyGUI extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected PropertyDnDPanel propertyDnDPanel;
	protected javax.swing.JComponent propertyViewController;
	protected javax.swing.JLabel equalsLabel = new javax.swing.JLabel(" = ");

	public PropertyGUI() {
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		setOpaque(false);
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		clean();

		propertyDnDPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyDnDPanel(property);
		propertyViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(property, includeDefaults, allowExpressions, true, factory);

		add(propertyDnDPanel);
		add(equalsLabel);
		add(propertyViewController);
		add(javax.swing.Box.createHorizontalGlue());
	}

	@Override
	public void goToSleep() {
	}
	@Override
	public void wakeUp() {
	}

	@Override
	public void clean() {
		removeAll();
		if (propertyDnDPanel != null) {
			propertyDnDPanel.release();
		}
		if (propertyViewController instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) propertyViewController).release();
		}
		propertyDnDPanel = null;
		propertyViewController = null;
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	@Override
	public void remove(java.awt.Component c) {
		super.remove(c);
	}
}
