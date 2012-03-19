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

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author Jason Pratt
 */
public class DefaultElementTreeCellRenderer extends DefaultTreeCellRenderer {

	// TODO: make this more sophisticated

	@Override
	public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		String stringValue;
		if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
			stringValue = (String) ((edu.cmu.cs.stage3.alice.core.Element) value).name.getValue();
		} else {
			stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
		}

		this.hasFocus = hasFocus;
		setText(stringValue);
		if (sel) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}

		setIcon(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(value));

		setComponentOrientation(tree.getComponentOrientation());

		selected = sel;

		return this;
	}
}