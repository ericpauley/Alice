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
public class StringPropertyViewController extends TextFieldEditablePropertyViewController {
	protected javax.swing.JLabel stringLabel = new javax.swing.JLabel();
	protected boolean emptyStringWritesNull;
	private java.awt.Dimension minSize = new java.awt.Dimension( 20, 16 );

	public StringPropertyViewController() {
		stringLabel.setMinimumSize( minSize );
		textField.setColumns( 20 );
	}

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		set( property, allowExpressions, omitPropertyName, true, factory );
	}

	
	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, boolean emptyStringWritesNull, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		super.set( property, includeDefaults, allowExpressions, true, omitPropertyName, factory );
		this.emptyStringWritesNull = emptyStringWritesNull && (! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ));
		if( edu.cmu.cs.stage3.alice.core.response.Comment.class.isAssignableFrom( property.getOwner().getClass() ) || edu.cmu.cs.stage3.alice.core.question.userdefined.Comment.class.isAssignableFrom( property.getOwner().getClass() ) ) {
			stringLabel.setForeground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "commentForeground" ) );
			int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
			stringLabel.setFont( new java.awt.Font( "Helvetica", java.awt.Font.BOLD, (int)(13*fontSize/12.0) ) );
		} else {
			stringLabel.setForeground( javax.swing.UIManager.getColor( "Label.foreground" ) );
			stringLabel.setFont( javax.swing.UIManager.getFont( "Label.font" ) );
		}
		refreshGUI();
	}
	
	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions, boolean omitPropertyName, boolean emptyStringWritesNull, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		set( property, false, allowExpressions, omitPropertyName, emptyStringWritesNull, factory );
	}

	
	protected void setValueFromString( String valueString ) {
		if( valueString.trim().equals( "" ) ) {
			if( emptyStringWritesNull ) {
				valueString = null;
			}
		}
		((Runnable)factory.createItem( valueString )).run();
	}

	
	protected java.awt.Component getNativeComponent() {
		return stringLabel;
	}

	
	protected Class getNativeClass() {
		return String.class;
	}

	
	protected void updateNativeComponent() {
		stringLabel.setText( property.get().toString() );
	}

	
	protected void refreshGUI() {
		//if( (property.getValue() != null) && (property.getValue().toString()).trim().equals( "" ) ) {
		//	stringLabel.setPreferredSize( minSize );
		//} else {
		//	stringLabel.setPreferredSize( null );
		//}
		stringLabel.setPreferredSize( null );
		if( this.isAncestorOf( textField ) ) {
			remove( textField );
		}
		super.refreshGUI();
	}
}
