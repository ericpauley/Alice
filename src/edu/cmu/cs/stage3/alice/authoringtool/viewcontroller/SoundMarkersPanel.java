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

/** @deprecated */
public class SoundMarkersPanel extends javax.swing.JPanel {
	protected edu.cmu.cs.stage3.alice.core.Media sound = null;
	protected java.util.HashMap soundMarkersGuiCache = new java.util.HashMap();
	protected javax.swing.JButton dropMarkerButton = new javax.swing.JButton( "Drop Marker" );
	protected RefreshListener refreshListener = new RefreshListener();

	public SoundMarkersPanel() {
		guiInit();
	}

	private void guiInit() {
		//setTitle( "Markers" );
		setLayout( new java.awt.GridBagLayout() );
		dropMarkerButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		dropMarkerButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		dropMarkerButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
//                    if (m_lock!=null && sound!=null && m_lock.getPlayer()!=null) {
//                        sound.addMarker(m_lock.getPlayer().getMediaTime().getSeconds());
//                    }
				}
			}
		);
		//setOpaque( false );
	}

//	public void setMarkers( Media sound, MediaLock lock ) {
//		if( this.sound != null ) {
//			this.sound.markers.removeObjectArrayPropertyListener( refreshListener );
//		}
//
//		this.sound = sound;
//        this.m_lock = lock;
//
//		if( sound != null ) {
//            this.sound.markers.addObjectArrayPropertyListener( refreshListener );
//		}
//
//		refreshGUI();
//	}

	public void refreshGUI() {
        removeAll();

		if( sound!=null && sound.markers != null ) {
			int count = 0;
			for( int i = 0; i < sound.markers.size(); i++ ) {
				final edu.cmu.cs.stage3.alice.core.media.SoundMarker marker = (edu.cmu.cs.stage3.alice.core.media.SoundMarker)sound.markers.get( i );
				javax.swing.JComponent gui = (javax.swing.JComponent)soundMarkersGuiCache.get( marker );
				if( gui == null ) {
					gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( marker );
					soundMarkersGuiCache.put( marker, gui );
				}
				if( gui != null ) {
					add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
				} else {
					System.err.println( "Unable to create gui for marker: " + marker );
				}
			}

			add( dropMarkerButton, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 4, 2, 4, 2 ), 0, 0 ) );
			java.awt.Component glue = javax.swing.Box.createGlue();
			add( glue, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
		}
		revalidate();
		repaint();
	}

	protected class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) { }
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			SoundMarkersPanel.this.refreshGUI();
		}
	}
}