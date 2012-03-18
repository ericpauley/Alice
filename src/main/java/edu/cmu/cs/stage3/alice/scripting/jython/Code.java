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

package edu.cmu.cs.stage3.alice.scripting.jython;

/**
 * @author Dennis Cosgrove
 */
public class Code implements edu.cmu.cs.stage3.alice.scripting.Code {
	private org.python.core.PyCode m_pyCode;
	private edu.cmu.cs.stage3.alice.scripting.CompileType m_compileType;
	public Code( org.python.core.PyCode pyCode, edu.cmu.cs.stage3.alice.scripting.CompileType compileType ) {
		m_pyCode = pyCode;
		m_compileType = compileType;
	}
	public org.python.core.PyCode getPyCode() {
		return m_pyCode;
	}
	public edu.cmu.cs.stage3.alice.scripting.CompileType getCompileType() {
		return m_compileType;
	}
}
