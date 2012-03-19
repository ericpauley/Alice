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

public abstract class CompositeElementPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements CompositeComponentOwner, edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.core.event.PropertyListener {
	protected CompositeComponentElementPanel componentElementPanel;
	protected edu.cmu.cs.stage3.alice.core.Element m_element;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty m_components;
	protected edu.cmu.cs.stage3.alice.core.property.BooleanProperty m_isCommentedOut;
	protected String headerText = "Composite Element";
	protected javax.swing.JPanel headerPanel;
	protected javax.swing.JLabel closeBrace = new javax.swing.JLabel("}");
	protected javax.swing.JPanel containingPanel;
	protected javax.swing.JButton expandButton;
	protected javax.swing.JComponent nameInputField;
	protected javax.swing.Action expandAction;
	protected javax.swing.JLabel headerLabel;
	protected boolean isExpanded = true;
	protected java.awt.Color backgroundColor = new java.awt.Color(255, 255, 255);
	protected int m_depth = 0;
	protected javax.swing.ImageIcon plus;
	protected javax.swing.ImageIcon minus;
	protected java.awt.Component glue;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected java.awt.event.ActionListener actionListener;
	// protected javax.swing.JPopupMenu popUpMenu;
	protected java.awt.event.MouseListener elementMouseListener = new java.awt.event.MouseAdapter() {

		@Override
		public void mouseReleased(java.awt.event.MouseEvent ev) {
			if (ev.isPopupTrigger() || System.getProperty("os.name") != null && !System.getProperty("os.name").startsWith("Windows") && ev.isControlDown()) {
				if (CompositeElementPanel.this.getParent() != null) {
					java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(m_element);
					if (!ForEachElementPanel.class.isAssignableFrom(CompositeElementPanel.this.getClass()) && !LoopNElementPanel.class.isAssignableFrom(CompositeElementPanel.this.getClass())) {
						Runnable dissolveRunnable = new Runnable() {
							@Override
							public void run() {
								dissolve();
							}
						};
						edu.cmu.cs.stage3.util.StringObjectPair dissolveEntry = new edu.cmu.cs.stage3.util.StringObjectPair("dissolve", dissolveRunnable);
						if (structure != null) {
							structure.add(dissolveEntry);
						}
					}
					if (structure != null) {
						edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.createAndShowElementPopupMenu(m_element, structure, CompositeElementPanel.this, ev.getX(), ev.getY());
					}
				}
			}
		}
	};

	protected static String IS_EXPANDED_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor IS_EXPANDED_KEY";

