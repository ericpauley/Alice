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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class WorldDifferencesCapsule implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener, edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected java.util.HashMap changedProperties = new java.util.HashMap();
	protected java.util.ArrayList changedObjectArrayProperties = new java.util.ArrayList();
	protected java.util.ArrayList changedElements = new java.util.ArrayList();

        // need to keep track of where things are moving
        protected java.util.HashMap changedElementPositions = new java.util.HashMap();

        // need to keep track of the order of changes so restore happens in the right order
        protected java.util.ArrayList changeOrder = new java.util.ArrayList();

	protected boolean isListening;

        protected static final String elementChange = "elementChange";
        protected static final String propertyChange = "propertyChange";
        protected static final String objectArrayChange = "objectArrayChange";

	public WorldDifferencesCapsule( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.core.World world ) {
		this.authoringTool = authoringTool;
		this.world = world;

		startListening();
	}

        public edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule getStateCapsule() {
//          System.out.println("\nCURRENT STATE: ");

          edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule capsule = new edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule();

          java.util.Iterator iter = changedElements.iterator();

          iter = changedObjectArrayProperties.iterator();
//          System.out.println("\tChanged Object Array Properties:");
          while (iter.hasNext()) {
            Object obj = iter.next();

            if (obj instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
              int type = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).changeType;
              int oldPos = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).oldIndex;
              int newPos = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).newIndex;
              Object o = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).value;
