/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.caitlin.stencilhelp.application;

import java.awt.Dimension;

/**
 * Title:        Show Me
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Caitlin Kelleher
 * @version 1.0
 */

public interface StencilApplication {

    public void setGlassPane( java.awt.Component c );
    public void setVisible( boolean visible ); // setStencilVisible

    public String getIDForPoint( java.awt.Point p, boolean dropSite );
    public java.awt.Rectangle getBoxForID( String ID ) throws IDDoesNotExistException;

    public boolean isIDVisible( String ID ) throws IDDoesNotExistException;
    public void makeIDVisible( String ID ) throws IDDoesNotExistException;

    public void makeWayPoint();
    public void goToPreviousWayPoint();
    public void clearWayPoints();

    public StateCapsule getCurrentState();
    public StateCapsule getStateCapsuleFromString(String capsuleString);
    public boolean doesStateMatch(StateCapsule stateCapsule);

    public void performTask(String taskString);

    public void handleMouseEvent( java.awt.event.MouseEvent e );
    public void deFocus();

    public Dimension getScreenSize();

}