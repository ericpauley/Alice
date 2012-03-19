package edu.cmu.cs.stage3.alice.core.response.visualization.list;

public class InsertItemAtBeginning extends ListVisualizationWithItemAnimation {
	public class RuntimeInsertItemAtBeginning extends RuntimeListVisualizationWithItemAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.addValue(0, getItem());
		}
	}
}
