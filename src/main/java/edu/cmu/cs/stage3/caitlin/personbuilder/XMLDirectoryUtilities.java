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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class XMLDirectoryUtilities {

	public XMLDirectoryUtilities() {
	}

	public static Node loadURL(java.net.URL url) {
		Document document = null;
		if (url != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(url.openStream());
			} catch (java.io.IOException ioe) {
				document = null;
				ioe.printStackTrace();
			} catch (ParserConfigurationException pce) {
				document = null;
				pce.printStackTrace();
			} catch (org.xml.sax.SAXException se) {
				document = null;
				se.printStackTrace();
			}

			return document;
		}
		return null;
	}

	public static String getPath(Node node) {
		NamedNodeMap nodeMap = node.getAttributes();
		Node pathNode = nodeMap.getNamedItem("path");
		if (pathNode != null) {
			return pathNode.getNodeValue();
		} else
			return null;
	}

	public static Node loadFile(String fileName) {
		java.net.URL url = XMLDirectoryUtilities.class.getResource(fileName);
		return loadURL(url);
	}

	public static Vector getDirectories(Node node) {
		NodeList nList = node.getChildNodes();
		Vector directoryNodes = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals("directory")) {
				directoryNodes.addElement(kidNode);
			}
		}
		return directoryNodes;
	}

	public static Vector getSetColorNodes(Node node) {
		NodeList nList = node.getChildNodes();
		Vector propertySetNodes = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals("setColor")) {
				propertySetNodes.addElement(kidNode);
			}
		}
		return propertySetNodes;
	}

	protected static Vector getFilesOfType(String nodeType, Node node) {
		NodeList nList = node.getChildNodes();
		Vector files = new Vector();
		for (int i = 0; i < nList.getLength(); i++) {
			Node kidNode = nList.item(i);
			if (kidNode.getNodeName().equals(nodeType)) {
				NamedNodeMap nodeMap = kidNode.getAttributes();
				Node pathNode = nodeMap.getNamedItem("path");
				String path = null;
				if (pathNode != null) {
					path = pathNode.getNodeValue();
					java.net.URL url = PersonBuilder.class.getResource(path);
					if (url != null)
						files.addElement(url);
				}
			}
		}
		return files;
	}

	public static Vector getImageURLs(Node node) {
		return getFilesOfType("image", node);
	}

	public static Vector getImages(Node node) {
		Vector urls = getImageURLs(node);
		Vector images = new Vector();
		java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		for (int i = 0; i < urls.size(); i++) {
			java.net.URL url = (java.net.URL) urls.elementAt(i);
			try {
				java.awt.Image img = tk.createImage(url);
				if (img != null)
					images.addElement(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return images;
	}

	public static Vector getModelURLs(Node node) {
		return getFilesOfType("model", node);
	}

	public static Vector getXMLURLs(Node node) {
		return getFilesOfType("xml", node);
	}

	public static Vector getPropertySets(Node node) {
		return getFilesOfType("propertySet", node);
	}
}