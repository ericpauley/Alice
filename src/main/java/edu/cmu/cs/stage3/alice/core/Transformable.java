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

import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public class Transformable extends ReferenceFrame {
	public final ElementArrayProperty parts = new ElementArrayProperty( this, "parts", null, Transformable[].class );
	public final ElementArrayProperty poses = new ElementArrayProperty( this, "poses", null, Pose[].class );
	public final Matrix44Property localTransformation = new Matrix44Property( this, "localTransformation", new edu.cmu.cs.stage3.math.Matrix44() );
	public final VehicleProperty vehicle = new VehicleProperty( this, "vehicle", null );
	public final BooleanProperty isPivotShowing = new BooleanProperty( this, "isPivotShowing", Boolean.FALSE );
	private edu.cmu.cs.stage3.alice.scenegraph.Transformable m_sgTransformable;
	private edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator m_pivotDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator();

	public Transformable() {
		super();
		m_sgTransformable = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
		m_sgTransformable.setBonus( this );
		m_pivotDecorator.setTransformable( this );
	}


//	public void HACK_copyPoseNamed( String poseName, Transformable dst ) {
//		Pose pose = (Pose)getChildNamed( poseName );
//		Pose dstPose = (Pose)pose.createCopyNamed( null, null, null );
//		dstPose.setParent( dst );
//		dstPose.name.set( poseName );
//		dst.poses.add( dstPose );
//	}

    public edu.cmu.cs.stage3.alice.scenegraph.Visual[] getAllSceneGraphVisuals( edu.cmu.cs.stage3.util.HowMuch howMuch ) {
        final java.util.Vector v = new java.util.Vector();
        visit( new edu.cmu.cs.stage3.util.VisitListener() {
                public void visited( Object o ) {
                    if( o instanceof Model ) {
                        v.addElement( ((Model)o).getSceneGraphVisual() );
                    }
                }
            },
            howMuch
        );
        edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[ v.size() ];
        v.copyInto( sgVisuals );
        return sgVisuals;
    }
    public edu.cmu.cs.stage3.alice.scenegraph.Visual[] getAllSceneGraphVisuals() {
        return getAllSceneGraphVisuals( edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS );
    }

    
	protected void internalRelease( int pass ) {
        switch( pass ) {
        case 1:
            m_sgTransformable.setParent( null );
            m_pivotDecorator.internalRelease( 1 );
            break;
        case 2:
            m_sgTransformable.release();
            m_sgTransformable = null;
            m_pivotDecorator.internalRelease( 2 );
            m_pivotDecorator = null;
            break;
        }
        super.internalRelease( pass );
    }

	private Transformable internalGetPartNamed( String nameValue, boolean ignoreCase ) {
		for( int i=0; i<parts.size(); i++ ) {
			Transformable part = (Transformable)parts.get( i );
			if( nameValue!=null ) {
				boolean found;
				if( ignoreCase ) {
					found = nameValue.equalsIgnoreCase( part.name.getStringValue() );
				} else {
					found = nameValue.equals( part.name.getStringValue() );
				}
				if( found ) {
					return part;
				}
			} else {
				if( part.name.getStringValue()==null ) {
					return part;
				}
			}
		}
		return null;
	}

	private Transformable internalGetPartKeyed( String key, int fromIndex, boolean ignoreCase ) {
		if( key.equals( "" ) ) {
			return this;
		} else {
			int toIndex = key.indexOf( SEPARATOR, fromIndex );
			if( toIndex == -1 ) {
				String childName = key.substring( fromIndex );
				return internalGetPartNamed( childName, ignoreCase );
			} else {
				String childName = key.substring( fromIndex, toIndex );
				Transformable child = internalGetPartNamed( childName, ignoreCase );
				if( child != null ) {
					return child.internalGetPartKeyed( key, toIndex+1, ignoreCase );
				} else {
					return null;
				}
			}
		}
	}

	public Element getPartKeyed( String key ) {
		return internalGetPartKeyed( key, 0, false );
	}
	public Element getPartKeyedIgnoreCase( String key ) {
		return internalGetPartKeyed( key, 0, true );
	}

	
	public void addAbsoluteTransformationListener( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener ) {
		m_sgTransformable.addAbsoluteTransformationListener( absoluteTransformationListener );
	}
	
	public void removeAbsoluteTransformationListener( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener ) {
		m_sgTransformable.removeAbsoluteTransformationListener( absoluteTransformationListener );
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphTransformable() {
		return m_sgTransformable;
	}
	
	public edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame() {
		return m_sgTransformable;
	}
	
	public edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer() {
		return m_sgTransformable;
	}

	//todo
	public void syncLocalTransformationPropertyToSceneGraph() {
		javax.vecmath.Matrix4d m = m_sgTransformable.getLocalTransformation();
		localTransformation.set( m );
	}

	
	protected void nameValueChanged( String value ) {
		super.nameValueChanged( value );
		String s = null;
		if( value!=null ) {
			s = value+".m_sgTransformable";
		}
		m_sgTransformable.setName( s );
	}

	
	protected void propertyChanging( Property property, Object value ) {
		if( property == vehicle ) {
			ReferenceFrame vehicleToBe = (ReferenceFrame)value;
			if( vehicleToBe != null ) {
				if( vehicleToBe==this ) {
					throw new RuntimeException( this + " cannot be its own vehicle." );
				}
				//if( vehicleToBe.isDescendantOf( this ) ) {
				if( vehicleToBe.getSceneGraphContainer().isDescendantOf( getSceneGraphContainer() ) ) {
					throw new RuntimeException( this + " cannot have a scenegraph descendant ("+vehicleToBe+") as its vehicle." );
				}
			}
		} else if( property == localTransformation ) {
            if( value == null ) {
                throw new NullPointerException();
            }
		} else if( property == isPivotShowing ) {
			//pass
		} else {
			super.propertyChanging( property, value );
		}
	}
	
	protected void propertyChanged( Property property, Object value ) {
		if( property == vehicle ) {
			if( value!=null ) {
				m_sgTransformable.setParent( ((ReferenceFrame)value).getSceneGraphContainer() );
			} else {
				m_sgTransformable.setParent( null );
			}
		} else if( property == localTransformation ) {
            m_sgTransformable.setLocalTransformation( (javax.vecmath.Matrix4d)value );
		} else if( property == isPivotShowing ) {
			m_pivotDecorator.setIsShowing( (Boolean)value );
		} else {
			super.propertyChanged( property, value );
		}
	}

	public void setVehiclePreservingAbsoluteTransformation( ReferenceFrame vehicleValue ) {
		edu.cmu.cs.stage3.alice.scenegraph.Transformable sgTransformable = getSceneGraphTransformable();
		edu.cmu.cs.stage3.alice.scenegraph.Container sgRoot = sgTransformable.getRoot();
		javax.vecmath.Matrix4d absoluteTransformation = sgTransformable.getAbsoluteTransformation();
		vehicle.set( vehicleValue );
		if( sgRoot instanceof edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame ) {
			sgTransformable.setTransformation( absoluteTransformation, (edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame)sgRoot );
			syncLocalTransformationPropertyToSceneGraph();
		}
	}

	public double getSpatialRelationDistance( SpatialRelation spatialRelation, Transformable object, ReferenceFrame asSeenBy ) {
		try {
			if( asSeenBy==null ) {
				asSeenBy = object;
			}
			edu.cmu.cs.stage3.math.Box subjectBB = getBoundingBox( asSeenBy );
			edu.cmu.cs.stage3.math.Box objectBB = object.getBoundingBox( asSeenBy );
			if( spatialRelation == SpatialRelation.RIGHT_OF ) {
				return subjectBB.getMinimum().x - objectBB.getMaximum().x;
			} else if( spatialRelation == SpatialRelation.LEFT_OF ) {
				return objectBB.getMinimum().x - subjectBB.getMaximum().x;
			} else if( spatialRelation == SpatialRelation.ABOVE ) {
				return subjectBB.getMinimum().y - objectBB.getMaximum().y;
			} else if( spatialRelation == SpatialRelation.BELOW ) {
				return objectBB.getMinimum().y - subjectBB.getMaximum().y;
			} else if( spatialRelation == SpatialRelation.IN_FRONT_OF ) {
				return subjectBB.getMinimum().z - objectBB.getMaximum().z;
			} else if( spatialRelation == SpatialRelation.BEHIND ) {
				return objectBB.getMinimum().z - subjectBB.getMaximum().z;
			} else {
				throw new RuntimeException();
			}
		} catch( NullPointerException npe ) {
			warnln( npe );
			return 0.0;
		}
	}

	public boolean isInSpatialRelationTo( SpatialRelation spatialRelation, Transformable object, ReferenceFrame asSeenBy ) {
		return getSpatialRelationDistance( spatialRelation, object, asSeenBy ) > 0;
		/*
		try {
			if( asSeenBy==null ) {
				asSeenBy = object;
			}
			Box subjectBB = getBoundingBox( asSeenBy );
			Box objectBB = object.getBoundingBox( asSeenBy );
			if( spatialRelation == SpatialRelation.RIGHT_OF ) {
				return subjectBB.getMinimum().x > objectBB.getMaximum().x;
			} else if( spatialRelation == SpatialRelation.LEFT_OF ) {
				return subjectBB.getMaximum().x < objectBB.getMinimum().x;
			} else if( spatialRelation == SpatialRelation.ABOVE ) {
				return subjectBB.getMinimum().y > objectBB.getMaximum().y;
			} else if( spatialRelation == SpatialRelation.BELOW ) {
				return subjectBB.getMaximum().y < objectBB.getMinimum().y;
			} else if( spatialRelation == SpatialRelation.IN_FRONT_OF ) {
				return subjectBB.getMinimum().z > objectBB.getMaximum().z;
			} else if( spatialRelation == SpatialRelation.BEHIND ) {
				return subjectBB.getMaximum().z < objectBB.getMinimum().z;
			} else {
				warnln( spatialRelation );
				return false;
			}
		} catch( NullPointerException npe ) {
			warnln( npe );
			return false;
		}
		*/
	}
	public boolean isRightOf( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.RIGHT_OF, object, asSeenBy );
	}
	public boolean isRightOf( Transformable object ) {
		return isRightOf( object, null );
	}
	public boolean isLeftOf( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.LEFT_OF, object, asSeenBy );
	}
	public boolean isLeftOf( Transformable object ) {
		return isLeftOf( object, null );
	}
	public boolean isAbove( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.ABOVE, object, asSeenBy );
	}
	public boolean isAbove( Transformable object ) {
		return isAbove( object, null );
	}
	public boolean isBelow( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.BELOW, object, asSeenBy );
	}
	public boolean isBelow( Transformable object ) {
		return isBelow( object, null );
	}
	public boolean isInFrontOf( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.IN_FRONT_OF, object, asSeenBy );
	}
	public boolean isInFrontOf( Transformable object ) {
		return isInFrontOf( object, null );
	}
	public boolean isBehind( Transformable object, ReferenceFrame asSeenBy ) {
		return isInSpatialRelationTo( SpatialRelation.BEHIND, object, asSeenBy );
	}
	public boolean isBehind( Transformable object ) {
		return isBehind( object, null );
	}

	public double getVolume() {
		edu.cmu.cs.stage3.math.Sphere sphere = getBoundingSphere();
		if( sphere != null ) {
			double r = sphere.getRadius();
			return r*r;
		} else {
			return 0;
		}
	}

	public edu.cmu.cs.stage3.math.Matrix44 getLocalTransformation() {
		javax.vecmath.Matrix4d localTransformation = m_sgTransformable.getLocalTransformation();
		if( localTransformation!=null ) {
			return new edu.cmu.cs.stage3.math.Matrix44( localTransformation );
		} else {
			return null;
		}
	}
	
	public edu.cmu.cs.stage3.math.Matrix44 getTransformation( javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy ) {
		ReferenceFrame vehicleValue = (ReferenceFrame)vehicle.getValue();
		if( asSeenBy==null ) {
			asSeenBy = vehicleValue;
		}
		if( asSeenBy==vehicleValue && offset==null ) {
			return getLocalTransformation();
		} else {
			return super.getTransformation( offset, asSeenBy );
		}
	}
    private static edu.cmu.cs.stage3.alice.core.Transformable s_getAGoodLookDummy = null;
    protected double getViewingAngleForGetAGoodLookAt() {
        return Math.PI/4;
    }


