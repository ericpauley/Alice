package edu.cmu.cs.stage3.alice.core.response.visualization.queue;

public class Dequeue extends QueueVisualizationAnimation {
	public class RuntimeDequeue extends RuntimeQueueVisualizationAnimation {

		@Override
		public void epilogue(double t) {
			super.epilogue(t);
			getCollection().values.remove(0);
		}
	}
}
