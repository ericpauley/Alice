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
 * @author David Culyba
 */
public class ErrorContentPane extends AliceAlertContentPane {
	public final static int LESS_DETAIL_MODE = 0;
	public final static int MORE_DETAIL_MODE = 1;

	protected Throwable throwable;
	protected String message;


	public ErrorContentPane() {
		super();
	}
	
	
	public void preDialogShow( javax.swing.JDialog dialog ) {
		super.preDialogShow( dialog );
		writeAliceHeaderToTextPane();
		writeThrowableToTextPane();
	}

	
	public String getTitle() {
		return "Alice - Error";
	}
	

	public void setThrowable( Throwable t ) {
		throwable = t;
	}


	public void setDetails( String m ) {
		message = m;
	}
	
	protected void writeThrowableToTextPane() {
		if( throwable != null ) {
			detailStream.println( "Throwable that caused the error:" );
			throwable.printStackTrace( detailStream );
			if( throwable instanceof edu.cmu.cs.stage3.alice.core.ExceptionWrapper ) {
				edu.cmu.cs.stage3.alice.core.ExceptionWrapper ew = (edu.cmu.cs.stage3.alice.core.ExceptionWrapper)throwable;
				Exception e = ew.getWrappedException();
				detailStream.println( "Wrapped exception:" );
				e.printStackTrace( detailStream );
			}
		} else {
			if (message != null)
				detailStream.println( message );
			else
				new Exception("No throwable given. Here's the stack trace:").printStackTrace(detailStream);
		}
		detailStream.println();
	}

}