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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

public class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer {
    
	protected boolean requiresHierarchyAndAbsoluteTransformationListening() {
		return true;
	}
	
	protected boolean requiresBoundListening() {
		return false;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget() {
		return new OffscreenRenderTarget( this );
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() {
        return new OnscreenRenderTarget( this );
	}
    
	protected void dispatchAbsoluteTransformationChange( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
        edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = (edu.cmu.cs.stage3.alice.scenegraph.Component)absoluteTransformationEvent.getSource();
        ComponentProxy componentProxy = (ComponentProxy)getProxyFor( sgComponent );
        componentProxy.handleAbsoluteTransformationChange();
    }
    
	protected void dispatchBoundChange( edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent ) {
    }
	
	public void dispatchChildAdd( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
        edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container)childrenEvent.getSource();
        ContainerProxy containerProxy = (ContainerProxy)getProxyFor( sgContainer );
        ComponentProxy childProxy = (ComponentProxy)getProxyFor( childrenEvent.getChild() );
        containerProxy.handleChildAdd( childProxy );
	}
	
	public void dispatchChildRemove( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
        edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container)childrenEvent.getSource();
        ContainerProxy containerProxy = (ContainerProxy)getProxyFor( sgContainer );
        ComponentProxy childProxy = (ComponentProxy)getProxyFor( childrenEvent.getChild() );
        containerProxy.handleChildRemove( childProxy );
    }
    
	protected void dispatchHierarchyChange( edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent ) {
    }

    public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent, javax.vecmath.Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
        return null;
    }
	private static final String RENDERER_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.";
	private static final String SCENEGRAPH_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.";
	private static final int SCENEGRAPH_PACKAGE_NAME_COUNT = SCENEGRAPH_PACKAGE_NAME.length();
	
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy createProxyFor( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
		Class sgClass = sgElement.getClass();
		while( sgClass!=null ) {
			String className = sgClass.getName();
			if( className.startsWith( SCENEGRAPH_PACKAGE_NAME ) ) {
				break;
			} else {
				sgClass = sgClass.getSuperclass();
			}
		}
		try {
			Class proxyClass = Class.forName( RENDERER_PACKAGE_NAME+sgClass.getName().substring( SCENEGRAPH_PACKAGE_NAME_COUNT )+"Proxy" );
			return (edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy)proxyClass.newInstance();
		} catch( ClassNotFoundException cnfe ) {
			cnfe.printStackTrace();
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		} catch( InstantiationException ie ) {
			ie.printStackTrace();
		}
		return null;
	}
}
