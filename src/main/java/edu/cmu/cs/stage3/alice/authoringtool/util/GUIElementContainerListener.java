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
public class GUIElementContainerListener extends java.awt.event.ContainerAdapter {
	protected final static GUIElementContainerListener staticListener = new GUIElementContainerListener();

	public static GUIElementContainerListener getStaticListener() {
		return staticListener;
	}

	
	public void componentRemoved( java.awt.event.ContainerEvent ev ) {
//		releaseRecursively( ev.getChild() );
//
		if( ev.getChild() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable ) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)ev.getChild()).release();
		}

//		if( ev.getChild() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement ) {
//			GUIFactory.releaseGUI( (edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement)ev.getChild() );
//		}
	}

//	protected void releaseRecursively( java.awt.Component c ) {
//		if( c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable ) {
//			((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)c).release();
//		} else if( c instanceof java.awt.Container ) {
//			java.awt.Component[] children = ((java.awt.Container)c).getComponents();
//			for( int i = 0; i < children.length; i++ ) {
//				releaseRecursively( children[i] );
//			}
//		}
//	}
}