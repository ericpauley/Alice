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

package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;

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

public class MainCompositeQuestionPanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel {

	public ComponentQuestionPanel returnPanel;
	protected edu.cmu.cs.stage3.alice.core.question.userdefined.Return returnQuestion;
	protected javax.swing.JPanel questionArea;

	protected class MainCompositeComponentQuestionPanel extends CompositeComponentQuestionPanel {

		@Override
		protected void updateGUI() {
			if (componentElements.size() > 1) {
				removeAll();
				resetGUI();
				for (int i = 0; i < componentElements.size(); i++) {
					edu.cmu.cs.stage3.alice.core.Element currentElement = (edu.cmu.cs.stage3.alice.core.Element) componentElements.getArrayValue()[i];
					if (currentElement != returnQuestion) {
						java.awt.Component toAdd = makeGUI(currentElement);
						if (toAdd != null) {
							addElementPanel(toAdd, i);
						}
					}
				}
			} else {
				addDropTrough();
			}
			revalidate();
			this.repaint();
		}

		@Override
		protected boolean componentsIsEmpty() {
			return componentElements.size() == 1;

		}

		@Override
		protected int getLastElementLocation() {
			return componentElements.size() - 1;
		}

		@Override
		protected boolean checkGUI() {
			java.awt.Component c[] = getComponents();
			edu.cmu.cs.stage3.alice.core.Element elements[] = (edu.cmu.cs.stage3.alice.core.Element[]) componentElements.get();
			int elementCount = getElementComponentCount();
			boolean aOkay = elements.length - 1 == elementCount; // There's a
																	// return at
																	// the end
																	// we need
																	// to ignore
			if (aOkay) {
				// Loops through the components and makes sure that component[i]
				// == componentElement[i]
				for (int i = 0; i < elements.length - 1; i++) {
					if (c[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel) {
						if (i < elements.length - 1) {
							if (((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel) c[i]).getElement() != elements[i]) {
								aOkay = false;
								break;
							}
						} else {
							aOkay = false;
							break;
						}
					}
					if (c[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel) {
						if (i < elements.length - 1) {
							if (((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel) c[i]).getElement() != elements[i]) {
								aOkay = false;
								break;
							}
						} else {
							aOkay = false;
							break;
						}
					}
				}
			}
			return aOkay;
		}

		@Override
		protected void addToElement(edu.cmu.cs.stage3.alice.core.Element toAdd, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty toAddTo, int location) {
			if (location < 0) {
				super.addToElement(toAdd, toAddTo, componentElements.size() - 1);
			} else {
				super.addToElement(toAdd, toAddTo, location);
			}
		}
	};

	public MainCompositeQuestionPanel() {
		super();
	}

	@Override
	protected java.awt.Color getCustomBackgroundColor() {
		return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestionEditor");
	}

	@Override
	protected String getHeaderHTML() {
		String htmlToReturn = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(returnQuestion.valueClass.getClassValue()) + " " + super.getHeaderHTML();
		return htmlToReturn;
	}

	public void getHTML(StringBuffer toWriteTo, int colSpan, boolean useColor) {
		super.getHTML(toWriteTo, colSpan, useColor, false);
	}

	@Override
	public void set(edu.cmu.cs.stage3.alice.core.Element question, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		if (question instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion setQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) question;
			if (setQuestion != null) {
				if (setQuestion.components.size() > 0) {
					if (setQuestion.components.get(setQuestion.components.size() - 1) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Return) {
						returnQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Return) setQuestion.components.get(setQuestion.components.size() - 1);
					} else {
						returnQuestion = new edu.cmu.cs.stage3.alice.core.question.userdefined.Return();
						returnQuestion.valueClass.set(setQuestion.valueClass.get());
						returnQuestion.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass((Class) setQuestion.valueClass.get()));
						returnQuestion.setParent(setQuestion);

						setQuestion.components.add(setQuestion.components.size(), returnQuestion);
					}
				} else {
					returnQuestion = new edu.cmu.cs.stage3.alice.core.question.userdefined.Return();
					returnQuestion.valueClass.set(setQuestion.valueClass.get());
					returnQuestion.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass((Class) setQuestion.valueClass.get()));
					returnQuestion.setParent(setQuestion);
					setQuestion.components.add(0, returnQuestion);
				}
				returnPanel.set(returnQuestion);
				disableDrag(returnPanel);
			}
			super.set(question, authoringTool);
		} else {
			throw new java.lang.IllegalArgumentException();
		}
	}

	@Override
	protected void generateGUI() {
		super.generateGUI();
		returnPanel = new ComponentQuestionPanel();
		returnPanel.setDragEnabled(false);
		if (questionArea == null) {
			questionArea = new javax.swing.JPanel();
			questionArea.setOpaque(true);
			questionArea.setBorder(null);
			questionArea.setLayout(new java.awt.GridBagLayout());
			questionArea.setDropTarget(new java.awt.dnd.DropTarget(questionArea, componentElementPanel));
			questionArea.setBackground(getBackground());
		}
	}

	@Override
	protected void updateGUI() {
		removeAll();
		buildParameterPanel();
		buildVariablePanel();
		headerPanel.add(mainParameterPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		headerPanel.add(mainVariablePanel, new java.awt.GridBagConstraints(0, 1, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		questionArea.removeAll();
		questionArea.add(componentElementPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		questionArea.add(returnPanel, new java.awt.GridBagConstraints(0, 1, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel.LEFT_INDENT + 1, 0, edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel.RIGHT_INDENT + 1), 0, 0));
		questionArea.add(javax.swing.Box.createVerticalGlue(), new java.awt.GridBagConstraints(0, 2, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		scrollPane.setViewportView(questionArea);
		this.add(scrollPane, java.awt.BorderLayout.CENTER);
		this.add(headerPanel, java.awt.BorderLayout.NORTH);
		if (CompositeElementEditor.IS_JAVA) {
			closeBrace.setText(" }");
			this.add(closeBrace, java.awt.BorderLayout.SOUTH);
		}
		setBackground(getCustomBackgroundColor());
		questionArea.setBackground(getBackground());
		this.repaint();
		revalidate();
	}

	@Override
	protected void variableInit() {
		super.variableInit();
		if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) m_element;
			m_components = proxy.components;
			m_isCommentedOut = null;
			componentElementPanel = new MainCompositeComponentQuestionPanel();
			componentElementPanel.set(m_components, this, authoringTool);
			componentElementPanel.setBackground(backgroundColor);
			addDragSourceComponent(componentElementPanel);
			componentElementPanel.addMouseListener(elementMouseListener);
		}
	}

	protected void disableDrag(java.awt.Container c) {
		if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c).setDragEnabled(false);
		}
		for (int i = 0; i < c.getComponentCount(); i++) {
			if (c.getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
				((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c.getComponent(i)).setDragEnabled(false);
			} else if (c instanceof java.awt.Container) {
				disableDrag(c);
			}
		}
	}
}