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
public class CatmullRomSpline extends Spline {
	protected SimpleKey[] keys;
	protected edu.cmu.cs.stage3.math.CatmullRomCubic[][] curves;
	protected java.util.HashMap curveMap = new java.util.HashMap();
	protected int numComponents;

	public boolean addKey(SimpleKey key) {
		boolean result = super.addKey(key);
		updateKeys();
		return result;
	}

	public boolean removeKey(SimpleKey key) {
		boolean result = super.removeKey(key);
		updateKeys();
		return result;
	}

	public void updateKeys() {
		keys = (SimpleKey[]) getKeyArray(new SimpleKey[0]);
		curveMap.clear();

		if (keys != null) {
			numComponents = keys[0].getValueComponents().length;
			curves = new edu.cmu.cs.stage3.math.CatmullRomCubic[keys.length - 1][numComponents];

			for (int i = 0; i < curves.length; i++) {
				SimpleKey keyLast = keys[Math.max(i - 1, 0)];
				SimpleKey keyThis = keys[i];
				SimpleKey keyNext = keys[i + 1];
				SimpleKey keyNextNext = keys[Math.min(i + 2, keys.length - 1)];
				curveMap.put(keyThis, new Integer(i));
				for (int j = 0; j < numComponents; j++) {
					double pLast = keyLast.getValueComponents()[j];
					double pThis = keyThis.getValueComponents()[j];
					double pNext = keyNext.getValueComponents()[j];
					double pNextNext = keyNextNext.getValueComponents()[j];
					curves[i][j] = new edu.cmu.cs.stage3.math.CatmullRomCubic(pLast, pThis, pNext, pNextNext);
				}
			}
		} else {
			curves = null;
		}
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

				Object o = curveMap.get(boundingKeys[0]);
				if (o instanceof Integer) {
					int i = ((Integer) o).intValue();

					double[] components = new double[numComponents];
					for (int j = 0; j < numComponents; j++) {
						components[j] = curves[i][j].evaluate(portion);
					}

					return boundingKeys[0].createSample(components);
				}
			}
		}
		return null;
	}
}
