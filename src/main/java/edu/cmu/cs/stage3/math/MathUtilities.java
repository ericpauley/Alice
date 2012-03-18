package edu.cmu.cs.stage3.math;

public class MathUtilities {
	private static final javax.vecmath.Matrix4d IDENTITY_MATRIX_4D = new javax.vecmath.Matrix4d();
	private static final javax.vecmath.Matrix3d IDENTITY_MATRIX_3D = new javax.vecmath.Matrix3d();
	private static final javax.vecmath.Vector3d X_AXIS = new javax.vecmath.Vector3d( 1, 0, 0 );
	private static final javax.vecmath.Vector3d Y_AXIS = new javax.vecmath.Vector3d( 0, 1, 0 );
	private static final javax.vecmath.Vector3d Z_AXIS = new javax.vecmath.Vector3d( 0, 0, 1 );
	private static final javax.vecmath.Vector3d NEGATIVE_X_AXIS = new javax.vecmath.Vector3d( -1, 0, 0 );
	private static final javax.vecmath.Vector3d NEGATIVE_Y_AXIS = new javax.vecmath.Vector3d( 0, -1, 0 );
	private static final javax.vecmath.Vector3d NEGATIVE_Z_AXIS = new javax.vecmath.Vector3d( 0, 0, -1 );

	public static javax.vecmath.Matrix4d getIdentityMatrix4d() {
		IDENTITY_MATRIX_4D.setIdentity();
		return IDENTITY_MATRIX_4D;
	}
	public static javax.vecmath.Matrix3d getIdentityMatrix3d() {
		IDENTITY_MATRIX_3D.setIdentity();
		return IDENTITY_MATRIX_3D;
	}
	
	public static javax.vecmath.Vector3d getXAxis() {
		X_AXIS.x = 1.0;
		X_AXIS.y = X_AXIS.z = 0.0;
		return X_AXIS;
	}
	public static javax.vecmath.Vector3d getYAxis() {
		Y_AXIS.y = 1.0;
		Y_AXIS.x = Y_AXIS.z = 0.0;
		return Y_AXIS;
	}
	public static javax.vecmath.Vector3d getZAxis() {
		Z_AXIS.z = 1.0;
		Z_AXIS.x = Z_AXIS.y = 0.0;
		return Z_AXIS;
	}

	public static javax.vecmath.Vector3d getNegativeXAxis() {
		NEGATIVE_X_AXIS.x = -1.0;
		NEGATIVE_X_AXIS.y = NEGATIVE_X_AXIS.z = 0.0;
		return NEGATIVE_X_AXIS;
	}
	public static javax.vecmath.Vector3d getNegativeYAxis() {
		NEGATIVE_Y_AXIS.y = -1.0;
		NEGATIVE_Y_AXIS.x = NEGATIVE_Y_AXIS.z = 0.0;
		return NEGATIVE_Y_AXIS;
	}
	public static javax.vecmath.Vector3d getNegativeZAxis() {
		NEGATIVE_Z_AXIS.z = 1.0;
		NEGATIVE_Z_AXIS.x = Z_AXIS.y = 0.0;
		return NEGATIVE_Z_AXIS;
	}

	public static javax.vecmath.Matrix4d createIdentityMatrix4d() {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();	
		m.setIdentity();
		return m;
	}
	public static javax.vecmath.Matrix3d createIdentityMatrix3d() {
		javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();	
		m.setIdentity();
		return m;
	}
	public static javax.vecmath.Vector3d createXAxis() {
		return new javax.vecmath.Vector3d( 1, 0, 0 );
	}
	public static javax.vecmath.Vector3d createYAxis() {
		return new javax.vecmath.Vector3d( 0, 1, 0 );
	}
	public static javax.vecmath.Vector3d createZAxis() {
		return new javax.vecmath.Vector3d( 0, 0, 1 );
	}
	public static javax.vecmath.Vector3d createNegativeXAxis() {
		return new javax.vecmath.Vector3d( -1, 0, 0 );
	}
	public static javax.vecmath.Vector3d createNegativeYAxis() {
		return new javax.vecmath.Vector3d( 0, -1, 0 );
	}
	public static javax.vecmath.Vector3d createNegativeZAxis() {
		return new javax.vecmath.Vector3d( 0, 0, -1 );
	}
	
