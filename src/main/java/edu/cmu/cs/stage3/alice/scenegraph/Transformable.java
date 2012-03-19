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

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;

/**
 * @author Dennis Cosgrove
 */
public class Transformable extends ReferenceFrame {
	public static final Property LOCAL_TRANSFORMATION_PROPERTY = new Property(Transformable.class, "LOCAL_TRANSFORMATION");
	public static final Property IS_FIRST_CLASS_PROPERTY = new Property(Transformable.class, "IS_FIRST_CLASS");
	private javax.vecmath.Matrix4d m_localTransformation = null;
	private boolean m_isFirstClass = true;
	private static Transformable s_calculatePointAtHelperOffset = new Transformable();
	private static Transformable s_calculatePointAtHelperA = new Transformable();
	private static Transformable s_calculatePointAtHelperB = new Transformable();
	static {
		s_calculatePointAtHelperOffset.setName("s_calculatePointAtHelperOffset");
		s_calculatePointAtHelperA.setName("s_calculatePointAtHelperA");
		s_calculatePointAtHelperB.setName("s_calculatePointAtHelperB");
		s_calculatePointAtHelperOffset.setIsHelper(true);
		s_calculatePointAtHelperA.setIsHelper(true);
		s_calculatePointAtHelperB.setIsHelper(true);
	}
	private javax.vecmath.Matrix4d m_absoluteTransformation = null;
	private javax.vecmath.Matrix4d m_inverseAbsoluteTransformation = null;
	private Object m_absoluteTransformationLock = new Object();
	private boolean m_isHelper = false;

	public Transformable() {
		m_localTransformation = new javax.vecmath.Matrix4d();
		m_localTransformation.setIdentity();
	}

	public boolean isHelper() {
		return m_isHelper;
	}
	public void setIsHelper(boolean isHelper) {
		m_isHelper = isHelper;
	}

	public boolean getIsFirstClass() {
		return m_isFirstClass;
	}
	public void setIsFirstClass(boolean isFirstClass) {
		if (m_isFirstClass != isFirstClass) {
			m_isFirstClass = isFirstClass;
			onPropertyChange(IS_FIRST_CLASS_PROPERTY);
		}
	}
	public javax.vecmath.Matrix4d getLocalTransformation() {
		if (m_localTransformation == null) {
			throw new NullPointerException();
		}
		return new javax.vecmath.Matrix4d(m_localTransformation);
	}
	public void setLocalTransformation(javax.vecmath.Matrix4d localTransformation) {
		if (localTransformation == null) {
			throw new NullPointerException();
		}
		if (notequal(m_localTransformation, localTransformation)) {
			// if( Math.abs( localTransformation.m33-1.0 ) > 0.01 ) {
			// System.err.println(
			// "JAVA SCENEGRAH LOCAL: holy corrupt matrix batman " +
			// localTransformation );
			// //throw new RuntimeException( localTransformation.toString() );
			// }
			m_localTransformation = localTransformation;
			onPropertyChange(LOCAL_TRANSFORMATION_PROPERTY);
			onAbsoluteTransformationChange();
		}
	}

	@Override
	public javax.vecmath.Matrix4d getAbsoluteTransformation() {
		synchronized (m_absoluteTransformationLock) {
			if (m_absoluteTransformation == null) {
				Container parent = getParent();
				if (parent != null) {
					m_absoluteTransformation = MathUtilities.multiply(m_localTransformation, parent.getAbsoluteTransformation());
				} else {
					m_absoluteTransformation = new javax.vecmath.Matrix4d(m_localTransformation);
				}
				if (Math.abs(m_absoluteTransformation.m33 - 1.0) > 0.01) {
					System.err.println("JAVA SCENEGRAH LOCAL: holy corrupt matrix batman " + m_absoluteTransformation);
				}
			}
			return new javax.vecmath.Matrix4d(m_absoluteTransformation);
		}
	}

	@Override
	public javax.vecmath.Matrix4d getInverseAbsoluteTransformation() {
		synchronized (m_absoluteTransformationLock) {
			if (m_inverseAbsoluteTransformation == null) {
				m_inverseAbsoluteTransformation = getAbsoluteTransformation();
				try {
					m_inverseAbsoluteTransformation.invert();
				} catch (javax.vecmath.SingularMatrixException sme) {
					System.err.println("cannot invert: " + m_inverseAbsoluteTransformation);
					throw sme;
				}
			}
			return new javax.vecmath.Matrix4d(m_inverseAbsoluteTransformation);
		}
	}

