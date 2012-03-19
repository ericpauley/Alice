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

package edu.cmu.cs.stage3.pratt.maxkeyframing;

/**
 * @author Jason Pratt
 */
public abstract class TCBKey extends Key {
	private double tension; // ranges from -1.0 to 1.0
	private double continuity; // ranges from -1.0 to 1.0
	private double bias; // ranges from -1.0 to 1.0

	protected TCBKey(double time, double[] components, double tension, double continuity, double bias) {
		setTime(time);
		setValueComponents(components);
		this.tension = tension;
		this.continuity = continuity;
		this.bias = bias;
	}

	public double getTension() {
		return tension;
	}

	public double getContinuity() {
		return continuity;
	}

	public double getBias() {
		return bias;
	}

	@Override
	public String toString() {
		String className = this.getClass().getName();
		double[] components = getValueComponents();
		int numComponents = components.length;

		StringBuffer repr = new StringBuffer();
		repr.append(className);
		repr.append("[");
		repr.append(getTime());
		repr.append(",");
		for (int i = 0; i < numComponents; i++) {
			repr.append(components[i]);
			repr.append(",");
		}
		repr.append(tension);
		repr.append(",");
		repr.append(continuity);
		repr.append(",");
		repr.append(bias);
		repr.append("]");

		return repr.toString();
	}
}