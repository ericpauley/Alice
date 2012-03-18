package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;

public class ItemOfCollectionProperty extends ObjectProperty {
	private Collection m_collection = null;
	public ItemOfCollectionProperty( Element owner, String name ) {
		super( owner, name, null, Object.class );
	}
	public void setCollection( Collection collection ) {
		m_collection = collection;
	}
	
	public Class getValueClass() {
		if( m_collection!=null ) {
			return m_collection.valueClass.getClassValue();
		} else {
			return super.getValueClass();
		}
	}
}
