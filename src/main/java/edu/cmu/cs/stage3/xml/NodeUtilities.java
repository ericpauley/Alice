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

package edu.cmu.cs.stage3.xml;

public class NodeUtilities {
	public static String getNodeText( org.w3c.dom.Node node ) {
		StringBuffer propertyTextBuffer = new StringBuffer();
		org.w3c.dom.NodeList children = node.getChildNodes();
		for( int j = 0; j < children.getLength(); j++ ) {
			org.w3c.dom.Node childNode = children.item( j );
			if( childNode instanceof org.w3c.dom.CDATASection ) {
				propertyTextBuffer.append( ((org.w3c.dom.CDATASection)childNode).getData() );
			} else if( childNode instanceof org.w3c.dom.Text ) {
				propertyTextBuffer.append( ((org.w3c.dom.Text)childNode).getData().trim() );
			} else if( childNode instanceof org.w3c.dom.EntityReference ) {
				org.w3c.dom.NodeList grandchildren = childNode.getChildNodes();
				for( int k = 0; k < grandchildren.getLength(); k++ ) {
					org.w3c.dom.Node grandchildNode = grandchildren.item( k );
					if( grandchildNode instanceof org.w3c.dom.Text ) {
						propertyTextBuffer.append( ((org.w3c.dom.Text)grandchildNode).getData() );
					}
				}
			}
		}
		return propertyTextBuffer.toString();
	}
}