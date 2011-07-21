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

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;

public class BoundingBoxDecorator extends Decorator {
	private edu.cmu.cs.stage3.alice.scenegraph.LineArray m_sgLineArray = null;
	private ReferenceFrame m_referenceFrame = null;

	
	public ReferenceFrame getReferenceFrame() {
		return m_referenceFrame;
	}
	public void setReferenceFrame( ReferenceFrame referenceFrame ) {
		if( referenceFrame != m_referenceFrame ) {
			m_referenceFrame = referenceFrame;
            markDirty();
			updateIfShowing();
		}
	}
    
	public void internalRelease( int pass ) {
        switch( pass ) {
        case 2:
            if( m_sgLineArray != null ) {
                m_sgLineArray.release();
                m_sgLineArray = null;
            }
            break;
        }
        super.internalRelease( pass );
    }

	
	protected void update() {
		super.update();
		edu.cmu.cs.stage3.math.Box box = m_referenceFrame.getBoundingBox();
		if( box==null || box.getMinimum()==null || box.getMaximum()==null ) {
			return;
		}
        boolean requiresVerticesToBeUpdated = isDirty();
		if( m_sgLineArray==null ) {
			m_sgLineArray = new edu.cmu.cs.stage3.alice.scenegraph.LineArray();
			m_sgVisual.setGeometry( m_sgLineArray );
			m_sgLineArray.setBonus( getReferenceFrame() );
            requiresVerticesToBeUpdated = true;
		}
        if( requiresVerticesToBeUpdated ) {
            edu.cmu.cs.stage3.alice.scenegraph.Color color = edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW;
            edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[24];
            javax.vecmath.Vector3d min = box.getMinimum();
            javax.vecmath.Vector3d max = box.getMaximum();
            edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vs = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[8];
            vs[0] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( min.x, min.y, min.z ), null, color, null, null );
            vs[1] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( min.x, min.y, max.z ), null, color, null, null );
            vs[2] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( max.x, min.y, max.z ), null, color, null, null );
            vs[3] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( max.x, min.y, min.z ), null, color, null, null );
            vs[4] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( min.x, max.y, min.z ), null, color, null, null );
            vs[5] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( min.x, max.y, max.z ), null, color, null, null );
            vs[6] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( max.x, max.y, max.z ), null, color, null, null );
            vs[7] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( new javax.vecmath.Point3d( max.x, max.y, min.z ), null, color, null, null );

            int bottom = 0;
            int top = 8;
            int sides = 16;
            for( int lcv=0; lcv<4; lcv++ ) {
                vertices[bottom++] = vs[lcv];
                vertices[bottom++] = vs[(lcv+1)%4];
                vertices[top++] = vs[4+lcv];
                vertices[top++] = vs[4+((lcv+1)%4)];
                vertices[sides++] = vs[lcv];
                vertices[sides++] = vs[lcv+4];
            }
            m_sgLineArray.setVertices( vertices );
        }
        setIsDirty( false );
	}
}
