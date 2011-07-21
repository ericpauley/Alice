package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

/**
 * <p>Title: MouseEventListener</p>
 * <p>Description: The interface for passing mouse events to stencil objects
 * or the stencilPane. The boolean is used to indicate that the event requires
 * a redraw. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

import java.awt.event.MouseEvent;
import java.awt.Point;

public interface MouseEventListener extends EventListener {
  public boolean contains( Point point );

  public boolean mousePressed(MouseEvent e);
  public boolean mouseReleased(MouseEvent e);
  public boolean mouseClicked(MouseEvent e);
  public boolean mouseEntered(MouseEvent e);
  public boolean mouseExited(MouseEvent e);

  // Mouse Motion Events
  public boolean mouseMoved(MouseEvent e);
  public boolean mouseDragged(MouseEvent e);
}