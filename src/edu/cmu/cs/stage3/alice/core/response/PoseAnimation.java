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

/**
 * @author Jason Pratt
 */
public class PoseAnimation extends Animation {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty subject = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty( this, "subject", null );
	public final PoseProperty pose = new PoseProperty( this, "pose", null );

	public PoseAnimation() {
//		subject.setIsAcceptingOfNull( true );
//		pose.setIsAcceptingOfNull( true );
	}

	public class PoseProperty extends edu.cmu.cs.stage3.alice.core.property.ElementProperty {
		public PoseProperty( edu.cmu.cs.stage3.alice.core.Element owner, String name, edu.cmu.cs.stage3.alice.core.Pose defaultValue ) {
			super( owner, name, defaultValue, edu.cmu.cs.stage3.alice.core.Pose.class );
		}
		public edu.cmu.cs.stage3.alice.core.Pose getPoseValue() {
			return (edu.cmu.cs.stage3.alice.core.Pose)getElementValue();
		}
//		public edu.cmu.cs.stage3.alice.core.Pose getPoseValue( RuntimePoseAnimation runtimePoseAnimation ) {
//			return (edu.cmu.cs.stage3.alice.core.Pose)super.getElementValue( runtimePoseAnimation );
//		}
	}

	public class RuntimePoseAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Transformable subject;
		protected edu.cmu.cs.stage3.alice.core.Pose pose;
		protected java.util.Dictionary poseStringMap;
		protected java.util.Vector transformableKeys = new java.util.Vector();
		protected java.util.Dictionary poseTransformableMap = new java.util.Hashtable();
		protected java.util.Dictionary sourcePositionMap = new java.util.Hashtable();
		protected java.util.Dictionary targetPositionMap = new java.util.Hashtable();
		protected java.util.Dictionary sourceQuaternionMap = new java.util.Hashtable();
		protected java.util.Dictionary targetQuaternionMap = new java.util.Hashtable();
		protected java.util.Dictionary sourceScaleMap = new java.util.Hashtable();
		protected java.util.Dictionary targetScaleMap = new java.util.Hashtable();

		
		public void prologue( double t ) {
			super.prologue( t );
			subject = PoseAnimation.this.subject.getTransformableValue();
			pose = PoseAnimation.this.pose.getPoseValue();
			java.util.Dictionary poseStringMap = pose.poseMap.getDictionaryValue();
			for( java.util.Enumeration enum0 = poseStringMap.keys(); enum0.hasMoreElements(); ) {
				String stringKey = (String)enum0.nextElement();
				edu.cmu.cs.stage3.alice.core.Transformable key = (edu.cmu.cs.stage3.alice.core.Transformable)subject.getDescendantKeyed( stringKey );
				if( key != null ) {
					transformableKeys.add( key );
					edu.cmu.cs.stage3.math.Matrix44 m = (edu.cmu.cs.stage3.math.Matrix44)poseStringMap.get( stringKey );
					sourcePositionMap.put( key, key.getPosition() );
					sourceQuaternionMap.put( key, key.getOrientationAsQuaternion() );
					targetPositionMap.put( key, m.getPosition() );
					targetQuaternionMap.put( key, m.getAxes().getQuaternion() );
				} else {
					System.err.println( "Can't find " + stringKey + " in " + subject );
				}
			}
		}

		
		public void update( double t ) {
			super.update( t );
			double portion = getPortion( t );
			for( java.util.Enumeration enum0 = transformableKeys.elements(); enum0.hasMoreElements(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable key = (edu.cmu.cs.stage3.alice.core.Transformable)enum0.nextElement();

				edu.cmu.cs.stage3.math.Vector3 sourcePosition = (edu.cmu.cs.stage3.math.Vector3)sourcePositionMap.get( key );
				edu.cmu.cs.stage3.math.Vector3 targetPosition = (edu.cmu.cs.stage3.math.Vector3)targetPositionMap.get( key );
				edu.cmu.cs.stage3.math.Quaternion sourceQuaternion = (edu.cmu.cs.stage3.math.Quaternion)sourceQuaternionMap.get( key );
				edu.cmu.cs.stage3.math.Quaternion targetQuaternion = (edu.cmu.cs.stage3.math.Quaternion)targetQuaternionMap.get( key );

				edu.cmu.cs.stage3.math.Vector3 currentPosition = edu.cmu.cs.stage3.math.Vector3.interpolate( sourcePosition, targetPosition, portion  );
				edu.cmu.cs.stage3.math.Quaternion currentQuaternion = edu.cmu.cs.stage3.math.Quaternion.interpolate( sourceQuaternion, targetQuaternion, portion );

				key.setPositionRightNow( currentPosition );
				key.setOrientationRightNow( currentQuaternion );
			}
		}

		/*
		private edu.cmu.cs.stage3.alice.core.Transformable getDescendantFromKey( edu.cmu.cs.stage3.alice.core.Transformable subject, String relativeKey ) {
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( relativeKey, ".", false );
			while( tokenizer.hasMoreTokens() ) {
				subject = (edu.cmu.cs.stage3.alice.core.Transformable)subject.getChildNamed( tokenizer.nextToken() );
			}
			return subject;
		}
		*/
	}
}