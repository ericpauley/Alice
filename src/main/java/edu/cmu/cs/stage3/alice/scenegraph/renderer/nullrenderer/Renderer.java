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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

public class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer {
	
	protected boolean requiresHierarchyAndAbsoluteTransformationListening() {
		return false;
	}
	
	protected boolean requiresBoundListening() {
		return false;
	}
    
	protected void dispatchPropertyChange( edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent ) {
    }
    
	protected void dispatchRelease( edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent ) {
    }
    
	protected void dispatchAbsoluteTransformationChange( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
    }
    
	protected void dispatchBoundChange( edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent ) {
    }
	
	public void dispatchChildAdd( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
	}
	
	public void dispatchChildRemove( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
    }
    
	protected void dispatchHierarchyChange( edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent ) {
    }
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget() {
		return new OffscreenRenderTarget( this );
	}
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() {
        return new OnscreenRenderTarget( this );
	}
    public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent, javax.vecmath.Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
        return null;
    }
}
