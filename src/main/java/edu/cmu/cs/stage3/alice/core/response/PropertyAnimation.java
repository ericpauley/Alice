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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;

public class PropertyAnimation extends Animation {
	public final OverridableElementProperty element = new OverridableElementProperty(this, "element", null);
	public final StringProperty propertyName = new StringProperty(this, "propertyName", null);
	public final ValueProperty value = new ValueProperty(this, "value", null);
	public final ObjectProperty howMuch = new ObjectProperty(this, "howMuch", edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS, edu.cmu.cs.stage3.util.HowMuch.class);
	private void updateOverrideValueClasses() {
		Class elementOverrideValueClass = null;
		Class valueOverrideValueClass = null;
		String propertyNameValue = propertyName.getStringValue();
		if (propertyNameValue != null) {
			Element elementValue = element.getElementValue();
			if (elementValue != null) {
				Property property = elementValue.getPropertyNamed(propertyNameValue);
				if (property != null) {
					elementOverrideValueClass = property.getDeclaredClass();
					valueOverrideValueClass = property.getValueClass();
				} else {
					if (elementValue instanceof Expression) {
						Class cls = ((Expression) elementValue).getValueClass();
						if (cls != null) {
							elementOverrideValueClass = cls;
							valueOverrideValueClass = Element.getValueClassForPropertyNamed(elementOverrideValueClass, propertyNameValue);
						}
					}
				}
			}
		}
		element.setOverrideValueClass(elementOverrideValueClass);
		value.setOverrideValueClass(valueOverrideValueClass);
	}

	@Override
	protected void propertyChanged(Property property, Object value) {
		if (property == element) {
			updateOverrideValueClasses();
		} else if (property == propertyName) {
			updateOverrideValueClasses();
		} else {
			super.propertyChanged(property, value);
		}
	}
	public class RuntimePropertyAnimation extends RuntimeAnimation {
		private Property m_property;
		private Object m_valueBegin;
		private Object m_valueEnd;
		private edu.cmu.cs.stage3.util.HowMuch m_howMuch;

		private edu.cmu.cs.stage3.alice.core.Element m_element;
		private String m_propertyName;

		protected Property getProperty() {
			return m_property;
		}
		protected void set(Object value) {
			if (m_property != null) {
				if (howMuch != null) {
					m_property.set(value, m_howMuch);
				} else {
					m_property.set(value);
				}
			} else {
				m_element.setPropertyNamed(m_propertyName, value, m_howMuch);
			}
		}

		@Override
		public void prologue(double t) {
			super.prologue(t);
			// todo: remove? the following line is required for when an author
			// changes the element property to a variable
			updateOverrideValueClasses();
			m_element = element.getElementValue();
			m_propertyName = propertyName.getStringValue();
			if (m_element != null) {
				if (m_propertyName != null) {
					m_property = m_element.getPropertyNamed(m_propertyName);
					if (m_property != null) {
						m_valueBegin = m_property.getValue();
						m_valueEnd = value.getValue();
						if (m_property.isAcceptingOfHowMuch()) {
							m_howMuch = (edu.cmu.cs.stage3.util.HowMuch) howMuch.getValue();
						} else {
							m_howMuch = edu.cmu.cs.stage3.util.HowMuch.INSTANCE;
						}
					} else {
						throw new edu.cmu.cs.stage3.alice.core.IllegalPropertyValueException(propertyName, m_propertyName, m_element + " does not have property named " + m_propertyName);
					}
				} else {
					throw new edu.cmu.cs.stage3.alice.core.IllegalPropertyValueException(propertyName, null, "propertyName must not be null.");
				}
			} else {
				throw new edu.cmu.cs.stage3.alice.core.IllegalPropertyValueException(element, null, "element must not be null.");
			}
		}

		@Override
		public void update(double t) {
			super.update(t);
			Object value;
			if (m_valueBegin != null && m_valueEnd != null) {
				value = edu.cmu.cs.stage3.math.Interpolator.interpolate(m_valueBegin, m_valueEnd, getPortion(t));
			} else {
				value = m_valueEnd;
			}
			set(value);
		}

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			set(m_valueEnd);
		}
	}
}