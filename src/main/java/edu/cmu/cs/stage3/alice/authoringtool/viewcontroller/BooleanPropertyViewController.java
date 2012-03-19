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
public class BooleanPropertyViewController extends PropertyViewController {
	protected javax.swing.JCheckBox checkBox = new javax.swing.JCheckBox();
	protected javax.swing.JLabel booleanLabel = new javax.swing.JLabel();
	protected java.awt.Color originalForegroundColor;

	public BooleanPropertyViewController() {
		originalForegroundColor = booleanLabel.getForeground(); // a bit of a
																// hack; default
																// color should
																// be defined
																// somewhere

		checkBox.setOpaque(false);
		checkBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				((Runnable) factory.createItem(checkBox.isSelected() ? Boolean.TRUE : Boolean.FALSE)).run();
			}
		});
	}

	public void set(edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, includeDefaults, allowExpressions, false, omitPropertyName, factory);
		if (omitPropertyName) {
			add(javax.swing.Box.createHorizontalStrut(8), java.awt.BorderLayout.WEST);
		}

		refreshGUI();
	}

	@Override
	public void setEditingEnabled(boolean editingEnabled) {
		super.setEditingEnabled(editingEnabled);
		checkBox.setEnabled(editingEnabled);
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return booleanLabel;
	}

	@Override
	protected Class getNativeClass() {
		return Boolean.class;
	}

	@Override
	protected void updateNativeComponent() {
		// boolean b = ((Boolean)property.get()).booleanValue();
		// if( checkBox.isSelected() != b ) {
		// checkBox.setSelected( b );
		// }
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.And || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.Or || property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.Not) { // hack
																																																																																																								// for
																																																																																																								// red,
																																																																																																								// unfinished
																																																																																																								// look
		// if( property.getValue().equals( Boolean.TRUE ) ||
		// property.getValue().equals( Boolean.FALSE ) ) {
		// booleanLabel.setForeground( new java.awt.Color( 200, 30, 30 ) );
		// } else {
		// booleanLabel.setForeground( originalForegroundColor ); // a bit of a
		// hack; default color should be defined somewhere
		// }
		} else {
			booleanLabel.setForeground(originalForegroundColor); // a bit of a
																	// hack;
																	// default
																	// color
																	// should be
																	// defined
																	// somewhere
		}
		booleanLabel.setText(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property.getValue(), property, property.getOwner().data));
	}
}
