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
public class FontPropertyViewController extends javax.swing.JButton implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected boolean omitPropertyName;
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
			FontPropertyViewController.this.refreshGUI();
		}
	};

	public FontPropertyViewController() {
		addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( FontPropertyViewController.this.property != null ) {
						boolean isFor3DText = FontPropertyViewController.this.property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Text3D;
						String sampleText = null;
						if( isFor3DText ) { // special-cased
							sampleText = ((edu.cmu.cs.stage3.alice.core.Text3D)FontPropertyViewController.this.property.getOwner()).text.getStringValue();
						}
						java.awt.Font currentFont = null;
						if( FontPropertyViewController.this.property.getValue() instanceof java.awt.Font ) {
							currentFont = (java.awt.Font)FontPropertyViewController.this.property.getValue();
						}
						edu.cmu.cs.stage3.alice.authoringtool.dialog.FontPanel fontPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.FontPanel( currentFont, ! isFor3DText, true, sampleText );
						if( edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog( fontPanel, "Choose a Font", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE ) == javax.swing.JOptionPane.OK_OPTION ) {
							java.awt.Font font = fontPanel.getChosenFont();
							if( font != null ) {
								FontPropertyViewController.this.property.set( font );
							}
						}
					}
				}
			}
		);
	}

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean omitPropertyName ) {
		clean();
		this.property = property;
		this.omitPropertyName = omitPropertyName;
		setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "propertyViewControllerBackground" ) );
		setMargin( new java.awt.Insets( 0, 4, 0, 4 ) );
		startListening();
		refreshGUI();
	}

	public void goToSleep() {
		stopListening();
	}

	public void wakeUp() {
		startListening();
	}

	public void clean() {
		stopListening();
	}

	public void die() {
		stopListening();
	}

	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
	}

	public void startListening() {
		if( property != null ) {
			property.addPropertyListener( propertyListener );
		}
	}

	public void stopListening() {
		if( property != null ) {
			property.removePropertyListener( propertyListener );
		}
	}

	protected void refreshGUI() {
		Object value = property.get();
		StringBuffer repr = new StringBuffer();

		if( ! omitPropertyName ) {
			repr.append( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property ) + " = " );
		}

		if( value instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
			repr.append( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameInContext( (edu.cmu.cs.stage3.alice.core.Element)value, property.getOwner() ) );
		} else if( value == null ) {
			repr.append( "<None>" );
		} else if( value instanceof java.awt.Font ) {
			java.awt.Font font = (java.awt.Font)value;
			repr.append( font.getFontName() );
			if( ! (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Text3D) ) {
				repr.append( ", " + font.getSize() );
			}
		} else {
			throw new RuntimeException( "Bad value: " + value );
		}

		setText( repr.toString() );
		revalidate();
		repaint();
	}
}
