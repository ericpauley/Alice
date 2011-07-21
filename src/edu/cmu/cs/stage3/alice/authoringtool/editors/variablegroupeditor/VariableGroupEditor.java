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

package edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor;

/**
 * @author Jason Pratt
 */
public class VariableGroupEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, java.awt.dnd.DropTargetListener {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables;
	protected javax.swing.JButton newVariableButton = new javax.swing.JButton( "create new variable" );
	protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane newVariableDialog;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

	protected int lineLocation = -1;
	protected int variablePosition = 0;

	public static final edu.cmu.cs.stage3.alice.authoringtool.util.Configuration variableConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( VariableGroupEditor.class.getPackage() );

	public VariableGroupEditor() {
		guiInit();
	}

	private void guiInit() {
		setLayout( new java.awt.GridBagLayout() );
		setBackground( java.awt.Color.white );
		newVariableButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		newVariableButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		newVariableButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( authoringTool != null ) {
						edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool.showNewVariableDialog( "create new variable", variables.getOwner());
						if( variable != null ) {
							authoringTool.getUndoRedoStack().startCompound();
							try {
								variables.getOwner().addChild( variable );
								variables.add( variable );
							} finally {
								authoringTool.getUndoRedoStack().stopCompound();
							}
						}
					}
				}
			}
		);
		addContainerListener( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener() );
		setDropTarget( new java.awt.dnd.DropTarget( this, this ) );
		newVariableButton.setToolTipText( "<html><font face=arial size=-1>Open New Variable Dialog.<p><p>Variables allow you to store information.<p>You may choose among several types<p>of information (like Numbers, Booleans, and Objects).</font></html>" );

		refreshGUI();
	}

	public javax.swing.JComponent getJComponent() {
		return this;
	}

	public void setVariableObjectArrayProperty( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables ) {
		if( this.variables != null ) {
			this.variables.removeObjectArrayPropertyListener( this );
		}

		this.variables = variables;

		if( this.variables != null ) {
			this.variables.addObjectArrayPropertyListener( this );
		}
		refreshGUI();
	}

	public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
	}

	public javax.swing.JButton getNewVariableButton() {
		return newVariableButton;
	}

	public void refreshGUI() {
		this.removeAll();
		if( variables != null ) {
			int count = 0;
			for( int i = 0; i < variables.size(); i++ ) {
				if( variables.get( i ) instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
					final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)variables.get( i );
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory( variable.value );
//						public Object createItem( final Object value ) {
//							return new Runnable() {
//								public void run() {
//									variable.value.set( value );
//								}
//							};
//						}
//					};
					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableGUI( variable, true, factory );
					if( gui != null ) {
						this.add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to create gui for variable: " + variable, null );
					}
				}
			}

			this.add( newVariableButton, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 8, 2, 8, 2 ), 0, 0 ) );
			newVariableButton.setDropTarget( new java.awt.dnd.DropTarget( newVariableButton, this ) );
			java.awt.Component glue = javax.swing.Box.createGlue();
			this.add( glue, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
			glue.setDropTarget( new java.awt.dnd.DropTarget( glue, this ) );
		}
		revalidate();
		repaint();
	}

	// line drawing
	
	public void paintComponent( java.awt.Graphics g ) {
		super.paintComponent( g );
		if( lineLocation > -1 ) {
			java.awt.Rectangle bounds = getBounds();
			g.setColor( java.awt.Color.black );
			g.fillRect( 0, lineLocation, bounds.width, 2 );
		}
	}


	///////////////////////////////////////////////
	// DropTargetListener interface
	///////////////////////////////////////////////

	protected void calculateLineLocation( int mouseY ) {
		int numSpots = variables.size() + 1;
		int[] spots = new int[numSpots];
		spots[0] = 0;
		for( int i = 1; i < numSpots; i++ ) {
			java.awt.Component c = getComponent( i - 1 );
			spots[i] = c.getBounds().y + c.getBounds().height + 1; // assumes gridBagConstraints insets.bottom == 2
		}

		int closestSpot = -1;
		int minDist = Integer.MAX_VALUE;
		for( int i = 0; i < numSpots; i++ ) {
			int d = Math.abs( mouseY - spots[i] );
			if( d < minDist ) {
				minDist = d;
				closestSpot = i;
			}
		}

		variablePosition = closestSpot;
		lineLocation = spots[closestSpot];
	}

	public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
		java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
		try {
			edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor );
			if( variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ){
				lineLocation = -1;
				dtde.rejectDrag();
			} else {
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor ) ) {
					dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );	
					int mouseY = javax.swing.SwingUtilities.convertPoint( dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this ).y;
					calculateLineLocation( mouseY );
				} else {
					lineLocation = -1;
					dtde.rejectDrag();
				}
			}		
		} catch (Exception e) {}
		
		repaint();
	}

	public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
		java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
		try {
			edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor );
			if( variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ){
				lineLocation = -1;
				dtde.rejectDrag();
			} else {
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor ) ) {
					dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );	
					int mouseY = javax.swing.SwingUtilities.convertPoint( dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this ).y;
					calculateLineLocation( mouseY );
				} else {
					lineLocation = -1;
					dtde.rejectDrag();
				}
			}		
		} catch (Exception e) {}
		
		repaint();
	}

	public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
		java.awt.datatransfer.Transferable transferable = dtde.getTransferable();

		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor ) ) {
			dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
			try {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor );
				if( variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ){
					dtde.dropComplete( false );
				} else {
					if( variables.contains( variable ) ) {
						if(	variablePosition > variables.indexOf( variable ) ) {
							variablePosition--;
						}
						variables.remove( variable );
					}
					variables.add( variablePosition, variable );
					dtde.dropComplete( true );
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
		} else {
			dtde.rejectDrop();
			dtde.dropComplete( false );
		}
		lineLocation = -1;
		repaint();
	}

	public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor ) ) {
			dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
		} else {
			dtde.rejectDrag();
		}
	}

	public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
		lineLocation = -1;
		repaint();
	}


	///////////////////////////////////////////////
	// ChildrenListener interface
	///////////////////////////////////////////////

	public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}

	public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
		refreshGUI();
	}


	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}

	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
}