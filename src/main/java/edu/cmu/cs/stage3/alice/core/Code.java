package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public abstract class Code extends Element {
	public final BooleanProperty isCommentedOut = new BooleanProperty( this, "isCommentedOut", Boolean.FALSE );
}
