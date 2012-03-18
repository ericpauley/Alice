package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;

public class CollectionProperty extends ElementProperty {
	protected CollectionProperty( Element owner, String name, Collection defaultValue, Class cls ) {
		super( owner, name, defaultValue, cls );
	}
	public CollectionProperty( Element owner, String name, Collection defaultValue ) {
		super( owner, name, defaultValue, Collection.class );
	}
	public Collection getCollectionValue() {
		return (Collection)getElementValue();
	}
}
