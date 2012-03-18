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

package edu.cmu.cs.stage3.alice.core.util;

public abstract class WorldListener {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	private edu.cmu.cs.stage3.alice.core.event.ChildrenListener m_childrenListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
			WorldListener.this.handleChildrenChanging( e );
		}
		public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
			if( e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
				WorldListener.this.hookUp( e.getChild() );
			} else if( e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
				WorldListener.this.unhookUp( e.getChild() );
			}
			WorldListener.this.handleChildrenChanged( e );
		}
	};
	private edu.cmu.cs.stage3.alice.core.event.PropertyListener m_propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			WorldListener.this.handlePropertyChanging( e );
		}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			WorldListener.this.handlePropertyChanged( e );
		}
	};
	private edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener m_objectArrayPropertyListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			WorldListener.this.handleObjectArrayPropertyChanging( e );
		}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			WorldListener.this.handleObjectArrayPropertyChanged( e );
		}
	};

	protected abstract void handleChildrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e );
	protected abstract void handleChildrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e );
	protected abstract void handlePropertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e );
	protected abstract void handlePropertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e );
	protected abstract void handleObjectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e );
	protected abstract void handleObjectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e );

	protected abstract boolean isPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.Property property );
	protected abstract boolean isObjectArrayPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap );

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		if( m_world != world ) {
			if( m_world != null ) {
				unhookUp( m_world );
			}
			m_world = world;
			if( m_world != null ) {
				hookUp( m_world );
			}
		}
	}
	
	private boolean isChildrenListenerHookedUp( edu.cmu.cs.stage3.alice.core.Element element ) {
		edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = element.getChildrenListeners();
		for( int i=0; i<childrenListeners.length; i++ ) {
			if( childrenListeners[ i ] == m_childrenListener ) {
				return true;
			}
		}
		return false;
	}
	private boolean isPropertyListenerHookedUp( edu.cmu.cs.stage3.alice.core.Property property ) {
		edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = property.getPropertyListeners();
		for( int i=0; i<propertyListeners.length; i++ ) {
			if( propertyListeners[ i ] == m_propertyListener ) {
				return true;
			}
		}
		return false;
	}
	private boolean isObjectArrayPropertyListenerHookedUp( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap ) {
		edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener[] oapListeners = oap.getObjectArrayPropertyListeners();
		for( int i=0; i<oapListeners.length; i++ ) {
			if( oapListeners[ i ] == m_objectArrayPropertyListener ) {
				return true;
			}
		}
		return false;
	}
	private void unhookUp( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( isChildrenListenerHookedUp( element ) ) {
			element.removeChildrenListener( m_childrenListener );
			//System.err.println( "-" + element );
		}
		edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
		for( int i=0; i<properties.length; i++ ) {
			edu.cmu.cs.stage3.alice.core.Property propertyI = properties[ i ];
			if( isPropertyListeningRequired( propertyI ) ) {
				if( isPropertyListenerHookedUp( propertyI ) ) {
					propertyI.removePropertyListener( m_propertyListener );
				}
			}
			if( propertyI instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
				edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oapI = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)propertyI;
				if( isObjectArrayPropertyListeningRequired( oapI ) ) {
					if( isObjectArrayPropertyListenerHookedUp( oapI ) ) {
						oapI.removeObjectArrayPropertyListener( m_objectArrayPropertyListener );
					}
				}
			}
		}
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for( int i=0; i<children.length; i++ ) {
			unhookUp( children[ i ] );
		}
	}
	private void hookUp( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( isChildrenListenerHookedUp( element ) ) {
			//pass
		} else {
			//System.err.println( "+" + element );
			element.addChildrenListener( m_childrenListener );
		}
		edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
		for( int i=0; i<properties.length; i++ ) {
			edu.cmu.cs.stage3.alice.core.Property propertyI = properties[ i ];
			if( isPropertyListeningRequired( propertyI ) ) {
				if( isPropertyListenerHookedUp( propertyI ) ) {
					//pass
				} else {
					propertyI.addPropertyListener( m_propertyListener );
				}
			}
			if( propertyI instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
				edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oapI = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)propertyI;
				if( isObjectArrayPropertyListeningRequired( oapI ) ) {
					if( isObjectArrayPropertyListenerHookedUp( oapI ) ) {
						//pass
					} else {
						oapI.addObjectArrayPropertyListener( m_objectArrayPropertyListener );
					}
				}
			}
		}
		edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
		for( int i=0; i<children.length; i++ ) {
			hookUp( children[ i ] );
		}
	}
}