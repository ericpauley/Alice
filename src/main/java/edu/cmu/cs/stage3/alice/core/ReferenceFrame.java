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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.EulerAngles;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Sphere;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;

public abstract class ReferenceFrame extends Sandbox {
	public static final ReferenceFrame ABSOLUTE = new World();

	public final BooleanProperty eventsStopAscending = new BooleanProperty(this, "eventsStopAscending", null);
	public final BooleanProperty isBoundingBoxShowing = new BooleanProperty(this, "isBoundingBoxShowing", Boolean.FALSE);
	public final BooleanProperty isBoundingSphereShowing = new BooleanProperty(this, "isBoundingSphereShowing", Boolean.FALSE);

	private edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator m_boundingBoxDecorator = new edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator();
	private edu.cmu.cs.stage3.alice.core.decorator.BoundingSphereDecorator m_boundingSphereDecorator = new edu.cmu.cs.stage3.alice.core.decorator.BoundingSphereDecorator();

	public abstract edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame getSceneGraphReferenceFrame();
	public abstract edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer();

	public abstract void addAbsoluteTransformationListener(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener);
	public abstract void removeAbsoluteTransformationListener(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener absoluteTransformationListener);

	public ReferenceFrame() {
		m_boundingBoxDecorator.setReferenceFrame(this);
		m_boundingSphereDecorator.setReferenceFrame(this);
	}

	@Override
	protected void internalRelease(int pass) {
		switch (pass) {
			case 1 :
				m_boundingBoxDecorator.internalRelease(1);
				m_boundingSphereDecorator.internalRelease(1);
				break;
			case 2 :
				m_boundingBoxDecorator.internalRelease(2);
				m_boundingBoxDecorator = null;
				m_boundingSphereDecorator.internalRelease(2);
				m_boundingSphereDecorator = null;
				break;
		}
		super.internalRelease(pass);
	}
	public boolean doEventsStopAscending() {
		return eventsStopAscending.booleanValue(isFirstClass.booleanValue());
	}

