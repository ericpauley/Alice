package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.property.CollectionProperty;

public class CollisionBehavior extends TriggerBehavior {
	private static Class[] s_supportedCoercionClasses = { IsCollidingBehavior.class };
	
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	public final CollectionProperty a = new CollectionProperty( this, "a", null );
	public final CollectionProperty b = new CollectionProperty( this, "b", null );

	
	public void manufactureDetails() {
		super.manufactureDetails();
	}
	private void updateDetails( java.awt.event.MouseEvent mouseEvent ) {
		for( int i=0; i<details.size(); i++ ) {
			//Expression detail = (Expression)details.get( i );
			//if( detail.name.getStringValue().equals( "x" ) ) {
			//	((Variable)detail).value.set( new Double( mouseEvent.getX() ) );
			//}
		}
	}
}