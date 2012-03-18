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
public class Interpreter implements edu.cmu.cs.stage3.alice.scripting.Interpreter {
	static final java.util.Dictionary s_map = new java.util.Hashtable();
	static {
		s_map.put( edu.cmu.cs.stage3.alice.scripting.CompileType.EVAL, "eval" );
		s_map.put( edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_SINGLE, "single" );
		s_map.put( edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_MULTIPLE, "exec" );
	}

	private ScriptingFactory m_scriptingFactory;
	private org.python.core.PyModule m_module;
	private Namespace m_dict;

	private edu.cmu.cs.stage3.alice.core.World m_world;

	public Interpreter( ScriptingFactory scriptingFactory ) {
		m_scriptingFactory = scriptingFactory;

		m_dict = new Namespace();
		m_module = new org.python.core.PyModule( "main", m_dict );
	}

	private void resetNamespace() {
		m_dict.clear();
		m_dict.setWorld( m_world );
	}
	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		m_world = world;
		resetNamespace();
	}
	public void release() {
		m_scriptingFactory.releaseInterpreter( this );
		m_scriptingFactory = null;
	}

	public void start() {
		resetNamespace();
	}
	public void stop() {
	}

	public edu.cmu.cs.stage3.alice.scripting.Code compile( String script, Object source, edu.cmu.cs.stage3.alice.scripting.CompileType compileType ) {
		org.python.core.PyCode pyCode = org.python.core.Py.compile_flags( script, source.toString(), (String)s_map.get( compileType ), null );
		return new Code( pyCode, compileType );
	}
	public Object eval( edu.cmu.cs.stage3.alice.scripting.Code code ) {
		return org.python.core.__builtin__.eval( ((Code)code).getPyCode(), m_dict, m_dict );
	}
	public void exec( edu.cmu.cs.stage3.alice.scripting.Code code ) {
		org.python.core.Py.exec( ((Code)code).getPyCode(), m_dict, m_dict );
	}
}
