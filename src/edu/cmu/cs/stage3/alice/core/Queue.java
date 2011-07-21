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

public class Queue extends Collection {
	public boolean isEmpty() {
		return values.isEmpty();
	}
	public int size() {
		return values.size();
	}
    public Object front() {
        return values.get( 0 );
    }
    public Object frontValue() {
		return values.getValue( 0 );
    }
	public void enqueue( Object item ) {
		values.add( -1, item );
	}
	public void enqueueValue( Object item ) {
		values.addValue( -1, item );
	}
	public void dequeue() {
		values.remove( 0 );
	}
}