package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * <p>Title: Caitlin Kelleher </p>
 * <p>Description: Interface for passing keystrokes to stencil objects. The
 * boolean is used to indicate whether or not a redraw is necessary. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

 import java.util.EventListener;
 import java.awt.event.KeyEvent;

public interface KeyEventListener extends EventListener {
  public boolean keyTyped(KeyEvent e);
  public boolean keyPressed(KeyEvent e);
  public boolean keyReleased(KeyEvent e);
}