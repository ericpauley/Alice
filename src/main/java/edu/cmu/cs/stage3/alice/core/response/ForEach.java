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

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;

public abstract class ForEach extends DoInOrder {
	public final VariableProperty each = new VariableProperty(this, "each", null);

	// todo: change to collection property
	public final ListProperty list = new ListProperty(this, "list", null);

	@Override
	protected void internalFindAccessibleExpressions(Class cls, java.util.Vector v) {
		internalAddExpressionIfAssignableTo((Expression) each.get(), cls, v);
		super.internalFindAccessibleExpressions(cls, v);
	}
	public class RuntimeForEach extends RuntimeDoInOrder {
		protected int m_listSize = -1;
		protected void setForkIndex(int index) {
			edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if (currentBehavior != null) {
				if (m_listSize > 0) {
					currentBehavior.setForkIndex(this, index);
				}
			}
		}

		@Override
		public void prologue(double t) {
			edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if (currentBehavior != null) {
				edu.cmu.cs.stage3.alice.core.List listValue = list.getListValue();
				if (listValue != null) {
					m_listSize = listValue.size();
				} else {
					m_listSize = 0;
				}
				if (m_listSize > 0) {
					currentBehavior.openFork(this, m_listSize);
					for (int i = 0; i < m_listSize; i++) {
						currentBehavior.setForkIndex(this, i);
						edu.cmu.cs.stage3.alice.core.Variable eachVariable = each.getVariableValue();
						edu.cmu.cs.stage3.alice.core.Variable eachRuntimeVariable = new edu.cmu.cs.stage3.alice.core.Variable();
						eachRuntimeVariable.valueClass.set(eachVariable.valueClass.get());
						eachRuntimeVariable.value.set(list.getListValue().itemAtIndex(i));
						currentBehavior.pushEach(eachVariable, eachRuntimeVariable);
					}
				}
			}
			super.prologue(t);
		}

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			edu.cmu.cs.stage3.alice.core.Behavior currentBehavior = getCurrentBehavior();
			if (currentBehavior != null) {
				if (m_listSize > 0) {
					for (int i = 0; i < m_listSize; i++) {
						currentBehavior.setForkIndex(this, i);
						currentBehavior.popStack();
					}
					currentBehavior.setForkIndex(this, 0);
					currentBehavior.closeFork(this);
				}
			}
		}
		/*
		 * private edu.cmu.cs.stage3.alice.core.Variable m_each; private
		 * edu.cmu.cs.stage3.alice.core.Variable m_runtimeEach; private
		 * edu.cmu.cs.stage3.alice.core.Behavior m_currentBehavior = null;
		 * protected void preEach( int index ) { m_runtimeEach.value.set(
		 * list.getListValue().values.get( index ) ); m_currentBehavior = null;
		 * edu.cmu.cs.stage3.alice.core.World world = ForEach.this.getWorld();
		 * if( world != null ) { edu.cmu.cs.stage3.alice.core.Sandbox sandbox =
		 * world.getCurrentSandbox(); if( sandbox != null ) { m_currentBehavior
		 * = sandbox.getCurrentBehavior(); } } if( m_currentBehavior != null ) {
		 * m_currentBehavior.pushEach( m_each, m_runtimeEach ); } } protected
		 * void postEach() { if( m_currentBehavior != null ) {
		 * m_currentBehavior.popStack(); } m_currentBehavior = null; } public
		 * void prologue( double t ) { if( getListSize() > 0 ) { m_each =
		 * each.getVariableValue(); m_runtimeEach = new
		 * edu.cmu.cs.stage3.alice.core.Variable();
		 * m_runtimeEach.valueClass.set( m_each.valueClass.get() ); } else {
		 * m_runtimeEach = null; } super.prologue( t ); }
		 */
	}
}
