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

public abstract class AbstractProxyRenderer extends AbstractRenderer {
	private java.util.Hashtable m_sgElementToProxyMap = new java.util.Hashtable();
	private java.util.Vector m_queuedPropertyChanges = new java.util.Vector();

    
	protected void dispatchPropertyChange( edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent ) {
		edu.cmu.cs.stage3.alice.scenegraph.Property property = propertyEvent.getProperty();
		edu.cmu.cs.stage3.alice.scenegraph.Element sgElement = (edu.cmu.cs.stage3.alice.scenegraph.Element)propertyEvent.getSource();
		if( sgElement.isReleased() ) {
			//pass
		} else {
            AbstractProxy proxy = getProxyFor( sgElement );
			proxy.changed( property, property.get( sgElement ) );
    		markAllRenderTargetsDirty();
		}
    }
    
	protected void dispatchRelease( edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent ) {
        edu.cmu.cs.stage3.alice.scenegraph.Element sgElement = (edu.cmu.cs.stage3.alice.scenegraph.Element)releaseEvent.getSource();
        AbstractProxy proxy = getProxyFor( sgElement );
        proxy.release();
    }

	protected abstract AbstractProxy createProxyFor( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement );
	public AbstractProxy getProxyFor( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
		AbstractProxy proxy;
		if( sgElement!=null ) {
			proxy = (AbstractProxy)m_sgElementToProxyMap.get( sgElement );
			if( proxy==null ) {
				proxy = createProxyFor( sgElement );
				if( proxy!=null ) {
					m_sgElementToProxyMap.put( sgElement, proxy );
					proxy.initialize( sgElement, this );
                    createNecessaryProxies( sgElement );
				} else {
					edu.cmu.cs.stage3.alice.scenegraph.Element.warnln( "warning: could not create proxy for: " + sgElement );
				}
			} else {
				if( proxy.getSceneGraphElement()==null ) {
					proxy = null;
					edu.cmu.cs.stage3.alice.scenegraph.Element.warnln( sgElement + "'s proxy has null for a sgElement" );
				}
			}
		} else {
			proxy = null;
		}
		return proxy;
	}
    public AbstractProxy[] getProxiesFor( edu.cmu.cs.stage3.alice.scenegraph.Element[] sgElements, Class componentType ) {
        if( sgElements!=null ) {
            AbstractProxy[] proxies = (AbstractProxy[])java.lang.reflect.Array.newInstance( componentType, sgElements.length );
            for( int i=0; i<sgElements.length; i++ ) {
                proxies[i] = getProxyFor( sgElements[i] );
            }
            return proxies;
        } else {
            return null;
        }
    }

	public void forgetProxyFor( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
		m_sgElementToProxyMap.remove( sgElement );
	}
	public void createNecessaryProxies( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
		getProxyFor( sgElement );
        if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container ) {
            edu.cmu.cs.stage3.alice.scenegraph.Container sgContainer = (edu.cmu.cs.stage3.alice.scenegraph.Container)sgElement;
            for( int i=0; i<sgContainer.getChildCount(); i++ ) {
                edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent = sgContainer.getChildAt( i );
                getProxyFor( sgComponent );
                if( sgComponent instanceof edu.cmu.cs.stage3.alice.scenegraph.Container ) {
                    createNecessaryProxies( sgComponent );
                }
            }
        }
	}
}
