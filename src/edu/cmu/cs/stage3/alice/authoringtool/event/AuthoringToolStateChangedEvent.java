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

package edu.cmu.cs.stage3.alice.authoringtool.event;

/**
 * @author Jason Pratt
 */
public class AuthoringToolStateChangedEvent {
	public final static int AUTHORING_STATE = 1;
	public final static int DEBUG_STATE = 2;
	public final static int RUNTIME_STATE = 3;

	private int currentState;
	private int previousState;
	private edu.cmu.cs.stage3.alice.core.World world;

	public AuthoringToolStateChangedEvent( int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world ) {
		this.currentState = currentState;
		this.previousState = previousState;
		this.world = world;
	}

	/**
	 * @deprecated Please use getCurrentState() instead
	 */
	public int getState() {
		return getCurrentState();
	}

	public int getCurrentState() {
		return currentState;
	}

	public int getPreviousState() {
		return previousState;
	}

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return world;
	}
}