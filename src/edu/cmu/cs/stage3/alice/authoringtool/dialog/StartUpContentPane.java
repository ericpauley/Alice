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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.event.*;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

/**
 * @author David Culyba, Dennis Cosgrove
 */

class AliceWorldFilter implements java.io.FileFilter {
	private javax.swing.filechooser.FileFilter m_filter;
	public AliceWorldFilter(javax.swing.filechooser.FileFilter filter) {
		m_filter = filter;
	}
	public boolean accept(java.io.File file) {
		if (m_filter != null) {
			if (file.isDirectory()) {
				return true;
			}
			return m_filter.accept(file);
		}
		return false;
	}
}

class TutorialWorldFilter implements java.io.FileFilter {
	public boolean accept(java.io.File file) {
		if (file.getName().endsWith(".stl")) {
			return true;
		}
		return false;
	}
}

public class StartUpContentPane extends edu.cmu.cs.stage3.swing.ContentPane { 
	public static final int DO_NOT_CHANGE_TAB_ID = -1;
	public static final int OPEN_TAB_ID = 1;
	public static final int TUTORIAL_TAB_ID = 2;
	public static final int RECENT_TAB_ID = 3;
	public static final int TEMPLATE_TAB_ID = 4;
	public static final int EXAMPLE_TAB_ID = 5;
	public static final int TEXTBOOK_EXAMPLE_TAB_ID = 6;

	private static final String TUTORIAL_STRING = "Tutorial";
	private static final String EXAMPLES_STRING = "Examples";
	private static final String RECENT_STRING = "Recent Worlds";
	private static final String TEXTBOOK_EXAMPLES_STRING = "Textbook";
	private static final String OPEN_STRING = "Open a world";
	private static final String TEMPLATES_STRING = "Templates";

	private final int WIDTH = 480;
	private final int HEIGHT = 500;
	//private final int INSET = 14;

	private static final java.awt.Color SELECTED_COLOR = new java.awt.Color(10, 10, 100);
	private static final java.awt.Color SELECTED_TEXT_COLOR = new java.awt.Color(255, 255, 255);
	private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(0, 0, 0);

	private static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig =
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	private AliceWorldFilter aliceFilter;
	private javax.swing.filechooser.FileFilter worldFilter;
	private TutorialWorldFilter tutorialFilter = new TutorialWorldFilter();

	private StartUpIcon currentlySelected;

	private javax.swing.ImageIcon headerImage;
	private javax.swing.ImageIcon basicIcon;
	private javax.swing.ImageIcon directoryIcon;
	private javax.swing.ImageIcon upDirectoryIcon;
	private javax.swing.ImageIcon tutorialButtonIcon;

	private java.io.File exampleWorlds = null;
	private java.io.File templateWorlds = null;
	private java.io.File tutorialWorlds = null;
	private java.io.File textbookExampleWorlds = null;

