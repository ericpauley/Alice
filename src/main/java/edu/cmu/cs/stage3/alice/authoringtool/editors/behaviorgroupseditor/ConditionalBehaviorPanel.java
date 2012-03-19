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

public class ConditionalBehaviorPanel extends BasicBehaviorPanel {

	protected javax.swing.JComponent beginPanel;
	protected javax.swing.JComponent duringPanel;
	protected javax.swing.JComponent endPanel;

	protected static final int INDENT = 15;

	public ConditionalBehaviorPanel() {
		super();
	}
	public void set(edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior behavior, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		super.set(behavior, authoringTool);
	}

	@Override
	protected void removeAllListening() {
		super.removeAllListening();
		if (m_containingPanel != null) {
			removeDragSourceComponent(m_containingPanel);
			m_containingPanel.removeMouseListener(behaviorMouseListener);
		}
		if (labelPanel != null) {
			removeDragSourceComponent(labelPanel);
			labelPanel.removeMouseListener(behaviorMouseListener);
		}
		if (beginPanel != null) {
			removeDragSourceComponent(beginPanel);
			beginPanel.removeMouseListener(behaviorMouseListener);
		}
		if (duringPanel != null) {
			removeDragSourceComponent(duringPanel);
			duringPanel.removeMouseListener(behaviorMouseListener);
		}
		if (endPanel != null) {
			removeDragSourceComponent(endPanel);
			endPanel.removeMouseListener(behaviorMouseListener);
		}
	}

	@Override
	public void getHTML(StringBuffer toWriteTo, boolean useColor) {
		java.awt.Color bgColor = COLOR;
		String strikeStart = "";
		String strikeEnd = "";
		if (!m_behavior.isEnabled.booleanValue()) {
			bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML");
			strikeStart = "<strike><font color=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
			strikeEnd = "</font></strike>";
		}
		toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " colspan=\"2\" >" + strikeStart);
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(labelPanel));
		toWriteTo.append(strikeEnd + "</td>\n</tr>\n");
		toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + "<b>Begin:</b>" + strikeEnd + "</td>\n");
		toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(beginPanel));
		toWriteTo.append("</table>\n</td>\n</tr>\n");

		toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + "<b>During:</b>" + strikeEnd + "</td>\n");
		toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(duringPanel));
		toWriteTo.append("</table>\n</td>\n</tr>\n");

		toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + "<b>End:</b>" + strikeEnd + "</td>\n");
		toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(endPanel));
		toWriteTo.append("</table>\n</td>\n</tr>\n");

	}

	@Override
	protected void guiInit() {
		super.guiInit();
		setBackground(COLOR);
		if (m_containingPanel == null) {
			m_containingPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
			m_containingPanel.setBorder(null);
			m_containingPanel.setLayout(new java.awt.GridBagLayout());
			m_containingPanel.setBackground(COLOR);
			m_containingPanel.addMouseListener(behaviorMouseListener);
		}
		this.remove(m_containingPanel);
		addDragSourceComponent(m_containingPanel);
		m_containingPanel.removeAll();
		if (labelPanel == null) {
			labelPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
			labelPanel.setBorder(null);
			labelPanel.setLayout(new java.awt.GridBagLayout());
			labelPanel.setBackground(COLOR);
			labelPanel.addMouseListener(behaviorMouseListener);
		}
		addDragSourceComponent(labelPanel);
		labelPanel.removeAll();
		buildLabel(labelPanel);
		m_containingPanel.add(labelPanel, new java.awt.GridBagConstraints(0, 0, 2, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));

		m_containingPanel.add(new javax.swing.JLabel("Begin:"), new java.awt.GridBagConstraints(0, 1, 1, 1, 0, 0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, INDENT, 0, 2), 0, 0));

		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory beginFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).beginResponse);
		beginPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).beginResponse, false, true, true, beginFactory);
		m_containingPanel.add(beginPanel, new java.awt.GridBagConstraints(1, 1, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));

		m_containingPanel.add(new javax.swing.JLabel("During:"), new java.awt.GridBagConstraints(0, 2, 1, 1, 0, 0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, INDENT, 0, 2), 0, 0));
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory duringFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).duringResponse);
		duringPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).duringResponse, false, true, true, duringFactory);
		m_containingPanel.add(duringPanel, new java.awt.GridBagConstraints(1, 2, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));

		m_containingPanel.add(new javax.swing.JLabel("End:"), new java.awt.GridBagConstraints(0, 3, 1, 1, 0, 0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, INDENT, 0, 2), 0, 0));
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory endFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).endResponse);
		endPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior) m_behavior).endResponse, false, true, true, endFactory);

		m_containingPanel.add(endPanel, new java.awt.GridBagConstraints(1, 3, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));
		this.add(m_containingPanel, java.awt.BorderLayout.CENTER);
		this.repaint();
		revalidate();
	}

}