package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventListener;

public interface MouseEventListener extends EventListener {
	public boolean contains(Point point);

	public boolean mousePressed(MouseEvent e);
	public boolean mouseReleased(MouseEvent e);
	public boolean mouseClicked(MouseEvent e);
	public boolean mouseEntered(MouseEvent e);
	public boolean mouseExited(MouseEvent e);

	// Mouse Motion Events
	public boolean mouseMoved(MouseEvent e);
	public boolean mouseDragged(MouseEvent e);
}