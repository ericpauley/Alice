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

import javax.swing.tree.DefaultTreeCellEditor;

/**
 * @author Jason Pratt
 */
public class DefaultElementTreeCellEditor extends DefaultTreeCellEditor {
	public DefaultElementTreeCellEditor(javax.swing.JTree tree, javax.swing.tree.DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}

	public DefaultElementTreeCellEditor(javax.swing.JTree tree, javax.swing.tree.DefaultTreeCellRenderer renderer, javax.swing.tree.TreeCellEditor editor) {
		super(tree, renderer, editor);
	}

	@Override
	public java.awt.Component getTreeCellEditorComponent(javax.swing.JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
			return super.getTreeCellEditorComponent(tree, ((edu.cmu.cs.stage3.alice.core.Element) value).name.getStringValue(), isSelected, expanded, leaf, row);
		} else {
			return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		}
	}

	@Override
	protected void prepareForEditing() {
		super.prepareForEditing();
		if (editingComponent instanceof javax.swing.JTextField) {
			((javax.swing.JTextField) editingComponent).selectAll();
		}
	}
}