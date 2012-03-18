package edu.cmu.cs.stage3.alice.core.summary;

public class WorldSummary extends ElementSummary {
	private edu.cmu.cs.stage3.alice.core.World getWorld() {
		return (edu.cmu.cs.stage3.alice.core.World)getElement();
	}
	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		super.setElement( world );
	}
}
