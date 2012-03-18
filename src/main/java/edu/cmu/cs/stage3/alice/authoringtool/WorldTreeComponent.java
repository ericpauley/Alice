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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jason Pratt
 */
public class WorldTreeComponent extends javax.swing.JPanel {
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.core.Element bogusRoot = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = new edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellRenderer cellRenderer = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellRenderer();
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellEditor cellEditor = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellEditor();
	protected edu.cmu.cs.stage3.alice.core.Element selectedElement;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig;
	protected java.awt.dnd.DragSource dragSource = new java.awt.dnd.DragSource();
	protected java.util.HashSet elementSelectionListeners = new java.util.HashSet();
	protected WorldTreeDropTargetListener worldTreeDropTargetListener = new WorldTreeDropTargetListener();
	protected AuthoringTool authoringTool;

	public WorldTreeComponent( AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		modelInit();
		jbInit();
		treeInit();
		dndInit();
		selectionInit();
	}

	private void modelInit() {
		worldTreeModel.setRoot( bogusRoot );
	}

	private void treeInit() {
		worldTree.setModel( worldTreeModel );
		worldTree.addTreeSelectionListener( worldSelectionListener );
		worldTree.putClientProperty( "JTree.lineStyle", "Angled" );
		worldTree.setCellEditor( cellEditor );
		worldTree.setCellRenderer( cellRenderer );
		CustomTreeUI treeUI = new CustomTreeUI();
		worldTree.setUI( treeUI );
		treeUI.setExpandedIcon( AuthoringToolResources.getIconForString( "minus" ) );
		treeUI.setCollapsedIcon( AuthoringToolResources.getIconForString( "plus" ) );
		worldTree.setEditable( true );
		worldTree.addMouseListener( worldTreeMouseListener );
		worldTree.addTreeWillExpandListener(
			new javax.swing.event.TreeWillExpandListener() {
				public void treeWillCollapse( javax.swing.event.TreeExpansionEvent ev ) throws javax.swing.tree.ExpandVetoException {
					if( ev.getPath().getLastPathComponent() == worldTreeModel.getRoot() ) {
						throw new javax.swing.tree.ExpandVetoException( ev );
					}
				}
				public void treeWillExpand( javax.swing.event.TreeExpansionEvent ev ) {}
			}
		);

		treeScrollPane.setBorder( null );
	}

	private void dndInit() {
		dragSource.createDefaultDragGestureRecognizer( worldTree, java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE | java.awt.dnd.DnDConstants.ACTION_LINK, new ElementTreeDragGestureListener( worldTree ) );
		worldTree.setDropTarget( new java.awt.dnd.DropTarget( worldTree, worldTreeDropTargetListener ) );
	}

	private void selectionInit() {
		authoringTool.addElementSelectionListener(
			new edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener() {
				public void elementSelected( edu.cmu.cs.stage3.alice.core.Element element ) {
					WorldTreeComponent.this.setSelectedElement( element );
				}
			}
		);
	}

