package edu.cmu.cs.stage3.progress;

public interface ProgressObserver {
	public static final int UNKNOWN_TOTAL = -1;

	public void progressBegin( int total );
	public void progressUpdateTotal( int total );
	public void progressUpdate( int current, String description ) throws ProgressCancelException;
	public void progressEnd();
}