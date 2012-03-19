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
public class BezierSpline extends Spline {
	public boolean addKey(BezierKey key) {
		return super.addKey(key);
	}

	public boolean removeKey(BezierKey key) {
		return super.removeKey(key);
	}

	@Override
	public Object getSample(double t) {
		if (t <= 0.0) {
			Key key = getFirstKey();
			if (key != null) {
				return key.createSample(key.getValueComponents());
			}
		} else if (t >= getDuration()) {
			Key key = getLastKey();
			if (key != null) {
				return key.createSample(key.getValueComponents());
			}
		} else {
			Key[] boundingKeys = getBoundingKeys(t);
			if (boundingKeys != null) {
				double timeSpan = boundingKeys[1].getTime() - boundingKeys[0].getTime();
				double portion = (t - boundingKeys[0].getTime()) / timeSpan;
				int numComponents = boundingKeys[0].getValueComponents().length;

				edu.cmu.cs.stage3.math.BezierCubic[] curves = new edu.cmu.cs.stage3.math.BezierCubic[numComponents];
				for (int j = 0; j < numComponents; j++) {
					double p0 = ((BezierKey) boundingKeys[0]).getValueComponents()[j];
					double p1 = ((BezierKey) boundingKeys[0]).getOutgoingControlComponents()[j];
					double p2 = ((BezierKey) boundingKeys[1]).getIncomingControlComponents()[j];
					double p3 = ((BezierKey) boundingKeys[1]).getValueComponents()[j];
					curves[j] = new edu.cmu.cs.stage3.math.BezierCubic(p0, p1, p2, p3);
				}

				double[] components = new double[numComponents];
				for (int j = 0; j < numComponents; j++) {
					components[j] = curves[j].evaluate(portion);
				}

				return boundingKeys[0].createSample(components);
			}
		}
		return null;
	}

	// MAX has a really screwy way of storing the tangent data for its bezier
	// splines.
	// when I get some more time I'll try to document this more thoroughly.
	public void convertMAXTangentsToBezierTangents(double timeFactor) {
		BezierKey[] keys = (BezierKey[]) getKeyArray(new BezierKey[0]);
		for (int i = 0; i < keys.length; i++) {
			BezierKey prevKey = keys[Math.max(i - 1, 0)];
			BezierKey thisKey = keys[i];
			BezierKey nextKey = keys[Math.min(i + 1, keys.length - 1)];

			double[] in = thisKey.getIncomingControlComponents();
			double[] out = thisKey.getOutgoingControlComponents();

			double dt;
			for (int j = 0; j < thisKey.getValueComponents().length; j++) {
				double value = thisKey.getValueComponents()[j];
				dt = (thisKey.getTime() - prevKey.getTime()) / timeFactor / 3.0;
				in[j] = value + in[j] * dt;
				dt = (nextKey.getTime() - thisKey.getTime()) / timeFactor / 3.0;
				out[j] = value + out[j] * dt;
			}
		}
	}

	// have to overload this to take care of the tangents...

	@Override
	public void scaleKeyValueComponents(double scaleFactor) {
		super.scaleKeyValueComponents(scaleFactor);

		BezierKey[] keys = (BezierKey[]) getKeyArray(new BezierKey[0]);
		for (BezierKey key : keys) {
			double[] incomingControlComponents = key.getIncomingControlComponents();
			for (int j = 0; j < incomingControlComponents.length; j++) {
				incomingControlComponents[j] *= scaleFactor;
			}
			double[] outgoingControlComponents = key.getOutgoingControlComponents();
			for (int j = 0; j < outgoingControlComponents.length; j++) {
				outgoingControlComponents[j] *= scaleFactor;
			}
		}
	}
}
