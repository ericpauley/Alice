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
public class ObjectArrayPropertyPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty;
	protected javax.swing.JPanel contentPanel = new javax.swing.JPanel();
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected RefreshListener refreshListener = new RefreshListener();

	public ObjectArrayPropertyPanel( String title, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		guiInit( title );
	}

	private void guiInit( String title ) {
		setTitle( title );
		contentPanel.setLayout( new java.awt.GridBagLayout() );
		setContent( contentPanel );
		setOpaque( false );
		contentPanel.setOpaque( false );
	}

	public void setObjectArrayProperty( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty objectArrayProperty ) {
		if( this.objectArrayProperty != null ) {
			this.objectArrayProperty.removeObjectArrayPropertyListener( refreshListener );
		}

		this.objectArrayProperty = objectArrayProperty;

		if( objectArrayProperty != null ) {
			objectArrayProperty.addObjectArrayPropertyListener( refreshListener );
		}

		refreshGUI();
	}

	public void refreshGUI() {
		contentPanel.removeAll();

		if( objectArrayProperty != null ) {
			int count = 0;
			for( int i = 0; i < objectArrayProperty.size(); i++ ) {
				final Object object = objectArrayProperty.get( i );
				javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( object );
				if( gui != null ) {
					contentPanel.add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 2, 0, 2 ), 0, 0 ) );
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to create gui for object: " + object, null );
				}
			}

			java.awt.Component glue = javax.swing.Box.createGlue();
			contentPanel.add( glue, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
		}
		revalidate();
		repaint();
	}

	protected class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			ObjectArrayPropertyPanel.this.refreshGUI();
		}
	}
}