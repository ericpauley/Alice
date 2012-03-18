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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class CallToUserDefinedResponsePrototype extends ResponsePrototype {
	protected edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse;
	protected RefreshListener refreshListener = new RefreshListener();

	public CallToUserDefinedResponsePrototype( edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse ) {
		super( edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class, new edu.cmu.cs.stage3.util.StringObjectPair[0], new String[0] );
		this.actualResponse = actualResponse;
	}

	protected CallToUserDefinedResponsePrototype( edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties ) {
		super( edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class, knownPropertyValues, desiredProperties );
		this.actualResponse = actualResponse;
	}

	public void startListening() {
		if( actualResponse != null ) {
			actualResponse.requiredFormalParameters.addObjectArrayPropertyListener( refreshListener );
			Object[] vars = actualResponse.requiredFormalParameters.getArrayValue();
			desiredProperties = new String[vars.length];
			for( int i = 0; i < vars.length; i++ ) {
				((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.addPropertyListener( refreshListener );
				desiredProperties[i] = ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.getStringValue();
			}
		}
	}

	public void stopListening() {
		if( actualResponse != null ) {
			actualResponse.requiredFormalParameters.removeObjectArrayPropertyListener( refreshListener );
			Object[] vars = actualResponse.requiredFormalParameters.getArrayValue();
			desiredProperties = new String[vars.length];
			for( int i = 0; i < vars.length; i++ ) {
				((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.removePropertyListener( refreshListener );
				desiredProperties[i] = ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.getStringValue();
			}
		}
	}

	
	public edu.cmu.cs.stage3.alice.core.Element createNewElement() {
		java.util.HashMap knownMap = new java.util.HashMap();
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			knownMap.put( knownPropertyValues[i].getString(), knownPropertyValues[i].getObject() );
		}

		edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = new edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse();
		callToUserDefinedResponse.userDefinedResponse.set( actualResponse );
		Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
		for( int i = 0; i < params.length; i++ ) {
			edu.cmu.cs.stage3.alice.core.Variable formalParameter = (edu.cmu.cs.stage3.alice.core.Variable)params[i];
			edu.cmu.cs.stage3.alice.core.Variable actualParameter = new edu.cmu.cs.stage3.alice.core.Variable();
			actualParameter.name.set( formalParameter.name.get() );
			actualParameter.valueClass.set( formalParameter.valueClass.get() );
			if (!knownMap.containsKey(formalParameter.name.get())){
				actualParameter.value.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass((Class)formalParameter.valueClass.get()) );
			} else{
				actualParameter.value.set( knownMap.get( formalParameter.name.get() ) );
			}
			callToUserDefinedResponse.addChild( actualParameter );
			callToUserDefinedResponse.requiredActualParameters.add( actualParameter );
		}

		return callToUserDefinedResponse;
	}

	
	public ElementPrototype createCopy( edu.cmu.cs.stage3.util.StringObjectPair[] newKnownPropertyValues ) {
		return super.createCopy( newKnownPropertyValues );
	}

	public edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse getActualResponse() {
		return actualResponse;
	}

	
	public String[] getDesiredProperties() {
		return desiredProperties;
	}

	public void calculateDesiredProperties() {
		Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
		desiredProperties = new String[params.length];
		for( int i = 0; i < params.length; i++ ) {
			desiredProperties[i] = ((edu.cmu.cs.stage3.alice.core.Variable)params[i]).name.getStringValue();
		}
	}

	// a rather inelegant solution for creating copies of the correct type.
	// subclasses should override this method and call their own constructor
	
	protected ElementPrototype createInstance( Class elementClass, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues, String[] desiredProperties ) {
		return new CallToUserDefinedResponsePrototype( actualResponse, knownPropertyValues, desiredProperties );
	}

	class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			try {
				if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
					edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)ev.getItem();
					variable.name.addPropertyListener( this );
				} else if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED ) {
					edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)ev.getItem();
					variable.name.removePropertyListener( this );
				}
			} catch( Throwable t ) {
				//BIG FAT HACK
			}
			calculateDesiredProperties();
		}
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
			calculateDesiredProperties();
		}
	}
}

