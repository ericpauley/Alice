package edu.cmu.cs.stage3.media.event;

public class PlayerEvent extends java.util.EventObject {
	public PlayerEvent( edu.cmu.cs.stage3.media.Player source ) {
		super( source );
	}
	public edu.cmu.cs.stage3.media.Player getPlayer() {
		return (edu.cmu.cs.stage3.media.Player)getSource();
	}
}
