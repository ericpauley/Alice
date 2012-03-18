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
public class ElementListCellRenderer extends javax.swing.DefaultListCellRenderer {
	public final static int JUST_NAME = 0;
	public final static int DEFAULT_REPR = 1;
	public final static int FULL_KEY = 2;

	protected int verbosity;

	public ElementListCellRenderer( int verbosity ) {
		this.verbosity = verbosity;
	}

	
	public java.awt.Component getListCellRendererComponent( javax.swing.JList list, Object value, int index, boolean isSelected,  boolean cellHasFocus ) {
		if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)value;
			if( verbosity == JUST_NAME ) {
				value = element.name.getStringValue();
			} else if( verbosity == DEFAULT_REPR ) {
				value = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element, true );
			} else if( verbosity == FULL_KEY ) {
				value = element.getKey();
			}
		}

		return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
	}
}