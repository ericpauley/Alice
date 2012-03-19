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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import java.io.File;

import javax.swing.ScrollPaneConstants;

/**
 * @author David Culyba
 * 
 */

public class GalleryViewer extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel {

	protected static final String FILENAME = "directoryIndex.xml";
	protected static final String FILENAME2 = "galleryIndex.xml";
	public static final int LOCAL = 1;
	public static final int WEB = 2;
	public static final int CD = 3;
	protected final java.awt.Insets panelInset = new java.awt.Insets(1, 2, 0, 0);
	public static final String webGalleryName = "Web Gallery";
	public static final String localGalleryName = "Local Gallery";
	public static final String cdGalleryName = "CD Gallery";

	public static String webGalleryRoot;
	public static String localGalleryRoot;
	public static String cdGalleryRoot;

	protected static boolean alreadyEnteredWebGallery = false;

	public static final String homeName = "Home";
	protected static final java.awt.Color backgroundColor = new java.awt.Color(118, 128, 128);
	protected static final java.awt.Color textColor = new java.awt.Color(255, 255, 255);
	protected static final java.awt.Color linkColor = new java.awt.Color(153, 204, 255);
	protected static final String noModelsYet = "No models found yet.";
	public static String cacheDir;

	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage());

	protected RootDirectoryStructure webGallery = null;
	protected RootDirectoryStructure localGallery = null;
	protected RootDirectoryStructure cdGallery = null;
	protected java.util.Vector currentGalleryObjects;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel objectPanel;
	protected java.awt.FlowLayout objectPanelLayout;
	protected javax.swing.JPanel directoryPanel;
	protected javax.swing.JPanel searchPanel;
	protected java.util.Vector rootDirectories;
	protected DirectoryStructure directoryOnDisplay;
	protected DirectoryStructure searchResults;
	protected DirectoryStructure oldDirectoryOnDisplay;
	protected javax.swing.ImageIcon webGalleryIcon;
	protected javax.swing.ImageIcon localGalleryIcon;
	protected javax.swing.ImageIcon cdGalleryIcon;
	protected javax.swing.ImageIcon add3DTextIcon;
	protected ObjectXmlData add3DTextData;
	protected TextBuilderButton add3DTextButton;
	protected javax.swing.JButton upLevelButton;

	public static javax.swing.ImageIcon noFolderImageIcon;
	public static javax.swing.ImageIcon loadingImageIcon;
	public static javax.swing.ImageIcon noImageIcon;

	protected boolean inBrowseMode;
	protected boolean stopSearch = true;

	protected class SearchingLabel extends javax.swing.JLabel {

		protected String doneString = "Ready to search";
		protected String searchingString1 = "Searching";
		protected String searchingString2 = "Searching .";
		protected String searchingString3 = "Searching . .";
		protected String searchingString4 = "Searching . . .";
		protected int state = 0;

		public SearchingLabel() {
			super();
			setText(doneString);
			setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			setForeground(java.awt.Color.white);
		}

		public void advance() {
			state = (state + 1) % 4;
			switch (state) {
				case 0 :
					setText(searchingString1);
					break;
				case 1 :
					setText(searchingString2);
					break;
				case 2 :
					setText(searchingString3);
					break;
				case 3 :
					setText(searchingString4);
					break;
			}
			this.repaint();
		}

		public void reset() {
			setText(doneString);
			this.repaint();
		}
	}

	protected SearchingLabel searchingProgressLabel = new SearchingLabel();
	protected String lastSearchString = "";
	protected String stopSearchString = "Stop Search!";
	protected String startSearchString = "Search!";
	protected String startSearchWebString = "Search www.alice.org";
	protected String webGalleryHostName = "www.alice.org";
	protected String searchString = "Browse Gallery";
	protected String browseString = "Search Gallery";
	protected javax.swing.JLabel attributeLabel;
	protected javax.swing.JPanel attributePanel;
	protected javax.swing.JTextField searchField;
	protected javax.swing.JButton searchButton;
	protected javax.swing.JButton searchWebButton;
	protected javax.swing.JButton searchBrowseButton;
	protected javax.swing.JPanel headerPanel;
	protected javax.swing.JLabel noObjectsLabel;
	protected javax.swing.JLabel noSearchResults;
	protected javax.swing.JLabel searching;
	protected java.awt.GridBagConstraints glueConstraints = new java.awt.GridBagConstraints(0, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0);
	protected java.lang.Thread changingThread;
	protected java.util.Vector builderButtonsVector;

	protected static double bitsPerSecond = 0;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected static javax.swing.JFrame aliceFrame;
	protected ModelInfoContentPane modelContentPane;

	protected int searchCount = 0;
	protected boolean updatePanelsWhileLoading = false;
	protected boolean stopBuildingGallery = false;
	protected boolean isInWebGallery = false;
	protected boolean oldIsInWebGalleryValue = false;

	protected AliceCharacterFilter characterFilter = new AliceCharacterFilter();
	protected DirectoryFilter directoryFilter = new DirectoryFilter();
	protected ThumbnailFilter thumbnailFilter = new ThumbnailFilter();

	protected class AliceCharacterFilter implements java.io.FileFilter {
		private javax.swing.filechooser.FileFilter filter;

		public AliceCharacterFilter() {
		}

		public void setFilter(javax.swing.filechooser.FileFilter f) {
			filter = f;
		}

		@Override
		public boolean accept(java.io.File fileToCheck) {
			if (filter != null) {
				if (!fileToCheck.isDirectory()) {
					return filter.accept(fileToCheck) || fileToCheck.getName().endsWith(".link");
				} else {
					return false;
				}
			} else {
				if (!fileToCheck.isDirectory()) {
					return fileToCheck.getName().endsWith(".a2c") || fileToCheck.getName().endsWith(".link");
				}
			}
			return false;
		}
	}

	protected class ThumbnailFilter implements java.io.FilenameFilter {
		public ThumbnailFilter() {
		}

		@Override
		public boolean accept(java.io.File directory, String name) {
			if (name.equalsIgnoreCase("directoryThumbnail.png")) {
				return true;
			}
			return false;
		}
	}

	protected class DirectoryFilter implements java.io.FilenameFilter {
		public DirectoryFilter() {
		}

		@Override
		public boolean accept(java.io.File directory, String name) {
			if (name.indexOf('.') == -1) {
				return true;
			} else {
				// System.out.println("rejected "+directory.getAbsolutePath()+"   "+name);
			}
			return false;
		}
	}

	public class ObjectXmlData {
		public String name;
		public int size = -1;
		public javax.vecmath.Vector3d dimensions = new javax.vecmath.Vector3d();
		public String objectFilename = null;
		public String imageFilename = null;
		public java.util.Vector details = new java.util.Vector();
		public java.awt.datatransfer.Transferable transferable;
		public int type;
		public GalleryViewer mainViewer;
		public DirectoryStructure directoryData = null;
		public DirectoryStructure parentDirectory = null;
		public long timeStamp = 1;
		public boolean isThere = false;

		public ObjectXmlData() {
		}

		@Override
		public boolean equals(Object toCheck) {
			if (toCheck instanceof ObjectXmlData) {
				ObjectXmlData b = (ObjectXmlData) toCheck;
				boolean toReturn = b.size == size && b.name.equals(name) && b.timeStamp == timeStamp;
				return toReturn;
			}
			return false;
		}

		public boolean matches(String toSearch) {
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(toSearch.toUpperCase(), " ");
			while (tokenizer.hasMoreTokens()) {
				String current = tokenizer.nextToken();
				if (name.toUpperCase().indexOf(current) > -1) {
					return true;
				}
			}
			return false;
		}

		public void setDimensions(String dimensionString) {
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(dimensionString, "x");
			String length = "", width = "", depth = "";
			if (tokenizer.hasMoreTokens()) {
				length = tokenizer.nextToken();
				while (length.endsWith("m") || length.endsWith(" ") || length.endsWith("M")) {
					length = length.substring(0, length.length() - 1);
				}
			}
			if (tokenizer.hasMoreTokens()) {
				width = tokenizer.nextToken();
				while (width.endsWith("m") || width.endsWith(" ") || width.endsWith("M")) {
					width = width.substring(0, width.length() - 1);
				}
			}
			if (tokenizer.hasMoreTokens()) {
				depth = tokenizer.nextToken();
				while (depth.endsWith("m") || depth.endsWith(" ") || depth.endsWith("M")) {
					depth = depth.substring(0, depth.length() - 1);
				}
			}
			try {
				double lengthVal = Double.parseDouble(length);
				double widthVal = Double.parseDouble(width);
				double depthVal = Double.parseDouble(depth);
				dimensions.set(lengthVal, widthVal, depthVal);
			} catch (Exception e) {}
		}

		public void setSize(String sizeString) {
			char suffix = sizeString.charAt(sizeString.length() - 1);
			if (suffix == 'm' || suffix == 'M') {
				try {
					float megSize = Float.parseFloat(sizeString.substring(0, sizeString.length() - 1));
					size = (int) (megSize * 1000);
				} catch (java.lang.NumberFormatException e) {
					size = -1;
				}
			} else if (sizeString.endsWith("mb") || sizeString.endsWith("Mb") || sizeString.endsWith("MB") || sizeString.endsWith("mB")) {
				try {
					float megSize = Float.parseFloat(sizeString.substring(0, sizeString.length() - 2));
					size = (int) (megSize * 1000);
				} catch (java.lang.NumberFormatException e) {
					size = -1;
				}
			} else if (sizeString.endsWith("kb") || sizeString.endsWith("Kb") || sizeString.endsWith("KB") || sizeString.endsWith("kB")) {
				try {
					size = (int) Float.parseFloat(sizeString.substring(0, sizeString.length() - 2));
				} catch (java.lang.NumberFormatException e) {
					size = -1;
				}
			} else if (suffix == 'k' || suffix == 'K') {
				try {
					size = (int) Float.parseFloat(sizeString.substring(0, sizeString.length() - 1));
				} catch (java.lang.NumberFormatException e) {
					size = -1;
				}
			} else {
				try {
					size = (int) Float.parseFloat(sizeString);
				} catch (java.lang.NumberFormatException e) {
					size = -1;
				}
			}
		}

		public void incrementName() {
			// TODO: write this function
		}

		public void addDetail(String detailName, Object detailValue) {
			details.add(new edu.cmu.cs.stage3.util.StringObjectPair(detailName, detailValue));
		}

		public String getDetail(String toGet) {
			for (int i = 0; i < details.size(); i++) {
				edu.cmu.cs.stage3.util.StringObjectPair current = (edu.cmu.cs.stage3.util.StringObjectPair) details.get(i);
				if (current.getString().equalsIgnoreCase(toGet)) {
					if (current.getObject() instanceof String) {
						return (String) current.getObject();
					} else {
						String toReturn = "";
						if (current.getObject() instanceof java.util.Vector) {
							java.util.Vector allDetails = (java.util.Vector) current.getObject();
							for (int j = 0; j < allDetails.size(); j++) {
								String currentDetail = allDetails.get(j).toString();
								if (j < allDetails.size() - 1) {
									currentDetail += ", ";
								}
								toReturn += currentDetail;
							}
							return toReturn;
						}
					}
				}
			}
			return null;
		}

	}

	protected class DirectoryXmlData {
		public java.util.Vector directories = new java.util.Vector();
		public java.util.Vector models = new java.util.Vector();
		public String name = "directory";

		public DirectoryXmlData(String name) {
			this.name = name;
		}

		public void addDirectory(ObjectXmlData dir) {
			for (int i = 0; i < directories.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) directories.get(i);
				if (current.equals(dir)) {
					return;
				}
			}
			directories.add(dir);
		}

		public void addDirectory(ObjectXmlData dir, int index) {
			for (int i = 0; i < directories.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) directories.get(i);
				if (current.equals(dir)) {
					return;
				}
			}
			directories.insertElementAt(dir, index);
		}

		public void addModel(ObjectXmlData model) {
			for (int i = 0; i < models.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) models.get(i);
				if (current.equals(model)) {
					return;
				}
			}
			models.add(model);
		}

		public void addModel(ObjectXmlData model, int index) {
			for (int i = 0; i < models.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) models.get(i);
				if (current.equals(model)) {
					return;
				}
			}
			if (index > -1 && index <= models.size()) {
				models.insertElementAt(model, index);
			} else {
				models.add(model);
			}
		}

		/**
		 * returns a matching ObjectXmlData object contained in this directory
		 * based on the ObjectXmlData.equals() call
		 */
		public ObjectXmlData getModel(ObjectXmlData toGet) {
			for (int i = 0; i < models.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) models.get(i);
				if (current.equals(toGet)) {
					return current;
				}
			}
			return null;
		}

		public DirectoryStructure getDirectory(int i) {
			if (directories != null) {
				return ((ObjectXmlData) directories.get(i)).directoryData;
			}
			return null;
		}

		public ObjectXmlData getDirectory(ObjectXmlData toGet) {
			for (int i = 0; i < directories.size(); i++) {
				ObjectXmlData current = (ObjectXmlData) directories.get(i);
				if (current.equals(toGet)) {
					return current;
				}
			}
			return null;
		}

		public DirectoryStructure getDirectoryNamed(String toSearchFor) {
			if (directories != null) {
				for (int i = 0; i < directories.size(); i++) {
					String currentName = ((ObjectXmlData) directories.get(i)).name;
					if (currentName.equals(toSearchFor)) {
						return ((ObjectXmlData) directories.get(i)).directoryData;
					}
				}
			}
			return null;
		}

		public ObjectXmlData getDirectoryXMLNamed(String toSearchFor) {
			if (directories != null) {
				for (int i = 0; i < directories.size(); i++) {
					String currentName = ((ObjectXmlData) directories.get(i)).name;
					if (currentName.equals(toSearchFor)) {
						return (ObjectXmlData) directories.get(i);
					}
				}
			}
			return null;
		}

	}

	protected class DirectoryStructure {
		public String name;
		public DirectoryStructure parent;
		public DirectoryXmlData xmlData;
		public DirectoryStructure firstLocalDirectory = null;
		public DirectoryStructure secondLocalDirectory = null;
		public String path;
		public DirectoryStructure directoryToUse;
		public ObjectXmlData data;

		public RootDirectoryStructure rootNode;

		public DirectoryStructure(RootDirectoryStructure root, String name, String path) {
			directoryToUse = this;
			rootNode = root;
			parent = null;
			this.name = name;
			this.path = path;
		}

		@Override
		public boolean equals(Object toCompareTo) {
			if (toCompareTo instanceof DirectoryStructure) {
				DirectoryStructure b = (DirectoryStructure) toCompareTo;
				if (b.name.equals(name)) {
					if (data != null && b.data != null) {
						return data.timeStamp == b.data.timeStamp;
					}
				}
			}
			return false;
		}

		public boolean contains(ObjectXmlData toSearchFor) {
			if (xmlData != null) {
				for (int i = 0; i < xmlData.models.size(); i++) {
					if (((ObjectXmlData) xmlData.models.get(i)).equals(toSearchFor)) {
						return true;
					}
				}
				for (int i = 0; i < xmlData.directories.size(); i++) {
					if (((ObjectXmlData) xmlData.directories.get(i)).equals(toSearchFor)) {
						return true;
					}
				}
			}
			return false;
		}

		public java.util.Vector getObjectMatches(String searchString) {
			java.util.Vector toReturn = null;
			if (xmlData != null) {
				toReturn = new java.util.Vector();
				for (int i = 0; i < xmlData.models.size(); i++) {
					if (((ObjectXmlData) xmlData.models.get(i)).matches(searchString)) {
						toReturn.add(xmlData.models.get(i));
					}
					if (stopSearch) {
						break;
					}
				}
			}
			return toReturn;
		}

		public ObjectXmlData getObjectNamed(String toSearchFor) {
			if (xmlData != null) {
				for (int i = 0; i < xmlData.models.size(); i++) {
					String currentName = ((ObjectXmlData) xmlData.models.get(i)).name;
					if (currentName.equals(toSearchFor)) {
						return (ObjectXmlData) xmlData.models.get(i);
					}
				}
				for (int i = 0; i < xmlData.directories.size(); i++) {
					String currentName = ((ObjectXmlData) xmlData.directories.get(i)).name;
					if (currentName.equals(toSearchFor)) {
						return (ObjectXmlData) xmlData.directories.get(i);
					}
				}
			}
			return null;
		}

		public ObjectXmlData getObjectFileNamed(String toSearchFor) {
			if (xmlData != null) {
				for (int i = 0; i < xmlData.models.size(); i++) {
					String currentName = ((ObjectXmlData) xmlData.models.get(i)).parentDirectory.rootNode.rootPath + ((ObjectXmlData) xmlData.models.get(i)).objectFilename;
					if (currentName.equals(toSearchFor)) {
						return (ObjectXmlData) xmlData.models.get(i);
					}
				}
				for (int i = 0; i < xmlData.directories.size(); i++) {
					String currentName = ((ObjectXmlData) xmlData.directories.get(i)).parentDirectory.rootNode.rootPath + ((ObjectXmlData) xmlData.directories.get(i)).objectFilename;
					if (currentName.equals(toSearchFor)) {
						return (ObjectXmlData) xmlData.directories.get(i);
					}
				}
			}
			return null;
		}

		public ObjectXmlData getModel(java.awt.datatransfer.Transferable t) {
			if (xmlData != null) {
				for (int i = 0; i < xmlData.models.size(); i++) {
					if (t == ((ObjectXmlData) xmlData.models.get(i)).transferable) {
						return (ObjectXmlData) xmlData.models.get(i);
					}
				}
			}
			return null;
		}

		public DirectoryStructure getDirectoryNamed(String toSearchFor) {
			if (xmlData != null) {
				return xmlData.getDirectoryNamed(toSearchFor);
			}
			return null;
		}

		public ObjectXmlData getDirectoryXMLNamed(String toSearchFor) {
			if (xmlData != null) {
				return xmlData.getDirectoryXMLNamed(toSearchFor);
			}
			return null;
		}

		public String getGUIPath() {
			DirectoryStructure parentDir = parent;
			String toReturn = new String(name);
			while (parentDir != null) {
				toReturn = parentDir.name + java.io.File.separator + toReturn;
				parentDir = parentDir.parent;
			}
			toReturn = homeName + java.io.File.separator + toReturn;
			return toReturn;
		}

		public DirectoryStructure getDirectory(int i) {
			if (xmlData != null) {
				return xmlData.getDirectory(i);
			}
			return null;
		}

		private DirectoryStructure initDirStructure(ObjectXmlData currentDirData) {
			if (currentDirData == null) {
				return null;
			}
			currentDirData.parentDirectory = this;
			DirectoryStructure currentDir = new DirectoryStructure(rootNode, currentDirData.name, currentDirData.objectFilename);
			if (firstLocalDirectory != null) {
				currentDir.firstLocalDirectory = firstLocalDirectory.getDirectoryNamed(currentDirData.name);
				if (currentDir.equals(currentDir.firstLocalDirectory)) {
					// currentDir.directoryToUse =
					// currentDir.firstLocalDirectory; // For using local
					// gallery as web gallery
				}
			}
			if (secondLocalDirectory != null) {
				currentDir.secondLocalDirectory = secondLocalDirectory.getDirectoryNamed(currentDirData.name);
				if (currentDir.equals(currentDir.secondLocalDirectory) && currentDir.directoryToUse == currentDir) {
					// currentDir.directoryToUse =
					// currentDir.secondLocalDirectory; // For using local
					// gallery as web gallery
				}
			}
			currentDir.data = currentDirData;
			currentDirData.directoryData = currentDir;
			currentDir.parent = this;
			return currentDir;
		}

		private ObjectXmlData createObjectFromZip(java.io.File zipFileSource, javax.xml.parsers.DocumentBuilder builder) {
			java.util.zip.ZipFile zipFile = null;
			try {
				zipFile = new java.util.zip.ZipFile(zipFileSource);
			} catch (Exception e) {
				return null;
			}
			String xml = getXML(zipFile);
			ObjectXmlData currentModelData = null;
			if (xml != null && xml != "") {
				java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(xml.getBytes());
				org.w3c.dom.Document document;
				org.w3c.dom.Element xmlRoot;
				try {
					document = builder.parse(bais);
					xmlRoot = document.getDocumentElement();
				}

				catch (java.io.IOException e) {
					return null;
				} catch (java.lang.IllegalArgumentException e) {
					return null;
				} catch (org.xml.sax.SAXException e) {
					return null;
				}
				org.w3c.dom.NodeList xmlModels = xmlRoot.getElementsByTagName("model");
				org.w3c.dom.Node currentModel = null;
				if (xmlModels.getLength() < 1) {
					currentModel = xmlRoot;
				} else {
					currentModel = xmlModels.item(0);
				}
				currentModelData = createObjectXmlData(currentModel, rootNode.rootPath, rootNode.type);
			}
			if (currentModelData == null) {
				currentModelData = createObjectXmlData(zipFileSource, rootNode.rootPath, rootNode.type);
			} else {
				String relativeFilename = getRelativeDirectory(rootNode.rootPath, zipFileSource.getAbsolutePath(), java.io.File.separator);
				currentModelData.objectFilename = relativeFilename + zipFileSource.getName();
				currentModelData.imageFilename = currentModelData.objectFilename;
			}
			currentModelData.size = (int) (zipFileSource.length() / 1000.0);
			currentModelData.timeStamp = zipFileSource.lastModified();
			currentModelData.transferable = createFileTransferable(zipFileSource.getAbsolutePath());
			return currentModelData;
		}

		private void removeMissing(java.util.Vector toSearch) {
			int count = 0;
			while (count < toSearch.size()) {
				ObjectXmlData current = (ObjectXmlData) toSearch.get(count);
				if (!current.isThere) {
					toSearch.remove(count);
				} else {
					count++;
				}
			}
			for (int i = 0; i < toSearch.size(); i++) {
				((ObjectXmlData) toSearch.get(i)).isThere = false;
			}
		}

		protected String getLinkPath(java.io.File linkFile) {
			try {
				java.io.BufferedReader fileReader = new java.io.BufferedReader(new java.io.FileReader(linkFile));
				char b[] = new char[1000];
				int numRead = fileReader.read(b);
				String content = new String(b, 0, numRead);
				while (numRead != -1) {
					numRead = fileReader.read(b);
					if (numRead != -1) {
						String newContent = new String(b, 0, numRead);
						content += newContent;
					}
				}

				String toReturn = rootNode.rootPath + makeRelativePathReady(content);
				return toReturn;
			} catch (Exception e) {}
			return null;
		}

		public int updateSelf(java.io.File dirFile) throws java.io.IOException, java.lang.IllegalArgumentException, org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException {
			if (dirFile == null || !dirFile.isDirectory() || !dirFile.canRead()) {
				return -1;
			}
			java.io.File[] modelsInDir = dirFile.listFiles(characterFilter);
			java.io.File[] dirsInDir = dirFile.listFiles(directoryFilter);
			int total = modelsInDir.length + dirsInDir.length;
			glueConstraints.gridx = total;
			int count = 0;
			for (int dirIndex = 0; dirIndex < dirsInDir.length; dirIndex++) {
				if (stopBuildingGallery) {
					return -1;
				}
				ObjectXmlData currentDirData = getObjectFileNamed(dirsInDir[dirIndex].getAbsolutePath());
				if (currentDirData == null) {
					currentDirData = createDirectoryObjectXmlData(dirsInDir[dirIndex], rootNode.rootPath, rootNode.type);
					DirectoryStructure currentDir = initDirStructure(currentDirData);
					if (currentDir != null && currentDirData != null) {
						xmlData.addDirectory(currentDir.directoryToUse.data, dirIndex);
						if (updatePanelsWhileLoading) {
							directoryAdded(currentDir.directoryToUse.data, count);
							updateLoading((float) count / total);
						}
					}
				} else {
					count++;
				}
				if (currentDirData != null) {
					currentDirData.isThere = true;
				}
			}
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			javax.xml.parsers.DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (javax.xml.parsers.ParserConfigurationException e) {
				return count;
			}
			for (int i = 0; i < modelsInDir.length; i++) {
				if (stopBuildingGallery) {
					return -1;
				}
				String absolutePath = modelsInDir[i].getAbsolutePath();
				java.io.File toAdd = modelsInDir[i];
				if (modelsInDir[i].getAbsolutePath().endsWith(".link")) {
					absolutePath = getLinkPath(modelsInDir[i]);
					if (absolutePath != null) {
						toAdd = new java.io.File(absolutePath);
					}

				}
				if (absolutePath != null) {
					ObjectXmlData currentModelData = getObjectFileNamed(absolutePath);
					if (currentModelData == null) {
						currentModelData = createObjectFromZip(toAdd, builder);
						if (currentModelData != null) {
							count++;
							currentModelData.parentDirectory = this;
							xmlData.addModel(currentModelData, i);
							if (updatePanelsWhileLoading) {
								modelAdded(currentModelData, count);
								updateLoading((float) count / total);
							}
						}
					} else {
						count++;
					}
					if (currentModelData != null) {
						currentModelData.isThere = true;
					}
				}
			}
			// Add people here too...
			if (name.equalsIgnoreCase("people") && updatePanelsWhileLoading && directoryOnDisplay != searchResults) {
				for (int p = 0; p < builderButtonsVector.size(); p++) {
					final GenericBuilderButton builderButton = (GenericBuilderButton) builderButtonsVector.get(p);
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							objectPanel.add(builderButton);
							builderButton.updateGUI();
							objectPanel.repaint();
						}
					});
				}
			}
			if (name.equalsIgnoreCase("local gallery")) {
				count++;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						objectPanel.add(add3DTextButton);
						add3DTextButton.updateGUI();
						objectPanel.repaint();
					}
				});
			}
			removeMissing(xmlData.directories);
			removeMissing(xmlData.models);
			return count;
		}

		public int initSelf(java.io.File dirFile) throws java.io.IOException, java.lang.IllegalArgumentException, org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException {
			if (dirFile == null || !dirFile.isDirectory() || !dirFile.canRead()) {
				return -1;
			}
			xmlData = new DirectoryXmlData(name);
			java.io.File[] modelsInDir = dirFile.listFiles(characterFilter);
			java.io.File[] dirsInDir = dirFile.listFiles(directoryFilter);

			int total = modelsInDir.length + dirsInDir.length;
			glueConstraints.gridx = total;
			int count = 0;
			for (File element : dirsInDir) {
				if (stopBuildingGallery) {
					xmlData = null;
					return -1;
				}
				count++;
				ObjectXmlData currentDirData = createDirectoryObjectXmlData(element, rootNode.rootPath, rootNode.type);
				DirectoryStructure currentDir = initDirStructure(currentDirData);
				if (currentDir != null && currentDirData != null) {
					xmlData.addDirectory(currentDir.directoryToUse.data);
					if (updatePanelsWhileLoading) {
						directoryAdded(currentDir.directoryToUse.data, count);
						updateLoading((float) count / total);
					}
				}
			}
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			javax.xml.parsers.DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (javax.xml.parsers.ParserConfigurationException e) {
				return count;
			}
			for (File element : modelsInDir) {
				if (stopBuildingGallery) {
					xmlData = null;
					return -1;
				}
				java.io.File toAdd = element;
				if (element.getAbsolutePath().endsWith(".link")) {
					String absolutePath = getLinkPath(element);
					if (absolutePath != null) {
						toAdd = new java.io.File(absolutePath);
					}
				}
				ObjectXmlData currentModelData = createObjectFromZip(toAdd, builder);
				if (currentModelData != null) {
					count++;
					currentModelData.parentDirectory = this;
					xmlData.addModel(currentModelData);
					if (updatePanelsWhileLoading) {
						modelAdded(currentModelData, count);
						updateLoading((float) count / total);
					}
				}
			}
			if (name.equalsIgnoreCase("people") && updatePanelsWhileLoading && directoryOnDisplay != searchResults) {
				for (int p = 0; p < builderButtonsVector.size(); p++) {
					count++;
					final GenericBuilderButton builderButton = (GenericBuilderButton) builderButtonsVector.get(p);
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							objectPanel.add(builderButton);
							builderButton.updateGUI();
							objectPanel.repaint();
						}
					});
				}
			}
			if (name.equalsIgnoreCase("local gallery")) {
				count++;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						objectPanel.add(add3DTextButton);
						add3DTextButton.updateGUI();
						objectPanel.repaint();
					}
				});

			}
			if (count == 0) {// updatePanelsWhileLoading && count == 0){
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						objectPanel.removeAll();
						objectPanel.add(noObjectsLabel);
					}
				});
			}
			return count;
		}

		public int initSelf(String xml) throws java.io.IOException, java.lang.IllegalArgumentException, org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException {
			if (xml == null) {
				return -1;
			}
			long oldTime = System.currentTimeMillis();
			xmlData = new DirectoryXmlData(name);
			xmlData.name = name;
			java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(xml.getBytes());
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			org.w3c.dom.Document document;
			org.w3c.dom.Element xmlRoot;
			try {
				javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
				// System.out.println("0a: "+(System.currentTimeMillis()-oldTime));
				oldTime = System.currentTimeMillis();
				document = builder.parse(bais);
				// System.out.println("parse time: "+(System.currentTimeMillis()-oldTime));
				oldTime = System.currentTimeMillis();
				xmlRoot = document.getDocumentElement();
			} catch (javax.xml.parsers.ParserConfigurationException e) {
				throw e;
			} catch (java.io.IOException e) {
				throw e;
			} catch (java.lang.IllegalArgumentException e) {
				throw e;
			} catch (org.xml.sax.SAXException e) {
				throw e;
			}
			// System.out.println("1: "+(System.currentTimeMillis()-oldTime));
			oldTime = System.currentTimeMillis();
			org.w3c.dom.NodeList xmlDirectories = xmlRoot.getElementsByTagName("directory");
			org.w3c.dom.NodeList xmlModels = xmlRoot.getElementsByTagName("model");
			// System.out.println("getting all models and dirs: "+(System.currentTimeMillis()-oldTime));
			oldTime = System.currentTimeMillis();
			int total = xmlDirectories.getLength() + xmlModels.getLength();
			glueConstraints.gridx = total;
			int count = 0;
			for (int i = 0; i < xmlDirectories.getLength(); i++) {
				if (stopBuildingGallery) {
					xmlData = null;
					return -1;
				}
				count++;
				org.w3c.dom.Node currentDirectory = xmlDirectories.item(i);
				ObjectXmlData currentDirData = createObjectXmlData(currentDirectory, rootNode.rootPath, rootNode.type);
				if (currentDirData == null) {
					continue;
				}
				currentDirData.parentDirectory = this;
				DirectoryStructure currentDir = new DirectoryStructure(rootNode, currentDirData.name, currentDirData.objectFilename);
				if (firstLocalDirectory != null) {
					currentDir.firstLocalDirectory = firstLocalDirectory.getDirectoryNamed(currentDirData.name);
					if (currentDir.equals(currentDir.firstLocalDirectory)) {
						// currentDir.directoryToUse =
						// currentDir.firstLocalDirectory; // For using local
						// gallery as web gallery
					}
				}
				if (secondLocalDirectory != null) {
					currentDir.secondLocalDirectory = secondLocalDirectory.getDirectoryNamed(currentDirData.name);
					if (currentDir.equals(currentDir.secondLocalDirectory) && currentDir.directoryToUse == currentDir) {
						// currentDir.directoryToUse =
						// currentDir.secondLocalDirectory; // For using local
						// gallery as web gallery
					}
				}
				currentDir.data = currentDirData;
				currentDirData.directoryData = currentDir;
				currentDir.parent = this;
				if (currentDir != null && currentDirData != null) {
					xmlData.addDirectory(currentDir.directoryToUse.data);
					if (updatePanelsWhileLoading) {
						directoryAdded(currentDir.directoryToUse.data, count);
						updateLoading((float) count / total);
					}
				}
			}
			// System.out.println("dir loading: "+(System.currentTimeMillis()-oldTime));
			oldTime = System.currentTimeMillis();
			for (int i = 0; i < xmlModels.getLength(); i++) {
				if (stopBuildingGallery) {
					xmlData = null;
					return -1;
				}
				count++;
				org.w3c.dom.Node currentModel = xmlModels.item(i);
				ObjectXmlData currentModelData = createObjectXmlData(currentModel, rootNode.rootPath, rootNode.type);
				if (currentModelData != null) {
					currentModelData.parentDirectory = this;
					xmlData.addModel(currentModelData);
					if (updatePanelsWhileLoading) {
						modelAdded(currentModelData, count);
						updateLoading((float) count / total);
					}
				}
			}
			// System.out.println("model loading: "+(System.currentTimeMillis()-oldTime));
			oldTime = System.currentTimeMillis();
			return count;
		}
	}

	protected class RootDirectoryStructure {
		public String rootPath;
		public int type;
		public DirectoryStructure directory;
		public ObjectXmlData xmlData;

		public RootDirectoryStructure(String rootPath, int type, DirectoryStructure directory, ObjectXmlData xmlData) {
			this.xmlData = xmlData;
			this.rootPath = rootPath;
			this.type = type;
			this.directory = directory;
		}

		public ObjectXmlData getObject(String toGet) {
			if (toGet.indexOf(rootPath) != -1) {}
			return null;
		}

	}

	public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		characterFilter.setFilter(this.authoringTool.getCharacterFileFilter());
		add3DTextButton.set(add3DTextData, add3DTextIcon, authoringTool);
	}

	public boolean shouldShowWebWarning() {
		return authoringToolConfig.getValue("showWebWarningDialog").equalsIgnoreCase("true");
	}

	public GalleryViewer() {
		java.net.URL mainWebGalleryURL = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainWebGalleryURL();
		if (mainWebGalleryURL != null) {
			webGalleryHostName = mainWebGalleryURL.getHost();
			startSearchWebString = "Search " + webGalleryHostName;
		}
		java.io.File mainLocalGalleryFile = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainDiskGalleryDirectory();
		java.io.File mainCDGalleryFile = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainCDGalleryDirectory();

		cacheDir = edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory().getAbsolutePath() + java.io.File.separator + "webGalleryCache" + java.io.File.separator; // TODO:
																																													// set
																																													// from
																																													// pref
		java.io.File testDir = new java.io.File(cacheDir);
		if (!testDir.exists()) {
			testDir.mkdirs();
		}

		String filename = "galleryIndex.xml";
		rootDirectories = new java.util.Vector();
		inBrowseMode = true;
		guiInit();
		localGallery = createDirectory(mainLocalGalleryFile, localGalleryName, LOCAL);
		if (localGallery != null) {
			rootDirectories.add(localGallery);
			localGalleryRoot = localGallery.rootPath;
		}
		cdGallery = createDirectory(mainCDGalleryFile, cdGalleryName, CD);
		if (cdGallery != null) {
			rootDirectories.add(cdGallery);
			cdGalleryRoot = cdGallery.rootPath;
		}
		webGallery = createDirectory(mainWebGalleryURL, webGalleryName, FILENAME2, false);
		if (webGallery != null) {
			rootDirectories.add(webGallery);
			webGalleryRoot = webGallery.rootPath;
		} else {
			webGallery = createDirectory(mainWebGalleryURL, webGalleryName, FILENAME, false);
			if (webGallery != null) {
				rootDirectories.add(webGallery);
				webGalleryRoot = webGallery.rootPath;
			}
		}
		if (localGallery != null) {
			webGallery.directory.firstLocalDirectory = localGallery.directory;
		}
		if (cdGallery != null) {
			webGallery.directory.secondLocalDirectory = cdGallery.directory;
		}
		if (inBrowseMode) {
			if (localGallery != null) {
				directoryOnDisplay = localGallery.directory;
			} else {
				directoryOnDisplay = null;
			}
		} else {
			directoryOnDisplay = searchResults;
		}

		searchResults = new DirectoryStructure(null, "Search", null);
		searchResults.xmlData = new DirectoryXmlData("Search");

		refreshGUI();
	}

	private GalleryObject getGalleryObject(java.awt.Component c) {
		if (c instanceof GalleryObject) {
			return (GalleryObject) c;
		}
		if (c == null) {
			return null;
		}
		return getGalleryObject(c.getParent());
	}

	private String getRelativeDirectory(String root, String filename, String separator) {
		int split = filename.indexOf(root);
		String toReturn = null;
		if (split >= 0) {
			toReturn = filename.substring(split + root.length());
			split = toReturn.lastIndexOf(separator);
			if (split >= 0 && split < toReturn.length() - 1) {
				toReturn = toReturn.substring(0, split) + separator;
			} else {
				toReturn = "";
			}
		}
		return toReturn;
	}

	private String removeRootFromDirectory(String root, String filename) {
		int split = filename.indexOf(root);
		String toReturn = null;
		if (split >= 0) {
			toReturn = filename.substring(split + root.length());
		}
		return toReturn;
	}

	private String getFilename(String filename, String separator) {
		int split = filename.lastIndexOf(separator);
		String toReturn = null;
		if (split >= 0) {
			toReturn = filename.substring(split, filename.length());
		}
		return toReturn;
	}

	public void saveModel(edu.cmu.cs.stage3.alice.core.Element toSave, java.awt.datatransfer.Transferable transferable) {
		/*
		 * if (toSave == null){ return; }
		 */
		ObjectXmlData objectToAdd = null;
		java.awt.Image image = null;
		for (int i = 0; i < objectPanel.getComponentCount(); i++) {
			if (objectPanel.getComponent(i) instanceof GalleryObject) {
				GalleryObject currentObject = (GalleryObject) objectPanel.getComponent(i);
				if (currentObject.data.transferable == transferable) {
					objectToAdd = currentObject.data;
					image = currentObject.image.getImage();
					if (objectToAdd.type != WEB) {
						return;
					}
					break;
				}
			}
		}
		String path = null;
		if (objectToAdd != null) {
			path = objectToAdd.objectFilename;
			int split = path.lastIndexOf('/');
			path = path.substring(0, split);
		} else {
			try {
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
					java.net.URL url = (java.net.URL) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor);
					if (url != null) {
						path = getRelativeDirectory(webGalleryRoot, url.toString(), "/");
						path = webGalleryName + java.io.File.separator + reverseWebReady(path);
					}
				}
			} catch (Exception e) {
				return;
			}
			if (path != null) {
				DirectoryStructure dirOwner = getDirectoryStructure(path);
				if (dirOwner != null) {
					objectToAdd = dirOwner.getModel(transferable);
					if (objectToAdd != null) {
						if (objectToAdd.type == WEB) {
							image = WebGalleryObject.retrieveImage(webGalleryRoot, objectToAdd.imageFilename, objectToAdd.timeStamp);
						} else {
							return;
						}
					}
				}
			}
		}
		if (objectToAdd != null) {
			String localFilename = reverseWebReady(objectToAdd.objectFilename);
			String baseFilename = localFilename.substring(0, localFilename.length() - 3);
			String xmlFilename = objectToAdd.objectFilename.substring(0, objectToAdd.objectFilename.length() - 3) + "xml";
			String pngFilename = baseFilename + "png";
			String a2cFilename = baseFilename + "a2c";
			GalleryObject.storeThumbnail(localGalleryRoot + pngFilename, image, objectToAdd.timeStamp);
			getXML(objectToAdd.parentDirectory.rootNode.rootPath, xmlFilename, objectToAdd.type, -1, localGalleryRoot, true);
			java.io.File objectFile = createFile(localGalleryRoot + a2cFilename);
			if (objectFile != null) {
				try {
					toSave.store(objectFile);
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	protected void addObject(edu.cmu.cs.stage3.alice.core.Model toAdd) {
		if (authoringTool != null) {
			authoringTool.getUndoRedoStack().startCompound();
			authoringTool.addCharacter(toAdd, null);
			authoringTool.getUndoRedoStack().stopCompound();
		}
	}

	protected void addObject(java.io.File toAdd) {
		if (authoringTool != null) {
			authoringTool.loadAndAddCharacter(toAdd);
		}
	}

	protected void addObject(java.net.URL toAdd) {
		if (authoringTool != null) {
			authoringTool.loadAndAddCharacter(toAdd);
		}
	}

	private DirectoryStructure getRootDirectoryNamed(String dirName) {
		for (int i = 0; i < rootDirectories.size(); i++) {
			RootDirectoryStructure current = (RootDirectoryStructure) rootDirectories.get(i);
			if (current.directory.name.equalsIgnoreCase(dirName)) {
				return current.directory;
			}
		}
		return null;
	}

	public String getDirectory() {
		if (directoryOnDisplay == null) {
			return homeName;
		} else {
			return directoryOnDisplay.getGUIPath();
		}
	}

	protected void displayModelDialog(ObjectXmlData data, javax.swing.ImageIcon image) {
		if (modelContentPane == null) {
			modelContentPane = new ModelInfoContentPane();
		}
		modelContentPane.set(data, image);
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(modelContentPane);
		if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			if (data.type != 2) {
				java.io.File file = new java.io.File(modelContentPane.getFilename());
				addObject(file);
			} else {
				while (true) {
					try {
						java.net.URL url = new java.net.URL(modelContentPane.getFilename());
						addObject(url);
						break;
					} catch (Exception exception) {
						Object[] options = {"Retry", "Cancel"};
						int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog("Alice can't reach the web gallery. Your computer may not be connected to the internet properly.", "Internet Connection Error", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, options, options[1]);
						if (returnVal != javax.swing.JOptionPane.YES_OPTION) {
							continue;
						} else {
							break;
						}
					}
				}
			}
		}

	}

	protected DirectoryStructure getDirectoryStructure(String pathToSet) {
		if (pathToSet == null) {
			return null;
		}
		java.util.StringTokenizer token = new java.util.StringTokenizer(pathToSet, java.io.File.separator);
		boolean isFirst = true;
		DirectoryStructure currentDirToSet = null;
		while (token.hasMoreTokens()) {
			String current = token.nextToken();
			if (current != null && !current.equalsIgnoreCase("") && !current.equalsIgnoreCase(" ")) {
				if (isFirst) {
					isFirst = false;
					if (current.equalsIgnoreCase(homeName)) {
						currentDirToSet = null;
						isFirst = true;
					} else if (current.equalsIgnoreCase(webGalleryName)) {
						currentDirToSet = getRootDirectoryNamed(webGalleryName);
					} else if (current.equalsIgnoreCase(localGalleryName)) {
						currentDirToSet = getRootDirectoryNamed(localGalleryName);
					} else if (current.equalsIgnoreCase(cdGalleryName)) {
						currentDirToSet = getRootDirectoryNamed(cdGalleryName);
					} else {
						return null;
					}
				} else {
					if (currentDirToSet != null) {
						currentDirToSet = currentDirToSet.getDirectoryNamed(current);
					} else {
						return null;
					}
				}
			}
		}
		return currentDirToSet;
	}

	public void setDirectory(String pathToSet) {
		DirectoryStructure d = null;
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			d = getDirectoryStructure(pathToSet);
		} else {
			pathToSet = pathToSet.replace("\\", "/");
			d = getDirectoryStructure(pathToSet);
		}
		if (d == null) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error changing gallery viewer to " + pathToSet, null);
			return;
		}
		changeDirectory(d);
	}

	private RootDirectoryStructure createDirectory(java.net.URL root, String name, String rootFilename, boolean initXML) {
		String path = getRootString(root);
		if (path == null) {
			return null;
		}
		rootFilename = makeWebRelativePathReady(rootFilename);
		ObjectXmlData data = new ObjectXmlData();
		data.name = name;
		data.type = WEB;
		data.objectFilename = rootFilename;
		data.size = -1;
		data.mainViewer = this;
		data.transferable = new java.awt.datatransfer.StringSelection(data.name);
		RootDirectoryStructure toReturn = new RootDirectoryStructure(path, WEB, null, data);
		DirectoryStructure dirStruct = new DirectoryStructure(toReturn, name, rootFilename);
		if (initXML) {
			try {
				String xml = getXML(path, rootFilename, WEB, getURLTimeStamp(path + rootFilename), cacheDir, false);
				if (xml == null) {
					return null;
				}
				dirStruct.initSelf(xml);
			} catch (Exception e) {
				return null;
			}
		}
		data.directoryData = dirStruct;
		toReturn.directory = dirStruct;
		return toReturn;
	}

	private RootDirectoryStructure createDirectory(java.io.File root, String name, int type) {
		if (root == null || !root.exists() || !root.isDirectory() || !root.canRead()) {
			return null;
		}
		String path = getRootString(root);
		ObjectXmlData data = new ObjectXmlData();
		data.name = name;
		data.objectFilename = "";
		data.size = -1;
		data.type = type;
		data.mainViewer = this;
		data.transferable = new java.awt.datatransfer.StringSelection(data.name);
		RootDirectoryStructure toReturn = new RootDirectoryStructure(path, type, null, data);
		DirectoryStructure dirStruct = null;
		try {
			dirStruct = new DirectoryStructure(toReturn, name, "");
			dirStruct.initSelf(root);
		} catch (Exception e) {
			return null;
		}
		data.directoryData = dirStruct;
		toReturn.directory = dirStruct;
		return toReturn;
	}

	private String getRootString(java.io.File file) {
		if (file == null) {
			return null;
		}
		String toReturn = file.getAbsolutePath();
		if (!toReturn.endsWith(java.io.File.separator)) {
			toReturn += java.io.File.separator;
		}
		return toReturn;
	}

	private String getRootString(java.net.URL url) {
		if (url == null) {
			return null;
		}
		String toReturn = url.toString();
		if (!toReturn.endsWith("/")) {
			toReturn += "/";
		}
		return toReturn;
	}

	protected static synchronized void setDownloadRate(long time, int bytes) {
		double newRate = bytes / (time * .001);
		if (bitsPerSecond != 0) {
			bitsPerSecond = (bitsPerSecond * .8) + newRate * .2;
		} else {
			bitsPerSecond = newRate;
		}
	}

	public static java.io.File createFile(String filename) {
		int nameSplit = filename.lastIndexOf(java.io.File.separator);
		if (java.io.File.separator.equals("\\")) {
			int split2 = filename.lastIndexOf("/");
			if (split2 > nameSplit) {
				nameSplit = split2;
			}
		} else {
			int split2 = filename.lastIndexOf("\\");
			if (split2 > nameSplit) {
				nameSplit = split2;
			}
		}
		String parentDir = filename.substring(0, nameSplit + 1);
		java.io.File cacheFile = new java.io.File(filename);
		if (!cacheFile.exists()) {
			try {
				java.io.File parentDirFile = new java.io.File(parentDir);
				if (!parentDirFile.exists()) {
					parentDirFile.mkdirs();
				}
				cacheFile.createNewFile();
			} catch (Exception e) {
				return null;
			}
		}
		return cacheFile;
	}

	protected long getURLTimeStamp(String urlString) {
		long toReturn = 0;
		try {
			java.net.URL url = new java.net.URL(urlString);
			toReturn = url.openConnection().getLastModified();
		} catch (Exception e) {}
		return toReturn;
	}

	private String getXML(String root, final String relativeFile, int type, final long sourceTimeStamp, String cacheDirectory, boolean forceCache) {
		String xmlTemp = null;
		String cacheFilenameTemp = null;
		boolean needToCache = true;
		if (type == LOCAL || type == CD) {
			java.io.File file = new java.io.File(root + relativeFile);
			if (file.exists() && file.canRead()) {
				xmlTemp = getXML(file);
				cacheFilenameTemp = cacheDirectory + relativeFile;
				needToCache = false;
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error accessing the local gallery: " + file.getAbsolutePath() + " is either not there or can not be read", null);
				return null;
			}
		} else {
			cacheFilenameTemp = cacheDirectory + reverseWebReady(relativeFile);
			java.io.File cachedCopy = new java.io.File(cacheFilenameTemp);
			long cacheTimeStamp = -2;
			if (cachedCopy.exists() && cachedCopy.canRead()) { // Check for
																// cached copy
				cacheTimeStamp = cachedCopy.lastModified();
				if (cacheTimeStamp == sourceTimeStamp) {
					xmlTemp = getXML(cachedCopy);
					needToCache = false;
				}
			}
			// download xml and save it to the cache

			java.net.URL url = null;
			while (true) {
				try {
					url = new java.net.URL(root + relativeFile);
					xmlTemp = getXML(url);
				} catch (Exception e) {
					xmlTemp = null;
				}
				if (xmlTemp == null) {
					Object[] options = {"Retry", "Cancel"};
					int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog("Alice can't reach the web gallery. Your computer may not be connected to the internet properly.", "Internet Connection Error", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					if (returnVal != javax.swing.JOptionPane.YES_OPTION) {
						return null;
					}
				} else {
					break;
				}
			}
		}
		final String xml = xmlTemp;
		final String cacheFilename = cacheFilenameTemp;
		if (needToCache || forceCache) {
			Runnable doStore = new Runnable() {
				@Override
				public void run() {
					java.io.File cacheFile = createFile(cacheFilename);
					if (cacheFile != null) {
						try {
							java.io.FileOutputStream fos = new java.io.FileOutputStream(cacheFile);
							java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos);
							java.io.BufferedWriter bw = new java.io.BufferedWriter(osw);
							bw.write(xml);
							bw.flush();
							osw.close();
							cacheFile.setLastModified(sourceTimeStamp);
						} catch (Exception e) {}
					}
				}
			};
			Thread t = new Thread(doStore);
			t.start();
		}
		return xml;
	}

	private String getXML(java.util.zip.ZipFile zipFile) {
		String content = "";
		try {
			java.util.zip.ZipEntry entry = zipFile.getEntry("galleryData.xml");
			if (entry != null) {
				java.io.InputStream stream = zipFile.getInputStream(entry);
				java.io.BufferedReader fileReader = new java.io.BufferedReader(new java.io.InputStreamReader(stream));
				char b[] = new char[1000];
				int numRead = fileReader.read(b);
				content = new String(b, 0, numRead);
				while (numRead != -1) {
					numRead = fileReader.read(b);
					if (numRead != -1) {
						String newContent = new String(b, 0, numRead);
						content += newContent;
					}
				}
			}
			zipFile.close();
		} catch (Exception e) {
			return null;
		}
		return content;
	}

	private String getXML(java.io.File file) {
		String content = "";
		try {
			java.io.FileReader fileReader = new java.io.FileReader(file);
			char b[] = new char[1000];
			int numRead = fileReader.read(b);
			content = new String(b, 0, numRead);
			while (numRead != -1) {
				numRead = fileReader.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}
			fileReader.close();
		} catch (java.io.IOException e) {
			return null;
		}
		return content;
	}

	private String getXML(java.net.URL url) {
		String content = "";
		try {
			java.io.InputStream urlStream = url.openStream();
			java.io.BufferedInputStream bufis = new java.io.BufferedInputStream(urlStream);
			byte b[] = new byte[1000];
			int numRead = bufis.read(b);
			content = new String(b, 0, numRead);
			while (numRead != -1) {
				numRead = bufis.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}
			urlStream.close();
		} catch (java.net.MalformedURLException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return content;
	}

	private java.awt.datatransfer.Transferable createURLTransferable(String filename) {
		java.net.URL toReturn = null;
		try {
			java.net.URL url = new java.net.URL(filename);
			if (!url.getProtocol().equalsIgnoreCase("http")) {
				return null;
			}
			java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
			int response = connection.getResponseCode();
			if (response != java.net.HttpURLConnection.HTTP_OK) {
				return null;
			}
			connection.disconnect();
			return new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable(url);

		} catch (java.net.MalformedURLException e) {
			return null;
		} catch (java.io.IOException e) {
			return null;
		}
	}

	private java.awt.datatransfer.Transferable createFileTransferable(String filename) {
		java.io.File fileToTransfer = new java.io.File(filename);
		if (fileToTransfer.exists() && fileToTransfer.canRead()) {
			java.util.ArrayList list = new java.util.ArrayList(1);
			list.add(fileToTransfer);
			return new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.FileListTransferable(list);
		} else {
			return null;
		}
	}

	protected static void enteredWebGallery() {
		alreadyEnteredWebGallery = true;
	}

	public void switchMode() {
		if (inBrowseMode) {
			searchBrowseButton.setText(searchString);
			headerPanel.remove(directoryPanel);
			headerPanel.add(searchPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			oldDirectoryOnDisplay = directoryOnDisplay;
			directoryOnDisplay = searchResults;
		} else {
			searchBrowseButton.setText(browseString);
			headerPanel.remove(searchPanel);
			stopSearch = true;
			isInWebGallery = oldIsInWebGalleryValue;
			headerPanel.add(directoryPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			directoryOnDisplay = oldDirectoryOnDisplay;
		}
		inBrowseMode = !inBrowseMode;
		refreshGUI();
	}

	private void guiInit() {
		setBackground(backgroundColor);
		objectPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
		objectPanel.setBorder(null);
		objectPanel.setBackground(backgroundColor);
		objectPanelLayout = new java.awt.FlowLayout();
		objectPanelLayout.setAlignment(java.awt.FlowLayout.LEFT);
		objectPanelLayout.setVgap(0);
		objectPanelLayout.setHgap(1);
		objectPanel.setLayout(objectPanelLayout);

		directoryPanel = new javax.swing.JPanel();
		directoryPanel.setLayout(new java.awt.GridBagLayout());
		directoryPanel.setOpaque(false);
		// directoryPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(1));

		attributeLabel = new javax.swing.JLabel(" ");
		attributeLabel.setForeground(textColor);

		attributePanel = new javax.swing.JPanel();
		attributePanel.setOpaque(false);
		attributePanel.setLayout(new java.awt.GridBagLayout());
		// attributePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(1));
		attributePanel.add(attributeLabel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));

		searchBrowseButton = new javax.swing.JButton(browseString);
		searchBrowseButton.setBackground(new java.awt.Color(240, 240, 255));
		searchBrowseButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		searchBrowseButton.setMinimumSize(new java.awt.Dimension(100, 26));

		if (!inBrowseMode) {
			searchBrowseButton.setText(searchString);
		}
		searchBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				switchMode();
			}
		});

		searchField = new javax.swing.JTextField(15);
		searchField.setMinimumSize(new java.awt.Dimension(200, 26));
		searchField.setPreferredSize(new java.awt.Dimension(200, 26));
		searchField.setMaximumSize(new java.awt.Dimension(200, 26));

		searchField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {

				searchGallery(null, searchField.getText(), false);
			}
		});

		searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent ev) {
				searchWebButton.setEnabled(false);
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent ev) {
				searchWebButton.setEnabled(false);
			}
			private void updateHeightTextField() {
				searchWebButton.setEnabled(false);
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent ev) {
				searchWebButton.setEnabled(false);
			}
		});

		searchButton = new javax.swing.JButton(startSearchString);
		searchButton.setBackground(new java.awt.Color(240, 240, 255));
		searchButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		// searchButton.setMinimumSize(new java.awt.Dimension(60, 26));
		// searchButton.setPreferredSize(new java.awt.Dimension(60, 26));
		// searchButton.setMaximumSize(new java.awt.Dimension(60, 26));
		searchButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (!stopSearch) {
					stopSearch = true;
					isInWebGallery = oldIsInWebGalleryValue;
					searchButton.setText(startSearchString);
				} else {
					searchGallery(null, searchField.getText(), false);
				}
			}
		});
		searchWebButton = new javax.swing.JButton(startSearchWebString);
		searchWebButton.setBackground(new java.awt.Color(240, 240, 255));
		searchWebButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		// searchWebButton.setMinimumSize(new java.awt.Dimension(140, 26));
		// searchWebButton.setPreferredSize(new java.awt.Dimension(140, 26));
		// searchWebButton.setMaximumSize(new java.awt.Dimension(140, 26));
		searchWebButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (!stopSearch) {
					stopSearch = true;
					isInWebGallery = oldIsInWebGalleryValue;
					searchWebButton.setText(startSearchWebString);
				} else {
					if (!alreadyEnteredWebGallery && shouldShowWebWarning()) {
						int dialogVal = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog("You are about to search the online gallery. This is accessed through the internet\n" + " and is potentially slow depending on your connection.", "Web gallery may be slow", javax.swing.JOptionPane.WARNING_MESSAGE);
						if (dialogVal == javax.swing.JOptionPane.YES_OPTION) {
							enteredWebGallery();
							searchGallery(webGallery.directory, searchField.getText(), true);
						}
					} else {
						searchGallery(webGallery.directory, searchField.getText(), true);
					}
				}
			}
		});
		searchWebButton.setEnabled(false);
		searchPanel = new javax.swing.JPanel();
		searchPanel.setOpaque(false);
		searchPanel.setBorder(null);
		searchPanel.setLayout(new java.awt.GridBagLayout());
		searchPanel.add(searchField, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 4, 0, 8), 0, 0));
		searchPanel.add(searchButton, new java.awt.GridBagConstraints(1, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 0, 0, 0), 0, 0));
		searchPanel.add(searchWebButton, new java.awt.GridBagConstraints(2, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 4, 0, 0), 0, 0));
		searchPanel.add(searchingProgressLabel, new java.awt.GridBagConstraints(3, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 4, 0, 0), 0, 0));
		searchPanel.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(4, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));

		headerPanel = new javax.swing.JPanel();
		headerPanel.setMinimumSize(new java.awt.Dimension(1, 34));
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new java.awt.GridBagLayout());
		// headerPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(1));
		if (inBrowseMode) {
			headerPanel.add(directoryPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		} else {
			headerPanel.add(searchPanel, new java.awt.GridBagConstraints(0, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		}
		headerPanel.add(attributeLabel, new java.awt.GridBagConstraints(0, 1, 2, 1, 0, 0, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 30, 0, 0), 0, 0));
		headerPanel.add(searchBrowseButton, new java.awt.GridBagConstraints(2, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.NORTHEAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(2, 0, 0, 4), 0, 0));
		headerPanel.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(1, 1, 2, 1, 1, 1, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		headerPanel.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(1, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		// headerPanel.add(javax.swing.Box.createHorizontalGlue(), new
		// java.awt.GridBagConstraints(1,1,1,1,1,1,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.HORIZONTAL,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		// headerPanel.add(javax.swing.Box.createVerticalGlue(), new
		// java.awt.GridBagConstraints(0,2,1,1,1,1,java.awt.GridBagConstraints.SOUTH,java.awt.GridBagConstraints.VERTICAL,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		// headerPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE,
		// 40));
		webGalleryIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/webGalleryIcon.png"));
		loadingImageIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/loadingImageIcon.png"));
		noImageIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/noImageIcon.png"));
		noFolderImageIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/defaultFolderIcon.png"));
		localGalleryIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/localGalleryIcon.png"));
		cdGalleryIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/cdGalleryIcon.png"));
		add3DTextIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/3DText.png"));
		javax.swing.ImageIcon upLevelIcon = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/upLevelIcon.png"));
		javax.swing.ImageIcon upLevelIconPressed = new javax.swing.ImageIcon(GalleryViewer.class.getResource("images/upLevelIconPressed.png"));
		// javax.swing.ImageIcon upLevelIconDisabled = new
		// javax.swing.ImageIcon( GalleryViewer.class.getResource(
		// "images/upLevelIconDisabled.png" ) );
		javax.swing.ImageIcon upLevelIconDisabled = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDisabledIcon(upLevelIcon, 45);

		upLevelButton = new javax.swing.JButton(upLevelIcon);
		upLevelButton.setToolTipText("Move Up a Level");
		upLevelButton.setOpaque(false);
		upLevelButton.setDisabledIcon(upLevelIconDisabled);
		upLevelButton.setPressedIcon(upLevelIconPressed);
		upLevelButton.setSize(upLevelIcon.getIconWidth() + 2, upLevelIcon.getIconHeight() + 2);
		upLevelButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		upLevelButton.setBorder(null);
		upLevelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		upLevelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				goUpOneLevel();
			}
		});

		add3DTextData = new ObjectXmlData();
		add3DTextData.name = "Create 3D Text";
		add3DTextData.mainViewer = this;
		add3DTextData.transferable = new java.awt.datatransfer.StringSelection(add3DTextData.name);

		add3DTextButton = new TextBuilderButton();
		add3DTextButton.set(add3DTextData, add3DTextIcon, null);

		java.util.Vector builderVector = edu.cmu.cs.stage3.caitlin.personbuilder.PersonBuilder.getAllBuilders();
		builderButtonsVector = new java.util.Vector();
		// builderButtonsVector.add(add3DTextButton);
		for (int i = 0; i < builderVector.size(); i++) {
			if (builderVector.get(i) instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair) builderVector.get(i);
				javax.swing.ImageIcon builderIcon = null;
				if (sop.getObject() instanceof javax.swing.ImageIcon) {
					builderIcon = (javax.swing.ImageIcon) sop.getObject();
				} else {
					continue;
				}
				ObjectXmlData personBuilderData = new ObjectXmlData();
				personBuilderData.name = sop.getString();
				personBuilderData.mainViewer = this;
				personBuilderData.transferable = new java.awt.datatransfer.StringSelection(personBuilderData.name);
				PersonBuilderButton currentButton = new PersonBuilderButton();
				currentButton.set(personBuilderData, builderIcon);
				builderButtonsVector.add(currentButton);
			}
		}

		noObjectsLabel = new javax.swing.JLabel();
		noObjectsLabel.setFont(new java.awt.Font("Dialog", 0, 24));
		noObjectsLabel.setForeground(java.awt.Color.white);
		noObjectsLabel.setText("No folders or Alice characters found in this directory.");

		noSearchResults = new javax.swing.JLabel();
		noSearchResults.setFont(new java.awt.Font("Dialog", 0, 24));
		noSearchResults.setForeground(java.awt.Color.white);
		noSearchResults.setText("No models were found.");

		searching = new javax.swing.JLabel();
		searching.setFont(new java.awt.Font("Dialog", 0, 24));
		searching.setForeground(java.awt.Color.white);
		searching.setText(noModelsYet);

		setLayout(new java.awt.BorderLayout());
		this.add(headerPanel, java.awt.BorderLayout.NORTH);
		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(objectPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane, java.awt.BorderLayout.CENTER);

		// Aik Min added this to make scroll bar scroll more
		scrollPane.getHorizontalScrollBar().setUnitIncrement(44);

		// this.add(javax.swing.Box.createHorizontalGlue(), new
		// java.awt.GridBagConstraints(1,0,1,1,1,1,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.HORIZONTAL,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		// this.add(javax.swing.Box.createHorizontalGlue(), new
		// java.awt.GridBagConstraints(1,1,1,1,1,1,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.HORIZONTAL,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		// this.add(javax.swing.Box.createVerticalGlue(), new
		// java.awt.GridBagConstraints(0,2,1,1,1,1,java.awt.GridBagConstraints.SOUTH,java.awt.GridBagConstraints.BOTH,
		// new java.awt.Insets(0,0,0,0), 0,0 ));
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		setPreferredSize(new java.awt.Dimension(Integer.MAX_VALUE, 250 + (fontSize - 12) * 6)); // Aik
																								// Min
		setMinimumSize(new java.awt.Dimension(100, 250));
	}

	private String getNodeText(org.w3c.dom.Node node) {
		String toReturn = "";
		org.w3c.dom.NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
				toReturn += children.item(i).getNodeValue();
			}
		}
		return toReturn;
	}

	private Object getDetailedNodeText(org.w3c.dom.Node node) {
		Object toReturn = null;
		org.w3c.dom.NodeList children = node.getChildNodes();
		if (children.getLength() == 1 && children.item(0).getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
			toReturn = children.item(0).getNodeValue();
		} else if (children.getLength() > 0) {
			java.util.Vector detailVector = new java.util.Vector();
			for (int i = 0; i < children.getLength(); i++) {
				org.w3c.dom.Node currentNode = children.item(i);
				if (currentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					if (currentNode.getFirstChild().getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
						detailVector.add(currentNode.getFirstChild().getNodeValue());
					}
				}
			}
			toReturn = detailVector;
		}
		return toReturn;
	}

	public static String reverseWebReady(String url) {
		String toReturn = "";
		int beginning = 0;
		int index = url.indexOf("%20");
		while (index != -1) {
			toReturn += url.substring(beginning, index);
			toReturn += " ";
			beginning = index + 3;
			index = url.indexOf("%20", beginning);
		}
		toReturn += url.substring(beginning, url.length());
		toReturn = toReturn.replace('/', java.io.File.separatorChar);
		return toReturn;
	}

	private String makeWebReady(String url) {
		String toReturn = "";
		int beginning = 0;
		for (int i = 0; i < url.length(); i++) {
			if (url.charAt(i) == ' ') {
				toReturn += url.substring(beginning, i) + "%20";
				beginning = i + 1;
			}
		}
		toReturn += url.substring(beginning, url.length());
		toReturn = toReturn.replace('\\', '/');
		return toReturn;
	}

	private String makeWebRelativePathReady(String url) {
		String toReturn = makeWebReady(url);
		if (toReturn.charAt(0) == '/') {
			toReturn = toReturn.substring(1);
		}
		return toReturn;
	}

	private String makeRelativePathReady(String relativeFilename) {
		String pathname = new String(relativeFilename);
		if (java.io.File.separatorChar == '\\') {
			pathname = pathname.replace('/', java.io.File.separatorChar);
		} else if (java.io.File.separatorChar == '/') {
			pathname = pathname.replace('\\', java.io.File.separatorChar);
		}
		if (pathname.charAt(0) == java.io.File.separatorChar) {
			pathname = pathname.substring(1);
		}
		return pathname;
	}

	private GalleryObject createGalleryObject(ObjectXmlData currentObject) {
		GalleryViewer.ObjectXmlData localMatch = null;
		if (currentObject.parentDirectory.firstLocalDirectory != null && currentObject.parentDirectory.firstLocalDirectory.xmlData != null) {
			localMatch = currentObject.parentDirectory.firstLocalDirectory.xmlData.getModel(currentObject);
		}
		if (localMatch == null && currentObject.parentDirectory.secondLocalDirectory != null && currentObject.parentDirectory.secondLocalDirectory.xmlData != null) {
			localMatch = currentObject.parentDirectory.secondLocalDirectory.xmlData.getModel(currentObject);
		}
		if (localMatch != null) {
			currentObject = localMatch;
		}
		GalleryObject toReturn = (GalleryObject) edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentObject);
		try {
			toReturn.set(currentObject);
		} catch (Exception e) {
			return null;
		}
		toReturn.loadImage();
		return toReturn;
	}

	private GalleryObject createGalleryDirectory(ObjectXmlData currentObject) {
		GalleryObject toReturn = (GalleryObject) edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentObject);
		try {
			toReturn.set(currentObject);
		} catch (Exception e) {
			return null;
		}
		/*
		 * if (toReturn instanceof WebGalleryDirectory){
		 * ((WebGalleryDirectory)toReturn
		 * ).setDirectoryData(directoryOnDisplay.getDirectoryNamed
		 * (currentObject.name)); } else if (toReturn instanceof
		 * LocalGalleryDirectory){
		 * ((LocalGalleryDirectory)toReturn).setDirectoryData
		 * (directoryOnDisplay.getDirectoryNamed(currentObject.name)); }
		 */
		toReturn.loadImage();
		return toReturn;
	}

	private ObjectXmlData createObjectXmlData(java.io.File dirFile, String root, int type) {
		if (dirFile == null || dirFile.isDirectory() || !dirFile.canRead()) {
			return null;
		}
		ObjectXmlData currentObject = new ObjectXmlData();
		currentObject.type = type;
		currentObject.mainViewer = this;

		currentObject.name = dirFile.getName();
		currentObject.timeStamp = dirFile.lastModified();
		currentObject.objectFilename = removeRootFromDirectory(root, dirFile.getAbsolutePath());
		currentObject.transferable = createFileTransferable(root + currentObject.objectFilename);
		currentObject.imageFilename = currentObject.objectFilename;
		currentObject.size = (int) (dirFile.length() / 1000);
		return currentObject;
	}

	private ObjectXmlData createDirectoryObjectXmlData(java.io.File dirFile, String root, int type) {
		if (dirFile == null || !dirFile.isDirectory() || !dirFile.canRead()) {
			return null;
		}

		ObjectXmlData currentObject = new ObjectXmlData();
		currentObject.type = type;
		currentObject.mainViewer = this;

		currentObject.name = dirFile.getName();
		currentObject.timeStamp = dirFile.lastModified();
		currentObject.objectFilename = removeRootFromDirectory(root, dirFile.getAbsolutePath());
		currentObject.transferable = createFileTransferable(root + currentObject.objectFilename);
		java.io.File[] thumbFiles = dirFile.listFiles(thumbnailFilter);
		if (thumbFiles != null && thumbFiles.length > 0) {
			currentObject.imageFilename = makeRelativePathReady(currentObject.objectFilename + java.io.File.separator + thumbFiles[0].getName());
		} else {
			currentObject.imageFilename = null;
		}
		currentObject.size = -1;
		return currentObject;
	}

	private ObjectXmlData createObjectXmlData(org.w3c.dom.Node currentModel, String root, int type) {
		ObjectXmlData currentObject = new ObjectXmlData();
		currentObject.type = type;
		currentObject.mainViewer = this;

		org.w3c.dom.NodeList nodeDetails = currentModel.getChildNodes();
		for (int j = 0; j < nodeDetails.getLength(); j++) {
			org.w3c.dom.Node currentDetail = nodeDetails.item(j);
			if (currentDetail != null) {
				if (currentDetail.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && !currentDetail.getNodeName().equals("#text")) {
					try {
						if (currentDetail.getNodeName().equalsIgnoreCase("name")) {
							currentObject.name = getNodeText(currentDetail);
						} else if (currentDetail.getNodeName().equalsIgnoreCase("timestamp")) {
							currentObject.timeStamp = Long.parseLong(getNodeText(currentDetail));
						} else if (currentDetail.getNodeName().equalsIgnoreCase("objectfilename")) {
							String rawFilename = getNodeText(currentDetail);
							if (type == WEB) {
								currentObject.objectFilename = makeWebRelativePathReady(rawFilename);
							} else {
								currentObject.objectFilename = makeRelativePathReady(rawFilename);
							}
							if (type == WEB) {
								currentObject.transferable = createURLTransferable(root + currentObject.objectFilename);
								if (currentObject.transferable == null) {
									return null;
								}
							} else if (type == LOCAL || type == CD) {
								currentObject.transferable = createFileTransferable(root + currentObject.objectFilename);
								if (currentObject.transferable == null) {
									return null;
								}
							}
						} else if (currentDetail.getNodeName().equalsIgnoreCase("imagefilename")) {
							String rawFilename = getNodeText(currentDetail);
							if (type == WEB) {
								currentObject.imageFilename = makeWebRelativePathReady(rawFilename);
							} else {
								currentObject.imageFilename = makeRelativePathReady(rawFilename);
							}
						} else if (currentDetail.getNodeName().equalsIgnoreCase("size")) {
							currentObject.setSize(getNodeText(currentDetail));
						} else if (currentDetail.getNodeName().equalsIgnoreCase("physicalsize")) {
							currentObject.setDimensions(getNodeText(currentDetail));
						} else {
							currentObject.addDetail(currentDetail.getNodeName(), getDetailedNodeText(currentDetail));
						}
					} catch (Exception e) {
						currentObject = null;
						break;
					}
				}
			}
		}
		return currentObject;
	}

	private void buildNewDirectory(final DirectoryStructure newDir, boolean setAsCurrent) {
		String xml;
		DirectoryStructure oldDir = directoryOnDisplay;
		boolean oldIsInWebGallery = isInWebGallery;
		if (setAsCurrent) {
			// System.out.println("ok, we're building the current dir");
			directoryOnDisplay = newDir;

			if (!stopBuildingGallery) {
				// System.out.println("we haven't been cancelled");
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						createDirectoryButtons();
						directoryPanel.revalidate();
						GalleryViewer.this.repaint();
					}
				});
			} else {
				directoryOnDisplay = oldDir;
				refreshGUI();
				return;
			}
		}
		if (newDir.rootNode.type == WEB) {
			isInWebGallery = true;
			boolean oldUpdateValue = updatePanelsWhileLoading;
			updatePanelsWhileLoading = false;
			if (newDir.firstLocalDirectory != null) {
				if (newDir.firstLocalDirectory.xmlData == null) {
					buildNewDirectory(newDir.firstLocalDirectory, false);
				}
			} else if (newDir.secondLocalDirectory != null) {
				if (newDir.secondLocalDirectory.xmlData == null) {
					buildNewDirectory(newDir.secondLocalDirectory, false);
				}
			}
			// System.out.println("done building local equivalents");
			updatePanelsWhileLoading = oldUpdateValue;
			long tempTimeStamp = -1;
			if (newDir.data != null) {
				// System.out.println("able to pull the timestamp from the data");
				if (newDir.data.timeStamp == -1) {
					newDir.data.timeStamp = getURLTimeStamp(newDir.rootNode.rootPath + newDir.path);
					tempTimeStamp = newDir.data.timeStamp;
					// System.out.println("ooops, new time stamp");
				}
			} else {
				// System.out.println("new time stamp");
				tempTimeStamp = getURLTimeStamp(newDir.rootNode.rootPath + newDir.path);
			}
			xml = getXML(newDir.rootNode.rootPath, newDir.path, WEB, tempTimeStamp, cacheDir, false);
			// System.out.println("just got the xml, wheh!");
			if (xml == null) {
				// System.out.println("it's null?!");
				if (setAsCurrent) {
					isInWebGallery = oldIsInWebGallery;
					directoryOnDisplay = oldDir;
					refreshGUI();
				}
				return;
			}
			// System.out.println("yay, not null!");
		} else {
			if (directoryOnDisplay.name.equals(localGalleryName) || directoryOnDisplay.name.equals(cdGalleryName)) {
				isInWebGallery = false;
			}
			xml = null;
		}
		int totalInside = -1;
		try {
			if (xml != null) {
				// System.out.println("wow, now we gotta go init the whole thing");
				totalInside = newDir.initSelf(xml);
				// System.out.println("yahtzee! inited it just fine!");
			} else if (newDir.rootNode.type != WEB) {
				totalInside = newDir.initSelf(new java.io.File(newDir.rootNode.rootPath + newDir.path));
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (setAsCurrent) {
				isInWebGallery = oldIsInWebGallery;
				directoryOnDisplay = oldDir;
				refreshGUI();
			}
			return;
		}
		if (setAsCurrent && totalInside < 0) {
			isInWebGallery = oldIsInWebGallery;
			directoryOnDisplay = oldDir;
			refreshGUI();
		}
		if (updatePanelsWhileLoading) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					removeAttribution();
				}
			});
		}
	}

	public int searchDirectory(DirectoryStructure toSearch, String toSearchFor) {
		if (toSearch != null && !stopSearch) {
			if (toSearch.xmlData == null) {
				updatePanelsWhileLoading = false;
				buildNewDirectory(toSearch, false);
			}
			java.util.Vector matches = toSearch.getObjectMatches(toSearchFor);
			if (matches != null) {
				for (int i = 0; i < matches.size(); i++) {
					if (!searchResults.contains((ObjectXmlData) matches.get(i))) {
						searchResults.xmlData.addModel((ObjectXmlData) matches.get(i));
						if (searchCount == 0) {
							objectPanel.removeAll();
						}
						modelAdded((ObjectXmlData) matches.get(i), searchCount);
						searchCount++;
					}
				}
			}
			int count = matches.size();
			for (int i = 0; i < toSearch.xmlData.directories.size(); i++) {
				count += searchDirectory(toSearch.xmlData.getDirectory(i), toSearchFor);
			}
			return count;
		}
		return 0;
	}

	public void searchGallery(final DirectoryStructure toSearch, final String toSearchFor, final boolean isWeb) {
		oldIsInWebGalleryValue = isInWebGallery;
		if (toSearchFor == null) {
			return;
		}
		lastSearchString = toSearchFor;
		if (!isWeb) {
			searchResults.xmlData.models.removeAllElements();
			searching.setText(noModelsYet);
			searchCount = 0;
			objectPanel.removeAll();
			// objectPanel.add(javax.swing.Box.createHorizontalGlue(), new
			// java.awt.GridBagConstraints(1,0,1,1,1,1,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,
			// new java.awt.Insets(0,0,0,0), 0,0 ));
			// objectPanel.add(searching, new
			// java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
			// new java.awt.Insets(0,4,0,0), 0,0 ));
			objectPanel.add(searching);
			objectPanel.revalidate();
			objectPanel.repaint();
		}
		stopSearch = false;
		searchingProgressLabel.reset();
		if (isWeb) {
			searchWebButton.setText(stopSearchString);
		} else {
			searchButton.setText(stopSearchString);
		}

		Thread searchThread = new Thread() {
			@Override
			public void run() {
				int total = 0;
				Thread t = new Thread() {
					@Override
					public void run() {
						while (!stopSearch) {
							javax.swing.SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									searchingProgressLabel.advance();
								}
							});
							try {
								Thread.sleep(250);
							} catch (Exception e) {}
						}
					}
				};
				t.start();

				if (toSearch == null) {
					for (int i = 0; i < rootDirectories.size(); i++) {
						RootDirectoryStructure currentRoot = (RootDirectoryStructure) rootDirectories.get(i);
						if (!isWeb && currentRoot != webGallery) {
							total += searchDirectory(currentRoot.directory, toSearchFor);
						}
					}
				} else {
					total = searchDirectory(toSearch, toSearchFor);
				}
				final int finalTotal = total;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (finalTotal <= 0 && searchCount <= 0) {
							objectPanel.removeAll();
							// objectPanel.add(javax.swing.Box.createHorizontalGlue(),
							// new
							// java.awt.GridBagConstraints(1,0,1,1,1,1,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,
							// new java.awt.Insets(0,0,0,0), 0,0 ));
							if (isWeb) {
								noSearchResults.setText("No models matching \"" + toSearchFor + "\" were found on " + webGalleryHostName);
							} else {
								noSearchResults.setText("No models matching \"" + toSearchFor + "\" were found on your machine.");
							}
							// objectPanel.add(noSearchResults, new
							// java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
							// new java.awt.Insets(0,4,0,0), 0,0 ));
							objectPanel.add(noSearchResults);
							objectPanel.revalidate();
							objectPanel.repaint();
						}
						if (isWeb) {
							searchWebButton.setText(startSearchWebString);
						} else {
							searchButton.setText(startSearchString);
							searchWebButton.setEnabled(true);
						}
						searchingProgressLabel.reset();
					}
				});
				stopSearch = true;
				isInWebGallery = oldIsInWebGalleryValue;

			}
		};
		searchThread.start();
	}

	protected void changeDirectory(final DirectoryStructure toChangeTo) {
		DirectoryStructure temp = null;
		if (toChangeTo != null) {
			temp = toChangeTo.directoryToUse;
			// System.out.println("switch to "+toChangeTo.name);
		} else {
			// System.out.println("switch to null");
		}
		final DirectoryStructure actualToChangeTo = temp;
		if (actualToChangeTo == directoryOnDisplay) {
			// System.out.println("already there");
			return;
		}
		Runnable toRun = null;
		if (actualToChangeTo != null && actualToChangeTo.xmlData == null) {
			toRun = new Runnable() {
				@Override
				public void run() {
					updatePanelsWhileLoading = true;
					// System.out.println("gotta build it");
					buildNewDirectory(actualToChangeTo, true);
					updatePanelsWhileLoading = false;
				}
			};
		} else {
			toRun = new Runnable() {
				@Override
				public void run() {
					directoryOnDisplay = actualToChangeTo;
					if (directoryOnDisplay == null) {
						isInWebGallery = false;
					} else {
						if (directoryOnDisplay.name.equals(localGalleryName) || directoryOnDisplay.name.equals(cdGalleryName)) {
							isInWebGallery = false;
						} else if (directoryOnDisplay.name.equals(webGalleryName)) {
							isInWebGallery = true;
						}
					}
					if (directoryOnDisplay != null && !isInWebGallery) {
						try {
							directoryOnDisplay.updateSelf(new java.io.File(directoryOnDisplay.rootNode.rootPath + directoryOnDisplay.path));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					refreshGUI();
				}
			};
		}
		if (changingThread != null && changingThread.isAlive()) {
			stopBuildingGallery = true;
			// System.out.println("trying to kill old build");
			while (changingThread.isAlive()) {
				try {
					// System.out.println("trying to kill old build");
					Thread.sleep(10);
				} catch (Exception e) {}
			}
			// System.out.println("it's dead");
			stopBuildingGallery = false;
		}
		if (isInWebGallery) {
			attributeLabel.setText("Loading from " + webGalleryHostName + "...");
		} else {
			attributeLabel.setText("Loading...");
		}
		objectPanel.removeAll();
		// objectPanel.add(javax.swing.Box.createHorizontalGlue(),
		// glueConstraints);
		// System.out.println("ok, let's do it");
		changingThread = new Thread(toRun);
		changingThread.start();
	}

	private void updateLoading(final float percentage) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (isInWebGallery) {
					attributeLabel.setText("Loading from " + webGalleryHostName + "..." + String.valueOf((int) (percentage * 100)) + "%");
				} else {
					attributeLabel.setText("Loading..." + String.valueOf((int) (percentage * 100)) + "%");
				};
			}
		});
		// attributeLabel.revalidate();
	}

	protected static String cleanUpName(String name) {
		String newName = new String(name);
		if (name.length() > 4 && name.charAt(name.length() - 4) == '.') {
			newName = newName.substring(0, newName.length() - 4);
		}
		return newName;
	}

	public void diplayAttribution(ObjectXmlData o) {
		String modeledBy = o.getDetail("modeledby");
		String programmedBy = o.getDetail("programmedby");
		String paintedBy = o.getDetail("paintedby");
		String displayName = cleanUpName(o.name);
		String attributeString = displayName;
		if (modeledBy != null || programmedBy != null || paintedBy != null) {
			attributeString += ": ";
		}
		boolean haveOne = false;
		if (modeledBy != null) {
			attributeString += "Modeled by " + modeledBy;
			haveOne = true;
		}
		if (paintedBy != null) {
			if (haveOne) {
				attributeString += ", ";
			}
			attributeString += "Painted by " + paintedBy;
			haveOne = true;
		}
		if (programmedBy != null) {
			if (haveOne) {
				attributeString += ", ";
			}
			attributeString += "Programmed by " + programmedBy;
		}
		attributeLabel.setText(attributeString);
		attributePanel.repaint();
	}

	public void removeAttribution() {
		attributeLabel.setText(" ");
		attributePanel.repaint();
	}

	protected void refreshGUI() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!stopBuildingGallery) {
					if (inBrowseMode) {
						createDirectoryButtons();
						directoryPanel.revalidate();
					} else {
						searchPanel.revalidate();
					}
					GalleryViewer.this.repaint();
				} else {
					return;
				}
				if (!stopBuildingGallery) {
					createGalleryPanels();
					objectPanel.revalidate();
					GalleryViewer.this.repaint();
				} else {
					return;
				}
				removeAttribution();
			}
		});
	}

	private void bumpDown(int index) {
		// for (int i=index; i<objectPanel.getComponentCount(); i++){
		// java.awt.Component c = objectPanel.getComponent(i);
		// java.awt.GridBagConstraints g = objectPanelLayout.getConstraints(c);
		// g.gridx++;
		// objectPanelLayout.setConstraints(c,g);
		// }
	}

	private void resetLayout(int index) {
		// for (int i=index; i<objectPanel.getComponentCount(); i++){
		// java.awt.Component c = objectPanel.getComponent(i);
		// java.awt.GridBagConstraints g = objectPanelLayout.getConstraints(c);
		// g.gridx = i;
		// objectPanelLayout.setConstraints(c,g);
		// }
	}

	private void removeGalleryObject(GalleryObject toRemove) {
		objectPanel.remove(toRemove);
		resetLayout(0);
	}

	private void modelAdded(ObjectXmlData added, int count) {
		final GalleryObject toAdd = createGalleryObject(added);
		if (toAdd != null) {
			count++;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					objectPanel.add(toAdd);
					objectPanel.repaint();
				}
			});
		}
	}

	private void directoryAdded(ObjectXmlData added, final int count) {
		final GalleryObject toAdd = createGalleryDirectory(added);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (toAdd != null) {
					bumpDown(count);
					// objectPanel.add(toAdd, new
					// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
					// panelInset, 0,0 ), count);
					objectPanel.add(toAdd);
					objectPanel.revalidate();
				}
			}
		});
	}

	private GalleryObject getGalleryObject(String objectName) {
		for (int i = 0; i < objectPanel.getComponentCount(); i++) {
			if (objectPanel.getComponent(i) instanceof GalleryObject) {
				GalleryObject current = (GalleryObject) objectPanel.getComponent(i);
				if (current.data.name.equals(objectName)) {
					return current;
				}
			}
		}
		return null;
	}

	/**
	 * Creates the gui elements to display the current directory based on
	 * <code>directoryOnDisplay</code>. If <code>directoryOnDisplay</code> is
	 * null, then the base directory is created.
	 */
	private void createGalleryPanels() {
		if (directoryOnDisplay == null) {
			createRootGalleryPanels();
		} else {
			DirectoryXmlData xmlData = directoryOnDisplay.xmlData;
			objectPanel.removeAll();
			int count = 0;
			if (xmlData == null || xmlData.directories == null || xmlData.models == null) {
				return;
			}
			int size = xmlData.directories.size() + xmlData.models.size();
			boolean isPeople = false;
			boolean isLocal = false;
			if (xmlData.name.equalsIgnoreCase("people")) {
				size += builderButtonsVector.size();
				isPeople = true;
			}
			if (xmlData.name.equalsIgnoreCase("local gallery")) {
				size++;
				isLocal = true;
			}
			// objectPanel.add(javax.swing.Box.createHorizontalGlue(), new
			// java.awt.GridBagConstraints(size,0,1,1,1,1,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,
			// new java.awt.Insets(0,0,0,0), 0,0 ));
			for (int i = 0; i < xmlData.directories.size(); i++) {
				if (!stopBuildingGallery) {
					ObjectXmlData currentDirectory = (ObjectXmlData) xmlData.directories.get(i);
					long oldTime = System.currentTimeMillis();
					GalleryObject toAdd = createGalleryDirectory(currentDirectory);
					// System.out.println("directory build time: "+(System.currentTimeMillis()-oldTime));
					if (toAdd != null) {
						// objectPanel.add(toAdd, new
						// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
						// panelInset, 0,0 ));
						objectPanel.add(toAdd);
						count++;
					}
				} else {
					return;
				}
			}
			for (int i = 0; i < xmlData.models.size(); i++) {
				if (!stopBuildingGallery) {
					ObjectXmlData currentModel = (ObjectXmlData) xmlData.models.get(i);
					long oldTime = System.currentTimeMillis();
					GalleryObject toAdd = createGalleryObject(currentModel);
					// System.out.println("object build time: "+(System.currentTimeMillis()-oldTime));
					if (toAdd != null) {
						// objectPanel.add(toAdd, new
						// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
						// panelInset, 0,0 ));
						objectPanel.add(toAdd);
						count++;
					}
				} else {
					return;
				}
			}
			// Add personBuilders:
			if (isPeople) {
				for (int p = 0; p < builderButtonsVector.size(); p++) {
					count++;
					GenericBuilderButton builderButton = (GenericBuilderButton) builderButtonsVector.get(p);
					// objectPanel.add(builderButton, new
					// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
					// panelInset, 0,0 ));
					objectPanel.add(builderButton);
					builderButton.updateGUI();

				}
			}

			if (isLocal) {
				count++;
				bumpDown(count);
				// objectPanel.add(add3DTextButton, new
				// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
				// panelInset, 0,0 ), count);
				objectPanel.add(add3DTextButton);
				add3DTextButton.updateGUI();
				objectPanel.repaint();
			}
			if (count == 0) {
				objectPanel.removeAll();
				// objectPanel.add(javax.swing.Box.createHorizontalGlue(), new
				// java.awt.GridBagConstraints(1,0,1,1,1,1,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,
				// new java.awt.Insets(0,0,0,0), 0,0 ));
				if (directoryOnDisplay == searchResults) {
					// objectPanel.add(noSearchResults, new
					// java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
					// new java.awt.Insets(0,4,0,0), 0,0 ));
					objectPanel.add(noSearchResults);
				} else {
					// objectPanel.add(noObjectsLabel, new
					// java.awt.GridBagConstraints(0,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
					// new java.awt.Insets(0,4,0,0), 0,0 ));
					objectPanel.add(noObjectsLabel);
				}
			}
		}
	}

	private void createRootGalleryPanels() {
		if (rootDirectories != null && rootDirectories.size() > 0) {
			int count = 0;
			int maxCount = rootDirectories.size();
			objectPanel.removeAll();
			// objectPanel.add(javax.swing.Box.createHorizontalGlue(), new
			// java.awt.GridBagConstraints(maxCount,0,1,1,1,1,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,
			// new java.awt.Insets(0,0,0,0), 0,0 ));
			for (int i = 0; i < rootDirectories.size(); i++) {
				if (!stopBuildingGallery) {
					RootDirectoryStructure currentRoot = (RootDirectoryStructure) rootDirectories.get(i);
					GalleryObject toAdd = (GalleryObject) edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentRoot.xmlData);
					try {
						toAdd.set(currentRoot.xmlData);
						if (currentRoot.directory.name == cdGalleryName) {
							toAdd.setImage(cdGalleryIcon);
						} else if (currentRoot.directory.name == localGalleryName) {
							toAdd.setImage(localGalleryIcon);
						} else if (currentRoot.directory.name == webGalleryName) {
							toAdd.setImage(webGalleryIcon);
							((WebGalleryDirectory) toAdd).isTopLevelDirectory = true;

						}
					} catch (Exception e) {
						toAdd = null;
						continue;
					}
					if (toAdd != null) {
						// objectPanel.add(toAdd, new
						// java.awt.GridBagConstraints(count,0,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE,
						// panelInset, 0,0 ));
						objectPanel.add(toAdd);
						count++;
					}
				} else {
					return;
				}
			}

		}
	}

	protected void goUpOneLevel() {
		if (directoryOnDisplay != null) {
			changeDirectory(directoryOnDisplay.parent);
		}
	}

	private void createDirectoryButtons() {
		DirectoryStructure currentDir = directoryOnDisplay;
		java.util.Stack dirs = new java.util.Stack();
		directoryPanel.removeAll();
		javax.swing.JLabel currentDirLabel = new javax.swing.JLabel();
		currentDirLabel.setForeground(textColor);
		int count = 0;
		upLevelButton.setEnabled(true);
		if (currentDir != null) {
			DirectoryBarButton rootButton = new DirectoryBarButton(null, this);
			directoryPanel.add(rootButton, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 3, 0, 3), 0, 0));
			count++;
			while (currentDir != null) {
				if (isInWebGallery && (currentDir.name.equals(localGalleryName) || currentDir.name.equals(cdGalleryName))) {
					if (webGallery != null) {
						dirs.push(webGallery.directory);
						currentDir = webGallery.directory.parent;
					} else {
						dirs.push(currentDir);
						currentDir = currentDir.parent;
					}
				} else {
					dirs.push(currentDir);
					currentDir = currentDir.parent;
				}
			}
			while (!dirs.empty()) {
				currentDir = (DirectoryStructure) dirs.pop();
				if (count > 0) {
					javax.swing.JLabel arrow = new javax.swing.JLabel(">");
					arrow.setForeground(textColor);
					directoryPanel.add(arrow, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 3, 0, 3), 0, 0));
					count++;
				}
				if (currentDir == directoryOnDisplay) {
					currentDirLabel.setText(currentDir.name);
					directoryPanel.add(currentDirLabel, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				} else {
					DirectoryBarButton currentButton = new DirectoryBarButton(currentDir, this);
					directoryPanel.add(currentButton, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
				}
				count++;
			}
		} else {
			// isInWebGallery = false;
			currentDirLabel.setText(homeName);
			directoryPanel.add(currentDirLabel, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
			count++;
			upLevelButton.setEnabled(false);
			upLevelButton.repaint();
		}
		directoryPanel.add(upLevelButton, new java.awt.GridBagConstraints(count, 0, 1, 1, 0, 0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 4, 0, 0), 0, 0));
		count++;
		directoryPanel.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(count, 0, 1, 1, 1, 1, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, 0, 0, 0), 0, 0));
	}

	private void exploreXML(org.w3c.dom.Node current, int level) {
		if (current != null) {
			System.out.println(level + ": " + "Name: " + current.getNodeName() + ", Type: " + current.getNodeType() + ", Value: " + current.getNodeValue() + ", Children {");
			org.w3c.dom.NodeList children = current.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				exploreXML(children.item(i), level + 1);
			}
			System.out.println("}");
		}
	}
}