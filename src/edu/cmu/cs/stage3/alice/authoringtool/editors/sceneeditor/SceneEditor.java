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

package edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * @author Jason Pratt
 * @author Clifton Forlines
 */
public class SceneEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor {
	public String editorName = "Scene Editor";

	public static int LARGE_MODE = 1;
	public static int SMALL_MODE = 2;
	protected int guiMode = LARGE_MODE;

	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.core.Camera renderCamera = null;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage() );
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ScriptComboWidget scriptComboWidget = new edu.cmu.cs.stage3.alice.authoringtool.util.ScriptComboWidget();
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

	protected java.awt.Image makeSceneEditorBigImage = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "makeSceneEditorBig" );
	protected java.awt.Image makeSceneEditorSmallImage = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString( "makeSceneEditorSmall" );
	protected javax.swing.JButton makeSceneEditorBigButton = new javax.swing.JButton( new javax.swing.ImageIcon( makeSceneEditorBigImage ) );
	protected javax.swing.JButton makeSceneEditorSmallButton = new javax.swing.JButton( new javax.swing.ImageIcon( makeSceneEditorSmallImage ) );

	//////////////////
	// Constructor
	//////////////////

	public SceneEditor() {
		configInit();
		jbInit();
		guiInit();
	}

	private void configInit() {
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(
			new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
				public void changing( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {}
				public void changed( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {
					if( ev.getKeyName().equals( "edu.cmu.cs.stage3.alice.authoringtool.enableScripting" ) ) {
						if( authoringToolConfig.getValue( "enableScripting" ).equalsIgnoreCase( "true" ) ) {
							topPanel.add( scriptComboWidget, BorderLayout.CENTER );
						} else {
							topPanel.remove( scriptComboWidget );
						}
						SceneEditor.this.revalidate();
						SceneEditor.this.repaint();
					}
				}
			}
		);
	}

	private void guiInit() {
		// set the divider locations
		int width = (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() * .8);
		mainPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );

		if( authoringToolConfig.getValue( "enableScripting" ).equalsIgnoreCase( "true" ) ) {
			topPanel.add( scriptComboWidget, BorderLayout.CENTER );
		}

		makeSceneEditorBigButton.setMargin( new java.awt.Insets( 0, 0, 0, 0 ) );
		makeSceneEditorSmallButton.setMargin( new java.awt.Insets( 0, 0, 0, 0 ) );
		makeSceneEditorBigButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				authoringTool.getJAliceFrame().setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame.SCENE_EDITOR_LARGE_MODE );
			}
		} );
		makeSceneEditorSmallButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				authoringTool.getJAliceFrame().setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame.SCENE_EDITOR_SMALL_MODE );
			}
		} );
		makeSceneEditorBigButton.setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "makeSceneEditorBigBackground" ) );
		makeSceneEditorSmallButton.setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "makeSceneEditorSmallBackground" ) );

		cameraViewPanel.navPanel.add( makeSceneEditorBigButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 2, 2 ), 0, 0 ) );
		cameraViewPanel.controlPanel.add( makeSceneEditorSmallButton, new java.awt.GridBagConstraints( 0, 9, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 8, 8 ), 0, 0 ) );

		// tooltips
		makeSceneEditorBigButton.setToolTipText( "<html><font face=arial size=-1>Open the Object Gallery and Layout Tool.<p><p>Objects are added to the world from the Gallery.<p>The Layout Tool has tools that will help you position objects in the world.<p>You will not be able to edit Methods or Events while the Gallery is open.</font></html>" );
		makeSceneEditorSmallButton.setToolTipText( "<html><font face=arial size=-1>Close the Gallery and Layout Tool.<p><p>Closes the gallery and returns<p>to the Method and Event editors.</font></html>" );
	}

	// big hack for now.  need to consolidate CameraViewPanel and LayoutViewPanel
	public void setGuiMode( int guiMode ) {
////		System.out.println("set gui: "+this.guiMode+" ?= "+guiMode);
		if( this.guiMode != guiMode ) {
			this.guiMode = guiMode;
			cameraViewPanel.setTargetsDirty();
			mainPanel.removeAll();
			if( guiMode == SMALL_MODE ) {
////				System.out.println("making gui small");
				cameraViewPanel.setViewMode( CameraViewPanel.SINGLE_VIEW_MODE );
				cameraViewPanel.navPanel.add( makeSceneEditorBigButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 2, 2 ), 0, 0 ) );
				cameraViewPanel.guiNavigator.setImageSize( edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.SMALL_IMAGES );
				cameraViewPanel.defaultMoveModeButton.doClick();
				if( cameraViewPanel.affectSubpartsCheckBox.isSelected() ) {
					cameraViewPanel.affectSubpartsCheckBox.doClick();
				}
				mainPanel.add( cameraViewPanel.superRenderPanel, java.awt.BorderLayout.CENTER );
			} else if( guiMode == LARGE_MODE ) {
////				System.out.println("making gui large");
				cameraViewPanel.navPanel.remove( makeSceneEditorBigButton );
				cameraViewPanel.singleViewButton.doClick();
				cameraViewPanel.guiNavigator.setImageSize( edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.LARGE_IMAGES );
//				if( cameraViewPanel.quadViewButton.isSelected() ) {
//					cameraViewPanel.setViewMode( CameraViewPanel.QUAD_VIEW_MODE );
//					cameraViewPanel.guiNavigator.setImageSize( edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.SMALL_IMAGES );
//				} else {
//					cameraViewPanel.guiNavigator.setImageSize( edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.LARGE_IMAGES );
//				}
				cameraViewPanel.add( cameraViewPanel.superRenderPanel, java.awt.BorderLayout.CENTER);
				cameraViewPanel.defaultMoveModeButton.doClick();
				mainPanel.add( cameraViewPanel, java.awt.BorderLayout.CENTER );
				
			}
		}
	}

	public int getGuiMode() {
		return guiMode;
	}

	public void setViewMode( int mode ) {
		cameraViewPanel.setViewMode( mode );
	}

	public int getViewMode() {
		return cameraViewPanel.getViewMode();
	}

	public java.awt.Dimension getRenderSize() {
		return cameraViewPanel.getRenderSize();
	}

	public javax.swing.JPanel getSuperRenderPanel() {
		return cameraViewPanel.superRenderPanel;
	}

	public javax.swing.JPanel getRenderPanel() {
		return cameraViewPanel.renderPanel;
	}

	public void makeDirty(){
		cameraViewPanel.setTargetsDirty();	
	}
		
	public edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer getGalleryViewer() {
		return cameraViewPanel.galleryViewer;
	}

	///////////////////////
	// Editor Interface
	///////////////////////

	public javax.swing.JComponent getJComponent(){
		return this;
	}

	public Object getObject(){
		return world;
	}

	public void setObject( edu.cmu.cs.stage3.alice.core.World world ){
		this.world = world;
		scriptComboWidget.setSandbox( world );
		cameraViewPanel.setWorld( world );
//		layoutViewPanel.setWorld( world );
		if( world != null ){
			// set the renderCamera variable to the scene's camera (TODO: handle multiple cameras better)
			edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[])world.getDescendants( edu.cmu.cs.stage3.alice.core.Camera.class );
			if( cameras.length > 0 ) {
				renderCamera = cameras[0];
			}
		} else {
			renderCamera = null;
		}
	}

	public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		if( this.authoringTool != null ) {
			this.authoringTool.removeAuthoringToolStateListener( this );
		}

		this.authoringTool = authoringTool;
