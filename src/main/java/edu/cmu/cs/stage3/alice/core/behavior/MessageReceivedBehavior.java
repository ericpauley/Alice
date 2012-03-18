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

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class MessageReceivedBehavior extends TriggerBehavior implements edu.cmu.cs.stage3.alice.core.event.MessageListener {
	public final TransformableProperty fromWho = new TransformableProperty( this, "fromWho", null );
	public final TransformableProperty toWhom = new TransformableProperty( this, "toWhom", null );
	
	public void manufactureDetails() {
		super.manufactureDetails();
		edu.cmu.cs.stage3.alice.core.Variable message = new edu.cmu.cs.stage3.alice.core.Variable();
		message.name.set( "message" );
		message.setParent( this );
		message.valueClass.set( String.class );
		details.add( message );
		edu.cmu.cs.stage3.alice.core.Variable fromWho = new edu.cmu.cs.stage3.alice.core.Variable();
		fromWho.name.set( "fromWho" );
		fromWho.setParent( this );
		fromWho.valueClass.set( Transformable.class );
		details.add( fromWho );
		edu.cmu.cs.stage3.alice.core.Variable toWhom = new edu.cmu.cs.stage3.alice.core.Variable();
		toWhom.name.set( "message" );
		toWhom.setParent( this );
		toWhom.valueClass.set( Transformable.class );
		details.add( toWhom );
	}

	private void updateDetails( edu.cmu.cs.stage3.alice.core.event.MessageEvent messageEvent ) {
		for( int i=0; i<details.size(); i++ ) {
			Variable detail = (Variable)details.get( i );
			if( detail.name.getStringValue().equals( "message" ) ) {
				detail.value.set( messageEvent.getMessage() );
			} else if( detail.name.getStringValue().equals( "fromWho" ) ) {
				detail.value.set( messageEvent.getFromWho() );
			} else if( detail.name.getStringValue().equals( "toWhom" ) ) {
				detail.value.set( messageEvent.getToWhom() );
			}
		}
	}

	private boolean check( edu.cmu.cs.stage3.alice.core.event.MessageEvent messageEvent ) {
		Transformable fromWhoValue = fromWho.getTransformableValue();
		if( fromWhoValue==null || messageEvent.getFromWho()==fromWhoValue ) {
			Transformable toWhomValue = toWhom.getTransformableValue();
			if( toWhomValue==null || messageEvent.getToWhom()==toWhomValue ) {
				return true;
			}
		}
		return false;
	}

	public void messageSent( edu.cmu.cs.stage3.alice.core.event.MessageEvent messageEvent ) {
		if( check( messageEvent ) ) {
			updateDetails( messageEvent );
			trigger( messageEvent.getWhen()*0.001 );
		}
	}

	
	protected void started( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		super.started( world, time );
		world.addMessageListener( this );
	}
	
	protected void stopped( edu.cmu.cs.stage3.alice.core.World world, double time ) {
		super.stopped( world, time );
		world.removeMessageListener( this );
	}
}
