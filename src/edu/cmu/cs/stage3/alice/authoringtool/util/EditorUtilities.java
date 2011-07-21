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

import java.util.Vector; //TODO: move up to Collections interface

/**
 * Utilities for Editors.
 * This Class keeps the master list of which Editors are available,
 * as well as providing a bunch of static methods for manipulating and
 * getting information from Editors.
 *
 * @author Jason Pratt
 * @see edu.cmu.cs.stage3.alice.authoringtool.Editor
 */
public final class EditorUtilities {
	private static Class[] allEditors = null;

	static {
		allEditors = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getEditorClasses();
		if( allEditors == null ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "no editors found!", null );
			allEditors = new Class[0];
		}
		//TODO: auto-find more editors?
	}

	/**
	 * Returns the current list of Editors that the system knows about.
	 * You must call findAllEditors if you want to find new Editors
	 * that have been added since initialization.
	 */
	public static Class[] getAllEditors() {
		return allEditors;
	}

	/**
	 * Returns all of the Editors that are able to view the specified
	 * Object type.
	 *
	 * TODO: CACHING...
	 */
	public static Class[] getEditorsForClass( Class objectClass ) {
		Vector editors = new Vector();
		if( !Object.class.isAssignableFrom( objectClass ) ) {
			return null;
		}
		if( allEditors == null ) {
			return null;
		}
		for( int i=0; i<allEditors.length; i++ ) {
			Class acceptedClass = getObjectParameter( allEditors[i] );
			if( acceptedClass.isAssignableFrom( objectClass ) ) {
				editors.addElement( allEditors[i] );
			}
		}

		sort( editors, objectClass );

		Class[] cvs = new Class[editors.size()];
		for( int i=0; i<cvs.length; i++ ) {
			cvs[i] = (Class)editors.elementAt( i );
		}
		return cvs;
	}

	/**
	 * returns true if potentialEditor is in the current list of all
	 * known Editors.
	 */
	public static boolean isInAllEditors( Class potentialEditor ) {
		if( allEditors == null ) {
			return false;
		}
		for( int i=0; i<allEditors.length; i++ ) {
			if( potentialEditor == allEditors[i] ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a new Editor instance of the specified Class
	 */
	public static edu.cmu.cs.stage3.alice.authoringtool.Editor getEditorFromClass( Class editorClass ) {
		try {
			return (edu.cmu.cs.stage3.alice.authoringtool.Editor)editorClass.newInstance();
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating new editor of type " + editorClass, t );
		}
		return null;
	}

	public static java.lang.reflect.Method getSetMethodFromClass( Class editorClass ) {
		java.lang.reflect.Method [] methods = editorClass.getMethods();
		for( int i = 0; i < methods.length; i++ ) {
			java.lang.reflect.Method potentialMethod = methods[i];
			if( potentialMethod.getName().equals( "setObject" ) ) {
				Class[] parameterTypes = potentialMethod.getParameterTypes();
				if( parameterTypes.length == 1 ) {
					if( Object.class.isAssignableFrom( parameterTypes[0] ) ) {
						return potentialMethod;
					}
				}
			}
		}

		return null;
	}

	public static Class getObjectParameter( Class editorClass ) {
		java.lang.reflect.Method setObject = getSetMethodFromClass( editorClass );
		if( setObject != null ) {
			return setObject.getParameterTypes()[0];
		}

		return null;
	}

	/**
	 * Returns the Editor type can most suitably view the given objectClass.
	 * Suitability is determined by getting the Class hierarchical distance between
	 * the given objectClass and the actual type that each Editor's setObject method
	 * accepts.
	 */
	public static Class getBestEditor( Class objectClass ) {
		//DEBUG System.out.println( "objectClass: " + objectClass );
		Class bestEditor = null;
		int bestDepth = Integer.MAX_VALUE;
		for( int i = 0; i < allEditors.length; i++ ) {
			Class editorClass = allEditors[i];
			//DEBUG System.out.println( "editorClass: " + editorClass );
			java.lang.reflect.Method setObject = getSetMethodFromClass( editorClass );
			if( setObject != null ) {
				Class[] parameterTypes = setObject.getParameterTypes();
				if( parameterTypes.length == 1 ) {
					int depth = getObjectClassDepth( parameterTypes[0], objectClass );
					//DEBUG System.out.println( "getObjectClassDepth( " + parameterTypes[0] + ", " + objectClass + " ): " + depth );
					if( (depth < bestDepth) && (depth >= 0) ) {
						bestDepth = depth;
						bestEditor = editorClass;
						//DEBUG System.out.println( "setting bestEditor: " + bestEditor );
					}
				}

			}
		}
		//DEBUG System.out.println( "bestEditor: " + bestEditor );
		return bestEditor;
	}

	public static void editObject( edu.cmu.cs.stage3.alice.authoringtool.Editor editor, Object object ) {
		java.lang.reflect.Method setObject = getSetMethodFromClass( editor.getClass() );
		try {
			setObject.invoke( editor, new Object[] { object } );
		} catch( Exception e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error editing object: " + object, e );
		}
	}

	/**
	 * This method <bold>defines</bold> what it means to be a valid Editor.
	 *
	 * To be valid, a Class must:
	 * -implement Editor
	 * -have an constructor that takes no arguments
	 * -have a method called setObject that takes a single argument
	 */
	private static boolean isValidEditor( Class editorClass ) {
		if( ! edu.cmu.cs.stage3.alice.authoringtool.Editor.class.isAssignableFrom( editorClass ) ) {
			return false;
		}

		boolean constructorFound = false;
		java.lang.reflect.Constructor [] editorConstructors = editorClass.getConstructors();
		for( int i=0; i<editorConstructors.length; i++ ) {
			Class[] parameterTypes = editorConstructors[i].getParameterTypes();
			if( parameterTypes.length == 0 ) {
				constructorFound = true;
				break;
			}
		}

		if( (getSetMethodFromClass( editorClass ) != null) && constructorFound ) {
			return true;
		}

		return false;
	}

	/**
	 * Determines how close a subclass is to a superclass
	 *
	 * @returns  the depth of the class hierarchy between the given superclass and subclass
	 */
	private static int getObjectClassDepth( Class superclass, Class subclass ) {
		if( ! superclass.isAssignableFrom( subclass ) ) {
			return -1;
		}

		Class temp = subclass;
		int i = 0;
		while( (temp != superclass) && (superclass.isAssignableFrom( temp ) ) ) {
			i++;
			temp = temp.getSuperclass();
		}

		return i;
	}

	/**
	 * Swaps elements a and b in Vector v
	 */
	private static void swap( Vector v, int a, int b ) {
		Object t = v.elementAt( a );
		v.setElementAt( v.elementAt( b ), a );
		v.setElementAt( t, b );
	}

	/**
	 * Compares two EditorClasses based on how close their setObject's
	 * Object parameter is to the given objectClass.
	 *
	 * returns -1 if a is closer
	 * returns  1 if b is closer
	 * returns  0 if they are equally close
	 */
	private static int compare( Class a, Class b, Class objectClass ) {
		int aDist = getObjectClassDepth( getObjectParameter( a ), objectClass );
		int bDist = getObjectClassDepth( getObjectParameter( b ), objectClass );
		if( aDist < bDist )
			return -1;
		else if( bDist < aDist )
			return 1;
		else
			return 0;
	}

	/**
	 * Sorts a Vector of Editor Classes based on how fit they are to view
	 * the given objectClass
	 *
	 * This is going to be used on small vectors,
	 * so I'm just using Insertion sort.
	 */
	private static void sort( Vector v, Class objectClass ) {
		for( int i=0; i<v.size(); i++ ) {
			for( int j=i; j>0 && compare( (Class)v.elementAt(j-1), (Class)v.elementAt(j), objectClass )>0; j-- ) {
				swap( v, j, j-1 );
			}
		}
	}
}
