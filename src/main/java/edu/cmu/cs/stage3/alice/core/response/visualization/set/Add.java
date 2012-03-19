package edu.cmu.cs.stage3.alice.core.response.visualization.set;

public class Add extends SetVisualizationWithItemAnimation {
	public class RuntimeAdd extends RuntimeSetVisualizationWithItemAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			Object item = getItem();
			if (getCollection().values.contains(item)) {
				// pass
			} else {
				getCollection().values.add(item);
			}
		}
	}
}
