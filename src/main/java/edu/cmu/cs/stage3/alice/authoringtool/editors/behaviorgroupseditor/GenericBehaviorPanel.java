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

package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class GenericBehaviorPanel extends BasicBehaviorPanel {

	public GenericBehaviorPanel() {
		super();
	}

	@Override
	public void set(edu.cmu.cs.stage3.alice.core.Behavior behavior, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		super.set(behavior, authoringTool);
	}

	@Override
	protected void removeAllListening() {
		super.removeAllListening();
		if (m_containingPanel != null) {
			removeDragSourceComponent(m_containingPanel);
			m_containingPanel.removeMouseListener(behaviorMouseListener);
		}
	}

	@Override
	protected void guiInit() {
		super.guiInit();
		if (m_containingPanel == null) {
			m_containingPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
			m_containingPanel.setLayout(new java.awt.GridBagLayout());
			m_containingPanel.setBackground(COLOR);
			m_containingPanel.addMouseListener(behaviorMouseListener);
		}
		addDragSourceComponent(m_containingPanel);
		m_containingPanel.removeAll();
		setBackground(COLOR);
		int base = 0;
		buildLabel(m_containingPanel);
		int x = m_containingPanel.getComponentCount();
		java.awt.Component glue = javax.swing.Box.createHorizontalGlue();
		addDragSourceComponent(glue);
		m_containingPanel.add(glue, new java.awt.GridBagConstraints(x, 0, 1, 1, 1, 0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		this.add(m_containingPanel, java.awt.BorderLayout.CENTER);
		this.repaint();
		revalidate();
	}
}