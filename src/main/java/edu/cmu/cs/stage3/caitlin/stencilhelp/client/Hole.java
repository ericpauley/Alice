package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Vector;

import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;

public class Hole implements StencilObject, MouseEventListener, KeyEventListener, LayoutChangeListener {
	Vector shapes = new Vector();
	Vector stencilObjectPositionListeners = new Vector();
	ObjectPositionManager positionManager = null;
	StencilApplication stencilApp = null;
	StencilManager stencilManager = null; // TEMP
	RoundRectangle2D.Double rect = null;
	String id = null;
	protected boolean isModified = true;
	protected boolean isInitialized = false;
	protected Rectangle previousRect = null;

	protected boolean autoAdvance = false;
	protected int advanceEvent = 0;

	public final static int ADVANCE_ON_CLICK = 0;
	public final static int ADVANCE_ON_PRESS = 1;
	public final static int ADVANCE_ON_ENTER = 2;

	public Hole(String id, ObjectPositionManager positionManager, StencilApplication stencilApp, StencilManager stencilManager) {
		this.positionManager = positionManager;
		this.stencilApp = stencilApp;
		this.id = id;
		this.stencilManager = stencilManager;
		// setInitialPosition();
	}

	/* stencil object stuff */
	@Override
	public Vector getShapes() {
		return shapes;
	}
	@Override
	public Rectangle getRectangle() {
		if (rect != null) {
			return rect.getBounds();
		} else {
			return null;
		}
	}
	@Override
	public Rectangle getPreviousRectangle() {
		if (previousRect != null) {
			return previousRect.union(rect.getBounds());
			// return previousRect;
		} else if (rect != null) {
			return rect.getBounds();
		} else {
			// System.out.println("Nothing reasonable to return for hole bounds");
			return null;
		}
	}
	@Override
	public boolean isModified() {
		if (isModified) {
			isModified = false;
			return true;
		}
		return false;
	}
	@Override
	public boolean intersectsRectangle(Rectangle rect) {
		if (this.rect != null) {
			return rect.intersects(getRectangle());
		} else {
			return false;
		}
	}
	public Point getNotePoint() {
		if (rect != null) {
			return new Point((int) rect.getX(), (int) rect.getY());
		} else {
			return new Point(0, 0);
		}
	}
	@Override
	public void addStencilObjectPositionListener(StencilObjectPositionListener posListener) {
		stencilObjectPositionListeners.addElement(posListener);
	}
	@Override
	public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener) {
		stencilObjectPositionListeners.remove(posListener);
	}
	@Override
	public String getComponentID() {
		return id;
	}

	public void setAutoAdvance(boolean autoAdvance, int advanceEvent) {
		this.autoAdvance = autoAdvance;
		this.advanceEvent = advanceEvent;
	}
	public boolean getAutoAdvance() {
		return autoAdvance;
	}
	public int getAdvanceEvent() {
		return advanceEvent;
	}

	/* Mouse Event Listener */
	@Override
	public boolean contains(Point point) {
		// COME BACK TO ME
		if (rect != null) {

			Rectangle smallRect = rect.getBounds();
			smallRect.grow(-4, -4);
			return smallRect.contains(point.getX(), point.getY());
		} else {
			return false;
		}
	}
	@Override
	public boolean mousePressed(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		// System.out.println("mouse pressed " + autoAdvance + " " +
		// advanceEvent + " ?= 1");
		if (autoAdvance && advanceEvent == ADVANCE_ON_PRESS && (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
			// try {
			// Thread.sleep(700);
			// } catch (Exception ex){}
			stencilManager.showNextStencil();
			return true;
		} else {
			return false;
		}
	}
	@Override
	public boolean mouseReleased(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		return false;
	}
	@Override
	public boolean mouseClicked(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		if (autoAdvance && advanceEvent == ADVANCE_ON_CLICK && (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
			try {
				Thread.sleep(700);
			} catch (Exception ex) {}
			stencilManager.showNextStencil();
			return true;
		} else {
			return false;
		}
	}
	@Override
	public boolean mouseEntered(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		return false;
	}
	@Override
	public boolean mouseExited(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		return false;
	}
	@Override
	public boolean mouseMoved(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		return false;
	}
	@Override
	public boolean mouseDragged(MouseEvent e) {
		stencilApp.handleMouseEvent(e);
		return false;
	}

	/* key listener stuff */
	@Override
	public boolean keyTyped(KeyEvent e) {
		return false;
	}
	@Override
	public boolean keyPressed(KeyEvent e) {
		return false;
	}
	@Override
	public boolean keyReleased(KeyEvent e) {
		if (autoAdvance && advanceEvent == ADVANCE_ON_ENTER && e.getKeyCode() == KeyEvent.VK_ENTER) {
			stencilManager.showNextStencil();
			return true;
		}
		return false;
	}

	/* layout change stuff */
	protected void setInitialPosition() {
		Rectangle r = positionManager.getInitialBox(id);
		if (r != null) {
			rect = new RoundRectangle2D.Double(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 10, 10);
			shapes.removeAllElements();
			shapes.addElement(new ScreenShape(null, rect, false, 0));
		} // else System.out.println("Could not get box for id: " + id);
		isModified = true;
		isInitialized = true;
	}
	public boolean updatePosition() {
		previousRect = getRectangle();
		Rectangle r = positionManager.getBoxForID(id);
		if (r != null) {
			rect = new RoundRectangle2D.Double(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 10, 10);
			shapes.removeAllElements();
			shapes.addElement(new ScreenShape(null, rect, false, 0));

			isModified = true;

			// we updated successfully
			return true;
		} else {
			isModified = true;

			// this id seems to be missing
			return false;
		}
	}
	@Override
	public boolean layoutChanged() {
		boolean success = true;
		success = updatePosition();

		if (!success) {
			// give the authoring tool a chance to update
			try {
				java.lang.Thread.sleep(1000);
			} catch (java.lang.InterruptedException ie) {}

			// ask for it again
			success = updatePosition();
		}

		if (success) {
			// we've found this id and updated appropriately
			return true;
		} else {
			// this id is still missing, and probably isn't coming
			return false;
		}

	}
}