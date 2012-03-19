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

package edu.cmu.cs.stage3.alice.core;

import java.lang.reflect.Field;

import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public abstract class Property {
	private static boolean HACK_s_isListeningEnabled = true;
	public static void HACK_enableListening() {
		HACK_s_isListeningEnabled = true;
	}
	public static void HACK_disableListening() {
		HACK_s_isListeningEnabled = false;
	}

	private boolean m_isAcceptingOfHowMuch = false;

	public boolean isAcceptingOfHowMuch() {
		return m_isAcceptingOfHowMuch;
	}
	public void setIsAcceptingOfHowMuch(boolean isAcceptingOfHowMuch) {
		m_isAcceptingOfHowMuch = isAcceptingOfHowMuch;
	}

	private Element m_owner;
	private String m_name;
	private Object m_defaultValue;
	private Class m_valueClass;
	private Object m_value;

	private java.util.Vector m_propertyListeners = new java.util.Vector();
	private edu.cmu.cs.stage3.alice.core.event.PropertyListener[] m_propertyListenerArray = null;

	private boolean m_isDeprecated = false;

	protected Object m_associatedFileKey = null;

	protected Property(Element owner, String name, Object defaultValue, Class valueClass) {
		m_owner = owner;
		m_name = name;
		m_defaultValue = defaultValue;
		m_valueClass = valueClass;
		m_isAcceptingOfHowMuch = false;
		m_value = m_defaultValue;
		m_owner.propertyCreated(this);
	}

	private static java.util.Dictionary s_ownerClassMap = new java.util.Hashtable();
	public static String[] getPropertyNames(Class ownerClass, Class valueClass) {
		java.util.Dictionary valueClassMap = (java.util.Dictionary) s_ownerClassMap.get(ownerClass);
		if (valueClassMap == null) {
			valueClassMap = new java.util.Hashtable();
			s_ownerClassMap.put(ownerClass, valueClassMap);
		}
		String[] propertyNameArray = (String[]) valueClassMap.get(valueClass);
		if (propertyNameArray == null) {
			java.util.Vector propertyNames = new java.util.Vector();
			java.lang.reflect.Field[] fields = ownerClass.getFields();
			for (Field field : fields) {
				if (Property.class.isAssignableFrom(field.getType())) {
					String propertyName = field.getName();
					Class cls = Element.getValueClassForPropertyNamed(ownerClass, propertyName);
					if (cls != null) {
						if (valueClass.isAssignableFrom(cls)) {
							propertyNames.addElement(propertyName);
						}
					} else {
						System.err.println(ownerClass + " " + propertyName);
					}
				}
			}
			propertyNameArray = new String[propertyNames.size()];
			propertyNames.copyInto(propertyNameArray);
			valueClassMap.put(valueClass, propertyNameArray);
		}
		return propertyNameArray;
	}

	public static String[] getPropertyNames(Class ownerClass) {
		return getPropertyNames(ownerClass, Object.class);
	}
	public boolean isAlsoKnownAs(Class cls, String name) {
		if (cls.isAssignableFrom(m_owner.getClass())) {
			try {
				java.lang.reflect.Field field = cls.getField(name);
				Object o = field.get(m_owner);
				return o == this;
			} catch (NoSuchFieldException nsfe) {} catch (IllegalAccessException nsfe) {}
		}
		return false;
	}

	public Element getOwner() {
		return m_owner;
	}
	/** @deprecated */
	@Deprecated
	public Element getElement() {
		return getOwner();
	}
	public Class getValueClass() {
		return m_valueClass;
	}

	protected void setValueClass(Class valueClass) {
		m_valueClass = valueClass;
	}
	public Object getDefaultValue() {
		return m_defaultValue;
	}
	public String getName() {
		return m_name;
	}

	public void addPropertyListener(edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener) {
		if (m_propertyListeners.contains(propertyListener)) {
			// edu.cmu.cs.stage3.alice.core.Element.warnln( "WARNING: " + this +
			// " already has propertyListener " + propertyListener +
			// "(class="+propertyListener.getClass()+").  NOT added again." );
		} else {
			m_propertyListeners.addElement(propertyListener);
			m_propertyListenerArray = null;
		}
	}
	public void removePropertyListener(edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener) {
		m_propertyListeners.removeElement(propertyListener);
		m_propertyListenerArray = null;
	}
	public edu.cmu.cs.stage3.alice.core.event.PropertyListener[] getPropertyListeners() {
		if (m_propertyListenerArray == null) {
			m_propertyListenerArray = new edu.cmu.cs.stage3.alice.core.event.PropertyListener[m_propertyListeners.size()];
			m_propertyListeners.copyInto(m_propertyListenerArray);
		}
		return m_propertyListenerArray;
	}

	public Class getDeclaredClass() {
		if (m_owner != null) {
			Class cls = m_owner.getClass();
			while (cls != null) {
				try {
					java.lang.reflect.Field field = cls.getDeclaredField(m_name);
					if (field.get(m_owner) == this) {
						return cls;
					} else {
						throw new RuntimeException(m_owner + " has field named " + m_name + " that is not " + this);
					}
				} catch (NoSuchFieldException nsfe) {
					cls = cls.getSuperclass();
				} catch (IllegalAccessException iae) {
					throw new ExceptionWrapper(iae, null);
				}
			}
			return null;
		} else {
			return null;
		}
	}

	public Object get() {
		if (m_value instanceof Cloneable) {
			if (m_value.getClass().isArray()) {
				// todo
				return m_value;
			} else {
				try {
					Class[] parameterTypes = {};
					Object[] parameterValues = {};
					java.lang.reflect.Method method = m_value.getClass().getMethod("clone", parameterTypes);
					if (method.isAccessible()) {
						return method.invoke(m_value, parameterValues);
					} else {
						return m_value;
					}
				} catch (NoSuchMethodException nsme) {
					Element.warnln("property get failure to clone: " + this + " " + nsme);
					// nsme.printStackTrace();
					return m_value;
				} catch (IllegalAccessException iae) {
					Element.warnln("property get failure to clone: " + this + " " + iae);
					// iae.printStackTrace();
					return m_value;
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Element.warnln("property get failure to clone: " + this + " " + ite);
					// ite.getTargetException().printStackTrace();
					return m_value;
				}
			}
		} else {
			return m_value;
		}
	}

	private boolean isValueInADifferentWorld(Object value) {
		World world = getElement().getWorld();
		if (world != null) {
			if (value instanceof Element) {
				Element element = (Element) value;
				return world != getElement().getWorld();
			}
		}
		return false;
	}

	public void checkForBadReferences(Object value) {
		if (value instanceof Object[]) {
			Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				if (isValueInADifferentWorld(array[i])) {
					throw new edu.cmu.cs.stage3.alice.core.IllegalArrayPropertyValueException(this, i, value, "value must be in world");
				}
			}
		} else {
			if (isValueInADifferentWorld(value)) {
				throw new edu.cmu.cs.stage3.alice.core.IllegalPropertyValueException(this, value, "value must be in world");
			}
		}
	}

	protected void checkValueType(Object value) {
		if (value != null) {
			Class valueClass = getValueClass();
			if (value instanceof Expression) {
				Expression expression = (Expression) value;
				// todo: there must be a better way
				if (valueClass.isAssignableFrom(Expression.class)) {
					// pass
				} else if (valueClass.isAssignableFrom(Variable.class)) {
					// pass
				} else if (valueClass.isAssignableFrom(Question.class)) {
					// pass
					// todo: fix
				} else if (Question.class.isAssignableFrom(valueClass)) {
					// pass
				} else {
					if (expression.getValueClass() != null) {
						if (valueClass.isAssignableFrom(expression.getValueClass())) {
							// pass
						} else {
							// Element.debugln( this + " " + value );
							throw new IllegalPropertyValueException(this, value, "Cannot set property " + getName() + " on " + getOwner() + ".  " + valueClass + " is not assignable from " + expression.getValueClass());
						}
					}
				}
			} else {
				if (valueClass.isAssignableFrom(value.getClass())) {
					// pass
				} else {
					throw new IllegalPropertyValueException(this, value, "Cannot set property " + getName() + " on " + getOwner() + ".  " + valueClass + " is not assignable from " + value.getClass());
				}
			}
		} else {
			// todo
		}
	}
	/*
	 * protected void valueCheck( Object value ) { if( value != null ) { Class
	 * valueClass = getValueClass(); if( value instanceof Expression ) {
	 * Expression expression = (Expression)value; if(
	 * valueClass.isAssignableFrom( expression.getValueClass() ) ) { //pass }
	 * else { throw new IllegalArgumentException( this + " isAssignableFrom " +
	 * valueClass + " but encountered an expression of valueClass " +
	 * expression.getValueClass() ); } } else { if( valueClass.isAssignableFrom(
	 * value.getClass() ) ) { //pass } else { throw new
	 * IllegalArgumentException( this + " isAssignableFrom " + valueClass +
	 * " but encountered an object of class " + value.getClass() ); } } } else {
	 * //todo } } public Object getValue( Response.RuntimeResponse
	 * runtimeResponse ) { Object value = get(); if( value instanceof Expression
	 * ) { if( runtimeResponse == null ) { Element owner = getElement(); if(
	 * owner != null ) { World world = owner.getWorld(); if( world != null ) {
	 * Sandbox sandbox = world.getCurrentSandbox(); if( sandbox != null ) {
	 * Behavior behavior = sandbox.getCurrentBehavior(); if( behavior != null )
	 * { runtimeResponse = behavior.getCurrentRuntimeResponse(); } } } } }
	 * Expression expression = (Expression)value; if( runtimeResponse != null )
	 * { Expression runtimeExpression = runtimeResponse.lookup( expression );
	 * if( runtimeExpression != null ) { expression = runtimeExpression; } }
	 * return expression.getValue(); } else { return value; } }
	 */
	// todo: this should not be necessary
	protected boolean getValueOfExpression() {
		Class valueClass = getValueClass();
		if (valueClass.isAssignableFrom(Expression.class)) {
			return false;
		} else if (valueClass.isAssignableFrom(Variable.class)) {
			return false;
		} else if (valueClass.isAssignableFrom(Question.class)) {
			return false;
		} else {
			return true;
		}
	}
	private static Behavior m_currentBehavior = null;

	protected Object evaluateIfNecessary(Object o) {
		if (o instanceof Expression) {
			Expression expression = (Expression) o;
			if (m_currentBehavior == null) {
				if (isDeprecated()) {
					// pass
				} else {
					if (expression instanceof Variable) {
						Element owner = getOwner();
						if (owner != null) {
							World world = owner.getWorld();
							if (world != null) {
								Sandbox sandbox = world.getCurrentSandbox();
								if (sandbox != null) {
									m_currentBehavior = sandbox.getCurrentBehavior();
									if (m_currentBehavior != null) {
										Variable runtimeVariable = m_currentBehavior.stackLookup((Variable) expression);
										if (runtimeVariable != null) {
											expression = runtimeVariable;
										}
									}
								}
							}
						}
					}
				}
			}
			Object value;
			if (getValueOfExpression()) {
				value = expression.getValue();
			} else {
				value = expression;
			}
			// if( value == null ) {
			// System.err.println();
			// System.err.println();
			// System.err.println();
			// System.err.println( "VALUE IS NULL" );
			// System.err.println( this );
			// System.err.println( o );
			// System.err.println( expression + " " + expression.hashCode() );
			// System.err.println( m_currentBehavior );
			// Object o = m_currentBehavior.stackLookup( (Variable)expression );
			// System.err.println( o + " " + o.hashCode() );
			// System.err.println();
			// System.err.println();
			// System.err.println();
			// }
			m_currentBehavior = null;

			return value;
		} else {
			return o;
		}
	}
	public Object getValue() {
		return evaluateIfNecessary(m_value);
	}
	private void onChanging(PropertyEvent propertyEvent) {
		m_owner.propertyChanging(propertyEvent);
		if (HACK_s_isListeningEnabled) {
			PropertyListener[] propertyListeners = getPropertyListeners();
			for (PropertyListener propertyListener : propertyListeners) {
				propertyListener.propertyChanging(propertyEvent);
			}
		} else {
			// pass
		}
	}
	private void onChanged(PropertyEvent propertyEvent) {
		// todo
		getElement().markKeepKeyDirty();
		m_owner.propertyChanged(propertyEvent);
		if (HACK_s_isListeningEnabled) {
			PropertyListener[] propertyListeners = getPropertyListeners();
			for (PropertyListener propertyListener : propertyListeners) {
				propertyListener.propertyChanged(propertyEvent);
			}
		} else {
			// pass
		}
	}
	protected void onSet(Object value) {
		Class valueClass = getValueClass();
		PropertyEvent propertyEvent = new PropertyEvent(this, value);
		onChanging(propertyEvent);
		m_value = value;
		onChanged(propertyEvent);
		m_associatedFileKey = null;
	}
	public void set(Object value) throws IllegalArgumentException {
		if (m_value == null) {
			if (value == null) {
				return;
			}
		} else if (m_value.equals(value)) {
			return;
		}
		if (Element.s_isLoading) {
			// pass
		} else {
			checkValueType(value);
			checkForBadReferences(value);
		}
		onSet(value);
	}
	private static void setHowMuch(Element owner, String propertyName, Object value, edu.cmu.cs.stage3.util.HowMuch howMuch) {
		Property property = owner.getPropertyNamed(propertyName);
		if (property != null) {
			property.set(value);
		}
		if (howMuch.getDescend()) {
			for (int i = 0; i < owner.getChildCount(); i++) {
				Element child = owner.getChildAt(i);
				if (child.isFirstClass.booleanValue() && howMuch.getRespectDescendant()) {
					// respect descendant
				} else {
					setHowMuch(child, propertyName, value, howMuch);
				}
			}
		}
	}
	public void set(Object value, edu.cmu.cs.stage3.util.HowMuch howMuch) throws IllegalArgumentException {
		// todo
		if (m_owner instanceof Element) {
			setHowMuch(m_owner, m_name, value, howMuch);
		}
	}

	// /** deprecated */
	// public void load( Class type, String text,
	// edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector
	// referencesToBeResolved, edu.cmu.cs.stage3.util.ProgressObserver
	// progressObserver ) throws java.io.IOException {
	// if( type==Object.class ) {
	// if( text.equalsIgnoreCase( "null" ) ) {
	// set( null );
	// } else {
	// throw new RuntimeException( "java.lang.Object not null" + type + " " +
	// text );
	// }
	// } else if( String.class.isAssignableFrom( type ) ) {
	// set( text );
	// } else if( Class.class.isAssignableFrom( type ) ) {
	// try {
	// set( Class.forName( text.substring( 6 ) ) );
	// } catch( ClassNotFoundException cnfe ) {
	// throw new RuntimeException( "ClassNotFoundException: " + type + " " +
	// text );
	// }
	// } else if( java.awt.Font.class.isAssignableFrom( type ) ) {
	// String[] markers = { "java.awt.Font[family=", ",name=", ",style=",
	// ",size=", "]" };
	// String[] values = new String[markers.length-1];
	// for( int i=0; i<values.length; i++ ) {
	// int begin = text.indexOf( markers[i] ) + markers[i].length();
	// int end = text.indexOf( markers[i+1] );
	// values[i] = text.substring( begin, end );
	// }
	// String name = values[1];
	// int style = java.awt.Font.PLAIN;
	// int size = Integer.parseInt( values[3] );
	// set( new java.awt.Font( name, style, size ) );
	// } else if( Property.class.isAssignableFrom( type ) ) {
	// Element.warnln( "handle " + text );
	// } else if( type.equals( Double.class ) && text.equals( "Infinity" ) ) {
	// set( new Double( Double.POSITIVE_INFINITY ) );
	// } else if( type.equals( Double.class ) && text.equals( "NaN" ) ) {
	// set( new Double( Double.NaN ) );
	// } else {
	// Class[] parameterTypes = { String.class };
	// try {
	// java.lang.reflect.Method valueOfMethod = type.getMethod( "valueOf",
	// parameterTypes );
	// int modifiers = valueOfMethod.getModifiers();
	// if( java.lang.reflect.Modifier.isPublic( modifiers ) &&
	// java.lang.reflect.Modifier.isStatic( modifiers ) ) {
	// Object[] parameters = { text };
	// Object o = valueOfMethod.invoke( null, parameters );
	// if( o instanceof edu.cmu.cs.stage3.util.Criterion ) {
	// //referencesToBeResolved.addElement( new
	// edu.cmu.cs.stage3.alice.core.PropertyCriterionPair( this,
	// (edu.cmu.cs.stage3.util.Criterion)o ) );
	// referencesToBeResolved.addElement( new PropertyReference( this,
	// (edu.cmu.cs.stage3.util.Criterion)o ) );
	// } else {
	// set( o );
	// }
	// } else {
	// throw new RuntimeException( "valueOf method not public static." );
	// }
	// } catch( NoSuchMethodException nsme ) {
	// throw new RuntimeException( "NoSuchMethodException:" + type + " " + text
	// );
	// } catch( IllegalAccessException iae ) {
	// throw new RuntimeException( "IllegalAccessException: " + type + " " +
	// text );
	// } catch( java.lang.reflect.InvocationTargetException ite ) {
	// throw new RuntimeException(
	// "java.lang.reflect.InvocationTargetException: " + type + " " + text );
	// }
	// }
	// }
	protected Object getValueOf(Class type, String text) {
		if (type.equals(Double.class)) {
			if (text.equals("Infinity")) {
				return new Double(Double.POSITIVE_INFINITY);
			} else if (text.equals("NaN")) {
				return new Double(Double.NaN);
			} else {
				return Double.valueOf(text);
			}
		} else if (type.equals(String.class)) {
			return text;
		} else {
			try {
				Class[] parameterTypes = {String.class};
				java.lang.reflect.Method valueOfMethod = type.getMethod("valueOf", parameterTypes);
				int modifiers = valueOfMethod.getModifiers();
				if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isStatic(modifiers)) {
					Object[] parameters = {text};
					return valueOfMethod.invoke(null, parameters);
				} else {
					throw new RuntimeException("valueOf method not public static.");
				}
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException("NoSuchMethodException:" + type);
			} catch (IllegalAccessException iae) {
				throw new RuntimeException("IllegalAccessException: " + type);
			} catch (java.lang.reflect.InvocationTargetException ite) {
				throw new RuntimeException("java.lang.reflect.InvocationTargetException: " + type + " " + text);
			}
		}
	}

	protected String getNodeText(org.w3c.dom.Node node) {
		return edu.cmu.cs.stage3.xml.NodeUtilities.getNodeText(node);
	}

	protected org.w3c.dom.Node createNodeForString(org.w3c.dom.Document document, String s) {
		char[] cdataCharacters = {' ', '\t', '\n', '"', '\'', '>', '<', '&'};
		for (char cdataCharacter : cdataCharacters) {
			if (s.indexOf(cdataCharacter) != -1) {
				return document.createCDATASection(s);
			}
		}
		return document.createTextNode(s);
	}

	protected String getFilename(String text) {
		String[] markers = {"java.io.File[", "]"};
		int begin = text.indexOf(markers[0]) + markers[0].length();
		int end = text.lastIndexOf(markers[1]);
		return text.substring(begin, end);
	}
	protected void decodeReference(org.w3c.dom.Element node, java.util.Vector referencesToBeResolved, double version, String typeName) {
		try {
			Class type = Class.forName(typeName);
			String text = getNodeText(node);
			// todo?
			if (text.equals(".")) {
				text = "";
			}
			edu.cmu.cs.stage3.util.Criterion criterion;
			if (type.isAssignableFrom(edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion.class)) {
				criterion = new edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion(text);
			} else if (type.isAssignableFrom(edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion.class)) {
				criterion = new edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion(text);
			} else {
				criterion = (edu.cmu.cs.stage3.util.Criterion) getValueOf(type, text);
			}
			// String indexText = node.getAttribute( "index" );
			// if( indexText.length()>0 ) {
			// referencesToBeResolved.addElement( new
			// ObjectArrayPropertyReference(
			// (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)this,
			// criterion, Integer.parseInt( indexText ), 0 ) );
			// } else {
			// referencesToBeResolved.addElement( new PropertyReference( this,
			// criterion ) );
			// }
			referencesToBeResolved.addElement(new PropertyReference(this, criterion));
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(typeName);
		}
	}

	protected void decodeObject(org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version) throws java.io.IOException {
		String typeName = node.getAttribute("class");
		if (typeName.length() > 0) {
			String text = getNodeText(node);
			try {
				Class type = Class.forName(typeName);
				set(getValueOf(type, text));
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException(typeName);
			}
		} else {
			System.err.println(this);
			throw new RuntimeException();
		}
	}
	public final void decode(org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version) throws java.io.IOException {
		if (node.hasChildNodes()) {
			String criterionClassname = node.getAttribute("criterionClass");
			if (criterionClassname.length() > 0) {
				decodeReference(node, referencesToBeResolved, version, criterionClassname);
			} else {
				decodeObject(node, loader, referencesToBeResolved, version);
			}
		} else {
			set(null);
		}
	}

	protected void encodeReference(org.w3c.dom.Document document, org.w3c.dom.Element node, ReferenceGenerator referenceGenerator, Element owner) {
		// todo:
		// try {
		edu.cmu.cs.stage3.util.Criterion criterion = referenceGenerator.generateReference(owner);
		if (criterion != null) {
			node.setAttribute("criterionClass", criterion.getClass().getName());
			String s;
			if (criterion instanceof edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion) {
				s = ((edu.cmu.cs.stage3.alice.core.criterion.InternalReferenceKeyedCriterion) criterion).getKey();
			} else if (criterion instanceof edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion) {
				s = ((edu.cmu.cs.stage3.alice.core.criterion.ExternalReferenceKeyedCriterion) criterion).getKey();
			} else {
				s = criterion.toString();
			}
			// todo?
			if (s.length() == 0) {
				s = ".";
			}
			node.appendChild(createNodeForString(document, s));
		}
		// } catch( RuntimeException re ) {
		// System.err.println( "cannot generate reference for\n\t" + this );
		// }
	}
	protected void encodeObject(org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws java.io.IOException {
		Object o = get();
		node.setAttribute("class", o.getClass().getName());
		node.appendChild(createNodeForString(document, o.toString()));
	}
	public final void encode(org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws java.io.IOException {
		Object o = get();
		if (o != null) {
			if (o instanceof Element) {
				encodeReference(document, node, referenceGenerator, (Element) o);
			} else {
				encodeObject(document, node, storer, referenceGenerator);
			}
		}
	}

	public void keepAnyAssociatedFiles(edu.cmu.cs.stage3.io.DirectoryTreeStorer storer) throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException, edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
	}

	public boolean isDeprecated() {
		return m_isDeprecated;
	}
	public void deprecate() {
		m_isDeprecated = true;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[name=" + m_name + ",owner=" + m_owner + "]";
	}
}
