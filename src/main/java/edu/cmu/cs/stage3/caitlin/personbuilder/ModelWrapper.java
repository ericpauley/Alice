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

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Element;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.*;

public class ModelWrapper {
	protected edu.cmu.cs.stage3.alice.core.World miniWorld;
	protected edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera;
	protected edu.cmu.cs.stage3.alice.core.light.DirectionalLight directionalLight;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget;
	protected java.awt.event.MouseListener renderTargetMouseListener;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected RotateManipulator rtom;
	protected Model person = null;
	protected Model template = null;
	protected edu.cmu.cs.stage3.alice.core.CopyFactory personFactory = null;
	protected java.awt.Image[] textureLayers = new java.awt.Image[10]; // what is the right value for this?
	protected Hashtable partsTable = new Hashtable();
	protected java.net.URL url = null; //HACK

	protected Vector propertyNameList = new Vector();
	protected Vector propertyValueList = new Vector();
	protected Vector propertyDescList = new Vector();
	protected Vector itemChoosersWithAlts = new Vector();

	public ModelWrapper(Node root) {
		worldInit();
		try {
			loadInitModel(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		makeNewPerson();
	}

	public void registerItemChooserWithAlt(ItemChooser itemChooser) {
		itemChoosersWithAlts.addElement(itemChooser);
	}

	protected void replaceModel(String modelName, Model model) {
		if (model != null) {
			if (model.name.getStringValue().equals(modelName)) {
				if (person != null) {
					edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion =
						new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(modelName);
					Element[] parts = person.search(nameCriterion, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
					model.isFirstClass.set(false);
					if (parts.length > 0) {
						Element part = null;
						part = parts[0];
						if (part != null) {
							edu.cmu.cs.stage3.math.Vector3 posToParent =
								((Model) part).getPosition((edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
							edu.cmu.cs.stage3.math.Matrix33 orientToParent =
								((Model) part).getOrientationAsAxes((edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
							part.replaceWith(model);
							if (part instanceof Model) {
								model.vehicle.set(((Model) part).vehicle.get());
								((Model) part).vehicle.set(null);
								if (posToParent != null)
									 (model).setPositionRightNow(posToParent, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) model.getParent());
								if (orientToParent != null)
									 (model).setOrientationRightNow(orientToParent, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) model.getParent());

								edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
								person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
							}
						}

					} else {
						System.out.println(model.name.getStringValue() + " is not found");
					}
				}
			}
		}
	}

	public void switchToAltModel(String modelName) {
		for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
			ItemChooser itemChooser = (ItemChooser) itemChoosersWithAlts.elementAt(i);
			edu.cmu.cs.stage3.alice.core.Model model = itemChooser.getAltModel();
			replaceModel(modelName, model);

		}
	}

	public void switchToOrigModel(String modelName) {
		for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
			ItemChooser itemChooser = (ItemChooser) itemChoosersWithAlts.elementAt(i);
			edu.cmu.cs.stage3.alice.core.Model model = itemChooser.getOriginalModel();
			replaceModel(modelName, model);

		}
	}

	public void resetWorld() {
		partsTable = new Hashtable();
		textureLayers = new java.awt.Image[10];
		makeNewPerson();
	}

	protected void worldInit() {
		miniWorld = new edu.cmu.cs.stage3.alice.core.World();
		camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
		directionalLight = new edu.cmu.cs.stage3.alice.core.light.DirectionalLight();

		camera.vehicle.set(miniWorld);
		camera.setPositionRightNow(0.0, 1.5, 6.0);
		camera.verticalViewingAngle.set(new Double(Math.PI / 6.0));
		directionalLight.vehicle.set(camera);
		directionalLight.setOrientationRightNow(camera.getOrientationAsQuaternion());
		directionalLight.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.FORWARD, .15);
		directionalLight.turnRightNow(edu.cmu.cs.stage3.alice.core.Direction.LEFT, .075);
		directionalLight.color.set(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);

		java.awt.Color dkBlue = new java.awt.Color(12, 36, 106);
		miniWorld.atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(dkBlue));
		miniWorld.ambientLightColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY);
	}

