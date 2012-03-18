package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

public interface StencilFocusListener extends EventListener {

  public void focusGained();
  public void focusLost();
}