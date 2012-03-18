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

package edu.cmu.cs.stage3.alice.authoringtool;

/**
 * @author Jason Pratt
 */
public interface Editor extends edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	/** implementers should override this with their own public static String name */
	public String editorName = "Unnamed Editor";

	/**
	 * this method should return the viewer's main JComponent, suitable for being layed out in a user-interface.
	 *
	 * Editors may contain heavyweight components (i.e. java.awt.Components), but this is highly discouraged,
	 * as the main JAlice GUI is implemented in Swing and heavyweight components do not play well with some widgets.
	 */
	public javax.swing.JComponent getJComponent();

	/** returns the Object that's being viewed */
	public Object getObject();

	/**
	 * provides the editor with access to authoringtool resources
	 */
	public void setAuthoringTool( AuthoringTool authoringTool );
}
