package edu.cmu.cs.stage3.alice.core.response.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class RemoveItemFromIndex extends ListVisualizationAnimation {
	public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
	public class RuntimeRemoveItemFromIndex extends RuntimeListVisualizationAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.remove(index.intValue());
		}
	}
}
