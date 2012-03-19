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

package edu.cmu.cs.stage3.awt;

/**
 * @author culyba
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */

public class DynamicFlowLayout extends java.awt.FlowLayout {

	private java.awt.Dimension lastPreferredSize;
	private java.awt.Component anchorComponent;
	private java.awt.Component ownerComponent;
	private int anchorConstant = 0;
	private Class anchorClass;

	public DynamicFlowLayout(int align, java.awt.Component anchor, Class anchorClass) {
		this(align, anchor, anchorClass, 0);
	}

	public DynamicFlowLayout(int align, java.awt.Component anchor, Class anchorClass, int anchorConstant) {
		super(align);
		anchorComponent = anchor;
		this.anchorClass = anchorClass;
		this.anchorConstant = anchorConstant;
	}

	@Override
	public void layoutContainer(java.awt.Container target) {
		synchronized (target.getTreeLock()) {
			java.awt.Insets insets = target.getInsets();
			int hgap = getHgap();
			int vgap = getVgap();
			if (lastPreferredSize == null) {
				lastPreferredSize = preferredLayoutSize(target);
			}
			int maxwidth = lastPreferredSize.width;
			int nmembers = target.getComponentCount();
			int x = 0, y = insets.top + vgap;
			int rowh = 0, start = 0;

			boolean ltr = target.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < nmembers; i++) {
				java.awt.Component m = target.getComponent(i);
				if (m.isVisible()) {
					java.awt.Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);
					if (x == 0 || x + d.width <= maxwidth) {
						if (x > 0) {
							x += hgap;
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, i, ltr);
						x = d.width;
						y += vgap + rowh;
						rowh = d.height;
						start = i;
					}
				}
			}
			moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr);
		}
	}

	private void moveComponents(java.awt.Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr) {
		synchronized (target.getTreeLock()) {
			switch (getAlignment()) {
				case LEFT :
					x += ltr ? 0 : width;
					break;
				case CENTER :
					x += width / 2;
					break;
				case RIGHT :
					x += ltr ? width : 0;
					break;
				case LEADING :
					break;
				case TRAILING :
					x += width;
					break;
			}
			for (int i = rowStart; i < rowEnd; i++) {
				java.awt.Component m = target.getComponent(i);
				if (target.isVisible()) {
					if (ltr) {
						m.setLocation(x, y + (height - m.getHeight()) / 2);
					} else {
						m.setLocation(target.getWidth() - x - m.getWidth(), y + (height - m.getHeight()) / 2);
					}
					x += m.getWidth() + getHgap();
				}
			}
		}
	}

	@Override
	public java.awt.Dimension preferredLayoutSize(java.awt.Container target) {
		java.awt.Insets insets = target.getInsets();
		int hgap = getHgap();
		int vgap = getVgap();
		if (anchorComponent == null) {
			anchorComponent = getAnchor(target);
		}
		int maxwidth = 0;
		if (anchorComponent == null) {
			maxwidth = target.getWidth() - (insets.left + insets.right + hgap * 2);
		} else {
			maxwidth = anchorComponent.getWidth() - (insets.left + insets.right + hgap * 2) - anchorConstant;
		}
		// maxwidth = (ownerComponent.getWidth() - (insets.left + insets.right +
		// hgap*2));
		// System.out.println(ownerComponent+", "+target);
		// System.out.println(target.getWidth()+", "+maxwidth);
		// ownerComponent.setBackground(java.awt.Color.red);
		int nmembers = target.getComponentCount();
		int x = 0, y = insets.top + vgap;
		int rowh = 0;

		if (maxwidth < 0) {
			maxwidth = 0;
			for (int i = 0; i < nmembers; i++) {
				java.awt.Component m = target.getComponent(i);
				if (m.isVisible()) {
					java.awt.Dimension d = m.getPreferredSize();
					y = Math.max(y, d.height);
					if (d.width > 0 && d.height > 0) {
						if (maxwidth > 0) {
							maxwidth += hgap;
						}
						maxwidth += d.width;
					}
				}
			}
		} else {
			boolean ltr = target.getComponentOrientation().isLeftToRight();
			for (int i = 0; i < nmembers; i++) {
				java.awt.Component m = target.getComponent(i);
				if (m.isVisible()) {
					java.awt.Dimension d = m.getPreferredSize();
					// System.out.println("on "+m+"\n dimensions: "+d);
					// System.out.println("x is currently "+x);
					// // if (d.width > 0 && d.height > 0){
					// System.out.println("expected :"+(x + d.width)+
					// " < "+maxwidth+" ?");
					// // m.setSize(d.width, d.height);
					if (x == 0 || x + d.width <= maxwidth) {
						// System.out.println("it fits");
						if (x > 0) {
							x += hgap;
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						// System.out.println("too big");
						x = d.width;
						y += vgap + rowh;
						rowh = d.height;
						// System.out.println("rowh is now "+rowh+", y is now "+y);
					}
					// }
					// System.out.println("x is now: "+x);
				}
			}
		}
		// System.out.println("returning: "+(new java.awt.Dimension(maxwidth,
		// y+rowh+vgap)));
		// System.out.println("DONE GETTING SIZE\n");
		lastPreferredSize = new java.awt.Dimension(maxwidth, y + rowh + vgap);
		return lastPreferredSize;
	}

	@Override
	public java.awt.Dimension minimumLayoutSize(java.awt.Container target) {
		return preferredLayoutSize(target);
	}

	private java.awt.Component getAnchor(java.awt.Component current) {
		if (current == null || anchorClass.isAssignableFrom(current.getClass())) {
			return current;
		} else {
			return getAnchor(current.getParent());
		}
	}
}
