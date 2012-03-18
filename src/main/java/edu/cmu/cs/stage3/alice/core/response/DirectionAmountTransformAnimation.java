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

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;

public abstract class DirectionAmountTransformAnimation extends TransformAnimation {
	public final DirectionProperty direction = new DirectionProperty( this, "direction", getDefaultDirection() );
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 1 ) );
	protected abstract Direction getDefaultDirection();
	protected abstract boolean acceptsDirection( Direction direction );
	
	
	protected void propertyChanging( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
		if( property == direction ) {
			if( value instanceof Direction ) {
				if( acceptsDirection( (Direction)value ) ) {
					//pass
				} else {
					throw new RuntimeException( this + " does not accept direction " + value );
				}
			}
		} else {
			super.propertyChanging( property, value );
		}
	}

	public class RuntimeDirectionAmountTransformAnimation extends RuntimeTransformAnimation {
		
		public void prologue( double t ) {
			super.prologue( t );
			if( DirectionAmountTransformAnimation.this.direction.getDirectionValue() == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "direction value must not be null.", null, DirectionAmountTransformAnimation.this.direction );
			}
			//if( DirectionAmountTransformAnimation.this.amount.getValue() == null ) {
			//	throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "amount value must not be null.", null, DirectionAmountTransformAnimation.this.amount );
			//}
		}
	}
	
}
