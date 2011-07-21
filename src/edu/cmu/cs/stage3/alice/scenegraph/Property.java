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

public class Property {
	private java.lang.reflect.Method m_getter;
	private java.lang.reflect.Method m_setter;
	private java.lang.reflect.Method m_setterHowMuch;
	private Class m_elementClass;
	private Class m_valueClass;
	private String m_capsAndUnderscoresName;
	private String m_mixedCaseName;
	private boolean m_isPersistent;

	public Property( Class elementClass, String capsAndUnderscoresName, boolean isPersistent ) {
		m_capsAndUnderscoresName = capsAndUnderscoresName;
		m_mixedCaseName = new String( convertAllCapsAndUnderscoresToMixedCase( capsAndUnderscoresName ) );
		/*
		java.lang.reflect.Method[] methods = elementClass.getDeclaredMethods();
		for( int i=0; i<methods.length; i++ ) {
			String methodName = methods[i].getName();
			if( methodName.equals( "get"+m_mixedCaseName ) ) {
				m_getter = methods[i];
			} else if( methodName.equals( "set"+m_mixedCaseName ) ) {
				switch( methods[i].getParameterTypes().length ) {
				case 1:
					m_setter = methods[i];
					break;
				case 2:
					m_setterHowMuch = methods[i];
					break;
				}
			}
		}
		*/
		try {
			Class[] parameterTypes = {};
			m_getter = elementClass.getDeclaredMethod( "get"+m_mixedCaseName, parameterTypes );
		} catch( NoSuchMethodException nsme ) {
			Element.warnln( "get"+m_mixedCaseName );
			nsme.printStackTrace();
		}
		m_valueClass = m_getter.getReturnType();
		try {
			Class[] parameterTypes = { m_valueClass };
			m_setter = elementClass.getDeclaredMethod( "set"+m_mixedCaseName, parameterTypes );
		} catch( NoSuchMethodException nsme ) {
			nsme.printStackTrace();
		}

		try {
			Class[] parameterTypes = { m_valueClass, edu.cmu.cs.stage3.util.HowMuch.class };
			m_setterHowMuch = elementClass.getDeclaredMethod( "set"+m_mixedCaseName, parameterTypes );
		} catch( NoSuchMethodException nsme ) {
			//no problem
		}

		if( m_getter==null || m_setter==null ) {
			throw new NullPointerException();
		}
		m_elementClass = elementClass;
		m_isPersistent = isPersistent;
	}
	public Property( Class elementClass, String capsAndUnderscoresName ) {
		this( elementClass, capsAndUnderscoresName, true );
	}
	public boolean acceptsHowMuch() {
		return m_setterHowMuch!=null;
	}
	public Object get( Object o ) {
		Object[] getParameters = {};
		try {
			return m_getter.invoke( o, getParameters );
		} catch( java.lang.reflect.InvocationTargetException ite ) {
			ite.printStackTrace();
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		} catch( Exception e ) {
			Element.warnln( m_getter );
			Element.warnln( o );
			if( o!=null ) {
				Element.warnln( o.getClass() );
			}
			e.printStackTrace();
		}
		return null;
	}
	public double getDouble( Object o ) {
		return ((Number)get( o )).doubleValue();
	}
	public int getInt( Object o ) {
		return ((Number)get( o )).intValue();
	}
	public boolean getBoolean( Object o ) {
		return ((Boolean)get( o )).booleanValue();
	}
	public void set( Object o, Object value ) {
		try {
			Object[] setParameters = { value };
			m_setter.invoke( o, setParameters );
		} catch( java.lang.reflect.InvocationTargetException ite ) {
			ite.printStackTrace();
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		} catch( NullPointerException npe ) {
			Element.warnln( npe );
		} catch( Exception e ) {
			Element.warnln( e );
			Element.warnln( m_setter );
			if( o!=null ) {
				Element.warnln( o );
				Element.warnln( o.getClass() );
			} else {
				Element.warnln( "null" );
			}
			if( value!=null ) {
				Element.warnln( value );
				Element.warnln( value.getClass() );
			} else {
				Element.warnln( "null" );
			}
			e.printStackTrace();
		}
	}
	public void set( Object o, Object value, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
		try {
			if( m_setterHowMuch!=null ) {
				Object[] setParameters = { value, howMuch };
				m_setterHowMuch.invoke( o, setParameters );
			} else {
				Element.warnln( "ignoring howMuch" );
				set( o, value );
			}
		} catch( java.lang.reflect.InvocationTargetException ite ) {
			ite.printStackTrace();
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		} catch( NullPointerException npe ) {
			Element.warnln( npe );
		}

	}
	public Class getElementClass() {
		return m_elementClass;
	}
	public Class getValueClass() {
		return m_valueClass;
	}
	public boolean getIsPersistent() {
		return m_isPersistent;
	}
	public String getMixedCaseName() {
		return m_mixedCaseName;
	}
	public String getCapsAndUnderscoresName() {
		return m_capsAndUnderscoresName;
	}

	
	public String toString() {
		return m_elementClass.getName() + "." + m_capsAndUnderscoresName + "_PROPERTY";
	}
	public static Property valueOf( String propertyName ) {
		int i = propertyName.lastIndexOf( '.' );
		String classPart = propertyName.substring( 0 , i );
		String fieldPart = propertyName.substring( i+1 );
		try {
			Class cls = Class.forName( classPart );
			java.lang.reflect.Field field = cls.getField( fieldPart );
			int modifiers = field.getModifiers();
			if( java.lang.reflect.Modifier.isPublic( modifiers ) && java.lang.reflect.Modifier.isFinal( modifiers ) && java.lang.reflect.Modifier.isStatic( modifiers ) ) {
				Object o = field.get( null );
				if( o instanceof Property ) {
					return (Property)o;
				}
			}
		} catch( ClassNotFoundException cnfe ) {
			cnfe.printStackTrace();
		} catch( NoSuchFieldException nsfe ) {
			//nsfe.printStackTrace();
			Element.warnln( "backward compatibility? skipping: " + propertyName + " " + nsfe );
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		}
		return null;
	}
	public static java.util.Vector getProperties( Class cls, boolean persistentOnly, boolean declaredOnly ) {
		java.util.Vector v = new java.util.Vector();
		java.lang.reflect.Field[] fields;
		if( declaredOnly ) {
			fields = cls.getDeclaredFields();
		} else {
			fields = cls.getFields();
		}
		for( int i=0; i<fields.length; i++ ) {
			int modifiers = fields[i].getModifiers();
			if( java.lang.reflect.Modifier.isPublic( modifiers ) && java.lang.reflect.Modifier.isStatic( modifiers ) && java.lang.reflect.Modifier.isFinal( modifiers ) ) {
				try {
					if( Property.class.isAssignableFrom( fields[i].getType() ) ) {
						Property property = (Property)(fields[i].get( null ));
						if( !persistentOnly || property.getIsPersistent() ) {
							v.addElement( property );
						}
					}
				} catch( IllegalAccessException iae ) {
					iae.printStackTrace();
				}
			}
		}
		return v;
	}
	public static java.util.Vector getProperties( Class cls ) {
		return getProperties( cls, false, false );
	}

