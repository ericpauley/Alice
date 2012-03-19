package edu.cmu.cs.stage3.alice.core.response.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class InsertItemAtIndex extends ListVisualizationWithItemAnimation {
	public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
	public class RuntimeInsertItemAtIndex extends RuntimeListVisualizationWithItemAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.addValue(index.intValue(), getItem());
		}
	}
}
