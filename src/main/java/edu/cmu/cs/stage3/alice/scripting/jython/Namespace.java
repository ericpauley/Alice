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

public class Namespace extends org.python.core.PyStringMap {
	private java.util.Hashtable m_map = new java.util.Hashtable();
	private edu.cmu.cs.stage3.alice.core.World m_world = null;
	private PyElement m_pyWorld = null;

	public void setWorld(edu.cmu.cs.stage3.alice.core.World world) {
		m_world = world;
		m_pyWorld = getPyElement(m_world);
		m_map.clear();
		clear();
	}
	/* package protected */PyElement getPyElement(edu.cmu.cs.stage3.alice.core.Element element) {
		PyElement pyElement = (PyElement) m_map.get(element);
		if (pyElement == null) {
			if (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				pyElement = new PySandbox((edu.cmu.cs.stage3.alice.core.Sandbox) element, this);
			} else {
				pyElement = new PyElement(element, this);
			}
			m_map.put(element, pyElement);
		}
		return pyElement;
	}
	/* package protected */org.python.core.PyObject java2py(Object o) {
		if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
			return getPyElement((edu.cmu.cs.stage3.alice.core.Element) o);
		} else {
			return org.python.core.Py.java2py(o);
		}
	}

	@Override
	public synchronized org.python.core.PyObject __finditem__(String key) {
		org.python.core.PyObject py = super.__finditem__(key);
		if (py != null) {
			return py;
		} else {
			if (key.equalsIgnoreCase(m_world.name.getStringValue())) {
				return m_pyWorld;
			}
			edu.cmu.cs.stage3.alice.core.Expression expression = m_world.lookup(key);
			if (expression != null) {
				return java2py(expression.getValue());
			} else {
				return m_pyWorld.__findattr__(key);
			}
		}
	}
}
