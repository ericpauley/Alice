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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt
 */
public class ColorPropertyViewController extends PropertyViewController {
	protected java.awt.Component strut = javax.swing.Box.createHorizontalStrut( 16 );

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean allowExpressions, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		super.set( property, true, allowExpressions, true, omitPropertyName, factory );
		setPopupEnabled( true );
		refreshGUI();
	}
	
	protected String getHTMLColorString(edu.cmu.cs.stage3.alice.scenegraph.Color color){
		String r = Integer.toHexString((int)(color.getRed()*255));
		String g = Integer.toHexString((int)(color.getGreen()*255));
		String b = Integer.toHexString((int)(color.getBlue()*255));
		
		if (r.length() == 1){
			r = "0"+r;
		}
		if (g.length() == 1){
			g = "0"+g;
		}
		if (b.length() == 1){
			b = "0"+b;
		}
		return new String("#"+r+g+b);
	}
	
	protected String getReadableColorString(edu.cmu.cs.stage3.alice.scenegraph.Color color){
		java.text.DecimalFormat numberFormatter = new java.text.DecimalFormat();
		String toReturn = new String("(");
		toReturn += numberFormatter.format(color.getRed());
		toReturn += ", ";
		toReturn += numberFormatter.format(color.getGreen());
		toReturn += ", ";
		toReturn += numberFormatter.format(color.getBlue());
		toReturn += ")";
		return toReturn;
	}
		
	
	public void getHTML(StringBuffer toWriteTo){
		if (property.get() instanceof edu.cmu.cs.stage3.alice.scenegraph.Color){
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)property.get();
			String colorString = getHTMLColorString(color);
			String colorName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getName(color);
			if (colorName == null){
				colorName = getReadableColorString(color);
			}
			toWriteTo.append("<span style=\"background-color:"+colorString+"\"><font color="+colorString+"\">"+colorName+"</font></span>");
		} else{
			for (int i=0; i<this.getComponentCount(); i++){
				toWriteTo.append(edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(this.getComponent(i)));
			}
		}
		
	}
	

	
	protected java.awt.Component getNativeComponent() {
		return strut;
	}

	
	protected Class getNativeClass() {
		return edu.cmu.cs.stage3.alice.scenegraph.Color.class;
	}

	
	protected void updateNativeComponent() {
		setBackground( ((edu.cmu.cs.stage3.alice.scenegraph.Color)property.getValue()).createAWTColor() );
	}

	
	protected void refreshGUI() {
		setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "propertyViewControllerBackground" ) );
		super.refreshGUI();
	}
}
