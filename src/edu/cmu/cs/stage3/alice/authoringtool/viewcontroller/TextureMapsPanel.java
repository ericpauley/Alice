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
public class TextureMapsPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty textureMaps;
	protected javax.swing.JPanel contentPanel = new javax.swing.JPanel();
	protected java.util.HashMap textureMapGuiCache = new java.util.HashMap();
	protected javax.swing.JButton importTextureMapButton = new javax.swing.JButton( "import texture map" );
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected RefreshListener refreshListener = new RefreshListener();

	public TextureMapsPanel( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		guiInit();
	}

	private void guiInit() {
		setTitle( "Texture Maps" );
		contentPanel.setLayout( new java.awt.GridBagLayout() );
		setContent( contentPanel );
		importTextureMapButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		importTextureMapButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		importTextureMapButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					authoringTool.setImportFileFilter( "Image Files" );
					authoringTool.importElement( null, TextureMapsPanel.this.textureMaps.getOwner() );
				}
			}
		);
		setOpaque( false );
		contentPanel.setOpaque( false );

		importTextureMapButton.setToolTipText( "<html><font face=arial size=-1>Load an Image File into this World.<p><p>Objects use image files as textures.<p>You can change an object's texture by setting its <b>skin</b> property.</font></html>" );
	}

	public void setTextureMaps( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty textureMaps ) {
		if( this.textureMaps != null ) {
			this.textureMaps.removeObjectArrayPropertyListener( refreshListener );
		}

		this.textureMaps = textureMaps;

		if( textureMaps != null ) {
			textureMaps.addObjectArrayPropertyListener( refreshListener );
		}

		refreshGUI();
	}

	public void refreshGUI() {
		contentPanel.removeAll();

		if( textureMaps != null ) {
			int count = 0;
			for( int i = 0; i < textureMaps.size(); i++ ) {
				final edu.cmu.cs.stage3.alice.core.TextureMap textureMap = (edu.cmu.cs.stage3.alice.core.TextureMap)textureMaps.get( i );
				javax.swing.JComponent gui = (javax.swing.JComponent)textureMapGuiCache.get( textureMap );
				if( gui == null ) {
					gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( textureMap );
					textureMapGuiCache.put( textureMap, gui );
				}
				if( gui != null ) {
					contentPanel.add( gui, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 2, 0, 2 ), 0, 0 ) );
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to create gui for textureMap: " + textureMap, null );
				}
			}

			contentPanel.add( importTextureMapButton, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 4, 2, 4, 2 ), 0, 0 ) );
			java.awt.Component glue = javax.swing.Box.createGlue();
			contentPanel.add( glue, new java.awt.GridBagConstraints( 0, count++, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
		}
		revalidate();
		repaint();
	}

	protected class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			TextureMapsPanel.this.refreshGUI();
		}
	}
}