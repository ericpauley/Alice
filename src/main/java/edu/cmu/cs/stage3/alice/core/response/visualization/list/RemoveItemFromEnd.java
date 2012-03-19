package edu.cmu.cs.stage3.alice.core.response.visualization.list;

public class RemoveItemFromEnd extends ListVisualizationAnimation {
	public class RuntimeRemoveItemFromEnd extends RuntimeListVisualizationAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.remove(getCollection().values.size() - 1);
		}
	}
}
