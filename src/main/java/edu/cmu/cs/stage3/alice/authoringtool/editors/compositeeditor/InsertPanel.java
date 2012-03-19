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

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class InsertPanel extends javax.swing.JPanel implements java.awt.dnd.DropTargetListener {

	protected javax.swing.JLabel m_label = new javax.swing.JLabel();
	protected String m_doNothingLabel = " Do Nothing";
	private boolean highlight = false;

	public InsertPanel() {
		setDropTarget(new java.awt.dnd.DropTarget(this, this));
		m_label.setDropTarget(new java.awt.dnd.DropTarget(this, this));
		m_label.setFont(m_label.getFont().deriveFont(java.awt.Font.ITALIC));
		setOpaque(false);
		setLayout(new java.awt.GridBagLayout());
		this.add(m_label, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		this.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(1, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		setMaximumSize(new java.awt.Dimension(2400, 30));
		setMinimumSize(new java.awt.Dimension(0, 0));
		setBorder(null);
		m_label.setText(m_doNothingLabel);
	}

	public void setHighlight(boolean toSet) {
		if (highlight != toSet) {
			highlight = toSet;
			repaint();
		}
	}

	@Override
	public void paintComponent(java.awt.Graphics g) {
		java.awt.Rectangle r = new java.awt.Rectangle(0, 0, getWidth(), getHeight());
		if (highlight) {
			int arcWidth = 12;
			int arcHeight = 10;
			Object oldAntialiasing = null;
			if (g instanceof java.awt.Graphics2D) {
				oldAntialiasing = ((java.awt.Graphics2D) g).getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
			}
			java.awt.Rectangle bounds = getBounds();
			g.setColor(new java.awt.Color(255, 255, 255));
			g.fillRoundRect(0, 0, bounds.width, bounds.height, arcWidth, arcHeight);
			g.setColor(java.awt.Color.lightGray);
			g.drawRoundRect(0, 0, bounds.width - 1, bounds.height - 1, arcWidth, arcHeight);
			if (g instanceof java.awt.Graphics2D) {
				((java.awt.Graphics2D) g).addRenderingHints(new java.awt.RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
			}
		}
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintTrough(g, r, 12, 10);
		// System.out.println("size: "+r);
	}

	@Override
	public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
		if (getParent() instanceof java.awt.dnd.DropTargetListener) {
			((java.awt.dnd.DropTargetListener) getParent()).dragEnter(dtde);
		} else {
			dtde.rejectDrag();
		}
	}

	@Override
	public void dragExit(java.awt.dnd.DropTargetEvent dte) {
		if (getParent() instanceof java.awt.dnd.DropTargetListener) {
			((java.awt.dnd.DropTargetListener) getParent()).dragExit(dte);
		}
	}

	@Override
	public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
		if (getParent() instanceof java.awt.dnd.DropTargetListener) {
			((java.awt.dnd.DropTargetListener) getParent()).dragOver(dtde);
		} else {
			dtde.rejectDrag();
		}
	}

	@Override
	public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
		if (getParent() instanceof java.awt.dnd.DropTargetListener) {
			((java.awt.dnd.DropTargetListener) getParent()).drop(dtde);
		} else {
			dtde.rejectDrop();
		}
	}

	@Override
	public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
		if (getParent() instanceof java.awt.dnd.DropTargetListener) {
			((java.awt.dnd.DropTargetListener) getParent()).dropActionChanged(dtde);
		} else {
			dtde.rejectDrag();
		}
	}

}