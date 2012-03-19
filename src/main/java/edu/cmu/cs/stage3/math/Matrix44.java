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

package edu.cmu.cs.stage3.math;

public class Matrix44 extends javax.vecmath.Matrix4d implements Interpolable {
	public static final Matrix44 IDENTITY = new Matrix44();
	public Matrix44() {
		this(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}
	public Matrix44(double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33) {
		m00 = rc00;
		m01 = rc01;
		m02 = rc02;
		m03 = rc03;
		m10 = rc10;
		m11 = rc11;
		m12 = rc12;
		m13 = rc13;
		m20 = rc20;
		m21 = rc21;
		m22 = rc22;
		m23 = rc23;
		m30 = rc30;
		m31 = rc31;
		m32 = rc32;
		m33 = rc33;
	}
	public Matrix44(double[] row0, double[] row1, double[] row2, double row3[]) {
		this(row0[0], row0[1], row0[2], row0[3], row1[0], row1[1], row1[2], row1[3], row2[0], row2[1], row2[2], row2[3], row3[0], row3[1], row3[2], row3[3]);
	}
	public Matrix44(double[] a) {
		this(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]);
	}
	public Matrix44(double[][] m) {
		this(m[0], m[1], m[2], m[3]);
	}
	public Matrix44(javax.vecmath.Matrix4d m) {
		super();
		if (m != null) {
			set(m);
		} else {
			throw new NullPointerException();
		}
	}
	public Matrix44(javax.vecmath.Matrix3d axes, javax.vecmath.Vector3d t) {
		setAxes(axes);
		setPosition(t);
		m33 = 1;
	}
	public Matrix44(AxisAngle aa, Vector3 t) {
		this(aa.getMatrix33(), t);
	}
	public Matrix44(Quaternion q, Vector3 t) {
		this(q.getMatrix33(), t);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof Matrix44) {
			Matrix44 m = (Matrix44) o;
			return m00 == m.m00 && m01 == m.m01 && m02 == m.m02 && m03 == m.m03 && m10 == m.m10 && m11 == m.m11 && m12 == m.m12 && m13 == m.m13 && m20 == m.m20 && m21 == m.m21 && m22 == m.m22 && m23 == m.m23 && m30 == m.m30 && m31 == m.m31 && m32 == m.m32 && m33 == m.m33;
		} else {
			return false;
		}
	}
	public double getItem(int i, int j) {
		switch (i) {
			case 0 :
				switch (j) {
					case 0 :
						return m00;
					case 1 :
						return m01;
					case 2 :
						return m02;
					case 3 :
						return m03;
				}
				break;
			case 1 :
				switch (j) {
					case 0 :
						return m10;
					case 1 :
						return m11;
					case 2 :
						return m12;
					case 3 :
						return m13;
				}
				break;
			case 2 :
				switch (j) {
					case 0 :
						return m20;
					case 1 :
						return m21;
					case 2 :
						return m22;
					case 3 :
						return m23;
				}
				break;
			case 3 :
				switch (j) {
					case 0 :
						return m30;
					case 1 :
						return m31;
					case 2 :
						return m32;
					case 3 :
						return m33;
				}
				break;
		}
		throw new IllegalArgumentException();
	}
	public void setItem(int i, int j, double v) {
		switch (i) {
			case 0 :
				switch (j) {
					case 0 :
						m00 = v;
						return;
					case 1 :
						m01 = v;
						return;
					case 2 :
						m02 = v;
						return;
					case 3 :
						m03 = v;
						return;
				}
				break;
			case 1 :
				switch (j) {
					case 0 :
						m10 = v;
						return;
					case 1 :
						m11 = v;
						return;
					case 2 :
						m12 = v;
						return;
					case 3 :
						m13 = v;
						return;
				}
				break;
			case 2 :
				switch (j) {
					case 0 :
						m20 = v;
						return;
					case 1 :
						m21 = v;
						return;
					case 2 :
						m22 = v;
						return;
					case 3 :
						m23 = v;
						return;
				}
				break;
			case 3 :
				switch (j) {
					case 0 :
						m30 = v;
						return;
					case 1 :
						m31 = v;
						return;
					case 2 :
						m32 = v;
						return;
					case 3 :
						m33 = v;
						return;
				}
				break;
		}
		throw new IllegalArgumentException();
	}
	public Vector4 getRow(int i) {
		switch (i) {
			case 0 :
				return new Vector4(m00, m01, m02, m03);
			case 1 :
				return new Vector4(m10, m11, m12, m13);
			case 2 :
				return new Vector4(m20, m21, m22, m23);
			case 3 :
				return new Vector4(m30, m31, m32, m33);
			default :
				return null;
		}
	}
	public void setRow(int i, Vector4 v) {
		switch (i) {
			case 0 :
				m00 = v.x;
				m01 = v.y;
				m02 = v.z;
				m03 = v.w;
				break;
			case 1 :
				m10 = v.x;
				m11 = v.y;
				m12 = v.z;
				m13 = v.w;
				break;
			case 2 :
				m20 = v.x;
				m21 = v.y;
				m22 = v.z;
				m23 = v.w;
				break;
			case 3 :
				m30 = v.x;
				m31 = v.y;
				m32 = v.z;
				m33 = v.w;
				break;
			default :
				throw new IndexOutOfBoundsException();
		}
	}
	public Vector4 getColumn(int i) {
		switch (i) {
			case 0 :
				return new Vector4(m00, m10, m20, m30);
			case 1 :
				return new Vector4(m01, m11, m21, m31);
			case 2 :
				return new Vector4(m02, m12, m22, m32);
			case 3 :
				return new Vector4(m03, m13, m23, m33);
			default :
				throw new IndexOutOfBoundsException();
		}
	}
	public void setColumn(int i, Vector4 v) {
		switch (i) {
			case 0 :
				m00 = v.x;
				m10 = v.y;
				m20 = v.z;
				m30 = v.w;
				break;
			case 1 :
				m01 = v.x;
				m11 = v.y;
				m21 = v.z;
				m31 = v.w;
				break;
			case 2 :
				m02 = v.x;
				m12 = v.y;
				m22 = v.z;
				m32 = v.w;
				break;
			case 3 :
				m03 = v.x;
				m13 = v.y;
				m23 = v.z;
				m33 = v.w;
				break;
			default :
				throw new IndexOutOfBoundsException();
		}
	}
	public double[] getArray(boolean rowMajor) {
		if (rowMajor) {
			double[] a = {m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33};
			return a;
		} else {
			double[] a = {m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33};
			return a;
		}
	}
	public void setArray(double[] a, boolean rowMajor) {
		if (rowMajor) {
			m00 = a[0];
			m01 = a[1];
			m02 = a[2];
			m03 = a[3];
			m10 = a[4];
			m11 = a[5];
			m12 = a[6];
			m13 = a[7];
			m20 = a[8];
			m21 = a[9];
			m22 = a[10];
			m23 = a[11];
			m30 = a[12];
			m31 = a[13];
			m32 = a[14];
			m33 = a[15];
		} else {
			m00 = a[0];
			m01 = a[4];
			m02 = a[8];
			m03 = a[12];
			m10 = a[1];
			m11 = a[5];
			m12 = a[9];
			m13 = a[13];
			m20 = a[2];
			m21 = a[6];
			m22 = a[10];
			m23 = a[14];
			m30 = a[3];
			m31 = a[7];
			m32 = a[11];
			m33 = a[15];
		}
	}
	public double[][] getMatrix() {
		double[][] m = {{m00, m01, m02, m03}, {m10, m11, m12, m13}, {m20, m21, m22, m23}, {m30, m31, m32, m33}};
		return m;
	}
	public void setMatrix(double[][] m) {
		m00 = m[0][0];
		m01 = m[0][1];
		m02 = m[0][2];
		m03 = m[0][3];
		m10 = m[1][0];
		m11 = m[1][1];
		m12 = m[1][2];
		m13 = m[1][3];
		m20 = m[2][0];
		m21 = m[2][1];
		m22 = m[2][2];
		m23 = m[2][3];
		m30 = m[3][0];
		m31 = m[3][1];
		m32 = m[3][2];
		m33 = m[3][3];
	}
	public void set(Matrix44 other) {
		m00 = other.m00;
		m01 = other.m01;
		m02 = other.m02;
		m03 = other.m03;
		m10 = other.m10;
		m11 = other.m11;
		m12 = other.m12;
		m13 = other.m13;
		m20 = other.m20;
		m21 = other.m21;
		m22 = other.m22;
		m23 = other.m23;
		m30 = other.m30;
		m31 = other.m31;
		m32 = other.m32;
		m33 = other.m33;
	}

	public Vector3 getPosition() {
		return new Vector3(m30, m31, m32);
	}
	public void setPosition(javax.vecmath.Vector3d position) {
		m30 = position.x;
		m31 = position.y;
		m32 = position.z;
	}
	public Matrix33 getAxes() {
		return new Matrix33(m00, m01, m02, m10, m11, m12, m20, m21, m22);
	}
	public void setAxes(javax.vecmath.Matrix3d axes) {
		m00 = axes.m00;
		m01 = axes.m01;
		m02 = axes.m02;
		m10 = axes.m10;
		m11 = axes.m11;
		m12 = axes.m12;
		m20 = axes.m20;
		m21 = axes.m21;
		m22 = axes.m22;
	}

	/*
	 * public Vector3 getScale() { //assuming their is no projection or shear
	 * double x = Vector3.getLength( m00, m01, m02 ); double y =
	 * Vector3.getLength( m10, m11, m12 ); double z = Vector3.getLength( m20,
	 * m21, m22 ); return new Vector3( x, y, z ); }
	 */
	public void translate(javax.vecmath.Vector3d vector) {
		if (vector.x != 0) {
			m00 += m03 * vector.x;
			m10 += m13 * vector.x;
			m20 += m23 * vector.x;
			m30 += m33 * vector.x;
		}
		if (vector.y != 0) {
			m01 += m03 * vector.y;
			m11 += m13 * vector.y;
			m21 += m23 * vector.y;
			m31 += m33 * vector.y;
		}
		if (vector.z != 0) {
			m02 += m03 * vector.z;
			m12 += m13 * vector.z;
			m22 += m23 * vector.z;
			m32 += m33 * vector.z;
		}
	}
	public void rotateX(double angle) {
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		for (int i = 0; i < 4; i++) {
			double tmp = getItem(i, 1);
			setItem(i, 1, tmp * cosAngle - getItem(i, 2) * sinAngle);
			setItem(i, 2, tmp * sinAngle + getItem(i, 2) * cosAngle);
		}
	}
	public void rotateY(double angle) {
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		for (int i = 0; i < 4; i++) {
			double tmp = getItem(i, 0);
			setItem(i, 0, tmp * cosAngle + getItem(i, 2) * sinAngle);
			setItem(i, 2, -tmp * sinAngle + getItem(i, 2) * cosAngle);
		}
	}
	public void rotateZ(double angle) {
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		for (int i = 0; i < 4; i++) {
			double tmp = getItem(i, 0);
			setItem(i, 0, tmp * cosAngle - getItem(i, 1) * sinAngle);
			setItem(i, 1, tmp * sinAngle + getItem(i, 1) * cosAngle);
		}
	}
	public void scale(javax.vecmath.Vector3d vector) {
		if (vector.x != 1) {
			m00 *= vector.x;
			m10 *= vector.x;
			m20 *= vector.x;
			m30 *= vector.x;
		}
		if (vector.y != 1) {
			m01 *= vector.y;
			m11 *= vector.y;
			m21 *= vector.y;
			m31 *= vector.y;
		}
		if (vector.z != 1) {
			m02 *= vector.z;
			m12 *= vector.z;
			m22 *= vector.z;
			m32 *= vector.z;
		}
	}
	public void transform(javax.vecmath.Matrix4d m) {
		set(Matrix44.multiply(this, m));
	}
	public void rotate(javax.vecmath.Vector3d axis, double angle) {
		if (axis.equals(Vector3.X_AXIS)) {
			rotateX(angle);
		} else if (axis.equals(Vector3.Y_AXIS)) {
			rotateY(angle);
		} else if (axis.equals(Vector3.Z_AXIS)) {
			rotateZ(angle);
		} else if (axis.equals(Vector3.X_AXIS_NEGATIVE)) {
			rotateX(-angle);
		} else if (axis.equals(Vector3.Y_AXIS_NEGATIVE)) {
			rotateY(-angle);
		} else if (axis.equals(Vector3.Z_AXIS_NEGATIVE)) {
			rotateZ(-angle);
		} else {
			Matrix44 m = new Matrix44();
			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			m.m00 = axis.x * axis.x + cosAngle * (1.0f - axis.x * axis.x);
			m.m01 = axis.x * axis.y * (1.0f - cosAngle) + axis.z * sinAngle;
			m.m02 = axis.z * axis.x * (1.0f - cosAngle) - axis.y * sinAngle;

			m.m10 = axis.x * axis.y * (1.0f - cosAngle) - axis.z * sinAngle;
			m.m11 = axis.y * axis.y + cosAngle * (1.0f - axis.y * axis.y);
			m.m12 = axis.y * axis.z * (1.0f - cosAngle) + axis.x * sinAngle;

			m.m20 = axis.z * axis.x * (1.0f - cosAngle) + axis.y * sinAngle;
			m.m21 = axis.y * axis.z * (1.0f - cosAngle) - axis.x * sinAngle;
			m.m22 = axis.z * axis.z + cosAngle * (1.0f - axis.z * axis.z);

			transform(m);
		}
	}

	public static Matrix44 multiply(javax.vecmath.Matrix4d a, javax.vecmath.Matrix4d b) {
		Matrix44 m = new Matrix44();
		m.m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30;
		m.m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31;
		m.m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32;
		m.m03 = a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33;

		m.m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30;
		m.m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31;
		m.m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32;
		m.m13 = a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33;

		m.m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30;
		m.m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31;
		m.m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32;
		m.m23 = a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33;

		m.m30 = a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30;
		m.m31 = a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31;
		m.m32 = a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32;
		m.m33 = a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33;
		return m;
	}
	public static Matrix44 transpose(Matrix44 m) {
		Matrix44 mTranspose = new Matrix44();
		mTranspose.m00 = m.m00;
		mTranspose.m01 = m.m10;
		mTranspose.m02 = m.m20;
		mTranspose.m03 = m.m30;
		mTranspose.m10 = m.m01;
		mTranspose.m11 = m.m11;
		mTranspose.m12 = m.m21;
		mTranspose.m13 = m.m31;
		mTranspose.m20 = m.m02;
		mTranspose.m21 = m.m12;
		mTranspose.m22 = m.m22;
		mTranspose.m23 = m.m32;
		mTranspose.m30 = m.m03;
		mTranspose.m31 = m.m13;
		mTranspose.m32 = m.m23;
		mTranspose.m33 = m.m33;
		return mTranspose;
	}
	public void divide(double denom) {
		m00 /= denom;
		m01 /= denom;
		m02 /= denom;
		m03 /= denom;
		m10 /= denom;
		m11 /= denom;
		m12 /= denom;
		m13 /= denom;
		m20 /= denom;
		m21 /= denom;
		m22 /= denom;
		m23 /= denom;
		m30 /= denom;
		m31 /= denom;
		m32 /= denom;
		m33 /= denom;
	}
	// from svl
	private static Vector3 ROW(Vector4 a, Vector4 b, Vector4 c, int index) {
		return new Vector3(a.getItem(index), b.getItem(index), c.getItem(index));
	}
	private static double DET(Vector4 a, Vector4 b, Vector4 c, int i, int j, int k) {
		return Vector3.dotProduct(ROW(a, b, c, i), Vector3.crossProduct(ROW(a, b, c, j), ROW(a, b, c, k)));
	}
	private static Vector4 cross(Vector4 a, Vector4 b, Vector4 c) {
		Vector4 result = new Vector4();
		result.x = DET(a, b, c, 1, 2, 3);
		result.y = -DET(a, b, c, 0, 2, 3);
		result.z = DET(a, b, c, 0, 1, 3);
		result.w = -DET(a, b, c, 0, 1, 2);
		return result;
	}
	public static Matrix44 adjoint(Matrix44 m) {
		Matrix44 mAdjoint = new Matrix44();
		mAdjoint.setRow(0, cross(m.getRow(1), m.getRow(2), m.getRow(3)));
		mAdjoint.setRow(1, Vector4.negate(cross(m.getRow(0), m.getRow(2), m.getRow(3))));
		mAdjoint.setRow(2, cross(m.getRow(0), m.getRow(1), m.getRow(3)));
		mAdjoint.setRow(3, Vector4.negate(cross(m.getRow(0), m.getRow(1), m.getRow(2))));
		return mAdjoint;
	}
	public static Matrix44 invert(Matrix44 m) {
		Matrix44 mInverse = new Matrix44();
		if (Math.abs(m.m03) > 0.001 || Math.abs(m.m13) > 0.001 || Math.abs(m.m23) > 0.001 || Math.abs(m.m33 - 1.0) > 0.001) {
			// from svl
			// handle matrices with projection
			Matrix44 adj = adjoint(m);
			double mDet = Vector4.dotProduct(adj.getRow(0), m.getRow(0));
			if (mDet == 0) {
				throw new SingularityException();
			}
			mInverse = transpose(adj);
			mInverse.divide(mDet);
		} else {
			// from d3dframe
			double fDetInv = 1.0 / (m.m00 * (m.m11 * m.m22 - m.m12 * m.m21) - m.m01 * (m.m10 * m.m22 - m.m12 * m.m20) + m.m02 * (m.m10 * m.m21 - m.m11 * m.m20));

			mInverse.m00 = fDetInv * (m.m11 * m.m22 - m.m12 * m.m21);
			mInverse.m01 = -fDetInv * (m.m01 * m.m22 - m.m02 * m.m21);
			mInverse.m02 = fDetInv * (m.m01 * m.m12 - m.m02 * m.m11);
			mInverse.m03 = 0.0;

			mInverse.m10 = -fDetInv * (m.m10 * m.m22 - m.m12 * m.m20);
			mInverse.m11 = fDetInv * (m.m00 * m.m22 - m.m02 * m.m20);
			mInverse.m12 = -fDetInv * (m.m00 * m.m12 - m.m02 * m.m10);
			mInverse.m13 = 0.0;

			mInverse.m20 = fDetInv * (m.m10 * m.m21 - m.m11 * m.m20);
			mInverse.m21 = -fDetInv * (m.m00 * m.m21 - m.m01 * m.m20);
			mInverse.m22 = fDetInv * (m.m00 * m.m11 - m.m01 * m.m10);
			mInverse.m23 = 0.0;

			mInverse.m30 = -(m.m30 * mInverse.m00 + m.m31 * mInverse.m10 + m.m32 * mInverse.m20);
			mInverse.m31 = -(m.m30 * mInverse.m01 + m.m31 * mInverse.m11 + m.m32 * mInverse.m21);
			mInverse.m32 = -(m.m30 * mInverse.m02 + m.m31 * mInverse.m12 + m.m32 * mInverse.m22);
			mInverse.m33 = 1.0;
		}
		return mInverse;
	}

	public static Matrix44 interpolate(Matrix44 a, Matrix44 b, double portion) {
		Vector3 t = Vector3.interpolate(a.getPosition(), b.getPosition(), portion);
		Matrix33 m = Matrix33.interpolate(a.getAxes(), b.getAxes(), portion);
		return new Matrix44(m, t);
	}
	@Override
	public Interpolable interpolate(Interpolable b, double portion) {
		return interpolate(this, (Matrix44) b, portion);
	}

	@Override
	public String toString() {
		return "edu.cmu.cs.stage3.math.Matrix44[rc00=" + m00 + ",rc01=" + m01 + ",rc02=" + m02 + ",rc03=" + m03 + ",rc10=" + m10 + ",rc11=" + m11 + ",rc12=" + m12 + ",rc13=" + m13 + ",rc20=" + m20 + ",rc21=" + m21 + ",rc22=" + m22 + ",rc23=" + m23 + ",rc30=" + m30 + ",rc31=" + m31 + ",rc32=" + m32 + ",rc33=" + m33 + "]";
	}
	public static Matrix44 valueOf(String s) {
		String[] markers = {"edu.cmu.cs.stage3.math.Matrix44[rc00=", ",rc01=", ",rc02=", ",rc03=", ",rc10=", ",rc11=", ",rc12=", ",rc13=", ",rc20=", ",rc21=", ",rc22=", ",rc23=", ",rc30=", ",rc31=", ",rc32=", ",rc33=", "]"};
		double[] values = new double[markers.length - 1];
		for (int i = 0; i < values.length; i++) {
			int begin = s.indexOf(markers[i]) + markers[i].length();
			int end = s.indexOf(markers[i + 1]);
			String v = s.substring(begin, end);
			Double d = Double.valueOf(v);
			values[i] = d.doubleValue();
		}
		return new Matrix44(values);
	}
}
/*
 * private static double[] negate( double[] a ) { double[] negativeA = new
 * double[a.length]; for( int i=0; i<a.length; i++ ) { negativeA[i] = -a[i]; }
 * return negativeA; } private static void divideEqual( double[][] m, double d )
 * { for( int j=0; j<m.length; j++ ) { for( int i=0; i<m.length; i++ ) { m[j][i]
 * /= d; } } } private static double dotProduct( double[] a, double[] b) {
 * double sum = 0; for( int i=0; i<a.length; i++ ) { sum += a[i]*b[i]; } return
 * sum; } private static double[][] transpose( double[][] m ) { double[][]
 * result = new double[4][4]; for( int i=0; i<4; i++ ) { for( int j=0; j<4; j++
 * ) { result[j][i] = m[i][j]; } } return result; } private static double
 * determinate( double[] a, double[] b, double[] c ) { return dotProduct( a,
 * crossProduct( b, c ) ); } private static double[] crossProduct( double[] a,
 * double[] b ) { double[] result = new double[3]; result[0] = a[1] * b[2] -
 * a[2] * b[1]; result[1] = a[2] * b[0] - a[0] * b[2]; result[2] = a[0] * b[1] -
 * a[1] * b[0]; return result; } private static double[] crossProduct( double[]
 * a, double[] b, double[] c ) { double[] row0 = { a[0], b[0], c[0] }; double[]
 * row1 = { a[1], b[1], c[1] }; double[] row2 = { a[2], b[2], c[2] }; double[]
 * row3 = { a[3], b[3], c[3] }; double[] result = new double[4]; result[0] =
 * determinate( row1, row2, row3 ); result[1] = -determinate( row0, row2, row3
 * ); result[2] = determinate( row0, row1, row3 ); result[3] = -determinate(
 * row0, row1, row2 ); return result; } private static double[][] adjoint(
 * double[][] m ) { double[][] result = new double[4][]; result[0] =
 * crossProduct(m[1], m[2], m[3]); result[1] = negate( crossProduct(m[0], m[2],
 * m[3]) ); result[2] = crossProduct(m[0], m[1], m[3]); result[3] = negate(
 * crossProduct(m[0], m[1], m[2]) ); return result; } public static Matrix44
 * invert( Matrix44 a ) { double[][] m = a.getMatrix(); double[][] adj =
 * adjoint( m ); double det = dotProduct( adj[0], m[0] ); if( det==0 ) throw new
 * SingularityException(); double[][] result = transpose( adj ); divideEqual(
 * result, det ); return new Matrix44( result ); }
 */