	protected void makeNewPerson() {
		if (personFactory == null) {
			try {
				personFactory = template.createCopyFactory();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			person = (Model) personFactory.manufactureCopy(null);
			addModelToWorld(person, "none", null);
		} catch( edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre ) {
			throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper( upre, "UnresolvablePropertyReferencesException" );
		}
	}

	protected void loadInitModel(Node root) {
		Vector modelURLs = XMLDirectoryUtilities.getModelURLs(root);
		for (int i = 0; i < modelURLs.size(); i++) {
			url = (java.net.URL) modelURLs.elementAt(i);
			try {
				template = (Model) Element.load(url, null);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				upre.printStackTrace();
			}
		}
	}

	private void initializeModels(Model part, String parentName, edu.cmu.cs.stage3.math.Vector3 position) {
		// check to see if anything should be parented to this
		Model partsToAttach = (Model) partsTable.get(part.getKey());
		if ((partsToAttach != null) && (partsToAttach.getParent() == null)) {
			addChildToModel(part, partsToAttach, position);
		}
	}

	private Element[] removeModelFromWorld(Model model) {
		if (model != null) {
			Element[] kids = model.getChildren();
			model.removeFromParent();
			model.vehicle.set(null);
			return kids;
		}
		return null;
	}

	private void removeAllKids(Model parent) {
		if (parent.getChildCount() > 0) {
			Element[] oldKids = parent.getChildren();
			for (int i = 0; i < oldKids.length; i++) {
				if (oldKids[i] instanceof Model) {
					oldKids[i].removeFromParent();
					Model oldModel = (Model) oldKids[i];
					oldModel.vehicle.set(null);
					parent.removeChild(oldModel);
				}
			}
		}
	}

	private void addKidsToModel(Model newParent, Element[] kids) {
		// remove any old kids the newParent might already have
		removeAllKids(newParent);
		if ((newParent != null) && (kids != null)) {
			for (int i = 0; i < kids.length; i++) {
				if (kids[i] instanceof Model) {
					Model kidModel = (Model) kids[i];
					addChildToModel(newParent, kidModel, null);
				}
			}
		}
	}

	private void addChildToModel(Model parent, Model child, edu.cmu.cs.stage3.math.Vector3 position) {
		parent.addChild(child);
		parent.parts.add(child);
		child.setParent(parent);
		child.isFirstClass.set(false);
		child.vehicle.set(parent);
		if (position != null)
			child.setPositionRightNow(position, parent);
	}

	private void addModelToWorld(Model model, String parent, edu.cmu.cs.stage3.math.Vector3 position) {
		if (parent.equals("none")) {
			person = model;
			regenerateTexture();
			person.vehicle.set(miniWorld);
			camera.pointAtRightNow(person);
		}
	}

	public void removeModel() {
		if (person != null)
			person.vehicle.set(null);
	}

	private void renderInit() {
		authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		renderTarget = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
		if (renderTarget != null) {
			renderTarget.addCamera(camera.getSceneGraphCamera());
			rtom = new RotateManipulator(renderTarget);
			rtom.setTransformableToRotate(person);
		} else {
			System.err.println("PersonBuilder unable to create renderTarget");
		}
	}

	public java.awt.Component getRenderPanel() {
		renderInit();
		return renderTarget.getAWTComponent();
	}

	public Model getModel() {
		return person;
//		if ((person != null) && (person.name.getStringValue().equals("NoName"))) {
//			return null;
//		} else
//			return person;
	}

	protected void regenerateTexture() {
		java.awt.image.BufferedImage finalTexture = null;
		java.awt.Graphics2D g2 = null;

		for (int i = 0; i < textureLayers.length; i++) {
			if (textureLayers[0] == null) {
				java.awt.Image im = person.diffuseColorMap.getTextureMapValue().image.getImageValue();
				textureLayers[0] = im;
			}
			if (textureLayers[i] != null) {
				if (finalTexture == null) {
					finalTexture =
						new java.awt.image.BufferedImage(
							textureLayers[i].getHeight(null),
							textureLayers[i].getWidth(null),
							java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
					g2 = finalTexture.createGraphics();
				}
				g2.drawImage(textureLayers[i], 0, 0, null);
			}
		}

		if (finalTexture != null) {
			person.diffuseColorMap.getTextureMapValue().image.set(finalTexture);
			person.diffuseColorMap.getTextureMapValue().touchImage();
		}
	}

	public void addTexture(java.awt.Image texture, int level) {
		textureLayers[level] = texture;
		regenerateTexture();
	}

	public void clearLevel(int level) {
		textureLayers[level] = null;
	}

	public void addModel(Model modelToAdd, String parentName, edu.cmu.cs.stage3.math.Vector3 position) {
		if (person != null) {
			edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion =
				new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(parentName);
			Element[] parents = person.search(nameCriterion, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			if (parents.length > 0) {
				modelToAdd.setParent(parents[0]);
				((edu.cmu.cs.stage3.alice.core.Model) parents[0]).parts.add(modelToAdd);
				modelToAdd.vehicle.set(parents[0]);
				modelToAdd.isFirstClass.set(false);

				modelToAdd.setPositionRightNow(position, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) parents[0]);
			}
		}
		edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
		modelToAdd.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
		person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

	public void removeModel(String modelName) {
		if (person != null) {
			edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion =
				new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(modelName);
			Element[] models = person.search(nameCriterion, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			if (models.length > 0) {
				models[0].getParent().removeChild(models[0]);
				((edu.cmu.cs.stage3.alice.core.Model) models[0]).vehicle.set(null);
			}
		}
		edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
		person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

	private void rootModelChanged() {
		regenerateTexture();

		for (int i = 0; i < propertyNameList.size(); i++) {
			String propName = (String) propertyNameList.elementAt(i);
			String propValue = (String) propertyValueList.elementAt(i);
			String propDesc = (String) propertyDescList.elementAt(i);

			setPropertyValue(propName, propValue, propDesc);
		}

	}

	public String getModelName() {
		if (person != null)
			return person.name.getStringValue();
		else
			return "";
	}

	public void setModel(Model part, String parentName) {
		if ((parentName.equals("none")) && (person == null)) {
		} else {
			if (person != null) {
				edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion nameCriterion =
					new edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion(parentName);
				Element[] parents = person.search(nameCriterion, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
				if (parents.length > 0) {
					Element child = parents[0].getChildNamed(part.name.getStringValue());
					if (child != null) {
						part.isFirstClass.set(false);
						edu.cmu.cs.stage3.math.Vector3 posToParent = ((Model) child).getPosition((edu.cmu.cs.stage3.alice.core.ReferenceFrame) parents[0]);
						child.replaceWith(part);
						if (child instanceof Model) {
							part.vehicle.set(((Model) child).vehicle.get());
							((Model) child).vehicle.set(null);
							if (posToParent != null)
								 (part).setPositionRightNow(posToParent, (edu.cmu.cs.stage3.alice.core.ReferenceFrame) part.getParent());
						}
					}

				} else {
					System.out.println(part.name.getStringValue() + " is not found");
				}
			}
			edu.cmu.cs.stage3.alice.core.TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
			person.diffuseColorMap.set(tMap, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
		}
	}

	private void setPropertyValue(String propertyName, String propertyValue, String propertyDescription) {
		edu.cmu.cs.stage3.alice.core.Property property = person.getPropertyNamed(propertyName);
		if ((property != null) && (property instanceof edu.cmu.cs.stage3.alice.core.property.StringProperty))
			property.set(propertyValue);
		else if ((property != null) && (property instanceof edu.cmu.cs.stage3.alice.core.property.DictionaryProperty)) {
			((edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) property).put(propertyDescription, propertyValue);
		}
	}

	public void setProperty(String propertyName, String propertyValue, String propertyDesc) {
		int propertyIndex = propertyNameList.indexOf(propertyName);

		// we already have this property in the list
		if (propertyIndex != -1) {
			propertyValueList.setElementAt(propertyValue, propertyIndex);
			if (propertyDesc != null)
				propertyDescList.setElementAt(propertyDesc, propertyIndex);
			else
				propertyDescList.setElementAt("", propertyIndex);
		} else {
			propertyNameList.addElement(propertyName);
			propertyValueList.addElement(propertyValue);
			if (propertyDesc != null)
				propertyDescList.addElement(propertyDesc);
			else
				propertyDescList.addElement("");
		}

		setPropertyValue(propertyName, propertyValue, propertyDesc);
	}

	public void setColor(java.awt.Color color) {
		java.awt.image.BufferedImage baseColor = new java.awt.image.BufferedImage(512, 512, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = (java.awt.Graphics2D) baseColor.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 512, 512);
		addTexture(baseColor, 0);
		//person.color.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(color), edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS);
	}

}