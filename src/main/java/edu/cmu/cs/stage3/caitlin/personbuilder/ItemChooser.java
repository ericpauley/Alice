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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 * 
 * @author
 * @version 1.0
 */

public class ItemChooser extends JPanel {
	BorderLayout borderLayout1 = new BorderLayout();
	JLabel itemPicture = new JLabel();
	JButton backButton = new JButton();
	JButton forwardButton = new JButton();
	ImageIcon nextImage = null;
	ImageIcon backImage = null;
	ModelWrapper modelWrapper = null;
	Vector commandInfos = new Vector();
	int index = 0;

	public ItemChooser(Node itemsNode, ImageIcon nextImage, ImageIcon backImage, ModelWrapper modelWrapper) {
		this.nextImage = nextImage;
		this.backImage = backImage;
		this.modelWrapper = modelWrapper;
		try {
			initializeChoices(itemsNode);
			jbInit();

			CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
			itemPicture.setIcon(currentInfo.imageIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetDefaults() {
		index = 0;
		CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
		itemPicture.setIcon(currentInfo.imageIcon);
	}

	private void initializeCommandInfos(Node dirNode, Node choicesNode) {
		NodeList choiceNodeList = choicesNode.getChildNodes();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int altModelCount = 0;
		for (int i = 0; i < choiceNodeList.getLength(); i++) {
			Node choiceNode = choiceNodeList.item(i);

			if (choiceNode.getNodeName().equals("addTexture")) {
				CommandInfo cmdInfo = new CommandInfo();
				cmdInfo.id = "addTexture";
				NamedNodeMap attrs = choiceNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					if (attr.getNodeName().equals("icon")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						try {
							ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
							cmdInfo.imageIcon = icon;
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (attr.getNodeName().equals("texture")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
						try {
							java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
							tracker.addImage(img, 0);
							tracker.waitForID(0);
						} catch (java.lang.InterruptedException ie) {}
						cmdInfo.texture = img;
					} else if (attr.getNodeName().equals("layer")) {
						String layerString = attr.getNodeValue();
						int layer = Integer.parseInt(layerString);
						cmdInfo.level = layer;
					} else if (attr.getNodeName().equals("useAltModel")) {
						String altModelName = attr.getNodeValue();
						cmdInfo.altModelName = altModelName;
					}
				}
				commandInfos.addElement(cmdInfo);

			} else if (choiceNode.getNodeName().equals("setModel")) {
				CommandInfo cmdInfo = new CommandInfo();
				cmdInfo.id = "setModel";
				NamedNodeMap attrs = choiceNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					if (attr.getNodeName().equals("icon")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						try {
							ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
							cmdInfo.imageIcon = icon;
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (attr.getNodeName().equals("model")) {
						String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Model part = null;
						try {
							part = (Model) Element.load(PersonBuilder.class.getResource(modelString), null);
							part.vehicle.set(null);
						} catch (java.io.IOException ioe) {
							ioe.printStackTrace();
						} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
							upre.printStackTrace();
						}
						cmdInfo.model = part;
						cmdInfo.modelFactory = part.createCopyFactory();
						cmdInfo.modelString = modelString;
					} else if (attr.getNodeName().equals("parent")) {
						String parentString = attr.getNodeValue();
						cmdInfo.parentString = parentString;
					} else if (attr.getNodeName().equals("altModel")) {
						String altModelName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Model altModel = null;
						try {
							altModel = (Model) Element.load(PersonBuilder.class.getResource(altModelName), null);
							altModel.vehicle.set(null);
						} catch (java.io.IOException ioe) {
							ioe.printStackTrace();
						} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
							upre.printStackTrace();
						}
						cmdInfo.altModel = altModel;
						cmdInfo.altModelFactory = altModel.createCopyFactory();
						altModelCount++;
					}
				}

				commandInfos.addElement(cmdInfo);

			} else if (choiceNode.getNodeName().equals("addModelAndTexture")) {
				CommandInfo cmdInfo = new CommandInfo();
				cmdInfo.id = "addModelAndTexture";
				NamedNodeMap attrs = choiceNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					if (attr.getNodeName().equals("icon")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
						cmdInfo.imageIcon = icon;
					} else if (attr.getNodeName().equals("model")) {
						String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Model part = null;
						try {
							part = (Model) Element.load(PersonBuilder.class.getResource(modelString), null);
							part.vehicle.set(null);
						} catch (java.io.IOException ioe) {
							ioe.printStackTrace();
						} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
							upre.printStackTrace();
						}
						cmdInfo.model = part;
						cmdInfo.modelFactory = part.createCopyFactory();
						cmdInfo.modelString = modelString;
					} else if (attr.getNodeName().equals("parent")) {
						String parentString = attr.getNodeValue();
						cmdInfo.parentString = parentString;
					} else if (attr.getNodeName().equals("texture")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
						try {
							java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
							tracker.addImage(img, 0);
							tracker.waitForID(0);
						} catch (java.lang.InterruptedException ie) {}
						cmdInfo.texture = img;
					} else if (attr.getNodeName().equals("layer")) {
						String layerString = attr.getNodeValue();
						int layer = Integer.parseInt(layerString);
						cmdInfo.level = layer;
					} else if (attr.getNodeName().equals("useAltModel")) {
						String altModelName = attr.getNodeValue();
						cmdInfo.altModelName = altModelName;
					} else if (attr.getNodeName().equals("x")) {
						cmdInfo.x = Double.parseDouble(attr.getNodeValue());
					} else if (attr.getNodeName().equals("y")) {
						cmdInfo.y = Double.parseDouble(attr.getNodeValue());
					} else if (attr.getNodeName().equals("z")) {
						cmdInfo.z = Double.parseDouble(attr.getNodeValue());
					}
				}
				commandInfos.addElement(cmdInfo);
			} else if (choiceNode.getNodeName().equals("setMultipleModelsAndTexture")) {
				CommandInfo cmdInfo = new CommandInfo();
				cmdInfo.id = "setMultipleModelsAndTexture";
				NamedNodeMap attrs = choiceNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					if (attr.getNodeName().equals("icon")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
						cmdInfo.imageIcon = icon;
					} else if (attr.getNodeName().equals("model1")) {
						String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Model part = null;
						try {
							part = (Model) Element.load(PersonBuilder.class.getResource(modelString), null);
							part.vehicle.set(null);
						} catch (java.io.IOException ioe) {
							ioe.printStackTrace();
						} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
							upre.printStackTrace();
						}
						cmdInfo.model = part;
						cmdInfo.modelFactory = part.createCopyFactory();
						cmdInfo.modelString = modelString;
					} else if (attr.getNodeName().equals("parent1")) {
						String parentString = attr.getNodeValue();
						cmdInfo.parentString = parentString;
					} else if (attr.getNodeName().equals("model2")) {
						String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Model part = null;
						try {
							part = (Model) Element.load(PersonBuilder.class.getResource(modelString), null);
							part.vehicle.set(null);
						} catch (java.io.IOException ioe) {
							ioe.printStackTrace();
						} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
							upre.printStackTrace();
						}
						cmdInfo.altModel = part;
						cmdInfo.altModelFactory = part.createCopyFactory();
						// cmdInfo.altModelName = modelString;
					} else if (attr.getNodeName().equals("parent2")) {
						String parentString = attr.getNodeValue();
						cmdInfo.altParentString = parentString;
					} else if (attr.getNodeName().equals("texture")) {
						String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
						Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
						try {
							java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
							tracker.addImage(img, 0);
							tracker.waitForID(0);
						} catch (java.lang.InterruptedException ie) {}
						cmdInfo.texture = img;
					} else if (attr.getNodeName().equals("layer")) {
						String layerString = attr.getNodeValue();
						int layer = Integer.parseInt(layerString);
						cmdInfo.level = layer;
					}
				}
				commandInfos.addElement(cmdInfo);
			}
		}
		if (altModelCount > 0) {
			modelWrapper.registerItemChooserWithAlt(this);
		}
	}

	private Model reloadModel(String modelString) {
		Model part = null;
		try {
			part = (Model) Element.load(PersonBuilder.class.getResource(modelString), new Model());
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
			upre.printStackTrace();
		}
		return part;
	}

	private void initializeChoices(Node itemsNode) {
		Vector allImages = XMLDirectoryUtilities.getImages(itemsNode);
		Document configDoc = loadXMLFile(itemsNode);

		if (allImages != null && allImages.size() > 0 && configDoc != null) {
			NodeList choices = configDoc.getChildNodes();
			for (int i = 0; i < choices.getLength(); i++) {
				initializeCommandInfos(itemsNode, choices.item(i));
			}
		}
	}

	private Document loadXMLFile(Node itemsNode) {
		Document document = null;
		java.net.URL fileURL = null;

		Vector xmlFiles = XMLDirectoryUtilities.getXMLURLs(itemsNode);
		if (xmlFiles.size() == 0) {
			System.out.println("No xml file found: ");
		} else if (xmlFiles.size() > 1) {
			System.out.println("Multiple xml files found: ");
		} else {
			fileURL = (java.net.URL) xmlFiles.elementAt(0);
		}

		document = (Document) XMLDirectoryUtilities.loadURL(fileURL);
		return document;
	}

	private void currentLosingFocus() {
		if (index >= 0 && index < commandInfos.size()) {
			CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
			if (currentInfo.id.equals("addTexture") && currentInfo.altModelName != null) {
				modelWrapper.switchToOrigModel(currentInfo.altModelName);
			} else if (currentInfo.id.equals("addTexture")) {
				modelWrapper.clearLevel(currentInfo.level);
			} else if (currentInfo.id.equals("addModelAndTexture")) {
				// remove model
				modelWrapper.removeModel(currentInfo.model.name.getStringValue());

				modelWrapper.clearLevel(currentInfo.level);

				// switch to original model if necessary
				if (currentInfo.altModelName != null) {
					modelWrapper.switchToOrigModel(currentInfo.altModelName);
				}
			}
		}
	}

	private void setIcon() {
		try {
			if (index >= 0 && index < commandInfos.size()) {
				CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
				itemPicture.setIcon(currentInfo.imageIcon);
				if (currentInfo.id.equals("addTexture")) {
					modelWrapper.addTexture(currentInfo.texture, currentInfo.level);
					if (currentInfo.altModelName != null) {
						modelWrapper.switchToAltModel(currentInfo.altModelName);
					}
				} else if (currentInfo.id.equals("setModel")) {
					Model modelCopy = (Model) currentInfo.modelFactory.manufactureCopy(null);
					modelWrapper.setModel(modelCopy, currentInfo.parentString);
				} else if (currentInfo.id.equals("addModelAndTexture")) {
					// add texture
					modelWrapper.addTexture(currentInfo.texture, currentInfo.level);

					// add model
					Model modelCopy = (Model) currentInfo.modelFactory.manufactureCopy(null);
					edu.cmu.cs.stage3.math.Vector3 position = new edu.cmu.cs.stage3.math.Vector3(currentInfo.x, currentInfo.y, currentInfo.z);
					modelWrapper.addModel(modelCopy, currentInfo.parentString, position);

					// switch to alternate model if necessary
					if (currentInfo.altModelName != null) {
						modelWrapper.switchToAltModel(currentInfo.altModelName);
					}
				} else if (currentInfo.id.equals("setMultipleModelsAndTexture")) {
					// swap models
					Model modelCopy = (Model) currentInfo.modelFactory.manufactureCopy(null);
					modelWrapper.setModel(modelCopy, currentInfo.parentString);

					modelCopy = (Model) currentInfo.altModelFactory.manufactureCopy(null);
					modelWrapper.setModel(modelCopy, currentInfo.altParentString);

					// add texture
					modelWrapper.addTexture(currentInfo.texture, currentInfo.level);
				}
			}
		} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
			throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
		}
	}

	/*
	 * These will only be called by modelWrapper if this itemChooser has
	 * altModels
	 */
	public edu.cmu.cs.stage3.alice.core.Model getAltModel() {
		CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
		if (currentInfo.altModelFactory != null) {
			try {
				return (edu.cmu.cs.stage3.alice.core.Model) currentInfo.altModelFactory.manufactureCopy(null);
			} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
			}
		} else {
			return null;
		}
	}

	public edu.cmu.cs.stage3.alice.core.Model getOriginalModel() {
		CommandInfo currentInfo = (CommandInfo) commandInfos.elementAt(index);
		if (currentInfo.modelFactory != null) {
			try {
				return (edu.cmu.cs.stage3.alice.core.Model) currentInfo.modelFactory.manufactureCopy(null);
			} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
			}
		} else {
			return null;
		}
	}

	protected class CommandInfo {
		public String id = new String();
		public ImageIcon imageIcon = null;
		public String modelString = null;
		public edu.cmu.cs.stage3.alice.core.Model model = null;
		public edu.cmu.cs.stage3.alice.core.Model altModel = null;
		public edu.cmu.cs.stage3.alice.core.CopyFactory modelFactory = null;
		public edu.cmu.cs.stage3.alice.core.CopyFactory altModelFactory = null;
		public String altModelName = null;
		public String altParentString = null;
		public java.awt.Image texture = null;
		public int level = -1;
		public String parentString = new String();
		public double x = 0.0;
		public double y = 0.0;
		public double z = 0.0;
	}

	// AUTOGENERATED...
	private void jbInit() throws Exception {
		setBackground(new java.awt.Color(155, 159, 206));
		itemPicture.setMaximumSize(new Dimension(110, 110));
		itemPicture.setMinimumSize(new Dimension(110, 110));
		itemPicture.setPreferredSize(new Dimension(110, 110));
		setLayout(borderLayout1);
		backButton.setIcon(backImage);
		backButton.setBackground(new java.awt.Color(155, 159, 206));
		backButton.setBorder(null);
		backButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backButton_actionPerformed(e);
			}
		});
		forwardButton.setActionCommand("next");
		forwardButton.setIcon(nextImage);
		forwardButton.setBackground(new java.awt.Color(155, 159, 206));
		forwardButton.setBorder(null);
		forwardButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				forwardButton_actionPerformed(e);
			}
		});
		this.add(itemPicture, BorderLayout.CENTER);
		this.add(backButton, BorderLayout.WEST);
		this.add(forwardButton, BorderLayout.EAST);
	}

	void backButton_actionPerformed(ActionEvent e) {
		currentLosingFocus();
		index -= 1;
		// if (index < 0) index += itemIcons.size();
		if (index < 0) {
			index += commandInfos.size();
		}
		setIcon();
	}

	void forwardButton_actionPerformed(ActionEvent e) {
		currentLosingFocus();
		index += 1;
		// if (index > itemIcons.size()-1) index -= itemIcons.size();
		if (index > commandInfos.size() - 1) {
			index -= commandInfos.size();
		}
		setIcon();
	}
}