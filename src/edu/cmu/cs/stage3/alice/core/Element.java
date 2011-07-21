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
 * 2. Redistributions in binary form must reproduce the above right
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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;

public abstract class Element {
	private static java.util.Hashtable s_classnameMap = new java.util.Hashtable();
	static {
		//s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.behavior.SpacepadBehavior", edu.cmu.cs.stage3.alice.core.behavior.tracking.SpacePadBehavior.class );

		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ConditionalLoopSequentialResponse", edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ConditionalSequentialResponse", edu.cmu.cs.stage3.alice.core.response.IfElseInOrder.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.CountLoopSequentialResponse", edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEach.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.OrientationAnimation", edu.cmu.cs.stage3.alice.core.response.ForwardVectorAnimation.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ParallelForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ParallelResponse", edu.cmu.cs.stage3.alice.core.response.DoTogether.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.ProxyForScriptDefinedResponse", edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.SequentialForEachInListSequentialResponse", edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.SequentialResponse", edu.cmu.cs.stage3.alice.core.response.DoInOrder.class );

		s_classnameMap.put( "edu.cmu.cs.stage3.bb2.navigation.KeyboardNavigationBehavior", edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.bb2.navigation.MouseNavigationBehavior", edu.cmu.cs.stage3.alice.core.behavior.MouseLookingBehavior.class );

		s_classnameMap.put( "edu.cmu.cs.stage3.pratt.pose.Pose", edu.cmu.cs.stage3.alice.core.Pose.class );
		s_classnameMap.put( "edu.cmu.cs.stage3.pratt.pose.PoseAnimation", edu.cmu.cs.stage3.alice.core.response.PoseAnimation.class );

		s_classnameMap.put( "edu.cmu.cs.stage3.alice.core.response.MetaResponse", edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class );

		s_classnameMap.put( "edu.cmu.cs.stage3.bb2.navigation.KeyMapping", edu.cmu.cs.stage3.alice.core.navigation.KeyMapping.class );
	}

	public static final double VERSION = 2.001;
	public static final char SEPARATOR = '.';
	private static final String XML_FILENAME = "elementData.xml";

	private static int s_loadProgress = 0;

	public final StringProperty name = new StringProperty( this, "name", null );
	public final BooleanProperty isFirstClass = new BooleanProperty( this, "isFirstClass", Boolean.FALSE );
	public final DictionaryProperty data = new DictionaryProperty( this, "data", null );

