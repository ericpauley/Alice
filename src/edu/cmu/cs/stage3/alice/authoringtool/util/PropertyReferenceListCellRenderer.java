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

/**
 * @author Jason Pratt
 */
public class PropertyReferenceListCellRenderer extends javax.swing.DefaultListCellRenderer {
	
	public java.awt.Component getListCellRendererComponent( javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
		setComponentOrientation( list.getComponentOrientation() );

		if( isSelected ) {
			setBackground( list.getSelectionBackground() );
			setForeground( list.getSelectionForeground() );
		} else {
			setBackground( list.getBackground() );
			setForeground( list.getForeground() );
		}
		if( value instanceof edu.cmu.cs.stage3.alice.core.reference.PropertyReference ) {
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference reference = (edu.cmu.cs.stage3.alice.core.reference.PropertyReference)value;
			String text = "";
			if( reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
				text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( reference.getProperty().getOwner(), true ) + "." + reference.getProperty().getName() + "[" + index + "] -> " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( reference.getProperty().get(), true );
			} else {
				text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( reference.getProperty().getOwner(), true ) + "." + reference.getProperty().getName() + " -> " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( reference.getProperty().get(), true );
			}
			text = edu.cmu.cs.stage3.alice.authoringtool.dialog.DeleteContentPane.getDeleteString(reference);
			//javax.swing.ImageIcon icon = DeleteDialog.getDeleteIcon(reference);
			setIcon( null );
			setText( text );
			
			
			
		} else if( value instanceof javax.swing.Icon ) {
			setIcon( (javax.swing.Icon)value );
			setText( "" );
		} else {
			setIcon( null );
			setText( ( value == null ) ? "" : value.toString() );
		}

		setEnabled( list.isEnabled() );
		setFont( list.getFont() );
		setBorder( ( cellHasFocus ) ? javax.swing.UIManager.getBorder( "List.focusCellHighlightBorder" ) : noFocusBorder );

		return this;
	}
}