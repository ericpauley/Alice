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
public class ElementTreeCellEditor extends ElementTreeCellRenderer implements javax.swing.tree.TreeCellEditor {
	protected javax.swing.JTextField textField;
	protected java.util.HashSet cellEditorListeners;
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected long lastClickTime;
	protected long editDelay = 500;
	//protected boolean isSelected; // determined solely from isCellEditable calls, not from getTreeCellEditorComponent

	synchronized protected void initializeIfNecessary() {
		if( textField == null ) {
			textField = new javax.swing.JTextField();
			cellEditorListeners = new java.util.HashSet();

			elementPanel.remove( elementLabel );
			elementPanel.add( textField, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 2, 0, 2 ), 0, 0 ) );

			textField.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed( java.awt.event.ActionEvent ev ) {
						stopCellEditing();
					}
				}
			);
			textField.addKeyListener(
				new java.awt.event.KeyAdapter() {
					
					public void keyPressed( java.awt.event.KeyEvent ev ) {
						if( ev.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE ) {
							cancelCellEditing();
						}
					}
				}
			);
			textField.addFocusListener(
				new java.awt.event.FocusAdapter() {
					
					public void focusLost( java.awt.event.FocusEvent ev ) {
						if( ! ev.isTemporary() ) {
							stopCellEditing();
						}
					}
				}
			);

			dndPanel.addMouseListener( new java.awt.event.MouseAdapter() {
				
				public void mousePressed( java.awt.event.MouseEvent ev ) {
					ElementTreeCellEditor.this.stopCellEditing();
				}
			} );
		}
	}

	public java.awt.Component getTreeCellEditorComponent( javax.swing.JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row ) {
		initializeIfNecessary();
		if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			element = (edu.cmu.cs.stage3.alice.core.Element)value;
			iconLabel.setIcon( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( element ) );
			textField.setText( element.name.getStringValue() );
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error: not an Element: " + value, null );
		}
		return this;
	}

	public void addCellEditorListener( javax.swing.event.CellEditorListener listener ) {
		initializeIfNecessary();
		cellEditorListeners.add( listener );
	}

	public void removeCellEditorListener( javax.swing.event.CellEditorListener listener ) {
		initializeIfNecessary();
		cellEditorListeners.remove( listener );
	}

	public void cancelCellEditing() {
		initializeIfNecessary();
		textField.setText( element.name.getStringValue() );
		fireCellEditingCancelled();
	}

	public boolean stopCellEditing() {
		initializeIfNecessary();
		try {
			element.name.set( textField.getText() );
			fireCellEditingStopped();
			return true;
		} catch( edu.cmu.cs.stage3.alice.core.IllegalNameValueException e ) {
//			ErrorDialog.showErrorDialog( e.getMessage(), e );
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( e.getMessage(), "Error setting name", javax.swing.JOptionPane.ERROR_MESSAGE );
			return false;
		}
	}

	public Object getCellEditorValue() {
		initializeIfNecessary();
		return textField.getText();
	}

	synchronized public boolean isCellEditable( java.util.EventObject ev ) {
		boolean isSelected = false;
		if( (ev instanceof java.awt.event.MouseEvent) && (ev.getSource() instanceof javax.swing.JTree) ) {
			java.awt.event.MouseEvent mev = (java.awt.event.MouseEvent)ev;
			javax.swing.JTree tree = (javax.swing.JTree)ev.getSource();
			int row = tree.getRowForLocation( mev.getX(), mev.getY() );
			isSelected = tree.isRowSelected( row );
		}

		if( ev instanceof java.awt.event.MouseEvent ) {
			long time = System.currentTimeMillis();

			java.awt.event.MouseEvent mev = (java.awt.event.MouseEvent)ev;
			if( javax.swing.SwingUtilities.isLeftMouseButton( mev ) ) {
				if( mev.getClickCount() > 2 ) {
					return true;
				} else if( isSelected && ((time - lastClickTime) > editDelay) ) {
					return true;
				}
			}
			lastClickTime = time;
		} else if( ev == null ) {
			return true;
		}

		return false;
	}

	public boolean shouldSelectCell( java.util.EventObject ev ) {
		return true;
	}

	public void selectText() {
		initializeIfNecessary();
		//System.out.println( "selectAll" );
		textField.selectAll();
	}

	protected void fireCellEditingCancelled() {
		initializeIfNecessary();
		javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent( this );
		for( java.util.Iterator iter = cellEditorListeners.iterator(); iter.hasNext(); ) {
			((javax.swing.event.CellEditorListener)iter.next()).editingCanceled( ev );
		}
	}

	protected void fireCellEditingStopped() {
		initializeIfNecessary();
		javax.swing.event.ChangeEvent ev = new javax.swing.event.ChangeEvent( this );
		for( java.util.Iterator iter = cellEditorListeners.iterator(); iter.hasNext(); ) {
			((javax.swing.event.CellEditorListener)iter.next()).editingStopped( ev );
		}
	}
}