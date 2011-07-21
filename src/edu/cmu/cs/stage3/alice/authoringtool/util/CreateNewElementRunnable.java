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
public class CreateNewElementRunnable implements Runnable {
	private Class elementClass;
	private edu.cmu.cs.stage3.alice.core.Element parent;

	public CreateNewElementRunnable( Class elementClass, edu.cmu.cs.stage3.alice.core.Element parent ) {
		this.elementClass = elementClass;
		this.parent = parent;
	}

	public void run() {
		try {
			Object instance = elementClass.newInstance();
			if( instance instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				String simpleName = elementClass.getName();
				simpleName = simpleName.substring( simpleName.lastIndexOf( '.' ) + 1 );
				((edu.cmu.cs.stage3.alice.core.Element)instance).name.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( simpleName, parent ) );
				((edu.cmu.cs.stage3.alice.core.Element)instance).setParent( parent );
			}
		} catch( Throwable t ) {
			if( elementClass != null ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating new instance of " + elementClass.getName(), t );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating new intance of null.", t );
			}
		}
	}
}
