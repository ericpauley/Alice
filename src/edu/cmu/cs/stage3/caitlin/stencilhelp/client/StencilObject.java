package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * <p>Title: ScreenObject</p>
 * <p>Description: Interface that screenobjects need to implement; they
 * will also typically implement others to listen for mouse events or stencil
 * changes.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

import java.util.Vector;
import java.awt.Rectangle;

public interface StencilObject {
  public Vector getShapes();
  public Rectangle getRectangle();
  public Rectangle getPreviousRectangle();
  public boolean isModified();
  public boolean intersectsRectangle( Rectangle rect );
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener);
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener);
  public String getComponentID();
}