//              System.out.println( ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).property.getName());
              edu.cmu.cs.stage3.alice.core.Element value = null;
              if (o instanceof edu.cmu.cs.stage3.alice.core.Element) { value = (edu.cmu.cs.stage3.alice.core.Element)o; }

              o = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).getAffectedObject();
              edu.cmu.cs.stage3.alice.core.Element affected = null;
              if (o instanceof edu.cmu.cs.stage3.alice.core.Element) { affected = (edu.cmu.cs.stage3.alice.core.Element)o; }

              if (type == 1) {
//                System.out.println("\t\t INSERT: " + value.getKey() + " in " + affected.getKey(this.world) + " POSITION: " + newPos);
                capsule.addExistantElement(value.getKey(this.world));

                capsule.putElementPosition(value.getKey(this.world), newPos);

                // CALL to a user-defined response is handled a little differently to get the parameters
                if (value instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
                  edu.cmu.cs.stage3.alice.core.Property params = value.getPropertyNamed("requiredActualParameters");
                  Object udobj = params.getValue();
                  if (udobj instanceof edu.cmu.cs.stage3.alice.core.Variable[]) {
                    edu.cmu.cs.stage3.alice.core.Variable vars[] = (edu.cmu.cs.stage3.alice.core.Variable[]) udobj;
                    if (vars != null) {
                      for (int i = 0; i < vars.length; i++) {
//                        System.out.println("\t\t\tSet Property: " + vars[i].getKey(world) + " to " + vars[i].getValue());
                        String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
                        capsule.putPropertyValue(vars[i].getKey(world), valueRepr);
                      }
                    }
                  }
                } else {
                  // Properties
                  String[] visProps = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getInitialVisibleProperties(value.getClass());
                  if (visProps != null) {
                    for (int i = 0; i < visProps.length; i++) {
                      edu.cmu.cs.stage3.alice.core.Property visProp = ((edu.cmu.cs.stage3.alice.core.Response)value).getPropertyNamed(visProps[i]);
                      String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(visProp.get(), true);
                      capsule.putPropertyValue(value.getKey(world) + "." + visProp.getName(), valueRepr);
                    }
                  }
                }
              } else if (type == 2) {
                  capsule.putElementPosition(value.getKey(this.world), newPos);
              } else {
                if (world.isAncestorOf(value)) {
                } else {
                  capsule.addNonExistantElement(value.getKey());
                }
              }
            }
          }

          iter = changedProperties.keySet().iterator();
          while (iter.hasNext()) {
            Object o = iter.next();
            String keyAndProp = (String)o;

            int lastPd = keyAndProp.lastIndexOf(".");
            String key = keyAndProp.substring(0, lastPd);
            String propName = keyAndProp.substring(lastPd + 1, keyAndProp.length());

            if (propName.indexOf("data") == -1) {
              edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
              edu.cmu.cs.stage3.alice.core.Property p = null;
              if (e != null) p = e.getPropertyNamed(propName);
              if (p != null) {
                if (p.get() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
                  edu.cmu.cs.stage3.alice.core.Property resp = ( (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p.get()).getPropertyNamed("userDefinedResponse");
                  String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(resp.get(), true);
                  capsule.putPropertyValue(resp.getOwner().getKey(world)+".userDefinedResponse", valueRepr);

                  edu.cmu.cs.stage3.alice.core.Property pars = ( (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p.get()).getPropertyNamed("requiredActualParameters");

                  Object udobj = pars.getValue();
                  if (udobj instanceof edu.cmu.cs.stage3.alice.core.Variable[]) {
                    edu.cmu.cs.stage3.alice.core.Variable vars[] = (edu.cmu.cs.stage3.alice.core.Variable[]) udobj;
                    if (vars != null) {
                      for (int i = 0; i < vars.length; i++) {
//                        System.out.println("\t\t\tSet Property: " + vars[i].getKey(world) + " to " + vars[i].getValue());
                        valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
                        capsule.putPropertyValue(vars[i].getKey(world), valueRepr);
                      }
                    }
                  }

                } else {
                  String valueRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(p.get(), true);
//                  System.out.println("\t\t PROPERTY: " + key + "." + propName + " SET TO: " + valueRepr);
                  capsule.putPropertyValue(key+"."+propName, valueRepr);
                }
              }
            }

          }

          return capsule;
        }

        // START HERE.

        public java.util.Vector getChangedPropertiesNamed(String propertyName) {
          java.util.Vector props = new java.util.Vector();

          java.util.Set changedProps = changedProperties.keySet();
          java.util.Iterator iter = changedProps.iterator();
          while ( iter.hasNext() ) {
            String propAndKey = (String) iter.next();
            int endName = propAndKey.lastIndexOf(".");
            String elName = propAndKey.substring(0, endName);
            String propName = propAndKey.substring(endName + 1, propAndKey.length());
            if ( propAndKey.endsWith(propertyName) ) {
              edu.cmu.cs.stage3.alice.core.Element el = world.getDescendantKeyed(elName);
              props.addElement(el.getPropertyNamed(propName));
            }
          }

          return props;
        }

        public boolean otherPropertyChangesMade(java.util.Set correctPropertyChangeSet) {

          java.util.Set changedProps = changedProperties.keySet();
          java.util.Iterator iter = changedProps.iterator();
          while ( iter.hasNext() ) {
            String propAndKey = (String) iter.next();

//            System.out.println("propAndKey: " + propAndKey);

            // to capture info, triggerResponses are saved differently
            if (propAndKey.endsWith("triggerResponse") ) {
              int lastPd = propAndKey.lastIndexOf(".");
              String key = propAndKey.substring(0, lastPd);
              String propName = propAndKey.substring(lastPd + 1, propAndKey.length());

              edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
              edu.cmu.cs.stage3.alice.core.Property p = null;
              if (e != null) p = e.getPropertyNamed(propName);

               if (p.get() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
                  edu.cmu.cs.stage3.alice.core.Property resp = ( (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) p.get()).getPropertyNamed("userDefinedResponse");

                  propAndKey = resp.getOwner().getKey(world) + ".userDefinedResponse";
               }
            }

//            System.out.println(correctPropertyChangeSet);

            if ( (correctPropertyChangeSet != null) && !( correctPropertyChangeSet.contains(propAndKey) ) ) {

              // right now, changes to the world are illegal
              if (propAndKey.endsWith("data") ) {
              } if (propAndKey.endsWith("localTransformation") ) {
                return true;
              } else if (propAndKey.endsWith("name")) {
                return true;
              } else if (propAndKey.endsWith("isCommentedOut")){
                return true;
              } else {
                String key = propAndKey.substring(0, propAndKey.lastIndexOf("."));
                edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed(key);
//                System.out.println(key + " " + e);
                if (e instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
                  return true;
                }
              }

            }
          }

//          System.out.println("no illegal property changes");
          return false;
        }

        public boolean otherElementsShifted(java.util.Set correctElementsShifted) {
//          java.util.Set actualElementsShifted = this.changedElementPositions.keySet();
//
//          for( java.util.Iterator iter = changedObjectArrayProperties.iterator(); iter.hasNext(); ) {
//            Object obj = iter.next();
//            if (obj instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
//              Object o = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).value;
//              int type = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).changeType;
//              edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element)o;
//              String name = "";
//
//              // this change was a position shift
//              if (type == 2) {
//                if ( correctElementsShifted.contains(e.getKey(world)) ) {
//                  System.out.println("this is a legit addition");
//                } else {
//                  return true;
//                }
//              }
//            }
//          }
          return false;
        }

        public boolean otherElementsInsertedOrDeleted(String[] insertedNames, String[] deletedNames) {

          java.util.List insertedList = java.util.Arrays.asList(insertedNames);
          java.util.List deletedList = java.util.Arrays.asList(deletedNames);

          java.util.Vector illegalInserts = new java.util.Vector();

          for( java.util.Iterator iter = changedObjectArrayProperties.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
//            System.out.println(obj);
            if (obj instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
              Object o = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).value;
              int type = ( (edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)obj).changeType;
              edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element)o;
              String name = "";

//              System.out.println("value " + o + " " + type);
              if (e.isDescendantOf(world)) {
                name = e.getKey(world);
              } else name = e.getKey();

              if ( (!insertedList.contains(name)) && (type == 1) ) {
//                System.out.println("this was inserted and shouldn't have been: " + name);
                illegalInserts.addElement(name);
              }
              if ( (!deletedList.contains(name)) && (type == 3) ) {
                if ( illegalInserts.contains(name) ) {
                  // an illegal insertion is being removed
//                  System.out.println("removing an illegal delete: " + name);
                  illegalInserts.remove(name);
                } else {
                  // this is an illegal delete
//                  System.out.println("this was deleted and shouldn't have been: \n" + obj + "\n");

//                  return true;
                }
              }
            }
          }

          // check to make sure that everything that was supposed to be removed was.
          boolean insertsStillPresent = false;
          for (int i = 0; i < illegalInserts.size(); i++) {
            edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed((String)illegalInserts.elementAt(i));
            if (e != null) {
              insertsStillPresent = true;
            }

          }

          if (insertsStillPresent) {
//            System.out.println("something inserted or deleted that shouldn't have been");
            return true;
          }
          return false;
        }


	synchronized public void restoreWorld() {
		setIsListening( false );

                java.util.Iterator elementIterator = changedElements.iterator();

                java.util.Iterator objectArrayIterator = this.changedObjectArrayProperties.iterator();

                for (java.util.Iterator iter = changeOrder.iterator(); iter.hasNext(); ) {
                  String changeType = (String) iter.next();
                  if ( changeType.equals(WorldDifferencesCapsule.elementChange) ) {
                    if (elementIterator.hasNext()) {
                      ((UndoableRedoable)elementIterator.next()).undo();
                    }
                  } else if ( changeType.equals(WorldDifferencesCapsule.objectArrayChange) ) {
                    if (objectArrayIterator.hasNext()) {
                      ((UndoableRedoable)objectArrayIterator.next()).undo();
                    }
                  }
                }

		for( java.util.Iterator iter = changedProperties.keySet().iterator(); iter.hasNext(); ) {
			String propertyKey = (String)iter.next();
			Object oldValue = changedProperties.get( propertyKey );

			int dotIndex = propertyKey.lastIndexOf( "." );
			String elementKey = propertyKey.substring( 0, dotIndex );
			String propertyName = propertyKey.substring( dotIndex + 1 );

			edu.cmu.cs.stage3.alice.core.Element propertyOwner = world.getDescendantKeyed( elementKey );
			if( propertyOwner != null ) {
				//propertyOwner.setPropertyNamed( propertyName, oldValue );
                                edu.cmu.cs.stage3.alice.core.Property property = propertyOwner.getPropertyNamed(propertyName);
//                                System.out.println("changing: " + property.getOwner().getKey( world ) + "." + property.getName());
                                property.set(oldValue);
			}
		}

		clear();

		setIsListening( true );
	}

	synchronized public void startListening() {
		authoringTool.addAuthoringToolStateListener( this );
		listenTo( world );
		setIsListening( true );
	}

	synchronized public void stopListening() {
		authoringTool.removeAuthoringToolStateListener( this );
		stopListeningTo( world );
		setIsListening( false );
	}

	synchronized public void dispose() {
		clear();
		stopListening();
		this.authoringTool = null;
		this.world = null;
	}

	synchronized public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		if( this.world != null ) {
			stopListeningTo( this.world );
		}

		this.world = world;
		listenTo( world );
	}

	synchronized public void clear() {
		changedProperties.clear();
		changedObjectArrayProperties.clear();
		changedElements.clear();
//		removedElements.clear();
//		addedElements.clear();
	}

	synchronized protected void setIsListening( boolean isListening ) {
		this.isListening = isListening;
	}

	protected Object preChangeValue;
	synchronized public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
		if( isListening ) {
                        edu.cmu.cs.stage3.alice.core.Property property = propertyEvent.getProperty();
			preChangeValue = propertyEvent.getProperty().get();
		}
	}
	synchronized public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
		if( isListening ) {
			edu.cmu.cs.stage3.alice.core.Property property = propertyEvent.getProperty();
			String propertyRepr = property.getOwner().getKey( world ) + "." + property.getName();
			if( changedProperties.containsKey( propertyRepr ) ) {
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.equals( property.get(), changedProperties.get( propertyRepr ) ) ) {
					// if changing back to original value, remove entry
					changedProperties.remove( propertyRepr );
				}
			} else {
				changedProperties.put( propertyRepr, preChangeValue );
			}
		}
	}

	synchronized public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
	synchronized public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
		if( isListening ) {
			changedObjectArrayProperties.add( 0, new edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable( ev.getObjectArrayProperty(), ev.getChangeType(), ev.getOldIndex(), ev.getNewIndex(), ev.getItem() ) );
                        changeOrder.add(0, objectArrayChange);
		}
	}

	synchronized public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {}
	synchronized public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		if( isListening ) {
			changedElements.add( 0, new edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable( childrenEvent ) );
                        changeOrder.add(0, elementChange);

			int changeType = childrenEvent.getChangeType();
			if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
				listenTo( childrenEvent.getChild() );
			} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
				stopListeningTo( childrenEvent.getChild() );
			}
		}
	}

	synchronized public void listenTo( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for( int i = 0; i < elements.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.Property[] properties = elements[i].getProperties();
				for( int j = 0; j < properties.length; j++ ) {
					if( properties[j] instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)properties[j]).addObjectArrayPropertyListener( this );
					} else {
						properties[j].addPropertyListener( this );
					}
				}
				boolean alreadyChildrenListening = false;
				edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = elements[i].getChildrenListeners();
				for( int j = 0; j < childrenListeners.length; j++ ) {
					if( childrenListeners[j] == this ) {
						alreadyChildrenListening = true;
					}
				}
				if( ! alreadyChildrenListening ) {
					elements[i].addChildrenListener( this );
				}
			}
		}
	}

	synchronized public void stopListeningTo( edu.cmu.cs.stage3.alice.core.Element element ) {
		if( element != null ) {
			edu.cmu.cs.stage3.alice.core.Element[] elements = element.getDescendants();
			for( int i = 0; i < elements.length; i++ ) {
				edu.cmu.cs.stage3.alice.core.Property[] properties = elements[i].getProperties();
				for( int j = 0; j < properties.length; j++ ) {
					if( properties[j] instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
						((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)properties[j]).removeObjectArrayPropertyListener( this );
					} else {
						properties[j].removePropertyListener( this );
					}
				}
				elements[i].removeChildrenListener( this );
			}
		}
	}

	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////
	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		if( ev.getCurrentState() == edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent.RUNTIME_STATE ) {
			setIsListening( false );
		} else {
			setIsListening( true );
		}
	}

	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		//TODO
	}

	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		//TODO
	}

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
}