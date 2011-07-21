package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

/**
 * <p>Title: StencilStackChangeListener </p>
 * <p>Description: This interface is for objects that need to be aware of
 * stencil stack changes, such as the NavigationBar</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

public interface StencilStackChangeListener extends EventListener {
  public void numberOfStencilsChanged(int newNumberOfStencils);
  public void currentStencilChanged(int selectedStencil);
}