	@Override
	protected void onAbsoluteTransformationChange() {
		super.onAbsoluteTransformationChange();
		synchronized (m_absoluteTransformationLock) {
			m_absoluteTransformation = null;
			m_inverseAbsoluteTransformation = null;
		}
	}

	@Override
	public Matrix44 getTransformation(ReferenceFrame asSeenBy) {
		ReferenceFrame vehicle = (ReferenceFrame) getParent();
		if (asSeenBy == null) {
			asSeenBy = vehicle;
		}
		if (asSeenBy == vehicle) {
			return new Matrix44(getLocalTransformation());
		}
		if (asSeenBy instanceof Scene) {
			return new Matrix44(getAbsoluteTransformation());
		}
		return super.getTransformation(asSeenBy);
	}
	public Matrix44 calculateTransformation(javax.vecmath.Matrix4d m, ReferenceFrame asSeenBy) {
		ReferenceFrame vehicle = (ReferenceFrame) getParent();
		if (asSeenBy == null) {
			asSeenBy = vehicle;
		}
		if (asSeenBy == vehicle) {
			return new Matrix44(m);
		} else {
			javax.vecmath.Matrix4d vehicleInverse;
			if (vehicle != null) {
				vehicleInverse = vehicle.getInverseAbsoluteTransformation();
			} else {
				vehicleInverse = new javax.vecmath.Matrix4d();
				vehicleInverse.setIdentity();
			}
			return Matrix44.multiply(m, Matrix44.multiply(asSeenBy.getAbsoluteTransformation(), vehicleInverse));
		}
	}
	public void setAbsoluteTransformation(javax.vecmath.Matrix4d m) {
		ReferenceFrame vehicle = (ReferenceFrame) getParent();
		setLocalTransformation(MathUtilities.multiply(m, vehicle.getInverseAbsoluteTransformation()));
	}
	public void setTransformation(javax.vecmath.Matrix4d m, ReferenceFrame asSeenBy) {
		setLocalTransformation(calculateTransformation(m, asSeenBy));
	}
	public void setPosition(javax.vecmath.Vector3d position, ReferenceFrame asSeenBy) {
		Matrix33 axes = getAxes(null);
		Matrix44 m = new Matrix44();
		m.setPosition(position);
		m = calculateTransformation(m, asSeenBy);
		m.setAxes(axes);
		setLocalTransformation(m);
	}
	public void setAxes(javax.vecmath.Matrix3d axes, ReferenceFrame asSeenBy) {
		Vector3 translation = getPosition(null);
		Matrix44 m = new Matrix44();
		m.setAxes(axes);
		m = calculateTransformation(m, asSeenBy);
		m.setPosition(translation);
		setLocalTransformation(m);
	}

	public void setQuaternion(Quaternion quaternion, ReferenceFrame asSeenBy) {
		setAxes(quaternion.getMatrix33(), asSeenBy);
	}

