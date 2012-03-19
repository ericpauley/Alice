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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Window;

import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * designed to improve on popup menu behavior.
 * 
 * @author Jason Pratt
 */
public class AlicePopupMenu extends JPopupMenu {
	protected int millisecondDelay = 400;

	protected javax.swing.Timer setPopupVisibleTrueTimer;
	protected javax.swing.Timer setPopupVisibleFalseTimer;
	AliceMenu invokerMenu;

	public AlicePopupMenu() {
		setLayout(new MultiColumnPopupLayout());
	}

	// HACK for gray boxes bug; this can go away in 1.4

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		java.awt.Container popup = null;
		if (b) {
			popup = getParent();
			if (popup != null) { // content pane
				popup = popup.getParent();
			}
			if (popup != null) { // layered pane
				popup = popup.getParent();
			}
			if (popup != null) { // root pane
				popup = popup.getParent();
			}
			if (popup instanceof java.awt.Window) {
				java.awt.Window[] windows = ((java.awt.Window) popup).getOwnedWindows();
				for (Window window : windows) {
					window.setVisible(false);
				}
			}
		}
	}

	@Override
	public void menuSelectionChanged(boolean isIncluded) {
		if (setPopupVisibleTrueTimer == null) {
			setPopupVisibleTrueTimer = new javax.swing.Timer(millisecondDelay, new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					invokerMenu.setPopupMenuVisible(true);
				}
			});
			setPopupVisibleTrueTimer.setRepeats(false);
		}
		if (setPopupVisibleFalseTimer == null) {
			setPopupVisibleFalseTimer = new javax.swing.Timer(millisecondDelay, new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					invokerMenu.setPopupMenuVisible(false);
				}
			});
			setPopupVisibleFalseTimer.setRepeats(false);
		}
		if (setPopupVisibleTrueTimer.isRunning()) {
			setPopupVisibleTrueTimer.stop();
		}
		if (setPopupVisibleFalseTimer.isRunning()) {
			setPopupVisibleFalseTimer.stop();
		}

		if (getInvoker() instanceof AliceMenu) {
			boolean allGone = javax.swing.MenuSelectionManager.defaultManager().getSelectedPath().length == 0;
			invokerMenu = (AliceMenu) getInvoker();

			if (isIncluded) {
				setPopupVisibleTrueTimer.start();
			} else {
				if (allGone) {
					setVisible(false);
				} else {
					setPopupVisibleFalseTimer.start();
				}
			}
		}
		if (isPopupMenu() && !isIncluded) {
			setVisible(false);
		}
	}

	private boolean isPopupMenu() {
		return getInvoker() != null && !(getInvoker() instanceof AliceMenu);
	}

	@Override
	public void show(java.awt.Component invoker, int x, int y) {
		super.show(invoker, x, y);
		PopupMenuUtilities.ensurePopupIsOnScreen(this);
	}

	public void printPath(javax.swing.MenuElement[] path) {
		System.out.print("path [");
		for (MenuElement me : path) {
			if (me instanceof javax.swing.JMenu) {
				System.out.print(((javax.swing.JMenu) me).getText() + ", ");
			} else if (me instanceof javax.swing.JPopupMenu) {
				Object invoker = ((javax.swing.JPopupMenu) me).getInvoker();
				if (invoker instanceof javax.swing.JMenu) {
					System.out.print(((javax.swing.JMenu) invoker).getText() + ".popupMenu, ");
				} else {
					System.out.print("anonymous popupMenu, ");
				}
			} else {
				System.out.print(me.getClass().getName());
			}
		}
		System.out.println("]");
	}
}