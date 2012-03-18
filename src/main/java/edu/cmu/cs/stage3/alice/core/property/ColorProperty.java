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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class ColorProperty extends ObjectProperty {
	public ColorProperty( Element owner, String name, edu.cmu.cs.stage3.alice.scenegraph.Color defaultValue ) {
		super( owner, name, defaultValue, edu.cmu.cs.stage3.alice.scenegraph.Color.class );
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Color getColorValue() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Color)getValue();
	}

    
	protected void decodeObject( org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version ) throws java.io.IOException {
        org.w3c.dom.Node redNode = node.getElementsByTagName( "red" ).item( 0 );
        org.w3c.dom.Node greenNode = node.getElementsByTagName( "green" ).item( 0 );
        org.w3c.dom.Node blueNode = node.getElementsByTagName( "blue" ).item( 0 );
        org.w3c.dom.Node alphaNode = node.getElementsByTagName( "alpha" ).item( 0 );
        float red = Float.parseFloat( getNodeText( redNode ) );
        float green = Float.parseFloat( getNodeText( greenNode ) );
        float blue = Float.parseFloat( getNodeText( blueNode ) );
        float alpha = Float.parseFloat( getNodeText( alphaNode ) );
        set( new edu.cmu.cs.stage3.alice.scenegraph.Color( red, green, blue, alpha ) );
    }
    
	protected void encodeObject( org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator ) throws java.io.IOException {
        edu.cmu.cs.stage3.alice.scenegraph.Color color = getColorValue();

        org.w3c.dom.Element redNode = document.createElement( "red" );
        redNode.appendChild( createNodeForString( document, Float.toString( color.getRed() ) ) );
        node.appendChild( redNode );

        org.w3c.dom.Element greenNode = document.createElement( "green" );
        greenNode.appendChild( createNodeForString( document, Float.toString( color.getGreen() ) ) );
        node.appendChild( greenNode );

        org.w3c.dom.Element blueNode = document.createElement( "blue" );
        blueNode.appendChild( createNodeForString( document, Float.toString( color.getBlue() ) ) );
        node.appendChild( blueNode );

        org.w3c.dom.Element alphaNode = document.createElement( "alpha" );
        alphaNode.appendChild( createNodeForString( document, Float.toString( color.getAlpha() ) ) );
        node.appendChild( alphaNode );
    }
}
