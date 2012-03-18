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
public class KeyCodePropertyViewController extends PropertyViewController {
	protected javax.swing.JLabel keyLabel = new javax.swing.JLabel();

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		super.set( property, true, allowExpressions, false, true, factory );
		refreshGUI();
	}

	
	protected java.awt.Component getNativeComponent() {
		return keyLabel;
	}

	
	protected Class getNativeClass() {
		return Integer.class;
	}

	
	protected void updateNativeComponent() {
		javax.swing.ImageIcon icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( property.get() );
		if( icon != null ) {
			keyLabel.setText( null );
			keyLabel.setIcon( icon );
		} else {
			keyLabel.setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property.get(), property, property.getOwner().data ) );
			keyLabel.setIcon( null );
		}
	}
	
	
	public void getHTML(StringBuffer toWriteTo){
		toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property.get(), property, property.getOwner().data ) );
	}

//	protected void refreshGUI() {
//		if( this.isAncestorOf( textField ) ) {
//			remove( textField );
//		}
//		super.refreshGUI();
//	}
}
