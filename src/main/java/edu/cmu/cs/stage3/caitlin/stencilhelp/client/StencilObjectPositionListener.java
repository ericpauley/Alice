package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Rectangle;
import java.util.EventListener;

public interface StencilObjectPositionListener extends EventListener {
	public void stencilObjectMoved(Rectangle newPos);
}