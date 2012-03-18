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

import javax.swing.JPanel;
import java.util.Vector;
import org.w3c.dom.*;

public class StringPropertySetter extends JPanel implements javax.swing.event.DocumentListener {
	String propertyName = "";
	String labelText = "";
	String defaultValue = "";

	ModelWrapper modelWrapper = null;

	javax.swing.JTextField valueField = null;

	public StringPropertySetter(Node propNode, ModelWrapper modelWrapper) {
		this.modelWrapper = modelWrapper;

		Node propDetailsNode = loadPropertyXML(propNode);
		getPropertyInfo(propDetailsNode);
		createGUI();
	}

	public void changedUpdate(javax.swing.event.DocumentEvent de) {
		//System.out.println("changed: ");
		modelWrapper.setProperty(propertyName, valueField.getText(), labelText);
	}

	public void insertUpdate(javax.swing.event.DocumentEvent de) {
		//System.out.println("insert: ");
		modelWrapper.setProperty(propertyName, valueField.getText(), labelText);
	}

	public void removeUpdate(javax.swing.event.DocumentEvent de) {
		//System.out.println("remove: ");
		String value = valueField.getText();
		if (value.equals("")) {
			value = "noname";
		}
		modelWrapper.setProperty(propertyName, value, labelText);
	}

	protected Node loadPropertyXML(Node propNode) {
		String path = "";
		java.net.URL propSetURL = null;
		Node propSetNode = propNode;

		//    // get the top level prop set node
		//    NodeList nList = propNode.getChildNodes();
		//    for (int i = 0; i < nList.getLength(); i++) {
		//      Node curNode = nList.item(i);
		//      if (curNode.getNodeName().equals("propertySet")) {
		//        propSetNode = curNode;
		//      }
		//    }

		//    // get the xml path from here
		java.net.URL xmlURL = null;
		Vector xmlFiles = XMLDirectoryUtilities.getXMLURLs(propSetNode);
		if (xmlFiles.size() == 1) {
			xmlURL = (java.net.URL) xmlFiles.elementAt(0);
		} else {
			System.out.println("Zero or Multiple xml files found");
		}

		if (xmlURL != null) {
			Document doc = (Document) XMLDirectoryUtilities.loadURL(xmlURL);
			return doc.getDocumentElement();
			//return node;
		} else {
			return null;
		}
	}

	protected void getPropertyInfo(Node propNode) {
		NodeList nList = propNode.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			Node curNode = nList.item(i);
			if (curNode.getNodeName().equals("setProperty")) {
				NamedNodeMap attrs = curNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					if (attr.getNodeName().equals("name")) {
						propertyName = attr.getNodeValue();
					} else if (attr.getNodeName().equals("description")) {
						labelText = attr.getNodeValue();
					} else if (attr.getNodeName().equals("defaultValue")) {
						defaultValue = attr.getNodeValue();
					}
				}
			}
		}
	}

	protected void createGUI() {
		this.setLayout(new java.awt.FlowLayout());
		javax.swing.JLabel label = new javax.swing.JLabel(labelText);
		valueField = new javax.swing.JTextField(defaultValue);

		valueField.getDocument().addDocumentListener(this);

		this.add(label);
		this.add(valueField);
	}
}