//	public static javax.vecmath.Matrix4d getAGoodLookAtMatrix( edu.cmu.cs.stage3.alice.core.Transformable transformable, edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera ) {
//		if( (transformable != null) && (camera != null) ) {
//			edu.cmu.cs.stage3.alice.core.Transformable getAGoodLookDummy = new edu.cmu.cs.stage3.alice.core.Transformable();
//			getAGoodLookDummy.vehicle.set( camera.vehicle.get() );
//			edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
//			double radius = bs.getRadius();
//			if( (radius == 0.0) || Double.isNaN( radius ) ) {
//				radius = 1.0;
//			}
//			double theta = Math.min( camera.horizontalViewingAngle.doubleValue(), camera.verticalViewingAngle.doubleValue() );
//			double dist = radius/Math.sin( theta/2.0 );
//			double offset = dist/Math.sqrt( 3.0 );
//			javax.vecmath.Vector3d center = bs.getCenter();
//			if( center == null ) { // this should be unnecessary
//				center = transformable.getPosition();
//			}
//
//			if( center != null ) {
//				if( (! Double.isNaN( center.x ) ) && (! Double.isNaN( center.y ) ) && (! Double.isNaN( center.z ) ) && (! Double.isNaN( offset ) ) ) {
//					getAGoodLookDummy.setPositionRightNow( center.x - offset, center.y + offset, center.z + offset, transformable );
//					getAGoodLookDummy.pointAtRightNow( transformable, new edu.cmu.cs.stage3.math.Vector3( center ) );
//					javax.vecmath.Matrix4d result = getAGoodLookDummy.getLocalTransformation();
//					getAGoodLookDummy.vehicle.set( null );
//					return result;
//				} else {
//					edu.cmu.cs.stage3.alice.authoringtool.util.ErrorDialog.showErrorDialog( "bad bounding sphere center: " + center, null );
//				}
//			} else {
//				edu.cmu.cs.stage3.alice.authoringtool.util.ErrorDialog.showErrorDialog( "bounding sphere returned null center", null );
//			}
//		}
//
//		return null;
//	}

    public javax.vecmath.Matrix4d calculateGoodLookAt( ReferenceFrame target, ReferenceFrame asSeenBy, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
        if( s_getAGoodLookDummy == null ) {
            s_getAGoodLookDummy = new edu.cmu.cs.stage3.alice.core.Transformable();
			s_getAGoodLookDummy.name.set( "s_getAGoodLookDummy" );
        }

        //todo: rework all of this
        //Transformable targetTrans = (Transformable)target;
        if( target instanceof Transformable ) {
			s_getAGoodLookDummy.vehicle.set( ((Transformable)target).vehicle.get() );
        } else {
			s_getAGoodLookDummy.vehicle.set( target );
        }
        edu.cmu.cs.stage3.math.Sphere bs = target.getBoundingSphere();
        javax.vecmath.Vector3d center = bs.getCenter();
        double radius = bs.getRadius();
        if( center == null ) { // this should not be necessary
            center = target.getPosition();
        }
        if( (radius == 0.0) || Double.isNaN( radius ) ) {
            radius = 1.0;
        }

        double theta = getViewingAngleForGetAGoodLookAt();
        double dist = radius/Math.sin( theta/2.0 );
        double offset = dist/Math.sqrt( 3.0 );
                
        s_getAGoodLookDummy.setPositionRightNow( center.x - offset, center.y + offset, center.z + offset, target );
        s_getAGoodLookDummy.pointAtRightNow( target, center, null, asSeenBy );

		javax.vecmath.Matrix4d m = s_getAGoodLookDummy.getTransformation( asSeenBy );
        s_getAGoodLookDummy.vehicle.set( null );
        return m;
    }
    public javax.vecmath.Matrix4d calculateGoodLookAt( ReferenceFrame target, ReferenceFrame asSeenBy ) {
        return calculateGoodLookAt( target, asSeenBy, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS );
    }
    public javax.vecmath.Matrix4d calculateGoodLookAt( ReferenceFrame target ) {
        return calculateGoodLookAt( target, null );
    }
    public void getAGoodLookAtRightNow( ReferenceFrame target, ReferenceFrame asSeenBy, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
        setTransformationRightNow( calculateGoodLookAt( target, asSeenBy, howMuch ), asSeenBy );
    }
    public void getAGoodLookAtRightNow( ReferenceFrame target, ReferenceFrame asSeenBy ) {
        getAGoodLookAtRightNow( target, asSeenBy, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS );
    }
    public void getAGoodLookAtRightNow( ReferenceFrame target ) {
        getAGoodLookAtRightNow( target, null );
    }

	public edu.cmu.cs.stage3.math.Matrix44 calculateTransformation( javax.vecmath.Matrix4d m, ReferenceFrame asSeenBy ) {
		ReferenceFrame vehicleValue = (ReferenceFrame)vehicle.getValue();
		if( asSeenBy==null ) {
			asSeenBy = vehicleValue;
		}
		if( asSeenBy==vehicleValue ) {
			return new edu.cmu.cs.stage3.math.Matrix44( m );
		} else {
			javax.vecmath.Matrix4d asSeenByAbsolute = asSeenBy.getSceneGraphReferenceFrame().getAbsoluteTransformation();
			javax.vecmath.Matrix4d vehicleInverseAbsolute = vehicleValue.getSceneGraphReferenceFrame().getInverseAbsoluteTransformation();
			return edu.cmu.cs.stage3.math.Matrix44.multiply( m, edu.cmu.cs.stage3.math.Matrix44.multiply( asSeenByAbsolute, vehicleInverseAbsolute ) );
		}
	}
	public static edu.cmu.cs.stage3.math.Matrix33 calculateOrientation( javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide ) {
		return edu.cmu.cs.stage3.alice.scenegraph.Transformable.calculateOrientation( forward, upGuide );
	}
	public edu.cmu.cs.stage3.math.Matrix33 calculateStandUp( ReferenceFrame asSeenBy ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		return m_sgTransformable.calculateStandUp( sgAsSeenBy );
	}

	public edu.cmu.cs.stage3.math.Matrix33 calculatePointAt( ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		return m_sgTransformable.calculatePointAt( target.getSceneGraphReferenceFrame(), offset, upGuide, sgAsSeenBy, onlyAffectYaw );
	}
	/** @deprecated */
	public edu.cmu.cs.stage3.math.Matrix33 calculatePointAt( ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy ) {
		return calculatePointAt( target, offset, upGuide, asSeenBy, false );
	}
	public static edu.cmu.cs.stage3.math.Vector3 calculateResizeScale( Dimension dimension, double amount, boolean likeRubber ) {
		double squash;
		if( likeRubber ) {
			squash = 1.0/Math.sqrt( amount );
		} else {
			squash = 1;
		}
		edu.cmu.cs.stage3.math.Vector3 scale = edu.cmu.cs.stage3.math.Vector3.multiply( dimension.getScaleAxis(), amount );
		if( scale.x==0 ) {
			scale.x = squash;
		}
		if( scale.y==0 ) {
			scale.y = squash;
		}
		if( scale.z==0 ) {
			scale.z = squash;
		}
		return scale;
	}



    public void setAbsoluteTransformationRightNow( javax.vecmath.Matrix4d m ) {
        m_sgTransformable.setAbsoluteTransformation( m );
        syncLocalTransformationPropertyToSceneGraph();
    }

	public void setLocalTransformationRightNow( javax.vecmath.Matrix4d m ) {
		m_sgTransformable.setLocalTransformation( m );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void setTransformationRightNow( javax.vecmath.Matrix4d m, ReferenceFrame asSeenBy ) {
		m_sgTransformable.setLocalTransformation( calculateTransformation( m, asSeenBy ) );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void setPositionRightNow( javax.vecmath.Vector3d position, ReferenceFrame asSeenBy ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		m_sgTransformable.setPosition( position, sgAsSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	//todo
	public void placeOnRightNow( ReferenceFrame landmark, javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy ) {
		if( asSeenBy==null ) {
			asSeenBy = ReferenceFrame.ABSOLUTE;
		}
		javax.vecmath.Vector3d a = getBoundingBox( asSeenBy ).getCenterOfBottomFace();
		javax.vecmath.Vector3d b = landmark.getBoundingBox( asSeenBy ).getCenterOfTopFace();
		javax.vecmath.Vector3d v = edu.cmu.cs.stage3.math.MathUtilities.subtract( b, a );
		if( offset!=null ) {
			v.add( offset );
		}
		moveRightNow( v, asSeenBy );
	}
	public void placeOnRightNow( ReferenceFrame landmark, double[] offset, ReferenceFrame asSeenBy ) {
		placeOnRightNow( landmark, new javax.vecmath.Vector3d( offset ), asSeenBy );
	}
	public void placeOnRightNow( ReferenceFrame landmark, double x, double y, double z, ReferenceFrame asSeenBy ) {
		placeOnRightNow( landmark, new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
	}
	public void placeOnRightNow( ReferenceFrame landmark, javax.vecmath.Vector3d offset ) {
		placeOnRightNow( landmark, offset, null );
	}
	public void placeOnRightNow( ReferenceFrame landmark, double[] offset ) {
		placeOnRightNow( landmark, new javax.vecmath.Vector3d( offset ) );
	}
	public void placeOnRightNow( ReferenceFrame landmark, double x, double y, double z ) {
		placeOnRightNow( landmark, new javax.vecmath.Vector3d( x, y, z ) );
	}
	public void placeOnRightNow( ReferenceFrame landmark ) {
		placeOnRightNow( landmark, (javax.vecmath.Vector3d)null );
	}

	public void setOrientationRightNow( javax.vecmath.Matrix3d axes, ReferenceFrame asSeenBy ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		m_sgTransformable.setAxes( axes, sgAsSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void setOrientationRightNow( edu.cmu.cs.stage3.math.Quaternion quaternion, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( quaternion.getMatrix33(), asSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void setOrientationRightNow( edu.cmu.cs.stage3.math.EulerAngles eulerAngles, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( edu.cmu.cs.stage3.math.EulerAngles.revolutionsToRadians( eulerAngles ).getMatrix33(), asSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void setOrientationRightNow( javax.vecmath.Vector3d[] orientation, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( orientation[0], orientation[1], asSeenBy );
	}
	public void setOrientationRightNow( javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( calculateOrientation( forward, upGuide ), asSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void pointAtRightNow( ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw ) {
		setOrientationRightNow( calculatePointAt( target, offset, upGuide, asSeenBy, onlyAffectYaw ), asSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void scaleSpaceRightNow( javax.vecmath.Vector3d axis, ReferenceFrame asSeenBy ) {
		if( asSeenBy==null ) {
			asSeenBy = this;
		}
		edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44( getTransformation( asSeenBy ) );
		m.scale( axis );
		setTransformationRightNow( m, asSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}

	protected void scalePositionsOfPosesRightNow( Transformable t, javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy ) {
		for( int i=0; i<poses.size(); i++ ) {
			Pose poseI = (Pose)poses.get( i );
			poseI.scalePositionRightNow( t, this, scale, asSeenBy );
		}
		Element parent = getParent();
		if( parent instanceof Transformable ) {
			((Transformable)parent).scalePositionsOfPosesRightNow( t, scale, asSeenBy );
		}
	}
	protected void scalePositionRightNow( javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy ) {
		javax.vecmath.Matrix4d m = getLocalTransformation();
		m.m30 *= scale.x;
		m.m31 *= scale.y;
		m.m32 *= scale.z;
		setLocalTransformationRightNow( m );
		scalePositionsOfPosesRightNow( this, scale, asSeenBy );
	}

	public void resizeRightNow( javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
		for( int i=0; i<m_sgTransformable.getChildCount(); i++ ) {
			edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = m_sgTransformable.getChildAt( i );
            Object bonus = sgComponent.getBonus();
			if( sgComponent instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable ) {
                if( bonus instanceof Transformable ) {
                    Transformable transformable = (Transformable)bonus;
                    if( transformable != null ) {
                        transformable.scalePositionRightNow( scale, asSeenBy );
                        if( howMuch.getDescend() ) {
                            if( transformable.isFirstClass.booleanValue() && howMuch.getRespectDescendant() ) {
                                //respect descendant
                            } else {
                                transformable.resizeRightNow( scale, asSeenBy );
                            }
                        }
                    }
                }
			} else if( sgComponent instanceof edu.cmu.cs.stage3.alice.scenegraph.Visual ) {
                if( bonus instanceof Model ) {
                    Model model = (Model)bonus;
                    if( model != null ) {
                        if( sgComponent == model.getSceneGraphVisual() ) {
                            model.scaleVisualRightNow( scale, asSeenBy );
                        }
                    }
                }
			}
		}
	}

	public void setSizeRightNow( javax.vecmath.Vector3d size, ReferenceFrame asSeenBy ) {
		//todo... getSize( asSeenBy )
		javax.vecmath.Vector3d prevSize = getSize();
        javax.vecmath.Vector3d deltaSize = new javax.vecmath.Vector3d( 1,1,1 );
        if( size.x != prevSize.x && prevSize.x != 0 ) {
            deltaSize.x = size.x/prevSize.x;
        }
        if( size.y != prevSize.y && prevSize.y != 0 ) {
            deltaSize.y = size.y/prevSize.y;
        }
        if( size.z != prevSize.z && prevSize.z != 0 ) {
            deltaSize.z = size.z/prevSize.z;
        }
        if( edu.cmu.cs.stage3.math.MathUtilities.contains( deltaSize, Double.NaN ) ||
            edu.cmu.cs.stage3.math.MathUtilities.contains( deltaSize, Double.POSITIVE_INFINITY ) ||
            edu.cmu.cs.stage3.math.MathUtilities.contains( deltaSize, Double.NEGATIVE_INFINITY ) ) {
            throw new IllegalArgumentException( "size: " + size + "; previous size: " + prevSize );
        }
		resizeRightNow( deltaSize, asSeenBy );
	}

	public void setScaledSpaceRightNow( javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy ) {
		javax.vecmath.Vector3d prevScale = getScaledSpace( asSeenBy );
		javax.vecmath.Vector3d deltaScale = edu.cmu.cs.stage3.math.Vector3.divide( scale, prevScale );
		scaleSpaceRightNow( deltaScale, asSeenBy );
	}

	public void moveRightNow( javax.vecmath.Vector3d vector, ReferenceFrame asSeenBy, boolean isScaledBySize ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		if( isScaledBySize ) {
			vector = edu.cmu.cs.stage3.math.Vector3.multiply( vector, getSize() );
		}
		m_sgTransformable.translate( vector, sgAsSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void rotateRightNow( javax.vecmath.Vector3d axis, double amount, ReferenceFrame asSeenBy ) {
		edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
		if( asSeenBy!=null ) {
			sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
		} else {
			sgAsSeenBy = null;
		}
		m_sgTransformable.rotate( axis, amount/edu.cmu.cs.stage3.math.Angle.RADIANS, sgAsSeenBy );
		syncLocalTransformationPropertyToSceneGraph();
	}
	public void turnRightNow( Direction direction, double amount, ReferenceFrame asSeenBy ) {
		rotateRightNow( direction.getTurnAxis(), amount, asSeenBy );
	}
	public void rollRightNow( Direction direction, double amount, ReferenceFrame asSeenBy ) {
		rotateRightNow( direction.getRollAxis(), amount, asSeenBy );
	}
	public void standUpRightNow( ReferenceFrame asSeenBy ) {
		setOrientationRightNow( calculateStandUp( asSeenBy), asSeenBy );
	}

	//convenience methods
	public void setPointOfViewRightNow( javax.vecmath.Matrix4d m, ReferenceFrame asSeenBy ) {
		setTransformationRightNow( m, asSeenBy );
	}
	public void setPointOfViewRightNow( javax.vecmath.Matrix4d m ) {
		setPointOfViewRightNow( m, null );
	}
	public void setPointOfViewRightNow( ReferenceFrame asSeenBy ) {
		setPointOfViewRightNow( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), asSeenBy );
	}
	public void setPositionRightNow( javax.vecmath.Vector3d position ) {
		setPositionRightNow( position, null );
	}
	public void setPositionRightNow( double[] xyz ) {
		setPositionRightNow( new javax.vecmath.Vector3d( xyz ), null );
	}
	public void setPositionRightNow( double x, double y, double z, ReferenceFrame asSeenBy ) {
		setPositionRightNow( new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
	}
	public void setPositionRightNow( double x, double y, double z ) {
		setPositionRightNow( x, y, z, null );
	}
	public void setPositionRightNow( ReferenceFrame asSeenBy ) {
		setPositionRightNow( edu.cmu.cs.stage3.math.Vector3.ZERO, asSeenBy );
	}
	public void setOrientationRightNow( javax.vecmath.Matrix3d axes ) {
		setOrientationRightNow( axes, null );
	}
	public void setOrientationRightNow( ReferenceFrame asSeenBy ) {
		setOrientationRightNow( edu.cmu.cs.stage3.math.MathUtilities.getIdentityMatrix3d(), asSeenBy );
	}
	public void setOrientationRightNow( edu.cmu.cs.stage3.math.Quaternion quaternion ) {
		setOrientationRightNow( quaternion, null );
	}
	public void setOrientationRightNow( javax.vecmath.Vector3d[] orientation ) {
		setOrientationRightNow( orientation, null );
	}
	public void setOrientationRightNow( javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide ) {
		setOrientationRightNow( forward, upGuide, null );
	}
	public void setOrientationRightNow( javax.vecmath.Vector3d forward ) {
		setOrientationRightNow( forward, null );
	}
	public void setOrientationRightNow( double[][] orientation, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( orientation[0], orientation[1], asSeenBy );
	}
	public void setOrientationRightNow( double[][] orientation ) {
		setOrientationRightNow( orientation, null );
	}
	public void setOrientationRightNow( double[] forwardXYZ, double[] upGuideXYZ, ReferenceFrame asSeenBy ) {
		setOrientationRightNow( new javax.vecmath.Vector3d( forwardXYZ ), new javax.vecmath.Vector3d( upGuideXYZ ), asSeenBy );
	}
	public void setOrientationRightNow( double[] forwardXYZ, double[] upGuideXYZ ) {
		setOrientationRightNow( forwardXYZ, upGuideXYZ, null );
	}
	public void setOrientationRightNow( double[] forwardXYZ ) {
		setOrientationRightNow( forwardXYZ, null );
	}
	public void setOrientationRightNow( double forwardX, double forwardY, double forwardZ, double upGuideX, double upGuideY, double upGuideZ ) {
		setOrientationRightNow( new javax.vecmath.Vector3d( forwardX, forwardY, forwardZ ), new javax.vecmath.Vector3d( upGuideX, upGuideY, upGuideZ ) );
	}
	public void setOrientationRightNow( double forwardX, double forwardY, double forwardZ ) {
		setOrientationRightNow( new javax.vecmath.Vector3d( forwardX, forwardY, forwardZ ), null );
	}
	public void setOrientationRightNow( edu.cmu.cs.stage3.math.EulerAngles eulerAngles ) {
		setOrientationRightNow( eulerAngles, null );
	}

	public void pointAtRightNow( ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy ) {
		pointAtRightNow( target, offset, upGuide, asSeenBy, false );
	}
	public void pointAtRightNow( ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide ) {
		pointAtRightNow( target, offset, upGuide, null );
	}
	public void pointAtRightNow( ReferenceFrame target, javax.vecmath.Vector3d offset ) {
		pointAtRightNow( target, offset, null );
	}
	public void pointAtRightNow( ReferenceFrame target ) {
		javax.vecmath.Vector3d offset = null;
		pointAtRightNow( target, offset );
	}
	public void pointAtRightNow( ReferenceFrame target, double[] offsetXYZ, double[] upGuideXYZ, ReferenceFrame asSeenBy ) {
		pointAtRightNow( target, new javax.vecmath.Vector3d( offsetXYZ ), new javax.vecmath.Vector3d( upGuideXYZ ), asSeenBy );
	}
	public void pointAtRightNow( ReferenceFrame target, double[] offsetXYZ, double[] upGuideXYZ ) {
		pointAtRightNow( target, offsetXYZ, upGuideXYZ, null );
	}
	public void pointAtRightNow( ReferenceFrame target, double[] offsetXYZ ) {
		pointAtRightNow( target, offsetXYZ, null );
	}

	public void resizeRightNow( javax.vecmath.Vector3d scale, ReferenceFrame asSeenBy ) {
        resizeRightNow( scale, asSeenBy, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS );
    }
	public void resizeRightNow( javax.vecmath.Vector3d scale ) {
        resizeRightNow( scale, null );
    }
	public void resizeRightNow( Dimension dimension, double amount, boolean likeRubber, ReferenceFrame asSeenBy, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
		resizeRightNow( calculateResizeScale( dimension, amount, likeRubber), asSeenBy, howMuch );
	}
	public void resizeRightNow( Dimension dimension, double amount, boolean likeRubber, ReferenceFrame asSeenBy ) {
		resizeRightNow( dimension, amount, likeRubber, asSeenBy, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS );
	}
	public void resizeRightNow( Dimension dimension, double amount, boolean likeRubber ) {
		resizeRightNow( dimension, amount, likeRubber, null );
	}
	public void resizeRightNow( Dimension dimension, double amount ) {
		resizeRightNow( dimension, amount, false );
	}
	public void resizeRightNow( double x, double y, double z, ReferenceFrame asSeenBy ) {
		resizeRightNow( new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
	}
	public void resizeRightNow( double x, double y, double z ) {
		resizeRightNow( x, y, z, null );
	}
	public void resizeRightNow( double s ) {
		resizeRightNow( s, s, s );
	}

	public void setSizeRightNow( javax.vecmath.Vector3d xyz ) {
		setSizeRightNow( xyz, null );
	}
    public void setSizeRightNow( double x, double y, double z, ReferenceFrame asSeenBy ) {
        setSizeRightNow( new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
    }
	public void setSizeRightNow( double x, double y, double z ) {
		setSizeRightNow( x, y, z, null );
	}
    public void setSizeRightNow( double[] xyz, ReferenceFrame asSeenBy ) {
        setSizeRightNow( xyz[ 0 ], xyz[ 1 ], xyz[ 2 ], asSeenBy );
    }
	public void setScaledSpaceRightNow( javax.vecmath.Vector3d xyz ) {
		setScaledSpaceRightNow( xyz, null );
	}
	public void setScaledSpaceRightNow( double x, double y, double z, ReferenceFrame asSeenBy ) {
		setScaledSpaceRightNow( new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
	}
	public void setScaledSpaceRightNow( double x, double y, double z ) {
		setScaledSpaceRightNow( x, y, z, null );
	}
    public void setSizeRightNow( double[] xyz ) {
        setSizeRightNow( xyz[ 0 ], xyz[ 1 ], xyz[ 2 ] );
    }


	public void moveRightNow( Direction direction, double amount, ReferenceFrame asSeenBy, boolean isScaledBySize ) {
		moveRightNow( edu.cmu.cs.stage3.math.Vector3.multiply( direction.getMoveAxis(), amount ), asSeenBy, isScaledBySize );
	}
	public void moveRightNow( Direction direction, double amount, ReferenceFrame asSeenBy ) {
		moveRightNow( direction, amount, asSeenBy, false );
	}
	public void moveRightNow( Direction direction, double amount ) {
		moveRightNow( direction, amount, null );
	}
	public void moveRightNow( javax.vecmath.Vector3d vector, ReferenceFrame asSeenBy ) {
		moveRightNow( vector, asSeenBy, false );
	}
	public void moveRightNow( double[] xyz, ReferenceFrame asSeenBy ) {
		moveRightNow( new javax.vecmath.Vector3d( xyz ), asSeenBy );
	}
	public void moveRightNow( javax.vecmath.Vector3d vector ) {
		moveRightNow( vector, null );
	}
	public void moveRightNow( double[] xyz ) {
		moveRightNow( xyz, null );
	}
	public void moveRightNow( double x, double y, double z, ReferenceFrame asSeenBy ) {
		moveRightNow( new javax.vecmath.Vector3d( x, y, z ), asSeenBy );
	}
	public void moveRightNow( double x, double y, double z ) {
		moveRightNow( x, y, z, null );
	}
	public void rotateRightNow( javax.vecmath.Vector3d axis, double amount ) {
		rotateRightNow( axis, amount, null );
	}
	public void turnRightNow( Direction direction, double amount ) {
		turnRightNow( direction, amount, null );
	}
	public void rollRightNow( Direction direction, double amount ) {
		rollRightNow( direction, amount, null );
	}
	public void standUpRightNow() {
		standUpRightNow( null );
	}


    protected edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( javax.vecmath.Vector3d vector, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
        RenderTarget renderTarget = (RenderTarget)getWorld().getDescendants( RenderTarget.class )[ 0 ];
        //return renderTarget.getRenderer().pick( getSceneGraphTransformable(), vector, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired );
        return renderTarget.getRenderer().pick( ((Camera)this).getSceneGraphCamera(), vector, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired );
    }
    public javax.vecmath.Vector3d preventPassingThroughOtherObjects( javax.vecmath.Vector3d vector, double cushion ) {
        if( vector.z > 0 ) {
	        if( this instanceof Camera ) {
		        
		        Camera camera = (Camera)this;
		        java.awt.Dimension size = camera.renderTarget.getRenderTargetValue().getAWTComponent().getSize();
		        edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = camera.pick( size.width/2, size.height/2 );
		        cushion += camera.nearClippingPlaneDistance.doubleValue();

		        if( pickInfo.getCount() > 0 ) {
		            //System.err.println( pickInfo );
		            javax.vecmath.Vector3d localPos = pickInfo.getLocalPositionAt( 0 );
		            if( localPos != null ) {
		                Model model = (Model)pickInfo.getVisualAt( 0 ).getBonus();
		                javax.vecmath.Vector3d pos = model.transformTo( localPos, this );
		                if( pos.z < vector.z + cushion ) {
		                    int subElement = pickInfo.getSubElementAt( 0 );
		                    edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA = (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)model.geometry.getGeometryValue().getSceneGraphGeometry();
		                    int index = sgITA.getIndices()[ subElement*3 ];
		                    edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertex = sgITA.getVertices()[ index ];
		                    javax.vecmath.Vector4d transformedNormal = model.transformTo( new edu.cmu.cs.stage3.math.Vector4( vertex.normal, 0 ), this );
		                    javax.vecmath.Vector3d normal = new javax.vecmath.Vector3d( transformedNormal.x, transformedNormal.y, transformedNormal.z );
		                    javax.vecmath.Vector3d up = new javax.vecmath.Vector3d();
		                    javax.vecmath.Vector3d slide = new javax.vecmath.Vector3d();
		                    up.y = 1;
		                    normal.normalize();
		                    slide.cross( normal, up );
		                    slide.scale( vector.length() * 0.25  );
		                    if( normal.x < 0 ) {
		                        slide.x = -slide.x;
		                        slide.z = -slide.z;
		                    }
		                    vector = slide;
		                }
	                }
		        }
	        }
        }
        return vector;
    }
	
	public void setPivot( ReferenceFrame pivot ) {
		m_sgTransformable.setPivot( pivot.getSceneGraphReferenceFrame() );
		syncLocalTransformationPropertyToSceneGraph();
		for( int i=0; i<m_sgTransformable.getChildCount(); i++ ) {
			edu.cmu.cs.stage3.alice.scenegraph.Component sgChild = m_sgTransformable.getChildAt( i );
			if( sgChild instanceof edu.cmu.cs.stage3.alice.scenegraph.Transformable ) {
                if( sgChild.getBonus() instanceof Transformable ) {
    				((Transformable)sgChild.getBonus()).syncLocalTransformationPropertyToSceneGraph();
                }
			} else if( sgChild instanceof edu.cmu.cs.stage3.alice.scenegraph.Visual ) {
				edu.cmu.cs.stage3.alice.scenegraph.Geometry sgGeometry = ((edu.cmu.cs.stage3.alice.scenegraph.Visual)sgChild).getGeometry();
				if( sgGeometry instanceof edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray ) {
					HACK_syncPropertyToSceneGraph( "vertices", (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)sgGeometry );
				}
			}
		}
        m_pivotDecorator.markDirty();
	}
}