//		layoutViewPanel.setAuthoringTool( authoringTool );
//		layoutViewPanel.renderInit( authoringTool.getRenderTargetFactory(), authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler() );
		cameraViewPanel.setAuthoringTool( authoringTool );
		cameraViewPanel.renderInit( authoringTool );
		stencilInit();

		if( authoringTool != null ) {
			authoringTool.addAuthoringToolStateListener( this );
		}
	}

	/////////////////////////////////////////////////
	// for Stencils
	/////////////////////////////////////////////////

	protected java.util.HashMap idsToComponents = new java.util.HashMap();
	protected java.util.HashMap componentsToIds = new java.util.HashMap();

	protected void stencilInit() {
		idsToComponents.put( "makeSceneEditorBigButton", makeSceneEditorBigButton );
		idsToComponents.put( "makeSceneEditorSmallButton", makeSceneEditorSmallButton );
		idsToComponents.put( "guiNavigatorSlidePanel", cameraViewPanel.guiNavigator.getSlidePanel() );
		idsToComponents.put( "guiNavigatorDrivePanel", cameraViewPanel.guiNavigator.getDrivePanel() );
		idsToComponents.put( "guiNavigatorTiltPanel", cameraViewPanel.guiNavigator.getTiltPanel() );
		idsToComponents.put( "affectSubpartsCheckBox", cameraViewPanel.affectSubpartsCheckBox );
		idsToComponents.put( "aspectRatioComboBox", cameraViewPanel.aspectRatioComboBox );
		idsToComponents.put( "lensAngleSlider", cameraViewPanel.lensAngleSlider );
		idsToComponents.put( "singleViewButton", cameraViewPanel.singleViewButton );
		idsToComponents.put( "quadViewButton", cameraViewPanel.quadViewButton );
		idsToComponents.put( "cameraDummyButton", cameraViewPanel.cameraDummyButton );
		idsToComponents.put( "objectDummyButton", cameraViewPanel.objectDummyButton );
		idsToComponents.put( "moveCameraCombo", cameraViewPanel.moveCameraCombo );
		idsToComponents.put( "defaultMoveModeButton", cameraViewPanel.defaultMoveModeButton );
		idsToComponents.put( "moveUpDownModeButton", cameraViewPanel.moveUpDownModeButton );
		idsToComponents.put( "turnLeftRightModeButton", cameraViewPanel.turnLeftRightModeButton );
		idsToComponents.put( "turnForwardBackwardModeButton", cameraViewPanel.turnForwardBackwardModeButton );
		idsToComponents.put( "tumbleModeButton", cameraViewPanel.tumbleModeButton );
		idsToComponents.put( "copyModeButton", cameraViewPanel.copyModeButton );
		idsToComponents.put( "orthoScrollModeButton", cameraViewPanel.orthoScrollModeButton );
		idsToComponents.put( "orthoZoomInModeButton", cameraViewPanel.orthoZoomInModeButton );
//		idsToComponents.put( "orthoZoomOutModeButton", cameraViewPanel.orthoZoomOutModeButton );
		idsToComponents.put( "superRenderPanel", cameraViewPanel.superRenderPanel );
		idsToComponents.put( "renderPanel", cameraViewPanel.renderPanel );

		for( java.util.Iterator iter = idsToComponents.keySet().iterator(); iter.hasNext(); ) {
			 Object key = iter.next();
			 componentsToIds.put( idsToComponents.get( key ), key );
		}
	}

	public String getIdForComponent( java.awt.Component c ) {
		return (String)componentsToIds.get( c );
	}

	public java.awt.Component getComponentForId( String id ) {
		return (java.awt.Component)idsToComponents.get( id );
	}

	/////////////////////////////////////////////////
	// AuthoringtoolStateChangeListener Interface
	/////////////////////////////////////////////////

	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		cameraViewPanel.setTargetsDirty();
		if( ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE ) {
			cameraViewPanel.deactivate();
		} else if( isShowing() ) {
			cameraViewPanel.activate();
		}
	}

	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ){
		cameraViewPanel.activate();
	}

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev )  {cameraViewPanel.setTargetsDirty();}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {cameraViewPanel.setTargetsDirty();}


	///////////////////
	// GUI Callbacks
	///////////////////

