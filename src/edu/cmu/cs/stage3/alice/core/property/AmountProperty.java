/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.property;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Amount;

public class AmountProperty extends EnumerableProperty {
	public AmountProperty( Element owner, String name, Amount defaultValue ) {
		super( owner, name, defaultValue, Amount.class );
	}
	public Amount getAmountValue() {
		return (Amount)getEnumerableValue();
	}
}
