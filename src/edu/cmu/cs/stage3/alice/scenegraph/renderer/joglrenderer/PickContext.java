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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

class PickContext extends Context {
    private RenderTarget m_renderTarget;

    private java.util.Hashtable m_pickNameMap = new java.util.Hashtable();
    private PickParameters m_pickParameters;
    private PickInfo m_pickInfo;
    
    public PickContext( RenderTarget renderTarget ) {
        m_renderTarget = renderTarget;
    }

    
	public void display( javax.media.opengl.GLAutoDrawable drawable ) {
        super.display( drawable );
        if( m_pickParameters != null ) {
            m_renderTarget.commitAnyPendingChanges();
    	    m_pickNameMap.clear();
    	    try {
	    	    m_pickInfo = m_renderTarget.performPick( this, m_pickParameters );
    	    } finally {
    	        m_pickParameters = null;
    	    }
        }
    }
    public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( javax.media.opengl.GLAutoDrawable drawable, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
        m_pickParameters = new PickParameters( x, y, isSubElementRequired, isOnlyFrontMostRequired );
        //System.err.println( m_pickParameters );
        /*
        drawable.setRenderingThread( Thread.currentThread() );
        //drawable.setNoAutoRedrawMode( true );
        drawable.setAutoSwapBufferMode( false );
        */
        try {
            //System.err.println( "about to display: " + m_pickParameters );
            drawable.display();
        } finally {
            /*
            drawable.setRenderingThread( null );
            //drawable.setNoAutoRedrawMode( false );
            drawable.setAutoSwapBufferMode( true );
            */
        }
        return m_pickInfo;
    }
    public int getPickNameForVisualProxy( VisualProxy visualProxy ) {
        int name = m_pickNameMap.size();
        m_pickNameMap.put( new Integer( name ), visualProxy );
        return name;
    }
    public VisualProxy getPickVisualProxyForName( int name ) {
//        System.err.println( "getPickVisualProxyForName" );
//        System.err.println( name );
//        System.err.println( m_pickNameMap );
//        System.err.println( (VisualProxy)m_pickNameMap.get( new Integer( name ) ) );
        return (VisualProxy)m_pickNameMap.get( new Integer( name ) );
    }

	protected void renderPickVertex( edu.cmu.cs.stage3.alice.scenegraph.Vertex3d vertex ) {
		gl.glVertex3d( vertex.position.x, vertex.position.y, -vertex.position.z );
	}
}
