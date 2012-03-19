package edu.cmu.cs.stage3.alice.core.response.visualization.set;

public class Remove extends SetVisualizationWithItemAnimation {
	public class RuntimeRemove extends RuntimeSetVisualizationWithItemAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.remove(getItem());
		}
	}
}
