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

import javax.swing.tree.TreeModel;

/**
 * TreeTableModel is the model used by a JTreeTable. It extends TreeModel
 * to add methods for getting inforamtion about the set of columns each
 * node in the TreeTableModel may have. Each column, like a column in
 * a TableModel, has a name and a type associated with it. Each node in
 * the TreeTableModel can return a value for each of the columns and
 * set that value if isCellEditable() returns true.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public interface TreeTableModel extends TreeModel
{
	/**
	 * Returns the number ofs availible column.
	 */
	public int getColumnCount();

	/**
	 * Returns the name for column number <code>column</code>.
	 */
	public String getColumnName(int column);

	/**
	 * Returns the type for column number <code>column</code>.
	 */
	public Class getColumnClass(int column);

	/**
	 * Returns the value to be displayed for node <code>node</code>,
	 * at column number <code>column</code>.
	 */
	public Object getValueAt(Object node, int column);

	public Class getTypeAt( Object node, int column );
	public boolean isNullValidAt( Object node, int column );

	/**
	 * Indicates whether the the value for node <code>node</code>,
	 * at column number <code>column</code> is editable.
	 */
	public boolean isCellEditable(Object node, int column);

	/**
	 * Sets the value for node <code>node</code>,
	 * at column number <code>column</code>.
	 */
	public void setValueAt(Object aValue, Object node, int column);
}
