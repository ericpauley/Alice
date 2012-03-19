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

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class ElementPrototype {
	protected Class elementClass;
	protected edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues;
	protected String[] desiredProperties;

	public ElementPrototype(Class elementClass, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties) {
		if (!edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(elementClass)) {
			throw new IllegalArgumentException("The elementClass given is not actually a subclass of Element.");
		}

		edu.cmu.cs.stage3.alice.core.Element testElement = null;
		try {
			testElement = (edu.cmu.cs.stage3.alice.core.Element) elementClass.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create a new element of type: " + elementClass.getName());
		}
		if (!(edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class.isAssignableFrom(elementClass) || edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class.isAssignableFrom(elementClass))) { // don't
																																																											// do
																																																											// checking
																																																											// for
																																																											// CallToUserDefinedResponse/Question,
																																																											// since
																																																											// they
																																																											// use
																																																											// known
																																																											// and
																																																											// desired
																																																											// properties
																																																											// for
																																																											// requiredParameters
			if (knownPropertyValues != null) {
				for (StringObjectPair knownPropertyValue : knownPropertyValues) {
					String propertyName = knownPropertyValue.getString();
					Object propertyValue = knownPropertyValue.getObject();
					edu.cmu.cs.stage3.alice.core.Property property = testElement.getPropertyNamed(propertyName);
					if (property == null) {
						throw new IllegalArgumentException("property named " + propertyName + " does not exist in " + elementClass.getName());
					}
					if (propertyValue == null) {
						// if( ! property.isAcceptingOfNull() ) {
						// throw new IllegalArgumentException( "property named "
						// + propertyName + " in class " +
						// elementClass.getName() +
						// " does not accept null values" );
						// }
					} else if (propertyValue instanceof edu.cmu.cs.stage3.alice.core.Expression) {
						Class valueClass = property.getValueClass();
						if (property.getValueClass().isAssignableFrom(propertyValue.getClass())) {
							// allow
						} else {
							if (!property.getValueClass().isAssignableFrom(((edu.cmu.cs.stage3.alice.core.Expression) propertyValue).getValueClass())) {
								throw new IllegalArgumentException("property named " + propertyName + " in class " + elementClass.getName() + " does not accept expressions of type " + ((edu.cmu.cs.stage3.alice.core.Expression) propertyValue).getValueClass().getName());
							}
						}
					} else {
						if (!property.getValueClass().isAssignableFrom(propertyValue.getClass())) {
							throw new IllegalArgumentException("property named " + propertyName + " in class " + elementClass.getName() + " does not accept values of type " + propertyValue.getClass().getName() + "; bad value: " + propertyValue);
						}
					}
				}
			} else {
				knownPropertyValues = new edu.cmu.cs.stage3.util.StringObjectPair[0];
			}
			if (desiredProperties != null) {
				for (String desiredPropertie : desiredProperties) {
					edu.cmu.cs.stage3.alice.core.Property property = testElement.getPropertyNamed(desiredPropertie);
					if (property == null) {
						throw new IllegalArgumentException("property named " + desiredPropertie + " does not exist in " + elementClass.getName());
					}
				}
			} else {
				desiredProperties = new String[0];
			}
		}

		this.elementClass = elementClass;
		this.knownPropertyValues = knownPropertyValues;
		this.desiredProperties = desiredProperties;
	}

	public edu.cmu.cs.stage3.alice.core.Element createNewElement() {
		try {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) elementClass.newInstance();

			if (knownPropertyValues != null) {
				for (StringObjectPair knownPropertyValue : knownPropertyValues) {
					String propertyName = knownPropertyValue.getString();
					Object propertyValue = knownPropertyValue.getObject();
					edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(propertyName);
					property.set(propertyValue);

					if (propertyValue instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) { // this
																										// is
																										// pretty
																										// aggressive
						edu.cmu.cs.stage3.alice.core.question.PropertyValue propertyValueQuestion = (edu.cmu.cs.stage3.alice.core.question.PropertyValue) propertyValue;
						propertyValueQuestion.removeFromParent();
						property.getOwner().addChild(propertyValueQuestion);
					} else if (propertyValue instanceof edu.cmu.cs.stage3.alice.core.Question) {
						edu.cmu.cs.stage3.alice.core.Question q = (edu.cmu.cs.stage3.alice.core.Question) propertyValue;
						q.removeFromParent();
						property.getOwner().addChild(q);
						// What the hell is this used for?
						q.data.put("associatedProperty", property.getName());
					}

					// HACK; is this too aggressive?
					if (propertyValue instanceof edu.cmu.cs.stage3.alice.core.Element) {
						edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) propertyValue;
						if (e.getParent() == null && !(e instanceof edu.cmu.cs.stage3.alice.core.World)) {
							property.getOwner().addChild(e);
							e.data.put("associatedProperty", property.getName());
						}
					}
					// END HACK
				}
			}

			if (edu.cmu.cs.stage3.alice.core.response.ForEach.class.isAssignableFrom(elementClass)) {
				edu.cmu.cs.stage3.alice.core.response.ForEach forResponse = null;
				if (edu.cmu.cs.stage3.alice.core.response.ForEachInOrder.class.isAssignableFrom(elementClass)) {
					forResponse = (edu.cmu.cs.stage3.alice.core.response.ForEachInOrder) element;
				} else if (edu.cmu.cs.stage3.alice.core.response.ForEachTogether.class.isAssignableFrom(elementClass)) {
					forResponse = (edu.cmu.cs.stage3.alice.core.response.ForEachTogether) element;
				}
				edu.cmu.cs.stage3.alice.core.Variable eachVar = new edu.cmu.cs.stage3.alice.core.Variable();
				eachVar.name.set("item");
				eachVar.valueClass.set(Object.class);
				forResponse.addChild(eachVar);
				forResponse.each.set(eachVar);
			} else if (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach.class.isAssignableFrom(elementClass)) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach forQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach) element;
				edu.cmu.cs.stage3.alice.core.Variable eachVar = new edu.cmu.cs.stage3.alice.core.Variable();
				eachVar.name.set("item");
				eachVar.valueClass.set(Object.class);
				forQuestion.addChild(eachVar);
				forQuestion.each.set(eachVar);
			} else if (edu.cmu.cs.stage3.alice.core.response.LoopNInOrder.class.isAssignableFrom(elementClass)) {
				edu.cmu.cs.stage3.alice.core.response.LoopNInOrder loopN = (edu.cmu.cs.stage3.alice.core.response.LoopNInOrder) element;
				edu.cmu.cs.stage3.alice.core.Variable indexVar = new edu.cmu.cs.stage3.alice.core.Variable();
				indexVar.name.set("index");
				indexVar.valueClass.set(Number.class);
				loopN.addChild(indexVar);
				loopN.index.set(indexVar);
			} else if (edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN.class.isAssignableFrom(elementClass)) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN loopN = (edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN) element;
				edu.cmu.cs.stage3.alice.core.Variable indexVar = new edu.cmu.cs.stage3.alice.core.Variable();
				indexVar.name.set("index");
				indexVar.valueClass.set(Number.class);
				loopN.addChild(indexVar);
				loopN.index.set(indexVar);
			}
			return element;
		} catch (Exception e) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error creating new element.", e); // should
																													// not
																													// be
																													// reached
																													// if
																													// we
																													// did
																													// adequate
																													// checking
																													// in
																													// Constructor.
		}

		return null;
	}

	public ElementPrototype createCopy(edu.cmu.cs.stage3.util.StringObjectPair newKnownPropertyValue) {
		return createCopy(new edu.cmu.cs.stage3.util.StringObjectPair[]{newKnownPropertyValue});
	}

	public ElementPrototype createCopy(edu.cmu.cs.stage3.util.StringObjectPair[] newKnownPropertyValues) {
		java.util.Vector vKnownPropertyValues = new java.util.Vector(java.util.Arrays.asList(knownPropertyValues));
		java.util.Vector vDesiredProperties = new java.util.Vector(java.util.Arrays.asList(desiredProperties));

		if (newKnownPropertyValues != null) {
			for (StringObjectPair newKnownPropertyValue : newKnownPropertyValues) {
				if (vDesiredProperties.contains(newKnownPropertyValue.getString())) {
					vDesiredProperties.remove(newKnownPropertyValue.getString());
				}
				boolean subbed = false;
				for (java.util.ListIterator iter = vKnownPropertyValues.listIterator(); iter.hasNext();) {
					edu.cmu.cs.stage3.util.StringObjectPair pair = (edu.cmu.cs.stage3.util.StringObjectPair) iter.next();
					if (pair.getString().equals(newKnownPropertyValue.getString())) {
						iter.set(newKnownPropertyValue);
						subbed = true;
					}
				}
				if (!subbed) {
					vKnownPropertyValues.add(newKnownPropertyValue);
				}
			}
		}

		return createInstance(elementClass, (edu.cmu.cs.stage3.util.StringObjectPair[]) vKnownPropertyValues.toArray(new edu.cmu.cs.stage3.util.StringObjectPair[0]), (String[]) vDesiredProperties.toArray(new String[0]));
	}

	public Class getElementClass() {
		return elementClass;
	}

	public edu.cmu.cs.stage3.util.StringObjectPair[] getKnownPropertyValues() {
		return knownPropertyValues;
	}

	public String[] getDesiredProperties() {
		return desiredProperties;
	}

	// a rather inelegant solution for creating copies of the correct type.
	// subclasses should overrider this method and call their own constructor
	protected ElementPrototype createInstance(Class elementClass, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties) {
		return new ElementPrototype(elementClass, knownPropertyValues, desiredProperties);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName() + "[ ");
		sb.append("elementClass = " + elementClass.getName() + ", ");
		sb.append("knownPropertyValues = [ ");
		if (knownPropertyValues != null) {
			for (StringObjectPair knownPropertyValue : knownPropertyValues) {
				// sb.append( knownPropertyValues[i].toString() + ", " );
				sb.append("StringObjectPair( ");
				sb.append(knownPropertyValue.getString() + ", ");
				sb.append(knownPropertyValue.getObject().toString() + ", ");
				sb.append("), ");
			}
		} else {
			sb.append("<null>");
		}
		sb.append(" ], ");
		sb.append("desiredProperties = [ ");
		if (desiredProperties != null) {
			for (String desiredPropertie : desiredProperties) {
				sb.append(desiredPropertie + ", ");
			}
		} else {
			sb.append("<null>");
		}
		sb.append(" ], ]");

		return sb.toString();
	}
}