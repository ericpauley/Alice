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

package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JComponent;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

// Referenced classes of package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor:
//            BehaviorGroupsEditor, BehaviorGroupEditor

public class CompositeComponentBehaviorPanel extends CompositeComponentElementPanel {

	public CompositeComponentBehaviorPanel() {
	}

	public void set(ObjectArrayProperty elements, BehaviorGroupEditor owner, AuthoringTool authoringToolIn) {
		super.set(elements, owner, authoringToolIn);
	}

	@Override
	protected boolean isInverted() {
		return true;
	}

	@Override
	protected Component makeGUI(Element currentElement) {
		JComponent toAdd = null;
		if (currentElement instanceof Behavior) {
			toAdd = GUIFactory.getGUI(currentElement);
			return toAdd;
		} else {
			return null;
		}
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		Transferable transferable = dtde.getTransferable();
		boolean dropSuccess = true;
		try {
			int type = BehaviorGroupsEditor.checkTransferable(transferable);
			if (type == -1 || type == 1) {
				if (super.m_owner.getParent() instanceof DropTargetListener) {
					((DropTargetListener) super.m_owner.getParent()).drop(dtde);
				}
			} else if (type == 2) {
				dtde.acceptDrop(2);
				Behavior b;
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, CopyFactoryTransferable.copyFactoryFlavor)) {
					CopyFactory copyFactory = (CopyFactory) transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
					b = (Behavior) copyFactory.manufactureCopy(super.componentElements.getOwner().getWorld());
				} else {
					b = (Behavior) transferable.getTransferData(ElementReferenceTransferable.behaviorReferenceFlavor);
				}
				performDrop(b, dtde);
			}
		} catch (Exception e) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("The drop failed.", e);
			dropSuccess = false;
		}
		dtde.dropComplete(dropSuccess);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		if (!super.m_owner.isExpanded() && super.m_owner.getParent() instanceof DropTargetListener) {
			((DropTargetListener) super.m_owner.getParent()).dragOver(dtde);
		}
		try {
			if (BehaviorGroupsEditor.checkDragEvent(dtde) != 2) {
				if (super.m_owner.getParent() instanceof DropTargetListener) {
					((DropTargetListener) super.m_owner.getParent()).dragOver(dtde);
				}
			} else {
				insertDropPanel(dtde);
				dtde.acceptDrag(dtde.getDropAction());
			}
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrag();
			return;
		}
	}
}