	private void isBoundingBoxShowingValueChanged(Boolean value) {
		m_boundingBoxDecorator.setIsShowing(value);
	}
	private void isBoundingSphereShowingValueChanged(Boolean value) {
		m_boundingSphereDecorator.setIsShowing(value);
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == isBoundingBoxShowing) {
			isBoundingBoxShowingValueChanged((Boolean) value);
		} else if (property == isBoundingSphereShowing) {
			isBoundingSphereShowingValueChanged((Boolean) value);
		} else {
			super.propertyChanged(property, value);
		}
	}
	/** @deprecated */
	@Deprecated
	public Matrix44 getAbsoluteTransformation() {
		return new Matrix44(getSceneGraphReferenceFrame().getAbsoluteTransformation());
	}
	public Matrix44 getTransformation(javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy) {
		if (asSeenBy == this && offset == null) {
			return new Matrix44();
		} else {
			edu.cmu.cs.stage3.alice.scenegraph.ReferenceFrame sgAsSeenBy;
			if (asSeenBy != null) {
				sgAsSeenBy = asSeenBy.getSceneGraphReferenceFrame();
			} else {
				sgAsSeenBy = null;
			}
			return getSceneGraphReferenceFrame().getTransformation(offset, sgAsSeenBy);
		}
	}
	public Matrix44 getTransformation(ReferenceFrame asSeenBy) {
		return getTransformation(null, asSeenBy);
	}
	public Vector3 getPosition(javax.vecmath.Vector3d offset, ReferenceFrame asSeenBy) {
		return getTransformation(offset, asSeenBy).getPosition();
	}
	public Vector3 getPosition(ReferenceFrame asSeenBy) {
		return getPosition(null, asSeenBy);
	}

	public Matrix33 getOrientationAsAxes(ReferenceFrame asSeenBy) {
		return getTransformation(asSeenBy).getAxes();
	}
	public Vector3[] getOrientationAsForwardAndUpGuide(ReferenceFrame asSeenBy) {
		Matrix33 axes = getOrientationAsAxes(asSeenBy);
		Vector3[] orientation = {axes.getRow(2), axes.getRow(1)};
		return orientation;
	}
	public Quaternion getOrientationAsQuaternion(ReferenceFrame asSeenBy) {
		return getOrientationAsAxes(asSeenBy).getQuaternion();
	}
	public EulerAngles getOrientationAsEulerAngles(ReferenceFrame asSeenBy) {
		return EulerAngles.radiansToRevolutions(getOrientationAsAxes(asSeenBy).getEulerAngles());
	}
	public Vector3 getScaledSpace(ReferenceFrame asSeenBy) {
		return getOrientationAsAxes(asSeenBy).getScaledSpace();
	}
	public double getDistanceTo(ReferenceFrame other) {
		return Math.sqrt(getDistanceSquaredTo(other));
	}
	public double getDistanceSquaredTo(ReferenceFrame other) {
		Vector3 pos = getPosition(other);
		return pos.getLengthSquared();
	}

	public javax.vecmath.Vector4d transformTo(javax.vecmath.Vector4d xyzw, ReferenceFrame to) {
		return getSceneGraphReferenceFrame().transformTo(xyzw, to.getSceneGraphReferenceFrame());
	}
	public javax.vecmath.Vector3d transformTo(javax.vecmath.Vector3d xyz, ReferenceFrame to) {
		return getSceneGraphReferenceFrame().transformTo(xyz, to.getSceneGraphReferenceFrame());
	}
	public javax.vecmath.Vector3d transformToViewport(Vector3 xyz, Camera camera) {
		javax.vecmath.Vector3d xyzInCamera = transformTo(xyz, camera);
		RenderTarget renderTarget = camera.renderTarget.getRenderTargetValue();
		return renderTarget.getInternal().transformFromCameraToViewport(xyzInCamera, camera.getSceneGraphCamera());
	}

	// convenience functions
	public Matrix44 getTransformation() {
		return getTransformation(null);
	}
	public Matrix44 getPointOfView(ReferenceFrame asSeenBy) {
		return getTransformation(asSeenBy);
	}
	public Matrix44 getPointOfView() {
		return getPointOfView(null);
	}
	public Vector3 getPosition() {
		return getPosition(null);
	}
	public Matrix33 getOrientationAsAxes() {
		return getOrientationAsAxes(null);
	}
	public Vector3[] getOrientationAsForwardAndUpGuide() {
		return getOrientationAsForwardAndUpGuide(null);
	}
	public Quaternion getOrientationAsQuaternion() {
		return getOrientationAsQuaternion(null);
	}
	public EulerAngles getOrientationAsEulerAngles() {
		return getOrientationAsEulerAngles(null);
	}
	public Vector3 getScaledSpace() {
		return getScaledSpace(null);
	}

	public javax.vecmath.Vector3d transformTo(double[] xyz, ReferenceFrame to) {
		return transformTo(new Vector3(xyz), to);
	}
	public javax.vecmath.Vector3d transformTo(double x, double y, double z, ReferenceFrame to) {
		return transformTo(new Vector3(x, y, z), to);
	}
	public javax.vecmath.Vector3d transformToViewport(double[] xyz, Camera camera) {
		return transformToViewport(new Vector3(xyz), camera);
	}
	public javax.vecmath.Vector3d transformToViewport(double x, double y, double z, Camera camera) {
		return transformToViewport(new Vector3(x, y, z), camera);
	}

	/*
	 * private void updateBoundingBox( Box box, Matrix44 m ) { for( int i=0;
	 * i<getChildCount(); i++ ) { Component child = getChildAt( i ); if( child
	 * instanceof Transformable ) { Transformable transformable =
	 * (Transformable)child; transformable.updateBoundingBox( box,
	 * Matrix44.multiply( m, transformable.getLocalTransformation() ) ); } else
	 * if( child instanceof Visual ) { Box localBox =
	 * ((Visual)child).getBoundingBox(); if( localBox!=null ) {
	 * localBox.transform( m ); box.union( localBox ); } } } }
	 * 
	 * private void updateBoundingSphere( Sphere sphere, Matrix44 m ) { for( int
	 * i=0; i<getChildCount(); i++ ) { Component child = getChildAt( i ); if(
	 * child instanceof Transformable ) { Transformable transformable =
	 * (Transformable)child; transformable.updateBoundingSphere( sphere,
	 * Matrix44.multiply( m, transformable.getLocalTransformation() ) ); } else
	 * if( child instanceof Visual ) { Sphere localSphere =
	 * ((Visual)child).getBoundingSphere(); if( localSphere!=null ) {
	 * localSphere.transform( m ); sphere.union( localSphere ); } } } }
	 */

	protected void updateBoundingSphere(Sphere sphere, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		if (howMuch.getDescend()) {
			for (int i = 0; i < getChildCount(); i++) {
				Element child = getChildAt(i);
				if (child instanceof ReferenceFrame) {
					if (howMuch.getRespectDescendant() && child.isFirstClass.booleanValue()) {
						// pass
					} else {
						((ReferenceFrame) child).updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
					}
				}
			}
		}
	}
	protected void updateBoundingBox(Box box, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		if (howMuch.getDescend()) {
			for (int i = 0; i < getChildCount(); i++) {
				Element child = getChildAt(i);
				if (child instanceof ReferenceFrame) {
					if (howMuch.getRespectDescendant() && child.isFirstClass.booleanValue()) {
						// pass
					} else {
						((ReferenceFrame) child).updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
					}
				}
			}
		}
	}

	public Sphere getBoundingSphere(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Sphere sphere = new Sphere(null, Double.NaN);
		// Sphere sphere = new Sphere( new Vector3(), 0 );
		updateBoundingSphere(sphere, asSeenBy, howMuch, ignoreHidden);
		return sphere;
	}
	public Box getBoundingBox(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Box box = new Box(null, null);
		updateBoundingBox(box, asSeenBy, howMuch, ignoreHidden);
		return box;
	}
	public Vector3 getSize(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		Box box = getBoundingBox(asSeenBy, howMuch, ignoreHidden);
		if (box != null) {
			return new Vector3(box.getWidth(), box.getHeight(), box.getDepth());
		} else {
			return new Vector3(0, 0, 0);
		}
	}
	public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		Box box = getBoundingBox(asSeenBy, howMuch, ignoreHidden);
		if (box != null) {
			if (dimension == Dimension.LEFT_TO_RIGHT) {
				return box.getWidth();
			} else if (dimension == Dimension.TOP_TO_BOTTOM) {
				return box.getHeight();
			} else if (dimension == Dimension.FRONT_TO_BACK) {
				return box.getDepth();
			} else {
				throw new IllegalArgumentException(dimension + " is expected to be in [LEFT_TO_RIGHT, TOP_TO_BOTTOM, FRONT_TO_BACK].");
			}
		} else {
			// todo: Double.NaN?
			return 0;
		}
	}

	public double getWidth(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		return getSizeAlongDimension(Dimension.LEFT_TO_RIGHT, asSeenBy, howMuch, ignoreHidden);
	}
	public double getHeight(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		return getSizeAlongDimension(Dimension.TOP_TO_BOTTOM, asSeenBy, howMuch, ignoreHidden);
	}
	public double getDepth(ReferenceFrame asSeenBy, HowMuch howMuch, boolean ignoreHidden) {
		return getSizeAlongDimension(Dimension.FRONT_TO_BACK, asSeenBy, howMuch, ignoreHidden);
	}

	public Sphere getBoundingSphere(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getBoundingSphere(asSeenBy, howMuch, false);
	}
	public Sphere getBoundingSphere(ReferenceFrame asSeenBy) {
		return getBoundingSphere(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public Sphere getBoundingSphere() {
		return getBoundingSphere(null);
	}
	public Box getBoundingBox(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getBoundingBox(asSeenBy, howMuch, false);
	}
	public Box getBoundingBox(ReferenceFrame asSeenBy) {
		return getBoundingBox(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public Box getBoundingBox() {
		return getBoundingBox(null);
	}
	public Vector3 getSize(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getSize(asSeenBy, howMuch, false);
	}
	public Vector3 getSize(ReferenceFrame asSeenBy) {
		return getSize(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public Vector3 getSize() {
		return getSize(null);
	}
	public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getSizeAlongDimension(dimension, asSeenBy, howMuch, false);
	}
	public double getSizeAlongDimension(Dimension dimension, ReferenceFrame asSeenBy) {
		return getSizeAlongDimension(dimension, asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public double getSizeAlongDimension(Dimension dimension) {
		return getSizeAlongDimension(dimension, null);
	}
	public double getWidth(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getWidth(asSeenBy, howMuch, false);
	}
	public double getWidth(ReferenceFrame asSeenBy) {
		return getWidth(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public double getWidth() {
		return getWidth(null);
	}
	public double getHeight(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getHeight(asSeenBy, howMuch, false);
	}
	public double getHeight(ReferenceFrame asSeenBy) {
		return getHeight(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public double getHeight() {
		return getHeight(null);
	}
	public double getDepth(ReferenceFrame asSeenBy, HowMuch howMuch) {
		return getDepth(asSeenBy, howMuch, false);
	}
	public double getDepth(ReferenceFrame asSeenBy) {
		return getDepth(asSeenBy, HowMuch.INSTANCE_AND_PARTS);
	}
	public double getDepth() {
		return getDepth(null);
	}

	// todo: remove
	protected static void HACK_syncPropertyToSceneGraph(String propertyName, edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA) {
		edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ita = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) sgITA.getBonus();
		if (propertyName.equals("vertices")) {
			ita.vertices.set(sgITA.getVertices());
		} else if (propertyName.equals("indices")) {
			ita.indices.set(sgITA.getIndices());
		}
	}

	// //todo: remove
	// private static edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray
	// getSceneGraphIndexedTriangleArray( Object o ) {
	// if( o instanceof Model ) {
	// Model model = (Model)o;
	// Geometry geometry = (Geometry)model.geometry.getValue();
	// if( geometry instanceof
	// edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
	// return
	// ((edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)geometry).getSceneGraphIndexedTriangleArray();
	// }
	// }
	// return null;
	// }
	// //todo... Matrix33
	// public void scaleGeometry( double x, double y, double z, ProgressObserver
	// progressObserver ) {
	// java.util.Vector v = new java.util.Vector();
	// internalSearch( new edu.cmu.cs.stage3.util.InstanceOfCriterion(
	// Transformable.class ), HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v );
	// for( int i=0; i<v.size(); i++ ) {
	// Transformable transformable = (Transformable)v.elementAt( i );
	// if( transformable != this ) {
	// javax.vecmath.Matrix4d m = transformable.getLocalTransformation();
	// m.m30 *= x;
	// m.m31 *= y;
	// m.m32 *= z;
	// transformable.setLocalTransformationRightNow( m );
	// }
	// edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA =
	// getSceneGraphIndexedTriangleArray( transformable );
	// if( sgITA!=null ) {
	// sgITA.scale( x, y, z, null );
	// HACK_syncPropertyToSceneGraph( "vertices", sgITA );
	// }
	// }
	// }
}
