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

public class CDUtil {
	private static boolean s_successfullyLoadedLibrary;
	static {
		try {
			System.loadLibrary("jni_cdutil");
			s_successfullyLoadedLibrary = true;
		} catch (UnsatisfiedLinkError ule) {
			s_successfullyLoadedLibrary = false;
		}
	}
	private static native String[] getCDRootPaths();
	public static java.io.File[] getCDRoots() {
		if (s_successfullyLoadedLibrary) {
			String[] cdRootPaths = getCDRootPaths();
			if (cdRootPaths != null) {
				java.io.File[] files = new java.io.File[cdRootPaths.length];
				for (int i = 0; i < cdRootPaths.length; i++) {
					files[i] = new java.io.File(cdRootPaths[i]);
				}
				return files;
			}
		}
		return new java.io.File[0];
	}
	/*
	 * public static void main( String[] args ) { java.io.File[] cdRoots =
	 * getCDRoots(); for( int i=0; i<cdRoots.length; i++ ) { System.err.println(
	 * "file: " + cdRoots[ i ] ); } }
	 */
}