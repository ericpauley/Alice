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
public class Vertex3d implements Cloneable, java.io.Serializable {
	public static final int FORMAT_POSITION = 1;
	public static final int FORMAT_NORMAL = 2;
	public static final int FORMAT_DIFFUSE_COLOR = 4;
	public static final int FORMAT_SPECULAR_HIGHLIGHT_COLOR = 8;
	public static final int FORMAT_TEXTURE_COORDINATE_0 = 16;

	public javax.vecmath.Point3d position = null;
	public javax.vecmath.Vector3d normal = null;
	public Color diffuseColor = null;
	public Color specularHighlightColor = null;
	public javax.vecmath.TexCoord2f textureCoordinate0 = null;

	public Vertex3d() {
	}
	public Vertex3d( int format ) {
		if( (format&FORMAT_POSITION) != 0 ) {
			position = new javax.vecmath.Point3d();
		}
		if( (format&FORMAT_NORMAL) != 0 ) {
			normal = new javax.vecmath.Vector3d();
		}
		if( (format&FORMAT_DIFFUSE_COLOR) != 0 ) {
			diffuseColor = new Color();
		}
		if( (format&FORMAT_SPECULAR_HIGHLIGHT_COLOR) != 0 ) {
			specularHighlightColor = new Color();
		}
		if( (format&FORMAT_TEXTURE_COORDINATE_0) != 0 ) {
			textureCoordinate0 = new javax.vecmath.TexCoord2f();
		}
	}
	public Vertex3d( javax.vecmath.Point3d position, javax.vecmath.Vector3d normal, Color diffuseColor, Color specularHighlightColor, javax.vecmath.TexCoord2f textureCoordinate0 ) {
		this.position = position;
		this.normal = normal;
		this.diffuseColor = diffuseColor;
		this.specularHighlightColor = specularHighlightColor;
		this.textureCoordinate0 = textureCoordinate0;
	}

	public static Vertex3d createXYZIJKUV( double x, double y, double z, double i, double j, double k, float u, float v ) {
		Vertex3d vertex = new Vertex3d();
		vertex.position = new javax.vecmath.Point3d( x, y, z );
		vertex.normal = new javax.vecmath.Vector3d( i, j, k );
		vertex.textureCoordinate0 = new javax.vecmath.TexCoord2f( u, v );
		return vertex;
	}

	
	public synchronized Object clone() {
		try {
			//todo?
			//do i need to make new clones of position, etc?
			return super.clone();
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	
	public boolean equals( Object o ) {
		if( o instanceof Vertex3d ) {
			Vertex3d v = (Vertex3d)o;
			if( v.position == null ) {
				if( position != null ) {
					return false;
				}
			} else {
				if( !v.position.equals( position ) ) {
					return false;
				}
			}
			if( v.normal == null ) {
				if( normal != null ) {
					return false;
				}
			} else {
				if( !v.normal.equals( normal ) ) {
					return false;
				}
			}
			if( v.diffuseColor == null ) {
				if( diffuseColor != null ) {
					return false;
				}
			} else {
				if( !v.diffuseColor.equals( diffuseColor ) ) {
					return false;
				}
			}
			if( v.specularHighlightColor == null ) {
				if( specularHighlightColor != null ) {
					return false;
				}
			} else {
				if( !v.specularHighlightColor.equals( specularHighlightColor ) ) {
					return false;
				}
			}
			if( v.textureCoordinate0 == null ) {
				if( textureCoordinate0 != null ) {
					return false;
				}
			} else {
				if( !v.textureCoordinate0.equals( textureCoordinate0 ) ) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	public int getFormat() {
		int format = 0;
		if( position!=null ) {
			format |= FORMAT_POSITION;
		}
		if( normal!=null ) {
			format |= FORMAT_NORMAL;
		}
		if( diffuseColor!=null ) {
			format |= FORMAT_DIFFUSE_COLOR;
		}
		if( specularHighlightColor!=null ) {
			format |= FORMAT_SPECULAR_HIGHLIGHT_COLOR;
		}
		if( textureCoordinate0!=null ) {
			format |= FORMAT_TEXTURE_COORDINATE_0;
		}
		return format;
	}
	public void translate( double x, double y, double z ) {
		position.x += x;
		position.y += y;
		position.z += z;
	}
	public void scale( double x, double y, double z ) {
		position.x *= x;
		position.y *= y;
		position.z *= z;
	}
	public void transform( javax.vecmath.Matrix4d trans ) {
		if( position != null ) {
			//trans.transform( position );
			edu.cmu.cs.stage3.math.Vector4 xyz1 = new edu.cmu.cs.stage3.math.Vector4( position.x, position.y, position.z, 1 );
			xyz1.transform( trans );
			position.x = xyz1.x;
			position.y = xyz1.y;
			position.z = xyz1.z;
		}
		if( normal != null ) {
			//trans.transform( normal );
			edu.cmu.cs.stage3.math.Vector4 ijk0 = new edu.cmu.cs.stage3.math.Vector4( normal.x, normal.y, normal.z, 0 );
			ijk0.transform( trans );
			normal.x = ijk0.x;
			normal.y = ijk0.y;
			normal.z = ijk0.z;
		}
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[position="+position+",normal="+normal+",diffuseColor="+diffuseColor+",specularHighlightColor="+specularHighlightColor+",textureCoordinate0="+textureCoordinate0+"]";
	}
	//todo
	//public static Vertex3d valueOf( String s ) {
	//	String[] markers = { "edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[position=", ",position=", ",normal=", ",diffuseColor=", ",specularColor=", ",textureCoordinate0=", "]" };
	//	return v;
	//}
}
