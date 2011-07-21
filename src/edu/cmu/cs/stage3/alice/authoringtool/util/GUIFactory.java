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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class GUIFactory {
	public static java.util.HashMap guiCache = new java.util.HashMap();


	protected static CollectionEditorPanel collectionEditorPanel;

	public static CollectionEditorPanel getCollectionEditorPanel() {
		if( collectionEditorPanel == null ) {
			collectionEditorPanel = new CollectionEditorPanel();
		}
		return collectionEditorPanel;
	}

	public static javax.swing.JComponent getGUI( Object o ) {
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		javax.swing.JComponent viewController = null;

		if( o instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ConditionalResponsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ConditionalResponsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.IfElseInOrder)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.LoopNInOrder ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CountLoopPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CountLoopPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.LoopNInOrder)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.LoopIfTrueResponsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.LoopIfTrueResponsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.ForEachInOrder ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForEachInListSequentialLoopPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForEachInListSequentialLoopPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.ForEachInOrder)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.ForEachTogether ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForAllTogetherResponsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForAllTogetherResponsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.ForEachTogether)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.DoTogether ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ParallelResponsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ParallelResponsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.DoTogether)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.DoInOrder ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.SequentialResponsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.SequentialResponsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.response.DoInOrder)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.IfElsePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.IfElsePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.LoopNPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.LoopNPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.While ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.WhilePanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.WhilePanel)viewController).set( (edu.cmu.cs.stage3.alice.core.question.userdefined.While)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.ForEachPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.ForEachPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.response.Print ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePrintViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePrintViewController)viewController).set( (edu.cmu.cs.stage3.alice.core.response.Print)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.QuestionPrintViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.QuestionPrintViewController)viewController).set( (edu.cmu.cs.stage3.alice.core.question.userdefined.Print)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController)viewController).setElement( (edu.cmu.cs.stage3.alice.core.Element)o );
			}
		} else if( (o instanceof edu.cmu.cs.stage3.alice.core.Response) || (o instanceof edu.cmu.cs.stage3.alice.core.Question) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController)viewController).setElement( (edu.cmu.cs.stage3.alice.core.Element)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.TriggerBehaviorPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.TriggerBehaviorPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.ConditionalBehaviorPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.ConditionalBehaviorPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.InternalResponseBehaviorPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.InternalResponseBehaviorPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.Behavior ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.GenericBehaviorPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.GenericBehaviorPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.Behavior)o, authoringTool );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.Sound ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundViewController)viewController).setSound( (edu.cmu.cs.stage3.alice.core.Sound)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.TextureMap ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapViewController)viewController).setTextureMap( (edu.cmu.cs.stage3.alice.core.TextureMap)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel)viewController).set( (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedQuestionPrototypeDnDPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedQuestionPrototypeDnDPanel)viewController).set( (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ) {
//			viewController = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel( (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)o );
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel)viewController).set( (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel)viewController).set( (edu.cmu.cs.stage3.alice.core.Element)o );
			}
		} else if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.ObjectXmlData ) {
			edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.ObjectXmlData objectXmlData = (edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.ObjectXmlData)o;
			if( objectXmlData.directoryData == null ) {
				if( objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.WEB ) {
					viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.WebGalleryObject.class );
				} else if( objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.LOCAL ||
						   objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.CD ) {
					viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.LocalGalleryObject.class );
				}
			} else {
				if( objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.WEB ) {
					viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.WebGalleryDirectory.class );
				} else if( objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.LOCAL ||
						   objectXmlData.type == edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.CD ) {
					viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.LocalGalleryDirectory.class );
				}
			}
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)viewController).set( (edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.ObjectXmlData)o );
			}
		}

		return viewController;
	}

	public static EditObjectButton getEditObjectButton( Object object, javax.swing.JComponent animationSource ) {
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		EditObjectButton editObjectButton = (EditObjectButton)getOrCreateGUI( EditObjectButton.class );
		editObjectButton.setAuthoringTool( authoringTool );
		editObjectButton.setObject( object );
		editObjectButton.setAnimationSource( animationSource );
		return editObjectButton;
	}

	public static void releaseGUI( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement guiElement ) {
//		guiElement.die();
		guiElement.clean();
		Class guiClass = guiElement.getClass();
		java.util.Set guiSet = (java.util.Set)guiCache.get( guiClass );
		if( guiSet == null ) {
			guiSet = new java.util.HashSet();
			guiCache.put( guiClass, guiSet );
		}
		guiSet.add( guiElement );
//		if( guiElement instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel ) {
//			System.out.println( "added to pool: " + guiElement.hashCode() );
//		}
	}

	protected static javax.swing.JComponent getOrCreateGUI( Class guiClass ) {
		java.util.Set guiSet = (java.util.Set)guiCache.get( guiClass );
		if( (guiSet != null) && (! guiSet.isEmpty()) ) {
			javax.swing.JComponent guiElement = (javax.swing.JComponent)guiSet.iterator().next();
			if( guiElement.getParent() != null ) {
				guiElement.getParent().remove( guiElement );
			}
			guiSet.remove( guiElement );
//			if( guiElement instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel ) {
//				System.out.println( "taken from pool: " + guiElement.hashCode() );
//			}
			return guiElement;
		} else {
			try {
				return (javax.swing.JComponent)guiClass.newInstance();
			} catch( Throwable t ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating new GUI; " + guiClass, t );
			}
		}
		return null;
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyGUI getPropertyGUI( edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, PopupItemFactory factory ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyGUI propertyGUI = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyGUI)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyGUI.class );
		if( propertyGUI != null ) {
			propertyGUI.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack(), property, includeDefaults, allowExpressions, factory );
		}
		return propertyGUI;
	}

	public static String cleanHTMLString(String toReturn){
		if (toReturn == ""){return toReturn;}
		while (toReturn != null && toReturn.indexOf("<") > -1){
			toReturn = toReturn.substring(0, toReturn.indexOf("<"))+ "&lt;"+toReturn.substring(toReturn.indexOf("<")+1, toReturn.length());
		}
		if (toReturn == ""){return toReturn;}
		while (toReturn != null && toReturn.indexOf(">") > -1){
			toReturn = toReturn.substring(0, toReturn.indexOf(">"))+ "&gt;"+toReturn.substring(toReturn.indexOf(">")+1, toReturn.length());
		}
		if (toReturn == ""){return toReturn;}
		while (toReturn != null &&  toReturn.indexOf("\"") > -1){
			toReturn = toReturn.substring(0, toReturn.indexOf("\""))+ "&quot;"+toReturn.substring(toReturn.indexOf("\"")+1, toReturn.length());
		}
		return toReturn;
	}

	public static String getHTMLStringForComponent(java.awt.Component c){
		String toReturn = "";

		if (c instanceof javax.swing.JLabel){
			if (((javax.swing.JLabel)c).getIcon() != null){
				javax.swing.Icon icon = ((javax.swing.JLabel)c).getIcon();
				if (icon.equals(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("mouse"))){
					toReturn = "<b>the mouse</b>";
				} else if (icon.equals(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("arrowKeys"))){
					toReturn = "<b>the arrow keys</b>";
				} 
			}
			String labelText = cleanHTMLString(((javax.swing.JLabel)c).getText());
			if (((javax.swing.JLabel)c).getFont().isBold()){
				toReturn += "<b>"+labelText+"</b>";
			}
			if (((javax.swing.JLabel)c).getFont().isItalic()){
				toReturn += "<i>"+labelText+"</i>";
			}
			toReturn = " "+toReturn+" ";
			if (((javax.swing.JLabel)c).getText() == null || ((javax.swing.JLabel)c).getText().equals("more...")){
				toReturn = "";
			}
		} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController){
			edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController viewControllerC = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)c;
			StringBuffer htmlStringBuf = new StringBuffer();
			viewControllerC.getHTML(htmlStringBuf);
			toReturn = " "+htmlStringBuf.toString()+" ";
		} else if (c instanceof java.awt.Container){
			java.awt.Container containerC = (java.awt.Container)c;
			for (int i=0; i<containerC.getComponentCount(); i++){
				toReturn += getHTMLStringForComponent(containerC.getComponent(i))+" ";
			}
		} else {
		}
		toReturn = toReturn.trim();
		while ( toReturn.indexOf("  ") > -1){
			int index = toReturn.indexOf("  ");
			toReturn = toReturn.substring(0, index)+toReturn.substring(index+1, toReturn.length());
		}
		
		return toReturn;
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel getPropertyDnDPanel( edu.cmu.cs.stage3.alice.core.Property property ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel propertyDnDPanel = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel.class );
		if( propertyDnDPanel != null ) {
			propertyDnDPanel.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack(), property );
		}
		return propertyDnDPanel;
	}

	/**
	 * @deprecated  use getPropertyViewController
	 */
	public static javax.swing.JComponent createPropertyViewController( edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory ) {
		   return getPropertyViewController( property, includeDefaults, allowExpressions, omitPropertyName, factory );
	}
	public static javax.swing.JComponent getPropertyViewController( edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory ) {
		javax.swing.JComponent viewController = null;
		Class desiredValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(property);

		if( property.getName().equals( "keyCode" ) && Integer.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.KeyCodePropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.KeyCodePropertyViewController)viewController).set( property, allowExpressions, factory );
			}
		} else if( Boolean.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.BooleanPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.BooleanPropertyViewController)viewController).set( property, includeDefaults, allowExpressions, omitPropertyName, factory );
			}
		} else if( edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ColorPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ColorPropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
			}
		} else if( Number.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.NumberPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.NumberPropertyViewController)viewController).set( property, includeDefaults, allowExpressions, omitPropertyName, factory );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
				
			}
		} else if( String.class.isAssignableFrom( desiredValueClass ) ) {
			boolean emptyStringWritesNull = true;
			if( property.getName().equals( "script" ) ) {
				allowExpressions = false;
				emptyStringWritesNull = false;
			}
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StringPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StringPropertyViewController)viewController).set( property, includeDefaults, allowExpressions, omitPropertyName, emptyStringWritesNull, factory );
			}
		} else if( java.awt.Font.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FontPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FontPropertyViewController)viewController).set( property, omitPropertyName );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StylePropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StylePropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
			}
		} else if( edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.EnumerablePropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.EnumerablePropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom( desiredValueClass ) ) {
			if( allowExpressions ) {
				viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController.class );
				if( viewController != null ) {
					((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
				}
			} else {
				viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController.class );
				if( viewController != null ) {
					((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController)viewController).set( property, omitPropertyName );
				}
			}
		} else if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( desiredValueClass ) ) {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController)viewController).set( property, allowExpressions, omitPropertyName, factory );
			}
		} else if( property instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
			edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)property;
			if( edu.cmu.cs.stage3.alice.core.Variable.class.isAssignableFrom( oap.getComponentType() ) ) {
				viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariablesViewController.class );
				if( viewController != null ) {
					((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariablesViewController)viewController).set( oap );
				}
			}
		} else {
			viewController = getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.DefaultPropertyViewController.class );
			if( viewController != null ) {
				((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.DefaultPropertyViewController)viewController).set( property, includeDefaults, allowExpressions, false, omitPropertyName, factory );
			}
		}

		return viewController;
	}

	/**
	 * @deprecated use getVariableGUI
	 */
	public static javax.swing.JComponent createVariableGUI( edu.cmu.cs.stage3.alice.core.Variable variable, boolean includeDefaults, PopupItemFactory factory ) {
		return getVariableGUI( variable, includeDefaults, factory );
//		if( variable != null ) {
//			javax.swing.JLabel equalsLabel = new javax.swing.JLabel( " = " );
//			javax.swing.JPanel mainPanel = new javax.swing.JPanel();
//			mainPanel.setLayout( new javax.swing.BoxLayout( mainPanel, javax.swing.BoxLayout.X_AXIS ) );
//			mainPanel.add( createVariableDnDPanel( variable ) );
//			mainPanel.add( equalsLabel );
//			mainPanel.add( getPropertyViewController( variable.value, includeDefaults, false, true, factory ) );
//			mainPanel.add( javax.swing.Box.createHorizontalGlue() );
//			mainPanel.setOpaque( false );
//
//			return mainPanel;
//		} else {
//			return null;
//		}
	}

	/**
	 * @deprecated use getVariableDnDPanel
	 */
	public static javax.swing.JComponent createVariableDnDPanel( edu.cmu.cs.stage3.alice.core.Variable variable ) {
		return getVariableDnDPanel( variable );
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableGUI getVariableGUI( edu.cmu.cs.stage3.alice.core.Variable variable, boolean includeDefaults, PopupItemFactory factory ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableGUI variableGUI = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableGUI)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableGUI.class );
		if( variableGUI != null ) {
			variableGUI.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack(), variable, includeDefaults, factory );
		}
		return variableGUI;
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableDnDPanel getVariableDnDPanel( edu.cmu.cs.stage3.alice.core.Variable variable ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableDnDPanel variableDnDPanel = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableDnDPanel)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableDnDPanel.class );
		if( variableDnDPanel != null ) {
			variableDnDPanel.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack(), variable );
		}
		return variableDnDPanel;
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createResponseViewController( edu.cmu.cs.stage3.alice.core.Response response ) {
		return getGUI( response );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createQuestionViewController( edu.cmu.cs.stage3.alice.core.Question question ) {
		return getGUI( question );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createSoundViewController( edu.cmu.cs.stage3.alice.core.Sound sound ) {
		return getGUI( sound );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createTextureMapViewController( edu.cmu.cs.stage3.alice.core.TextureMap textureMap ) {
		return getGUI( textureMap );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createElementPrototypeDnDPanel( edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype ) {
		return getGUI( elementPrototype );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createResponsePrototypeDnDPanel( edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype ) {
		return getGUI( responsePrototype );
	}

	/**
	 * @deprecated  use getGUI( Object )
	 */
	public static javax.swing.JComponent createCallToUserDefinedResponsePrototypeDnDPanel( edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype ) {
		return getGUI( callToUserDefinedResponsePrototype );
	}

	/**
	 * @deprecated  use getElementNamePropertyViewController
	 */
	public static javax.swing.JComponent createRightClickNameEditor( edu.cmu.cs.stage3.alice.core.Element element ) {
		return getElementNamePropertyViewController( element );
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController getElementNamePropertyViewController( edu.cmu.cs.stage3.alice.core.Element element ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController enpvc = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController.class );
		enpvc.set( element );
		return enpvc;
	}

	public static edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel getElementDnDPanel( edu.cmu.cs.stage3.alice.core.Element element ) {
		edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel panel = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel)getOrCreateGUI( edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel.class );
		panel.set( element );
		return panel;
	}

	public static boolean isOtherDialogSupportedForClass( Class valueClass ) {
		if( Number.class.isAssignableFrom( valueClass ) ) {
			return true;
		} else if( edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom( valueClass ) ) {
			return false; //TODO: true
		} else if( edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom( valueClass ) ) {
			return true;
		} else if( String.class.isAssignableFrom( valueClass ) ) {
			return true;
		} else if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( valueClass ) ) {
			return true;
		} else {
			return false;
		}
	}

	public static void showOtherPropertyDialog( edu.cmu.cs.stage3.alice.core.Property property, PopupItemFactory factory ) {
		showOtherPropertyDialog( property, factory, null );
	}

	public static void showOtherPropertyDialog( edu.cmu.cs.stage3.alice.core.Property property, PopupItemFactory factory, java.awt.Point location ) {
		showOtherPropertyDialog( property, factory, location, null );
	}

	public static void showOtherPropertyDialog( edu.cmu.cs.stage3.alice.core.Property property, PopupItemFactory factory, java.awt.Point location, Class valueClass ) {
		if( valueClass == null ) {
			valueClass = property.getValueClass();
		}
		if( Number.class.isAssignableFrom( valueClass ) ) {
			String initialValue = "";
			if( property.getValue() != null ) {
				String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
				Object userRepr = property.getOwner().data.get( propertyKey );
				if( userRepr instanceof String ) {
					initialValue = (String)userRepr;
				} else {
					initialValue = property.getValue().toString();
				}
			}
			edu.cmu.cs.stage3.swing.numpad.NumPad numPad = new edu.cmu.cs.stage3.swing.numpad.NumPad();
			numPad.setNumberString( initialValue );
			numPad.selectAll();
			int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( numPad );
			if( result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION ) {
				String numberString = numPad.getNumberString();
				Double number = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble( numberString );
				((Runnable)factory.createItem( number )).run();
				String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
				property.getOwner().data.put( propertyKey, numberString );
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, number );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom( valueClass ) ) {
			System.out.println( "Not supported yet" );
		} else if( edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom( valueClass ) ) {
			java.awt.Color currentColor = java.awt.Color.white;
			if( property.getValue() instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
				currentColor = ((edu.cmu.cs.stage3.alice.scenegraph.Color)property.getValue()).createAWTColor();
			}
			javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
			java.awt.Color color = edu.cmu.cs.stage3.swing.DialogManager.showDialog( colorChooser, "Custom Color", currentColor );
			if( color != null ) {
				((Runnable)factory.createItem( new edu.cmu.cs.stage3.alice.scenegraph.Color( color ) )).run();
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, new edu.cmu.cs.stage3.alice.scenegraph.Color( color ) );
			}
		} else if( String.class.isAssignableFrom( valueClass ) ) {
			Object currentValue = property.getValue();
			String currentString = "";
			if (currentValue != null){
				currentString = currentValue.toString();
			}
			String string = (String)edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Enter a string:", "Enter a string", javax.swing.JOptionPane.PLAIN_MESSAGE, null, null, currentString );
			if( string != null ) {
				((Runnable)factory.createItem( string )).run();
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, string );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( valueClass ) ) {
			String script = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Please enter a jython script that will evaluate to a response:", "Custom Response Script", javax.swing.JOptionPane.PLAIN_MESSAGE );
			edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse scriptResponse = new edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse();
			scriptResponse.script.set( script );
			property.getOwner().addChild( scriptResponse );
			((Runnable)factory.createItem( scriptResponse )).run();
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Other... is not supported for " + valueClass.getName(), null ); // should not see this
		}
	}

	// this is not very clean
	public static void showOtherDialog( Class valueClass, Object initialValue, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element anchorForAnonymousItems ) {
		if( (initialValue != null) && (! valueClass.isAssignableFrom( initialValue.getClass() )) ) {
			initialValue = null;
		}

		if( Number.class.isAssignableFrom( valueClass ) ) {
			if( initialValue == null ) {
				initialValue = "1";
			}
			edu.cmu.cs.stage3.swing.numpad.NumPad numPad = new edu.cmu.cs.stage3.swing.numpad.NumPad();
			numPad.setNumberString( (String)initialValue );
			numPad.selectAll();
			int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( numPad );
			if( result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION ) {
				String numberString = numPad.getNumberString();
				Double number = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble( numberString );
				((Runnable)factory.createItem( number )).run();
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, number );
//				String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
//				property.getOwner().data.put( propertyKey, numberString );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom( valueClass ) ) {
//			System.out.println( "Not supported yet" );
		} else if( edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom( valueClass ) ) {
			java.awt.Color currentColor = java.awt.Color.white;
			if( initialValue != null ) {
				currentColor = ((edu.cmu.cs.stage3.alice.scenegraph.Color)initialValue).createAWTColor();
			}
			javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
			java.awt.Color color = edu.cmu.cs.stage3.swing.DialogManager.showDialog( colorChooser, "Custom Color", currentColor );
			if (color != null){
				((Runnable)factory.createItem( new edu.cmu.cs.stage3.alice.scenegraph.Color( color ) )).run();
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, new edu.cmu.cs.stage3.alice.scenegraph.Color( color ) );
			}
		} else if( String.class.isAssignableFrom( valueClass ) ) {
			String currentString = "";
			if( initialValue != null ) {
				currentString = (String)initialValue;
			}
			String string = (String)edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Enter a string:", "Enter a string", javax.swing.JOptionPane.PLAIN_MESSAGE, null, null, currentString );
			if( string != null ) {
				((Runnable)factory.createItem( string )).run();
				PopupMenuUtilities.addRecentlyUsedValue( valueClass, string );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( valueClass ) ) {
			String script = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Please enter a jython script that will evaluate to a response:", "Custom Response Script", javax.swing.JOptionPane.PLAIN_MESSAGE );
			edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse scriptResponse = new edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse();
			scriptResponse.script.set( script );
			if( anchorForAnonymousItems != null ) {
				anchorForAnonymousItems.addChild( scriptResponse );
			}
			((Runnable)factory.createItem( scriptResponse )).run();
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Other... is not supported for " + valueClass.getName(), null ); // should not see this
		}
	}

	public static void showScriptDefinedPropertyDialog( edu.cmu.cs.stage3.alice.core.Property property, PopupItemFactory factory ) {
		Class valueClass = property.getValueClass();
		String initialValue = "";
		if( property.get() instanceof edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject ) {
			initialValue = ((edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject)property.get()).evalScript.getStringValue();
		}
		String script = (String)edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Please enter a jython script that will evaluate to the appropriate type:", "Script Expression", javax.swing.JOptionPane.PLAIN_MESSAGE, null, null, initialValue );
		if( script != null ) {
			edu.cmu.cs.stage3.alice.core.question.ScriptDefinedObject scriptDefinedObject = new edu.cmu.cs.stage3.alice.core.question.ScriptDefinedObject();
			scriptDefinedObject.valueClass.set( valueClass );
			scriptDefinedObject.evalScript.set( script );
			property.getOwner().addChild( scriptDefinedObject );
			((Runnable)factory.createItem( scriptDefinedObject )).run();
		}
	}
}