package edu.cmu.cs.stage3.alice.core.response.visualization.queue;

public class Enqueue extends QueueVisualizationWithItemAnimation {
	public class RuntimeEnqueue extends RuntimeQueueVisualizationWithItemAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.addValue(getItem());
		}
	}
}
