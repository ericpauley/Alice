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

import edu.cmu.cs.stage3.alice.core.property.EulerAnglesProperty;

public class EulerAnglesAnimation extends OrientationAnimation {
	public final EulerAnglesProperty eulerAngles = new EulerAnglesProperty( this, "eulerAngles", new edu.cmu.cs.stage3.math.EulerAngles( 0, 0, 0 ) );
	public class RuntimeEulerAnglesAnimation extends RuntimeOrientationAnimation {
		private edu.cmu.cs.stage3.math.EulerAngles m_eulerAngles;
		
		public void prologue( double t ) {
			super.prologue( t );
			m_eulerAngles = edu.cmu.cs.stage3.math.EulerAngles.revolutionsToRadians( eulerAngles.getEulerAnglesValue() );
		}
		
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return m_eulerAngles.getQuaternion();
		}
	}
}
