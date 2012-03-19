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

/**
 * @author Jason Pratt
 */
public class CallToUserDefinedQuestionPrototypeDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0);
	protected RefreshListener refreshListener = new RefreshListener();
	protected boolean nameEditorShouldBeInvoked = false;
	protected MouseListener mouseListener = new MouseListener();
	protected ElementNamePropertyViewController nameViewController;

	public CallToUserDefinedQuestionPrototypeDnDPanel() {
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestion"));
		subPanel.setLayout(new java.awt.GridBagLayout());
		subPanel.setOpaque(false);
		subPanel.setBorder(null);
		this.add(subPanel, java.awt.BorderLayout.CENTER);
		addDragSourceComponent(subPanel);
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype) {
		clean();

		this.callToUserDefinedQuestionPrototype = callToUserDefinedQuestionPrototype;
		setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(callToUserDefinedQuestionPrototype));
		startListening();
		refreshGUI();
	}

	protected void startListening() {
		if (callToUserDefinedQuestionPrototype != null) {
			callToUserDefinedQuestionPrototype.startListening();
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
			if (question != null) {
				question.requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
				question.keywordFormalParameters.addObjectArrayPropertyListener(refreshListener);
				Object[] variables = question.requiredFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.addPropertyListener(refreshListener);
				}
				variables = question.keywordFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.addPropertyListener(refreshListener);
				}
				question.name.addPropertyListener(refreshListener);
				// addMouseListener( mouseListener );
				// subPanel.addMouseListener( mouseListener );
			}
		}
	}

	protected void stopListening() {
		if (callToUserDefinedQuestionPrototype != null) {
			callToUserDefinedQuestionPrototype.stopListening();
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
			if (question != null) {
				question.requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
				question.keywordFormalParameters.removeObjectArrayPropertyListener(refreshListener);
				Object[] variables = question.requiredFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.removePropertyListener(refreshListener);
				}
				variables = question.keywordFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.removePropertyListener(refreshListener);
				}
				question.name.removePropertyListener(refreshListener);
				// removeMouseListener( mouseListener );
				// subPanel.removeMouseListener( mouseListener );
			}
		}
	}

	@Override
	public void goToSleep() {
		stopListening();
	}

	@Override
	public void wakeUp() {
		startListening();
	}

	@Override
	public void clean() {
		stopListening();
		callToUserDefinedQuestionPrototype = null;
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

	public void editName() {
		nameEditorShouldBeInvoked = true;
		refreshGUI();
	}

	public void refreshGUI() {
		java.awt.Component[] components = subPanel.getComponents();
		for (Component component2 : components) {
			// components[i].removeMouseListener( mouseListener );
		}
		if (nameViewController != null) {
			// nameViewController.removeMouseListener( mouseListener );
			removeDragSourceComponent(nameViewController);
		}
		subPanel.removeAll();

		if (callToUserDefinedQuestionPrototype != null) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
			constraints.gridx = 0;
			nameViewController = null;
			if (question != null) {
				nameViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getElementNamePropertyViewController(question);
				nameViewController.setBorder(null);
				nameViewController.setOpaque(false);
				subPanel.add(nameViewController, constraints);
				// nameViewController.addMouseListener( mouseListener );
				addDragSourceComponent(nameViewController);
			} else {
				subPanel.add(new javax.swing.JLabel("<null question>"), constraints);
			}
			constraints.gridx++;
			subPanel.add(javax.swing.Box.createHorizontalStrut(6), constraints);
			constraints.gridx++;
			if (question != null && question.requiredFormalParameters.size() > 0) {
				Object[] variables = question.requiredFormalParameters.getArrayValue();
				for (Object variable : variables) { // TODO: include type info?
					addTile(((edu.cmu.cs.stage3.alice.core.Variable) variable).name.getStringValue());
					constraints.gridx++;
				}
			}

			// TODO: keyword parameters
			if (nameEditorShouldBeInvoked && nameViewController != null) {
				nameViewController.editValue();
				nameEditorShouldBeInvoked = false;
			}
		} else {
			subPanel.add(new javax.swing.JLabel("<null callToUserDefinedQuestionPrototype>"), constraints);
		}

		revalidate();
		repaint();
	}

	public void addTile(String text) {
		edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel tilePanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
		tilePanel.setLayout(new java.awt.BorderLayout());
		tilePanel.setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("prototypeParameter"));
		javax.swing.JLabel tileLabel = new javax.swing.JLabel(text);
		tileLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 2));
		tilePanel.add(tileLabel, java.awt.BorderLayout.CENTER);
		subPanel.add(tilePanel, constraints);
		// tilePanel.addMouseListener( mouseListener );
		addDragSourceComponent(tilePanel);
	}

	class MouseListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {

		@Override
		public void singleClickResponse(java.awt.event.MouseEvent ev) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(callToUserDefinedQuestionPrototype.getActualQuestion(), CallToUserDefinedQuestionPrototypeDnDPanel.this);
		}
	}

	class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			refreshGUI();
		}
		@Override
		public void objectArrayPropertyChanging(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}
		@Override
		public void objectArrayPropertyChanged(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev.getItem();
				variable.name.addPropertyListener(this);
			} else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev.getItem();
				variable.name.removePropertyListener(this);
			}
			refreshGUI();
		}
	};
}
