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

import javax.swing.ScrollPaneConstants;

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

public class MainCompositeElementPanel extends CompositeElementPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {

	protected java.awt.Color backgroundColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponseEditor");
	protected javax.swing.JPanel parameterPanel;
	protected javax.swing.JPanel variablePanel;

	protected javax.swing.JPanel mainParameterPanel;
	protected javax.swing.JPanel mainVariablePanel;

	protected javax.swing.JButton newParameterButton;
	protected javax.swing.JButton newVariableButton;
	protected javax.swing.JScrollPane scrollPane;
	protected DropTargetHandler parameterDropHandler;
	protected DropTargetHandler variableDropHandler;

	protected javax.swing.JLabel methodNameLabel;
	protected javax.swing.JLabel noParametersLabel;
	protected javax.swing.JLabel noVariablesLabel;

	private edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty requiredParameters;
	private edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty keywordParameters;
	private edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty localVariables;
	private int lineLocation = -1;
	private int lineHeight = 24;
	private int verticalLineLocation = 5;
	private boolean paintParameter = false;
	private boolean paintVariable = false;

	public MainCompositeElementPanel() {
		super();
		setOpaque(false);
		setLayout(new java.awt.BorderLayout());
		setDragEnabled(false);
		setBorder(null);
		elementMouseListener = null;

	}

	@Override
	public void set(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		super.set(element, authoringTool);
	}

	@Override
	protected void setDropTargets() {
	}

	@Override
	protected void variableInit() {
		super.variableInit();
		setVariableObjects(m_element);
	}

	@Override
	protected void startListening() {
		super.startListening();
		if (keywordParameters != null) {
			keywordParameters.addObjectArrayPropertyListener(this);
		}
		if (requiredParameters != null) {
			requiredParameters.addObjectArrayPropertyListener(this);
		}
		if (localVariables != null) {
			localVariables.addObjectArrayPropertyListener(this);
		}
	}

	@Override
	protected void stopListening() {
		super.stopListening();
		if (keywordParameters != null) {
			keywordParameters.removeObjectArrayPropertyListener(this);
		}
		if (requiredParameters != null) {
			requiredParameters.removeObjectArrayPropertyListener(this);
		}
		if (localVariables != null) {
			localVariables.removeObjectArrayPropertyListener(this);
		}
	}

	@Override
	protected void removeAllListening() {
		super.removeAllListening();
		variablePanel.setDropTarget(null);
		parameterPanel.setDropTarget(null);
		parameterDropHandler = null;
		variableDropHandler = null;
	}

