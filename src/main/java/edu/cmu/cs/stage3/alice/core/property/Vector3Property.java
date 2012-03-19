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

public class Vector3Property extends ObjectProperty {
	public Vector3Property(Element owner, String name, javax.vecmath.Vector3d defaultValue) {
		super(owner, name, defaultValue, javax.vecmath.Vector3d.class);
	}
	public javax.vecmath.Vector3d getVector3dValue() {
		return (javax.vecmath.Vector3d) getValue();
	}
	public edu.cmu.cs.stage3.math.Vector3 getVector3Value() {
		javax.vecmath.Vector3d v = getVector3dValue();
		if (v == null || v instanceof edu.cmu.cs.stage3.math.Vector3) {
			return (edu.cmu.cs.stage3.math.Vector3) v;
		} else {
			return new edu.cmu.cs.stage3.math.Vector3(v);
		}
	}

	@Override
	protected void decodeObject(org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version) throws java.io.IOException {
		org.w3c.dom.Node xNode = node.getElementsByTagName("x").item(0);
		org.w3c.dom.Node yNode = node.getElementsByTagName("y").item(0);
		org.w3c.dom.Node zNode = node.getElementsByTagName("z").item(0);
		float x = Float.parseFloat(getNodeText(xNode));
		float y = Float.parseFloat(getNodeText(yNode));
		float z = Float.parseFloat(getNodeText(zNode));
		set(new javax.vecmath.Vector3d(x, y, z));
	}

	@Override
	protected void encodeObject(org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator) throws java.io.IOException {
		javax.vecmath.Vector3d v = getVector3Value();

		org.w3c.dom.Element xNode = document.createElement("x");
		xNode.appendChild(createNodeForString(document, Double.toString(v.x)));
		node.appendChild(xNode);

		org.w3c.dom.Element yNode = document.createElement("y");
		yNode.appendChild(createNodeForString(document, Double.toString(v.y)));
		node.appendChild(yNode);

		org.w3c.dom.Element zNode = document.createElement("z");
		zNode.appendChild(createNodeForString(document, Double.toString(v.z)));
		node.appendChild(zNode);
	}
}
