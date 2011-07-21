package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Point;

public class Frame implements StencilObject, LayoutChangeListener {
  protected Vector stencilObjectPositionListeners = new Vector();
  protected Vector shapes = new Vector();
  protected String id = null;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle2D.Double rect = null;
  protected boolean isInitialized = false;
  protected ObjectPositionManager positionManager = null;
  protected boolean missingID = false;
  public Frame(String id, ObjectPositionManager positionManager) {
    this.positionManager = positionManager;
    this.id = id;
    //setInitialPosition();
  }

  public boolean updatePosition() {
    //if (! (isInitialized)) { setInitialPosition(); }
    //if (isInitialized) {
      previousRect = getRectangle();

      if (shapes == null) shapes = new Vector();
      shapes.removeAllElements();

      Rectangle r = null;
      try {
        r = positionManager.getBoxForID( id );
      } catch (java.lang.NullPointerException npu) {}

      if (r != null) {

        rect = new Rectangle2D.Double(r.x-3, r.y-3, r.width + 6, r.height + 6);

        RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x-3,r.y-3, r.width +6, r.height + 6, 1, 1);
        shapes.addElement(new ScreenShape( null, rr, true, 0));

        Rectangle2D.Double temp = new Rectangle2D.Double(r.x - 6, r.y -6, 3, r.height + 12);
        shapes.addElement(new ScreenShape(Color.red, temp, true, 1));

        temp = new Rectangle2D.Double(r.x -6, r.y - 6, r.width + 12, 3);
        shapes.addElement(new ScreenShape(Color.red, temp, true, 2));

        temp = new Rectangle2D.Double(r.x + r.width + 3, r.y - 6, 3, r.height + 12);
        shapes.addElement(new ScreenShape(Color.red, temp, true, 3));

        temp = new Rectangle2D.Double(r.x -6, r.y + r.height + 3, r.width + 12, 3);
        shapes.addElement(new ScreenShape(Color.red, temp, true, 4));

        temp = new Rectangle2D.Double(r.x-3,r.y-3, r.width +6, r.height + 6);
        shapes.addElement(new ScreenShape((new Color (0, 100,255,50)), temp, true, 5));

        // we updated successfully
        isModified = true;
        return true;
    } else {
        // we failed to find the box for this id
        isModified = true;
        return false;
    }

  }

  protected void setInitialPosition() {
    previousRect = getRectangle();

    if (shapes == null) shapes = new Vector();
    shapes.removeAllElements();

    Rectangle r = positionManager.getInitialBox( id );
    if (r != null) {
      rect = new Rectangle2D.Double(r.x-3, r.y-3, r.width + 6, r.height + 6);

      RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x-3,r.y-3, r.width +6, r.height + 6, 1, 1);
      shapes.addElement(new ScreenShape( null, rr, true, 0));

      Rectangle2D.Double temp = new Rectangle2D.Double(r.x - 6, r.y -6, 3, r.height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 1));

      temp = new Rectangle2D.Double(r.x -6, r.y - 6, r.width + 12, 3);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 2));

      temp = new Rectangle2D.Double(r.x + r.width + 3, r.y - 6, 3, r.height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 3));

      temp = new Rectangle2D.Double(r.x -6, r.y + r.height + 3, r.width + 12, 3);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 4));

      temp = new Rectangle2D.Double(r.x-3,r.y-3, r.width +6, r.height + 6);
      shapes.addElement(new ScreenShape((new Color (0, 100,255,50)), temp, true, 5));
    }

   //Area a = new Area(new Rectangle2D.Double( r.x-6, r.y-6, r.width + 12,r.height + 12 ) );
   isModified = true;
   isInitialized = true;

  }

  /* stencil object stuff */
  public Vector getShapes(){
    return shapes;
  }
  public Rectangle getRectangle(){
    if (rect != null) {
      return rect.getBounds();
    } else return null;
  }
  public Rectangle getPreviousRectangle(){
    if ((previousRect == null) && (rect != null)) return rect.getBounds();
    else return previousRect;
  }
  public boolean isModified(){
    if (isModified) {
      isModified = false;
      return true;
    } else return false;
  }
  public boolean intersectsRectangle( Rectangle rect ){
    if (this.rect != null) {
      return (rect.intersects(this.getRectangle()));
    } else {
      return false;
    }
  }
  // COME BACK - should stencil app really get passed in?
  public Point getNotePoint() {
    if (rect != null) return new Point((int)rect.getX(), (int)rect.getY());
    else return new Point(0,0);
  }
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener){
    stencilObjectPositionListeners.addElement(posListener);
  }
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener){
    stencilObjectPositionListeners.remove(posListener);
  }
  public String getComponentID() {
    return id;
  }

  /* layout stuff */
   public boolean layoutChanged(){
    boolean success = true;
    success = updatePosition();

    return success;



   }
}