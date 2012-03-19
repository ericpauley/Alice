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
public class VariableGUI extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement {
	protected VariableDnDPanel variableDnDPanel;
	protected javax.swing.JComponent variableViewController;
	protected javax.swing.JLabel equalsLabel = new javax.swing.JLabel(" = ");

	public VariableGUI() {
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		setOpaque(false);
		setBorder(null);
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.core.Variable variable, boolean includeDefaults, edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		clean();

		variableDnDPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel(variable);
		variableViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(variable.value, includeDefaults, false, true, factory);

		add(variableDnDPanel);
		add(equalsLabel);
		add(variableViewController);
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
		if (variableDnDPanel != null) {
			variableDnDPanel.release();
		}
		if (variableViewController instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable) variableViewController).release();
		}
		variableDnDPanel = null;
		variableViewController = null;
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
