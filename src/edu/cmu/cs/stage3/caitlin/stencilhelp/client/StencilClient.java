package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * Title:        Show Me
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Caitlin Kelleher
 * @version 1.0
 */

 import java.awt.Component;

public interface StencilClient {
    public boolean isDropAccessible( java.awt.Point p );
    public void update();
    public void stateChanged();
    public Component getStencilComponent();
    public void showStencils( boolean show );
    public boolean getIsShowing();
    public void loadStencilTutorial( java.io.File tutorialFile );
}