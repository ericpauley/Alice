package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

/**
 * <p>Title: ScreenObjectPositionListener </p>
 * <p>Description: Provided to make it easy for screen objects to listen,
 * and react to, changes in position of other screen objects. This makes it
 * easy for notes to stay with their hole or frame, etc. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

 import java.awt.Rectangle;

public interface StencilObjectPositionListener extends EventListener {
  public void stencilObjectMoved(Rectangle newPos);
}