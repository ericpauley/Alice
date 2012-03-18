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

abstract class Context implements javax.media.opengl.GLEventListener {
    public javax.media.opengl.GL gl;
    public javax.media.opengl.glu.GLU glu;
	public com.sun.opengl.util.GLUT glut;
    
    protected int m_width;
    protected int m_height;

    public void init( javax.media.opengl.GLAutoDrawable drawable ) {
        //drawable.setGL( new javax.media.opengl.DebugGL( drawable.getGL() ) );
    }
 
    public void display( javax.media.opengl.GLAutoDrawable drawable ) {
        gl = drawable.getGL();
    	glu = new javax.media.opengl.glu.GLU();
        glut = new com.sun.opengl.util.GLUT();
    }
    public void reshape( javax.media.opengl.GLAutoDrawable drawable, int x, int y, int width, int height ) {
        //System.err.println( "reshape: " + drawable );
        m_width = width;
        m_height = height;
    }
    public void displayChanged( javax.media.opengl.GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) {
        //System.err.println( "displayChanged: " + drawable + " " + modeChanged + " "  + deviceChanged );
    }

    public int getWidth() {
        return m_width;
    }
    public int getHeight() {
        return m_height;
    }
}
