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

package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author David Culyba
 * @version 1.0
 */

public abstract class IfElseElementPanel extends CompositeElementPanel {

	protected javax.swing.JPanel ifElsePanel;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty m_elseComponents;
	protected CompositeComponentElementPanel elseComponentPanel;
	protected javax.swing.JPanel elsePanel;
	protected javax.swing.JLabel elseEndBrace = new javax.swing.JLabel("}");
	protected javax.swing.JLabel elseLabel;
	protected javax.swing.JLabel endHeader;
	protected javax.swing.JComponent conditionalInput;
	protected edu.cmu.cs.stage3.alice.core.property.BooleanProperty m_condition;
	protected IfElseDropTargetHandler ifElseDropHandler = new IfElseDropTargetHandler();
	protected java.awt.Component elseGlue;

	public IfElseElementPanel() {
		super();
		backgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("IfElseInOrder");
		headerText = "If";
	}

	public java.awt.Component getIfDoNothingPanel() {
		java.awt.Component doNothing = componentElementPanel.getDropPanel();
		if (doNothing.getParent() == componentElementPanel) {
			return doNothing;
		} else {
			return null;
		}
	}

	public java.awt.Component getElseDoNothingPanel() {
		java.awt.Component doNothing = elseComponentPanel.getDropPanel();
		if (doNothing.getParent() == elseComponentPanel) {
			return doNothing;
		} else {
			return null;
		}
	}

	@Override
	protected void setDropTargets() {
		headerLabel.setDropTarget(new java.awt.dnd.DropTarget(headerLabel, componentElementPanel));
		setDropTarget(new java.awt.dnd.DropTarget(this, ifElseDropHandler));
		containingPanel.setDropTarget(new java.awt.dnd.DropTarget(containingPanel, ifElseDropHandler));
		headerPanel.setDropTarget(new java.awt.dnd.DropTarget(headerPanel, componentElementPanel));
		grip.setDropTarget(new java.awt.dnd.DropTarget(grip, ifElseDropHandler));
		glue.setDropTarget(new java.awt.dnd.DropTarget(glue, ifElseDropHandler));
		expandButton.setDropTarget(new java.awt.dnd.DropTarget(expandButton, componentElementPanel));
		elsePanel.setDropTarget(new java.awt.dnd.DropTarget(elsePanel, elseComponentPanel));
		elseLabel.setDropTarget(new java.awt.dnd.DropTarget(elseLabel, elseComponentPanel));
		endHeader.setDropTarget(new java.awt.dnd.DropTarget(endHeader, elseComponentPanel));
		elseGlue.setDropTarget(new java.awt.dnd.DropTarget(elseGlue, elseComponentPanel));
		ifElsePanel.setDropTarget(new java.awt.dnd.DropTarget(ifElsePanel, ifElseDropHandler));
		elseEndBrace.setDropTarget(new java.awt.dnd.DropTarget(elseEndBrace, ifElseDropHandler));
	}

