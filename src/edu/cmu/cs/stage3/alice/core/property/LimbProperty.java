/*
 * Created on Jul 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * @author caitlink
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LimbProperty extends EnumerableProperty {
	public LimbProperty( Element owner, String name, edu.cmu.cs.stage3.alice.core.Limb defaultValue ) {
		super( owner, name, defaultValue, edu.cmu.cs.stage3.alice.core.Limb.class );
	}
	
	public edu.cmu.cs.stage3.alice.core.Limb getLimbValue() {
		return (edu.cmu.cs.stage3.alice.core.Limb)getEnumerableValue();
	}
}
