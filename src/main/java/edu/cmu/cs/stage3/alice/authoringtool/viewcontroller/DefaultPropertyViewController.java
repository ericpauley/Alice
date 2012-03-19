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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt
 */
public class DefaultPropertyViewController extends PropertyViewController {
	protected javax.swing.JLabel label = new javax.swing.JLabel();

	@Override
	public void set(edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean includeOther, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, includeDefaults, allowExpressions, includeOther, omitPropertyName, factory);
		setPopupEnabled(true);
		refreshGUI();
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return label;
	}

	@Override
	protected Class getNativeClass() {
		return Object.class;
	}

	@Override
	protected void updateNativeComponent() {
		label.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property.get(), property));
	}
}
