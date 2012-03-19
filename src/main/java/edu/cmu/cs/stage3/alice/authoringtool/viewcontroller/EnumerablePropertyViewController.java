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
public class EnumerablePropertyViewController extends PropertyViewController {
	protected javax.swing.JLabel enumerableLabel = new javax.swing.JLabel();
	protected Class valueClass;

	// public EnumerablePropertyViewController(
	// edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions,
	// boolean omitPropertyName, final
	// edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
	// this.addMouseListener(
	// new java.awt.event.MouseAdapter() {
	// public void mouseReleased( java.awt.event.MouseEvent ev ) {
	// if( (ev.getX() >= 0) && (ev.getX() < ev.getComponent().getWidth()) &&
	// (ev.getY() >= 0) && (ev.getY() < ev.getComponent().getHeight()) ) {
	// EnumerablePropertyViewController.this.popupButton.doClick();
	// }
	// }
	// }
	// );
	// }

	public void set(edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory) {
		super.set(property, true, allowExpressions, false, omitPropertyName, factory);
		valueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(property);
		if (!edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(valueClass)) {
			throw new IllegalArgumentException("valueClass of property " + property + " is not an Enumerable; instead: " + valueClass);
		}
		setPopupEnabled(true);
		refreshGUI();
	}

	@Override
	protected java.awt.Component getNativeComponent() {
		return enumerableLabel;
	}

	@Override
	protected Class getNativeClass() {
		return edu.cmu.cs.stage3.util.Enumerable.class;
	}

	@Override
	protected void updateNativeComponent() {
		String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property.get(), property);
		enumerableLabel.setText(text);
	}
}
