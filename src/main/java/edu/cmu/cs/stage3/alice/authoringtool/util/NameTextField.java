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
 * @author Dennis Cosgrove
 */
public class NameTextField extends javax.swing.JTextField {
	private edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback okButtonCallback;
	private edu.cmu.cs.stage3.alice.core.Element m_parent;
	
	public NameTextField( edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback okButtonCallback ) {
		this.okButtonCallback = okButtonCallback;
		getDocument().addDocumentListener( new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				NameTextField.this.refresh();
			}
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				NameTextField.this.refresh();
			}
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				NameTextField.this.refresh();
			}
		});
		addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				textFieldAction();
			}
		});
	}
	
	public void setParent( edu.cmu.cs.stage3.alice.core.Element parent ) {
		m_parent = parent;
		refresh();
	}
	
	private void textFieldAction(){
		okButtonCallback.doAction();
	}
	
	private boolean isNameValid() {
		if( m_parent != null ) {
			String name = getText();
			if( edu.cmu.cs.stage3.alice.core.Element.isPotentialNameValid( name ) ) {
				if( m_parent.getChildNamedIgnoreCase( name ) == null ) {
					return true;
				}
			}
		}
		return false;
	}
	private void refresh() {
		if( isNameValid() ) {
			okButtonCallback.setValidity(this, true);
			setForeground( java.awt.Color.black );
		} else {
			okButtonCallback.setValidity(this, false);
			setForeground( java.awt.Color.red );
		}
	}
}