//	void viewsTabbedPane_stateChanged(ChangeEvent e) {
//		if(viewsTabbedPane.getSelectedIndex() == 0){
//			layoutViewPanel.deactivate();
//			cameraViewPanel.activate();
//		}else if(viewsTabbedPane.getSelectedIndex() == 1){
//			cameraViewPanel.deactivate();
//			layoutViewPanel.activate();
//		}
//	}

	void this_componentShown(ComponentEvent e) {
		cameraViewPanel.setTargetsDirty();
		cameraViewPanel.activate();
	}

	/////////////////////
	// Autogenerated
	/////////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel mainPanel = new JPanel();
//	JPanel viewsPanel = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	FlowLayout flowLayout1 = new FlowLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	BevelBorder bevelBorder1 = new BevelBorder(BevelBorder.LOWERED);
	FlowLayout flowLayout4 = new FlowLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	FlowLayout flowLayout6 = new FlowLayout();
	FlowLayout flowLayout7 = new FlowLayout();
	FlowLayout flowLayout8 = new FlowLayout();
	FlowLayout flowLayout9 = new FlowLayout();
	BorderLayout borderLayout7 = new BorderLayout();
//	JTabbedPane viewsTabbedPane = new JTabbedPane();
//	JPanel cameraViewTab = new JPanel();
	BorderLayout borderLayout8 = new BorderLayout();
//	JPanel layoutViewTab = new JPanel();
//	LayoutViewPanel layoutViewPanel = new LayoutViewPanel();
	BorderLayout borderLayout6 = new BorderLayout();
	Border border1;
	Border border2;
	Border border3;
	FlowLayout flowLayout3 = new FlowLayout();
	JPanel topPanel = new JPanel();
	BorderLayout borderLayout5 = new BorderLayout();
	BorderLayout borderLayout9 = new BorderLayout();
	CameraViewPanel cameraViewPanel = new CameraViewPanel( this );

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(0,10,0,10);
		border2 = BorderFactory.createMatteBorder(0,10,0,10,new Color(236, 235, 235));
		border3 = BorderFactory.createEmptyBorder(0,10,0,10);
		this.setBackground(new Color(126, 159, 197));
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			
			public void componentShown(ComponentEvent e) {
				this_componentShown(e);
			}
		});
		this.setLayout(borderLayout1);
		mainPanel.setBackground(new Color(126, 159, 197));
		mainPanel.setMinimumSize(new Dimension(10, 100));
		mainPanel.setLayout(borderLayout9);
//		viewsPanel.setBackground(Color.black);
//		viewsPanel.setMinimumSize(new Dimension(100, 100));
//		viewsPanel.setPreferredSize(new Dimension(100, 100));
//		viewsPanel.setLayout(borderLayout2);
		flowLayout4.setHgap(0);
		flowLayout4.setVgap(0);
//		cameraViewTab.setLayout(borderLayout8);
//		layoutViewTab.setLayout(borderLayout6);
//		viewsTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				viewsTabbedPane_stateChanged(e);
//			}
//		});
//		viewsTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		flowLayout3.setAlignment(FlowLayout.LEFT);
		flowLayout3.setHgap(0);
		flowLayout3.setVgap(0);
		topPanel.setLayout(borderLayout5);
		mainPanel.add( cameraViewPanel );
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(topPanel, BorderLayout.NORTH);
	}
}