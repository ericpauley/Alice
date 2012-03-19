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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;

// Referenced classes of package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor:
//            TempColorPicker, MainCompositeElementPanel, CompositeElementPanel

public abstract class CompositeElementEditor extends GroupingPanel implements Editor, PropertyListener {

	public CompositeElementEditor() {
		mainColor = Color.white;
		configInit();
		guiInit();
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getModifiers() & InputEvent.SHIFT_MASK) > 0) {
					TempColorPicker tempColorPicker = new TempColorPicker(CompositeElementEditor.this);
					edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(tempColorPicker);
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseevent) {
			}

			@Override
			public void mouseEntered(MouseEvent mouseevent) {
			}

			@Override
			public void mouseExited(MouseEvent mouseevent) {
			}

			@Override
			public void mouseReleased(MouseEvent mouseevent) {
			}

		});
	}

	public MainCompositeElementPanel getMainPanel() {
		return compositeElementPanel;
	}

	@Override
	public void setBackground(Color backgroundColor) {
		super.setBackground(backgroundColor);
		if (compositeElementPanel != null) {
			compositeElementPanel.setBackground(backgroundColor);
		}
	}

	private void configInit() {
		if (AuthoringToolResources.getMiscItem("javaLikeSyntax") != null) {
			IS_JAVA = AuthoringToolResources.getMiscItem("javaLikeSyntax").equals("true");
		}
	}

	@Override
	public JComponent getJComponent() {
		return this;
	}

	@Override
	public Object getObject() {
		return elementBeingEdited;
	}

	@Override
	public void setAuthoringTool(AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	protected abstract MainCompositeElementPanel createElementTree(Element element);

	protected abstract void initPrototypes();

	protected abstract void addPrototypes(Container container);

	protected abstract Color getEditorColor();

	protected void guiInit() {
		setBorder(null);
		removeAll();
		buttonPanel = new JPanel();
		Color buttonPanelColor = Color.white;
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(buttonPanelColor);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
		buttonPanel.setMinimumSize(new Dimension(0, 0));
		buttonPanel.setLayout(new FlowLayout(0) {

			@Override
			public Dimension preferredLayoutSize(Container target) {
				Insets insets = target.getParent().getInsets();
				int hgap = getHgap();
				int vgap = getVgap();
				int maxwidth = target.getParent().getWidth() - (insets.left + insets.right + hgap * 2);
				int nmembers = target.getComponentCount();
				int x = 0;
				int y = insets.top + vgap;
				int rowh = 0;
				int start = 0;
				boolean ltr = target.getComponentOrientation().isLeftToRight();
				for (int i = 0; i < nmembers; i++) {
					Component m = target.getComponent(i);
					if (!m.isVisible()) {
						continue;
					}
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);
					if (x == 0 || x + d.width <= maxwidth) {
						if (x > 0) {
							x += hgap;
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						x = d.width;
						y += vgap + rowh;
						rowh = d.height;
						start = i;
					}
				}

				return new Dimension(target.getParent().getWidth() - (insets.left + insets.right), y + rowh + vgap);
			}

			@Override
			public Dimension minimumLayoutSize(Container target) {
				return preferredLayoutSize(target);
			}

		});
		initPrototypes();
		addPrototypes(buttonPanel);
		JScrollPane buttonPanelScrollPane = new JScrollPane(buttonPanel, 20, 30);
		mainElementContainer = new JPanel();
		mainElementContainer.setLayout(new BorderLayout());
		mainElementContainer.setMinimumSize(new Dimension(0, 0));
		mainElementContainer.setBorder(null);
		mainElementContainer.setOpaque(false);
		mainElementContainer.setBackground(getEditorColor());
		setLayout(new BorderLayout());
		setBackground(getEditorColor());
		add(buttonPanel, "South");
		add(mainElementContainer, "Center");
		updateGui();
	}

	public void updateGui() {
		mainElementContainer.removeAll();
		if (elementBeingEdited != null && authoringTool != null) {
			clearAllListening();
			compositeElementPanel = createElementTree(elementBeingEdited);
			if (compositeElementPanel != null) {
				mainElementContainer.add(compositeElementPanel, "Center");
				setBackground(compositeElementPanel.getBackground());
			}
		} else {
			JLabel emptyLabel = new JLabel("Not an editable element");
			emptyLabel.setFont(emptyLabel.getFont().deriveFont(2));
			JPanel emptyPanel = new JPanel();
			emptyPanel.setLayout(new GridBagLayout());
			emptyPanel.setBackground(getBackground());
			emptyPanel.add(emptyLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
			mainElementContainer.add(emptyPanel, "Center");
		}
		revalidate();
		repaint();
	}

	public static int getDepthCount(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty children) {
		int maxDepth = 0;
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse) {
				edu.cmu.cs.stage3.alice.core.response.CompositeResponse currentResponse = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse) children.get(i);
				int depth = getDepthCount(currentResponse.componentResponses);
				if (depth > maxDepth) {
					maxDepth = depth;
				}
				if (currentResponse instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) {
					edu.cmu.cs.stage3.alice.core.response.IfElseInOrder ifResponse = (edu.cmu.cs.stage3.alice.core.response.IfElseInOrder) currentResponse;
					depth = getDepthCount(ifResponse.elseComponentResponses);
					if (depth > maxDepth) {
						maxDepth = depth;
					}
				}
			} else if (children.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.Composite currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Composite) children.get(i);
				int depth = getDepthCount(currentQuestion.components);
				if (depth > maxDepth) {
					maxDepth = depth;
				}
				if (currentQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) {
					edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse ifQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse) currentQuestion;
					depth = getDepthCount(ifQuestion.elseComponents);
					if (depth > maxDepth) {
						maxDepth = depth;
					}
				}
			}
		}
		return 1 + maxDepth;
	}

	public void getHTML(StringBuffer toWriteTo, boolean useColor) {
		if (compositeElementPanel != null) {
			int colSpan = getDepthCount(compositeElementPanel.m_components);
			compositeElementPanel.getHTML(toWriteTo, colSpan + 1, useColor, false);
		}
	}

	public void prePropertyChange(PropertyEvent propertyevent) {
	}

	@Override
	public void propertyChanging(PropertyEvent propertyevent) {
	}

	@Override
	public void propertyChanged(PropertyEvent propertyevent) {
	}

	protected void startListeningToTree(Element element) {
		if (element == null) {
			;
		}
	}

	protected void stopListeningToTree(Element element) {
		if (element == null) {
			;
		}
	}

	protected void clearAllListening() {
		if (compositeElementPanel != null) {
			compositeElementPanel.clean();
		}
	}

	@Override
	public void stateChanging(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldLoading(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldUnLoading(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldStarting(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldStopping(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldPausing(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldSaving(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void stateChanged(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldLoaded(AuthoringToolStateChangedEvent ev) {
		updateGui();
	}

	@Override
	public void worldUnLoaded(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldStarted(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldStopped(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldPaused(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	@Override
	public void worldSaved(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {
	}

	public final String editorName = "Composite Editor";
	protected Element elementBeingEdited;
	protected MainCompositeElementPanel compositeElementPanel;
	protected JPanel mainElementContainer;
	protected JPanel buttonPanel;
	protected Color mainColor;
	protected static Configuration authoringToolConfig;
	protected AuthoringTool authoringTool;
	public static boolean IS_JAVA = false;

	static {
		authoringToolConfig = Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage());
	}
}