    public static Property getPropertyMixedCaseNamed( Class cls, String mixedCaseName ) {
        java.util.Enumeration enum0 = getProperties( cls ).elements();
        while( enum0.hasMoreElements() ) {
            Property property = (Property)enum0.nextElement();
            if( property.getMixedCaseName().equals( mixedCaseName ) ) {
                return property;
            }
        }
        return null;
    }
    public static Property getPropertyCapsAndUnderscoresNamed( Class cls, String capsAndUnderscoresName ) {
        java.util.Enumeration enum0 = getProperties( cls ).elements();
        while( enum0.hasMoreElements() ) {
            Property property = (Property)enum0.nextElement();
            if( property.getCapsAndUnderscoresName().equals( capsAndUnderscoresName ) ) {
                return property;
            }
        }
        return null;
    }

	public static java.util.Vector getPropertyValuePairs( Object o, boolean persistentOnly, boolean declaredOnly ) {
		java.util.Vector v = new java.util.Vector();
		java.util.Enumeration enum0 = getProperties( o.getClass(), persistentOnly, declaredOnly ).elements();
		while( enum0.hasMoreElements() ) {
			Property property = (Property)enum0.nextElement();
			v.addElement( new PropertyValuePair( property, property.get( o ) ) );
		}
		return v;
	}
	public static java.util.Vector getPropertyValuePairs( Object o ) {
		return getPropertyValuePairs( o, false, false );
	}
	public static StringBuffer convertAllCapsAndUnderscoresToMixedCase( String allCapsAndUnderscores ) {
		StringBuffer mixedCase = new StringBuffer();
		mixedCase.append( allCapsAndUnderscores.charAt( 0 ) );
		boolean lowerCase = true;
		for( int lcv=1; lcv<allCapsAndUnderscores.length(); lcv++ ) {
			char c = allCapsAndUnderscores.charAt( lcv );
			if( c=='_' ) {
				lowerCase = false;
			} else {
				if( lowerCase ) {
					mixedCase.append( Character.toLowerCase( c ) );
				} else {
					mixedCase.append( c );
				}
				lowerCase = true;
			}
		}
		return mixedCase;
	}
	public static StringBuffer convertMixedCaseToAllCapsAndUnderscores( String mixedCase ) {
		StringBuffer allCapsAndUnderscores = new StringBuffer();
		allCapsAndUnderscores.append( mixedCase.charAt( 0 ) );
		for( int lcv=1; lcv<mixedCase.length(); lcv++ ) {
			char c = mixedCase.charAt( lcv );
			if( Character.isUpperCase( c ) ) {
				allCapsAndUnderscores.append( "_" + c );
			} else {
				allCapsAndUnderscores.append( Character.toUpperCase( c ) );
			}
		}
		return allCapsAndUnderscores;
	}
}
