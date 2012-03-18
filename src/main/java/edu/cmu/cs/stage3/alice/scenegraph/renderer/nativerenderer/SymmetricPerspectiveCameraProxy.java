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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class SymmetricPerspectiveCameraProxy extends CameraProxy {
	protected abstract void onVerticalViewingAngleChange( double value );
	protected abstract void onHorizontalViewingAngleChange( double value );
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera.VERTICAL_VIEWING_ANGLE_PROPERTY ) {
			onVerticalViewingAngleChange( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera.HORIZONTAL_VIEWING_ANGLE_PROPERTY ) {
			onHorizontalViewingAngleChange( ((Double)value).doubleValue() );
		} else {
			super.changed( property, value );
		}
	}
}