	private JTabbedPane mainTabPane = new JTabbedPane();
	private JScrollPane exampleScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JScrollPane textbookExampleScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JScrollPane recentScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JScrollPane templateScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private JPanel exampleWorldsContainer = new JPanel();
	private JPanel recentWorldsContainer = new JPanel();
	private JPanel textbookExampleWorldsContainer = new JPanel();
	private JPanel templateWorldsContainer = new JPanel();
	private JPanel tutorialWorldsContainer = new JPanel();
	private edu.cmu.cs.stage3.awt.DynamicFlowLayout examplePanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private edu.cmu.cs.stage3.awt.DynamicFlowLayout recentPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private edu.cmu.cs.stage3.awt.DynamicFlowLayout templatePanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private edu.cmu.cs.stage3.awt.DynamicFlowLayout tutorialPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);
	private edu.cmu.cs.stage3.awt.DynamicFlowLayout textbookPanelLayout = new edu.cmu.cs.stage3.awt.DynamicFlowLayout(java.awt.FlowLayout.LEFT, null, javax.swing.JPanel.class, 20);

	private JPanel exampleWorldsDirectoryContainer = new JPanel();
	private JPanel textbookExampleWorldsDirectoryContainer = new JPanel();
	private JPanel templateWorldsDirectoryContainer = new JPanel();
	//private JPanel tutorialWorldsDirectoryContainer = new JPanel();
	//private JPanel recentWorldsDirectoryContainer = new JPanel();

	private JLabel exampleWorldsDirLabel = new JLabel();
	private JLabel textbookExampleWorldsDirLabel = new JLabel();
	private JLabel templateWorldsDirLabel = new JLabel();
	private JLabel tutorialWorldsDirLabel = new JLabel();
	//private JLabel recentWorldsDirLabel = new JLabel();

	private JButton openButton = new JButton();
	private JButton cancelButton = new JButton();
	//private JButton refreshButton = new JButton();
	private JCheckBox stopShowingCheckBox = new JCheckBox();

	private JLabel headerLabel = new JLabel();

	private JPanel tutorialButtonPanel = new JPanel();
	private JButton startTutorialButton = new JButton();
	private JPanel tutorialTopContainer = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JScrollPane tutorialScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private JFileChooser fileChooser = new JFileChooser() {
		public void setSelectedFile( java.io.File file ) {
			super.setSelectedFile( file );
			StartUpContentPane.this.handleFileSelectionChange( file );
		}
	};

	private JPanel buttonPanel = new JPanel();
	private JLabel jLabel1 = new JLabel();
	protected int currentTab = TUTORIAL_TAB_ID;

	public StartUpContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		headerImage = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/StartupScreen.png"));
		basicIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/aliceIcon.png"));
		directoryIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/directoryIcon.png"));
		upDirectoryIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/upDirectoryIcon.png"));
		tutorialButtonIcon = new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/startUpDialog/tutorialButton.png"));
		
		String[] recentWorldsStrings = authoringToolConfig.getValueList("recentWorlds.worlds");
		String max = authoringToolConfig.getValue("recentWorlds.maxWorlds");
		int maxRecentWorlds = Integer.parseInt(max);
		if (maxRecentWorlds > 0 && maxRecentWorlds <= recentWorldsStrings.length) {
			String[] cappedRecentWorlds = new String[maxRecentWorlds];
			System.arraycopy(recentWorldsStrings, 0, cappedRecentWorlds, 0, maxRecentWorlds);
			recentWorldsStrings = cappedRecentWorlds;
		}
		String filename = authoringToolConfig.getValue("directories.examplesDirectory");

		if (filename != null) {
			exampleWorlds = new java.io.File(filename).getAbsoluteFile();
		}
		filename = authoringToolConfig.getValue("directories.templatesDirectory");
		if (filename != null) {
			templateWorlds = new java.io.File(filename).getAbsoluteFile();
		}
		filename = authoringToolConfig.getValue("directories.textbookExamplesDirectory");
		if (filename != null) {
			textbookExampleWorlds = new java.io.File(filename).getAbsoluteFile();
		}

		worldFilter = authoringTool.getWorldFileFilter();
		aliceFilter = new AliceWorldFilter(worldFilter);

		jbInit();
		guiInit();

		int count = 0;
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		String font = "SansSerif";//"Dialog";
		if (authoringTool != null) {
			tutorialWorlds = authoringTool.getTutorialDirectory();
			count = buildPanel(tutorialWorldsContainer, buildVectorFromDirectory(tutorialWorlds, tutorialFilter), false, null, StartUpIcon.TUTORIAL);
			if (count <= 0) {
				tutorialTopContainer.removeAll();
				javax.swing.JLabel noTutorialWorldsLabel = new javax.swing.JLabel();
				noTutorialWorldsLabel.setText("No tutorial found.");
				noTutorialWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int)(18*fontSize/12.0)));
				tutorialTopContainer.add(noTutorialWorldsLabel);
			}
		}
		count = buildPanel(exampleWorldsContainer, buildVectorFromDirectory(exampleWorlds, aliceFilter), false, null, StartUpIcon.STANDARD);
		if (count <= 0) {
			exampleWorldsContainer.removeAll();
			javax.swing.JLabel noExampleWorldsLabel = new javax.swing.JLabel();
			noExampleWorldsLabel.setText("No example worlds.");
			noExampleWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int)(18*fontSize/12.0)));
			exampleWorldsContainer.add(noExampleWorldsLabel);
		}
		count = buildPanel(templateWorldsContainer, buildVectorFromDirectory(templateWorlds, aliceFilter), false, null, StartUpIcon.STANDARD);
		if (count <= 0) {
			templateWorldsContainer.removeAll();
			javax.swing.JLabel noTemplateWorldsLabel = new javax.swing.JLabel();
			noTemplateWorldsLabel.setText("No templates.");
			noTemplateWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int)(18*fontSize/12.0)));
			templateWorldsContainer.add(noTemplateWorldsLabel);
		}
		count = buildPanel(recentWorldsContainer, buildVectorFromString(recentWorldsStrings), true, null, StartUpIcon.STANDARD);
		if (count <= 0) {
			recentWorldsContainer.removeAll();
			javax.swing.JLabel noRecentWorldsLabel = new javax.swing.JLabel();
			noRecentWorldsLabel.setText("No recent worlds.");
			noRecentWorldsLabel.setFont(new java.awt.Font(font, java.awt.Font.BOLD, (int)(18*fontSize/12.0)));
			recentWorldsContainer.add(noRecentWorldsLabel);
		}
		count = buildPanel(textbookExampleWorldsContainer, buildVectorFromDirectory(textbookExampleWorlds, aliceFilter), false, null, StartUpIcon.STANDARD);
		if (count <= 0) {
			mainTabPane.remove(textbookExampleWorldsDirectoryContainer);
		}
		
		addComponentListener( new java.awt.event.ComponentAdapter() {
			public void componentResized( java.awt.event.ComponentEvent e ) {
				matchSizes();
			}
		} );
		tutorialPanelLayout.setHgap(21);
		tutorialPanelLayout.setVgap(10);
		examplePanelLayout.setHgap(21);
		examplePanelLayout.setVgap(10);
		recentPanelLayout.setHgap(21);
		recentPanelLayout.setVgap(10);
		templatePanelLayout.setHgap(21);
		templatePanelLayout.setVgap(10);
		textbookPanelLayout.setHgap(21);
		textbookPanelLayout.setVgap(10);

	}

	public void preDialogShow( javax.swing.JDialog dialog ) {
		super.preDialogShow(dialog);
//		initializeFileChooser();
		mainTabPane.setSelectedComponent(getTabForID(currentTab));
	}
	
	private void handleFileSelectionChange( java.io.File file ) {
		openButton.setEnabled( file!=null && file.exists() && !file.isDirectory() );

	}
	//todo? isResizable() return false;
	
	//todo: adjust title based on tab
	public String getTitle() {
		return "Welcome to Alice!";
	}
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		openButton.addActionListener( l );
	}
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		openButton.removeActionListener( l );
	}
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
		cancelButton.addActionListener( l );
	}
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
		cancelButton.removeActionListener( l );
	}

	public boolean isTutorial() {
		if( currentlySelected != null ) {
			return currentlySelected.type == StartUpIcon.TUTORIAL;
		} else {
			return getTabID() == TUTORIAL_TAB_ID;
		}
	}
	public boolean isSaveNeeded() {
		if( currentlySelected != null ) {
			return currentlySelected.needToSave;
		} else {
			return true;
		}
	}
	public java.io.File getFile() {
		if( getTabID() == OPEN_TAB_ID ) {
			return fileChooser.getSelectedFile();
		} else {
			if( currentlySelected != null ) {
				return new java.io.File( currentlySelected.file );
			} else {
				return null;
			}
		}
	}

	private int getIDForTab(java.awt.Component tab) {
		if (tab == fileChooser)
			return OPEN_TAB_ID;
		if (tab == tutorialTopContainer)
			return TUTORIAL_TAB_ID;
		if (tab == recentScrollPane)
			return RECENT_TAB_ID;
		if (tab == exampleWorldsDirectoryContainer)
			return EXAMPLE_TAB_ID;
		if (tab == textbookExampleWorldsDirectoryContainer)
			return TEXTBOOK_EXAMPLE_TAB_ID;
		if (tab == templateWorldsDirectoryContainer)
			return TEMPLATE_TAB_ID;
		return 0;
	}

	private java.awt.Component getTabForID( int tabID ) {
		switch( tabID ) {
		case OPEN_TAB_ID:
			return fileChooser;
		case TUTORIAL_TAB_ID:
			return tutorialTopContainer;
		case RECENT_TAB_ID:
			return recentScrollPane;
		case EXAMPLE_TAB_ID:
			return exampleWorldsDirectoryContainer;
		case TEMPLATE_TAB_ID:
			return templateWorldsDirectoryContainer;
		case TEXTBOOK_EXAMPLE_TAB_ID:
			return textbookExampleWorldsDirectoryContainer;
		default :
			return tutorialTopContainer;
		}
	}

	private int getTabID() {
		return getIDForTab( mainTabPane.getSelectedComponent() );
	}
	
	public void setTabID( int tabID ) {
		if( tabID == OPEN_TAB_ID ) {
			fileChooser.rescanCurrentDirectory();
		}
		if( tabID != DO_NOT_CHANGE_TAB_ID ) {
			currentTab = tabID; 
			mainTabPane.setSelectedComponent(getTabForID(currentTab));
			
		}
	}

	private String makeNameFromFilename(String filename) {
		String name = filename.substring(0, (filename.length() - 4));
		//int count = 0;
		int last = name.lastIndexOf(java.io.File.separator);
		if (last >= 0 && last < name.length()) {
			name = name.substring(last + 1);
		}
		return name;
	}

	private String makeDirectoryNameFromFilename(String filename) {
		String name = new String(filename);
		//int count = 0;
		if (filename.endsWith(java.io.File.separator)) {
			filename = filename.substring(filename.length());
		}
		int last = filename.lastIndexOf(java.io.File.separator);
		if (last >= 0 && last < filename.length()) {
			name = filename.substring(last + 1);
		}
		return name;
	}

	private java.util.Vector buildVectorFromString(String[] files) {
		java.util.Vector toReturn = new java.util.Vector();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String name = makeNameFromFilename(files[i]);
				edu.cmu.cs.stage3.util.StringObjectPair sop = new edu.cmu.cs.stage3.util.StringObjectPair(name, files[i]);
				toReturn.add(sop);
			}
		}
		return toReturn;
	}

	private java.util.Vector buildVectorFromDirectory(java.io.File dir, java.io.FileFilter f) {
		java.util.Vector toReturn = null;
		if (dir != null && dir.isDirectory()) {
			toReturn = new java.util.Vector();
			java.io.File[] files = dir.listFiles(f);
			for (int i = 0; i < files.length; i++) {
				String name = "";
				if (files[i].isDirectory()) {
					name = makeDirectoryNameFromFilename(files[i].getName());
				} else {
					name = makeNameFromFilename(files[i].getName());
				}
				edu.cmu.cs.stage3.util.StringObjectPair sop = new edu.cmu.cs.stage3.util.StringObjectPair(name, files[i].getAbsolutePath());
				toReturn.add(sop);
			}
		}
		return toReturn;
	}

	private javax.swing.ImageIcon getIconFromFile(java.io.File file) {
		String filename = file.getAbsolutePath();
		javax.swing.ImageIcon icon = null;
		try {
			if (filename.endsWith(".stl")) {
				javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
				org.w3c.dom.Document document;
				//org.w3c.dom.Element xmlRoot;
				javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(file);
				org.w3c.dom.NodeList nl = document.getElementsByTagName("stencilStack");
				if (nl != null && nl.getLength() > 0) {
					org.w3c.dom.Node n = nl.item(0);
					if (n instanceof org.w3c.dom.Element) {
						String worldFileName = ((org.w3c.dom.Element) n).getAttribute("world");
						file = new java.io.File(worldFileName);
					}
				}
			}
			java.util.zip.ZipFile zip = new java.util.zip.ZipFile(file);
			java.util.zip.ZipEntry entry = zip.getEntry("thumbnail.png");
			if (entry != null) {
				java.io.InputStream stream = zip.getInputStream(entry);
				java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load("png", stream);
				if (image != null) {
					icon = new javax.swing.ImageIcon(image);
				}
			}
			zip.close();
		} catch (Exception e) {
			return null;
		}
		return icon;
	}

	protected java.awt.Component getTopContainer(java.awt.Component innerContainer) {
		if (innerContainer == tutorialWorldsContainer) {
			return tutorialTopContainer;
		} else if (innerContainer == exampleWorldsContainer) {
			return exampleScrollPane;
		} else if (innerContainer == templateWorldsContainer) {
			return templateScrollPane;
		} else if (innerContainer == textbookExampleWorldsContainer) {
			return textbookExampleScrollPane;
		} else {
			return null;
		}

	}

	protected String getBaseDirString(java.awt.Component topLevelOwner) {
		if (topLevelOwner == tutorialTopContainer) {
			return TUTORIAL_STRING;
		} else if (topLevelOwner == exampleScrollPane) {
			return EXAMPLES_STRING;
		} else if (topLevelOwner == templateScrollPane) {
			return TEMPLATES_STRING;
		} else if (topLevelOwner == textbookExampleScrollPane) {
			return TEXTBOOK_EXAMPLES_STRING;
		} else {
			return "";
		}
	}

	private int buildPanel(javax.swing.JPanel toBuild, java.util.Vector toAdd, boolean needToSave, java.io.File parentDir, int type) {
//		int width = 0;
//		int currentRow = 0;
//		int currentColumn = 0;
		int count = 0;
//		int maxWidth = WIDTH - 20;
		if (parentDir != null || toAdd != null) {
			toBuild.removeAll();
		}
		if (parentDir != null) {
			String parentDirName = "Back";
			StartUpIcon parentDirIcon = new StartUpIcon(parentDirName, upDirectoryIcon, parentDir.getAbsolutePath(), false, StartUpIcon.DIRECTORY, getTopContainer(toBuild));
			toBuild.add(parentDirIcon);
			count++;
		}
		if (toAdd != null) {
			for (int i = 0; i < toAdd.size(); i++) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair) toAdd.get(i);
				String name = sop.getString();
				String filename = (String) sop.getObject();
				java.io.File file = new java.io.File(filename);
				javax.swing.ImageIcon icon = basicIcon;
				if (file.exists() && file.canRead()) {
					filename = file.getAbsolutePath();
					if (file.isDirectory()) {
						StartUpIcon dirIcon = new StartUpIcon(name, directoryIcon, filename, false, StartUpIcon.DIRECTORY, getTopContainer(toBuild));
						toBuild.add(dirIcon);
						count++;
					} else {
						boolean worldIsThere = true;
						if (file.exists() && file.canRead()) {
							icon = getIconFromFile(file);
							if (icon == null) {
								icon = basicIcon;
							}
						} else {
							worldIsThere = false;
						}
						if (worldIsThere) {
							StartUpIcon sui = new StartUpIcon(name, icon, filename, needToSave, type, getTopContainer(toBuild));
							toBuild.add(sui);
							count++;
						}
					}
				}
			}
		}
		toBuild.revalidate();
		return count;
	}

	private void initializeFileChooser(){
		javax.swing.LookAndFeel feel = UIManager.getLookAndFeel();
		String font = "SansSerif";
		try {
			if( (System.getProperty( "os.name" ) != null) && System.getProperty( "os.name" ).startsWith( "Windows" ) )
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");//"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			else
				UIManager.setLookAndFeel("apple.laf.AquaLookAndFeel");
			AikMin.setFontSize(12);
		}
		catch(Exception e) { }

		mainTabPane.remove(fileChooser);
		java.io.File currentDir = fileChooser.getCurrentDirectory();
		fileChooser = new JFileChooser() {
			public void setSelectedFile( java.io.File file ) {
				super.setSelectedFile( file );
				StartUpContentPane.this.handleFileSelectionChange( file );
			}
		};
		for (int i = 0; i < fileChooser.getComponentCount(); i++) {
			setButtonBackgroundColors(fileChooser.getComponent(i), fileChooser.getBackground());
		}
		if (currentDir.exists()) {
			fileChooser.setCurrentDirectory(currentDir);
		} else {
			currentDir =  new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory"));
			if (currentDir.exists()) {
				fileChooser.setCurrentDirectory(currentDir);
			}
		}
		fileChooser.setFileFilter(worldFilter);
		fileChooser.setBackground(Color.white);
		fileChooser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser_actionPerformed(e);
			}
		});
		fileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
		mainTabPane.add(fileChooser, OPEN_STRING);
		try {
			AikMin.setFontSize(Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) ));
		}
		catch(Exception e) { }
	}
	
	private void guiInit() {
		int fontSize = Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) );
		if ( fontSize > 12)
			setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT + fontSize * 3));
		else
			setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT ));
		headerLabel.setIcon(headerImage);
		startTutorialButton.setIcon(tutorialButtonIcon);
		startTutorialButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		exampleWorldsContainer.setBorder(null);
		tutorialWorldsContainer.setBorder(null);
		recentWorldsContainer.setBorder(null);
		templateWorldsContainer.setBorder(null);
		textbookExampleWorldsContainer.setBorder(null);
		authoringToolConfig.getValue("showStartUpDialog");
		stopShowingCheckBox.setSelected(authoringToolConfig.getValue("showStartUpDialog").equalsIgnoreCase("true"));
		int selectedTab = Integer.parseInt(authoringToolConfig.getValue("showStartUpDialog_OpenTab"));
		setTabID(selectedTab);

		mainTabPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
		mainTabPane.setOpaque(false);
		initializeFileChooser();

		
	}
	
