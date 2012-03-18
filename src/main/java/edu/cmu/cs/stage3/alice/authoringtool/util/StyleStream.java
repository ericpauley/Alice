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
public class StyleStream extends java.io.PrintStream {
	protected javax.swing.text.Style style;
	protected StyledStreamTextPane styledStreamTextPane;

	public StyleStream( StyledStreamTextPane styledStreamTextPane, javax.swing.text.Style style ) {
		super( System.out );
		this.styledStreamTextPane = styledStreamTextPane;
		this.style = style;
	}

	
	public void write( int b ) {
		try {
			styledStreamTextPane.document.insertString( styledStreamTextPane.endPosition.getOffset() - 1, String.valueOf( b ), style );
		} catch( javax.swing.text.BadLocationException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error while printing.", e );
		}
	}

	
	public void write( byte buf[], int off, int len ) {
		try {
			styledStreamTextPane.document.insertString( styledStreamTextPane.endPosition.getOffset() - 1, new String( buf, off, len ), style );
		} catch( javax.swing.text.BadLocationException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error while printing.", e );
		}
	}
}
