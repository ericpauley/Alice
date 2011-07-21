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

package edu.cmu.cs.stage3.alice.core;

/**
 * @author Jason Pratt
 */
public class Pose extends Element {
	public final edu.cmu.cs.stage3.alice.core.property.DictionaryProperty poseMap = new edu.cmu.cs.stage3.alice.core.property.DictionaryProperty( this, "poseMap", null );

	public static Pose manufacturePose( Transformable modelRoot, Transformable poseRoot ) {
		Pose pose = new Pose();
		java.util.Hashtable map = new java.util.Hashtable();
		Transformable[] descendants = (Transformable[])poseRoot.getDescendants( Transformable.class );
		for( int i = 0; i < descendants.length; i++ ) {
			if( (descendants[i] != poseRoot) ) {
				map.put( descendants[i].getKey( modelRoot ), descendants[i].getLocalTransformation() );
			}
		}
		pose.poseMap.set( map );
		return pose;
	}
	
	private java.util.Hashtable HACK_hardMap = new java.util.Hashtable();

	public void HACK_harden() {
		Element parent = getParent();
		HACK_hardMap.clear();
		java.util.Enumeration enum0 = poseMap.keys();
		while( enum0.hasMoreElements() ) {
			String key = (String)enum0.nextElement();
			Object value = poseMap.get( key );
			Element hardKey = parent.getDescendantKeyedIgnoreCase( key );
			if( hardKey != null ) {
				HACK_hardMap.put( hardKey, value );
			} else {
				System.err.println( "COULD NOT HARDEN KEY: " + key );
			}
		}
	}
	
	public void HACK_soften() {
		Element parent = getParent();
		java.util.Dictionary softMap = new java.util.Hashtable();
		java.util.Enumeration enum0 = HACK_hardMap.keys();
		while( enum0.hasMoreElements() ) {
			Element hardKey = (Element)enum0.nextElement();
			Object value = HACK_hardMap.get( hardKey );
			String softKey = hardKey.getKey( parent );
			if( softKey != null ) {
				softMap.put( softKey, value );
			} else {
				System.err.println( "COULD NOT SOFTEN KEY: " + hardKey );
			}
		}
		poseMap.set( softMap );
	}

	public void scalePositionRightNow( Element part, Element modelRoot, javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy ) {
		String key = part.getKey( modelRoot );
		javax.vecmath.Matrix4d prev = (javax.vecmath.Matrix4d)poseMap.get( key );
		if( prev != null ) {
			javax.vecmath.Matrix4d curr = new edu.cmu.cs.stage3.math.Matrix44();
			curr.set( prev );
			curr.m30 *= scale.x;
			curr.m31 *= scale.y;
			curr.m32 *= scale.z;
			poseMap.put( key, curr );
		} else {
			//todo?
		}
	}

	/*
	private String getRelativeKey( edu.cmu.cs.stage3.alice.core.Element root, edu.cmu.cs.stage3.alice.core.Element child ) {
		return child.getKey().substring( 0, child.getKey().indexOf( root.getKey() ) + 1 );
	}
	*/

    public void resize(Element part, Element modelRoot, double ratio) {
        java.util.Enumeration keys = poseMap.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (((String)key).indexOf(part.getKey(modelRoot))!=-1) {
                edu.cmu.cs.stage3.math.Matrix44 transform = (edu.cmu.cs.stage3.math.Matrix44)poseMap.get(key);
                edu.cmu.cs.stage3.math.Vector3 pos = transform.getPosition();
                pos.scale(ratio);
                transform.setPosition(pos);
                poseMap.put(key,transform);
            }
        }
    }
}