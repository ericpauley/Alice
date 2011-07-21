package edu.cmu.cs.stage3.media.event;

public interface PlayerListener {
	public void stateChanged( edu.cmu.cs.stage3.media.event.PlayerEvent e );
	public void endReached( edu.cmu.cs.stage3.media.event.PlayerEvent e );
}