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

package edu.cmu.cs.stage3.alice.scenegraph.renderer;

public class DefaultRenderTargetFactory implements RenderTargetFactory {

	public static Class[] getPotentialRendererClasses() {
		java.util.Vector vector = new java.util.Vector();
		
		if ( System.getProperty("os.name").startsWith("Win") ){
			try {
				if( edu.cmu.cs.stage3.alice.scenegraph.renderer.util.DirectXVersion.getVersion() >= 7.0 ) {
					vector.addElement( edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class );
				}
			} catch( Throwable t ) {
				//pass
			}
		}

		try {
			vector.addElement( edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class );
		} catch( Throwable t ) {
			//pass
		}

		try {
			vector.addElement( edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer.class );
		} catch( Throwable t ) {
			//pass
		}
		Class[] array = new Class[ vector.size() ];
		vector.copyInto( array );
		return array;
	}
	private Class m_rendererClass;
	private Renderer m_renderer;
	public DefaultRenderTargetFactory() {
		this( null );
	}
	public DefaultRenderTargetFactory( Class rendererClass ) {
		if( rendererClass == null ) {
			rendererClass = getPotentialRendererClasses()[ 0 ];		
		}
		m_rendererClass = rendererClass;
	}
	public boolean isSoftwareEmulationForced() {
		return getRenderer().isSoftwareEmulationForced();
	}
	public void setIsSoftwareEmulationForced( boolean isSoftwareEmulationForced ) {
		getRenderer().setIsSoftwareEmulationForced( isSoftwareEmulationForced );
	}

	private Renderer getRenderer() {
		if( m_renderer == null ) {
			try {
				m_renderer = (Renderer)m_rendererClass.newInstance();
			} catch( Throwable t ) {
				t.printStackTrace();
			}
		}
		return m_renderer;
	}

	public OffscreenRenderTarget createOffscreenRenderTarget() {
		return getRenderer().createOffscreenRenderTarget();
	}
	public OnscreenRenderTarget createOnscreenRenderTarget() {
		return getRenderer().createOnscreenRenderTarget();
	}

	public void releaseOffscreenRenderTarget( OffscreenRenderTarget offscreenRenderTarget ) {
		offscreenRenderTarget.release();
	}
	public void releaseOnscreenRenderTarget( OnscreenRenderTarget onscreenRenderTarget ) {
		onscreenRenderTarget.release();
	}
	
	public void release() {
		//todo
	}
}
