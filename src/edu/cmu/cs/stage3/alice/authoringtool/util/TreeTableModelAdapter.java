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

import javax.swing.table.AbstractTableModel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class TreeTableModelAdapter extends AbstractTableModel implements TypedTableModel {
	JTree tree;
	TreeTableModel treeTableModel;

	public TreeTableModelAdapter( TreeTableModel treeTableModel, JTree tree ) {
		this.tree = tree;
		this.treeTableModel = treeTableModel;

		tree.addTreeExpansionListener(
			new TreeExpansionListener() {
				// Don't use fireTableRowsInserted() here;
				// the selection model would get  updated twice.
				public void treeExpanded(TreeExpansionEvent event) {
					TreeTableModelAdapter.this.fireTableDataChanged();
				}
				public void treeCollapsed(TreeExpansionEvent event) {
					TreeTableModelAdapter.this.fireTableDataChanged();
				}
			}
		);
	}

  // Wrappers, implementing TableModel interface.

	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	
	public String getColumnName(int column) {
		return treeTableModel.getColumnName( column );
	}

	
	public Class getColumnClass(int column) {
		return treeTableModel.getColumnClass( column );
	}

	public int getRowCount() {
		return tree.getRowCount();
	}

	public Object nodeForRow( int row ) {
		TreePath treePath = tree.getPathForRow( row );
		return treePath.getLastPathComponent();
	}

	public Object getValueAt( int row, int column ) {
		return treeTableModel.getValueAt( nodeForRow(row), column );
	}

	public Class getTypeAt( int row, int column ) {
		return treeTableModel.getTypeAt( nodeForRow(row), column );
	}

	public boolean isNullValidAt( int row, int column ) {
		return treeTableModel.isNullValidAt( nodeForRow( row ), column );
	}

	
	public boolean isCellEditable( int row, int column ) {
		 return treeTableModel.isCellEditable( nodeForRow(row), column );
	}

	
	public void setValueAt( Object value, int row, int column ) {
		treeTableModel.setValueAt( value, nodeForRow(row), column );
	}
}