	public void startListening( AuthoringTool authoringTool ) {
		authoringTool.addAuthoringToolStateListener( worldTreeModel );
	}

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return world;
	}

	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		this.world = world;
		if( world == null ) {
			worldTreeModel.setRoot( bogusRoot );
			setCurrentScope( bogusRoot );
		} else {
			worldTreeModel.setRoot( world );
			setCurrentScope( world );
			worldTree.setSelectionRow( 0 );
		}

		revalidate();
		repaint();
	}

	public void setCurrentScope( edu.cmu.cs.stage3.alice.core.Element element ) {
		worldTreeModel.setCurrentScope( element );
	}

	public void setSelectedElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != selectedElement ) {
			selectedElement = element;
			if( element == null ) {
				worldTree.clearSelection();
			} else {
				while( true ) {
					Object[] path = worldTreeModel.getPath( element );
					if( path == null || path.length==0 ) {
						try {
							Thread.sleep( 10 );
						} catch( InterruptedException ie ) {
							break;
						}
					} else {
						javax.swing.tree.TreePath selectedPath = new javax.swing.tree.TreePath( path );
						worldTree.setSelectionPath( selectedPath );
						worldTree.scrollPathToVisible( selectedPath );
						break;
					}
				}
			}
		}
	}

	public edu.cmu.cs.stage3.alice.core.Element getSelectedElement() {
		return selectedElement;
	}

	private javax.swing.event.TreeSelectionListener worldSelectionListener = new javax.swing.event.TreeSelectionListener() {
		public void valueChanged( javax.swing.event.TreeSelectionEvent ev ) {
			javax.swing.tree.TreePath path = ev.getNewLeadSelectionPath();
			if( path != null ) {
				Object o = path.getLastPathComponent();
				if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					selectedElement = (edu.cmu.cs.stage3.alice.core.Element)o;
					if( selectedElement == worldTreeModel.HACK_getOriginalRoot() ) {
						//pass
					} else {
						WorldTreeComponent.this.authoringTool.setSelectedElement( selectedElement );
					}
				}
			} else {
				WorldTreeComponent.this.setSelectedElement( (edu.cmu.cs.stage3.alice.core.Element)WorldTreeComponent.this.worldTreeModel.getRoot() );
			}
		}
	};

	////////////////////
	// Drag and Drop
	////////////////////

	public class ElementTreeDragGestureListener implements java.awt.dnd.DragGestureListener {
		protected javax.swing.JTree tree;
		protected edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel treeModel;

		public ElementTreeDragGestureListener( javax.swing.JTree tree ) {
			this.tree = tree;
			this.treeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel)tree.getModel();
		}

		public void dragGestureRecognized( java.awt.dnd.DragGestureEvent dge ) {
			edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.fireDragGestureRecognized( dge );

			if( tree.isEditing() ) {
				return;
			}

			/* do we need to avoid double-click drags?
			java.awt.event.InputEvent triggerEvent = dge.getTriggerEvent();
			if( triggerEvent instanceof java.awt.event.MouseEvent ) {
				if( ((java.awt.event.MouseEvent)triggerEvent).getClickCount() > 1 ) {
					return;
				}
			}
			*/

			java.awt.Point p = dge.getDragOrigin();
			javax.swing.tree.TreePath path = tree.getPathForLocation( (int)p.getX(), (int)p.getY() );
			if( path != null ) {
				Object element = path.getLastPathComponent();
				if( element instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					if( treeModel.isElementInScope( (edu.cmu.cs.stage3.alice.core.Element)element ) ) {
						java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable( element );
						dragSource.startDrag( dge, java.awt.dnd.DragSource.DefaultMoveDrop, transferable, edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getInternalListener() );
						edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.fireDragStarted( transferable, WorldTreeComponent.this );
					}
				}
			}
		}
	}

	public class WorldTreeDropTargetListener implements java.awt.dnd.DropTargetListener {
		protected boolean checkDrag( java.awt.dnd.DropTargetDragEvent dtde ) {
			if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Model.class ) ) ) {
				dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
				return true;
			}

			dtde.rejectDrag();
			return false;
		}

		public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
			if( checkDrag( dtde ) ) {
				worldTree.setDropLinesActive( true );
			}
		}

		public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
			if( checkDrag( dtde ) ) {
				worldTree.setCursorLocation( dtde.getLocation() );

				javax.swing.tree.TreePath parentPath = worldTree.getParentPath();
				edu.cmu.cs.stage3.alice.core.Element parent = (edu.cmu.cs.stage3.alice.core.Element)parentPath.getLastPathComponent();

				java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
				edu.cmu.cs.stage3.alice.core.Element child = null;
				if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported(transferable, AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Model.class ) ) ) {
					try {
						child = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Model.class ) );
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error encountered extracting drop transferable.", e );
					}
				}

				if( child != null ) {
					if( isAcceptableDrop( parent, child ) ) {
//						worldTree.setSelectionPath( parentPath );
						worldTree.setShowDropLines( true );
					} else {
						//TODO setCursor
						//if( (!invalidCursorShown) && (invalidCursor != null) ) {
						//	worldTree.setCursor( invalidCursor );
						//}
						worldTree.setShowDropLines( false );
					}
				} else {
					worldTree.setShowDropLines( true );
				}
			}
		}

		public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
		}

		public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
			worldTree.setDropLinesActive( false );
		}

		public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
			boolean succeeded = true;
			//DEBUG System.out.println( "drop" );
			worldTree.setCursorLocation( dtde.getLocation() );

			try {
				Object o = null;
				if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Model.class ) ) ) {
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					final edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)transferable.getTransferData( AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Model.class ) );

					javax.swing.tree.TreePath parentPath = worldTree.getParentPath();
					final edu.cmu.cs.stage3.alice.core.Element parent = (edu.cmu.cs.stage3.alice.core.Element)parentPath.getLastPathComponent();

					if( isAcceptableDrop( parent, model ) ) {
						javax.swing.SwingUtilities.invokeLater( new Runnable() {
							public void run() {
								try {
									insertChild( parent, model, worldTree.getParentToPredecessorPaths() );
								} catch( Throwable t ) {
									AuthoringTool.showErrorDialog( "Error moving child.", t );
								}
							}
						} );
						dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
						succeeded = true;
					} else {
						dtde.rejectDrop();
						succeeded = false;
					}
				} else {
					dtde.rejectDrop();
					succeeded = false;
				}
			} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
				AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
				succeeded = false;
			} catch( java.io.IOException e ) {
				AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
				succeeded = false;
			} catch( Throwable t ) {
				AuthoringTool.showErrorDialog( "Drop didn't work.", t );
				succeeded = false;
			}

			worldTree.setDropLinesActive( false );
			dtde.dropComplete( succeeded );
		}

		private boolean isAcceptableDrop( edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.core.Element child ) {
			if( parent instanceof edu.cmu.cs.stage3.alice.core.Group ) {
				edu.cmu.cs.stage3.alice.core.Group group = (edu.cmu.cs.stage3.alice.core.Group)parent;
				Class childValueClass = child.getClass();
				if( child instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
					childValueClass = ((edu.cmu.cs.stage3.alice.core.Expression)child).getValueClass();
				}
				if( ! group.valueClass.getClassValue().isAssignableFrom( childValueClass ) ) {
					return false;
				}
			}

			if( (parent instanceof edu.cmu.cs.stage3.alice.core.Group) || (parent instanceof edu.cmu.cs.stage3.alice.core.World) ) {
				if( parent instanceof edu.cmu.cs.stage3.alice.core.World && child.getParent() instanceof edu.cmu.cs.stage3.alice.core.World ) {
					// return true when rearranging objects in world
					return true;
				} else if( parent instanceof edu.cmu.cs.stage3.alice.core.Group && child.getParent() instanceof edu.cmu.cs.stage3.alice.core.Group ) {
					// return true when rearranging objects in group folder
					if (!parent.name.getValue().equals(child.getParent().name.getValue()) ) {
							for (int i = 0; i < parent.getChildCount(); i++){
								if( parent.getChildAt(i).name.getValue().equals(child.name.getValue()) )
									return false;
							}
					} else
						return true;
						
				} else if( child.getParent() == null ) {
					// 
					return true;
				}
				
				// check if object exists when moving object
				for (int i = 0; i < parent.getChildCount(); i++){
					if( parent.getChildAt(i).name.getValue().equals(child.name.getValue()) )
						return false;
				}
				return true;
			}

			return false;
		}

		private void insertChild( edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.core.Element child, javax.swing.tree.TreePath[] parentToPredecessor ) {
			int index;
			javax.swing.tree.TreePath parentPath = parentToPredecessor[0];
			javax.swing.tree.TreePath predecessorPath = parentToPredecessor[parentToPredecessor.length-1];
			edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = null;

			if( parent instanceof edu.cmu.cs.stage3.alice.core.World ) {
				oap = ((edu.cmu.cs.stage3.alice.core.World)parent).sandboxes;
			} else if( parent instanceof edu.cmu.cs.stage3.alice.core.Group ) {
				oap = ((edu.cmu.cs.stage3.alice.core.Group)parent).values;
			}

			if( predecessorPath == parentPath ) {
				index = 0;
			} else {
				int i = parentToPredecessor.length - 1;
				while( (i >= 0) && (parentToPredecessor[i] != null) && (! parentToPredecessor[i].getParentPath().equals( parentPath )) ) {
					i--;
				}
				edu.cmu.cs.stage3.alice.core.Element predecessor = (edu.cmu.cs.stage3.alice.core.Element)parentToPredecessor[i].getLastPathComponent();
				index = 1 + oap.indexOf( predecessor );
			}

			if( isAcceptableDrop( parent, child ) ) {
				int currentIndex = oap.indexOf( child );
				if( (currentIndex > -1) && (currentIndex < index) ) {
					index--;
				}

				authoringTool.getUndoRedoStack().startCompound();

//				child.removeFromParent();
				child.removeFromParentsProperties();				
//				parent.addChild( child );
				child.setParent( parent );
				oap.add( index, child );

				authoringTool.getUndoRedoStack().stopCompound();
			}
		}
	}

	// avoid editing on drag... and other mouse enhancements
	public class CustomTreeUI extends javax.swing.plaf.metal.MetalTreeUI {
		
		protected java.awt.event.MouseListener createMouseListener() {
			return new CustomMouseHandler();
		}

		
		protected boolean startEditing( javax.swing.tree.TreePath path, java.awt.event.MouseEvent ev ) {
			boolean result = super.startEditing( path, ev );
			if( result ) {
				WorldTreeComponent.this.cellEditor.selectText();
			}
			return result;
		}

		public class CustomMouseHandler extends java.awt.event.MouseAdapter  {
			// we'll do our own click detection
			protected long pressTime;
			protected java.awt.Point pressPoint;
			protected long clickDelay = 300;
			protected double clickDistance = 8.0;

			
			public void mousePressed( java.awt.event.MouseEvent ev ) {
				pressTime = ev.getWhen();
				pressPoint = ev.getPoint();
				if( (tree != null) && tree.isEnabled() ) {
					tree.requestFocus();
					javax.swing.tree.TreePath path = getClosestPathForLocation( tree, ev.getX(), ev.getY() );

					if( path != null ) {
						java.awt.Rectangle bounds = getPathBounds( tree, path );
						if( ev.getY() > (bounds.y + bounds.height) ) {
							return;
						}
						if( javax.swing.SwingUtilities.isLeftMouseButton( ev ) ) {
							checkForClickInExpandControl( path, ev.getX(), ev.getY() );
						}
					}
				}
			}

			
			public void mouseReleased( java.awt.event.MouseEvent ev ) {
				if( (tree != null) && tree.isEnabled() ) {
					tree.requestFocus();
					javax.swing.tree.TreePath path = getClosestPathForLocation( tree, ev.getX(), ev.getY() );

					if( path != null ) {
						edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)path.getLastPathComponent();
						boolean elementInScope = ((edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel)tree.getModel()).isElementInScope( element );

						if( elementInScope ) {
							java.awt.Rectangle bounds = getPathBounds( tree, path );
							if( ev.getY() > (bounds.y + bounds.height) ) {
								return;
							}

							int x = ev.getX();
							if( x > bounds.x && (x <= (bounds.x + bounds.width)) ) {
								if( isClick( ev ) ) {
									if( startEditing( path, ev ) ) {
										return;
									}
								}
								//System.out.println( element + " in scope of " + ((edu.cmu.cs.stage3.alice.authoringtool.util.ScopedElementTreeModel)tree.getModel()).getCurrentScope() );
								selectPathForEvent( path, ev );
							}
						}
					}
				}
			}

			protected boolean isClick( java.awt.event.MouseEvent ev ) {
				if( ev.getClickCount() > 1 ) {
					return true;
				}

				long time = ev.getWhen();
				java.awt.Point p = ev.getPoint();

				if( (time - pressTime) <= clickDelay ) {
					double dx = p.getX() - pressPoint.getX();
					double dy = p.getY() - pressPoint.getY();
					double dist = Math.sqrt( dx*dx + dy*dy  );
					if( dist <= clickDistance ) {
						return true;
					}
				}

				return false;
			}
		}
	}

	/////////////////////////////
	// Mouse event handling
	/////////////////////////////

	protected final java.awt.event.MouseListener worldTreeMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
		protected java.util.Vector defaultStructure;

		
		protected void popupResponse( java.awt.event.MouseEvent ev ) {
			javax.swing.JTree tree = (javax.swing.JTree)ev.getSource();
			javax.swing.tree.TreePath path = tree.getPathForLocation( ev.getX(), ev.getY() );
			if( path != null ) {
				Object node = path.getLastPathComponent();
				if( node instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					javax.swing.JPopupMenu popup = createPopup( (edu.cmu.cs.stage3.alice.core.Element)node, path );
					if( popup != null ) {
						popup.show( ev.getComponent(), ev.getX(), ev.getY() );
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen( popup );
					}
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu( getDefaultStructure(), ev.getComponent(), ev.getX(), ev.getY() );
			}
		}

		private javax.swing.JPopupMenu createPopup( final edu.cmu.cs.stage3.alice.core.Element element, javax.swing.tree.TreePath path ) {
			java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure( element, worldTreeModel.isElementInScope( element ), authoringTool, worldTree, path );
			return edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu( element, structure );
		}

		protected java.util.Vector getDefaultStructure() {
			if( defaultStructure == null ) {
				defaultStructure = new java.util.Vector();
				defaultStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "create new group", new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.core.Group newGroup = new edu.cmu.cs.stage3.alice.core.Group();
						String name = AuthoringToolResources.getNameForNewChild( "Group", world );
						newGroup.name.set( name );
						newGroup.valueClass.set( edu.cmu.cs.stage3.alice.core.Model.class );
						world.addChild( newGroup );
						world.groups.add( newGroup );
					}
				} ) );
			}
			return defaultStructure;
		}
	};

	//////////////////////////
	// GUI
	//////////////////////////

	BorderLayout borderLayout2 = new BorderLayout();
	TitledBorder titledBorder1;
	JScrollPane treeScrollPane = new JScrollPane();
	JPanel treePanel = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	WorldTree worldTree = new WorldTree();
	//JTree charactersTree = new javax.swing.JTree();

	public void jbInit() {
		titledBorder1 = new TitledBorder("");

		setLayout(borderLayout2 );
		if (authoringtoolConfig != null) {
			int fontSize = Integer.parseInt(authoringtoolConfig.getValue("fontSize"));
			worldTree.setFont(new java.awt.Font("Dialog", 0, (int)(14*fontSize/12.0)));
		} else {
			worldTree.setFont(new java.awt.Font("Dialog", 0, 14));
		}
		this.setMinimumSize(new Dimension(5, 0));
		treePanel.setLayout(borderLayout4);
		worldTree.setBorder(new JScrollPane().getViewportBorder());
		//	charactersTree.setFont(new java.awt.Font("Dialog", 0, 14));
		//	charactersTree.setRootVisible(false);
		this.add(treeScrollPane, BorderLayout.CENTER);
		treeScrollPane.getViewport().add(treePanel, null);
		treePanel.add(worldTree, BorderLayout.CENTER);
		//treePanel.add(charactersTree, BorderLayout.CENTER);
	}

	public static class HackBorder extends AbstractBorder { // a hack to simulate a Metal ScrollPane border
	   private static final Insets insets = new Insets( 1, 1, 2, 2 );

		
		public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
			g.translate( x, y );

			g.setColor( javax.swing.plaf.metal.MetalLookAndFeel.getControlDarkShadow() );
			g.drawRect( 0, 0, w-2, h-2 );
			g.setColor( javax.swing.plaf.metal.MetalLookAndFeel.getControlHighlight() );

			g.drawLine( w-1, 1, w-1, h-1 );
			g.drawLine( 1, h-1, w-1, h-1 );

			g.setColor( javax.swing.plaf.metal.MetalLookAndFeel.getControl() );
			g.drawLine( w-2, 2, w-2, 2 );
			g.drawLine( 1, h-2, 1, h-2 );

			g.translate( -x, -y );
		}

		
		public Insets getBorderInsets( Component c ) {
			return insets;
		}
	}
}