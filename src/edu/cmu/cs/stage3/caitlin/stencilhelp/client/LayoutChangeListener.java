package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

/**
 * <p>Title: LayoutChangeListener</p>
 * <p>Description: This is to notify stencil objects that something in the
 * underlying interface has changed and they all need to re-lay themselves
 * out.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

public interface LayoutChangeListener extends EventListener {
  public boolean layoutChanged();
}