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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author Jason Pratt
 */
public class TabbedEditorComponent extends javax.swing.JPanel {
	protected AuthoringTool authoringTool;
	protected EditorManager editorManager;
	protected EditorDropTargetListener editorDropTargetListener = new EditorDropTargetListener();
	protected java.util.HashMap componentsToEditors = new java.util.HashMap();
	protected RightClickListener rightClickListener = new RightClickListener();
	protected NameListener nameListener = new NameListener();
	protected DeletionListener deletionListener = new DeletionListener();
	protected Runnable closeAllTabsRunnable = new Runnable() {
		@Override
		public void run() {
			TabbedEditorComponent.this.closeAllTabs();
		}
	};
	protected edu.cmu.cs.stage3.alice.core.World world;
	private edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());

	public TabbedEditorComponent(AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		editorManager = authoringTool.getEditorManager();
		jbInit();
		guiInit();
		// miscInit();
	}

	private void guiInit() {
		tabbedPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
		setDropTarget(new java.awt.dnd.DropTarget(this, editorDropTargetListener));
		tabbedPane.setDropTarget(new java.awt.dnd.DropTarget(tabbedPane, editorDropTargetListener));
		tabbedPane.addMouseListener(rightClickListener);
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		tabbedPane.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, (int) (16 * (fontSize / 12.0))));
	}

	// private void miscInit() {
	// authoringTool.addAuthoringToolStateListener(
	// new
	// edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateAdapter() {
	// public void worldUnLoaded(
	// edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent
	// ev ) {
	// TabbedEditorComponent.this.closeAllTabs();
	// }
	// }
	// );
	// }

	// public int getComponentCount(){
	// int toReturn = super.getComponentCount();
	// System.out.println("count: "+toReturn+", tabbedPane Count: "+tabbedPane.getComponentCount());
	// return toReturn;
	// }

	public void setWorld(edu.cmu.cs.stage3.alice.core.World world) {
		stopListeningToTree(world);
		closeAllTabs();

		this.world = world;
		if (world != null) {
			startListeningToTree(world);
		}
	}

	public void editObject(Object object, Class editorClass, boolean switchToNewTab) {
		if (object == null || editorClass == null) { // TODO: this is a hack
			closeAllTabs();
		} else {
			if (!isObjectBeingEdited(object)) { // TODO: allow editing of the
												// same object with different
												// editors
				Editor editor = editorManager.getEditorInstance(editorClass);
				if (editor != null) {
					componentsToEditors.put(editor.getJComponent(), editor);
					edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.editObject(editor, object);
					String repr = AuthoringToolResources.getReprForValue(object, true);
					Object iconObject = object;
					if (object instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
						edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion udq = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) object;
						if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(udq.getValueClass())) {
							edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List) udq.getValue();
							iconObject = "types/lists/" + list.valueClass.getClassValue().getName();
						} else {
							iconObject = "types/" + udq.getValueClass().getName();
						}
					}
					javax.swing.ImageIcon icon = AuthoringToolResources.getIconForValue(iconObject);
					tabbedPane.addTab(repr, icon, editor.getJComponent());
					if (switchToNewTab) {
						tabbedPane.setSelectedComponent(editor.getJComponent());
						tabbedPane.getSelectedComponent().setVisible(true); // HACK
																			// for
																			// Java
																			// Bug
																			// 4190719
					}
					if (object instanceof edu.cmu.cs.stage3.alice.core.Element) {
						((edu.cmu.cs.stage3.alice.core.Element) object).name.addPropertyListener(nameListener);
						// if(
						// ((edu.cmu.cs.stage3.alice.core.Element)object).getParent()
						// != null ) {
						// ((edu.cmu.cs.stage3.alice.core.Element)object).getParent().addChildrenListener(
						// deletionListener );
						// }
					}
					authoringTool.saveTabs();
				} else {
					AuthoringTool.showErrorDialog("failed to create editor for " + object + ", " + editorClass, null);
				}
			} else if (switchToNewTab) {
				for (int i = 0; i < tabbedPane.getTabCount(); i++) {
					java.awt.Component component = tabbedPane.getComponentAt(i);
					if (component != null) {
						Editor editor = (Editor) componentsToEditors.get(component);
						if (editor != null) {
							if (editor.getObject() == object) {
								tabbedPane.setSelectedIndex(i);
								tabbedPane.getSelectedComponent().setVisible(true); // HACK
																					// for
																					// Java
																					// Bug
																					// 4190719
								break;
							}
						}
					}
				}
			}
		}
	}

	public Object getObjectBeingEdited() {
		java.awt.Component component = tabbedPane.getSelectedComponent();
		if (component != null) {
			Editor editor = (Editor) componentsToEditors.get(component);
			if (editor != null) {
				return editor.getObject();
			}
		}
		return null;
	}

	public Object getObjectBeingEditedAt(int index) {
		java.awt.Component component = tabbedPane.getComponentAt(index);
		if (component != null) {
			Editor editor = (Editor) componentsToEditors.get(component);
			if (editor != null) {
				return editor.getObject();
			}
		}
		return null;
	}

	public Object[] getObjectsBeingEdited() {
		java.awt.Component[] components = tabbedPane.getComponents();
		if (components != null) {
			java.util.ArrayList objects = new java.util.ArrayList();
			for (Component component2 : components) {
				Editor editor = (Editor) componentsToEditors.get(component2);
				if (editor != null) {
					objects.add(editor.getObject());
				}
			}
			return objects.toArray();
		}
		return null;
	}

	public int getIndexOfObject(Object o) {
		java.awt.Component[] components = tabbedPane.getComponents();
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				Editor editor = (Editor) componentsToEditors.get(components[i]);
				if (editor != null) {
					if (editor.getObject().equals(o)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	public Editor getCurrentEditor() {
		java.awt.Component component = tabbedPane.getSelectedComponent();
		if (component != null) {
			return (Editor) componentsToEditors.get(component);
		}
		return null;
	}

	public Editor getEditorAt(int index) {
		java.awt.Component component = tabbedPane.getComponentAt(index);
		if (component != null) {
			return (Editor) componentsToEditors.get(component);
		}
		return null;
	}

	public Editor[] getEditors() {
		java.awt.Component[] components = tabbedPane.getComponents();
		if (components != null) {
			java.util.ArrayList editors = new java.util.ArrayList();
			for (Component component2 : components) {
				Editor editor = (Editor) componentsToEditors.get(component2);
				if (editor != null) {
					editors.add(editor);
				}
			}
			return (Editor[]) editors.toArray(new Editor[0]);
		}
		return null;
	}

	public int getIndexOfEditor(Editor editor) {
		java.awt.Component[] components = tabbedPane.getComponents();
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				Editor e = (Editor) componentsToEditors.get(components[i]);
				if (editor.equals(e)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void closeTab(int index) {
		java.awt.Component component = tabbedPane.getComponentAt(index);
		if (component != null) {
			Editor editor = (Editor) componentsToEditors.get(component);
			if (editor != null) {
				Object object = editor.getObject();
				tabbedPane.removeTabAt(index);
				edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.editObject(editor, null);
				editorManager.releaseEditorInstance(editor);
				componentsToEditors.remove(component);
				if (object instanceof edu.cmu.cs.stage3.alice.core.Element && !isObjectBeingEdited(object)) {
					((edu.cmu.cs.stage3.alice.core.Element) object).name.removePropertyListener(nameListener);
					// if(
					// ((edu.cmu.cs.stage3.alice.core.Element)object).getParent()
					// != null ) {
					// System.out.println("removed listener: "+((edu.cmu.cs.stage3.alice.core.Element)object).getParent());
					// ((edu.cmu.cs.stage3.alice.core.Element)object).getParent().removeChildrenListener(
					// deletionListener );
					// }
				}
				authoringTool.saveTabs();
			}
		} else {
			AuthoringTool.showErrorDialog("no editor to close at " + index, null);
		}
	}

	public void closeAllTabs() {
		while (tabbedPane.getTabCount() > 0) {
			closeTab(0);
		}
	}

	public boolean isObjectBeingEdited(Object o) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			java.awt.Component component = tabbedPane.getComponentAt(i);
			if (component != null) {
				Editor editor = (Editor) componentsToEditors.get(component);
				if (editor != null) {
					if (editor.getObject() == o) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor getCurrentSceneEditor() {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			java.awt.Component component = tabbedPane.getComponentAt(i);
			if (component != null) {
				Editor editor = (Editor) componentsToEditors.get(component);
				if (editor instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor) {
					return (edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor) editor;
				}
			}
		}
		return null;
	}

	protected class RightClickListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {

		@Override
		public void popupResponse(final java.awt.event.MouseEvent ev) {
			final int index = tabbedPane.getUI().tabForCoordinate(tabbedPane, ev.getX(), ev.getY());
			if (index >= 0 && index < tabbedPane.getTabCount()) {
				Runnable closeTabRunnable = new Runnable() {
					@Override
					public void run() {
						closeTab(index);
					}
				};
				java.util.Vector structure = new java.util.Vector();
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Close " + tabbedPane.getTitleAt(index), closeTabRunnable));
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Close All", closeAllTabsRunnable));
				edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(structure, tabbedPane, ev.getX(), ev.getY());
			}
		}
	}

	protected class EditorDropTargetListener implements java.awt.dnd.DropTargetListener {
		protected void checkDrag(java.awt.dnd.DropTargetDragEvent dtde) {
			// TODO: better feedback
			if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				return;
			} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				return;
			} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class))) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				return;
			} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class))) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
				return;
			} else {
				java.awt.datatransfer.DataFlavor[] flavors = AuthoringToolResources.safeGetCurrentDataFlavors(dtde);
				if (flavors != null) {
					for (DataFlavor flavor : flavors) {
						Class c = flavor.getRepresentationClass();
						if (edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(c) != null) {
							dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
							return;
						}
					}
				}
			}
			dtde.rejectDrag();
		}

		@Override
		public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);
		}

		@Override
		public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);
		}

		@Override
		public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);
		}

		@Override
		public void dragExit(java.awt.dnd.DropTargetEvent dte) {
			// TODO: feedback
		}

		@Override
		public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
			// DEBUG System.out.println( "drop" );
			try {
				Object o = null;
				if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class))) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					o = transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class));
					o = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) o).userDefinedResponse.getUserDefinedResponseValue();
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class))) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					o = transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class));
					o = ((edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion) o).userDefinedQuestion.getUserDefinedQuestionValue();
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					o = transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
					o = callToUserDefinedResponsePrototype.getActualResponse();
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
					o = callToUserDefinedQuestionPrototype.getActualQuestion();
				} else {
					java.awt.datatransfer.DataFlavor[] flavors = AuthoringToolResources.safeGetCurrentDataFlavors(dtde);
					if (flavors != null) {
						for (DataFlavor flavor : flavors) {
							Class c = flavor.getDefaultRepresentationClass();
							if (edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(c) != null) {
								dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
								java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
								o = transferable.getTransferData(flavor);
								break;
							}
						}
					}
				}
				if (o != null) {
					// DEBUG System.out.println( "o: " + o );
					Class editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(o.getClass());
					// DEBUG System.out.println( "editorClass: " + editorClass
					// );
					if (editorClass != null) {
						editObject(o, editorClass, true);
					}
					dtde.dropComplete(true);
				} else {
					dtde.rejectDrop();
					dtde.dropComplete(false);
				}
			} catch (java.awt.datatransfer.UnsupportedFlavorException e) {
				AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
				dtde.dropComplete(false);
			} catch (java.io.IOException e) {
				AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
				dtde.dropComplete(false);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog("Drop didn't work.", t);
				dtde.dropComplete(false);
			}
		}
	}

	// ////////////////////////
	// CustomTabbedPaneUI
	// ////////////////////////

	// protected class CustomTabbedPaneUI extends
	// javax.swing.plaf.metal.MetalTabbedPaneUI {
	// protected java.awt.Color behindTheTabsColor = new java.awt.Color( 255,
	// 230, 180 );
	//
	// public CustomTabbedPaneUI() {
	// setTabAreaInsets( new java.awt.Insets( 0, 0, 0, 0 ) );
	// }
	//
	// public void setTabAreaInsets( java.awt.Insets insets ) {
	// this.tabAreaInsets = insets;
	// }
	//
	// public void update( java.awt.Graphics g, javax.swing.JComponent c ) {
	// g.setColor( tabAreaBackground );
	// g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
	//
	// paint( g, c );
	// }
	//
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
	//
	// public int getTabAreaHeight() {
	// java.awt.Insets tabAreaInsets = getTabAreaInsets(
	// TabbedEditorComponent.this.tabbedPane.getTabPlacement() );
	// int runCount = TabbedEditorComponent.this.tabbedPane.getTabRunCount();
	// int tabRunOverlay = getTabRunOverlay(
	// TabbedEditorComponent.this.tabbedPane.getTabPlacement() );
	// return (runCount > 0 ? runCount * (maxTabHeight-tabRunOverlay) +
	// tabRunOverlay + tabAreaInsets.top + tabAreaInsets.bottom : 0);
	// }
	//
	// public int getTabAreaHeightIgnoringInsets() {
	// int runCount = TabbedEditorComponent.this.tabbedPane.getTabRunCount();
	// int tabRunOverlay = getTabRunOverlay(
	// TabbedEditorComponent.this.tabbedPane.getTabPlacement() );
	// return (runCount > 0 ? runCount * (maxTabHeight-tabRunOverlay) +
	// tabRunOverlay : 0);
	// }
	// }

	protected class NameListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				Object object = getObjectBeingEditedAt(i);
				if (object == ev.getProperty().getOwner()) {
					tabbedPane.setTitleAt(i, AuthoringToolResources.getReprForValue(ev.getProperty().getOwner(), true));
				}
			}
		}
	}

	protected class DeletionListener implements edu.cmu.cs.stage3.alice.core.event.ChildrenListener {
		@Override
		public void childrenChanging(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
		}
		@Override
		public void childrenChanged(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) {
				stopListeningToTree(ev.getChild());
				for (int i = 0; i < tabbedPane.getTabCount(); i++) {
					Object object = getObjectBeingEditedAt(i);
					if (object instanceof edu.cmu.cs.stage3.alice.core.Element) {
						edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) object;
						if (element == ev.getChild() || ev.getChild().isAncestorOf(element)) {
							closeTab(i);
							i--;
						}
					}
				}
			} else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED) {
				startListeningToTree(ev.getChild());
			}
		}
	}

	protected void startListeningToTree(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			edu.cmu.cs.stage3.alice.core.Element[] descendants = element.getDescendants();
			for (Element descendant : descendants) {
				descendant.addChildrenListener(deletionListener);
			}
		}
	}

	protected void stopListeningToTree(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element != null) {
			edu.cmu.cs.stage3.alice.core.Element[] descendants = element.getDescendants();
			for (Element descendant : descendants) {
				descendant.removeChildrenListener(deletionListener);
			}
		}
	}

	// ////////////////
	// Autogenerated
	// ////////////////

	JTabbedPane tabbedPane = new JTabbedPane();
	BorderLayout borderLayout1 = new BorderLayout();
	Border border1;

	private void jbInit() {
		border1 = BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(255, 230, 180));
		setLayout(borderLayout1);
		setOpaque(false);
		this.add(tabbedPane, BorderLayout.CENTER);
	}
}