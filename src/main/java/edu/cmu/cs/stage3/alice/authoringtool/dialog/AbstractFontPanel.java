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

import java.awt.GridBagConstraints;
import java.awt.Insets;

public abstract class AbstractFontPanel extends javax.swing.JPanel {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser fontChooser = new edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser( false, false );
	protected javax.swing.JTextArea textArea = new javax.swing.JTextArea();

	public AbstractFontPanel( String text, boolean isTextAreaEnabled ) {
		fontChooser.setFontSize( 28 );
		fontChooser.addChangeListener(
			new javax.swing.event.ChangeListener() {
				public void stateChanged( javax.swing.event.ChangeEvent ev ) {
					textArea.setFont( fontChooser.getChosenFont() );
				}
			}
		);

		setLayout( new java.awt.GridBagLayout() );
		textArea.setText( text );
		textArea.setEnabled( isTextAreaEnabled );
		textArea.selectAll();
		textArea.setFont( fontChooser.getChosenFont() );
		

		javax.swing.JScrollPane textScrollPane = new javax.swing.JScrollPane();
		textScrollPane.getViewport().add(textArea, null);

		add( new javax.swing.JLabel( "Text:" ), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 4, 4), 0, 0));
		add( textScrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 4, 0), 0, 0));
		add( new javax.swing.JLabel( "Font:" ), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
		add( fontChooser, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		setPreferredSize( new java.awt.Dimension( 500, 200 ) );
	}


	public java.awt.Font getChosenFont() {
		return fontChooser.getChosenFont();
	}
}