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

import java.lang.reflect.Method;

/**
 * @author Jason Pratt
 */
public abstract class Spline {
	private java.util.TreeSet keys;
	private java.util.Comparator keyComparator = new java.util.Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof Key && o2 instanceof Key) {
				Key key1 = (Key) o1;
				Key key2 = (Key) o2;
				if (key1.getTime() < key2.getTime()) {
					return -1;
				} else if (key1.getTime() > key2.getTime()) {
					return 1;
				} else {
					return 0;
				}
			} else {
				throw new ClassCastException(Key.class.getName() + " required.");
			}
		}
	};
	private Key recentKey;

	protected Spline() {
		keys = new java.util.TreeSet(keyComparator);
	}

	protected boolean addKey(Key key) {
		return keys.add(key);
	}

	protected boolean removeKey(Key key) {
		return keys.remove(key);
	}

	public void clearKeys() {
		keys.clear();
	}

	public Key[] getKeyArray(Key[] keyArray) {
		return (Key[]) keys.toArray(keyArray);
	}

	private Key[] boundingKeys = new Key[2];
	public Key[] getBoundingKeys(double time) {
		Key prevKey = null;
		Key nextKey = null;

		if (keys.size() == 1) {
			prevKey = nextKey = (Key) keys.first();
			boundingKeys[0] = prevKey;
			boundingKeys[1] = nextKey;
			return boundingKeys;
		}

		// first try our cached position
		if (recentKey != null) {
			java.util.Iterator iter = keys.tailSet(recentKey).iterator();
			if (iter.hasNext()) {
				nextKey = (Key) iter.next();
				while (iter.hasNext()) {
					prevKey = nextKey;
					nextKey = (Key) iter.next();
					if (time >= prevKey.getTime() && time < nextKey.getTime()) {
						recentKey = prevKey;
						boundingKeys[0] = prevKey;
						boundingKeys[1] = nextKey;
						return boundingKeys;
					}
				}
			}
		}

		// next, try from the beginning
		java.util.Iterator iter = keys.iterator();
		if (iter.hasNext()) {
			nextKey = (Key) iter.next();
			while (iter.hasNext()) {
				prevKey = nextKey;
				nextKey = (Key) iter.next();
				if (time >= prevKey.getTime() && time < nextKey.getTime()) {
					recentKey = prevKey;
					boundingKeys[0] = prevKey;
					boundingKeys[1] = nextKey;
					return boundingKeys;
				}
			}
		}

		return null;
	}

	public Key getFirstKey() {
		return (Key) keys.first();
	}

	public Key getLastKey() {
		return (Key) keys.last();
	}

	public double getDuration() {
		return getLastTime();
	}

	public double getFirstTime() {
		if (!keys.isEmpty()) {
			return getFirstKey().getTime();
		} else {
			return 0.0;
		}
	}

	public double getLastTime() {
		if (!keys.isEmpty()) {
			return getLastKey().getTime();
		} else {
			return 0.0;
		}
	}

	public void scaleKeyValueComponents(double scaleFactor) {
		for (java.util.Iterator iter = keys.iterator(); iter.hasNext();) {
			Key key = (Key) iter.next();
			double[] valueComponents = key.getValueComponents();
			for (int i = 0; i < valueComponents.length; i++) {
				valueComponents[i] *= scaleFactor;
			}
		}
	}

	public abstract Object getSample(double t);

	// the main purpose of this toString method is to produce a reasonable
	// representation to be used for loading/storing.
	// eventually, I'd like to make spline loading/storing use separate files
	// (like scripts, geometry, textures, etc),
	// at which point this method will revert to its normal use.

	@Override
	public String toString() {
		StringBuffer repr = new StringBuffer();

		repr.append("{spline}");
		repr.append("{splineType}");
		repr.append(this.getClass().getName());
		repr.append("{/splineType}");

		repr.append("{keys}");
		for (java.util.Iterator iter = keys.iterator(); iter.hasNext();) {
			Key key = (Key) iter.next();
			repr.append("{key}");
			repr.append("{type}");
			repr.append(key.getClass().getName());
			repr.append("{/type}");
			repr.append("{data}");
			repr.append(key.toString());
			repr.append("{/data}");
			repr.append("{/key}");
		}
		repr.append("{/keys}");

		repr.append("{/spline}");

		return repr.toString();
	}

	public static Spline valueOf(String s) {
		s = s.replace('{', '<');
		s = s.replace('}', '>');

		edu.cmu.cs.stage3.io.TokenBlock splineBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(0, s);
		edu.cmu.cs.stage3.io.TokenBlock splineTypeBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(0, splineBlock.tokenContents);
		edu.cmu.cs.stage3.io.TokenBlock keysBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(splineTypeBlock.tokenEndIndex, splineBlock.tokenContents);

		Spline spline = null;
		java.lang.reflect.Method addKeyMethod = null;
		try {
			Class splineClass = Class.forName(splineTypeBlock.tokenContents);
			spline = (Spline) splineClass.newInstance();
			addKeyMethod = null;
			java.lang.reflect.Method[] methods = splineClass.getMethods();
			for (Method method : methods) {
				if (method.getName().equals("addKey")) {
					addKeyMethod = method;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}

		if (addKeyMethod == null) {
			System.err.println("Unable to find addKey method for " + spline);
			return null;
		}

		int beginIndex = 0;
		edu.cmu.cs.stage3.io.TokenBlock keyBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(beginIndex, keysBlock.tokenContents);
		while (keyBlock.tokenContents != null) {
			edu.cmu.cs.stage3.io.TokenBlock typeBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(0, keyBlock.tokenContents);
			edu.cmu.cs.stage3.io.TokenBlock dataBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(typeBlock.tokenEndIndex, keyBlock.tokenContents);

			try {
				Class keyClass = Class.forName(typeBlock.tokenContents);
				java.lang.reflect.Method valueOfMethod = keyClass.getMethod("valueOf", new Class[]{String.class});
				Object key = valueOfMethod.invoke(null, new Object[]{dataBlock.tokenContents});
				addKeyMethod.invoke(spline, new Object[]{key});
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.reflect.InvocationTargetException e) {
				e.printStackTrace();
			}

			beginIndex = keyBlock.tokenEndIndex;
			keyBlock = edu.cmu.cs.stage3.io.TokenBlock.getTokenBlock(beginIndex, keysBlock.tokenContents);
		}

		return spline;
	}
}