//	private static void updateDescendants( java.awt.Component component, Class cls, java.util.Vector v ) {
//		if( cls.isAssignableFrom( component.getClass() ) ) {
//			v.addElement( component );
//		}
//		if( component instanceof java.awt.Container ) {
//			java.awt.Container container = (java.awt.Container)component;
//			for( int i=0; i<container.getComponentCount(); i++ ) {
//				updateDescendants( container.getComponent( i ), cls, v );
//			}
//		}
//	}
//	private static java.awt.Component[] getDescendants( java.awt.Container root, Class cls ) {
//		java.util.Vector v = new java.util.Vector();
//		updateDescendants( root, cls, v );
//		Object[] array = (Object[])java.lang.reflect.Array.newInstance( cls, v.size() );
//		v.copyInto( array );
//		return (java.awt.Component[])array;
//	}

	private void setButtonBackgroundColors(java.awt.Component c, java.awt.Color color) {
		if (!(c instanceof java.awt.Button)) {
			c.setBackground(color);
		}
		if (c instanceof java.awt.Container) {
			java.awt.Container cont = (java.awt.Container) c;
			for (int i = 0; i < cont.getComponentCount(); i++) {
				setButtonBackgroundColors(cont.getComponent(i), color);
			}
		}
	}

	private void jbInit() {
		setLayout(new GridBagLayout());

		//java.awt.Component component1 = Box.createGlue();
		java.awt.Component component2 = Box.createGlue();
		buttonPanel.setLayout(new GridBagLayout());
		setBackground(Color.white);
		//mainTabPane.setMinimumSize(new Dimension(480, 310));
		//mainTabPane.setPreferredSize(new Dimension(480, 310));
		mainTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				mainTabPane_stateChanged(e);
			}
		});
