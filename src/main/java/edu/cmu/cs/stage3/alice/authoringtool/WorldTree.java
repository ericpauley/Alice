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

package edu.cmu.cs.stage3.alice.authoringtool;

public class WorldTree extends javax.swing.JTree {
	protected java.awt.Point cursorLocation;
	protected boolean dropLinesActive;
	protected boolean showDropLines = false;
	protected int totalChildIndent;
	protected java.awt.Insets insets;
	protected int depthOffset;
	protected int legBuffer = 8;
	protected javax.swing.tree.TreePath fromPath = null;
	protected javax.swing.tree.TreePath toPath = null;

	public WorldTree() {
		super();
		init();
	}

	public WorldTree(javax.swing.tree.TreeModel model) {
		super(model);
		init();
	}

	private void init() {
		cursorLocation = new java.awt.Point(0, 0);
		javax.swing.plaf.basic.BasicTreeUI ui = (javax.swing.plaf.basic.BasicTreeUI) getUI();
		totalChildIndent = ui.getLeftChildIndent() + ui.getRightChildIndent();
		insets = getInsets();
		updateVars();
		dropLinesActive = true;
		needChange();
		dropLinesActive = false;
		getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);

		setToolTipText("<html><font face=arial size=-1>Object Tree<p><p>The Object Tree shows all<p>of the objects in the world.<p>Some objects have parts.</font></html>");
		javax.swing.ToolTipManager.sharedInstance().registerComponent(this);
	}

	@Override
	public String getToolTipText(java.awt.event.MouseEvent ev) {
		String tip = super.getToolTipText(ev);
		if (tip != null) {
			return tip;
		} else {
			return getToolTipText();
		}
	}

	protected void paintLines(java.awt.Graphics g, java.awt.Rectangle clipBounds, java.awt.Insets insets, javax.swing.tree.TreePath from, javax.swing.tree.TreePath to) {
		int lineX = (from.getPathCount() - 1 + depthOffset) * totalChildIndent + 8 + insets.left;

		// Swing component bounds don't seem to be well-defined
		int clipLeft = 0;
		int clipRight = clipBounds.width - 1;

		if (lineX > clipLeft && lineX < clipRight) {
			int clipTop = 0;
			int clipBottom = clipBounds.height - 1;
			java.awt.Rectangle fromBounds = getPathBounds(from);
			java.awt.Rectangle toBounds = getPathBounds(to);

			int top;
			int bottom;
			if (fromBounds == null) {
				top = Math.max(insets.top, clipTop);
			} else {
				top = Math.max(fromBounds.y + fromBounds.height - legBuffer, clipTop);
			}
			if (toBounds == null) {
				bottom = Math.min(insets.bottom, clipBottom);
			} else {
				bottom = Math.min(toBounds.y + toBounds.height, clipBottom);
			}

			g.drawLine(lineX, top, lineX, bottom);
			g.drawLine(lineX, bottom, clipRight, bottom);
		}
	}

	public void setCursorLocation(java.awt.Point p) {
		cursorLocation = p;
		if (needChange()) {
			repaint();
		}

		autoscrollIfNecessary();
	}

	// calculates the drop position as a side-effect
	synchronized protected boolean needChange() {
		if (dropLinesActive && cursorLocation != null) {
			// int lastRow = getRowCount() - 1 - (isRootVisible() ? 0 : 1);
			// int lastRow = getRowCount() - 1;

			// The following method for determining the lastRow is just plain
			// wrong.
			// However, getRowCount does not act consistently for me.
			int lastRow = 0;
			while (getPathForRow(lastRow) != null) {
				// DEBUG System.out.println( lastRow + ": " + getPathForRow(
				// lastRow ) );
				lastRow++;
			}
			lastRow--;
			if (lastRow > 0 && getPathForRow(lastRow).equals(getPathForRow(0))) {
				lastRow--;
			}

			// DEBUG System.out.println( "lastRow: " + lastRow );
			java.awt.Rectangle lastBounds = getRowBounds(lastRow);
			// DEBUG System.out.println( "lastBounds: " + lastRow );
			int bottomy = 0;
			if (lastBounds != null) {
				bottomy = lastBounds.y + lastBounds.height;
			}

			javax.swing.tree.TreePath pathToDrawTo;
			javax.swing.tree.TreePath pathToDrawFrom;

			if (cursorLocation.y > bottomy) { // if we're below the last item,
												// become last child of root
				pathToDrawTo = getPathForRow(lastRow);
				pathToDrawFrom = new javax.swing.tree.TreePath(getModel().getRoot());
			} else {
				int cursorLocationRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y);
				// DEBUG System.out.println( "cursorLocation.y: " +
				// cursorLocation.y );
				pathToDrawTo = getPathForRow(cursorLocationRow); // always add
																	// below the
																	// node the
																	// cursor is
																	// over
				// DEBUG System.out.println( "pathToDrawTo: " + pathToDrawTo );

				Object toNode = pathToDrawTo.getLastPathComponent();
				if (getModel().getChildCount(toNode) != 0 && isExpanded(pathToDrawTo)) { // if
																							// the
																							// node
																							// we're
																							// over
																							// has
																							// visible
																							// children,
																							// then
																							// we
																							// can't
																							// allow
																							// a
																							// higher
																							// level
																							// parent
					pathToDrawFrom = pathToDrawTo;
				} else { // if it doesn't have children, then we can pick a
							// higher level parent
					pathToDrawFrom = pathToDrawTo; // start at the current depth
					java.awt.Rectangle bounds = getRowBounds(cursorLocationRow);
					// DEBUG System.out.println( "boundsToDrawTo: " + bounds );
					if (bounds.y + bounds.height - cursorLocation.y < legBuffer) { // if
																					// we're
																					// near
																					// the
																					// bottom
																					// of
																					// the
																					// current
																					// row,
																					// calculate
																					// propert
																					// parent
																					// based
																					// on
																					// cursor's
																					// horizontal
																					// location
						int cursorLevel = (int) ((cursorLocation.x - insets.left - 8 + totalChildIndent / 2.0) / totalChildIndent);
						int maxLevel = pathToDrawTo.getPathCount() - 1; // don't
																		// go
																		// deeper
																		// than
																		// we
																		// already
																		// are
						int minLevel;
						if (cursorLocationRow == lastRow) {
							minLevel = 1; // don't go shallower than the root
						} else {
							javax.swing.tree.TreePath next = getPathForRow(cursorLocationRow + 1);
							minLevel = next.getPathCount() - 1; // don't go
																// shallower
																// than the next
																// row's depth
						}
						cursorLevel = Math.min(cursorLevel, maxLevel);
						cursorLevel = Math.max(cursorLevel, minLevel);
						for (int depthDelta = maxLevel - cursorLevel + 1; depthDelta > 0; depthDelta--) {
							pathToDrawFrom = pathToDrawFrom.getParentPath();
						}
					}
				}
			}

			if (!pathToDrawFrom.equals(fromPath) || !pathToDrawTo.equals(toPath)) {
				fromPath = pathToDrawFrom;
				toPath = pathToDrawTo;
				return true;
			}
		}

		return false;
	}

	public boolean getDropLinesActive() {
		return dropLinesActive;
	}

	public void setDropLinesActive(boolean a) {
		if (dropLinesActive != a) {
			dropLinesActive = a;
			if (a) {
				updateVars();
			}
			repaint();
		}
	}

	public boolean getShowDropLines() {
		return showDropLines;
	}

	public void setShowDropLines(boolean b) {
		if (showDropLines != b) {
			showDropLines = b;
			repaint();
		}
	}

	protected void updateVars() {
		updateDepthOffset();
		insets = getInsets();
	}

	protected void updateDepthOffset() {
		if (isRootVisible()) {
			if (getShowsRootHandles()) {
				depthOffset = 1;
			} else {
				depthOffset = 0;
			}
		} else if (!getShowsRootHandles()) {
			depthOffset = -1;
		} else {
			depthOffset = 0;
		}
	}

	public javax.swing.tree.TreePath getParentPath() {
		return fromPath;
	}

	public javax.swing.tree.TreePath[] getParentToPredecessorPaths() {
		return getPathBetweenRows(getRowForPath(fromPath), getRowForPath(toPath));
	}

	@Override
	public void paintComponent(java.awt.Graphics g) {
		try {
			super.paintComponent(g);
		} catch (NullPointerException e) {
			AuthoringTool.showErrorDialog("Error painting tree.", e);
		}
		if (dropLinesActive && showDropLines) {
			paintLines(g, getBounds(), insets, fromPath, toPath);
		}
	}

	synchronized public void autoscrollIfNecessary() {
		java.awt.Container parent = getParent();
		if (parent instanceof javax.swing.JViewport) {
			java.awt.Rectangle viewRect = ((javax.swing.JViewport) parent).getViewRect();
			int desiredRow = -1;
			if (cursorLocation.y < viewRect.y + 10) {
				desiredRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y) - 1;
			} else if (cursorLocation.y > viewRect.y + viewRect.height - 10) {
				desiredRow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y) + 1;
			}

			int lastRow = getRowCount() - 1 - (isRootVisible() ? 0 : 1);
			if (desiredRow > -1 && desiredRow <= lastRow) {
				scrollRowToVisible(desiredRow);
			}
		}
	}
}
