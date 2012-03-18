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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */

public class Add3DTextPanel extends AbstractFontPanel {
	public Add3DTextPanel() {
		super( "The quick brown fox", true );
	}
	public edu.cmu.cs.stage3.alice.core.Text3D createText3D() {
		edu.cmu.cs.stage3.alice.core.Text3D text3D = new edu.cmu.cs.stage3.alice.core.Text3D();
		text3D.text.set( textArea.getText().trim() );
		text3D.font.set( fontChooser.getChosenFont() );
		text3D.isFirstClass.set( Boolean.TRUE );
		String name = textArea.getText().trim();
		name = name.replace( '\\', '_' );
		name = name.replace( '/', '_' );
		name = name.replace( ':', '_' );
		name = name.replace( '*', '_' );
		name = name.replace( '?', '_' );
		name = name.replace( '"', '_' );
		name = name.replace( '<', '_' );
		name = name.replace( '>', '_' );
		name = name.replace( '|', '_' );
		name = name.replace( '.', '_' );
		name = name.replace( '\n', '_' );
		name = name.replace( '\t', '_' );

		if( name.length() > 20 ) {
			name = name.substring( 0, 21 ).trim();
		} 
		if (name.compareTo(" ")==0) {
			text3D.name.set( name );
		} else {
			text3D.name.set( "3D Text" );
		}
		text3D.create3DTextGeometry();
		return text3D;
	}
}

//import java.awt.GridBagConstraints;
//import java.awt.Insets;
//
//public class Add3DTextPanel extends javax.swing.JPanel {
//	private FontChooser fontChooser = new FontChooser( false, false );
//	private javax.swing.JTextArea textArea = new javax.swing.JTextArea();
//
//	public Add3DTextPanel() {
//		fontChooser.setFontSize( 28 );
//		fontChooser.addChangeListener(
//			new javax.swing.event.ChangeListener() {
//				public void stateChanged( javax.swing.event.ChangeEvent ev ) {
//					textArea.setFont( fontChooser.getChosenFont() );
//				}
//			}
//		);
//
//		setLayout( new java.awt.GridBagLayout() );
//		textArea.setText( "The quick brown fox" );
//		textArea.selectAll();
//		textArea.setFont( fontChooser.getChosenFont() );
//
//		javax.swing.JScrollPane textScrollPane = new javax.swing.JScrollPane();
//		textScrollPane.getViewport().add(textArea, null);
//
//		add( new javax.swing.JLabel( "Text:" ), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 4, 4), 0, 0));
//		add( textScrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 4, 0), 0, 0));
//		add( new javax.swing.JLabel( "Font:" ), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
//		add( fontChooser, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//
//		setPreferredSize( new java.awt.Dimension( 500, 200 ) );
//	}
//
//	public edu.cmu.cs.stage3.alice.core.Text3D createText3D() {
//		edu.cmu.cs.stage3.alice.core.Text3D text3D = new edu.cmu.cs.stage3.alice.core.Text3D();
//		text3D.text.set( textArea.getText().trim() );
//		text3D.font.set( fontChooser.getChosenFont() );
//		text3D.isFirstClass.set( Boolean.TRUE );
//		String name = textArea.getText().trim();
//		name = name.replace( '\\', '_' );
//		name = name.replace( '/', '_' );
//		name = name.replace( ':', '_' );
//		name = name.replace( '*', '_' );
//		name = name.replace( '?', '_' );
//		name = name.replace( '"', '_' );
//		name = name.replace( '<', '_' );
//		name = name.replace( '>', '_' );
//		name = name.replace( '|', '_' );
//		name = name.replace( '.', '_' );
//		name = name.replace( '\n', '_' );
//		name = name.replace( '\t', '_' );
//
//		if( name.length() > 20 ) {
//			name = name.substring( 0, 21 ).trim();
//		}
//		text3D.name.set( name );
//		text3D.create3DTextGeometry();
//		return text3D;
//	}
//}