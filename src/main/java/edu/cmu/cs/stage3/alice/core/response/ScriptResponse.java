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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;

public class ScriptResponse extends edu.cmu.cs.stage3.alice.core.Response {
	// todo: should default value be null or ""?
	public final ScriptProperty script = new ScriptProperty(this, "script", "");

	@Override
	protected Number getDefaultDuration() {
		return new Double(0);
	}
	public class RuntimeScriptResponse extends RuntimeResponse {

		@Override
		public void update(double t) {
			super.update(t);
			exec(script.getCode(edu.cmu.cs.stage3.alice.scripting.CompileType.EXEC_MULTIPLE));
		}
	}
}