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
public abstract class VertexGeometry extends Geometry {
	public static final Property VERTICES_PROPERTY = new Property( VertexGeometry.class, "VERTICES" );
	public static final Property VERTEX_LOWER_BOUND_PROPERTY = new Property( VertexGeometry.class, "VERTEX_LOWER_BOUND" );
	public static final Property VERTEX_UPPER_BOUND_PROPERTY = new Property( VertexGeometry.class, "VERTEX_UPPER_BOUND" );
	private Vertex3d[] m_vertices = null;
	private int m_vertexLowerBound = 0;
	private int m_vertexUpperBound = -1;

	
	protected void updateBoundingBox() {
		if( m_vertices!=null && m_vertices.length>0 ) {
            if( m_vertices[0] != null ) {
                javax.vecmath.Point3d min = new javax.vecmath.Point3d();
                javax.vecmath.Point3d max = new javax.vecmath.Point3d();

                Vertex3d v0 = m_vertices[0];
                min.set( v0.position );
                max.set( v0.position );
                for( int i=1; i<m_vertices.length; i++ ) {
                    Vertex3d vi = m_vertices[i];
                    min.x = Math.min( vi.position.x, min.x );
                    min.y = Math.min( vi.position.y, min.y );
                    min.z = Math.min( vi.position.z, min.z );
                    max.x = Math.max( vi.position.x, max.x );
                    max.y = Math.max( vi.position.y, max.y );
                    max.z = Math.max( vi.position.z, max.z );
                }
                javax.vecmath.Vector3d minimum = new javax.vecmath.Vector3d( min.x, min.y, min.z );
                javax.vecmath.Vector3d maximum = new javax.vecmath.Vector3d( max.x, max.y, max.z );
                m_boundingBox = new edu.cmu.cs.stage3.math.Box( minimum, maximum );
            } else {
                throw new RuntimeException( this + " vertex[ 0 ] has somehow become null." );
                //m_boundingBox = null;
            }
		} else {
			m_boundingBox = null;
		}
	}
	private static double getDistanceSquaredBetween( Vertex3d vertex, javax.vecmath.Vector3d vector ) {
		double dx = vertex.position.x-vector.x;
		double dy = vertex.position.y-vector.y;
		double dz = vertex.position.z-vector.z;
		return dx*dx + dy*dy + dz*dz;
	}
	
	protected void updateBoundingSphere() {
		edu.cmu.cs.stage3.math.Box box = getBoundingBox();
		if( box!=null ) {
			javax.vecmath.Vector3d center = box.getCenter();
			double distanceSquared = 0;
			for( int i=0; i<m_vertices.length; i++ ) {
				double d2 = getDistanceSquaredBetween( m_vertices[i], center );
				distanceSquared = Math.max( d2, distanceSquared );
			}
			m_boundingSphere = new edu.cmu.cs.stage3.math.Sphere( center, Math.sqrt( distanceSquared ) );
		}
	}

	public Vertex3d[] getVertices() {
		return m_vertices;
	}
	public void setVertices( Vertex3d[] vertices ) {
		m_vertices = vertices;
		onPropertyChange( VERTICES_PROPERTY );
		onBoundsChange();
	}

	public int getVertexLowerBound() {
		return m_vertexLowerBound;
	}
	public void setVertexLowerBound( int vertexLowerBound ) {
		if( m_vertexLowerBound != vertexLowerBound ) {
			m_vertexLowerBound = vertexLowerBound;
			onPropertyChange( VERTEX_LOWER_BOUND_PROPERTY );
			//onBoundsChange();
		}
	}
	public int getVertexUpperBound() {
		return m_vertexUpperBound;
	}
	public void setVertexUpperBound( int vertexUpperBound ) {
		if( m_vertexUpperBound != vertexUpperBound ) {
			m_vertexUpperBound = vertexUpperBound;
			onPropertyChange( VERTEX_UPPER_BOUND_PROPERTY );
			//onBoundsChange();
		}
	}

	public int getVertexCount() {
		if( m_vertices!=null ) {
			return m_vertices.length;
		} else {
			return 0;
		}
	}

	
	public void transform( javax.vecmath.Matrix4d trans ) {
		Vertex3d[] vertices = getVertices();
		for( int i=0; i<vertices.length; i++ ) {
			vertices[i].transform( trans );
		}
		setVertices( vertices );
	}

