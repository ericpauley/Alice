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

package edu.cmu.cs.stage3.caitlin.personbuilder;

public class PersonBuilder extends javax.swing.JPanel {
	public static java.util.Vector getAllBuilders() {
		java.util.Vector builders = new java.util.Vector();
		String name = "";
		javax.swing.ImageIcon icon = null;
		org.w3c.dom.Document doc = (org.w3c.dom.Document) XMLDirectoryUtilities.loadFile("images/builders.xml");
		org.w3c.dom.Node root = doc.getDocumentElement();

		org.w3c.dom.NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			org.w3c.dom.Node node = nl.item(i);
			if (node.getNodeName().equals("builder")) {
				org.w3c.dom.NamedNodeMap nodeMap = node.getAttributes();
				for (int j = 0; j < nodeMap.getLength(); j++) {
					org.w3c.dom.Node attr = nodeMap.item(j);
					if (attr.getNodeName().equals("name")) {
						name = attr.getNodeValue();
					} else if (attr.getNodeName().equals("icon")) {
						String iconName = attr.getNodeValue();
						icon = new javax.swing.ImageIcon(PersonBuilder.class.getResource("images/" + iconName), iconName);
					}
				}
				edu.cmu.cs.stage3.util.StringObjectPair sop = new edu.cmu.cs.stage3.util.StringObjectPair(name, icon);
				builders.addElement(sop);
			}
		}

		return builders;
	}
	
	private AllStepsPanel allStepsPanel = null;
	private NavigationPanel navPanel = null;
	private RenderPanel renderPanel = null;
	private NamePanel namePanel = null;
	private ModelWrapper modelWrapper = null;
	private String builderName = "";

	public PersonBuilder( String builderName, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		this.builderName = builderName;
		String builderFile = "images/" + builderName + ".xml";
		int progressOffset = 0;
		//progressObserver.progressBegin( edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL );
		progressObserver.progressBegin( 7 );
		try {
			org.w3c.dom.Document doc = (org.w3c.dom.Document) XMLDirectoryUtilities.loadFile(builderFile);
			progressObserver.progressUpdate( progressOffset++, null );
			org.w3c.dom.Node root = doc.getDocumentElement();
			progressObserver.progressUpdate( progressOffset++, null );
			modelWrapper = new ModelWrapper(root);
			progressObserver.progressUpdate( progressOffset++, null );
			allStepsPanel = new AllStepsPanel(root, modelWrapper, progressObserver, progressOffset );
			navPanel = new NavigationPanel(root, allStepsPanel);
			renderPanel = new RenderPanel(modelWrapper);
			namePanel = new NamePanel();
			renderPanel.initialize();

			setLayout(new java.awt.BorderLayout());
			add(allStepsPanel, java.awt.BorderLayout.EAST);
			add(renderPanel, java.awt.BorderLayout.CENTER);
			add(navPanel, java.awt.BorderLayout.NORTH);
			add(namePanel, java.awt.BorderLayout.SOUTH);
		} finally {
			progressObserver.progressEnd();
		}
		setBackground(new java.awt.Color(155, 159, 206));
		setSize(500, 500);
	}

	public void reset() {
		modelWrapper.resetWorld();
		try {
			allStepsPanel.resetDefaults();
		} catch (Exception e) {
			e.printStackTrace();
		}
		navPanel.setFirstStep();
	}
	public edu.cmu.cs.stage3.alice.core.Model getModel() {
		edu.cmu.cs.stage3.alice.core.Model model = modelWrapper.getModel();
		String text = namePanel.getCreatedBy();
		if( text.length()== 0 ) {
			text = "Anonymous";
		}
		model.data.put( "created by", text );

		text = namePanel.getName();
		if( text.length()== 0 ) {
			text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "What would you like to name your character?" );
		}
		text = text.trim();
		if( text.startsWith( "\"" ) || text.startsWith( "'" ) ) {
			text = text.substring( 1 );
		}
		if( text.endsWith( "\"" ) || text.endsWith( "'" ) ) {
			text = text.substring( 0, text.length()-1 );
		}
		model.name.set( text );

		return model;
	}
	
}