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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

/**
 * adapted from javax.swing.plaf.basic.BasicMenuUI to improve on popup menu
 * behavior.
 * 
 * @author Jason Pratt
 */
public class AliceMenuUI extends javax.swing.plaf.basic.BasicMenuUI {

	@Override
	protected MouseInputListener createMouseInputListener(JComponent c) {
		return new AliceMouseInputHandler();
	}

	protected class AliceMouseInputHandler implements MouseInputListener {
		@Override
		public void mousePressed(MouseEvent e) {
			JMenu menu = (JMenu) menuItem;

			if (!menu.isEnabled()) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();

			if (menu.isTopLevelMenu()) {
				if (menu.isSelected()) {
					manager.clearSelectedPath();
				} else {
					Container cnt = menu.getParent();

					if (cnt != null && cnt instanceof JMenuBar) {
						MenuElement[] me = new MenuElement[2];
						me[0] = (MenuElement) cnt;
						me[1] = menu;
						manager.setSelectedPath(me);
					}
				}
			}

			MenuElement[] selectedPath = manager.getSelectedPath();

			if (!(selectedPath.length > 0 && selectedPath[selectedPath.length - 1] == menu.getPopupMenu())) {
				if (menu.isTopLevelMenu() || menu.getDelay() == 0) {
					MenuElement[] newPath = new MenuElement[selectedPath.length + 1];
					System.arraycopy(selectedPath, 0, newPath, 0, selectedPath.length);
					newPath[selectedPath.length] = menu.getPopupMenu();
					manager.setSelectedPath(newPath);
				} else {
					setupPostTimer(menu);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JMenu menu = (JMenu) menuItem;

			if (!menu.isEnabled()) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			manager.processMouseEvent(e);

			if (!e.isConsumed()) {
				manager.clearSelectedPath();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JMenu menu = (JMenu) menuItem;

			if (!menu.isEnabled()) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			MenuElement[] selectedPath = manager.getSelectedPath();

			if (!menu.isTopLevelMenu()) {
				if (menu.getDelay() == 0) {
					MenuElement[] path = getPath();
					MenuElement[] newPath = new MenuElement[getPath().length + 1];
					System.arraycopy(path, 0, newPath, 0, path.length);
					newPath[path.length] = menu.getPopupMenu();
					manager.setSelectedPath(newPath);
					// System.out.print( "0 setSelectedPath: " );
					// printPath( newPath );
				} else {
					manager.setSelectedPath(getPath());
					setupPostTimer(menu);
				}
			} else {
				if (selectedPath.length > 0 && selectedPath[0] == menu.getParent()) {
					MenuElement[] newPath = new MenuElement[3];

					// A top level menu's parent is by definition
					// a JMenuBar
					newPath[0] = (MenuElement) menu.getParent();
					newPath[1] = menu;
					newPath[2] = menu.getPopupMenu();
					manager.setSelectedPath(newPath);
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			JMenu menu = (JMenu) menuItem;

			if (!menu.isEnabled()) {
				return;
			}

			MenuSelectionManager.defaultManager().processMouseEvent(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	@Override
	protected void setupPostTimer(JMenu menu) {
		Timer timer = new Timer(menu.getDelay(), new AlicePostAction(menu));
		timer.setRepeats(false);
		timer.start();
	}

	private static class AlicePostAction extends AbstractAction {
		JMenu menu;

		AlicePostAction(JMenu menu) {
			this.menu = menu;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();

			MenuElement[] path = ((javax.swing.plaf.basic.BasicMenuUI) menu.getUI()).getPath(); // hack
			MenuElement[] newPath = new MenuElement[path.length + 1];
			path[path.length] = menu.getPopupMenu();
			defaultManager.setSelectedPath(newPath);
		}

		@Override
		public boolean isEnabled() {
			return menu.getModel().isEnabled();
		}
	}

	@Override
	public MenuElement[] getPath() {
		java.util.Vector path = new java.util.Vector();
		MenuElement me = menuItem;
		while (me instanceof MenuElement) {
			path.add(0, me);
			if (me instanceof JPopupMenu) {
				Object o = ((JPopupMenu) me).getInvoker();
				if (o instanceof MenuElement && o != me) {
					me = (MenuElement) o;
				} else {
					me = null;
				}
			} else if (me instanceof JMenuItem) {
				Object o = ((JMenuItem) me).getParent();
				if (o instanceof MenuElement && o != me) {
					me = (MenuElement) o;
				} else {
					me = null;
				}
			} else {
				me = null;
			}
		}

		return (MenuElement[]) path.toArray(new MenuElement[0]);
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