	public static javax.vecmath.Vector3d createVector3d( javax.vecmath.Tuple4d t ) {
		return new javax.vecmath.Vector3d( t.x/t.w, t.y/t.w, t.z/t.w );
	}
	public static javax.vecmath.Vector4d createVector4d( javax.vecmath.Tuple3d t, double tW ) {
		return new javax.vecmath.Vector4d( t.x, t.y, t.z, tW );
	}
	public static javax.vecmath.Point3d createPoint3d( javax.vecmath.Tuple4d t ) {
		return new javax.vecmath.Point3d( t.x/t.w, t.y/t.w, t.z/t.w );
	}
	public static javax.vecmath.Point4d createPoint4d( javax.vecmath.Tuple3d t, double tW ) {
		return new javax.vecmath.Point4d( t.x, t.y, t.z, tW );
	}
		
	public static double getItem( javax.vecmath.Vector3d vector, int i ) {
		switch( i ) {
		case 0:
			return vector.x;
		case 1:
			return vector.y;
		case 2:
			return vector.z;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static void setItem( javax.vecmath.Vector3d vector, int i, double value ) {
		switch( i ) {
		case 0:
			vector.x = value;
			return;
		case 1:
			vector.y = value;
			return;
		case 2:
			vector.z = value;
			return;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static javax.vecmath.Vector3d add( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return new javax.vecmath.Vector3d( a.x+b.x, a.y+b.y, a.z+b.z );
	}
	public static javax.vecmath.Vector3d subtract( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return new javax.vecmath.Vector3d( a.x-b.x, a.y-b.y, a.z-b.z );
	}
	public static javax.vecmath.Vector3d negate( javax.vecmath.Vector3d v ) {
		return new javax.vecmath.Vector3d( -v.x, -v.y, -v.z );
	}
	public static javax.vecmath.Vector3d multiply( javax.vecmath.Vector3d v, double scalar ) {
		return new javax.vecmath.Vector3d( v.x*scalar, v.y*scalar, v.z*scalar );
	}
	public static javax.vecmath.Vector3d multiply( javax.vecmath.Vector3d v, javax.vecmath.Vector3d scalar ) {
		return new javax.vecmath.Vector3d( v.x*scalar.x, v.y*scalar.y, v.z*scalar.z );
	}
	public static javax.vecmath.Vector3d divide( javax.vecmath.Vector3d v, double divisor ) {
		return multiply( v, 1/divisor );
	}
	public static javax.vecmath.Vector3d divide( javax.vecmath.Vector3d numerator, javax.vecmath.Vector3d divisor ) {
		return new javax.vecmath.Vector3d( numerator.x/divisor.x, numerator.y/divisor.y, numerator.z/divisor.z );
	}

	public static javax.vecmath.Vector3d invert( javax.vecmath.Vector3d v ) {
		return new javax.vecmath.Vector3d( 1/v.x, 1/v.y, 1/v.z );
	}
	public static javax.vecmath.Vector3d normalizeV( javax.vecmath.Vector3d v ) {
		javax.vecmath.Vector3d nv = new javax.vecmath.Vector3d( v );
		nv.normalize();
		return nv;
	}

	public static double getLengthSquared( double x, double y, double z ) {
		return x*x + y*y + z*z;
	}
	public static double getLength( double x, double y, double z ) {
		double lengthSquared = getLengthSquared( x, y, z );
		if( lengthSquared==1 ) {
			return 1;
		} else {
			return Math.sqrt( lengthSquared );
		}
	}
	public static double getLengthSquared( javax.vecmath.Vector3d v ) {
		return getLengthSquared( v.x, v.y, v.z );
	}
	public static double getLength( javax.vecmath.Vector3d v ) {
		return getLength( v.x, v.y, v.z );
	}

	public static double dotProduct( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	public static javax.vecmath.Vector3d crossProduct( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return new javax.vecmath.Vector3d( a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x );
	}
	public static javax.vecmath.Vector3d interpolate( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b, double portion ) {
		return new javax.vecmath.Vector3d( a.x+(b.x-a.x)*portion, a.y+(b.y-a.y)*portion, a.z+(b.z-a.z)*portion );
	}

	public static javax.vecmath.Vector3d projectOnto( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return multiply( b, dotProduct( b, a )/dotProduct( b, b ) );
	}
	public static javax.vecmath.Vector3d multiply( javax.vecmath.Matrix3d a, javax.vecmath.Vector3d b ) {
		double x = a.m00*b.x + a.m01*b.y + a.m02*b.z;
		double y = a.m10*b.x + a.m11*b.y + a.m12*b.z;
		double z = a.m20*b.x + a.m21*b.y + a.m22*b.z;
		return new javax.vecmath.Vector3d( x, y, z );
	}
	public static javax.vecmath.Vector3d multiply( javax.vecmath.Vector3d a, javax.vecmath.Matrix4d b ) {
		javax.vecmath.Vector3d ab = new javax.vecmath.Vector3d();
		ab.x = a.x*b.m00 + a.y*b.m10 + a.z*b.m20;
		ab.y = a.x*b.m01 + a.y*b.m11 + a.z*b.m21;
		ab.z = a.x*b.m02 + a.y*b.m12 + a.z*b.m22;
		return ab;
	}

	public static javax.vecmath.Vector3d combine( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b, double asc1, double bsc1 ) {
		javax.vecmath.Vector3d ab = new javax.vecmath.Vector3d();
		ab.x = asc1*a.x + bsc1*b.x;
		ab.y = asc1*a.y + bsc1*b.y;
		ab.z = asc1*a.z + bsc1*b.z;
		return ab;
	}
	
	
	public static double getItem( javax.vecmath.Vector4d vector, int i ) {
		switch( i ) {
		case 0:
			return vector.x;
		case 1:
			return vector.y;
		case 2:
			return vector.z;
		case 3:
			return vector.w;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static void setItem( javax.vecmath.Vector4d vector, int i, double value ) {
		switch( i ) {
		case 0:
			vector.x = value;
			return;
		case 1:
			vector.y = value;
			return;
		case 2:
			vector.z = value;
			return;
		case 3:
			vector.w = value;
			return;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static javax.vecmath.Vector4d negate( javax.vecmath.Vector4d v ) {
		return new javax.vecmath.Vector4d( -v.x, -v.y, -v.z, -v.w );
	}
	public static double dotProduct( javax.vecmath.Vector4d a, javax.vecmath.Vector4d b ) {
		return a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w;
	}

	public static javax.vecmath.Vector4d multiply( double aX, double aY, double aZ, double aW, javax.vecmath.Matrix4d b ) {
		javax.vecmath.Vector4d ab = new javax.vecmath.Vector4d();
		ab.x = aX*b.m00 + aY*b.m10 + aZ*b.m20 + aW*b.m30;
		ab.y = aX*b.m01 + aY*b.m11 + aZ*b.m21 + aW*b.m31;
		ab.z = aX*b.m02 + aY*b.m12 + aZ*b.m22 + aW*b.m32;
		ab.w = aX*b.m03 + aY*b.m13 + aZ*b.m23 + aW*b.m33;
		return ab;
	}
	public static javax.vecmath.Vector4d multiply( javax.vecmath.Vector4d a, javax.vecmath.Matrix4d b ) {
		return multiply( a.x, a.y, a.z, a.w, b );
	}
	public static javax.vecmath.Vector4d multiply( javax.vecmath.Vector3d a, double aW, javax.vecmath.Matrix4d b ) {
		return multiply( a.x, a.y, a.z, aW, b );
	}

	public static javax.vecmath.Vector4d multiply( javax.vecmath.Matrix4d a, double bX, double bY, double bZ, double bW ) {
		javax.vecmath.Vector4d ab = new javax.vecmath.Vector4d();
		ab.x = bX*a.m00 + bY*a.m01 + bZ*a.m02 + bW*a.m03;
		ab.y = bX*a.m10 + bY*a.m11 + bZ*a.m12 + bW*a.m13;
		ab.z = bX*a.m20 + bY*a.m21 + bZ*a.m22 + bW*a.m23;
		ab.w = bX*a.m30 + bY*a.m31 + bZ*a.m32 + bW*a.m33;
		return ab;
	}
	public static javax.vecmath.Vector4d multiply( javax.vecmath.Matrix4d a, javax.vecmath.Vector4d b ) {
		return multiply( a, b.x, b.y, b.z, b.w );
	}
	public static javax.vecmath.Vector4d multiply( javax.vecmath.Matrix4d a, javax.vecmath.Vector3d b, double bW ) {
		return multiply( a, b.x, b.y, b.z, bW );
	}


	public static javax.vecmath.Vector3d getRow( javax.vecmath.Matrix3d m, int i ) {
		switch( i ) {
		case 0:
			return new javax.vecmath.Vector3d( m.m00, m.m01, m.m02 );
		case 1:
			return new javax.vecmath.Vector3d( m.m10, m.m11, m.m12 );
		case 2:
			return new javax.vecmath.Vector3d( m.m20, m.m21, m.m22 );
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static void setRow( javax.vecmath.Matrix3d m, int i, javax.vecmath.Vector3d v ) {
		switch( i ) {
		case 0:
			m.m00 = v.x;
			m.m01 = v.y;
			m.m02 = v.z;
			break;
		case 1:
			m.m10 = v.x;
			m.m11 = v.y;
			m.m12 = v.z;
			break;
		case 2:
			m.m20 = v.x;
			m.m21 = v.y;
			m.m22 = v.z;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static javax.vecmath.Vector3d getColumn( javax.vecmath.Matrix3d m, int i ) {
		switch( i ) {
		case 0:
			return new javax.vecmath.Vector3d( m.m00, m.m10, m.m20 );
		case 1:
			return new javax.vecmath.Vector3d( m.m01, m.m11, m.m21 );
		case 2:
			return new javax.vecmath.Vector3d( m.m02, m.m12, m.m22 );
		default:
			throw new IndexOutOfBoundsException();
		}
	}
	public static void setColumn( javax.vecmath.Matrix3d m, int i, javax.vecmath.Vector3d v ) {
		switch( i ) {
		case 0:
			m.m00 = v.x;
			m.m10 = v.y;
			m.m20 = v.z;
			break;
		case 1:
			m.m01 = v.x;
			m.m11 = v.y;
			m.m21 = v.z;
			break;
		case 2:
			m.m02 = v.x;
			m.m12 = v.y;
			m.m22 = v.z;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}


	public static javax.vecmath.Matrix3d multiply( javax.vecmath.Matrix3d a, javax.vecmath.Matrix3d b ) {
		javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
		m.m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
		m.m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
		m.m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

		m.m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
		m.m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
		m.m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

		m.m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
		m.m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
		m.m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;
		return m;
	}
	
	public static javax.vecmath.Matrix4d multiply( javax.vecmath.Matrix4d a, javax.vecmath.Matrix4d b ) {
		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
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

    public static boolean contains( javax.vecmath.Tuple3d t, double d ) {
        if( Double.isNaN( d ) ) {
            return Double.isNaN( t.x ) || Double.isNaN( t.y ) || Double.isNaN( t.z );
        } else {
            return t.x==d || t.y==d || t.z==d;
        }
    }
}
