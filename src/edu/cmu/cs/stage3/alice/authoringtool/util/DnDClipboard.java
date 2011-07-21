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
public class DnDClipboard extends javax.swing.JPanel {
	protected javax.swing.ImageIcon clipboardIcon = new javax.swing.ImageIcon( edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/clipboard.gif" ) );
	protected javax.swing.ImageIcon clipboardWithPaperIcon = new javax.swing.ImageIcon( edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/clipboardWithPaper.gif" ) );
	protected java.awt.Dimension size;
	protected java.awt.datatransfer.Transferable transferable;
	protected java.awt.dnd.DragSource dragSource = new java.awt.dnd.DragSource();
	protected boolean underDrag = false;
	protected boolean paintDropPotential = false;
	protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();

	public DnDClipboard() {
		setBorder( javax.swing.BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
		size = new java.awt.Dimension( clipboardIcon.getIconWidth(), clipboardIcon.getIconHeight() );
		setOpaque( false );
		dragSource.createDefaultDragGestureRecognizer( this, java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE | java.awt.dnd.DnDConstants.ACTION_LINK, new ClipboardDragGestureListener() );
		setDropTarget( new java.awt.dnd.DropTarget( this, new ClipboardDropTargetListener() ) );
		edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener( dropPotentialFeedbackListener );

		setToolTipText( "<html><font face=arial size=-1>Copy/Paste Clipboard<p><p>Drag and drop tiles <b>to</b> the clipboard to copy them.<p>Drag and drop tiles <b>from</b> the clipboard to paste them.</font></html>" );

		addMouseListener( new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
			
			public void singleClickResponse( java.awt.event.MouseEvent ev ) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( DnDClipboard.this.getToolTipText() );
			}
		} );
	}

	public void setTransferable( java.awt.datatransfer.Transferable transferable ) {
		this.transferable = transferable;
		repaint();
	}
public void clear (){
	setTransferable(null);
}
	
	public void paintComponent( java.awt.Graphics g ) {
		super.paintComponent( g );
		java.awt.Insets insets = getInsets();
		if( paintDropPotential ) {
			java.awt.Dimension size = getSize();
			if( underDrag ) {
				g.setColor( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight2" ) );
			} else {
				//g.setColor( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight" ) );
			}
			g.drawRect( 0, 0, size.width - 1, size.height - 1 );
			g.drawRect( 1, 1, size.width - 3, size.height  - 3);
		}
		if( (transferable != null) || underDrag  ) {
			clipboardWithPaperIcon.paintIcon( this, g, insets.left, insets.top );
		} else {
			clipboardIcon.paintIcon( this, g, insets.left, insets.top );
		}
	}

	
	public java.awt.Dimension getMinimumSize() {
		java.awt.Insets insets = getInsets();
		return new java.awt.Dimension( size.width + insets.left + insets.right, size.height + insets.top + insets.bottom );
	}
	
	public java.awt.Dimension getPreferredSize() {
		java.awt.Insets insets = getInsets();
		return new java.awt.Dimension( size.width + insets.left + insets.right, size.height + insets.top + insets.bottom );
	}
	
	public java.awt.Dimension getMaximumSize() {
		java.awt.Insets insets = getInsets();
		return new java.awt.Dimension( size.width + insets.left + insets.right, size.height + insets.top + insets.bottom );
	}

	public class ClipboardDragGestureListener implements java.awt.dnd.DragGestureListener {
		public void dragGestureRecognized( java.awt.dnd.DragGestureEvent dge ) {
			DnDManager.fireDragGestureRecognized( dge );
			if( DnDClipboard.this.transferable != null ) {
				dge.startDrag( java.awt.dnd.DragSource.DefaultCopyDrop, DnDClipboard.this.transferable, DnDManager.getInternalListener() );
				DnDManager.fireDragStarted( DnDClipboard.this.transferable, DnDClipboard.this );
			}
		}
	}

	public class ClipboardDropTargetListener implements java.awt.dnd.DropTargetListener {
		private boolean checkDrag( java.awt.dnd.DropTargetDragEvent dtde ) {
			try {
				java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
				edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor );
				if ( element instanceof edu.cmu.cs.stage3.alice.core.Sound ) {
					dtde.rejectDrag();
					return false;
				}
			} catch (Exception e) {
				
			}
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)){
				dtde.acceptDrag( dtde.getDropAction() );
				DnDClipboard.this.paintDropPotential = true;
				DnDClipboard.this.repaint();
				return true;
			}
			dtde.rejectDrag();
			return false;
		}

		public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
			underDrag = true;
			DnDClipboard.this.repaint();
		}

		public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
			underDrag = false;
			DnDClipboard.this.repaint();
		}

		public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
			underDrag = checkDrag( dtde );;
			DnDClipboard.this.repaint();
		}

		public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
		}

		public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
			java.awt.datatransfer.Transferable transferable = dtde.getTransferable();

			if( (DnDManager.getCurrentDragComponent() instanceof DnDClipboard) && (DnDManager.getCurrentDragComponent() != DnDClipboard.this) ) {
				DnDClipboard clipboard = (DnDClipboard)DnDManager.getCurrentDragComponent();
				clipboard.setTransferable( null );
			} else {
				java.awt.datatransfer.DataFlavor elementReferenceFlavor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Element.class );
				try {
					if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, elementReferenceFlavor ) ) {
						edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( elementReferenceFlavor );
						transferable = edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable( element.createCopyFactory() );
					}
				} catch( java.io.IOException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error dropping on clipboard.", e );
				} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error dropping on clipboard.", e );
				}
			}

			DnDClipboard.this.transferable = transferable;

			underDrag = false;
			DnDClipboard.this.repaint();

			dtde.acceptDrop( dtde.getDropAction() );
			dtde.getDropTargetContext().dropComplete( true );
		}
	}

	protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener {
		private void doCheck() {
			// always accept, for now
			DnDClipboard.this.paintDropPotential = false;
			DnDClipboard.this.repaint();
		}

		public void dragGestureRecognized( java.awt.dnd.DragGestureEvent dge ) {
			// do nothing for the gesture, wait until dragStarted
		}

		public void dragStarted() {
			//doCheck();
		}

		public void dragEnter( java.awt.dnd.DragSourceDragEvent dsde ) {
			//doCheck();
		}

		public void dragExit( java.awt.dnd.DragSourceEvent dse ) {
			doCheck();
		}

		public void dragOver( java.awt.dnd.DragSourceDragEvent dsde ) {
			//don't check here
		}

		public void dropActionChanged( java.awt.dnd.DragSourceDragEvent dsde ) {
			//doCheck();
		}

		public void dragDropEnd( java.awt.dnd.DragSourceDropEvent dsde ) {
			DnDClipboard.this.paintDropPotential = false;
			DnDClipboard.this.repaint();
		}
	}
}