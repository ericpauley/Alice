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

package edu.cmu.cs.stage3.alice.scenegraph;

/**
 * @author Dennis Cosgrove
 */
public class Visual extends Component {
	public static final Property FRONT_FACING_APPEARANCE_PROPERTY = new Property( Visual.class, "FRONT_FACING_APPEARANCE" );
	public static final Property BACK_FACING_APPEARANCE_PROPERTY = new Property( Visual.class, "BACK_FACING_APPEARANCE" );
	public static final Property GEOMETRY_PROPERTY = new Property( Visual.class, "GEOMETRY" );
	public static final Property SCALE_PROPERTY = new Property( Visual.class, "SCALE" );
	public static final Property IS_SHOWING_PROPERTY = new Property( Visual.class, "IS_SHOWING" );
	public static final Property DISABLED_AFFECTORS_PROPERTY = new Property( Visual.class, "DISABLED_AFFECTORS" );

	private Appearance m_frontFacingAppearance = null;
	private Appearance m_backFacingAppearance = null;
	private Geometry m_geometry = null;
	private javax.vecmath.Matrix3d m_scale = null;
	private boolean m_isShowing = true;
	private Affector[] m_disabledAffectors = null;

	public Visual() {
		m_scale = new javax.vecmath.Matrix3d();
		m_scale.setIdentity();
	}
	
	protected void releasePass1() {
		if( m_frontFacingAppearance != null ) {
			warnln( "WARNING: released visual " + this + " still has front facing appearance " + m_frontFacingAppearance + "." );
			setFrontFacingAppearance( null );
		}
		if( m_backFacingAppearance != null ) {
			warnln( "WARNING: released visual " + this + " still has back facing appearance " + m_frontFacingAppearance + "." );
			setBackFacingAppearance( null );
		}
		if( m_geometry != null ) {
			warnln( "WARNING: released visual " + this + " still has geometry " + m_geometry + "." );
			setGeometry( null );
		}
		if( m_disabledAffectors != null && m_disabledAffectors.length > 0 ) {
			warnln( "WARNING: released visual " + this + " still has disabled affectors: " );
			for( int i=0; i<m_disabledAffectors.length; i++ ) {
				warnln( "\t" + m_disabledAffectors[ i ] );
			}
			setDisabledAffectors( null );
		}
		super.releasePass1();
	}
	public Geometry getGeometry() {
		return m_geometry;
	}
	public void setGeometry( Geometry geometry ) {
		if( notequal( m_geometry, geometry ) ) {
			m_geometry = geometry;
			onPropertyChange( GEOMETRY_PROPERTY );
		}
	}

	public Appearance getFrontFacingAppearance() {
		return m_frontFacingAppearance;
	}
	public void setFrontFacingAppearance( Appearance frontFacingAppearance ) {
		if( notequal( m_frontFacingAppearance, frontFacingAppearance ) ) {
			m_frontFacingAppearance = frontFacingAppearance;
			onPropertyChange( FRONT_FACING_APPEARANCE_PROPERTY );
		}
	}

	public Appearance getBackFacingAppearance() {
		return m_backFacingAppearance;
	}
	public void setBackFacingAppearance( Appearance backFacingAppearance ) {
		if( notequal( m_backFacingAppearance, backFacingAppearance ) ) {
			m_backFacingAppearance = backFacingAppearance;
			onPropertyChange( BACK_FACING_APPEARANCE_PROPERTY );
		}
	}

	public javax.vecmath.Matrix3d getScale() {
		return m_scale;
	}
	public void setScale( javax.vecmath.Matrix3d scale ) {
		if( notequal( m_scale, scale ) ) {
			m_scale = scale;
			onPropertyChange( SCALE_PROPERTY );
		}
	}

	public boolean getIsShowing() {
		return m_isShowing;
	}
	public void setIsShowing( boolean isShowing ) {
		if( m_isShowing!=isShowing ) {
			m_isShowing = isShowing;
			onPropertyChange( IS_SHOWING_PROPERTY );
		}
	}
	public Affector[] getDisabledAffectors() {
		return m_disabledAffectors;
	}
	public void setDisabledAffectors( Affector[] disabledAffectors ) {
		if( notequal( m_disabledAffectors, disabledAffectors ) ) {
			m_disabledAffectors = disabledAffectors;
			onPropertyChange( DISABLED_AFFECTORS_PROPERTY );
		}
	}

	public edu.cmu.cs.stage3.math.Box getBoundingBox() {
		if( m_geometry!=null ) {
			edu.cmu.cs.stage3.math.Box box = m_geometry.getBoundingBox();
			if( box!=null ) {
				box.scale( m_scale );
			}
			return box;
		} else {
			return null;
		}
	}
	public edu.cmu.cs.stage3.math.Sphere getBoundingSphere() {
		if( m_geometry!=null ) {
			edu.cmu.cs.stage3.math.Sphere sphere = m_geometry.getBoundingSphere();
			if( sphere!=null ) {
				sphere.scale( m_scale );
			}
			return sphere;
		} else {
			return null;
		}
	}

	public void transform( javax.vecmath.Matrix4d trans ) {
		Geometry geometry = getGeometry();
		if( geometry!=null ) {
			geometry.transform( trans );
		}
	}

    public boolean isInProjectionVolumeOf( Camera camera ) {
        edu.cmu.cs.stage3.math.Sphere boundingSphere = getBoundingSphere();
        if( boundingSphere != null ) {
            javax.vecmath.Matrix4d cameraProjection = camera.getProjection();
            javax.vecmath.Matrix4d cameraInverse = camera.getInverseAbsoluteTransformation();
            javax.vecmath.Matrix4d absolute = getAbsoluteTransformation();

            javax.vecmath.Matrix4d m =  edu.cmu.cs.stage3.math.MathUtilities.multiply( absolute,
                edu.cmu.cs.stage3.math.MathUtilities.multiply( cameraInverse, cameraProjection )
            );
			javax.vecmath.Vector4d centerV4 = edu.cmu.cs.stage3.math.MathUtilities.multiply( boundingSphere.getCenter(), 1, m );
			javax.vecmath.Vector3d centerV3 = edu.cmu.cs.stage3.math.MathUtilities.createVector3d( centerV4 );
            //double bound = 1 + radiusV3.getLength();
            if( centerV3.x <= 1 && centerV3.x >= -1 ) {
                if( centerV3.y <= 1 && centerV3.y >= -1 ) {
                    //todo
                    if( centerV3.z <= 1 && centerV3.z >= 0 ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
