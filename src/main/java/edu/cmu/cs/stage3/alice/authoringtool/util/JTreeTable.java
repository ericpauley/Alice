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

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class JTreeTable extends JTable {
	protected TreeTableCellRenderer tree;

	public JTreeTable( TreeTableModel treeTableModel ) {
		super();

		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer( treeTableModel );

		// Install a tableModel representing the visible rows in the tree.
		super.setModel( new TreeTableModelAdapter( treeTableModel, tree ) );

		// Force the JTable and JTree to share their row selection models.
		tree.setSelectionModel( new DefaultTreeSelectionModel() {
			// Extend the implementation of the constructor, as if:
		/* public this() */ {
				setSelectionModel(listSelectionModel);
			}
		});
		// Make the tree and table row heights the same.
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor.
		setDefaultRenderer( TreeTableModel.class, tree );
		setDefaultEditor( TreeTableModel.class, new TreeTableCellEditor() );

		setShowGrid( false );
		setIntercellSpacing( new Dimension( 0, 0 ) );
	}

	public JTree getTree() {
		return tree;
	}

	public Object getNodeAtPoint( java.awt.Point point ) {
		return ((TreeTableModelAdapter)getModel()).nodeForRow( this.rowAtPoint( point ) );
	}

	/* Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 */
	
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	// prevent keystrokes from starting an edit -JFP
	
	protected boolean processKeyBinding( javax.swing.KeyStroke ks, java.awt.event.KeyEvent e, int condition, boolean pressed ) {
		return false;
	}

	//
	// The renderer used to display the tree nodes, a JTree.
	//
	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
		protected int visibleRow;

		public TreeTableCellRenderer( TreeModel model ) {
			super( model );
		}

		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds( x, 0, w, JTreeTable.this.getHeight() );
		}

		
		public void paint( Graphics g ) {
			g.translate( 0, -visibleRow * getRowHeight() );
			super.paint( g );
		}

		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
			if( isSelected )
				setBackground( table.getSelectionBackground() );
			else
				setBackground( table.getBackground() );

			visibleRow = row;
			return this;
		}
	}

	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int r, int c ) {
			return tree;
		}

		public Object getCellEditorValue() {
			return null;
		}
	}

	
	public void editingCanceled( javax.swing.event.ChangeEvent ev ) {
		super.editingCanceled( ev );
	}

	
	public void removeEditor() {
		super.removeEditor();
	}
}