	public CompositeElementPanel() {
		headerText = "Element Response";
		actionListener = new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (isExpanded) {
					reduceComponentElementPanel();
				} else {
					expandComponentElementPanel();
				}
			}
		};
		backgroundColor = getCustomBackgroundColor();
		generateGUI();
	}

	public void set(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
		clean();
		super.reset();
		authoringTool = authoringToolIn;
		m_element = element;
		variableInit();
		startListening();
		setHeaderLabel();
		updateGUI();
		setDropTargets();
	}

	protected java.awt.Color getCustomBackgroundColor() {
		return backgroundColor;
	}

	protected void setDropTargets() {
		headerLabel.setDropTarget(new java.awt.dnd.DropTarget(headerLabel, componentElementPanel));
		setDropTarget(new java.awt.dnd.DropTarget(this, componentElementPanel));
		containingPanel.setDropTarget(new java.awt.dnd.DropTarget(containingPanel, componentElementPanel));
		headerPanel.setDropTarget(new java.awt.dnd.DropTarget(headerPanel, componentElementPanel));
		grip.setDropTarget(new java.awt.dnd.DropTarget(grip, componentElementPanel));
		glue.setDropTarget(new java.awt.dnd.DropTarget(glue, componentElementPanel));
		expandButton.setDropTarget(new java.awt.dnd.DropTarget(expandButton, componentElementPanel));
	}

	protected void variableInit() {
		Object isExpandedValue = m_element.data.get(IS_EXPANDED_KEY);
		backgroundColor = getCustomBackgroundColor();
		if (isExpandedValue instanceof Boolean) {
			isExpanded = ((Boolean) isExpandedValue).booleanValue();
		}
		setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable(m_element));
		if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
			edu.cmu.cs.stage3.alice.core.response.CompositeResponse proxy = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse) m_element;
			m_components = proxy.componentResponses;
			m_isCommentedOut = proxy.isCommentedOut;
			componentElementPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CompositeComponentResponsePanel();
			componentElementPanel.set(m_components, this, authoringTool);
			componentElementPanel.setBackground(backgroundColor);
			addDragSourceComponent(componentElementPanel);
			componentElementPanel.addMouseListener(elementMouseListener);
		} else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.Composite proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) m_element;
			m_components = proxy.components;
			m_isCommentedOut = proxy.isCommentedOut;
			componentElementPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.CompositeComponentQuestionPanel();
			componentElementPanel.set(m_components, this, authoringTool);
			componentElementPanel.setBackground(backgroundColor);
			addDragSourceComponent(componentElementPanel);
			componentElementPanel.addMouseListener(elementMouseListener);
		}
	}

	@Override
	public java.awt.Component getGrip() {
		return grip;
	}

	@Override
	public java.awt.Container getParent() {
		return super.getParent();
	}

	protected void startListening() {
		if (m_element != null) {
			m_element.name.addPropertyListener(this);
		}
		if (m_isCommentedOut != null) {
			m_isCommentedOut.addPropertyListener(this);
		}
	}

	protected void stopListening() {
		if (m_element != null) {
			m_element.name.removePropertyListener(this);
		}
		if (m_isCommentedOut != null) {
			m_isCommentedOut.removePropertyListener(this);
		}
	}

	@Override
	public void goToSleep() {
		stopListening();
		if (componentElementPanel != null) {
			componentElementPanel.goToSleep();
		}
	}

	@Override
	public void wakeUp() {
		startListening();
		if (componentElementPanel != null) {
			componentElementPanel.wakeUp();
		}
	}

	@Override
	public void release() {
		super.release();
		if (componentElementPanel != null) {
			componentElementPanel.release();
		}
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	@Override
	public void clean() {
		goToSleep();
		if (componentElementPanel != null) {
			if (containingPanel != null) {
				containingPanel.remove(componentElementPanel);
			}
			componentElementPanel.release();
			componentElementPanel = null;
		}
	}

	protected void removeAllListening() {
		removeDragSourceComponent(componentElementPanel);
		componentElementPanel.removeMouseListener(elementMouseListener);
		grip.setDropTarget(null);
		removeDragSourceComponent(glue);
		glue.removeMouseListener(elementMouseListener);
		removeMouseListener(elementMouseListener);
		grip.removeMouseListener(elementMouseListener);
		expandButton.removeActionListener(actionListener);
		setTransferable(null);
		if (headerLabel != null) {
			headerLabel.removeMouseListener(elementMouseListener);
			removeDragSourceComponent(headerLabel);
		}
		if (containingPanel != null) {
			containingPanel.removeMouseListener(elementMouseListener);
			removeDragSourceComponent(containingPanel);
		}
		if (headerPanel != null) {
			headerPanel.removeMouseListener(elementMouseListener);
			removeDragSourceComponent(headerPanel);
		}
	}

	@Override
	public void die() {
		clean();
		removeAllListening();
	}

	public void dissolve() {
		if (m_element.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
			authoringTool.getUndoRedoStack().startCompound();
			edu.cmu.cs.stage3.alice.core.response.CompositeResponse parent = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse) m_element.getParent();
			int index = parent.componentResponses.indexOf(m_element);
			if (parent instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
				if (!parent.componentResponses.contains(m_element)) {
					index = ((edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) parent).elseComponentResponses.indexOf(m_element);
				}
			}
			Object responses[] = m_components.getArrayValue();
			for (int i = 0; i < responses.length; i++) {
				if (responses[i] instanceof edu.cmu.cs.stage3.alice.core.Response) {
					edu.cmu.cs.stage3.alice.core.Response currentResponse = (edu.cmu.cs.stage3.alice.core.Response) responses[i];
					currentResponse.removeFromParent();
					currentResponse.setParent(parent);
					parent.componentResponses.add(index + i, currentResponse);
				}
			}
			m_element.removeFromParent();
			authoringTool.getUndoRedoStack().stopCompound();
		} else if (m_element.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {

			authoringTool.getUndoRedoStack().startCompound();
			edu.cmu.cs.stage3.alice.core.question.userdefined.Composite parent = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) m_element.getParent();
			int index = parent.getIndexOfChild(m_element);
			if (parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
				if (!parent.components.contains(m_element)) {
					index = ((edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) parent).elseComponents.indexOf(m_element);
				}
			}

			Object questions[] = m_components.getArrayValue();
			for (int i = 0; i < questions.length; i++) {
				if (questions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
					edu.cmu.cs.stage3.alice.core.question.userdefined.Component currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Component) questions[i];
					currentQuestion.removeFromParent();
					currentQuestion.setParent(parent);
					parent.components.add(index + i, currentQuestion);
				}
			}
			m_element.removeFromParent();
			authoringTool.getUndoRedoStack().stopCompound();
		} else if (m_element.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			authoringTool.getUndoRedoStack().startCompound();
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion parent = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) m_element.getParent();
			int index = parent.getIndexOfChild(m_element);

			Object questions[] = m_components.getArrayValue();
			for (int i = 0; i < questions.length; i++) {
				if (questions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
					edu.cmu.cs.stage3.alice.core.question.userdefined.Component currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Component) questions[i];
					currentQuestion.removeFromParent();
					currentQuestion.setParent(parent);
					parent.components.add(index + i, currentQuestion);
				}
			}
			m_element.removeFromParent();
			authoringTool.getUndoRedoStack().stopCompound();
		}
	}

	public void prePropertyChange(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
	}

	@Override
	public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
	}

	@Override
	public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		if (propertyEvent.getProperty() == m_isCommentedOut) {
			revalidate();
			repaint();
		} else {
			updateGUI();
		}
	}

	@Override
	public void setBackground(java.awt.Color color) {
		super.setBackground(color);
		backgroundColor = color;
		if (containingPanel != null) {
			containingPanel.setBackground(backgroundColor);
		}
		if (headerLabel != null) {
			headerLabel.setBackground(backgroundColor);
		}
		if (headerPanel != null) {
			headerPanel.setBackground(backgroundColor);
		}
		if (componentElementPanel != null) {
			componentElementPanel.setBackground(backgroundColor);
		}
	}

	protected void generateGUI() {
		setOpaque(false);
		plus = new javax.swing.ImageIcon(CompositeElementPanel.class.getResource("images/plus.gif"));
		minus = new javax.swing.ImageIcon(CompositeElementPanel.class.getResource("images/minus.gif"));
		expandButton = new javax.swing.JButton();
		expandButton.setContentAreaFilled(false);
		expandButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		expandButton.setFocusPainted(false);
		expandButton.setBorderPainted(false);
		expandButton.setBorder(null);
		expandButton.addActionListener(actionListener);
		glue = javax.swing.Box.createHorizontalGlue();
		addDragSourceComponent(glue);
		glue.addMouseListener(elementMouseListener);
		addMouseListener(elementMouseListener);
		grip.addMouseListener(elementMouseListener);
		if (headerLabel == null) {
			headerLabel = new javax.swing.JLabel();
			setHeaderLabel();
			headerLabel.setOpaque(false);
			headerLabel.addMouseListener(elementMouseListener);
			addDragSourceComponent(headerLabel);
		}
		if (containingPanel == null) {
			containingPanel = new javax.swing.JPanel();
			containingPanel.setLayout(new java.awt.BorderLayout());
			containingPanel.addMouseListener(elementMouseListener);
			containingPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			containingPanel.setOpaque(false);
			addDragSourceComponent(containingPanel);
		}
		if (headerPanel == null) {
			headerPanel = new javax.swing.JPanel();
			headerPanel.setLayout(new java.awt.GridBagLayout());
			headerPanel.addMouseListener(elementMouseListener);
			headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			headerPanel.setOpaque(false);
			addDragSourceComponent(headerPanel);
		}
	}

	protected void restoreDrag() {
		addDragSourceComponent(glue);
		addDragSourceComponent(headerPanel);
		addDragSourceComponent(containingPanel);
		addDragSourceComponent(headerLabel);
	}

	protected void updateGUI() {
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
		headerPanel.add(glue, new java.awt.GridBagConstraints(3, 0, 1, 1, 1, 0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		containingPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
		if (isExpanded) {
			containingPanel.add(componentElementPanel, java.awt.BorderLayout.CENTER);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.add(closeBrace, java.awt.BorderLayout.SOUTH);
			}
		}
		this.add(containingPanel, java.awt.BorderLayout.CENTER);
		setBackground(getCustomBackgroundColor());
	}

	@Override
	public boolean isExpanded() {
		if (isExpanded) {
			return true;
		}
		return false;
	}

	public void expandComponentElementPanel() {
		if (!isExpanded) {
			m_element.data.put(IS_EXPANDED_KEY, Boolean.TRUE);
			isExpanded = true;
			setHeaderLabel();
			expandButton.setIcon(minus);
			/*
			 * if (componentElementPanel.getComponentCount() == 0){
			 * componentElementPanel = new CompositeComponentElementPanel();
			 * componentElementPanel.set(m_components, this, authoringTool); }
			 */
			containingPanel.add(componentElementPanel, java.awt.BorderLayout.CENTER);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.add(closeBrace, java.awt.BorderLayout.SOUTH);
			}
			revalidate();
			// this.repaint();
		}
	}

	public void setHeaderLabel() {
		if (headerLabel != null) {
			headerLabel.setText(headerText);
			if (CompositeElementEditor.IS_JAVA) {
				if (!isExpanded) {
					headerLabel.setText(headerText + " { " + getDots() + " }");
				} else {
					headerLabel.setText(headerText + " {");
				}
			}
		}
	}

	protected static int getEmptyRows(Object[] elements) {
		int count = 0;
		for (Object element : elements) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
				if (((edu.cmu.cs.stage3.alice.core.response.CompositeResponse) element).componentResponses.size() == 0) {
					count++;
				}
				if (element instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
					if (((edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) element).elseComponentResponses.size() == 0) {
						count++;
					}
				}
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
				if (((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) element).components.size() == 0) {
					count++;
				}
				if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
					if (((edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) element).elseComponents.size() == 0) {
						count++;
					}
				}
			}
		}
		return count;
	}

	protected static int getTotalRowsToRenderForIfElse(edu.cmu.cs.stage3.alice.core.Element ifElse, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty components) {
		int total = 0;
		java.util.Vector potentiallyEmpty = new java.util.Vector();
		if (ifElse instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
			edu.cmu.cs.stage3.alice.core.response.IfElseInOrder ifElseResponse = (edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) ifElse;
			for (int i = 0; i < components.size(); i++) {
				if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
					edu.cmu.cs.stage3.alice.core.Element[] ds = ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.Response.class);
					total += ds.length;
					total += ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class).length;
					potentiallyEmpty.addAll(java.util.Arrays.asList(((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class)));
					if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
						total += ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class).length; // add
																																													// on
																																													// the
																																													// extra
																																													// "}"
																																													// for
																																													// each
																																													// composite
					}
				} else {
					total++;
				}
			}
		} else if (ifElse instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse ifElseQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) ifElse;
			for (int i = 0; i < components.size(); i++) {
				total++;
				if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
					total++;
				}
				if (components.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
					potentiallyEmpty.add(components.get(i));
				}
				total += ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class).length;
				total += ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse.class).length;
				potentiallyEmpty.addAll(java.util.Arrays.asList(((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Composite.class)));
				if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
					total += ((edu.cmu.cs.stage3.alice.core.Element) components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Composite.class).length; // add
																																													// on
																																													// the
																																													// extra
																																													// "}"
																																													// for
																																													// each
																																													// composite
				}
			}
		}
		int emptyRows = getEmptyRows(potentiallyEmpty.toArray());
		total += emptyRows;
		return total;
	}

	protected String getHTMLColorString(java.awt.Color color) {
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());

		if (r.length() == 1) {
			r = "0" + r;
		}
		if (g.length() == 1) {
			g = "0" + g;
		}
		if (b.length() == 1) {
			b = "0" + b;
		}
		return new String("#" + r + g + b);
	}

	protected String getHeaderHTML() {
		return edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(headerPanel);
	}

	public static int getTotalHTMLRows(edu.cmu.cs.stage3.alice.core.Element element) {
		int totalRows = 0;
		if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
				totalRows = getTotalRowsToRenderForIfElse(element, ((edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) element).componentResponses);
			} else {
				totalRows = element.getDescendants(edu.cmu.cs.stage3.alice.core.Response.class).length - 1;
				totalRows += element.getDescendants(edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class).length;
				totalRows += getEmptyRows(element.getDescendants(edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class));

				if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
					totalRows += element.getDescendants(edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class).length - 1; // add
																																	// on
																																	// the
																																	// extra
																																	// "}"
																																	// for
																																	// each
																																	// composite
				}
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite || element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
				totalRows = getTotalRowsToRenderForIfElse(element, ((edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) element).components);
			} else {
				totalRows = element.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class).length - 1;
				totalRows += element.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse.class).length;
				totalRows += getEmptyRows(element.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Composite.class));
				if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
					totalRows += element.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Composite.class).length - 1; // add
																																		// on
																																		// the
																																		// extra
																																		// "}"
																																		// for
																																		// each
																																		// composite
				}
			}
		}
		return totalRows;
	}

	public void getHTML(StringBuffer toWriteTo, int colSpan, boolean useColor, boolean isDisabled) {
		int totalRows = 0;
		String colorString = "";
		String borderColorString = getHTMLColorString(java.awt.Color.lightGray);
		String styleString = "";
		String strikeStart = "";
		String strikeEnd = "";
		if (!isDisabled) {
			isDisabled = isDisabled();
		}
		if (useColor) {
			if (isDisabled) {
				colorString = " bgcolor=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML")) + "\"";
			} else {
				colorString = " bgcolor=\"" + getHTMLColorString(getCustomBackgroundColor()) + "\"";
			}
		}
		if (isDisabled) {
			strikeStart = "<strike><font color=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
			strikeEnd = "</font></strike>";
		}
		totalRows = getTotalHTMLRows(m_element);
		if (this instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel) {
			totalRows++;
		}
		styleString = " style=\"border-left: 1px solid " + borderColorString + "; border-top: 1px solid " + borderColorString + "; border-right: 1px solid " + borderColorString + "\"";
		toWriteTo.append("<td colspan=\"" + colSpan + "\"" + colorString + styleString + ">&nbsp;&nbsp;");

		toWriteTo.append(strikeStart + getHeaderHTML() + strikeEnd);
		if (this instanceof IfElseElementPanel || edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
			styleString = " style=\"border-left: 1px solid " + borderColorString + "\"";
		} else {
			styleString = " style=\"border-left: 1px solid " + borderColorString + "; border-bottom: 1px solid " + borderColorString + "\"";
		}
		toWriteTo.append("</td>\n</tr>\n<tr>\n<td width=\"20\" rowspan=\"" + totalRows + "\"" + colorString + styleString + ">&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
		for (int i = 0; i < componentElementPanel.getElementComponentCount(); i++) {
			if (i > 0) {
				toWriteTo.append("<tr>\n");
			}
			java.awt.Component currentComponent = componentElementPanel.getComponent(i);
			if (currentComponent instanceof CompositeElementPanel) {
				((CompositeElementPanel) currentComponent).getHTML(toWriteTo, colSpan - 1, useColor, isDisabled);
			} else {
				String componentColorString = "";
				String componentStrikeStart = "";
				String componentStrikeEnd = "";
				boolean isComponentDisabled = isDisabled || ((ComponentElementPanel) currentComponent).isDisabled();
				if (currentComponent instanceof ComponentElementPanel) {
					if (isComponentDisabled) {
						componentColorString = " bgcolor=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML")) + "\"";
						componentStrikeStart = "<strike><font color=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
						componentStrikeEnd = "</font></strike>";
					} else {
						componentColorString = " bgcolor=\"" + getHTMLColorString(((ComponentElementPanel) currentComponent).getCustomBackgroundColor()) + "\"";
					}
				}
				styleString = " style=\"border-bottom: 1px solid " + borderColorString + "; border-right: 1px solid " + borderColorString + "; border-left: 1px solid " + borderColorString + "; border-top: 1px solid " + borderColorString + "\"";
				toWriteTo.append("<td width=\"100%\" colspan=\"" + (colSpan - 1) + "\"" + componentColorString + styleString + ">&nbsp;&nbsp;" + componentStrikeStart + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(currentComponent) + componentStrikeEnd + "</td>\n");

			}

		}
		if (componentElementPanel.getElementComponentCount() == 0) {
			if (this instanceof IfElseElementPanel || edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
				styleString = " style=\"border-right: 1 solid " + borderColorString + "\"";
			} else {
				styleString = " style=\"border-right: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
			}
			toWriteTo.append("<td width=\"100%\" colspan=\"" + (colSpan - 1) + "\"" + colorString + styleString + ">" + strikeStart + "<i> Do Nothing </i>" + strikeEnd + "</td>\n</tr>\n");
		}
		if (this instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel) {
			edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel mainQuestion = (edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel) this;
			styleString = " style=\"border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "; border-left: 1 solid " + borderColorString + ";\"";
			colorString = " bgcolor=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("Return")) + "\"";
			toWriteTo.append("<tr>\n<td width=\"100%\" colspan=\"" + colSpan + "\"" + colorString + styleString + ">" + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(mainQuestion.returnPanel) + "</td>\n</tr>\n");
		}
		if (this instanceof IfElseElementPanel) {
			IfElseElementPanel ifElse = (IfElseElementPanel) this;
			if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
				totalRows = getTotalRowsToRenderForIfElse(m_element, ((edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) m_element).elseComponents);
			} else if (m_element instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
				totalRows = getTotalRowsToRenderForIfElse(m_element, ((edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) m_element).elseComponentResponses);
			}
			styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "\"";

			toWriteTo.append("<tr>\n<td colspan=\"" + colSpan + "\"" + colorString + styleString + ">&nbsp;&nbsp;" + strikeStart + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(((IfElseElementPanel) this).elsePanel) + strikeEnd + "</td>\n");
			if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
				styleString = " style=\"border-left: 1 solid " + borderColorString + "\"";
			} else {
				styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
			}
			toWriteTo.append("</tr>\n<tr>\n<td width=\"20\" rowspan=\"" + totalRows + "\"" + colorString + styleString + ">&nbsp;</td>\n");
			for (int i = 0; i < ifElse.elseComponentPanel.getElementComponentCount(); i++) {
				if (i > 0) {
					toWriteTo.append("<tr>\n");
				}
				java.awt.Component currentComponent = ifElse.elseComponentPanel.getComponent(i);
				if (currentComponent instanceof CompositeElementPanel) {
					((CompositeElementPanel) currentComponent).getHTML(toWriteTo, colSpan - 1, useColor, isDisabled);
				} else {
					String componentColorString = "";
					String componentStrikeStart = "";
					String componentStrikeEnd = "";
					boolean isComponentDisabled = isDisabled || ((ComponentElementPanel) currentComponent).isDisabled();
					if (currentComponent instanceof ComponentElementPanel) {
						if (isComponentDisabled) {
							componentColorString = " bgcolor=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTML")) + "\"";
							componentStrikeStart = "<strike><font color=\"" + getHTMLColorString(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
							componentStrikeEnd = "</font></strike>";
						} else {
							componentColorString = " bgcolor=\"" + getHTMLColorString(((ComponentElementPanel) currentComponent).getCustomBackgroundColor()) + "\"";
						}
					}
					styleString = " style=\"border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "; border-left: 1 solid " + borderColorString + "; border-top: 1 solid " + borderColorString + "\"";
					toWriteTo.append("<td colspan=\"" + (colSpan - 1) + "\"" + componentColorString + styleString + ">&nbsp;&nbsp;" + componentStrikeStart + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(currentComponent) + componentStrikeEnd + "</td>\n");

				}
				toWriteTo.append("</tr>\n");
			}
			if (ifElse.elseComponentPanel.getElementComponentCount() == 0) {
				if (!edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
					styleString = " style=\"border-right: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
				} else {
					styleString = " style=\"border-right: 1 solid " + borderColorString + "\"";
				}
				toWriteTo.append("<td colspan=\"" + (colSpan - 1) + "\"" + colorString + styleString + ">" + strikeStart + "<i> Do Nothing </i>" + strikeEnd + "</td>\n</tr>\n");
			}
		}
		if (edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor.IS_JAVA) {
			styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "\"";
			toWriteTo.append("<tr><td colspan=\"" + colSpan + "\"" + colorString + styleString + ">" + strikeStart + "<b>&nbsp;&nbsp;}</b>" + strikeEnd + "</td></tr>");
		}
		// toWriteTo.append("</tr>\n");
	}

	public String getDots() {
		String dots = "";
		for (int i = 0; i < m_components.size(); i++) {
			if (i == 0) {
				dots += ".";
			} else {
				dots += " .";
			}
		}
		return dots;
	}

	public void reduceComponentElementPanel() {
		if (isExpanded) {
			m_element.data.put(IS_EXPANDED_KEY, Boolean.FALSE);
			isExpanded = false;
			setHeaderLabel();
			expandButton.setIcon(plus);
			containingPanel.remove(componentElementPanel);
			if (CompositeElementEditor.IS_JAVA) {
				containingPanel.remove(closeBrace);
			}
			revalidate();
			// this.repaint();
		}
	}

	public CompositeComponentElementPanel getComponentPanel() {
		return componentElementPanel;
	}

	protected void dndInit() {
	}

	public void addResponsePanel(javax.swing.JComponent toAdd, int position) {
		componentElementPanel.addElementPanel(toAdd, position);
	}

	@Override
	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return m_element;
	}

	public boolean isDisabled() {
		boolean isEnabledValue = true;
		if (m_element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (((edu.cmu.cs.stage3.alice.core.Response) m_element).isCommentedOut.get() != null) {
				isEnabledValue = !((edu.cmu.cs.stage3.alice.core.Response) m_element).isCommentedOut.getBooleanValue().booleanValue();
			}
		} else if (m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component) {
			if (((edu.cmu.cs.stage3.alice.core.question.userdefined.Component) m_element).isCommentedOut.get() != null) {
				isEnabledValue = !((edu.cmu.cs.stage3.alice.core.question.userdefined.Component) m_element).isCommentedOut.getBooleanValue().booleanValue();
			}
		}
		return !isEnabledValue;
	}

	@Override
	public void paintForeground(java.awt.Graphics g) {
		super.paintForeground(g);
		if (m_element != null) {
			if (isDisabled()) {
				java.awt.Rectangle bounds = new java.awt.Rectangle(0, 0, getWidth(), getHeight());
				edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, bounds);
			}
		}
	}
}