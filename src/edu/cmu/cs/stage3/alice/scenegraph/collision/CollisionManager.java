package edu.cmu.cs.stage3.alice.scenegraph.collision;

import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;

public class CollisionManager implements AbsoluteTransformationListener {
	public void absoluteTransformationChanged( AbsoluteTransformationEvent absoluteTransformationEvent ) {
	}
	public void activateObject( Visual a ) {
	}
	public void deactivateObject( Visual a ) {
	}
	public void activatePair( Visual a, Visual b ) {
	}
	public void deactivatePair( Visual a, Visual b ) {
	}
	public void deleteObject( Visual a ) {
	}
	public Visual[][] update( int space ) {
		return null;
	}
    /*
	private edu.unc.cs.geom.VCollide m_vcollide = new edu.unc.cs.geom.VCollide();
    private java.util.Dictionary m_mapVisualToID = new java.util.Hashtable();
    private java.util.Dictionary m_mapIDToVisual = new java.util.Hashtable();
    private java.util.Vector m_pendingVisuals = new java.util.Vector();

    public void absoluteTransformationChanged( AbsoluteTransformationEvent absoluteTransformationEvent ) {
        Visual visual = (Visual)absoluteTransformationEvent.getSource();
        if( m_pendingVisuals.contains( visual ) ) {
            //pass
        } else {
            m_pendingVisuals.addElement( visual );
        }
    }
    private int getID( Visual visual ) {
        Integer idValue = (Integer)m_mapVisualToID.get( visual );
        if( idValue == null ) {
            synchronized( m_vcollide ) {
                int id = m_vcollide.newObject();
                IndexedTriangleArray ita = (IndexedTriangleArray)visual.getGeometry();
                if( ita != null ) {
                    Vertex3d[] vertices = ita.getVertices();
                    int[] indices = ita.getIndices();
                    double[] posA = new double[ 3 ];
                    double[] posB = new double[ 3 ];
                    double[] posC = new double[ 3 ];
                    for( int i=0; i<indices.length; i+=3 ) {
                        vertices[ indices[ i ] ].position.get( posA );
                        vertices[ indices[ i+1 ] ].position.get( posB );
                        vertices[ indices[ i+2 ] ].position.get( posC );
                        m_vcollide.addTri( posA, posB, posC, i/3 );
                    }
                } else {
                    System.err.println( "no geometry: " + visual );

                }
                m_vcollide.endObject();
                visual.addAbsoluteTransformationListener( this );
                m_pendingVisuals.addElement( visual );
                idValue = new Integer( id );
                m_mapVisualToID.put( visual, idValue );
                m_mapIDToVisual.put( idValue, visual );
            }
        }
        return idValue.intValue();
    }
    public void activateObject( Visual a ) {
        synchronized( m_vcollide ) {
            m_vcollide.activateObject( getID( a ) );
        }
    }
    public void deactivateObject( Visual a ) {
        synchronized( m_vcollide ) {
            m_vcollide.deactivateObject( getID( a ) );
        }
    }
    public void activatePair( Visual a, Visual b ) {
        synchronized( m_vcollide ) {
            m_vcollide.activatePair( getID( a ), getID( b ) );
        }
    }
    public void deactivatePair( Visual a, Visual b ) {
        synchronized( m_vcollide ) {
            m_vcollide.deactivatePair( getID( a ), getID( b ) );
        }
    }
    public void deleteObject( Visual a ) {
        synchronized( m_vcollide ) {
            Integer idValue = (Integer)m_mapVisualToID.get( a );
            if( idValue != null ) {
                m_mapVisualToID.remove( a );
                m_mapIDToVisual.remove( idValue );
                a.removeAbsoluteTransformationListener( this );
                //todo
                //m_vcollide.deleteObject( idValue.intValue() );
            }
        }
    }
    private Visual[][] m_pairs = {};
    public synchronized Visual[][] update( int space ) {
        synchronized( m_pendingVisuals ) {
            if( m_pendingVisuals.size() > 0 ) {
                java.util.Enumeration enum0 = m_pendingVisuals.elements();
                while( enum0.hasMoreElements() ) {
                    Visual visual = (Visual)enum0.nextElement();
                    javax.vecmath.Matrix4d mSrc = visual.getAbsoluteTransformation();
                    double[][] mDst = new double[ 4 ][ 4 ];
                    for( int rowIndex=0; rowIndex<4; rowIndex++ ) {
                        mSrc.getColumn( rowIndex, mDst[ rowIndex ] );
                    }
                    Integer idValue = (Integer)m_mapVisualToID.get( visual );
                    synchronized( m_vcollide ) {
                        m_vcollide.updateTrans( idValue.intValue(), mDst );
                    }
                }
                m_pendingVisuals.clear();
                synchronized( m_vcollide ) {
                    m_vcollide.collide( m_vcollide.VC_FIRST_CONTACT );
                    edu.unc.cs.geom.VCReportType[] buffer = new edu.unc.cs.geom.VCReportType[ space ];
                    m_vcollide.report( buffer );
                    java.util.Vector vector = new java.util.Vector();
                    for( int i=0; i<buffer.length; i++ ) {
                        if( buffer[ i ] != null ) {
                            Visual[] pair = new Visual[ 2 ];
                            pair[ 0 ] = (Visual)m_mapIDToVisual.get( new Integer( buffer[ i ].id1 ) );
                            pair[ 1 ] = (Visual)m_mapIDToVisual.get( new Integer( buffer[ i ].id2 ) );
                            vector.addElement( pair );
                        }
                    }
                    m_pairs = new Visual[ vector.size() ][];
                    vector.copyInto( m_pairs );
                }
            } else {
                //pass
            }
            return m_pairs;
        }
    }
	*/
}