	public Matrix33 calculatePointAt(ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw) {
		synchronized (s_calculatePointAtHelperOffset) {
			if (upGuide == null) {
				upGuide = MathUtilities.getYAxis();
			}
			if (asSeenBy == null) {
				asSeenBy = (ReferenceFrame) getParent();
			}
			Matrix44 transform = getTransformation(asSeenBy);
			Vector3 position = transform.getPosition();
			// Vector3 position = new Vector3( transform.m30, transform.m31,
			// transform.m32 );

			ReferenceFrame actualTarget;
			if (offset == null) {
				actualTarget = target;
			} else {
				s_calculatePointAtHelperOffset.setParent(target);
				Matrix44 m = new Matrix44();
				m.m30 = offset.x;
				m.m31 = offset.y;
				m.m32 = offset.z;
				s_calculatePointAtHelperOffset.setLocalTransformation(m);
				actualTarget = s_calculatePointAtHelperOffset;
			}

			Matrix33 result;
			if (onlyAffectYaw) {
				// setup "helperA" with the orientation of "asSeenBy" and the
				// position of "this"
				s_calculatePointAtHelperA.setParent(asSeenBy);
				s_calculatePointAtHelperA.setLocalTransformation(new Matrix44());
				s_calculatePointAtHelperA.setPosition(Vector3.ZERO, this);

				// calculate the angle of rotation around y of "actualTarget" as
				// seen by "helperA"
				Vector3 targetPosition = actualTarget.getPosition(s_calculatePointAtHelperA);
				double targetTheta = Math.atan2(targetPosition.x, targetPosition.z);

				// place "helperB" out in front of "this"
				s_calculatePointAtHelperB.setParent(this);
				s_calculatePointAtHelperB.setPosition(MathUtilities.getZAxis(), this);

				// calculate the angle of rotation around Y of "helperB" as seen
				// by "helperA"
				Vector3 forwardPosition = s_calculatePointAtHelperB.getPosition(s_calculatePointAtHelperA);
				double forwardTheta = Math.atan2(forwardPosition.x, forwardPosition.z);

				// setup "helperB" to have position and orientation of "this"
				s_calculatePointAtHelperB.setLocalTransformation(new Matrix44());

				// calculate how much to rotate
				double deltaTheta = targetTheta - forwardTheta;

				// rotate "helperB" around Y as seen by "helperA"
				s_calculatePointAtHelperB.rotate(MathUtilities.getYAxis(), deltaTheta, s_calculatePointAtHelperA);

				// extract result
				result = s_calculatePointAtHelperB.getAxes(asSeenBy);

				// clean up
				s_calculatePointAtHelperA.setParent(null);
				s_calculatePointAtHelperB.setParent(null);
			} else {
				javax.vecmath.Vector3d targetPosition = actualTarget.getPosition(asSeenBy);
				javax.vecmath.Vector3d zAxis = MathUtilities.normalizeV(MathUtilities.subtract(targetPosition, position));
				javax.vecmath.Vector3d xAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(upGuide, zAxis));
				if (Double.isNaN(xAxis.lengthSquared())) {
					xAxis.set(0, 0, 0);
					zAxis.set(0, 0, 0);
					// throw new RuntimeException(
					// "cannot calculate point at: zAxis=" + zAxis + " upGuide="
					// + upGuide );
				}
				javax.vecmath.Vector3d yAxis = MathUtilities.crossProduct(zAxis, xAxis);
				result = new Matrix33(xAxis, yAxis, zAxis);
			}

			if (offset == null) {
				s_calculatePointAtHelperOffset.setParent(null);
			}
			return result;
		}
	}
	/** deprecated */
	public Matrix33 calculatePointAt(ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy) {
		return calculatePointAt(target, offset, upGuide, asSeenBy, false);
	}
	public void pointAt(ReferenceFrame target, javax.vecmath.Vector3d offset, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy) {
		setAxes(calculatePointAt(target, offset, upGuide, asSeenBy), asSeenBy);
	}
	public static Matrix33 calculateOrientation(javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide) {
		if (upGuide == null) {
			upGuide = MathUtilities.getYAxis();
		}
		javax.vecmath.Vector3d zAxis = MathUtilities.normalizeV(forward);
		javax.vecmath.Vector3d xAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(upGuide, zAxis));
		if (Double.isNaN(xAxis.lengthSquared())) {
			throw new RuntimeException("cannot calculate orientation: forward=" + forward + " upGuide=" + upGuide);
		}
		javax.vecmath.Vector3d yAxis = MathUtilities.crossProduct(zAxis, xAxis);
		return new Matrix33(xAxis, yAxis, zAxis);
	}

	public void setOrientation(javax.vecmath.Vector3d forward, javax.vecmath.Vector3d upGuide, ReferenceFrame asSeenBy) {
		setAxes(calculateOrientation(forward, upGuide), asSeenBy);
	}

	public Matrix33 calculateStandUp(ReferenceFrame asSeenBy) {
		Matrix33 axes = getAxes(asSeenBy);
		javax.vecmath.Vector3d yAxis = MathUtilities.getYAxis();
		javax.vecmath.Vector3d zAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(axes.getRow(0), yAxis));
		javax.vecmath.Vector3d xAxis = MathUtilities.crossProduct(yAxis, zAxis);
		return new Matrix33(xAxis, yAxis, zAxis);
	}

	public void standUp(ReferenceFrame asSeenBy) {
		setAxes(calculateStandUp(asSeenBy), asSeenBy);
	}
	public void translate(javax.vecmath.Vector3d vector, ReferenceFrame asSeenBy) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Matrix44 m = getTransformation(asSeenBy);
		m.translate(vector);
		setTransformation(m, asSeenBy);
	}
	public void rotate(javax.vecmath.Vector3d axis, double amount, ReferenceFrame asSeenBy) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Matrix44 m = getTransformation(asSeenBy);
		m.rotate(axis, amount);
		setTransformation(m, asSeenBy);
	}
	public void scale(javax.vecmath.Vector3d axis, ReferenceFrame asSeenBy) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Matrix44 m = getTransformation(asSeenBy);
		m.scale(axis);
		setTransformation(m, asSeenBy);
	}
	public void transform(javax.vecmath.Matrix4d trans, ReferenceFrame asSeenBy) {
		if (asSeenBy == null) {
			asSeenBy = this;
		}
		Matrix44 m = getTransformation(asSeenBy);
		m.transform(trans);
		setTransformation(m, asSeenBy);
	}

	// todo
	/*
	 * public void removeScale() { Vector3 localScale =
	 * m_localTransformation.getScale(); for( int i=0; i<getChildCount(); i++ )
	 * { Component child = getChildAt( i ); if( child instanceof Transformable )
	 * { ((Transformable)child).scale( localScale, this );
	 * ((Transformable)child).removeScale(); } else if( child instanceof Visual
	 * ) { Vector3 visualScale = ((Visual)child).getScale();
	 * visualScale.multiply( localScale ); ((Visual)child).setScale( visualScale
	 * ); } } m_localTransformation.scale( Vector3.invert( localScale ) ); }
	 */
	/*
	 * private void updateBoundingBox( Box box, Matrix44 m ) { for( int i=0;
	 * i<getChildCount(); i++ ) { Component child = getChildAt( i ); if( child
	 * instanceof Transformable ) { Transformable transformable =
	 * (Transformable)child; transformable.updateBoundingBox( box,
	 * Matrix44.multiply( m, transformable.getLocalTransformation() ) ); } else
	 * if( child instanceof Visual ) { Box localBox =
	 * ((Visual)child).getBoundingBox(); if( localBox!=null ) {
	 * localBox.transform( m ); box.union( localBox ); } } } } private void
	 * updateBoundingSphere( Sphere sphere, Matrix44 m ) { for( int i=0;
	 * i<getChildCount(); i++ ) { Component child = getChildAt( i ); if( child
	 * instanceof Transformable ) { Transformable transformable =
	 * (Transformable)child; transformable.updateBoundingSphere( sphere,
	 * Matrix44.multiply( m, transformable.getLocalTransformation() ) ); } else
	 * if( child instanceof Visual ) { Sphere localSphere =
	 * ((Visual)child).getBoundingSphere(); if( localSphere!=null ) {
	 * localSphere.transform( m ); sphere.union( localSphere ); } } } } public
	 * Box getBoundingBox() { Box box = new Box( null, null );
	 * updateBoundingBox( box, Matrix44.IDENTITY ); return box; } public Sphere
	 * getBoundingSphere() { Sphere sphere = new Sphere(); updateBoundingSphere(
	 * sphere, Matrix44.IDENTITY ); if( Double.isNaN( sphere.getRadius() ) ||
	 * sphere.getCenter() == null || Double.isNaN( sphere.getCenter().x ) ) {
	 * sphere.setRadius( 0 ); sphere.setCenter( new Vector3() ); } return
	 * sphere; }
	 */

	public void setPivot(ReferenceFrame pivot) {
		Matrix44 m = getTransformation(pivot);
		Matrix44 mInverse = Matrix44.invert(m);
		transform(mInverse, this);
		for (int i = 0; i < getChildCount(); i++) {
			Component child = getChildAt(i);
			if (child instanceof Transformable) {
				((Transformable) child).transform(m, this);
			} else if (child instanceof Visual) {
				((Visual) child).transform(m);
			}
		}
	}
}
