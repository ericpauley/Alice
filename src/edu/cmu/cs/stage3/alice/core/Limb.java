/*
 * Created on Jul 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Enumerable;

/**
 * @author caitlink
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Limb extends Enumerable {
	public static final Limb rightArm = new Limb("rightUpperArm");
	public static final Limb leftArm = new Limb("leftUpperArm");
	public static final Limb rightLeg = new Limb("rightUpperLeg");
	public static final Limb leftLeg = new Limb("leftUpperLeg");
	
	protected String limbName = "rightUpperArm";
	
	public Limb(String limbName) {
		this.limbName = limbName;
	}
	
	public static Limb valueOf( String s ) {
		return (Limb)edu.cmu.cs.stage3.util.Enumerable.valueOf( s, Limb.class );
	}
}
