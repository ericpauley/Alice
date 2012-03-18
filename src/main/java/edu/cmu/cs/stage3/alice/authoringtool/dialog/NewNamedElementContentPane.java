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
public abstract class NewNamedElementContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	private javax.swing.JButton m_okButton = new javax.swing.JButton( "OK" );
	private javax.swing.JButton m_cancelButton = new javax.swing.JButton( "Cancel" );
	private java.util.HashMap validityHashmap = new java.util.HashMap();
	private edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback validityChecker = new edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback(){
		public void setValidity(Object source, boolean value){
			okButtonEnabler(source, value);
		}
		
		public void doAction(){
			if (m_okButton.isEnabled()){
				m_okButton.doClick();
			}
		}
	};
	
	private edu.cmu.cs.stage3.alice.authoringtool.util.NameTextField m_nameTextField = new edu.cmu.cs.stage3.alice.authoringtool.util.NameTextField( validityChecker );

	public NewNamedElementContentPane() {
		m_nameTextField.setMinimumSize(new java.awt.Dimension(4, 26));
		m_nameTextField.setPreferredSize(new java.awt.Dimension(4, 26));


		setLayout( new java.awt.GridBagLayout() );
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		initTopComponents( gbc );
		initBottomComponents( gbc );		
	}

	protected void initTopComponents( java.awt.GridBagConstraints gbc ) {
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		gbc.insets.top = 8;
		gbc.insets.left = 8;
		add( new javax.swing.JLabel( "Name:" ), gbc );
		gbc.insets.right = 8;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		add( m_nameTextField, gbc );
		gbc.weightx = 0.0;
	}
	protected void initBottomComponents( java.awt.GridBagConstraints gbc ) {
		javax.swing.JPanel okCancelPanel = new javax.swing.JPanel();
		java.awt.GridBagConstraints gbcOKCancel = new java.awt.GridBagConstraints();
		okCancelPanel.add( m_okButton, gbcOKCancel );
		okCancelPanel.add( m_cancelButton, gbcOKCancel );
		gbc.anchor = java.awt.GridBagConstraints.SOUTH;
		add( okCancelPanel, gbc );
	}

	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.addActionListener( l );
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.removeActionListener( l );
	}
	
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.addActionListener( l );
	}
	
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.removeActionListener( l );
	}

	public void reset( edu.cmu.cs.stage3.alice.core.Element parent ) {
		m_nameTextField.setText( "" );
		m_nameTextField.setParent( parent );
		m_nameTextField.grabFocus();
	}
	
	public String getNameValue() {
		return m_nameTextField.getText();
	}
	
	private void okButtonEnabler(Object source, boolean value){
		validityHashmap.put(source, new Boolean(value));
		java.util.Iterator valueIterator = validityHashmap.values().iterator();
		boolean isEnableable = true;
		while (valueIterator.hasNext()){
			Object currentValue = valueIterator.next();
			if (currentValue instanceof Boolean){
				if (!((Boolean)currentValue).booleanValue()){
					isEnableable = false;
					break;
				}
			}
		}
		m_okButton.setEnabled(isEnableable);
	}

	protected edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback getValidityChecker(){
		return validityChecker;
	}
}