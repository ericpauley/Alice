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
public class TrashComponent extends javax.swing.JPanel implements java.awt.dnd.DropTargetListener {
	protected javax.swing.ImageIcon trashOpenIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "trashOpen" );
	protected javax.swing.ImageIcon trashClosedIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "trashClosed" );
	protected javax.swing.JLabel trashLabel = new javax.swing.JLabel( trashClosedIcon );
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected boolean paintDropPotential = false;
	protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
	protected boolean beingDroppedOn = false;

	public TrashComponent( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		setLayout( new java.awt.BorderLayout() );
		setOpaque( false );
		add( trashLabel, java.awt.BorderLayout.CENTER );
		this.setDropTarget( new java.awt.dnd.DropTarget( this, this ) );
		trashLabel.setDropTarget( new java.awt.dnd.DropTarget( trashLabel, this ) );
		edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener( dropPotentialFeedbackListener );

		setToolTipText( "<html><font face=arial size=-1>Trash<p><p>Drag and drop tiles here to delete them.</font></html>" );

		addMouseListener( new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
			
			public void singleClickResponse( java.awt.event.MouseEvent ev ) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( TrashComponent.this.getToolTipText() );
			}
		} );
	}

	
	public java.awt.Dimension getMaximumSize() {
		return trashLabel.getMaximumSize();
	}

	
	public void paintComponent( java.awt.Graphics g ) {
		if( paintDropPotential ) {
//			trashLabel.setEnabled( true );
			java.awt.Dimension size = getSize();
			if( beingDroppedOn ) {
				g.setColor( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight2" ) );
			} else {
				g.setColor( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight" ) );
			}
			g.drawRect( 0, 0, size.width - 1, size.height - 1 );
			g.drawRect( 1, 1, size.width - 3, size.height - 3 );
//		} else {
//			trashLabel.setEnabled( false );
		}
		super.paintComponent( g );
	}

	////////////////////////////////////
	// DropTargetListener interface
	////////////////////////////////////

	//TODO: remove duplicated code
	protected boolean checkTransferable( java.awt.datatransfer.Transferable transferable ) {
		if( DnDManager.getCurrentDragComponent() instanceof DnDClipboard ) {
			return true;
		} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ||
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor ) ||
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) ||
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) )
		{
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkDrag( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( DnDManager.getCurrentDragComponent() instanceof DnDClipboard ) {
			dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
			trashLabel.setIcon( trashOpenIcon );
			return true;
		} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ||
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor ) ||
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) ||
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) )
		{
			dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
			trashLabel.setIcon( trashOpenIcon );
			return true;
		} else {
			dtde.rejectDrag();
			return false;
		}
	}

	public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( checkDrag( dtde ) ) {
			beingDroppedOn = true;
			repaint();
		}
	}

	public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
		if( beingDroppedOn ) {
			beingDroppedOn = false;
			repaint();
		}
		trashLabel.setIcon( trashClosedIcon );
	}

	public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( ! checkDrag( dtde ) ) {
			beingDroppedOn = false;
			repaint();
		}
	}

	public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
		try {
			if( DnDManager.getCurrentDragComponent() instanceof DnDClipboard ) {
				dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
				DnDClipboard clipboard = (DnDClipboard)DnDManager.getCurrentDragComponent();
				clipboard.setTransferable( null );
				dtde.dropComplete( true );
			} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ) {
				dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
				java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
				edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor );
				//HACK
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable( element, authoringTool );
				deleteRunnable.run();
				dtde.dropComplete( true );				
			} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) ) {
				dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
				java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
				edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor );
				//HACK
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable( callToUserDefinedResponsePrototype.getActualResponse(), authoringTool );
				deleteRunnable.run();
				dtde.dropComplete( true );
			} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) ) {
				dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
				java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
				edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor );
				//HACK
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable deleteRunnable = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable( callToUserDefinedQuestionPrototype.getActualQuestion(), authoringTool );
				deleteRunnable.run();
				dtde.dropComplete( true );
			} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor ) ) {
				dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
				java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
				ObjectArrayPropertyItem item = (ObjectArrayPropertyItem)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor );
				item.objectArrayProperty.remove( item.getIndex() );
				dtde.dropComplete( true );
			} else {
				dtde.rejectDrop();
				dtde.dropComplete( false );
			}
		} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
			dtde.dropComplete( false );
		} catch( java.io.IOException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
			dtde.dropComplete( false );
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Drop didn't work.", t );
			dtde.dropComplete( false );
		}
		trashLabel.setIcon( trashClosedIcon );
	}

	public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
		checkDrag( dtde );
	}

	protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener {
		private void doCheck() {
			java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
			boolean transferableHasPotential = checkTransferable( transferable );
			if( TrashComponent.this.paintDropPotential != transferableHasPotential ) {
				TrashComponent.this.paintDropPotential = transferableHasPotential;
				TrashComponent.this.repaint();
			}
		}

		public void dragGestureRecognized( java.awt.dnd.DragGestureEvent dge ) {
			// do nothing for the gesture, wait until dragStarted
		}

		public void dragStarted() {
			beingDroppedOn = false;
			doCheck();
		}

		public void dragEnter( java.awt.dnd.DragSourceDragEvent dsde ) {
			doCheck();
		}

		public void dragExit( java.awt.dnd.DragSourceEvent dse ) {
			doCheck();
		}

		public void dragOver( java.awt.dnd.DragSourceDragEvent dsde ) {
			//don't check here
		}

		public void dropActionChanged( java.awt.dnd.DragSourceDragEvent dsde ) {
			doCheck();
		}

		public void dragDropEnd( java.awt.dnd.DragSourceDropEvent dsde ) {
			TrashComponent.this.paintDropPotential = false;
			TrashComponent.this.repaint();
		}
	}
}