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
public class AuthoringToolStateAdapter implements AuthoringToolStateListener {
	public void stateChanging(AuthoringToolStateChangedEvent ev) {}
	public void stateChanged(AuthoringToolStateChangedEvent ev) {}
	public void worldLoading(AuthoringToolStateChangedEvent ev) {}
	public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
	public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
	public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
	public void worldStarting(AuthoringToolStateChangedEvent ev) {}
	public void worldStarted(AuthoringToolStateChangedEvent ev) {}
	public void worldStopping(AuthoringToolStateChangedEvent ev) {}
	public void worldStopped(AuthoringToolStateChangedEvent ev) {}
	public void worldPausing(AuthoringToolStateChangedEvent ev) {}
	public void worldPaused(AuthoringToolStateChangedEvent ev) {}
	public void worldSaving(AuthoringToolStateChangedEvent ev) {}
	public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}