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
public class MultiColumnPopupLayout implements java.awt.LayoutManager {
	protected java.awt.Dimension screenSize;

	protected void updateInfo() {
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar
	}

	public java.awt.Dimension preferredLayoutSize( java.awt.Container parent ) {
		updateInfo();

		synchronized( parent.getTreeLock() ) {
			java.awt.Insets insets = parent.getInsets();
			int count = parent.getComponentCount();

			int numCols = 1;
			int totalWidth = 0;
			int totalHeight = 0;
			int heightSoFar = 0;
			int maxWidth = 0;

			for( int i = 0; i < count; i++ ) {
				java.awt.Component comp = parent.getComponent( i );
				java.awt.Dimension d = comp.getPreferredSize();

				// all menuItems use same width for the purpose of accelerator hints
				// getPreferredSize doesn't seem to reflect this consistently, though
				maxWidth = Math.max( maxWidth, d.width );

				heightSoFar += d.height;
				if( heightSoFar > screenSize.height ) {
					numCols++;
					totalHeight = Math.max( totalHeight, heightSoFar - d.height );
					heightSoFar = d.height;
				}
			}

			if( totalHeight == 0 ) {
				totalHeight = heightSoFar;
			}

			totalWidth += (maxWidth + 1)*numCols - 1;

			java.awt.Dimension d = new java.awt.Dimension( insets.left + insets.right + totalWidth, insets.top + insets.bottom + totalHeight );
//			System.out.println( "preferredSize: " + d );
			return d;
		}
	}

	public java.awt.Dimension minimumLayoutSize( java.awt.Container parent ) {
		return preferredLayoutSize( parent );
	}

	public void layoutContainer( java.awt.Container parent ) {
		synchronized( parent.getTreeLock() ) {
			java.awt.Insets insets = parent.getInsets();
			java.awt.Dimension parentSize = parent.getSize();

			int count = parent.getComponentCount();

			int x = insets.left;
			int y = insets.top;
			int w = 0;
			int h = 0;

			int widthThisColumn = 0;
			int heightSoFar = 0;

			java.util.ArrayList oneColumn = new java.util.ArrayList();

			for( int i = 0; i < count; i++ ) {
				java.awt.Component comp = parent.getComponent( i );
				java.awt.Dimension d = comp.getPreferredSize();

				heightSoFar += d.height;
				if( (heightSoFar > parentSize.height) && (! oneColumn.isEmpty()) ) { // full column; lay it out
					w = widthThisColumn;
					for( java.util.Iterator iter = oneColumn.iterator(); iter.hasNext(); ) {
						java.awt.Component c = (java.awt.Component)iter.next();
						h = c.getPreferredSize().height;
						c.setBounds( x, y, w, h );
						y += h;
					}

					oneColumn.clear();
					x += widthThisColumn + 1;
					y = insets.top;
					w = 0;
					h = 0;

					heightSoFar = d.height;
					widthThisColumn = 0;
				}

				oneColumn.add( comp );
				widthThisColumn = Math.max( widthThisColumn, d.width );
			}

			if( ! oneColumn.isEmpty() ) { // last column
				w = widthThisColumn;
				for( java.util.Iterator iter = oneColumn.iterator(); iter.hasNext(); ) {
					java.awt.Component c = (java.awt.Component)iter.next();
					h = c.getPreferredSize().height;
					c.setBounds( x, y, w, h );
					y += h;
				}
			}
		}
	}

	public void addLayoutComponent( String name, java.awt.Component comp ) {}
	public void removeLayoutComponent( java.awt.Component comp ) {}
}