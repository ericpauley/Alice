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
public class ObjectArrayPropertyEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, java.awt.dnd.DropTargetListener {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty;
	protected javax.swing.JButton newItemButton = new javax.swing.JButton("new item");
	protected Class type = Object.class; // hack

	protected int lineLocation = -1;
	protected int position = 0;

	public ObjectArrayPropertyEditor() {
		guiInit();
	}

	private void guiInit() {
		setLayout(new java.awt.GridBagLayout());
		setBackground(java.awt.Color.white);
		newItemButton.setBackground(new java.awt.Color(240, 240, 255));
		newItemButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				objectArrayProperty.add(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass(type));
				newItemButton.scrollRectToVisible(newItemButton.getBounds());
			}
		});
		setDropTarget(new java.awt.dnd.DropTarget(this, this));
		refreshGUI();
	}

	public void setObjectArrayProperty(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty) {
		if (this.objectArrayProperty != null) {
			this.objectArrayProperty.removePropertyListener(this);
		}

		this.objectArrayProperty = objectArrayProperty;

		if (this.objectArrayProperty != null) {
			this.objectArrayProperty.addPropertyListener(this);
		}
		refreshGUI();
	}

	public void setType(Class type) {
		this.type = type;
	}

	public void refreshGUI() {
		removeAll();
		if (objectArrayProperty != null) {
			Object[] items = objectArrayProperty.getArrayValue();
			int count = 0;
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					edu.cmu.cs.stage3.alice.core.Element owner = objectArrayProperty.getOwner();
					if (owner == null || !(owner.getRoot() instanceof edu.cmu.cs.stage3.alice.core.World)) { // SUPER
																												// BIG
																												// HACK:
																												// for
																												// accessing
																												// Models
																												// as
																												// list
																												// elements
																												// before
																												// a
																												// variable
																												// has
																												// been
																												// put
																												// into
																												// the
																												// world
						owner = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld();
					}
					ObjectArrayPropertyItem item = new ObjectArrayPropertyItem(owner, objectArrayProperty, i, type);
					PopupItemFactory factory = new SetPropertyImmediatelyFactory(item);
					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyGUI(item, true, false, factory);
					if (gui != null) {
						this.add(gui, new java.awt.GridBagConstraints(0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 2, 2, 2), 0, 0));
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Unable to create gui for item: " + item, null);
					}
				}
			}

			this.add(newItemButton, new java.awt.GridBagConstraints(0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(8, 2, 8, 2), 0, 0));
			newItemButton.setDropTarget(new java.awt.dnd.DropTarget(newItemButton, this));
			java.awt.Component glue = javax.swing.Box.createGlue();
			this.add(glue, new java.awt.GridBagConstraints(0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(2, 2, 2, 2), 0, 0));
			glue.setDropTarget(new java.awt.dnd.DropTarget(glue, this));
		}
		revalidate();
		repaint();
	}

	// line drawing

	@Override
	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		if (lineLocation > -1) {
			java.awt.Rectangle bounds = getBounds();
			g.setColor(java.awt.Color.black);
			g.fillRect(0, lineLocation, bounds.width, 2);
		}
	}

	// /////////////////////////////////////////////
	// DropTargetListener interface
	// /////////////////////////////////////////////

	protected void calculateLineLocation(int mouseY) {
		int numSpots = objectArrayProperty.size() + 1;
		int[] spots = new int[numSpots];
		spots[0] = 0;
		for (int i = 1; i < numSpots; i++) {
			java.awt.Component c = getComponent(i - 1);
			spots[i] = c.getBounds().y + c.getBounds().height + 1; // assumes
																	// gridBagConstraints
																	// insets.bottom
																	// == 2
		}

		int closestSpot = -1;
		int minDist = Integer.MAX_VALUE;
		for (int i = 0; i < numSpots; i++) {
			int d = Math.abs(mouseY - spots[i]);
			if (d < minDist) {
				minDist = d;
				closestSpot = i;
			}
		}

		position = closestSpot;
		lineLocation = spots[closestSpot];
	}

	@Override
	public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
			dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			int mouseY = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this).y;
			calculateLineLocation(mouseY);
		} else {
			lineLocation = -1;
			dtde.rejectDrag();
		}
		repaint();
	}

	@Override
	public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
			dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			int mouseY = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this).y;
			calculateLineLocation(mouseY);
		} else {
			lineLocation = -1;
			dtde.rejectDrag();
		}
		repaint();
	}

	@Override
	public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
		java.awt.datatransfer.Transferable transferable = dtde.getTransferable();

		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
			dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
			try {
				edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem item = (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyItem) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor);
				Object value = item.get();
				if (position > item.getIndex()) {
					position--;
				}
				item.getObjectArrayProperty().remove(item.getIndex());
				item.getObjectArrayProperty().add(position, value);
				dtde.dropComplete(true);
			} catch (java.awt.datatransfer.UnsupportedFlavorException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
				dtde.dropComplete(false);
			} catch (java.io.IOException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
				dtde.dropComplete(false);
			} catch (Throwable t) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error moving item.", t);
				dtde.dropComplete(false);
			}
		} else {
			dtde.rejectDrop();
			dtde.dropComplete(false);
		}
		lineLocation = -1;
		repaint();
	}

	@Override
	public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
			dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
		} else {
			dtde.rejectDrag();
		}
	}

	@Override
	public void dragExit(java.awt.dnd.DropTargetEvent dte) {
		lineLocation = -1;
		repaint();
	}

	// /////////////////////////////////////////////
	// PropertyListener interface
	// /////////////////////////////////////////////

	@Override
	public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
	}
	@Override
	public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
		refreshGUI();
	}
}