//		buttonPanel.setMinimumSize(new Dimension(480, 50));
		buttonPanel.setOpaque(false);
//		buttonPanel.setPreferredSize(new Dimension(480, 50));
		//openButton.setMaximumSize(new Dimension(95, 27));
		int fontSize = Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) );
		openButton.setMinimumSize(new Dimension(fontSize  * 8, fontSize * 2 + 5));
		openButton.setPreferredSize(new Dimension(fontSize  * 8, fontSize * 2 + 5));
		openButton.setText("Open");
		openButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringToolConfig.setValue( "showStartUpDialog_OpenTab", Integer.toString(getIDForTab(mainTabPane.getSelectedComponent())) );
			}
		} );


		//cancelButton.setMaximumSize(new Dimension(95, 27));
		cancelButton.setMinimumSize(new Dimension(fontSize  * 8, fontSize * 2 + 5));
		cancelButton.setPreferredSize(new Dimension(fontSize  * 8, fontSize * 2 + 5));
		cancelButton.setText("Cancel");
		/*
		refreshButton.setMaximumSize(new Dimension(90, 22));
		refreshButton.setMinimumSize(new Dimension(90, 22));
		refreshButton.setPreferredSize(new Dimension(90, 22));
		refreshButton.setOpaque(false);
		refreshButton.setText("Refresh");
		refreshButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshButton_actionPerformed(e);
			}
		});
*/
		stopShowingCheckBox.setOpaque(false);
		stopShowingCheckBox.setText("Show this dialog at start");
		stopShowingCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopShowingCheckBox_actionPerformed(e);
			}
		});
		exampleWorldsContainer.setLayout(examplePanelLayout);
		recentWorldsContainer.setLayout(recentPanelLayout);
		exampleScrollPane.setBackground(Color.white);
		exampleScrollPane.setBorder(null);
		exampleScrollPane.setOpaque(false);
		exampleScrollPane.getViewport().setBackground(Color.white);
		exampleWorldsContainer.setBackground(Color.white);
		exampleWorldsContainer.setAlignmentX((float) 0.0);
		exampleWorldsContainer.setAlignmentY((float) 0.0);
		
		recentScrollPane.setBackground(Color.white);
		recentScrollPane.setBorder(null);
		recentScrollPane.getViewport().setBackground(Color.white);
		recentScrollPane.setOpaque(false);
		recentWorldsContainer.setBackground(Color.white);
		recentWorldsContainer.setAlignmentX((float) 0.0);
		recentWorldsContainer.setAlignmentY((float) 0.0);

		templateWorldsContainer.setLayout(templatePanelLayout);
		templateScrollPane.getViewport().setBackground(Color.white);
		templateScrollPane.setOpaque(false);
		templateScrollPane.setBorder(null);
		templateScrollPane.setBackground(Color.white);
		templateWorldsContainer.setBackground(Color.white);
		templateWorldsContainer.setAlignmentX((float) 0.0);
		templateWorldsContainer.setAlignmentY((float) 0.0);

		textbookExampleWorldsContainer.setLayout(textbookPanelLayout);
		textbookExampleScrollPane.getViewport().setBackground(Color.white);
		textbookExampleScrollPane.setOpaque(false);
		textbookExampleScrollPane.setBorder(null);
		textbookExampleScrollPane.setBackground(Color.white);
		textbookExampleWorldsContainer.setBackground(Color.white);
		textbookExampleWorldsContainer.setAlignmentX((float) 0.0);
		textbookExampleWorldsContainer.setAlignmentY((float) 0.0);
		
		tutorialButtonPanel.setBackground(Color.white);
		tutorialButtonPanel.setLayout(new GridBagLayout());
		startTutorialButton.setBackground(Color.white);
		startTutorialButton.setBorder(null);
		startTutorialButton.setMaximumSize(new Dimension(120, 90));
		startTutorialButton.setMinimumSize(new Dimension(120, 90));
		startTutorialButton.setPreferredSize(new Dimension(120, 90));
		startTutorialButton.setToolTipText("Start the Alice tutorial");
		startTutorialButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTutorialButton_actionPerformed(e);
			}
		});
		tutorialTopContainer.setLayout(borderLayout1);
		tutorialTopContainer.setBackground(Color.white);
		tutorialTopContainer.setOpaque(false);
		tutorialScrollPane.getViewport().setBackground(Color.white);
		tutorialScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		tutorialScrollPane.setOpaque(false);
		tutorialWorldsContainer.setLayout(tutorialPanelLayout);
		tutorialWorldsContainer.setBackground(Color.white);
		tutorialWorldsContainer.setAlignmentX((float) 0.0);
		tutorialWorldsContainer.setAlignmentY((float) 0.0);
		jLabel1.setText("or continue a tutorial:");

		exampleWorldsDirLabel.setText(getBaseDirString(exampleScrollPane));
		exampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
		exampleWorldsDirectoryContainer.setOpaque(true);
		exampleWorldsDirectoryContainer.setBackground(Color.white);
		exampleWorldsDirectoryContainer.add(exampleWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		exampleWorldsDirectoryContainer.add(exampleScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		textbookExampleWorldsDirLabel.setText(getBaseDirString(textbookExampleScrollPane));
		textbookExampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
		textbookExampleWorldsDirectoryContainer.setOpaque(true);
		textbookExampleWorldsDirectoryContainer.setBackground(Color.white);
		textbookExampleWorldsDirectoryContainer.add(textbookExampleWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		textbookExampleWorldsDirectoryContainer.add(textbookExampleScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		templateWorldsDirLabel.setText(getBaseDirString(templateScrollPane));
		templateWorldsDirectoryContainer.setLayout(new GridBagLayout());
		templateWorldsDirectoryContainer.setOpaque(true);
		templateWorldsDirectoryContainer.setBackground(Color.white);
		templateWorldsDirectoryContainer.add(templateWorldsDirLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		templateWorldsDirectoryContainer.add(templateScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(openButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
		buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(mainTabPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainTabPane.add(tutorialTopContainer, TUTORIAL_STRING);
		mainTabPane.add(recentScrollPane, RECENT_STRING);
		mainTabPane.add(templateWorldsDirectoryContainer, TEMPLATES_STRING);
		mainTabPane.add(exampleWorldsDirectoryContainer, EXAMPLES_STRING);
		mainTabPane.add(textbookExampleWorldsDirectoryContainer, TEXTBOOK_EXAMPLES_STRING);

		mainTabPane.add(fileChooser, OPEN_STRING);
		tutorialTopContainer.add(tutorialButtonPanel, BorderLayout.NORTH);
		tutorialButtonPanel.add(startTutorialButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
		tutorialButtonPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(2, 3, 2, 0), 0, 0));
		tutorialButtonPanel.add(component2, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		tutorialTopContainer.add(tutorialScrollPane, BorderLayout.CENTER);
		tutorialScrollPane.getViewport().add(tutorialWorldsContainer, null);
		add(stopShowingCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
		add(headerLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		templateScrollPane.getViewport().add(templateWorldsContainer, null);
		exampleScrollPane.getViewport().add(exampleWorldsContainer, null);
		recentScrollPane.getViewport().add(recentWorldsContainer, null);
		textbookExampleScrollPane.getViewport().add(textbookExampleWorldsContainer, null);		
		
		templateScrollPane.getVerticalScrollBar().setUnitIncrement(50); //Aik Min
		exampleScrollPane.getVerticalScrollBar().setUnitIncrement(50); //Aik Min
		recentScrollPane.getVerticalScrollBar().setUnitIncrement(50); //Aik Min
		textbookExampleScrollPane.getVerticalScrollBar().setUnitIncrement(50); //Aik Min

	}

	private void matchSizes(){
		tutorialWorldsContainer.setSize(recentScrollPane.getVisibleRect().width, tutorialWorldsContainer.getHeight());
		recentWorldsContainer.setSize(recentScrollPane.getVisibleRect().width, recentWorldsContainer.getHeight());
		templateWorldsContainer.setSize(templateScrollPane.getVisibleRect().width, templateWorldsContainer.getHeight());
		exampleWorldsContainer.setSize(exampleScrollPane.getVisibleRect().width, exampleWorldsContainer.getHeight());
		textbookExampleWorldsContainer.setSize(textbookExampleScrollPane.getVisibleRect().width, textbookExampleWorldsContainer.getHeight());
	}

	private void setFileChooserButtons(){
//		add(refreshButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 2, 1, 1), 0, 0));
//		buttonPanel.remove(openButton);
//		buttonPanel.remove(cancelButton);
//		buttonPanel.add(refreshButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
////		buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
//		buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		remove(buttonPanel);
	}
	
	private void setRegularButtons(){
//		buttonPanel.remove(refreshButton);
//		buttonPanel.add(openButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
//		buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 0, 0, 4), 0, 0));
//		buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
//		remove(refreshButton);
		add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void mainTabPane_stateChanged(ChangeEvent e) {
		if( currentlySelected != null ) {
			currentlySelected.deSelect();
			currentlySelected = null;
		}
		if (mainTabPane.getSelectedComponent() == fileChooser) {
			setFileChooserButtons();
			handleFileSelectionChange( fileChooser.getSelectedFile() );
		} else {
			setRegularButtons();
			openButton.setEnabled( false );
		}
	}
	private void stopShowingCheckBox_actionPerformed(ActionEvent e) {
		if (stopShowingCheckBox.isSelected()) {
			authoringToolConfig.setValue("showStartUpDialog", "true");
		} else {
			authoringToolConfig.setValue("showStartUpDialog", "false");
		}
	}
	private void fileChooser_actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(JFileChooser.APPROVE_SELECTION)) {
			openButton.setEnabled( true );
			openButton.doClick();
		} else if (actionCommand.equals(JFileChooser.CANCEL_SELECTION)){
			cancelButton.doClick();
		}
	}
	
/*	private void refreshButton_actionPerformed(ActionEvent e) {
		initializeFileChooser();
		setTabID( OPEN_TAB_ID );
	}
	*/
	private void startTutorialButton_actionPerformed(ActionEvent e) {
		openButton.setEnabled( true );
		openButton.doClick();
	}

	protected class StartUpIcon extends javax.swing.JLabel implements java.awt.event.MouseListener {
		protected static final int STANDARD = 1;
		protected static final int TUTORIAL = 2;
		protected static final int DIRECTORY = 3;
		protected boolean isSelected = false;
		protected String file;
		protected boolean needToSave = false;
		protected int type;
		protected java.awt.Component owner;

		public StartUpIcon(String name, javax.swing.ImageIcon icon, String file, boolean needToSave, int type, java.awt.Component owner) {
			super(name, icon, javax.swing.JLabel.CENTER);
			this.file = file;
			this.needToSave = needToSave;
			this.type = type;
			this.owner = owner;

			this.setBackground(BACKGROUND_COLOR);
			this.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
			this.setVerticalTextPosition(JLabel.BOTTOM);
			this.setHorizontalTextPosition(JLabel.CENTER);
			java.awt.Dimension size = new java.awt.Dimension(icon.getIconWidth()+4, (icon.getIconHeight() + 24));
			this.setPreferredSize(size);
			this.setMinimumSize(size);
			this.setMaximumSize(size);
			if (type == DIRECTORY) {
				this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
			}
			this.setOpaque(false);
			this.addMouseListener(this);
		}

		protected javax.swing.JPanel getContainer(java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorldsContainer;
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorldsContainer;
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorldsContainer;
			} else if (topLevelOwner == textbookExampleScrollPane) {
				return textbookExampleWorldsContainer;
			} else {
				return null;
			}

		}

		protected javax.swing.JLabel getJLabel(java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorldsDirLabel;
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorldsDirLabel;
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorldsDirLabel;
			} else if (topLevelOwner == textbookExampleScrollPane) {
				return textbookExampleWorldsDirLabel;
			} else {
				return null;
			}
		}

		protected String getRootPath(java.awt.Component topLevelOwner) {
			if (topLevelOwner == tutorialTopContainer) {
				return tutorialWorlds.getAbsolutePath();
			} else if (topLevelOwner == exampleScrollPane) {
				return exampleWorlds.getAbsolutePath();
			} else if (topLevelOwner == templateScrollPane) {
				return templateWorlds.getAbsolutePath();
				//			}else if (topLevelOwner == textbookExampleScrollPane){
				//				return textbookExampleWorlds.getAbsolutePath();
			} else {
				return null;
			}

		}

		protected String getRelativePath(String current, String root) {
			return current.substring(root.length());
		}

		protected void changeDirectory(String newDirectory) {
			java.io.File newDir = new java.io.File(newDirectory);
			java.io.File parentDir = newDir.getParentFile();
			JLabel labelToSet = getJLabel(owner);
			String baseDir = getBaseDirString(owner);
			if (owner instanceof javax.swing.JScrollPane) {
				((javax.swing.JScrollPane) owner).getVerticalScrollBar().setValue(0);
			}
			if (newDir.compareTo(exampleWorlds) == 0 || newDir.compareTo(templateWorlds) == 0 || //newDir.compareTo(textbookExampleWorlds) == 0 ||
			newDir.compareTo(tutorialWorlds) == 0) {
				buildPanel(getContainer(owner), buildVectorFromDirectory(newDir, aliceFilter), needToSave, null, StartUpIcon.STANDARD);
				labelToSet.setText(baseDir);
			} else {
				buildPanel(getContainer(owner), buildVectorFromDirectory(newDir, aliceFilter), needToSave, parentDir, StartUpIcon.STANDARD);
				labelToSet.setText(baseDir + getRelativePath(newDir.getAbsolutePath(), getRootPath(owner)));
			}

		}

		public void deSelect() {
			if (isSelected) {
				currentlySelected = null;
				this.isSelected = false;
				this.setBackground(BACKGROUND_COLOR);
				this.setOpaque(false);
				this.repaint();
				this.setForeground((java.awt.Color) javax.swing.UIManager.get("Label.foreground"));
			}
		}

		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (type == DIRECTORY) {
				changeDirectory(file);
			} else {
				if (!isSelected) {
					isSelected = true;
					if (currentlySelected != null) {
						currentlySelected.deSelect();
					}
					if (!openButton.isEnabled()) {
						openButton.setEnabled(true);
					}
					this.setBackground(SELECTED_COLOR);
					this.setOpaque(true);
					this.setForeground(SELECTED_TEXT_COLOR);
					currentlySelected = this;
					currentlySelected.repaint();
				}
				if (e.getClickCount() == 2) {
					openButton.doClick();
				}
			}
		}
		public void mouseReleased(java.awt.event.MouseEvent e) {
		}
		public void mousePressed(java.awt.event.MouseEvent e) {
		}
		public void mouseExited(java.awt.event.MouseEvent e) {
		}
		public void mouseEntered(java.awt.event.MouseEvent e) {
		}
	}
}