	protected boolean isValidName(String name, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group) {
		for (int i = 0; i < group.size(); i++) {
			if (name == ((edu.cmu.cs.stage3.alice.core.Element) group.get(i)).name.getStringValue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void objectArrayPropertyChanging(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e) {
	}

	@Override
	public void objectArrayPropertyChanged(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e) {
		if (parameterPanel != null) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buildParameterPanel();
				}
			});

		}
		if (variablePanel != null) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buildVariablePanel();
				}
			});
		}
	}

	@Override
	protected String getHeaderHTML() {
		String htmlToReturn = "<b>" + methodNameLabel.getText() + "</b>&nbsp;(&nbsp;";
		for (int i = 0; i < requiredParameters.size(); i++) {
			Class iconClass = ((edu.cmu.cs.stage3.alice.core.Variable) requiredParameters.get(i)).valueClass.getClassValue();

			boolean isList = false;
			if (edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom(iconClass)) {
				iconClass = (Class) ((edu.cmu.cs.stage3.alice.core.Collection) ((edu.cmu.cs.stage3.alice.core.Variable) requiredParameters.get(i)).getValue()).valueClass.get();
				isList = true;
			}
			String htmlName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getHTMLName(iconClass.getName());
			if (isList) {
				htmlName += edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getHTMLName("edu.cmu.cs.stage3.alice.core.Collection");
			}
			htmlToReturn += htmlName + " <b>" + ((edu.cmu.cs.stage3.alice.core.Element) requiredParameters.get(i)).name.getStringValue() + "</b>";
			if (i + 1 != requiredParameters.size()) {
				htmlToReturn += ", ";
			}
		}
		if (CompositeElementEditor.IS_JAVA) {
			htmlToReturn += ") <b>{</b>\n<br>&nbsp;&nbsp;&nbsp;&nbsp;";
		} else {
			htmlToReturn += ")\n<br>&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		htmlToReturn += edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(mainVariablePanel);
		return htmlToReturn;
	}

	private void setVariableObjects(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
			edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse r = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) element;
			keywordParameters = r.keywordFormalParameters;
			requiredParameters = r.requiredFormalParameters;
			localVariables = r.localVariables;
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion r = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) element;
			keywordParameters = r.keywordFormalParameters;
			requiredParameters = r.requiredFormalParameters;
			localVariables = r.localVariables;
		}
		if (keywordParameters != null && requiredParameters != null && localVariables != null) {
			keywordParameters.addObjectArrayPropertyListener(this);
			requiredParameters.addObjectArrayPropertyListener(this);
			localVariables.addObjectArrayPropertyListener(this);
			parameterDropHandler.setProperty(requiredParameters);
			variableDropHandler.setProperty(localVariables);
		}
	}

	protected int buildVariablePanel(String seperator, javax.swing.JPanel toCreate, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group, javax.swing.JButton button, DropTargetHandler dropHandler, int count) {
		int itemCount = 0;
		if (group != null == group.size() > 0) {
			// toCreate.add(new javax.swing.JLabel(label), new
			// java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.CENTER,
			// java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,2),
			// 0,0));
			// int row = 0;
			for (int i = 0; i < group.size(); i++) {
				if (group.get(i) instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Variable currentVariable = (edu.cmu.cs.stage3.alice.core.Variable) group.get(i);
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory variablePIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(currentVariable.value);
					javax.swing.JComponent variableGUI = null;
					// //TODO: fix this
					// System.out.println(currentVariable.hashCode());
					// if (currentVariable.getValue() instanceof
					// edu.cmu.cs.stage3.alice.core.List){
					// System.out.println(currentVariable+", "+((edu.cmu.cs.stage3.alice.core.List)currentVariable.getValue()).valueClass.getClassValue());
					// }
					// else{
					// System.out.println(currentVariable.getValue());
					// }
					if (toCreate != parameterPanel) {
						variableGUI = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableGUI(currentVariable, true, variablePIF);
					} else {
						variableGUI = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel(currentVariable);
					}
					itemCount++;
					variableGUI.setDropTarget(new java.awt.dnd.DropTarget(variableGUI, dropHandler));
					if (variableGUI instanceof java.awt.Container) {
						java.awt.Container variableContainer = variableGUI;
						for (int j = 0; j < variableContainer.getComponentCount(); j++) {
							variableContainer.getComponent(j).setDropTarget(new java.awt.dnd.DropTarget(variableContainer.getComponent(j), dropHandler));
							if (variableContainer.getComponent(j) instanceof java.awt.Container) {
								java.awt.Container secondaryContainer = (java.awt.Container) variableContainer.getComponent(j);
								for (int k = 0; k < secondaryContainer.getComponentCount(); k++) {
									secondaryContainer.getComponent(k).setDropTarget(new java.awt.dnd.DropTarget(secondaryContainer.getComponent(k), dropHandler));
								}
							}
						}
					}
					if (itemCount != group.size()) {
						javax.swing.JPanel holder = new javax.swing.JPanel();
						holder.setBorder(null);
						holder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
						holder.setBackground(toCreate.getBackground());
						holder.setDropTarget(new java.awt.dnd.DropTarget(holder, dropHandler));
						javax.swing.JLabel comma = new javax.swing.JLabel(" " + seperator);
						comma.setDropTarget(new java.awt.dnd.DropTarget(comma, dropHandler));
						holder.add(variableGUI);
						holder.add(comma);
						toCreate.add(holder);
						holder.setDropTarget(new java.awt.dnd.DropTarget(holder, dropHandler));
					} else {
						toCreate.add(variableGUI);
					}
				}
			}
		} else {
			java.awt.Component nonePanel = noParametersLabel;
			if (group == localVariables) {
				nonePanel = noVariablesLabel;
			}
			nonePanel.setDropTarget(new java.awt.dnd.DropTarget(nonePanel, dropHandler));
			toCreate.add(nonePanel);
		}
		return itemCount;
	}

	protected int buildJavaVariablePanel(String seperator, javax.swing.JPanel toCreate, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group, javax.swing.JButton button, DropTargetHandler dropHandler, int count) {
		int itemCount = 0;
		if (group != null) {
			// int row = 0;
			for (int i = 0; i < group.size(); i++) {
				if (group.get(i) instanceof edu.cmu.cs.stage3.alice.core.Variable) {
					final edu.cmu.cs.stage3.alice.core.Variable currentVariable = (edu.cmu.cs.stage3.alice.core.Variable) group.get(i);
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory variablePIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(currentVariable.value);
					String className = currentVariable.getValueClass().getName();
					boolean isList = false;
					if (edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom(currentVariable.getValueClass())) {
						className = ((Class) ((edu.cmu.cs.stage3.alice.core.Collection) currentVariable.getValue()).valueClass.get()).getName();
						isList = true;
					}
					String typeName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getHTMLName(className);
					if (typeName == null) {
						typeName = currentVariable.getValueClass().getName();
						typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
					}
					if (isList) {
						typeName += "[]";
					}
					javax.swing.JLabel typeLabel = new javax.swing.JLabel(typeName + " ");
					javax.swing.JComponent variableGUI = null;

					if (toCreate != parameterPanel) {
						variableGUI = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableGUI(currentVariable, true, variablePIF);
					} else {
						variableGUI = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableDnDPanel(currentVariable);
					}
					itemCount++;
					variableGUI.setDropTarget(new java.awt.dnd.DropTarget(variableGUI, dropHandler));
					if (variableGUI instanceof java.awt.Container) {
						java.awt.Container variableContainer = variableGUI;
						for (int j = 0; j < variableContainer.getComponentCount(); j++) {
							variableContainer.getComponent(j).setDropTarget(new java.awt.dnd.DropTarget(variableContainer.getComponent(j), dropHandler));
							if (variableContainer.getComponent(j) instanceof java.awt.Container) {
								java.awt.Container secondaryContainer = (java.awt.Container) variableContainer.getComponent(j);
								for (int k = 0; k < secondaryContainer.getComponentCount(); k++) {
									secondaryContainer.getComponent(k).setDropTarget(new java.awt.dnd.DropTarget(secondaryContainer.getComponent(k), dropHandler));
								}
							}
						}
					}
					if (itemCount != group.size()) {
						javax.swing.JPanel holder = new javax.swing.JPanel();
						holder.setBorder(null);
						holder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
						holder.setBackground(toCreate.getBackground());
						holder.setDropTarget(new java.awt.dnd.DropTarget(holder, dropHandler));
						javax.swing.JLabel comma = new javax.swing.JLabel(" " + seperator + " ");
						comma.setDropTarget(new java.awt.dnd.DropTarget(comma, dropHandler));
						typeLabel.setDropTarget(new java.awt.dnd.DropTarget(typeLabel, dropHandler));
						holder.add(typeLabel);
						holder.add(variableGUI);
						holder.add(comma);
						toCreate.add(holder);
					} else {
						javax.swing.JPanel holder = new javax.swing.JPanel();
						holder.setBorder(null);
						holder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
						holder.setBackground(toCreate.getBackground());
						holder.setDropTarget(new java.awt.dnd.DropTarget(holder, dropHandler));
						typeLabel.setDropTarget(new java.awt.dnd.DropTarget(typeLabel, dropHandler));
						holder.add(typeLabel);
						holder.add(variableGUI);
						toCreate.add(holder);
					}
				}
			}
		}
		return itemCount;
	}

	@Override
	public void setBackground(java.awt.Color color) {
		super.setBackground(color);
		if (parameterPanel != null) {
			parameterPanel.setBackground(color);
		}
		if (variablePanel != null) {
			variablePanel.setBackground(color);
		}
		if (mainVariablePanel != null) {
			mainVariablePanel.setBackground(color);
		}
		if (mainParameterPanel != null) {
			mainParameterPanel.setBackground(color);
		}
		if (componentElementPanel != null) {
			componentElementPanel.setBackground(color);
		}
	}

	private void clearReferences(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty toClear) {
		for (int i = 0; i < toClear.size(); i++) {
			if (toClear.get(i) instanceof edu.cmu.cs.stage3.alice.core.Variable) {
				edu.cmu.cs.stage3.alice.core.Variable variableToClear = (edu.cmu.cs.stage3.alice.core.Variable) toClear.get(i);
				if (!(variableToClear.value.get() instanceof edu.cmu.cs.stage3.alice.core.Collection)) {
					variableToClear.value.set(null);
				}
			}
		}
	}

	protected void buildParameterPanel() {
		parameterPanel.removeAll();

		String functionName = m_element.name.getStringValue();
		// clearReferences(requiredParameters);

		if (CompositeElementEditor.IS_JAVA) {
			functionName = functionName.replace(' ', '_');
			String typeName = "void";
			if (this instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) m_element;
				String className = currentQuestion.getValueClass().getName();
				boolean isList = false;
				if (edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom(currentQuestion.getValueClass())) {
					className = ((Class) ((edu.cmu.cs.stage3.alice.core.Collection) currentQuestion.getValue()).valueClass.get()).getName();
					isList = true;
				}
				typeName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getHTMLName(className);
				if (typeName == null) {
					typeName = currentQuestion.getValueClass().getName();
					typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
				}
				if (isList) {
					typeName += "[]";
				}
			}
			methodNameLabel.setText("public " + typeName + " " + functionName);
			parameterPanel.add(methodNameLabel);
			parameterPanel.add(new javax.swing.JLabel("("));
			// nameLabel.setDropTarget(new java.awt.dnd.DropTarget(nameLabel,
			// parameterDropHandler));
			buildJavaVariablePanel(",", parameterPanel, requiredParameters, newParameterButton, parameterDropHandler, 3);
			javax.swing.JLabel brace = new javax.swing.JLabel("{");
			brace.setDropTarget(new java.awt.dnd.DropTarget(brace, parameterDropHandler));
			javax.swing.JLabel paren = new javax.swing.JLabel(")");
			paren.setDropTarget(new java.awt.dnd.DropTarget(paren, parameterDropHandler));
			parameterPanel.add(paren);
			// parameterPanel.add(newParameterButton);
			parameterPanel.add(brace);
		} else {
			methodNameLabel.setText(m_element.getTrimmedKey());
			parameterPanel.add(methodNameLabel);
			buildVariablePanel(",", parameterPanel, requiredParameters, newParameterButton, parameterDropHandler, 3);
			// parameterPanel.add(newParameterButton);
		}
		newParameterButton.setDropTarget(new java.awt.dnd.DropTarget(newParameterButton, parameterDropHandler));
		// java.awt.Component c = javax.swing.Box.createHorizontalGlue();
		// c.setDropTarget(new java.awt.dnd.DropTarget(c,
		// parameterDropHandler));
		// parameterPanel.add(c);
		parameterPanel.validate();
		parameterPanel.repaint();
	}

	protected void buildVariablePanel() {
		variablePanel.removeAll();
		if (CompositeElementEditor.IS_JAVA) {
			int count = buildJavaVariablePanel(";", variablePanel, localVariables, newVariableButton, variableDropHandler, 1);
			if (count > 0) {
				javax.swing.JLabel semi = new javax.swing.JLabel(";");
				semi.setDropTarget(new java.awt.dnd.DropTarget(semi, parameterDropHandler));
				variablePanel.add(semi);
			}
		} else {
			buildVariablePanel(",", variablePanel, localVariables, newVariableButton, variableDropHandler, 1);
		}
		// variablePanel.add(newVariableButton);
		newVariableButton.setDropTarget(new java.awt.dnd.DropTarget(newVariableButton, variableDropHandler));
		// java.awt.Component c = javax.swing.Box.createHorizontalGlue();
		// c.setDropTarget(new java.awt.dnd.DropTarget(c, variableDropHandler));

		variablePanel.revalidate();
		variablePanel.repaint();
	}

	private java.awt.Component getAnchor(java.awt.Component current) {
		if (current == null || current instanceof javax.swing.JTabbedPane) {
			return current;
		} else {
			return getAnchor(current.getParent());
		}
	}

	@Override
	protected void generateGUI() {
		java.awt.Component anchor = getAnchor(this);
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		int buttonWidth = fontSize * 13;
		int buttonHeight = fontSize * 2 + 2;

		if (newParameterButton == null) {
			newParameterButton = new javax.swing.JButton("create new parameter");
			newParameterButton.setBackground(new java.awt.Color(240, 240, 255));
			newParameterButton.setOpaque(true);
			newParameterButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// if (fontSize == 12){
			newParameterButton.setMinimumSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			newParameterButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			newParameterButton.setMaximumSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			// }
			/*
			 * newParameterButton.setBorder(javax.swing.BorderFactory.
			 * createCompoundBorder(
			 * javax.swing.BorderFactory.createLineBorder(java
			 * .awt.Color.lightGray),
			 * javax.swing.BorderFactory.createEmptyBorder(2,2,2,2) ));
			 */
			newParameterButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool.showNewVariableDialog("Create New Parameter", requiredParameters.getOwner(), false, false);
					if (variable != null) {
						if (requiredParameters != null) {
							authoringTool.getUndoRedoStack().startCompound();
							try {
								// variable.value.set( null ); //How dow we deal
								// with parameters? They need a value to work as
								// drops, but it shows up in the behavior
								requiredParameters.getOwner().addChild(variable);
								requiredParameters.add(variable);
							} finally {
								authoringTool.getUndoRedoStack().stopCompound();
							}
						}
					}
				}
			});
			newParameterButton.setDropTarget(new java.awt.dnd.DropTarget(newParameterButton, parameterDropHandler));
			newParameterButton.setToolTipText("<html><body>" + "<p>Open the New Parameter Dialogue Box</p>" + "<p>Parameters allow you to send information</p>" + "<p>to a method when you run it. You may choose</p>" + "<p>among several types of information to send</p>" + "<p>(like numbers, objects, and Booleans).</p>" + "</body></html>");
		}
		if (newVariableButton == null) {
			newVariableButton = new javax.swing.JButton("create new variable");
			newVariableButton.setBackground(new java.awt.Color(240, 240, 255));
			newVariableButton.setOpaque(true);
			newVariableButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// if (fontSize == 12){
			newVariableButton.setMinimumSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			newVariableButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			newVariableButton.setMaximumSize(new java.awt.Dimension(buttonWidth, buttonHeight));
			// }

			/*
			 * newVariableButton.setBorder(javax.swing.BorderFactory.
			 * createCompoundBorder(
			 * javax.swing.BorderFactory.createLineBorder(java
			 * .awt.Color.lightGray),
			 * javax.swing.BorderFactory.createEmptyBorder(2,2,2,2) ));
			 */
			newVariableButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool.showNewVariableDialog("Create New Local Variable", localVariables.getOwner(), false, true);
					if (variable != null) {
						if (localVariables != null) {
							authoringTool.getUndoRedoStack().startCompound();
							try {
								localVariables.getOwner().addChild(variable);
								localVariables.add(variable);
							} finally {
								authoringTool.getUndoRedoStack().stopCompound();
							}
						}
					}
				}
			});
			newVariableButton.setDropTarget(new java.awt.dnd.DropTarget(newVariableButton, variableDropHandler));
			newVariableButton.setToolTipText("<html><body>" + "<p>Open the New Variable Dialogue Box</p>" + "<p>Variables allow you to store information</p>" + "<p>in a method when it runs. You may choose</p>" + "<p>among several types of information (like</p>" + "<p>numbers, objects, and Booleans).</p>" + "</body></html>");
		}
		if (scrollPane == null) {
			scrollPane = new javax.swing.JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

				@Override
				public void printComponent(java.awt.Graphics g) {
					// do nothing
				}
			};
			scrollPane.getViewport().setOpaque(false);
			// scrollPane.getViewport().putClientProperty("EnableWindowBlit",
			// Boolean.TRUE);
			scrollPane.setOpaque(false);
			scrollPane.setBorder(null);
		}
		if (parameterDropHandler == null) {
			parameterDropHandler = new DropTargetHandler(requiredParameters);
		}
		if (parameterPanel == null) {
			parameterPanel = new javax.swing.JPanel() {

				@Override
				public void paintComponent(java.awt.Graphics g) {
					super.paintComponent(g);
					if (lineLocation > -1 && paintParameter) {
						// java.awt.Rectangle bounds = getBounds();
						g.setColor(java.awt.Color.black);
						g.fillRect(lineLocation, verticalLineLocation, 2, lineHeight);
					}
				}
			};
			parameterPanel.setBackground(backgroundColor);
			parameterPanel.setBorder(null);
			parameterPanel.setLayout(new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, anchor, javax.swing.JTabbedPane.class, 152));
			parameterPanel.setDropTarget(new java.awt.dnd.DropTarget(parameterPanel, parameterDropHandler));
			parameterDropHandler.setPanel(parameterPanel);
		}
		if (variableDropHandler == null) {
			variableDropHandler = new DropTargetHandler(localVariables);
		}
		if (variablePanel == null) {
			variablePanel = new javax.swing.JPanel() {

				@Override
				public void paintComponent(java.awt.Graphics g) {
					super.paintComponent(g);
					if (lineLocation > -1 && paintVariable) {
						// java.awt.Rectangle bounds = getBounds();
						g.setColor(java.awt.Color.black);
						g.fillRect(lineLocation, verticalLineLocation, 2, lineHeight);

					}
				}
			};
			variablePanel.setBackground(backgroundColor);
			variablePanel.setBorder(null);
			variablePanel.setLayout(new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, anchor, javax.swing.JTabbedPane.class, 152));
			variablePanel.setDropTarget(new java.awt.dnd.DropTarget(variablePanel, variableDropHandler));
			variableDropHandler.setPanel(variablePanel);
		}
		if (headerPanel == null) {
			headerPanel = new javax.swing.JPanel();
			headerPanel.setOpaque(false);
			headerPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.gray));
			headerPanel.setLayout(new java.awt.GridBagLayout());
			// headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel,
			// javax.swing.BoxLayout.Y_AXIS));
		}
		methodNameLabel = new javax.swing.JLabel();
		java.awt.Font nameFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (14 * fontSize / 12.0));
		methodNameLabel.setFont(nameFont);
		methodNameLabel.setDropTarget(new java.awt.dnd.DropTarget(methodNameLabel, parameterDropHandler));

		noParametersLabel = new javax.swing.JLabel("No parameters");
		noParametersLabel.setDropTarget(new java.awt.dnd.DropTarget(noParametersLabel, parameterDropHandler));
		java.awt.Font noFont = new java.awt.Font("SansSerif", java.awt.Font.ITALIC, (int) (12 * fontSize / 12.0));
		noParametersLabel.setFont(noFont);
		noVariablesLabel = new javax.swing.JLabel("No variables");
		noVariablesLabel.setDropTarget(new java.awt.dnd.DropTarget(noVariablesLabel, variableDropHandler));
		noVariablesLabel.setFont(noFont);

		mainParameterPanel = new javax.swing.JPanel();
		mainParameterPanel.setBackground(backgroundColor);
		mainParameterPanel.setOpaque(true);
		mainParameterPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.lightGray));
		mainParameterPanel.setLayout(new java.awt.GridBagLayout());
		mainParameterPanel.add(parameterPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		mainParameterPanel.add(newParameterButton, new java.awt.GridBagConstraints(1, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(4, 4, 4, 4), 0, 0));
		// mainParameterPanel.setLayout(new java.awt.BorderLayout());
		// mainParameterPanel.add(parameterPanel, java.awt.BorderLayout.CENTER);
		// mainParameterPanel.add(newParameterButton,
		// java.awt.BorderLayout.EAST);

		mainVariablePanel = new javax.swing.JPanel();
		mainVariablePanel.setOpaque(true);
		mainVariablePanel.setBackground(backgroundColor);
		mainVariablePanel.setBorder(null);
		mainVariablePanel.setLayout(new java.awt.GridBagLayout());
		mainVariablePanel.add(variablePanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		mainVariablePanel.add(newVariableButton, new java.awt.GridBagConstraints(1, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(4, 4, 4, 4), 0, 0));

		// mainVariablePanel.setLayout(new java.awt.BorderLayout());
		// mainVariablePanel.add(variablePanel, java.awt.BorderLayout.CENTER);
		// mainVariablePanel.add(newVariableButton, java.awt.BorderLayout.EAST);

	}

	private void DEBUG_printTree(java.awt.Container c) {
		for (int i = 0; i < c.getComponentCount(); i++) {
			if (c.getComponent(i).getHeight() > 0) {
				java.awt.Color bc = c.getComponent(i).getBackground();
				if (bc.getRed() == bc.getBlue() && bc.getBlue() == bc.getGreen()) {

					if (!(c.getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)) {
						System.out.println(c.getComponent(i));
						System.out.println(c.getComponent(i).isOpaque());
					}
					if (c.getComponent(i) instanceof java.awt.Container) {
						if (c.getComponent(i) instanceof javax.swing.JScrollPane) {
							javax.swing.JScrollPane sp = (javax.swing.JScrollPane) c.getComponent(i);
							DEBUG_printTree(sp.getViewport());
						} else {
							DEBUG_printTree((java.awt.Container) c.getComponent(i));
						}
					}
				}
			}
		}
	}

	@Override
	protected void updateGUI() {
		removeAll();
		buildParameterPanel();
		buildVariablePanel();
		headerPanel.add(mainParameterPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		headerPanel.add(mainVariablePanel, new java.awt.GridBagConstraints(0, 1, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		scrollPane.setViewportView(componentElementPanel);
		scrollPane.getViewport().setOpaque(false);
		this.add(scrollPane, java.awt.BorderLayout.CENTER);
		this.add(headerPanel, java.awt.BorderLayout.NORTH);
		if (CompositeElementEditor.IS_JAVA) {
			closeBrace.setText(" }");
			this.add(closeBrace, java.awt.BorderLayout.SOUTH);
		}

		setBackground(getCustomBackgroundColor());
		this.repaint();
		revalidate();
	}

	protected boolean isInGroup(Object toCheck, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group) {
		if (toCheck instanceof edu.cmu.cs.stage3.alice.core.Element) {
			return group.contains(toCheck);
		}
		return false;
	}

	@Override
	public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (isInGroup(propertyEvent.getProperty().getOwner(), requiredParameters)) {

			buildParameterPanel();
		} else if (isInGroup(propertyEvent.getProperty().getOwner(), localVariables)) {
			buildVariablePanel();
		} else if (propertyEvent.getProperty() == m_element.name) {
			buildParameterPanel();
		}
		this.repaint();
		revalidate();
	}

	private edu.cmu.cs.stage3.alice.core.Variable isPromotable(java.awt.datatransfer.Transferable transferring) {
		if (transferring != null && edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferring, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
			try {
				edu.cmu.cs.stage3.alice.core.Variable toCheck = (edu.cmu.cs.stage3.alice.core.Variable) transferring.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
				if (localVariables.contains(toCheck) || requiredParameters.contains(toCheck)) {
					return toCheck;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	protected class DropTargetHandler implements java.awt.dnd.DropTargetListener {
		private edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty m_group;
		private javax.swing.JPanel containingPanel;
		private int variablePosition;
		boolean isParameter = false;

		// /////////////////////////////////////////////
		// DropTargetListener interface
		// /////////////////////////////////////////////

		public DropTargetHandler(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group) {
			m_group = group;
			if (group == requiredParameters) {
				isParameter = true;
			}
		}

		public void setProperty(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty group) {
			m_group = group;
			if (group == requiredParameters) {
				isParameter = true;
			} else {
				isParameter = false;
			}
		}

		public void setPanel(javax.swing.JPanel panel) {
			containingPanel = panel;
		}

		protected int getStartIndex() {
			int start = 0;
			for (int i = 0; i < containingPanel.getComponentCount(); i++) {
				java.awt.Component c = containingPanel.getComponent(i);
				if (c == methodNameLabel || c == noParametersLabel || c == noVariablesLabel) {
					start++;
					continue;
				}
				return start;
			}
			return start;
		}

		private void turnNoPanelOn() {
			if (isParameter) {
				if (!noParametersLabel.isOpaque()) {
					noParametersLabel.setBackground(java.awt.Color.white);
					noParametersLabel.setOpaque(true);
					noParametersLabel.repaint();
				}
			} else {
				if (!noVariablesLabel.isOpaque()) {
					noVariablesLabel.setBackground(java.awt.Color.white);
					noVariablesLabel.setOpaque(true);
					noVariablesLabel.repaint();
				}
			}
		}

		private void turnNoPanelOff() {
			if (isParameter) {
				if (noParametersLabel.isOpaque()) {
					noParametersLabel.setBackground(java.awt.Color.white);
					noParametersLabel.setOpaque(false);
					noParametersLabel.repaint();
				}
			} else {
				if (noVariablesLabel.isOpaque()) {
					noVariablesLabel.setBackground(java.awt.Color.white);
					noVariablesLabel.setOpaque(false);
					noVariablesLabel.repaint();
				}
			}
		}

		protected void calculateLineLocation(int mouseX, int mouseY) {
			int numSpots = m_group.size() + 1;
			int[] spots = new int[numSpots];
			int[] centers = new int[numSpots];
			int startIndex = getStartIndex();
			if (m_group.size() == 0) {
				variablePosition = 0;
				verticalLineLocation = -1;
				lineLocation = -1;
				turnNoPanelOn();
			} else {
				turnNoPanelOff();
				javax.swing.JComponent firstComponent = (javax.swing.JComponent) containingPanel.getComponent(startIndex);
				spots[0] = firstComponent.getBounds().x - firstComponent.getInsets().left - 4;
				centers[0] = 5 + lineHeight / 2;
				int currentIndex = 1;
				for (int i = 0; i < numSpots - 1; i++) {
					javax.swing.JComponent c = (javax.swing.JComponent) containingPanel.getComponent(startIndex);
					java.awt.Insets insets = c.getInsets();
					// System.out.println("insets: "+insets);
					spots[currentIndex] = c.getBounds().x + c.getBounds().width + insets.left; // assumes
																								// gridBagConstraints
																								// insets.bottom
																								// ==
																								// 2
					centers[currentIndex] = c.getBounds().y + lineHeight / 2;
					currentIndex++;
					startIndex++;
				}
				int closestSpot = -2;
				int minDist = Integer.MAX_VALUE;
				for (int i = 0; i < numSpots; i++) {
					int d = Math.abs(mouseX - spots[i]) + Math.abs(mouseY - centers[i]);
					// System.out.println(i+": current: "+spots[i]+", "+centers[i]+", d = "+d+", minDist = "+minDist);
					if (d < minDist) {
						minDist = d;
						closestSpot = i;
					}
				}
				// System.out.println("Selected "+ closestSpot);
				variablePosition = closestSpot;
				verticalLineLocation = centers[closestSpot] - lineHeight / 2;
				lineLocation = spots[closestSpot];
			}
		}

		@Override
		public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			} else {
				turnNoPanelOff();
				lineLocation = -1;
				paintParameter = false;
				paintVariable = false;
				dtde.rejectDrag();
				containingPanel.repaint();
			}
		}

		@Override
		public void dragExit(java.awt.dnd.DropTargetEvent dte) {
			lineLocation = -1;
			paintParameter = false;
			paintVariable = false;
			turnNoPanelOff();
			containingPanel.repaint();
		}

		@Override
		public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
			if (isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable()) != null) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				java.awt.Point p = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), containingPanel);
				int lineTemp = lineLocation;
				calculateLineLocation(p.x, p.y);
				if (lineTemp != lineLocation) {
					if (isParameter) {
						paintParameter = true;
						paintVariable = false;
					} else {
						paintParameter = false;
						paintVariable = true;
					}
					containingPanel.repaint();
				}
			} else {
				lineLocation = -1;
				paintParameter = false;
				paintVariable = false;
				turnNoPanelOff();
				dtde.rejectDrag();
				containingPanel.repaint();
			}
		}

		protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty getGroup(edu.cmu.cs.stage3.alice.core.Variable toRemove) {
			edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty containingArray = null;
			if (toRemove.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
				edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse parent = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) toRemove.getParent();
				if (parent.keywordFormalParameters.contains(toRemove)) {
					containingArray = parent.keywordFormalParameters;
				} else if (parent.localVariables.contains(toRemove)) {
					containingArray = parent.localVariables;
				} else if (parent.requiredFormalParameters.contains(toRemove)) {
					containingArray = parent.requiredFormalParameters;
				} else if (parent.requiredFormalParameters.contains(toRemove)) {
					containingArray = parent.requiredFormalParameters;
				}
			}
			if (toRemove.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion parent = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) toRemove.getParent();
				if (parent.keywordFormalParameters.contains(toRemove)) {
					containingArray = parent.keywordFormalParameters;
				} else if (parent.localVariables.contains(toRemove)) {
					containingArray = parent.localVariables;
				} else if (parent.requiredFormalParameters.contains(toRemove)) {
					containingArray = parent.requiredFormalParameters;
				} else if (parent.requiredFormalParameters.contains(toRemove)) {
					containingArray = parent.requiredFormalParameters;
				}
			}
			return containingArray;
		}

		@Override
		public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
			edu.cmu.cs.stage3.alice.core.Variable toAdd = isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable());
			paintParameter = false;
			paintVariable = false;
			boolean successful = true;
			if (toAdd != null) {
				dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
				java.awt.Point p = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), containingPanel);
				calculateLineLocation(p.x, p.y);
				edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty containingArray = getGroup(toAdd);
				if (variablePosition > m_group.size()) {
					variablePosition = m_group.size();
				}
				if (containingArray == m_group) {
					int to = variablePosition;
					int from = containingArray.indexOf(toAdd);
					if (from < to) {
						to--;
					}
					containingArray.shift(from, to);
				} else if (containingArray != null) {
					containingArray.remove(toAdd);
					m_group.add(variablePosition, toAdd);
				}
				turnNoPanelOff();
				lineLocation = -1;
			} else {
				turnNoPanelOff();
				lineLocation = -1;
				dtde.rejectDrop();
				successful = false;

			}
			dtde.dropComplete(successful);

		}

		@Override
		public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
			if (isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable()) != null) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			} else {
				dtde.rejectDrag();
			}
		}
	}

	// custom functions for stencil tutorial

	public java.awt.Component getWorkSpace() {
		return scrollPane;
	}

	public java.awt.Component getParameterPanel() {
		return parameterPanel;
	}

	public java.awt.Component getVariablePanel() {
		return variablePanel;
	}

}