	private Element m_parent = null;
	private java.util.Vector m_children = new java.util.Vector();
	private Element[] m_childArray = null;
	private java.util.Vector m_childrenListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] m_childrenListenerArray = null;

	private Object m_xmlFileKeepKey = null;

	static public boolean s_isLoading = false;

	public void markKeepKeyDirty() {
		if( s_isLoading ) {
			//pass
		} else {
			m_xmlFileKeepKey = null;
		}
	}

	private Property[] m_propertyArray = null;

	private boolean m_isReleased = false;
	private boolean m_updateParentsChildren = true;

	public edu.cmu.cs.stage3.alice.scripting.Code compile( String script, Object source, edu.cmu.cs.stage3.alice.scripting.CompileType compileType ) {
		return getWorld().compile( script, source, compileType );
	}
	public Object eval( edu.cmu.cs.stage3.alice.scripting.Code code ) {
		return getWorld().eval( code );
	}
	public void exec( edu.cmu.cs.stage3.alice.scripting.Code code ) {
		getWorld().exec( code );
	}

	private static boolean isPropertyField( java.lang.reflect.Field field ) {
		int modifiers = field.getModifiers();
		if( java.lang.reflect.Modifier.isPublic( modifiers ) ) {
			if( Property.class.isAssignableFrom( field.getType() ) ) {
				return true;
			}
		}
		return false;
	}

	private static final char[] ILLEGAL_NAME_CHARACTERS = { '\t', '\n', '\\', '/', ':', '*', '?', '"', '<', '>', '|', SEPARATOR };

	private static boolean isIllegal( char c ) {
		for( int i=0; i<ILLEGAL_NAME_CHARACTERS.length; i++ ) {
			if( c == ILLEGAL_NAME_CHARACTERS[ i ] ) {
				return true;
			}
		}
		return false;
	}
	private static boolean is8Bit( char c ) {
		int n = (int)c;
		return (n&0xFF)==n;
	}
	public static String generateValidName( String invalidName ) {
		byte[] bytes = invalidName.trim().getBytes();
		StringBuffer sb = new StringBuffer( bytes.length );
		for( int i=0; i<bytes.length; i++ ) {
			char c = (char)bytes[ i ];
			if( is8Bit( c ) ) {
				if( isIllegal( c ) ) {
					c = '_';
				} else {
					//pass
				}
			} else {
				c = '_';
			}
			sb.append( c );
		}
		if( sb.length() == 0 ) {
			sb.append( '_' );
		}
		return sb.toString();
	}
	public static boolean isPotentialNameValid( String nameValue ) {
		if( nameValue == null ) {
			return false;
		}
		if( nameValue.length() == 0  ) {
			return false;
		}
		if( nameValue.trim().length() != nameValue.length() ) {
			return false;
		}
		byte[] bytes = nameValue.getBytes();
		for( int i=0; i<bytes.length; i++ ) {
			char c = (char)bytes[ i ];
			if( is8Bit( c ) ) {
				if( isIllegal( c ) ) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
    private void checkForInvalidName( String nameValue ) {
		if( nameValue==null ) {
			//pass
		} else {
			String trimmedNameValue = nameValue.trim();
			if( trimmedNameValue.length() != nameValue.length() ) {
                //throw new IllegalNameValueException( nameValue, "an element's name cannot begin or end with whitespace." );
                throw new IllegalNameValueException( nameValue, "We're sorry, but names in Alice may not have spaces at the beginning or end." );
			}
			if( nameValue.length() == 0 ) {
				throw new IllegalNameValueException( nameValue, "We're sorry, but names in Alice may not be empty." );
			}
			char[] illegalCharacters = { '\t', '\n', '\\', '/', ':', '*', '?', '"', '<', '>', '|', SEPARATOR };
			for( int i=0; i<illegalCharacters.length; i++ ) {
				if( nameValue.indexOf( illegalCharacters[i] )!=-1 ) {
                    //throw new IllegalNameValueException( nameValue, "an element's name cannot contain any of the following characters: " + new String( illegalCharacters ) );
                    throw new IllegalNameValueException( nameValue, "We're sorry, but names in Alice may only contain letters and numbers.  The character \"" + illegalCharacters[i] + "\" can not be used in a name." );
				}
			}

			byte[] bytes = nameValue.getBytes();
			for( int i=0; i<bytes.length; i++ ) {
				if( is8Bit( (char)bytes[ i ] ) ) {
					//pass
				} else {
					char c;
					try {
						c = nameValue.charAt( i );
					} catch( Throwable t ) {
						c = (char)bytes[ i ];
					}
					throw new IllegalNameValueException( nameValue, "We're sorry, but names in Alice may only contain letters and numbers.  The character \"" + c + "\" can not be used in a name." );
				}
			}
        }
    }
	private void checkForNameCollision( Element parentValue, String nameValue ) {
		if( parentValue==null || nameValue==null ) {
			//pass
		} else {
			Element siblingToBe = parentValue.getChildNamedIgnoreCase( nameValue );
//			System.out.println("\n\n"+nameValue+", "+parentValue.hashCode());
//			Thread.dumpStack();
			if( siblingToBe!=null && siblingToBe!=this ) {
				throw new IllegalNameValueException( nameValue, "Unfortunately, something else in this world is already named \"" + nameValue + ",\" so you can't use that name here." );
			} 
		}
	}

    public void HACK_nameChanged() {
        markKeepKeyDirty();
        Element[] children = getChildren();
        for( int i=0; i<children.length; i++ ) {
            children[ i ].HACK_nameChanged();
        }
    }

	protected void nameValueChanging( String nameValueToBe ) {
		checkForInvalidName( nameValueToBe );
		checkForNameCollision( m_parent, nameValueToBe );
	}
	protected void nameValueChanged( String value ) {
		PropertyReference[] propertyReferences = getExternalPropertyReferences( HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
		for( int i=0; i<propertyReferences.length; i++ ) {
			propertyReferences[ i ].getProperty().getOwner().markKeepKeyDirty();
		}
	}

	public void propertyCreated( Property property ) {
		markKeepKeyDirty();
	}
	protected void propertyChanging( Property property, Object value ) {
		if( property == name ) {
			nameValueChanging( (String)value );
		}
	}
	protected void propertyChanged( Property property, Object value ) {
		if( property == name ) {
			nameValueChanged( (String)value );
		}
	}
	public final void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
		if( isReleased() ) {
			throw new RuntimeException( "property change attempted on released element: " + propertyEvent.getProperty() );
		}
		propertyChanging( propertyEvent.getProperty(), propertyEvent.getValue() );
	}
	public final void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent ) {
		if( isReleased() ) {
			throw new RuntimeException( "property changed on released element" + propertyEvent.getProperty() );
		}
		propertyChanged( propertyEvent.getProperty(), propertyEvent.getValue() );
        if( propertyEvent.getProperty() == name ) {
            getRoot().HACK_nameChanged();
        }
	}

    protected void internalRelease( int pass ) {
		m_isReleased = true;
		Element[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			children[i].internalRelease( pass );
		}
    }
	public final void release() {
		if( !m_isReleased ) {
            for( int pass=0; pass<3; pass++ ) {
                internalRelease( pass );
            }
		}
	}
	public boolean isReleased() {
		return m_isReleased;
	}
	protected void finalize() throws Throwable {
		if( !isReleased() ) {
			release();
		}
		super.finalize();
	}

    private class Property_Value {
        private Property m_property;
        private Element m_value;
        public Property_Value( Property property, Element value ) {
            m_property = property;
            m_value = value;
        }
        public Property getProperty( Element[] originals, Element[] replacements ) {
            Element propertyOwner = m_property.getOwner();
            int index = propertyOwner.indexIn( originals );
            if( index != -1 ) {
                return replacements[ index ].getPropertyNamed( m_property.getName() );
            } else {
                return m_property;
            }
        }
        public Element getValue( Element[] originals, Element[] replacements ) {
            if( m_value instanceof Element ) {
                int index = ((Element)m_value).indexIn( originals );
                if( index != -1 ) {
                    return replacements[ index ];
                }
            }
            return m_value;
        }
        public String toString() {
            return m_property + " " + m_value;
        }
    }
    private class ObjectArrayProperty_Value_Index extends Property_Value {
        private int m_index;
        public ObjectArrayProperty_Value_Index( ObjectArrayProperty objectArrayProperty, Element value, int index ) {
            super( objectArrayProperty, value );
            m_index = index;
        }
        public ObjectArrayProperty getObjectArrayProperty( Element[] originals, Element[] replacements ) {
            return (ObjectArrayProperty)getProperty( originals, replacements );
        }
        public int getIndex() {
            return m_index;
        }
        public String toString() {
            return super.toString() + " " + m_index;
        }
    }

    private int indexIn( Element[] array ) {
        for( int i=0; i<array.length; i++ ) {
            if( this == array[ i ] ) {
                return i;
            }
        }
        return -1;
    }
    private void clearAllReferences( Element[] originals, Element[] replacements, Element[] childrenWithNoReplacements, java.util.Vector toBeResolved ) {
        Element root = getRoot();
        Element[] descendants = root.getDescendants( Element.class, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
        for( int i=0; i<descendants.length; i++ ) {
            Element descendant = descendants[ i ];
            Property[] properties = descendant.getProperties();
            for( int j=0; j<properties.length; j++ ) {
                Property property = properties[ j ];
                if( properties[ j ] instanceof ObjectArrayProperty ) {
                    ObjectArrayProperty oap = (ObjectArrayProperty)property;
                    for( int k=0; k<oap.size(); k++ ) {
                        Object valueK = oap.get( k );
                        if( valueK instanceof Element ) {
                            Element elementK = (Element)valueK;
                            ObjectArrayProperty_Value_Index oap_v_i = null;
                            int index = elementK.indexIn( originals );
                            if( index != -1 ) {
                                oap_v_i = new ObjectArrayProperty_Value_Index( oap, replacements[ index ], k );
                            } else {
                                index = elementK.indexIn( childrenWithNoReplacements );
                                if( index != -1 ) {
                                    oap_v_i = new ObjectArrayProperty_Value_Index( oap, childrenWithNoReplacements[ index ], k );
                                }
                            }
                            if( oap_v_i != null ) {
                                toBeResolved.addElement( oap_v_i );
                                oap.set( k, null );
                            }
                        }
                    }
                } else {
                    Object value = properties[ j ].get();
                    if( value instanceof Element ) {
                        Element element = (Element)value;
                        Property_Value p_v = null;
                        int index = element.indexIn( originals );
                        if( index != -1 ) {
                            p_v = new Property_Value( property, replacements[ index ] );
                        } else {
                            index = element.indexIn( childrenWithNoReplacements );
                            if( index != -1 ) {
                                p_v = new Property_Value( property, childrenWithNoReplacements[ index ] );
                            }
                        }
                        if( p_v != null ) {
                            toBeResolved.addElement( p_v );
                            property.set( null );
                        }
                    }
                }
            }
        }
    }

    private void replace( Element[] originals, Element[] replacements ) {
        for( int i=0; i<originals.length; i++ ) {
            Element original = originals[ i ];
            Element replacement = replacements[ i ];
            Element parent;
            if( original != null ) {
                parent = original.getParent();
                original.setParent( null );
            } else {
                //todo
                System.err.println( "WARNING: original is null for " + replacement );
                parent = null;
            }
            if( parent != null ) {
                int index = parent.indexIn( originals );
                if( index != -1 ) {
                    parent = replacements[ index ];
                }
            }
            replacement.setParent( parent );
        }
    }

    private Element[] getChildrenThatHaveNoReplacements( Element[] originals, Element[] replacements ) {
        java.util.Vector vector = new java.util.Vector();
        for( int i=0; i<originals.length; i++ ) {
            Element original = originals[ i ];
            Element replacement = replacements[ i ];
            if( original != null ) {
                for( int j=0; j<original.getChildCount(); j++ ) {
                    Element childJ = original.getChildAt( j );
                    int index = childJ.indexIn( originals );
                    if( index == -1 ) {
                        vector.addElement( childJ );
                    }
                }
            }
        }
        Element[] array = new Element[ vector.size() ];
        vector.copyInto( array );
        return array;
    }

    private void addChildrenThatHaveNoReplacement( Element[] originals, Element[] replacements, Element[] childrenThatHaveNoReplacements ) {
/*
        for( int i=0; i<childrenThatHaveNoReplacements.length; i++ ) {
            Element childThatHasNoReplacement = childrenThatHaveNoReplacements[ i ];
            Element originalParent = childThatHasNoReplacement.getParent();
            int index = originalParent.indexIn( originals );
            Element replacementParent = replacements[ index ];
        }
    }
*/
        for( int i=0; i<originals.length; i++ ) {
            Element original = originals[ i ];
            Element replacement = replacements[ i ];
            if( original != null ) {
                for( int j=0; j<original.getChildCount(); j++ ) {
                    Element childJ = original.getChildAt( j );
                    int index = childJ.indexIn( originals );
                    if( index == -1 ) {
                        childJ.setParent( replacement );
                    }
                }
            }
        }
    }

    public void replaceWith( Element replacement ) {
        Element[] replacements = replacement.getDescendants( Element.class, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
        Element[] originals = new Element[ replacements.length ];
        for( int i=0; i<replacements.length; i++ ) {
            String replacementKey = replacements[ i ].getKey( replacement );
            originals[ i ] = getDescendantKeyed( replacementKey );
        }

        Element[] childrenThatHaveNoReplacements = getChildrenThatHaveNoReplacements( originals, replacements );
        java.util.Vector toBeResolved = new java.util.Vector();
        clearAllReferences( originals, replacements, childrenThatHaveNoReplacements, toBeResolved );

        replace( originals, replacements );

        addChildrenThatHaveNoReplacement( originals, replacements, childrenThatHaveNoReplacements );

        for( int i=0; i<toBeResolved.size(); i++ ) {
            Property_Value p_v = (Property_Value)toBeResolved.elementAt( i );
            if( p_v instanceof ObjectArrayProperty_Value_Index ) {
                ObjectArrayProperty_Value_Index oap_v_i = (ObjectArrayProperty_Value_Index)p_v;
                oap_v_i.getObjectArrayProperty( originals, replacements ).set( oap_v_i.getIndex(), oap_v_i.getValue( originals, replacements ) );
            } else {
                p_v.getProperty( originals, replacements ).set( p_v.getValue( originals, replacements ) );
            }
        }
    }
    public Class[] getSupportedCoercionClasses() {
        return null;
    }
    public boolean isCoercionSupported() {
        Class[] classes = getSupportedCoercionClasses();
        return classes!=null && classes.length>0;
    }
    public Element coerceTo( Class cls ) {
        World world = this.getWorld();
        PropertyReference[] propertyReferences = {};
        String[] keys = {};
        if( world != null ) {
            propertyReferences = world.getPropertyReferencesTo( this, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, false );
            keys = new String[ propertyReferences.length ];
        }
        for( int i=0; i<propertyReferences.length; i++ ) {
            PropertyReference propertyReference = propertyReferences[ i ];
            Element reference = propertyReference.getReference();
            keys[ i ] = reference.getKey( world );
            if( propertyReference instanceof ObjectArrayPropertyReference ) {
                ObjectArrayPropertyReference objectArrayPropertyReference = (ObjectArrayPropertyReference)propertyReference;
                ObjectArrayProperty objectArrayProperty = objectArrayPropertyReference.getObjectArrayProperty();
                objectArrayProperty.set( objectArrayPropertyReference.getIndex(), null );
            } else {
                Property property = propertyReference.getProperty();
                property.set( null );
                //System.out.println( "null->"+property );
            }
        }
        Element coercedElement;
        try {
            coercedElement = (Element)cls.newInstance();
        } catch( IllegalAccessException iae ) {
            throw new ExceptionWrapper( iae, cls.toString() );
        } catch( InstantiationException ie ) {
            throw new ExceptionWrapper( ie, cls.toString() );
        }
        Element parentValue = getParent();
        if( parentValue != null ) {
            int indexOfChild = parentValue.getIndexOfChild( this );
            setParent( null );
            parentValue.insertChildAt( coercedElement, indexOfChild );
        }

        Element[] children = getChildren();
        for( int i=0; i<children.length; i++ ) {
            children[i].setParent( coercedElement );
        }
        Property[] properties = getProperties();
        for( int i=0; i<properties.length; i++ ) {
            Property property = properties[i];
            Property cProperty = coercedElement.getPropertyNamed( property.getName() );
            if( cProperty != null ) {
                cProperty.set( property.get() );
            }
        }
        for( int i=0; i<propertyReferences.length; i++ ) {
            PropertyReference propertyReference = propertyReferences[ i ];
            String key = keys[ i ];
            Element reference = world.getDescendantKeyed( key );
            Property property = propertyReference.getProperty();
            if( property.getOwner() == this ) {
                property = coercedElement.getPropertyNamed( property.getName() );
            }
            if( property != null ) {
                if( propertyReference instanceof ObjectArrayPropertyReference ) {
                    ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)property;
                    objectArrayProperty.set( ((ObjectArrayPropertyReference)propertyReference).getIndex(), reference );
                } else {
                    property.set( reference );
                }
            }
        }
        return coercedElement;
    }
	public Property[] getProperties() {
		if( m_propertyArray == null ) {
			Class cls = getClass();
			java.util.Vector properties = new java.util.Vector();
			java.lang.reflect.Field[] fields = cls.getFields();
			for( int i=0; i<fields.length; i++ ) {
				java.lang.reflect.Field field = fields[i];
				if( isPropertyField( field ) ) {
					try {
						Property property = (Property)field.get( this );
						if( property!=null ) {
							if( property.isDeprecated() ) {
								//pass
							} else {
								properties.addElement( property );
							}
						} else {
							debugln( "warning: cannot find property field: " + field.getName() );
						}
					} catch( IllegalAccessException iae ) {
						iae.printStackTrace();
					}
				}
			}

			m_propertyArray = new Property[ properties.size() ];
			properties.copyInto( m_propertyArray );
		}
		return m_propertyArray;
	}

	public Property getPropertyNamed( String name ) {
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			Property property = properties[i];
			if( property.getName().equals( name ) ) {
				return property;
			}
		}
		return null;
		//throw new RuntimeException( "no property named " + name );
	}
	public Property getPropertyNamedIgnoreCase( String name ) {
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			Property property = properties[i];
			if( property.getName().equals( name ) ) {
				return property;
			}
		}
		return null;
		//throw new RuntimeException( "no property named " + name );
	}

	public Element getParent() {
		return m_parent;
	}

	public void setParent( Element parentValue ) {
		if( parentValue != m_parent ) {
			if( m_parent != null ) {
				//m_parent.removeChild( this );
				m_parent.internalRemoveChild( this );
                m_parent = null;
			}
			if( parentValue != null ) {
				parentValue.addChild( this );
			}
		}
	}

    private void checkAllPropertiesForBadReferences() {
        Property[] properties = getProperties();
        for( int i=0; i<properties.length; i++ ) {
            Property property = properties[ i ];
            property.checkForBadReferences( property.get() );
        }
        for( int i=0; i<getChildCount(); i++ ) {
            getChildAt( i ).checkAllPropertiesForBadReferences();
        }
    }
	protected void internalSetParent( Element parentValue ) {
//        if( m_parent != null ) {
//            World oldWorld = m_parent.getWorld();
//            if( oldWorld != null ) {
//                World newWorld;
//                if( parentValue != null ) {
//                    newWorld = parentValue.getWorld();
//                } else {
//                    newWorld = null;
//                }
//                if( oldWorld != newWorld ) {
//                    PropertyReference[] propertyReferences = oldWorld.getPropertyReferencesTo( this, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true );
//                    if( propertyReferences.length > 0 ) {
//                        StringBuffer sb = new StringBuffer();
//                        for( int i=0; i<propertyReferences.length; i++ ) {
//                            sb.append( '\n' );
//                            sb.append( propertyReferences[ i ] );
//                        }
//                        throw new RuntimeException( this + "cannot set its parent to " + parentValue + ".  It has references pointing to it. " + sb.toString() );
//                    }
//                }
//            }
//        }
		if( parentValue != null ) {
			checkForNameCollision( parentValue, name.getStringValue() );
			if( parentValue == this ) {
				throw new RuntimeException( this + " cannot be its own parent." );
			}
			if( parentValue.isDescendantOf( this ) ) {
				throw new RuntimeException( this + " cannot have descendant " + parentValue + " as its parent." );
			}
		}
		Element prevParent = m_parent;
		m_parent = parentValue;
		try {
			checkAllPropertiesForBadReferences();
		} catch( RuntimeException re ) {
			m_parent = prevParent;
			throw re;
		}
	}

	public Element getRoot() {
		if( m_parent==null ) {
			return this;
		} else {
			return m_parent.getRoot();
		}
	}

	public World getWorld() {
		Element root = getRoot();
		if( root instanceof World ) {
			return (World)root;
		} else {
			return null;
		}
	}
	public Sandbox getSandbox() {
		if( this instanceof Sandbox ) {
			if( this instanceof World ) {
				return (World)this;
			} else if( m_parent instanceof World || (m_parent instanceof Group) ) {
				return (Sandbox)this;
			}
		}
		if( m_parent == null ) {
			return null;
		} else {
			return m_parent.getSandbox();
		}
	}
	public boolean isDescendantOf( Element element ) {
		Element parentValue = getParent();
		while( parentValue!=null ) {
			if( parentValue==element ) {
				return true;
			}
			parentValue = parentValue.getParent();
		}
		return false;
	}
	public boolean isAncestorOf( Element element ) {
		if( element != null ) {
			return element.isDescendantOf( this );
		} else {
			return false;
		}
	}

	private void buildDetailedPath( StringBuffer sb ) {
		if( m_parent != null ) {
			m_parent.buildDetailedPath( sb );
		}
		sb.append( "\t" );
		sb.append( name.getStringValue() );
		sb.append( " " );
		sb.append( getClass() );
		sb.append( "\n" );
	}
	private String getInternalGetKeyExceptionDescription( Element ancestor, Element self, StringBuffer sbKey ) {
		StringBuffer sb = new StringBuffer();
		sb.append( "Could not find ancestor: " );
		if( ancestor != null ) {
			sb.append( ancestor.name.getStringValue() );
			sb.append( ", class: " );
			sb.append( ancestor.getClass() );
		} else {
			sb.append( "null" );
		}
		sb.append( "\nKey: " );
		sb.append( sbKey );
		sb.append( "\nDetails: " );
		self.buildDetailedPath( sb );
		return sb.toString();
	}

	private void internalGetKey( Element ancestor, Element self, StringBuffer sb ) {
		if( m_parent == ancestor ) {
			//pass
		} else {
			if( this != ancestor ) {
				if( m_parent==null && ancestor!=null ) {
					throw new RuntimeException( getInternalGetKeyExceptionDescription( ancestor, self, sb ) );
					//throw new RuntimeException( name.getStringValue() + " (hashCode=" + hashCode()+ ") did not find ancestor " + ancestor.name.getStringValue() + " (hashCode="+ancestor.hashCode()+")" );
				}
				m_parent.internalGetKey( ancestor, self, sb );
			}
		}
		if( this != ancestor ) {
			sb.append( getRepr() );
		}
		if( this != self ) {
			sb.append( SEPARATOR );
		}
	}
	public String getKey( Element ancestor ) {
		StringBuffer sb = new StringBuffer();
		internalGetKey( ancestor, this, sb );
		return new String( sb );
	}
	public String getKey() {
		return getKey( null );
	}

	public String getTrimmedKey() {
		Element ancestor = getSandbox();
		if( ancestor != null ) {
			ancestor = ancestor.getParent();
		}
		return getKey( ancestor );
	}

	//convenience method
	public void addPropertyListenerToAllProperties( edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener ) {
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			Property property = properties[i];
			property.addPropertyListener( propertyListener );
		}
	}
	//convenience method
	public void removePropertyListenerFromAllProperties( edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener ) {
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			Property property = properties[i];
			property.removePropertyListener( propertyListener );
		}
	}
	public int getChildCount() {
		return m_children.size();
	}
	public Element getChildAt( int index ) {
        if( index >= m_children.size() ) {
            warnln( this + ".getChildAt( " + index + " ) is out of range [0,"+m_children.size()+")." );
            return null;
        }
		return (Element)m_children.elementAt( index );
	}
	public int getIndexOfChild( Element child ) {
		return m_children.indexOf( child );
	}
	public boolean hasChild( Element child ) {
		return m_children.contains( child );
	}
	private Element internalGetChildNamed( String nameValue, boolean ignoreCase ) {
		if( nameValue != null ) {
			if( nameValue.startsWith( "__Unnamed" ) && nameValue.endsWith( "__" ) ) {
				Element child = getChildAt( Integer.parseInt( nameValue.substring( 9, nameValue.length()-2 ) ) );
                if( child != null ) {
                    if( child.name.get() == null ) {
                        return child;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
			} else {
				for( int i=0; i<getChildCount(); i++ ) {
					Element child = getChildAt( i );
					if( nameValue!=null ) {
						boolean found;
						if( ignoreCase ) {
							found = nameValue.equalsIgnoreCase( child.name.getStringValue() );
						} else {
							found = nameValue.equals( child.name.getStringValue() );
						}
						if( found ) {
							return child;
						}
					} else {
						if( child.name.getStringValue()==null ) {
							return child;
						}
					}
				}
				return null;
			}
		} else {
			return null;
		}
	}
	public Element getChildNamed( String nameValue ) {
		return internalGetChildNamed( nameValue, false );
	}
	public Element getChildNamedIgnoreCase( String nameValue ) {
		return internalGetChildNamed( nameValue, true );
	}
	private Element internalGetDescendantKeyed( String key, int fromIndex, boolean ignoreCase ) {
		if( key.equals( "" ) ) {
			return this;
		} else {
			int toIndex = key.indexOf( SEPARATOR, fromIndex );
			if( toIndex == -1 ) {
				String childName = key.substring( fromIndex );
				return internalGetChildNamed( childName, ignoreCase );
			} else {
				String childName = key.substring( fromIndex, toIndex );
				Element child = internalGetChildNamed( childName, ignoreCase );
				if( child != null ) {
					return child.internalGetDescendantKeyed( key, toIndex+1, ignoreCase );
				} else {
					return null;
				}
			}
		}
	}
	public Element getDescendantKeyed( String key ) {
		return internalGetDescendantKeyed( key, 0, false );
	}
	public Element getDescendantKeyedIgnoreCase( String key ) {
		return internalGetDescendantKeyed( key, 0, true );
	}
	public Element[] getChildren() {
		if( m_childArray==null ) {
			m_childArray = new Element[m_children.size()];
			m_children.copyInto( m_childArray );
		}
		return m_childArray;
	}
	public Element[] getChildren( Class cls ) {
		java.util.Vector v = new java.util.Vector();
		for( int i=0; i<m_children.size(); i++ ) {
			Object child = m_children.elementAt( i );
			if( cls.isAssignableFrom( child.getClass() ) ) {
				v.addElement( child );
			}
		}
		Element[] array = new Element[v.size()];
		v.copyInto( array );
		return array;
	}

	protected int internalGetElementCount( Class cls, HowMuch howMuch, int count ) {
		if( cls.isAssignableFrom( getClass() ) ) {
			count++;
		}
		for( int i=0; i<getChildCount(); i++ ) {
			count = getChildAt( i ).internalGetElementCount( cls, howMuch, count );
		}
		return count;
	}
	public int getElementCount( Class cls, HowMuch howMuch ) {
		return internalGetElementCount( cls, howMuch, 0 );
	}
	public int getElementCount( Class cls ) {
		return getElementCount( cls, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}
	public int getElementCount() {
		return getElementCount( Element.class );
	}

	public void internalSearch( Criterion criterion, HowMuch howMuch, java.util.Vector v ) {
		if( criterion.accept( this ) ) {
			v.addElement( this );
		}
		for( int i=0; i<getChildCount(); i++ ) {
			getChildAt( i ).internalSearch( criterion, howMuch, v );
		}
	}
	public Element[] search( Criterion criterion, HowMuch howMuch ) {
		java.util.Vector v = new java.util.Vector();
		internalSearch( criterion, howMuch, v );
		Element[] array = new Element[ v.size() ];
		v.copyInto( array );
		return array;
	}
	public Element[] search( Criterion criterion ) {
		return search( criterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}

	public Element[] getDescendants( Class cls, HowMuch howMuch ) {
		Element[] elements = search( new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( cls ), howMuch );
		if( cls == Element.class ) {
			return elements;
		} else {
			Object array = java.lang.reflect.Array.newInstance( cls, elements.length );
			System.arraycopy( elements, 0, array, 0, elements.length );
			return (Element[])array;
		}
	}
	public Element[] getDescendants( Class cls ) {
		return getDescendants( cls, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}
    public Element[] getDescendants() {
        return getDescendants( Element.class );
    }

	public void setPropertyNamed( String name, Object value, HowMuch howMuch ) {
		Property property = getPropertyNamed( name );
		if( property!=null ) {
			property.set( value, howMuch );
		} else {
			for( int i=0; i<m_children.size(); i++ ) {
				Element child = getChildAt( i );
				child.setPropertyNamed( name, value, howMuch );
			}
		}
	}
	public void setPropertyNamed( String name, Object value ) {
		setPropertyNamed( name, value, HowMuch.INSTANCE_AND_PARTS );
	}

	private void onChildrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		java.util.Enumeration enum0 = m_childrenListeners.elements();
		while( enum0.hasMoreElements() ) {
			edu.cmu.cs.stage3.alice.core.event.ChildrenListener childrenListener = (edu.cmu.cs.stage3.alice.core.event.ChildrenListener)enum0.nextElement();
			childrenListener.childrenChanging( childrenEvent );
		}
	}
	private void onChildrenChange( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		m_childArray = null;
		markKeepKeyDirty();
		java.util.Enumeration enum0 = m_childrenListeners.elements();
		while( enum0.hasMoreElements() ) {
			edu.cmu.cs.stage3.alice.core.event.ChildrenListener childrenListener = (edu.cmu.cs.stage3.alice.core.event.ChildrenListener)enum0.nextElement();
			childrenListener.childrenChanged( childrenEvent );
		}
	}
	private boolean internalRemoveChild( Element child ) {
		int oldIndex = m_children.indexOf( child );
		if( oldIndex!=-1 ) {
			edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent = new edu.cmu.cs.stage3.alice.core.event.ChildrenEvent( this, child, edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED, oldIndex, -1 );
			onChildrenChanging( childrenEvent );
			m_children.removeElementAt( oldIndex );
			onChildrenChange( childrenEvent );
			return true;
		} else {
			return false;
		}
	}
	public void insertChildAt( Element child, int index ) {
		if( child.getParent()==this ) {
			int oldIndex = m_children.indexOf( child );
			if( index!=oldIndex ) {
				edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent = new edu.cmu.cs.stage3.alice.core.event.ChildrenEvent( this, child, edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED, oldIndex, index );
				onChildrenChanging( childrenEvent );
				m_children.removeElementAt( oldIndex );
                if( index == -1 ) {
                    index = m_children.size();
                } else {
                    //todo?
                }
				m_children.insertElementAt( child, index );
				onChildrenChange( childrenEvent );
			}
		} else {
			if( index==-1 ) {
				index = m_children.size();
			}
			if( m_children.contains( child ) ) {
				throw new RuntimeException( child + " is already a child of " + this );
			}
			child.internalSetParent( this );
			edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent = new edu.cmu.cs.stage3.alice.core.event.ChildrenEvent( this, child, edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED, -1, index );
			onChildrenChanging( childrenEvent );
			m_children.insertElementAt( child, index );
			onChildrenChange( childrenEvent );
		}
	}
	public void addChild( Element child ) {
		insertChildAt( child, -1 );
	}
	public void removeChild( Element child ) {
		if( internalRemoveChild( child ) ) {
			child.internalSetParent( null );
		} else {
			warnln( "WARNING: could not remove child " + child + ".  it is not a child of " + this );
		}
	}
	public void addChildrenListener( edu.cmu.cs.stage3.alice.core.event.ChildrenListener childrenListener ) {
		m_childrenListeners.addElement( childrenListener );
		m_childrenListenerArray = null;
	}
	public void removeChildrenListener( edu.cmu.cs.stage3.alice.core.event.ChildrenListener childrenListener ) {
		m_childrenListeners.removeElement( childrenListener );
		m_childrenListenerArray = null;
	}
	public edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] getChildrenListeners() {
		if( m_childrenListenerArray==null ) {
			m_childrenListenerArray = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener[m_childrenListeners.size()];
			m_childrenListeners.copyInto( m_childrenListenerArray );
		}
		return m_childrenListenerArray;
	}

	public void visit( edu.cmu.cs.stage3.util.VisitListener visitListener, HowMuch howMuch ) {
		visitListener.visited( this );
		if( howMuch.getDescend() ) {
			for( int i=0; i<getChildCount(); i++ ) {
				Element child = getChildAt( i );
				if( howMuch.getRespectDescendant() && child.isFirstClass.booleanValue() ) {
					//pass
				} else {
					child.visit( visitListener, howMuch );
				}
			}
		}
	}

	public boolean isReferenceInternalTo( Element whom ) {
		return ( this == whom || this.isDescendantOf( whom ) );
	}
	public boolean isReferenceExternalFrom( Element whom ) {
		return !isReferenceInternalTo( whom );
	}

	protected void internalGetExternalPropertyReferences( Element whom, HowMuch howMuch, java.util.Vector references ) {
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			if( properties[ i ] instanceof ObjectArrayProperty ) {
				ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)properties[ i ];
				int precedingTotal = 0;
				for( int j=0; j<objectArrayProperty.size(); j++ ) {
					Object o = objectArrayProperty.get( j );
					if( o instanceof Element ) {
						if( ((Element)o).isReferenceExternalFrom( whom ) ) {
							references.addElement( new ObjectArrayPropertyReference( objectArrayProperty, null, j, precedingTotal++ ) );
						}
					}
				}
			} else {
				Object o = properties[ i ].get();
				if( o instanceof Element ) {
					if( ((Element)o).isReferenceExternalFrom( whom ) ) {
						references.addElement( new PropertyReference( properties[ i ], null ) );
					}
				}
			}
		}
		if( howMuch.getDescend() ) {
			for( int i=0; i<getChildCount(); i++ ) {
				Element child = getChildAt( i );
				if( child.isFirstClass.booleanValue() && howMuch.getRespectDescendant() ) {
					//pass
				} else {
					child.internalGetExternalPropertyReferences( whom, howMuch, references );
				}
			}
		}
	}

	public PropertyReference[] getExternalPropertyReferences( HowMuch howMuch ) {
		java.util.Vector references = new java.util.Vector();
		internalGetExternalPropertyReferences( this, howMuch, references );
		PropertyReference[] referencesArray = new PropertyReference[ references.size() ];
		references.copyInto( referencesArray );
		return referencesArray;
	}
	public PropertyReference[] getExternalPropertyReferences() {
		return getExternalPropertyReferences( HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}

	protected void internalGetPropertyReferencesTo( Element whom, HowMuch howMuch, boolean excludeWhomsParent, boolean excludeWhomAndItsDescendants, java.util.Vector references ) {
		if( excludeWhomAndItsDescendants && ( this==whom || this.isDescendantOf( whom ) ) ) {
			return;
		} else if( this == whom.getParent() && excludeWhomsParent ) {
			//pass
		} else {
			Property[] properties = getProperties();
			for( int i=0; i<properties.length; i++ ) {
				if( properties[ i ] instanceof ObjectArrayProperty ) {
					ObjectArrayProperty objectArrayProperty = (ObjectArrayProperty)properties[ i ];
					int precedingTotal = 0;
					for( int j=0; j<objectArrayProperty.size(); j++ ) {
						Object o = objectArrayProperty.get( j );
						if( o instanceof Element ) {
							if( ((Element)o).isReferenceInternalTo( whom ) ) {
								references.addElement( new ObjectArrayPropertyReference( objectArrayProperty, null, j, precedingTotal++ ) );
							}
						}
					}
				} else {
					Object o = properties[ i ].get();
					if( o instanceof Element ) {
						if( ((Element)o).isReferenceInternalTo( whom ) ) {
							references.addElement( new PropertyReference( properties[ i ], null ) );
						}
					}
				}
			}
		}
		if( howMuch.getDescend() ) {
			for( int i=0; i<getChildCount(); i++ ) {
				Element child = getChildAt( i );
				if( child.isFirstClass.booleanValue() && howMuch.getRespectDescendant() ) {
					//pass
				} else {
					child.internalGetPropertyReferencesTo( whom, howMuch, excludeWhomsParent, excludeWhomAndItsDescendants, references );
				}
			}
		}
	}

	public PropertyReference[] getPropertyReferencesTo( Element whom, HowMuch howMuch, boolean excludeWhomsParent, boolean excludeWhomAndItsDescendants ) {
		java.util.Vector references = new java.util.Vector();
		internalGetPropertyReferencesTo( whom, howMuch, excludeWhomsParent, excludeWhomAndItsDescendants, references );
		PropertyReference[] referencesArray = new PropertyReference[ references.size() ];
		references.copyInto( referencesArray );
		return referencesArray;
	}
	public PropertyReference[] getPropertyReferencesTo( Element whom, HowMuch howMuch, boolean excludeWhomsParent ) {
		return getPropertyReferencesTo( whom, howMuch, excludeWhomsParent, true );
	}
	public PropertyReference[] getPropertyReferencesTo( Element whom, HowMuch howMuch ) {
		return getPropertyReferencesTo( whom, howMuch, true );
	}
	public PropertyReference[] getPropertyReferencesTo( Element whom ) {
		return getPropertyReferencesTo( whom, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}

	public PropertyReference[] getPropertyReferencesToMe( Element fromWhom, HowMuch howMuch, boolean excludeMyParent, boolean excludeMeAndMyDescendants ) {
		return fromWhom.getPropertyReferencesTo( this, howMuch, excludeMyParent, excludeMeAndMyDescendants );
	}
	public PropertyReference[] getPropertyReferencesToMe( Element fromWhom, HowMuch howMuch, boolean excludeMyParent ) {
		return getPropertyReferencesToMe( fromWhom, howMuch, excludeMyParent, true );
	}
	public PropertyReference[] getPropertyReferencesToMe( Element fromWhom, HowMuch howMuch ) {
		return getPropertyReferencesToMe( fromWhom, howMuch, true );
	}
	public PropertyReference[] getPropertyReferencesToMe( Element fromWhom ) {
		return getPropertyReferencesToMe( fromWhom, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}
	public PropertyReference[] getPropertyReferencesToMe() {
		return getPropertyReferencesToMe( getRoot() );
	}

	/*
    public class ExternalReferenceException extends Exception {
		PropertyReference[] m_externalReferences;
		public ExternalReferenceException( PropertyReference[] externalReferences ) {
			m_externalReferences = externalReferences;
		}
		public PropertyReference[] getExternalReferences() {
			return m_externalReferences;
		}
	}
    */

	public void removeFromParentsProperties() { //throws ExternalReferenceException {
		Element parentValue = getParent();
		if( parentValue!= null ) {
			PropertyReference[] parentReferences = parentValue.getPropertyReferencesTo( this, HowMuch.INSTANCE, false, true );
			for( int i=0; i<parentReferences.length; i++ ) {
				if( parentReferences[ i ] instanceof ObjectArrayPropertyReference ) {
					ObjectArrayPropertyReference oapr = (ObjectArrayPropertyReference)parentReferences[ i ];
					oapr.getObjectArrayProperty().remove( oapr.getIndex() - oapr.getPrecedingTotal() );
				} else {
					parentReferences[ i ].getProperty().set( null );
				}
			}
		}
	}

	public void removeFromParent() { //throws ExternalReferenceException {
		Element root = getRoot();
		PropertyReference[] externalReferences = root.getPropertyReferencesTo( this, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true );
		if( externalReferences.length > 0 ) {
			StringBuffer sb = new StringBuffer();
			sb.append( "ExternalReferenceException:\n" );
			for( int i=0; i<externalReferences.length; i++ ) {
				sb.append( externalReferences[ i ] );
				sb.append( "\n" );
			}
			throw new RuntimeException( sb.toString() );
		}
        removeFromParentsProperties();
        setParent( null );
	}
	
	public void HACK_removeFromParentWithoutCheckingForExternalReferences() {
		removeFromParentsProperties();
		setParent( null );
	}
	
	public boolean isAssignableToOneOf( Class[] classes ) {
		if( classes != null ) {
			Class cls = getClass();
			for( int i=0; i<classes.length; i++ ) {
				if( classes[i].isAssignableFrom( cls ) ) {
					return true;
				}
			}
		}
		return false;
	}
	private CopyFactory createCopyFactory( Class[] classesToShare, HowMuch howMuch, Element internalReferenceRoot ) {
		return new CopyFactory( this, internalReferenceRoot, classesToShare, howMuch );
	}
	public CopyFactory createCopyFactory( Class[] classesToShare, HowMuch howMuch ) {
		return createCopyFactory( classesToShare, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, this );
	}
	public CopyFactory createCopyFactory( Class[] classesToShare ) {
		return createCopyFactory( classesToShare, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}
	public CopyFactory createCopyFactory() {
		return createCopyFactory( null );
	}

	public Element HACK_createCopy( String name, Element parent, int index, Class[] classesToShare, Element parentToBe ) {
		CopyFactory copyFactory = createCopyFactory( classesToShare );
		try {
			Element dst = copyFactory.manufactureCopy( getRoot(), null, null, parentToBe );
			dst.name.set( name );
			if( parent != null ) {
				parent.insertChildAt( dst, index );
			//todo
			//	dst.addToParent( parent, index );
			}
			return dst;
		} catch( UnresolvablePropertyReferencesException upre ) {
			upre.printStackTrace();
			throw new ExceptionWrapper( upre, "UnresolvablePropertyReferencesException" );
		}
	}
	
	public Element createCopyNamed( String name, Class[] classesToShare ) {
		CopyFactory copyFactory = createCopyFactory( classesToShare );
		try {
			Element dst = copyFactory.manufactureCopy( getRoot() );
			dst.name.set( name );
			return dst;
		} catch( UnresolvablePropertyReferencesException upre ) {
			upre.printStackTrace();
			throw new ExceptionWrapper( upre, "UnresolvablePropertyReferencesException" );
		}
	}
	public Element createCopyNamed( String name ) {
		return createCopyNamed( name, null );
	}

	
	protected void internalCopyOver( Element dst, boolean isTopLevel, java.util.Dictionary childCopyFactoryToParentMap ) {
		Element[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			Element child = children[ i ];
			String childName = child.name.getStringValue();
			Element dstChild = null;
			if( childName == null || childName.equals( "__ita__" ) ) {
				Element[] itas = dst.getChildren( edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray.class );
				if( itas.length>0 ) {
					dstChild = itas[ 0 ];
				}
			} else {
				dstChild = dst.getChildNamedIgnoreCase( childName );
			}
			if( dstChild != null ) {
				child.internalCopyOver( dstChild, false, childCopyFactoryToParentMap );
			} else {
				CopyFactory childCopyFactory = child.createCopyFactory( null, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, this );
				childCopyFactoryToParentMap.put( childCopyFactory, dst );
			}
		}
		Property[] properties = getProperties();
		for( int i=0; i<properties.length; i++ ) {
			Property property = properties[ i ];
			if( isTopLevel ) {
				String propertyName = property.getName();
				if( propertyName.equals( "name" ) ) {
					continue;
				}
				if( propertyName.equals( "vehicle" ) ) {
					continue;
				}
				if( propertyName.equals( "localTransformation" ) ) {
					continue;
				}
			}
			if( property instanceof ObjectArrayProperty && !( property instanceof edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty ) ) {
				//pass
			} else {
				Object value = property.get();
				if( value instanceof Element ) {
					//pass
				} else {
					Property dstProperty = dst.getPropertyNamed( property.getName() );
					if( dstProperty != null ) {
						dstProperty.set( value );
					} else {
						//this should never happen
					}
				}
			}
		}
	}
	protected void HACK_copyOverTextureMapReferences( Element dst, java.util.Dictionary srcTextureMapToDstTextureMapMap ) {
		Element[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			Element child = children[ i ];
			Element dstChild = dst.getChildNamedIgnoreCase( child.name.getStringValue() );
			if( dstChild != null ) {
				child.HACK_copyOverTextureMapReferences( dstChild, srcTextureMapToDstTextureMapMap );
			}
		}
	}
	public void copyOver( Element dst ) {
		java.util.Dictionary childCopyFactoryToParentMap = new java.util.Hashtable();
		internalCopyOver( dst, true, childCopyFactoryToParentMap );
		java.util.Enumeration enum0 = childCopyFactoryToParentMap.keys();
		while( enum0.hasMoreElements() ) {
			CopyFactory childCopyFactory = (CopyFactory)enum0.nextElement();
			Element parent = (Element)childCopyFactoryToParentMap.get( childCopyFactory );
			try {
				Element child = childCopyFactory.manufactureCopy( getRoot(), parent, null, parent );
				//todo?
				//child.addToParent( parent );
				child.setParent( parent );
			} catch( UnresolvablePropertyReferencesException upre ) {
				throw new ExceptionWrapper( upre, "UnresolvablePropertyReferencesException" );
			}
		}
		if( this instanceof Sandbox && dst instanceof Sandbox ) {
			TextureMap[] srcTMs = (TextureMap[])((Sandbox)this).textureMaps.getArrayValue();
			TextureMap[] dstTMs = (TextureMap[])((Sandbox)dst).textureMaps.getArrayValue();
			java.util.Dictionary srcTextureMapToDstTextureMapMap = new java.util.Hashtable();
			for( int i=0; i<srcTMs.length; i++ ) {
				TextureMap srcTM = srcTMs[ i ];
				for( int j=0; j<srcTMs.length; j++ ) {
					TextureMap dstTM = dstTMs[ i ];
					if( srcTM.name.getStringValue().equals( dstTM.name.getStringValue() ) ) {
						srcTextureMapToDstTextureMapMap.put( srcTM, dstTM );
						break;
					}
				}
			}
			HACK_copyOverTextureMapReferences( dst, srcTextureMapToDstTextureMapMap );
		}
	}
	

    protected void loadCompleted() {
        for( int i=0; i<getChildCount(); i++ ) {
            getChildAt( i ).loadCompleted();
        }
    }
    protected static Element load( javax.xml.parsers.DocumentBuilder builder, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		String currentDirectory = loader.getCurrentDirectory();
        try {
            java.io.InputStream is = loader.readFile( XML_FILENAME );
            org.w3c.dom.Document document = builder.parse( is );
            org.w3c.dom.Element elementNode = document.getDocumentElement();
            elementNode.normalize();

            String classname = elementNode.getAttribute( "class" );
            double version = Double.parseDouble( elementNode.getAttribute( "version" ) );
            String nameValue = elementNode.getAttribute( "name" );

            try {
                Class cls = Class.forName( classname );
                Element element = (Element)(cls.newInstance());
                try {
                    element.m_xmlFileKeepKey = loader.getKeepKey( XML_FILENAME );
                } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
                    element.m_xmlFileKeepKey = null;
                }

                if( nameValue.length() > 0 ) {
                    element.name.set( nameValue );
                }
                //s_loadDetail = "attempting directory " + currentDirectory;
                org.w3c.dom.NodeList propertyNodeList = elementNode.getElementsByTagName( "property" );
                for( int i = 0; i < propertyNodeList.getLength(); i++ ) {
                    org.w3c.dom.Element propertyNode = (org.w3c.dom.Element)propertyNodeList.item( i );
                    String propertyName = propertyNode.getAttribute( "name" ).trim();
                    Property property = element.getPropertyNamed( propertyName );
                    if( property!=null ) {
                        property.decode( propertyNode, loader, referencesToBeResolved, version );
                    } else {
                        warnln( classname + " has no property: " + propertyName );
                    }
                }


                s_loadProgress++;
                if( progressObserver!=null ) {
					progressObserver.progressUpdate( s_loadProgress, element.name.getStringValue() );//todo: this should be key...
                }

                org.w3c.dom.NodeList childNodeList = elementNode.getElementsByTagName( "child" );
                for( int i = 0; i < childNodeList.getLength(); i++ ) {
                    org.w3c.dom.Element childNode = (org.w3c.dom.Element)childNodeList.item( i );
                    String filename = childNode.getAttribute( "filename" ).trim();
                    loader.setCurrentDirectory( filename );
                    Element child = load( builder, loader, referencesToBeResolved, progressObserver );
                    String childName = child.name.getStringValue();
                    if( childName != null ) {
                        if( element.getChildNamed( childName ) != null ) {
                            child = null;
                            System.err.println( element + " already has child named " + childName + ".  skipping." );
                        }
                    }
                    if( child != null ) {
                        element.addChild( child );
                    }
                    loader.setCurrentDirectory( currentDirectory );
                }
                return element;
            } catch( ClassNotFoundException cnfe ) {
                throw new ExceptionWrapper( cnfe, "ClassNotFoundException: " + classname );
            } catch( InstantiationException ie ) {
                throw new ExceptionWrapper( ie, "InstantiationException: " + classname );
            } catch( IllegalAccessException iae ) {
                throw new ExceptionWrapper( iae, "IllegalAccessException: " + classname );
            }
        } catch( org.xml.sax.SAXException saxe ) {
			throw new ExceptionWrapper( saxe, "org.xml.sax.SAXException" );
        }
	}
	public static Element load( edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Element externalRoot, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException, UnresolvablePropertyReferencesException {
        java.util.Vector referencesToBeResolved = new java.util.Vector();
        java.util.Vector referencesLeftUnresolved = new java.util.Vector();
        Element element;
		try {
            s_isLoading = true;
			int elementCount = edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL;
			try {
				java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader ( new java.io.BufferedInputStream( loader.readFile( "elementCountHint.txt" ) ) ) );
				elementCount = Integer.parseInt( br.readLine() );
				loader.closeCurrentFile();
			} catch( java.io.FileNotFoundException fnfe ) {
				//pass
			}

			s_loadProgress = 0;
			if( progressObserver!=null ) {
				progressObserver.progressBegin( elementCount );
			}

            try {
                javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
                element = load( builder, loader, referencesToBeResolved, progressObserver );

                ReferenceResolver referenceResolver = new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver( element, externalRoot );
                java.util.Enumeration enum0 = referencesToBeResolved.elements();
                while( enum0.hasMoreElements() ) {
                    PropertyReference propertyReference = (PropertyReference)enum0.nextElement();
                    try {
                        propertyReference.resolve( referenceResolver );
                    } catch( UnresolvableReferenceException ure ) {
                        referencesLeftUnresolved.add( propertyReference );
                    } catch( Throwable t ) {
                        System.err.println( propertyReference );
						t.printStackTrace();
                        //referencesLeftUnresolved.add( propertyReference );
                    }
                }
            } catch( javax.xml.parsers.ParserConfigurationException pce ) {
                throw new ExceptionWrapper( pce, "loader: " + loader + "; externalRoot: " + externalRoot );
            }
        } finally {
			if( progressObserver!=null ) {
				progressObserver.progressEnd();
			}
            s_isLoading = false;
		}
        if( referencesLeftUnresolved.size() == 0 ) {
            element.loadCompleted();
            return element;
        } else {
            PropertyReference[] propertyReferences = new PropertyReference[ referencesLeftUnresolved.size() ];
            referencesLeftUnresolved.copyInto( propertyReferences );
            throw new UnresolvablePropertyReferencesException( propertyReferences, element, "loader: " + loader + "; externalRoot: " + externalRoot );
        }
	}
	public static Element load( java.io.File file, Element externalRoot, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException, UnresolvablePropertyReferencesException {
		edu.cmu.cs.stage3.io.DirectoryTreeLoader loader = null;
		if( file.isDirectory() ) {
			loader = new edu.cmu.cs.stage3.io.FileSystemTreeLoader();
		} else {
			String pathname = file.getAbsolutePath();
			if( pathname.endsWith( ".a2w" ) || pathname.endsWith( ".a2c" ) || pathname.endsWith( ".zip" ) ) {
				loader = new edu.cmu.cs.stage3.io.ZipFileTreeLoader();
			} else {
				//too restrictive?
				throw new IllegalArgumentException( file + " must be a directory or end in \".a2w\", \".a2c\", or \".zip\"." );
			}
		}
		loader.open( file );
        Element element = null;
        try {
            element = load( loader, externalRoot, progressObserver );
        } finally {
            loader.close();
        }
        return element;
	}
	public static Element load( java.net.URL url, Element externalRoot, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException, UnresolvablePropertyReferencesException {
		edu.cmu.cs.stage3.io.ZipTreeLoader loader = new edu.cmu.cs.stage3.io.ZipTreeLoader();
		loader.open( url );
		Element element = null;
		try {
			element = load( loader, externalRoot, progressObserver );
		} finally {
			loader.close();
		}
		return element;
	}
	public static Element load( java.io.InputStream is, Element externalRoot, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException, UnresolvablePropertyReferencesException {
		edu.cmu.cs.stage3.io.ZipTreeLoader loader = new edu.cmu.cs.stage3.io.ZipTreeLoader();
		loader.open( is );
		Element element = null;
		try {
			element = load( loader, externalRoot, progressObserver );
		} finally {
			loader.close();
		}
		return element;
	}
	public static Element load( java.io.InputStream is, Element externalRoot ) throws java.io.IOException, UnresolvablePropertyReferencesException {
		try {
			return load( is, externalRoot, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}
	public static Element load( java.io.File file, Element externalRoot ) throws java.io.IOException, UnresolvablePropertyReferencesException {
		try {
			return load( file, externalRoot, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}
	public static Element load( java.net.URL url, Element externalRoot ) throws java.io.IOException, UnresolvablePropertyReferencesException {
		try {
			return load( url, externalRoot, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}

    private void writeXMLDocument( org.w3c.dom.Document xmlDocument, DirectoryTreeStorer storer, String filename ) throws java.io.IOException {
        java.io.OutputStream os = storer.createFile( filename, true );
		edu.cmu.cs.stage3.xml.Encoder.write( xmlDocument, os );
        storer.closeCurrentFile();
    }

	protected int internalStore( javax.xml.parsers.DocumentBuilder builder, DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, HowMuch howMuch, ReferenceGenerator referenceGenerator, int count ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		count++;
		if( progressObserver!=null ) {
			progressObserver.progressUpdate( count, getKey() );
		}
		Object xmlFileKeepKey;
        try {
            xmlFileKeepKey = storer.getKeepKey( XML_FILENAME );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            xmlFileKeepKey = null;
        }
		if( m_xmlFileKeepKey == null || !m_xmlFileKeepKey.equals( xmlFileKeepKey ) ) {
            org.w3c.dom.Document document = builder.newDocument();

            org.w3c.dom.Element elementNode = document.createElement( "element" );
            elementNode.setAttribute( "class", getClass().getName() );
            elementNode.setAttribute( "version", Double.toString( VERSION ) );

            document.appendChild( elementNode );

            for( int i=0; i<getChildCount(); i++ ) {
                org.w3c.dom.Element childNode = document.createElement( "child" );
                //childNode.setAttribute( "index", Integer.toString( i ) );
                childNode.setAttribute( "filename", getChildAt( i ).getRepr( i ) );

                elementNode.appendChild( childNode );
            }

            Property[] properties = getProperties();
            for( int i=0; i<properties.length; i++ ) {
                String propertyName = properties[i].getName();
                if( propertyName.equals( "name" ) ) {
                    String nameValue = name.getStringValue();
                    if( nameValue != null ) {
                        elementNode.setAttribute( "name", nameValue );
                    }
                } else {
                    org.w3c.dom.Element propertyNode = document.createElement( "property" );
                    propertyNode.setAttribute( "name", properties[i].getName() );
                    properties[i].encode( document, propertyNode, storer, referenceGenerator );
                    elementNode.appendChild( propertyNode );
                }
            }

            document.getDocumentElement().normalize();

//            java.io.OutputStream os = storer.createFile( XML_FILENAME, true );
//            java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream( os );
//            try {
//                Class cls = document.getClass();
//                Class[] parameterTypes = { java.io.OutputStream.class };
//                Object[] args = { bos };
//                java.lang.reflect.Method method = cls.getMethod( "write", parameterTypes );
//                method.invoke( document, args );
//            } catch( NoSuchMethodException nsme ) {
//                throw new RuntimeException( nsme.toString() );
//            } catch( IllegalAccessException iae ) {
//                throw new RuntimeException( iae.toString() );
//            } catch( java.lang.reflect.InvocationTargetException ite ) {
//                throw new RuntimeException( ite.toString() );
//            }
//            storer.closeCurrentFile();

            writeXMLDocument( document, storer, XML_FILENAME );
			try {
				m_xmlFileKeepKey = storer.getKeepKey( XML_FILENAME );
			} catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
				m_xmlFileKeepKey = null;
			}
        } else {
            try {
                storer.keepFile( XML_FILENAME );
                Property[] properties = getProperties();
                for( int i=0; i<properties.length; i++ ) {
                    if( properties[ i ].get() != null ) {
                        properties[ i ].keepAnyAssociatedFiles( storer );
                    }
                }
            } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
                kfnse.printStackTrace();
            } catch( edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne ) {
                kfdne.printStackTrace();
            }
		}

		String thisDirectory = storer.getCurrentDirectory();
		for( int i=0; i<getChildCount(); i++ ) {
			Element child = (Element)getChildAt( i );
			String name = child.getRepr( i );
			storer.createDirectory( name );
			storer.setCurrentDirectory( name );
			count = child.internalStore( builder, storer, progressObserver, howMuch, referenceGenerator, count );
			storer.setCurrentDirectory( thisDirectory );
		}
		return count;
	}
	
//	public void store( javax.xml.parsers.DocumentBuilder builder, DirectoryTreeStorer storer, edu.cmu.cs.stage3.util.ProgressObserver progressObserver, HowMuch howMuch, ReferenceGenerator referenceGenerator, java.util.Dictionary map ) throws java.io.IOException {
//		int elementCount = getElementCount( Element.class, howMuch );
//		if( progressObserver!=null ) {
//			progressObserver.progressUpdateTotal( elementCount );
//			progressObserver.progressUpdateCurrent( 0 );
//		}
//		int storeCount = internalStore( builder, storer, progressObserver, howMuch, referenceGenerator, 0 );
//		if( elementCount != storeCount ) {
//			warnln( "WARNING: elementCount " + elementCount + " != storeCount " + storeCount );
//		}
//	}

	public void store( final DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, java.util.Dictionary filnameToByteArrayMap, HowMuch howMuch, ReferenceGenerator referenceGenerator ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		int elementCount = getElementCount( Element.class, howMuch );
		if( progressObserver!=null ) {
			progressObserver.progressBegin( elementCount );
		}

		java.io.BufferedWriter writer = new java.io.BufferedWriter( new java.io.OutputStreamWriter( new java.io.BufferedOutputStream( storer.createFile( "elementCountHint.txt", true ) ) ) );
		writer.write( Integer.toString( elementCount ) );
		writer.newLine();
		writer.flush();
		storer.closeCurrentFile();

		if( filnameToByteArrayMap != null ) {
			if( filnameToByteArrayMap.size()>0 ) {
				java.util.Enumeration enum0 = filnameToByteArrayMap.keys();
				while( enum0.hasMoreElements() ) {
					String filename = (String)enum0.nextElement();
					byte[] byteArray = (byte[])filnameToByteArrayMap.get( filename );
					java.io.OutputStream os = storer.createFile( filename, false );
					os.write( byteArray );
					os.flush();
					storer.closeCurrentFile();
				}
			}
		}
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            int storeCount = internalStore( builder, storer, progressObserver, howMuch, referenceGenerator, 0 );
            if( elementCount != storeCount ) {
                warnln( "WARNING: elementCount " + elementCount + " != storeCount " + storeCount );
            }
        } catch( javax.xml.parsers.ParserConfigurationException pce ) {
            pce.printStackTrace();
        } finally {
			if( progressObserver!=null ) {
				progressObserver.progressEnd();
			}
        }
	}
	public void store( DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, java.util.Dictionary filnameToByteArrayMap, HowMuch howMuch ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
	    store( storer, progressObserver, filnameToByteArrayMap, howMuch, new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceGenerator( this ) );
	}
	public void store( DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, java.util.Dictionary filnameToByteArrayMap ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		store( storer, progressObserver, filnameToByteArrayMap, HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
	}
	public void store( DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		store( storer, progressObserver, null );
	}
	public void store( DirectoryTreeStorer storer ) throws java.io.IOException {
		try {
			store( storer, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}

	public void store( java.io.File file, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, java.util.Dictionary filnameToByteArrayMap ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		edu.cmu.cs.stage3.io.DirectoryTreeStorer storer;
		if( file.isDirectory() ) {
			storer = new edu.cmu.cs.stage3.io.FileSystemTreeStorer();
		} else {
			String pathname = file.getAbsolutePath();
			if( pathname.endsWith( ".a2w" ) || pathname.endsWith( ".a2c" ) || pathname.endsWith( ".zip" ) ) {
                //todo: remove
                if( pathname.endsWith( ".a2c" ) ) {
    				storer = new edu.cmu.cs.stage3.io.ZipTreeStorer();
                } else {
    				storer = new edu.cmu.cs.stage3.io.ZipFileTreeStorer();
                }
			} else {
				storer = new edu.cmu.cs.stage3.io.FileSystemTreeStorer();
			}
		}
		storer.open( file );
		try {
			store( storer, progressObserver, filnameToByteArrayMap );
		} finally {
			storer.close();
		}
	}
	public void store( java.io.File file, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		store( file, progressObserver, null );
	}
	public void store( java.io.File file ) throws java.io.IOException {
		try {
			store( file, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}
	public void store( String filename, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		store( new java.io.File( filename ), progressObserver );
	}
	public void store( String filename ) throws java.io.IOException {
		try {
			store( filename, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error();
		}
	}

	private String getUnnamedRepr( int childIndex ) {
		return "__Unnamed"+childIndex+"__";
	}
	public String getRepr( int childIndex ) {
		String nameValue = name.getStringValue();
		if( nameValue==null || nameValue.length()==0 ) {
			nameValue = getUnnamedRepr( childIndex );
		}
		return nameValue;
	}
	public String getRepr() {
		String nameValue = (String)name.get();
		if( nameValue==null ) {
			int childIndex;
			if( m_parent!=null ) {
				childIndex = m_parent.getIndexOfChild( this );
			} else {
				childIndex = 0;
			}
			nameValue = getUnnamedRepr( childIndex );
		}
		return nameValue;
	}

	protected void started( World world, double time ) {
		Element[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			children[ i ].started( world, time );
		}
	}
	protected void stopped( World world, double time ) {
		Element[] children = getChildren();
		for( int i=0; i<children.length; i++ ) {
			children[ i ].stopped( world, time );
		}
	}
	public static void warn( Object o ) {
		edu.cmu.cs.stage3.alice.scenegraph.Element.warn( o );
	}
	public static void warnln( Object o ) {
		edu.cmu.cs.stage3.alice.scenegraph.Element.warnln( o );
	}
	public static void warnln() {
		edu.cmu.cs.stage3.alice.scenegraph.Element.warnln();
	}
	/** @deprecated */
	public static void debug( Object o ) {
		System.err.print( o );
	}
	/** @deprecated */
	public static void debugln( Object o ) {
		System.err.println( o );
	}

    private static java.util.Hashtable s_classToElementCache = new java.util.Hashtable();
	public static Class getValueClassForPropertyNamed( Class elementClass, String propertyName ) {
        Element element = (Element)s_classToElementCache.get( elementClass );
        if( element == null ) {
            try {
                element = (Element)elementClass.newInstance();
                s_classToElementCache.put( elementClass, element );
            } catch( InstantiationException ie ) {
                return null;
            } catch( IllegalAccessException iae ) {
                return null;
            }
        }
        Property property = element.getPropertyNamed( propertyName );
        return property.getValueClass();
	}

	public boolean isAccessibleFrom( Element e ) {
		return true;
	}

	protected void internalAddExpressionIfAssignableTo( Expression expression, Class cls, java.util.Vector v ) {
		if( expression != null ) {
			if( cls.isAssignableFrom( expression.getValueClass() ) ) {
				v.addElement( expression );
			}
		}
	}
	protected void internalFindAccessibleExpressions( Class cls, java.util.Vector v ) {
		if( m_parent != null ) {
			m_parent.internalFindAccessibleExpressions( cls, v );
		}
	}
	public Expression[] findAccessibleExpressions( Class cls ) {
		java.util.Vector v = new java.util.Vector();
		internalFindAccessibleExpressions( cls, v );
		Expression[] array = new Expression[ v.size() ];
		v.copyInto( array );
		return array;
	}

	public String toString() {
		return getClass().getName()+"["+getKey()+"]";
	}
}
