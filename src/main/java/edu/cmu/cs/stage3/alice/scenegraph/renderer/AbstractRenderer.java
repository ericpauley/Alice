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

public abstract class AbstractRenderer implements edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer,
        edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener,
        edu.cmu.cs.stage3.alice.scenegraph.event.BoundListener,
        edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenListener,
        edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyListener,
        edu.cmu.cs.stage3.alice.scenegraph.event.PropertyListener,
        edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseListener {

	private boolean m_isSoftwareEmulationForced;
	private java.util.Vector m_onscreenRenderTargets = new java.util.Vector();
	private java.util.Vector m_offscreenRenderTargets = new java.util.Vector();
	private OnscreenRenderTarget[] m_onscreenRenderTargetArray = null;
	private OffscreenRenderTarget[] m_offscreenRenderTargetArray = null;

	private java.util.Vector m_rendererListeners = new java.util.Vector();

	private java.util.Vector m_pendingAbsoluteTransformationChanges = new java.util.Vector();
	private java.util.Vector m_pendingBoundChanges = new java.util.Vector();
	private java.util.Vector m_pendingChildChanges = new java.util.Vector();
	private java.util.Vector m_pendingHeirarchyChanges = new java.util.Vector();
	private java.util.Vector m_pendingPropertyChanges = new java.util.Vector();
	private java.util.Vector m_pendingReleases = new java.util.Vector();

    protected abstract void dispatchAbsoluteTransformationChange( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent );
    protected abstract void dispatchBoundChange( edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent );
    protected abstract void dispatchChildAdd( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent );
    protected abstract void dispatchChildRemove( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent );
    protected abstract void dispatchHierarchyChange( edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent );
    protected abstract void dispatchPropertyChange( edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent );
    protected abstract void dispatchRelease( edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent );

	protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();
	protected abstract boolean requiresBoundListening();

	private int m_ignoreCount = 0;
	public void enterIgnore() {
		m_ignoreCount++;
	}
	public void leaveIgnore() {
		m_ignoreCount--;
	}
	private boolean ignore() {
		return m_ignoreCount>0;
	}
	public void markAllRenderTargetsDirty() {
		if( m_ignoreCount>0 ) {
			//pass
		} else {
			RenderTarget[] renderTargets;
			renderTargets = getOffscreenRenderTargets();
			for( int i=0; i<renderTargets.length; i++ ) {
				renderTargets[ i ].markDirty();
			}
			renderTargets = getOnscreenRenderTargets();
			for( int i=0; i<renderTargets.length; i++ ) {
				renderTargets[ i ].markDirty();
			}
		}
	}

    public void addListenersToSGElement( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
        if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Geometry ) {
            if( requiresBoundListening() ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Geometry)sgElement).addBoundListener( this );
            }
        } else if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Component ) {
            if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Container)sgElement).addChildrenListener( this );
            }
            if( requiresHierarchyAndAbsoluteTransformationListening() ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Component)sgElement).addAbsoluteTransformationListener( this );
                ((edu.cmu.cs.stage3.alice.scenegraph.Component)sgElement).addHierarchyListener( this );
            }
        }

        sgElement.addPropertyListener( this );
        sgElement.addReleaseListener( this );
    }
    public void removeListenersFromSGElement( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement ) {
        if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Geometry ) {
            if( requiresBoundListening() ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Geometry)sgElement).removeBoundListener( this );
            }
        } else if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Component ) {
            if( sgElement instanceof edu.cmu.cs.stage3.alice.scenegraph.Container ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Container)sgElement).removeChildrenListener( this );
            }
            if( requiresHierarchyAndAbsoluteTransformationListening() ) {
                ((edu.cmu.cs.stage3.alice.scenegraph.Component)sgElement).removeAbsoluteTransformationListener( this );
                ((edu.cmu.cs.stage3.alice.scenegraph.Component)sgElement).removeHierarchyListener( this );
            }
        }
        sgElement.removePropertyListener( this );
        sgElement.removeReleaseListener( this );
    }
	public void commitAnyPendingChanges() {
        java.util.Enumeration enum0;
        if( m_pendingAbsoluteTransformationChanges.size()>0 ) {
            synchronized( m_pendingAbsoluteTransformationChanges ) {
                enum0 = m_pendingAbsoluteTransformationChanges.elements();
                while( enum0.hasMoreElements() ) {
                    dispatchAbsoluteTransformationChange( (edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent)enum0.nextElement() );
                }
                m_pendingAbsoluteTransformationChanges.clear();
            }
        }
        if( m_pendingBoundChanges.size()>0 ) {
            synchronized( m_pendingBoundChanges ) {
                enum0 = m_pendingBoundChanges.elements();
                while( enum0.hasMoreElements() ) {
                    dispatchBoundChange( (edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent)enum0.nextElement() );
                }
                m_pendingBoundChanges.clear();
            }
        }
		if( m_pendingChildChanges.size()>0 ) {
			synchronized( m_pendingChildChanges ) {
				enum0 = m_pendingChildChanges.elements();
				while( enum0.hasMoreElements() ) {
					edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent e = (edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent)enum0.nextElement();
					if( e.getID()== edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent.CHILD_ADDED ) {
						dispatchChildAdd( e );
					} else {
						dispatchChildRemove( e );
					}
				}
				m_pendingChildChanges.clear();
			}
		}
        if( m_pendingHeirarchyChanges.size()>0 ) {
            synchronized( m_pendingHeirarchyChanges ) {
                enum0 = m_pendingHeirarchyChanges.elements();
                while( enum0.hasMoreElements() ) {
                    dispatchHierarchyChange( (edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent)enum0.nextElement() );
                }
                m_pendingHeirarchyChanges.clear();
            }
        }
        if( m_pendingPropertyChanges.size()>0 ) {
            synchronized( m_pendingPropertyChanges ) {
                enum0 = m_pendingPropertyChanges.elements();
                while( enum0.hasMoreElements() ) {
                    dispatchPropertyChange( (edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent)enum0.nextElement() );
                }
                m_pendingPropertyChanges.clear();
            }
        }
	}
	
	private boolean isThreadOK() {
		return javax.swing.SwingUtilities.isEventDispatchThread();
	}
	public void absoluteTransformationChanged( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
		if( isThreadOK() ) {
            dispatchAbsoluteTransformationChange( absoluteTransformationEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingAbsoluteTransformationChanges.addElement( absoluteTransformationEvent );
		}
	}
	public void boundChanged( edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent boundEvent ) {
		if( isThreadOK() ) {
            dispatchBoundChange( boundEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingBoundChanges.addElement( boundEvent );
		}
	}
	public void childAdded( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
		if( isThreadOK() ) {
            dispatchChildAdd( childrenEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingChildChanges.addElement( childrenEvent );
		}
	}
	public void childRemoved( edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent childrenEvent ) {
		if( isThreadOK() ) {
            dispatchChildRemove( childrenEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingChildChanges.addElement( childrenEvent );
		}
	}
	public void hierarchyChanged( edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent hierarchyEvent ) {
		if( isThreadOK() ) {
            dispatchHierarchyChange( hierarchyEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingHeirarchyChanges.addElement( hierarchyEvent );
		}
	}
	public synchronized void changed( edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent propertyEvent ) {
		if( isThreadOK() ) {
            dispatchPropertyChange( propertyEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingPropertyChanges.addElement( propertyEvent );
		}
	}
	public synchronized void releasing( edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent ) {
	}
	public synchronized void released( edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent releaseEvent ) {
		if( isThreadOK() ) {
            dispatchRelease( releaseEvent );
    		markAllRenderTargetsDirty();
		} else {
			m_pendingReleases.addElement( releaseEvent );
		}
	}
	
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
	public boolean addRenderTarget( RenderTarget renderTarget ) {
        if( renderTarget instanceof OnscreenRenderTarget ) {
			m_onscreenRenderTargetArray = null;
			m_onscreenRenderTargets.addElement( renderTarget );
        } else if( renderTarget instanceof OffscreenRenderTarget ) {
            m_offscreenRenderTargetArray = null;
            m_offscreenRenderTargets.addElement( renderTarget );
        } else {
            //todo?
        }
        return true;
	}
	public boolean removeRenderTarget( RenderTarget renderTarget ) {
		if( renderTarget instanceof OnscreenRenderTarget ) {
            m_onscreenRenderTargetArray = null;
            return m_onscreenRenderTargets.removeElement( renderTarget );
        } else if( renderTarget instanceof OffscreenRenderTarget ) {
            m_offscreenRenderTargetArray = null;
            return m_offscreenRenderTargets.removeElement( renderTarget );
        } else {
            return true;
        }
	}

	public OnscreenRenderTarget[] getOnscreenRenderTargets() {
		if( m_onscreenRenderTargetArray == null ) {
			m_onscreenRenderTargetArray = new OnscreenRenderTarget[ m_onscreenRenderTargets.size() ];
			m_onscreenRenderTargets.copyInto( m_onscreenRenderTargetArray );
		}
		return m_onscreenRenderTargetArray;
	}
	public OffscreenRenderTarget[] getOffscreenRenderTargets() {
		if( m_offscreenRenderTargetArray==null ) {
            m_offscreenRenderTargetArray = new OffscreenRenderTarget[m_offscreenRenderTargets.size()];
            m_offscreenRenderTargets.copyInto( m_offscreenRenderTargetArray );
		}
		return m_offscreenRenderTargetArray;
	}

	public synchronized void release() {
	}

	public boolean isSoftwareEmulationForced() {
		return m_isSoftwareEmulationForced;
	}
	public void setIsSoftwareEmulationForced( boolean isSoftwareEmulationForced ) {
		m_isSoftwareEmulationForced = isSoftwareEmulationForced;
	}
}
