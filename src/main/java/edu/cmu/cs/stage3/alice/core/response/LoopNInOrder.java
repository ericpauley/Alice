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

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public class LoopNInOrder extends DoInOrder {
	/** @deprecated */
	public final NumberProperty count = new NumberProperty( this, "count", null );

	public final VariableProperty index = new VariableProperty( this, "index", null );

	public final NumberProperty start = new NumberProperty( this, "start", new Double( 0 ) );
	public final NumberProperty end = new NumberProperty( this, "end", new Double( Double.POSITIVE_INFINITY ) );
	public final NumberProperty increment = new NumberProperty( this, "increment", new Double( 1 ) );

	private static Class[] s_supportedCoercionClasses = {};
	
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}

	public LoopNInOrder() {
		//count.deprecate();
	}

	
	protected void loadCompleted() {
		super.loadCompleted();
		if( index.get() == null ) {
			if( count.get() != null ) {
				end.set( count.get() );
			}
			edu.cmu.cs.stage3.alice.core.Variable indexVariable = new edu.cmu.cs.stage3.alice.core.Variable();
			indexVariable.valueClass.set( Number.class );
			indexVariable.name.set( "index" );
			indexVariable.setParent( this );
			index.set( indexVariable );
		}
	}
	
	protected void internalFindAccessibleExpressions( Class cls, java.util.Vector v ) {
		internalAddExpressionIfAssignableTo( (edu.cmu.cs.stage3.alice.core.Expression)index.get(), cls, v );
		super.internalFindAccessibleExpressions( cls, v );
	}
	public class RuntimeLoopNInOrder extends RuntimeDoInOrder {
		private int m_endTest;
		private double getIndexValue() {
			edu.cmu.cs.stage3.alice.core.Variable indexVariable = LoopNInOrder.this.index.getVariableValue();
			Number number = (Number)indexVariable.value.getValue();
			return number.doubleValue();
		}
		private void setIndexValue( double value ) {
			edu.cmu.cs.stage3.alice.core.Variable indexVariable = LoopNInOrder.this.index.getVariableValue();
			indexVariable.value.set( new Double( value ) );
		}
		
		protected boolean preLoopTest( double t ) {
			return getIndexValue() < m_endTest;
		}
		
		protected boolean postLoopTest( double t ) {
			setIndexValue( getIndexValue() + LoopNInOrder.this.increment.doubleValue( 1 ) );
			return true;
		}
		
		protected boolean isCullable() {
			return false;
		}
		
		public void prologue( double t ) {
			edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if( currentBehavior != null ) {
				edu.cmu.cs.stage3.alice.core.Variable indexVariable = LoopNInOrder.this.index.getVariableValue();
				edu.cmu.cs.stage3.alice.core.Variable indexRuntimeVariable = new edu.cmu.cs.stage3.alice.core.Variable();
				indexRuntimeVariable.valueClass.set( indexVariable.valueClass.get() );
				indexRuntimeVariable.value.set( LoopNInOrder.this.start.getNumberValue() );
				currentBehavior.pushEach( indexVariable, indexRuntimeVariable );
			}
			m_endTest = (int)LoopNInOrder.this.end.doubleValue( Double.POSITIVE_INFINITY );
			super.prologue( t );
		}
		
		public void epilogue( double t ) {
			super.epilogue( t );
			edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if( currentBehavior != null ) {
				currentBehavior.popStack();
			}
		}		
	}
}