	public static Vertex3d[] loadVertices( java.io.InputStream is ) throws java.io.IOException {
		Vertex3d[] vertices = null;
		java.io.BufferedInputStream bis = new java.io.BufferedInputStream ( is );
		java.io.DataInputStream dis = new java.io.DataInputStream ( bis );
		int version = dis.readInt ();
		if( version==1 ) {
			int vertexCount = dis.readInt();
			vertices = new Vertex3d[vertexCount];
			for (int i=0; i<vertices.length; i++) {
				vertices[i] = new Vertex3d( Vertex3d.FORMAT_POSITION | Vertex3d.FORMAT_NORMAL | Vertex3d.FORMAT_TEXTURE_COORDINATE_0 );
				vertices[i].position.x = dis.readDouble();
				vertices[i].position.y = dis.readDouble();
				vertices[i].position.z = dis.readDouble();
				vertices[i].normal.x = dis.readDouble();
				vertices[i].normal.y = dis.readDouble();
				vertices[i].normal.z = dis.readDouble();
				vertices[i].textureCoordinate0.x = (float)dis.readDouble();
				vertices[i].textureCoordinate0.y = (float)dis.readDouble();
			}
		} else if( version==2 ) {
			int vertexCount = dis.readInt();
			vertices = new Vertex3d[vertexCount];
			for (int i=0; i<vertices.length; i++) {
				int format = dis.readInt();
				vertices[i] = new Vertex3d( format );
				if( (format&Vertex3d.FORMAT_POSITION)!=0 ) {
					vertices[i].position.x = dis.readDouble();
					vertices[i].position.y = dis.readDouble();
					vertices[i].position.z = dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_NORMAL)!=0 ) {
					vertices[i].normal.x = dis.readDouble();
					vertices[i].normal.y = dis.readDouble();
					vertices[i].normal.z = dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_DIFFUSE_COLOR)!=0 ) {
					vertices[i].diffuseColor.red = (float)dis.readDouble();
					vertices[i].diffuseColor.green = (float)dis.readDouble();
					vertices[i].diffuseColor.blue = (float)dis.readDouble();
					vertices[i].diffuseColor.alpha = (float)dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_SPECULAR_HIGHLIGHT_COLOR)!=0 ) {
					//vertices[i].specularHighlightColor.x = (float)dis.readDouble();
					//vertices[i].specularHighlightColor.y = (float)dis.readDouble();
					//vertices[i].specularHighlightColor.z = (float)dis.readDouble();
					//vertices[i].specularHighlightColor.w = (float)dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_TEXTURE_COORDINATE_0)!=0 ) {
					vertices[i].textureCoordinate0.x = (float)dis.readDouble();
					vertices[i].textureCoordinate0.y = (float)dis.readDouble();
				}
			}
		} else if( version==3 ) {
			int vertexCount = dis.readInt();
			vertices = new Vertex3d[vertexCount];
			for (int i=0; i<vertices.length; i++) {
				int format = dis.readInt();
				vertices[i] = new Vertex3d( format );
				if( (format&Vertex3d.FORMAT_POSITION)!=0 ) {
					vertices[i].position.x = dis.readDouble();
					vertices[i].position.y = dis.readDouble();
					vertices[i].position.z = dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_NORMAL)!=0 ) {
					vertices[i].normal.x = dis.readDouble();
					vertices[i].normal.y = dis.readDouble();
					vertices[i].normal.z = dis.readDouble();
				}
				if( (format&Vertex3d.FORMAT_DIFFUSE_COLOR)!=0 ) {
					vertices[i].diffuseColor.red = dis.readFloat();
					vertices[i].diffuseColor.green = dis.readFloat();
					vertices[i].diffuseColor.blue = dis.readFloat();
					vertices[i].diffuseColor.alpha = dis.readFloat();
				}
				if( (format&Vertex3d.FORMAT_SPECULAR_HIGHLIGHT_COLOR)!=0 ) {
					vertices[i].specularHighlightColor.red = dis.readFloat();
					vertices[i].specularHighlightColor.green = dis.readFloat();
					vertices[i].specularHighlightColor.blue = dis.readFloat();
					vertices[i].specularHighlightColor.alpha = dis.readFloat();
				}
				if( (format&Vertex3d.FORMAT_TEXTURE_COORDINATE_0)!=0 ) {
					vertices[i].textureCoordinate0.x = dis.readFloat();
					vertices[i].textureCoordinate0.y = dis.readFloat();
				}
			}
		} else {
			throw new RuntimeException( "invalid file version: " + version );
		}
		return vertices;
	}
	public static void storeVertices( Vertex3d[] vertices, java.io.OutputStream os ) throws java.io.IOException {
		java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream ( os );
		java.io.DataOutputStream dos = new java.io.DataOutputStream ( bos );
		dos.writeInt( 3 );
		dos.writeInt( vertices.length );
		for (int i=0; i<vertices.length; i++) {
			int format = vertices[i].getFormat();
			dos.writeInt( format );
			if( (format&Vertex3d.FORMAT_POSITION)!=0 ) {
				dos.writeDouble( vertices[i].position.x );
				dos.writeDouble( vertices[i].position.y );
				dos.writeDouble( vertices[i].position.z );
			}
			if( (format&Vertex3d.FORMAT_NORMAL)!=0 ) {
				dos.writeDouble( vertices[i].normal.x );
				dos.writeDouble( vertices[i].normal.y );
				dos.writeDouble( vertices[i].normal.z );
			}
			if( (format&Vertex3d.FORMAT_DIFFUSE_COLOR)!=0 ) {
				dos.writeFloat( vertices[i].diffuseColor.red );
				dos.writeFloat( vertices[i].diffuseColor.green );
				dos.writeFloat( vertices[i].diffuseColor.blue );
				dos.writeFloat( vertices[i].diffuseColor.alpha );
			}
			if( (format&Vertex3d.FORMAT_SPECULAR_HIGHLIGHT_COLOR)!=0 ) {
				dos.writeFloat( vertices[i].specularHighlightColor.red );
				dos.writeFloat( vertices[i].specularHighlightColor.green );
				dos.writeFloat( vertices[i].specularHighlightColor.blue );
				dos.writeFloat( vertices[i].specularHighlightColor.alpha );
			}
			if( (format&Vertex3d.FORMAT_TEXTURE_COORDINATE_0)!=0 ) {
				dos.writeFloat( vertices[i].textureCoordinate0.x );
				dos.writeFloat( vertices[i].textureCoordinate0.y );
			}
		}
		dos.flush();
	}

}
