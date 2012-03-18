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

package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class ConditionalTriggerBehavior extends TriggerBehavior {
	private static Class[] s_supportedCoercionClasses = { ConditionalBehavior.class };
	
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public final BooleanProperty condition = new BooleanProperty( this, "condition", null );
	
	protected void internalSchedule( double t, double dt ) {
		Object conditionValue = condition.get();
		if( conditionValue instanceof Expression ) {
			conditionValue = ((Expression)conditionValue).getValue();
		}
		if( conditionValue != null ) {
			boolean currentValue = ((Boolean)conditionValue).booleanValue();
			if( m_previousValue ) {
				//pass
			} else {
				if( currentValue ) {
					trigger();
				}
			}
			m_previousValue = currentValue;
		} else {
			m_previousValue = false;
		}
		super.internalSchedule( t, dt );
	}
	private boolean m_previousValue;
	
	protected void started( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		super.started( world, time );
		m_previousValue = false;
	}
}
