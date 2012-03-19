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

/**
 * @author Jason Pratt
 */
public class AliceTabbedPaneUI extends javax.swing.plaf.metal.MetalTabbedPaneUI {
	// protected java.awt.Color behindTheTabsColor = new java.awt.Color( 255,
	// 230, 180 );
	protected java.awt.Color defaultTabForeground;

	public AliceTabbedPaneUI() {
		setTabAreaInsets(new java.awt.Insets(0, 0, 0, 0));
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectColor = java.awt.Color.white;
		selectHighlight = java.awt.Color.black;
		tabAreaBackground = java.awt.Color.white;
		darkShadow = java.awt.Color.black;
		focus = new java.awt.Color(255, 255, 255, 0); // don't paint focus
		highlight = java.awt.Color.darkGray;

		// new for 1.4
		contentBorderInsets = new java.awt.Insets(1, 1, 1, 1);

		defaultTabForeground = javax.swing.UIManager.getColor("TabbedPane.foreground");
	}

	public void setTabAreaInsets(java.awt.Insets insets) {
		tabAreaInsets = insets;
	}

	@Override
	public void update(java.awt.Graphics g, javax.swing.JComponent c) {
		int tabPlacement = tabPane.getTabPlacement();
		java.awt.Insets insets = c.getInsets();
		java.awt.Dimension size = c.getSize();

		// match tab color to the component's background color
		java.awt.Component selectedComponent = tabPane.getSelectedComponent();
		if (selectedComponent != null) {
			selectColor = tabAreaBackground = selectedComponent.getBackground();
		}

		g.setColor(tabAreaBackground);
		int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
		g.fillRect(insets.left, insets.top + tabAreaHeight, size.width - insets.right - insets.left, size.height - tabAreaHeight);

		paint(g, c);
	}

	/**
	 * overridden for tab fonts
	 */

	@Override
	protected void paintTab(java.awt.Graphics g, int tabPlacement, java.awt.Rectangle[] rects, int tabIndex, java.awt.Rectangle iconRect, java.awt.Rectangle textRect) {
		java.awt.Rectangle tabRect = rects[tabIndex];
		int selectedIndex = tabPane.getSelectedIndex();
		boolean isSelected = selectedIndex == tabIndex;
		paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected);
		paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected);

		// for dark tabs, draw text in white
		if (isSelected && tabPane.getSelectedComponent() != null) {
			java.awt.Color background = tabPane.getSelectedComponent().getBackground();
			int brightness = (background.getRed() + background.getGreen() + background.getBlue()) / 3;
			if (brightness < 128) {
				if (!tabPane.getForegroundAt(tabIndex).equals(java.awt.Color.white)) {
					tabPane.setForegroundAt(tabIndex, java.awt.Color.white);
				}
			} else {
				if (!tabPane.getForegroundAt(tabIndex).equals(defaultTabForeground)) {
					tabPane.setForegroundAt(tabIndex, null);
				}
			}
		} else {
			if (!tabPane.getForegroundAt(tabIndex).equals(defaultTabForeground)) {
				tabPane.setForegroundAt(tabIndex, null);
			}
		}

		String title = tabPane.getTitleAt(tabIndex);
		java.awt.Font font = tabPane.getFont();
		if (isSelected) {
			font = font.deriveFont(java.awt.Font.BOLD);
		} else {
			font = font.deriveFont(java.awt.Font.PLAIN); // size 12 is a hack
															// here
		}
		java.awt.FontMetrics metrics = g.getFontMetrics(font);
		javax.swing.Icon icon = getIconForTab(tabIndex);
		layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
		paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
		paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
		paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
	}

	// public void paint( java.awt.Graphics g, javax.swing.JComponent c ) {
	// int tabPlacement = tabPane.getTabPlacement();
	// java.awt.Insets insets = c.getInsets();
	// java.awt.Dimension size = c.getSize();
	//
	// g.setColor( behindTheTabsColor );
	// g.fillRect( insets.left, insets.top, size.width - insets.right -
	// insets.left, calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight
	// ) );
	//
	// super.paint( g, c );
	// }

	public int getTabAreaHeight() {
		java.awt.Insets tabAreaInsets = getTabAreaInsets(tabPane.getTabPlacement());
		int runCount = tabPane.getTabRunCount();
		int tabRunOverlay = getTabRunOverlay(tabPane.getTabPlacement());
		return runCount > 0 ? runCount * (maxTabHeight - tabRunOverlay) + tabRunOverlay + tabAreaInsets.top + tabAreaInsets.bottom : 0;
	}

	public int getTabAreaHeightIgnoringInsets() {
		int runCount = tabPane.getTabRunCount();
		int tabRunOverlay = getTabRunOverlay(tabPane.getTabPlacement());
		return runCount > 0 ? runCount * (maxTabHeight - tabRunOverlay) + tabRunOverlay : 0;
	}

	@Override
	protected java.awt.FontMetrics getFontMetrics() {
		java.awt.Font font = tabPane.getFont().deriveFont(java.awt.Font.BOLD);
		return java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);
	}
}
