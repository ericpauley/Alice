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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class PropertyValue extends edu.cmu.cs.stage3.alice.core.Question {
	private boolean m_ignorePropertyChanges = false;
	public final OverridableElementProperty element = new OverridableElementProperty(this, "element", null);
	public final StringProperty propertyName = new StringProperty(this, "propertyName", null);
	private void updateOverrideValueClass() {
		Class elementOverrideValueClass = null;
		String propertyNameValue = propertyName.getStringValue();
		if (propertyNameValue != null) {
			Element elementValue = element.getElementValue();
			if (elementValue != null) {
				Property property = elementValue.getPropertyNamed(propertyNameValue);
				if (property != null) {
					elementOverrideValueClass = property.getDeclaredClass();
				} else {
					if (elementValue instanceof Expression) {
						Class cls = ((Expression) elementValue).getValueClass();
						if (cls != null) {
							elementOverrideValueClass = cls;
						}
					}
				}
			}
		}
		element.setOverrideValueClass(elementOverrideValueClass);
	}

	@Override
	protected void propertyChanged(Property property, Object value) {
		if (m_ignorePropertyChanges) {
			return;
		}
		if (property == element) {
			updateOverrideValueClass();
		} else if (property == propertyName) {
			updateOverrideValueClass();
		} else {
			super.propertyChanged(property, value);
		}
	}
	private Property getPropertyValue() {
		if (element.getOverrideValueClass() == null) {
			updateOverrideValueClass();
		}
		Element elementValue = element.getElementValue();
		String propertyNameValue = propertyName.getStringValue();
		if (elementValue != null && propertyNameValue != null) {
			return elementValue.getPropertyNamed(propertyNameValue);
			// Property property = elementValue.getPropertyNamed(
			// propertyNameValue );
			// if( property == null ) {
			// if( elementValue instanceof Expression ) {
			// Expression expression = (Expression)elementValue;
			// if( Element.class.isAssignableFrom( expression.getValueClass() )
			// ) {
			// elementValue = (Element)expression.getValue();
			// if( elementValue != null ) {
			// property = elementValue.getPropertyNamed( propertyNameValue );
			// }
			// }
			// }
			// }
			// return property;
		} else {
			return null;
		}
	}

	@Override
	public Object getValue() {
		Property property = getPropertyValue();
		if (property != null) {
			return property.getValue();
		} else {
			throw new RuntimeException();
			// return null;
		}
	}

	@Override
	public Class getValueClass() {
		Property property = getPropertyValue();
		if (property != null) {
			return property.getValueClass();
		} else {
			String propertyNameValue = propertyName.getStringValue();
			if (propertyNameValue != null) {
				Class cls = element.getValueClass();
				if (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(cls)) {
					try {
						java.lang.reflect.Field field = cls.getField(propertyNameValue);
						if (field != null) {
							return Element.getValueClassForPropertyNamed(field.getDeclaringClass(), propertyNameValue);
						}
					} catch (java.lang.NoSuchFieldException nsfe) {
						// pass
					} catch (java.lang.SecurityException se) {
						// pass
					}
				}
			}
			return Object.class;
		}
	}
}