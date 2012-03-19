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

package edu.cmu.cs.stage3.alice.core;

public class SpatialRelation extends edu.cmu.cs.stage3.util.Enumerable {
	private javax.vecmath.Vector3d m_placeAxis;
	// public static final SpatialRelation IN = new SpatialRelation();
	// public static final SpatialRelation ON = new SpatialRelation();
	// public static final SpatialRelation AT = new SpatialRelation();

	private SpatialRelation(javax.vecmath.Vector3d placeAxis) {
		m_placeAxis = placeAxis;
	}

	public static final SpatialRelation LEFT_OF = new SpatialRelation(new javax.vecmath.Vector3d(-1, 0, 0));
	public static final SpatialRelation RIGHT_OF = new SpatialRelation(new javax.vecmath.Vector3d(1, 0, 0));
	public static final SpatialRelation ABOVE = new SpatialRelation(new javax.vecmath.Vector3d(0, 1, 0));
	public static final SpatialRelation BELOW = new SpatialRelation(new javax.vecmath.Vector3d(0, -1, 0));
	public static final SpatialRelation IN_FRONT_OF = new SpatialRelation(new javax.vecmath.Vector3d(0, 0, 1));
	public static final SpatialRelation BEHIND = new SpatialRelation(new javax.vecmath.Vector3d(0, 0, -1));

	public static final SpatialRelation FRONT_RIGHT_OF = new SpatialRelation(new javax.vecmath.Vector3d(0.7071068, 0, 0.7071068));
	public static final SpatialRelation FRONT_LEFT_OF = new SpatialRelation(new javax.vecmath.Vector3d(-0.7071068, 0, 0.7071068));
	public static final SpatialRelation BEHIND_RIGHT_OF = new SpatialRelation(new javax.vecmath.Vector3d(0.7071068, 0, -0.7071068));
	public static final SpatialRelation BEHIND_LEFT_OF = new SpatialRelation(new javax.vecmath.Vector3d(-0.7071068, 0, -0.7071068));

	public javax.vecmath.Vector3d getPlaceVector(double amount, edu.cmu.cs.stage3.math.Box subjectBoundingBox, edu.cmu.cs.stage3.math.Box objectBoundingBox) {
		double x = amount * m_placeAxis.x;
		double y = amount * m_placeAxis.y;
		double z = amount * m_placeAxis.z;

		if (m_placeAxis.x > 0) {
			x += m_placeAxis.x * (objectBoundingBox.getMaximum().x - subjectBoundingBox.getMinimum().x);
		} else if (m_placeAxis.x < 0) {
			x += m_placeAxis.x * (subjectBoundingBox.getMaximum().x - objectBoundingBox.getMinimum().x);
		}
		if (m_placeAxis.y > 0) {
			y += m_placeAxis.y * (objectBoundingBox.getMaximum().y - subjectBoundingBox.getMinimum().y);
		} else if (m_placeAxis.y < 0) {
			y += m_placeAxis.y * (subjectBoundingBox.getMaximum().y - objectBoundingBox.getMinimum().y);
		}
		if (m_placeAxis.z > 0) {
			z += m_placeAxis.z * (objectBoundingBox.getMaximum().z - subjectBoundingBox.getMinimum().z);
		} else if (m_placeAxis.z < 0) {
			z += m_placeAxis.z * (subjectBoundingBox.getMaximum().z - objectBoundingBox.getMinimum().z);
		}
		return new javax.vecmath.Vector3d(x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o != null && o instanceof SpatialRelation) {
			SpatialRelation spatialRelation = (SpatialRelation) o;
			if (m_placeAxis == null) {
				return spatialRelation.m_placeAxis == null;
			} else {
				return m_placeAxis.equals(spatialRelation.m_placeAxis);
			}
		} else {
			return false;
		}
	}
	public static SpatialRelation valueOf(String s) {
		return (SpatialRelation) edu.cmu.cs.stage3.util.Enumerable.valueOf(s, SpatialRelation.class);
	}
}
