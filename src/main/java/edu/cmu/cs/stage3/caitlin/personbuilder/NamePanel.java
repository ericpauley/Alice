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

package edu.cmu.cs.stage3.caitlin.personbuilder;

public class NamePanel extends javax.swing.JPanel {
	protected javax.swing.JTextField m_nameField = null;
	protected javax.swing.JTextField m_createdByField = null;

	public NamePanel() {
		m_nameField = new javax.swing.JTextField();
		m_createdByField = new javax.swing.JTextField("Anonymous");
		
		setLayout( new java.awt.GridBagLayout() );
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		gbc.weightx = 0.0;
		add( new javax.swing.JLabel( "Name:" ), gbc );

		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		add( m_nameField, gbc );

		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		gbc.weightx = 0.0;
		add( new javax.swing.JLabel( "Created By:" ), gbc );

		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		add( m_createdByField, gbc );
	}

	
	public String getName() {
		return m_nameField.getText();
	}
	public String getCreatedBy() {
		return m_createdByField.getText();
	}
}