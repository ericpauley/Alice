package edu.cmu.cs.stage3.scheduler;

public interface Scheduler extends Runnable {
	public void addEachFrameRunnable( Runnable runnable );
	public void markEachFrameRunnableForRemoval( Runnable runnable );
}
