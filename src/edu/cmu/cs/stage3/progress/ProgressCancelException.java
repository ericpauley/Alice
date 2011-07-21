package edu.cmu.cs.stage3.progress;

public class ProgressCancelException extends Exception {
	public ProgressCancelException() {
	}
	public ProgressCancelException( String detail ) {
		super( detail );
	}
}