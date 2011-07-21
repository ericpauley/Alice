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

public class MultipleRuntimeResponsePolicy extends edu.cmu.cs.stage3.util.Enumerable {
	public static final MultipleRuntimeResponsePolicy IGNORE_MULTIPLE = new MultipleRuntimeResponsePolicy();
	public static final MultipleRuntimeResponsePolicy ENQUEUE_MULTIPLE = new MultipleRuntimeResponsePolicy();
	public static final MultipleRuntimeResponsePolicy INTERLEAVE_MULTIPLE = new MultipleRuntimeResponsePolicy();
	public static MultipleRuntimeResponsePolicy valueOf( String s ) {
		return (MultipleRuntimeResponsePolicy)edu.cmu.cs.stage3.util.Enumerable.valueOf( s, MultipleRuntimeResponsePolicy.class );
	}
}
