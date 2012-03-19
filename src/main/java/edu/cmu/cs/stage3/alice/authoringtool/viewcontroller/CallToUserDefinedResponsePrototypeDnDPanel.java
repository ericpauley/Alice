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
public class CallToUserDefinedResponsePrototypeDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
	protected java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0);
	protected RefreshListener refreshListener = new RefreshListener();
	protected boolean nameEditorShouldBeInvoked = false;
	protected MouseListener mouseListener = new MouseListener();
	protected ElementNamePropertyViewController nameViewController;

	public CallToUserDefinedResponsePrototypeDnDPanel() {
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponse"));
		subPanel.setLayout(new java.awt.GridBagLayout());
		subPanel.setOpaque(false);
		subPanel.setBorder(null);
		this.add(subPanel, java.awt.BorderLayout.CENTER);
		addDragSourceComponent(subPanel);
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype) {
		clean();

		this.callToUserDefinedResponsePrototype = callToUserDefinedResponsePrototype;
		setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(callToUserDefinedResponsePrototype));
		startListening();
		refreshGUI();
	}

	protected void startListening() {
		if (callToUserDefinedResponsePrototype != null) {
			callToUserDefinedResponsePrototype.startListening();
			edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse response = callToUserDefinedResponsePrototype.getActualResponse();
			if (response != null) {
				response.requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
				response.keywordFormalParameters.addObjectArrayPropertyListener(refreshListener);
				Object[] variables = response.requiredFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.addPropertyListener(refreshListener);
				}
				variables = response.keywordFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.addPropertyListener(refreshListener);
				}
				response.name.addPropertyListener(refreshListener);
				// addMouseListener( mouseListener );
				// subPanel.addMouseListener( mouseListener );
			}
		}
	}

	protected void stopListening() {
		if (callToUserDefinedResponsePrototype != null) {
			callToUserDefinedResponsePrototype.stopListening();
			edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse response = callToUserDefinedResponsePrototype.getActualResponse();
			if (response != null) {
				response.requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
				response.keywordFormalParameters.removeObjectArrayPropertyListener(refreshListener);
				Object[] variables = response.requiredFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.removePropertyListener(refreshListener);
				}
				variables = response.keywordFormalParameters.getArrayValue();
				for (Object variable : variables) {
					((edu.cmu.cs.stage3.alice.core.Variable) variable).name.removePropertyListener(refreshListener);
				}
				response.name.removePropertyListener(refreshListener);
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
		callToUserDefinedResponsePrototype = null;
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
			removeDragSourceComponent(component2);
		}
		if (nameViewController != null) {
			// nameViewController.removeMouseListener( mouseListener );
			removeDragSourceComponent(nameViewController);
		}
		subPanel.removeAll();

		if (callToUserDefinedResponsePrototype != null) {
			edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse response = callToUserDefinedResponsePrototype.getActualResponse();
			constraints.gridx = 0;
			nameViewController = null;
			if (response != null) {
				nameViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getElementNamePropertyViewController(response);
				nameViewController.setBorder(null);
				nameViewController.setOpaque(false);
				subPanel.add(nameViewController, constraints);
				// nameViewController.addMouseListener( mouseListener );
				addDragSourceComponent(nameViewController);
			} else {
				subPanel.add(new javax.swing.JLabel("<null response>"), constraints);
			}
			constraints.gridx++;
			subPanel.add(javax.swing.Box.createHorizontalStrut(6), constraints);
			constraints.gridx++;
			if (response != null && response.requiredFormalParameters.size() > 0) {
				Object[] variables = response.requiredFormalParameters.getArrayValue();
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
			subPanel.add(new javax.swing.JLabel("<null callToUserDefinedResponsePrototype>"), constraints);
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
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(callToUserDefinedResponsePrototype.getActualResponse(), CallToUserDefinedResponsePrototypeDnDPanel.this);
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
