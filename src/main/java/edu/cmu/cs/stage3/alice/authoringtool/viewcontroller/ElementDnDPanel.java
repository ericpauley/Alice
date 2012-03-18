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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt
 */
public class ElementDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController nameViewController;
	protected javax.swing.JLabel iconLabel = new javax.swing.JLabel();

	public ElementDnDPanel() {
		setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "elementDnDPanel" ) );
		iconLabel.setOpaque( false );
	}

	public void set( edu.cmu.cs.stage3.alice.core.Element element ) {
		clean();
		this.element = element;
		nameViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getElementNamePropertyViewController( element );
		nameViewController.setBorder( null );
		nameViewController.setOpaque( false );
		add( nameViewController, java.awt.BorderLayout.CENTER );
		addDragSourceComponent( nameViewController );
		setTransferable( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable( element ) );
	}

	public void editName() {
		nameViewController.editValue();
	}

	protected void startListening() {
		if( nameViewController != null ) {
			nameViewController.startListening();
		}
	}

	protected void stopListening() {
		if( nameViewController != null ) {
			nameViewController.stopListening();
		}
	}

	public void goToSleep() {
		stopListening();
	}

	public void wakeUp() {
		startListening();
	}

	public void clean() {
		removeDragSourceComponent( nameViewController );
		setTransferable( null );
		if( nameViewController != null ) {
			remove( nameViewController );
		}
		nameViewController = null;
	}

	public void die() {
		clean();
	}

	
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
	}
}
