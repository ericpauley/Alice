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
import java.io.File;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class XMLDirectoryBuilder {

	public XMLDirectoryBuilder() {
		File mainFile = loadMainDirectory();
		generateDocument(mainFile);
	}

	protected void generateDocument(File mainFile) {
		Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException pce) {
			document = null;
		}

		if (document != null) {
			Element root = document.createElement("directory");
			root.setAttribute("path", mainFile.getName());
			if (mainFile.isDirectory()) {
				File[] kidFiles = mainFile.listFiles();
				for (int i = 0; i < kidFiles.length; i++) {
					createChildren(document, root, kidFiles[i], mainFile.getName());
				}
			}

			document.appendChild(root);
			document.getDocumentElement().normalize();

			try {
				java.io.FileWriter fileWriter = new java.io.FileWriter(mainFile.getAbsolutePath() + File.separator + "structure.xml");
				edu.cmu.cs.stage3.xml.Encoder.write(document, fileWriter);
				fileWriter.close();
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}

			//try {
			//((com.sun.xml.tree.XmlDocument)document).write( new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(mainFile.getAbsolutePath() + File.separator + "structure.xml"))) );
			//} catch (java.io.IOException ioe) {System.err.println("problems creating printwriter");};
		}
	}

	protected void createChildren(Document document, Element element, File file, String dir) {
		if (file.isDirectory()) {
			Element childElement = document.createElement("directory");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);

			File[] kidFiles = file.listFiles();
			for (int i = 0; i < kidFiles.length; i++) {
				createChildren(document, childElement, kidFiles[i], dir + '/' + file.getName());
			}
		} else if ((file.getName().endsWith(".jpg")) || (file.getName().endsWith(".gif"))) {
			Element childElement = document.createElement("image");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		} else if (file.getName().endsWith(".xml")) {
			Element childElement = document.createElement("xml");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		} else if (file.getName().endsWith(".a2c")) {
			Element childElement = document.createElement("model");
			childElement.setAttribute("path", dir + '/' + file.getName());
			element.appendChild(childElement);
		}

	}

	protected File loadMainDirectory() {
		java.net.URL imageURL = java.lang.ClassLoader.getSystemResource("edu\\cmu\\cs\\stage3\\caitlin\\personbuilder\\images");
		File mainFile = new java.io.File(imageURL.getFile());

		return mainFile;
	}
}