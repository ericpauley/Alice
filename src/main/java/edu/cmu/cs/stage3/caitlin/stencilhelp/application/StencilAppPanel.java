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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager;

/**
 * Title: Show Me Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author Caitlin Kelleher
 * @version 1.0
 */

public class StencilAppPanel extends JPanel implements StencilApplication {

	Hashtable nameToComp = new Hashtable();
	Hashtable compToName = new Hashtable();

	StencilManager stencilManager = null;
	JPanel stencilComponent = null;
	JFrame frame = null;

	long lastEventTime = -1;

	public StencilAppPanel(JFrame frame) {
		this.frame = frame;
	}

	public void launchControl() {

		if (stencilManager == null) {
			stencilManager = new StencilManager(this);
		}
		setGlassPane(stencilManager.getStencilComponent());
	}

	@Override
	public void setGlassPane(java.awt.Component c) {

		stencilComponent = (JPanel) c;
		stencilComponent.setOpaque(false);

		frame.setGlassPane(c);

		stencilManager.showStencils(!stencilManager.getIsShowing());

		if (stencilManager.getIsShowing() == false) {
			frame.removeKeyListener(stencilManager);
		} else {
			frame.addKeyListener(stencilManager);
		}

		this.requestFocus();
	}

	@Override
	public String getIDForPoint(java.awt.Point p, boolean dropSite) {
		Component c = getComponentAtPoint(p);
		// System.out.println( "Deepest" + c );
		// System.out.println( "Parent" + c.getParent() );
		// System.out.println(c);
		if (c != null) {
			Object value = compToName.get(c);
			if (value != null) {
				return (String) value;
			} else {
				while (c != null) {
					c = c.getParent();
					if (c != null) {
						value = compToName.get(c);
					}
					if (value != null) {
						return (String) value;
					}
				}
			}
			return null;
		}
		return null;
	}
	@Override
	public java.awt.Rectangle getBoxForID(String ID) {
		Component c = (Component) nameToComp.get(ID);
		if (c != null) {
			Point corner = c.getLocationOnScreen();
			javax.swing.SwingUtilities.convertPointFromScreen(corner, getRootPane());
			Rectangle rect = new Rectangle(corner, c.getSize());
			return rect;
		} else {
			return null;
		}
	}

	@Override
	public boolean isIDVisible(String ID) {
		return true;
	}
	@Override
	public void makeIDVisible(String ID) {
	}

	@Override
	public void makeWayPoint() {
	}
	@Override
	public void goToPreviousWayPoint() {
	}
	@Override
	public void clearWayPoints() {
	}

	@Override
	public boolean doesStateMatch(StateCapsule stateCapsule) {
		return true;
	}
	@Override
	public StateCapsule getCurrentState() {
		return null;
	}
	@Override
	public StateCapsule getStateCapsuleFromString(String capsuleString) {
		return null;
	}

	@Override
	public void performTask(String taskString) {
	}

	@Override
	public void handleMouseEvent(java.awt.event.MouseEvent e) {
		Point stencilComponentPoint = e.getPoint();
		if (e.getWhen() == lastEventTime) {
			// System.out.println("repeat event");
		} else {
			lastEventTime = e.getWhen();
			Point containerPoint = SwingUtilities.convertPoint(stencilComponent, stencilComponentPoint, this);

			Component component = SwingUtilities.getDeepestComponentAt(this, containerPoint.x, containerPoint.y);

			/*
			 * while ((component != null) &&(compToName.get(component) == null))
			 * { component = component.getParent(); }
			 */

			Point componentPoint = SwingUtilities.convertPoint(stencilComponent, // this,
					stencilComponentPoint, component);

			// if (e.getID() == e.MOUSE_CLICKED) System.out.println( e.getWhen()
			// + " " + component );

			if (component != null) {
				component.dispatchEvent(new MouseEvent(component, e.getID(), e.getWhen(), e.getModifiers(), componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
			}
		}
	}

	private Component getComponentAtPoint(Point stencilComponentPoint) {

		Point containerPoint = SwingUtilities.convertPoint(stencilComponent, stencilComponentPoint, this);

		Component component = SwingUtilities.getDeepestComponentAt(this, containerPoint.x, containerPoint.y);

		return component;
	}

	public void addToTable(String name, Component c) {
		nameToComp.put(name, c);
		compToName.put(c, name);
	}

	public void replaceTable(String name, Component c) {
		Component old = (Component) nameToComp.remove(name);
		nameToComp.put(name, c);

		if (old != null) {
			compToName.remove(old);
		}
		compToName.put(c, name);
	}

	@Override
	public void deFocus() {
		this.requestFocus();
	}

	@Override
	public Dimension getScreenSize() {
		return this.getSize();
	}
}