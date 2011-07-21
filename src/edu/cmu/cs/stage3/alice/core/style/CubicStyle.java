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

package edu.cmu.cs.stage3.alice.core.style;

import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;

public class CubicStyle extends edu.cmu.cs.stage3.alice.core.Element implements edu.cmu.cs.stage3.alice.core.Style {
	public final ObjectProperty controls = new ObjectProperty( this, "controls", null, double[].class );
	public double getPortion( double current, double total ) {
		double[] controlsValue = (double[])controls.getValue();
		if( controlsValue!=null ) {
			edu.cmu.cs.stage3.math.Cubic cubic = new edu.cmu.cs.stage3.math.BezierCubic( controlsValue[0], controlsValue[1], controlsValue[2], controlsValue[3] );
			double t = current/total;
			return cubic.evaluate( t );
		} else {
			return 0;
		}
	}
}