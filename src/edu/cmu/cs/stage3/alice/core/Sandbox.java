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

import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;

public abstract class Sandbox extends Element {
	public final ScriptProperty script = new ScriptProperty( this, "script", null );
	public final ElementArrayProperty responses = new ElementArrayProperty( this, "responses", null, Response[].class );
	public final ElementArrayProperty behaviors = new ElementArrayProperty( this, "behaviors", null, Behavior[].class );
	public final ElementArrayProperty variables = new ElementArrayProperty( this, "variables", null, Variable[].class );
	public final ElementArrayProperty questions = new ElementArrayProperty( this, "questions", null, Question[].class );
	public final ElementArrayProperty textureMaps = new ElementArrayProperty( this, "textureMaps", null, TextureMap[].class );
	public final ElementArrayProperty sounds = new ElementArrayProperty( this, "sounds", null, Sound[].class );
	public final ElementArrayProperty geometries = new ElementArrayProperty( this, "geometries", null, Geometry[].class );
	public final ElementArrayProperty misc = new ElementArrayProperty( this, "misc", null, Element[].class );

	private Behavior m_currentBehavior = null;

	public Expression lookup( String key ) {
		if( m_currentBehavior != null ) {
            Expression expression = m_currentBehavior.stackLookup( key );
            if( expression != null ) {
                return expression;
            } else {
    			return m_currentBehavior.detailLookup( key );
            }
		} else {
			return null;
		}
	}

	public Behavior getCurrentBehavior() {
		return m_currentBehavior;
	}

	protected void scheduleBehaviors( double t ) {
		for( int i=0; i<behaviors.size(); i++ ) {
			Behavior behaviorI = (Behavior)behaviors.get( i );
//			m_currentBehavior = null;
//			behaviorI.preSchedule( t );
//			m_currentBehavior = behaviorI;
//			behaviorI.schedule( t );
//			m_currentBehavior = null;
//			behaviorI.postSchedule( t );
			m_currentBehavior = behaviorI;
			behaviorI.preSchedule( t );
			behaviorI.schedule( t );
			behaviorI.postSchedule( t );
			m_currentBehavior = null;
		}
	}

	
	protected void internalFindAccessibleExpressions( Class cls, java.util.Vector v ) {
		for( int i=0; i<variables.size(); i++ ) {
			internalAddExpressionIfAssignableTo( (Expression)variables.get( i ), cls, v );
		}
		for( int i=0; i<questions.size(); i++ ) {
			internalAddExpressionIfAssignableTo( (Expression)questions.get( i ), cls, v );
		}
		//Aik Min commented this to remove function listing twice in the drop down menu.
		//super.internalFindAccessibleExpressions( cls, v );
	}
}
