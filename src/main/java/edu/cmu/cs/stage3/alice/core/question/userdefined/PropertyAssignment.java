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

package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;

public class PropertyAssignment extends Component {
	public final OverridableElementProperty element = new OverridableElementProperty( this, "element", null );
	public final StringProperty propertyName = new StringProperty( this, "propertyName", null );
	public final ValueProperty value = new ValueProperty( this, "value", null );
	private void updateOverrideValueClasses() {
		Class elementOverrideValueClass = null;
		Class valueOverrideValueClass = null;
		String propertyNameValue = propertyName.getStringValue();
		if( propertyNameValue!=null ) {
			Element elementValue = element.getElementValue();
			if( elementValue!=null ) {
				edu.cmu.cs.stage3.alice.core.Property property = elementValue.getPropertyNamed( propertyNameValue );
				if( property != null ) {
					elementOverrideValueClass = property.getDeclaredClass();
					valueOverrideValueClass = property.getValueClass();
				} else {
					if( elementValue instanceof Expression ) {
						Class cls = ((Expression)elementValue).getValueClass();
						if( cls != null ) {
							elementOverrideValueClass = cls;
							valueOverrideValueClass = Element.getValueClassForPropertyNamed( elementOverrideValueClass, propertyNameValue );
						}
					}
				}
			}
		}
		element.setOverrideValueClass( elementOverrideValueClass );
		value.setOverrideValueClass( valueOverrideValueClass );
	}
	
	protected void propertyChanged( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
		if( property == element ) {
			updateOverrideValueClasses();
		} else if( property == propertyName ) {
			updateOverrideValueClasses();
		} else {
			super.propertyChanged( property, value );
		}
	}
    
	public Object[] execute() {
        element.getElementValue().getPropertyNamed( propertyName.getStringValue() ).set( value.getValue() );
        return null;
    }
}