	@Override
	protected void variableInit() {
		super.variableInit();
		if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
			edu.cmu.cs.stage3.alice.core.response.IfElseInOrder proxy = (edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) m_element;
			m_condition = proxy.condition;
			m_elseComponents = proxy.elseComponentResponses;
			elseComponentPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CompositeComponentResponsePanel();
			elseComponentPanel.set(m_elseComponents, this, authoringTool);
			elseComponentPanel.setBackground(backgroundColor);
			addDragSourceComponent(elseComponentPanel);
			elseComponentPanel.addMouseListener(elementMouseListener);
		} else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) m_element;
			m_condition = proxy.condition;
			m_elseComponents = proxy.elseComponents;
			elseComponentPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.CompositeComponentQuestionPanel();
			elseComponentPanel.set(m_elseComponents, this, authoringTool);
			elseComponentPanel.setBackground(backgroundColor);
			addDragSourceComponent(elseComponentPanel);
			elseComponentPanel.addMouseListener(elementMouseListener);
		}
	}

	@Override
	public void release() {
		super.release();
		if (elseComponentPanel != null) {
			elseComponentPanel.release();
		}
	}

	@Override
	protected void startListening() {
		super.startListening();
		if (m_condition != null) {
			m_condition.addPropertyListener(this);
		}
	}

	@Override
	protected void stopListening() {
		super.stopListening();
		if (m_condition != null) {
			m_condition.removePropertyListener(this);
		}
	}

	@Override
	public void goToSleep() {
		super.goToSleep();
		if (elseComponentPanel != null) {
			elseComponentPanel.goToSleep();
		}
	}

	@Override
	public void wakeUp() {
		super.wakeUp();
		if (elseComponentPanel != null) {
			elseComponentPanel.wakeUp();
		}
	}

	@Override
	public void clean() {
		super.clean();
		if (elseComponentPanel != null) {
			containingPanel.remove(elseComponentPanel);
			elseComponentPanel.release();
			elseComponentPanel = null;
		}
	}

	@Override
	protected void removeAllListening() {
		super.removeAllListening();
		removeDragSourceComponent(elseComponentPanel);
		elseComponentPanel.removeMouseListener(elementMouseListener);
		removeDragSourceComponent(ifElsePanel);
		removeDragSourceComponent(elsePanel);
	}

	@Override
	public void setHeaderLabel() {
		if (headerLabel != null) {
			headerLabel.setText(headerText);
			if (CompositeElementEditor.IS_JAVA) {
				headerLabel.setText("if (");
			}
		}
		if (elseLabel != null) {
			elseLabel.setText("Else");
			if (CompositeElementEditor.IS_JAVA) {
				elseLabel.setText("} else {");
			}
		}
		if (endHeader != null) {
			endHeader.setText("");
			if (CompositeElementEditor.IS_JAVA) {
				if (!isExpanded) {
					endHeader.setText(") { " + getDots() + " }");
				} else {
					endHeader.setText(") {");
				}
			}
		}

	}

	@Override
	public String getDots() {
		String dots = "";
		int elses = m_elseComponents.size();
		for (int i = 0; i < m_components.size() + elses; i++) {
			if (i == 0) {
				dots += ".";
			} else {
				dots += " .";
			}
		}
		return dots;
	}

	@Override
	protected void generateGUI() {
		super.generateGUI();
		if (elseGlue == null) {
			elseGlue = javax.swing.Box.createHorizontalGlue();
		}
		if (endHeader == null) {
			endHeader = new javax.swing.JLabel();
		}
		if (elseLabel == null) {
			elseLabel = new javax.swing.JLabel();
			java.awt.Font elseFont = elseLabel.getFont();
		}
		if (ifElsePanel == null) {
			ifElsePanel = new javax.swing.JPanel();
			ifElsePanel.setBorder(null);
			ifElsePanel.setLayout(new java.awt.GridBagLayout());
			ifElsePanel.setOpaque(false);
		}
		if (elsePanel == null) {
			elsePanel = new javax.swing.JPanel();
			elsePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, CompositeComponentElementPanel.LEFT_INDENT - 2, 0, 0));
			elsePanel.setLayout(new java.awt.GridBagLayout());
			elsePanel.add(elseLabel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			elsePanel.add(elseGlue, new java.awt.GridBagConstraints(1, 0, 1, 1, 1, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			elsePanel.setOpaque(false);
		}
	}

	@Override
	protected void restoreDrag() {
		super.restoreDrag();
		addDragSourceComponent(endHeader);
		addDragSourceComponent(elseLabel);
		addDragSourceComponent(ifElsePanel);
		addDragSourceComponent(elsePanel);
		addDragSourceComponent(elseGlue);
	}

	@Override
	protected void updateGUI() {
		setHeaderLabel();
		containingPanel.removeAll();
		headerPanel.removeAll();
		restoreDrag();
		if (isExpanded) {
			expandButton.setIcon(minus);
		} else {
			expandButton.setIcon(plus);
		}
		headerPanel.add(expandButton, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		headerPanel.add(headerLabel, new java.awt.GridBagConstraints(1, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory pif = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_condition);
		conditionalInput = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(m_condition, true, true, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitPropertyName(m_condition), pif);
		headerPanel.add(conditionalInput, new java.awt.GridBagConstraints(3, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));
		headerPanel.add(endHeader, new java.awt.GridBagConstraints(4, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 2, 0, 2), 0, 0));
		headerPanel.add(glue, new java.awt.GridBagConstraints(5, 0, 1, 1, 1, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		ifElsePanel.removeAll();
		ifElsePanel.add(componentElementPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		ifElsePanel.add(elsePanel, new java.awt.GridBagConstraints(0, 1, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		ifElsePanel.add(elseComponentPanel, new java.awt.GridBagConstraints(0, 2, 1, 1, 1, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		if (isExpanded) {
			containingPanel.add(ifElsePanel, java.awt.BorderLayout.CENTER);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.add(closeBrace, java.awt.BorderLayout.SOUTH);
			}
		}
		containingPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
		this.add(containingPanel, java.awt.BorderLayout.CENTER);
		setOpaque(false);
		setBackground(backgroundColor);
	}

	@Override
	public void expandComponentElementPanel() {
		if (!isExpanded) {
			m_element.data.put(IS_EXPANDED_KEY, Boolean.TRUE);
			isExpanded = true;
			setHeaderLabel();
			expandButton.setIcon(minus);
			containingPanel.add(ifElsePanel, java.awt.BorderLayout.CENTER);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.add(closeBrace, java.awt.BorderLayout.SOUTH);
			}
			revalidate();
			// this.repaint();
		}
	}

	@Override
	public void reduceComponentElementPanel() {
		if (isExpanded) {
			m_element.data.put(IS_EXPANDED_KEY, Boolean.FALSE);
			isExpanded = false;
			setHeaderLabel();
			expandButton.setIcon(plus);
			containingPanel.remove(ifElsePanel);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.remove(closeBrace);
			}
			revalidate();
			// this.repaint();
		}
	}

	protected class IfElseDropTargetHandler implements java.awt.dnd.DropTargetListener {

		protected boolean onIf = true;

		protected boolean isInIf(java.awt.dnd.DropTargetDragEvent dtde) {
			java.awt.Point panelSpacePoint = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), IfElseElementPanel.this);
			java.awt.Point elsePoint = javax.swing.SwingUtilities.convertPoint(elsePanel, 0, 0, IfElseElementPanel.this);
			if (panelSpacePoint.y < elsePoint.y) {
				onIf = true;
			} else {
				onIf = false;
			}
			return onIf;
		}

		@Override
		public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
			if (isInIf(dtde)) {
				componentElementPanel.dragEnter(dtde);
			} else {
				elseComponentPanel.dragEnter(dtde);
			}
		}

		@Override
		public void dragExit(java.awt.dnd.DropTargetEvent dtde) {
			if (onIf) {
				componentElementPanel.dragExit(dtde);
			} else {
				elseComponentPanel.dragExit(dtde);
			}
		}

		@Override
		public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
			if (isInIf(dtde)) {
				componentElementPanel.dragOver(dtde);
			} else {
				elseComponentPanel.dragOver(dtde);
			}
		}

		@Override
		public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
			if (onIf) {
				componentElementPanel.drop(dtde);
			} else {
				elseComponentPanel.drop(dtde);
			}
		}

		@Override
		public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
			if (isInIf(dtde)) {
				componentElementPanel.dropActionChanged(dtde);
			} else {
				elseComponentPanel.dropActionChanged(dtde);
			}
		}
	}

	// Texture stuff

	protected static java.awt.image.BufferedImage conditionalBackgroundImage;
	protected static java.awt.Dimension conditionalBackgroundImageSize = new java.awt.Dimension(-1, -1);

	protected void createBackgroundImage(int width, int height) {
		conditionalBackgroundImageSize.setSize(width, height);
		conditionalBackgroundImage = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = (java.awt.Graphics2D) conditionalBackgroundImage.getGraphics();
		g.addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
	}

	protected void paintTextureEffect(java.awt.Graphics g, java.awt.Rectangle bounds) {
		if (bounds.width > conditionalBackgroundImageSize.width || bounds.height > conditionalBackgroundImageSize.height) {
			createBackgroundImage(bounds.width, bounds.height);
		}
		g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
		g.drawImage(conditionalBackgroundImage, bounds.x, bounds.y, this);
	}
}