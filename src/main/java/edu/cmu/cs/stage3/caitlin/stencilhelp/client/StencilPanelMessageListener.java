package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

/**
 * <p>Title: StencilPanelMessageListener</p>
 * <p>Description: The stencil panel needs to be able to communicate with other
 * objects, at the moment the menu, to trigger actions. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

public interface StencilPanelMessageListener extends EventListener {
  public static final int SHOW_MENU = 1;

  public void messageReceived(int messageID, Object data);
}