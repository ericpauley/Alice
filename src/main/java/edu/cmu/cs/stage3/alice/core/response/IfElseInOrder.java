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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;

public class IfElseInOrder extends DoInOrder {
	public final BooleanProperty condition = new BooleanProperty( this, "condition", Boolean.TRUE );
	public final ElementArrayProperty elseComponentResponses = new ElementArrayProperty( this, "elseComponentResponses", null, edu.cmu.cs.stage3.alice.core.Response[].class );
	private static Class[] s_supportedCoercionClasses = {};
	
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public class RuntimeIfElseInOrder extends RuntimeDoInOrder {
		private RuntimeResponse[] m_runtimeElseComponentResponses = null;
		private boolean m_condition;
		public RuntimeIfElseInOrder() {
			super();
			m_runtimeElseComponentResponses = manufactureComponentRuntimeResponses( elseComponentResponses );
		}
		
		protected RuntimeResponse[] getRuntimeResponses() {
			if( m_condition ) {
				return super.getRuntimeResponses();
			} else {
				return m_runtimeElseComponentResponses;
			}
		}
		
		public void prologue( double t ) {
			m_condition = IfElseInOrder.this.condition.booleanValue();
			super.prologue( t );
		}
	}
}
