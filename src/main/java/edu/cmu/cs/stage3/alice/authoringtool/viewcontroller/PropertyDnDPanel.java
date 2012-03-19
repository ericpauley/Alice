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
public class PropertyDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected javax.swing.JLabel nameLabel = new javax.swing.JLabel();
	protected java.util.Vector popupStructure = new java.util.Vector();

	public PropertyDnDPanel() {
		setBackground(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("propertyDnDPanel"));

		add(nameLabel, java.awt.BorderLayout.CENTER);
		addDragSourceComponent(nameLabel);

		java.awt.event.MouseListener mouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {

			@Override
			public void popupResponse(java.awt.event.MouseEvent ev) {
				PropertyDnDPanel.this.updatePopupStructure();
				edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, PropertyDnDPanel.this, ev.getX(), ev.getY());
			}
		};
		addMouseListener(mouseListener);
		nameLabel.addMouseListener(mouseListener);
		grip.addMouseListener(mouseListener);
	}

	public void set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.core.Property property) {
		this.authoringTool = authoringTool;
		this.property = property;
		nameLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property));
		String iconName = "types/" + property.getValueClass().getName();
		javax.swing.ImageIcon icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(iconName);
		if (icon == null) {
			icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("types/other");
		}
		if (icon != null) {
			// nameLabel.setIcon( icon );
		}
		setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(property));
	}

	public void updatePopupStructure() {
		popupStructure.clear();

		final edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel = authoringTool.getWatcherPanel();

		if (watcherPanel.isPropertyBeingWatched(property)) {
			popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("stop watching this property", new Runnable() {
				@Override
				public void run() {
					watcherPanel.removePropertyBeingWatched(property);
				}
			}));
		} else {
			popupStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("watch this property", new Runnable() {
				@Override
				public void run() {
					watcherPanel.addPropertyToWatch(property);
				}
			}));
		}
	}

	@Override
	public void goToSleep() {
	}
	@Override
	public void wakeUp() {
	}

	@Override
	public void clean() {
		setTransferable(null);
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}
}
