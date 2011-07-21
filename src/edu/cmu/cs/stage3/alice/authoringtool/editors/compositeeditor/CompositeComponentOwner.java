// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 8/5/2003 8:40:56 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CompositeComponentOwner.java

package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.core.Element;
import java.awt.Component;
import java.awt.Container;

public interface CompositeComponentOwner
{

    public abstract Element getElement();

    public abstract void setEnabled(boolean flag);

    public abstract Container getParent();

    public abstract boolean isExpanded();

    public abstract Component getGrip();
}