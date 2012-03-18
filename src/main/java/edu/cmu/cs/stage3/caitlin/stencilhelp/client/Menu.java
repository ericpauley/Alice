package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.Color;

public class Menu implements StencilObject, StencilPanelMessageListener, MouseEventListener, StencilFocusListener, ReadWriteListener {
  protected Vector shapes = new Vector();
  protected Vector stencilObjectPositionListeners = new Vector();
  protected StencilManager stencilManager = null;
  protected boolean isShowing = false;
  protected int level = 0;
  protected Point centerPoint;
  protected boolean writeEnabled = true;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle rect = null;

  private Shape upOption = null;
  private Shape downOption = null;
  private Shape rightOption = null;
  private Shape leftOption = null;
  int fontSize = 30;
  Font font = new Font("Arial", 0, fontSize);

  public Menu(StencilManager stencilManager) {
    this.stencilManager = stencilManager;
  }

  protected void updateShapes(Point p) {
      this.centerPoint = p;
      previousRect = this.getRectangle();
      if (level  == 1) {
          shapes.removeAllElements();

          Point upPosition = new Point( p.x -40, p.y - 50);
          upOption = new RoundRectangle2D.Double(upPosition.x, upPosition.y, 65, 30, 10,10);
          shapes.addElement(new ScreenShape(Color.blue,upOption, true, 0));
          Shape s = createWordShape("new", new Point(upPosition.x + 5, upPosition.y + 25));
          shapes.addElement(new ScreenShape(Color.white,s, true, 1));

          Point rightPosition = new Point( p.x -90, p.y - 15);
          rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 65, 30, 10,10);
          shapes.addElement(new ScreenShape(Color.blue,rightOption, true, 2));
          s = createWordShape("load", new Point(rightPosition.x + 5, rightPosition.y + 25));
          shapes.addElement(new ScreenShape(Color.white,s, true, 3));

          Point leftPosition = new Point( p.x + 20, p.y - 15);
          leftOption = new RoundRectangle2D.Double(leftPosition.x, leftPosition.y, 70, 30, 10,10);
          shapes.addElement(new ScreenShape(Color.blue,leftOption, true, 4));
          s = createWordShape("save", new Point(leftPosition.x + 5, leftPosition.y + 25));
          shapes.addElement(new ScreenShape(Color.white,s, true, 5));

          Point downPosition = new Point( p.x -40, p.y + 20);
          downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 65, 30, 10,10);
          shapes.addElement(new ScreenShape(Color.blue,downOption, true, 6));
          s = createWordShape("lock", new Point(downPosition.x + 5, downPosition.y + 25));
          shapes.addElement(new ScreenShape(Color.white,s, true, 7));

          previousRect = rect;
          rect = new Rectangle(p.x - 90, p.y - 50, p.x + 90, p.y + 50);

      } else if (level == 0) {
          shapes.removeAllElements();

          if (writeEnabled) {

            Point upPosition = new Point( p.x -40, p.y - 50);
            upOption = new RoundRectangle2D.Double(upPosition.x, upPosition.y, 75, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,upOption, true, 0));
            Shape s = createWordShape("note", new Point(upPosition.x + 5, upPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 1));

            Point rightPosition = new Point( p.x -90, p.y - 15);
            rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 80, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,rightOption, true, 2));
            s = createWordShape("frame", new Point(rightPosition.x + 5, rightPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 3));

            Point leftPosition = new Point( p.x + 20, p.y - 15);
            leftOption = new RoundRectangle2D.Double(leftPosition.x, leftPosition.y, 75, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,leftOption, true, 4));
            s = createWordShape("clear", new Point(leftPosition.x + 5, leftPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 5));

            Point downPosition = new Point( p.x -40, p.y + 20);
            downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 75, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,downOption, true, 6));
            s = createWordShape("other", new Point(downPosition.x + 5, downPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 7));

            previousRect = rect;
            rect = new Rectangle(p.x - 90, p.y - 50, p.x + 95, p.y + 50);
          } else {
            Point rightPosition = new Point( p.x -90, p.y - 15);
            rightOption = new RoundRectangle2D.Double(rightPosition.x, rightPosition.y, 65, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,rightOption, true, 0));
            Shape s = createWordShape("load", new Point(rightPosition.x + 5, rightPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 1));

            // should I really do this?
            Point downPosition = new Point( p.x -40, p.y + 20);
            downOption = new RoundRectangle2D.Double(downPosition.x, downPosition.y, 65, 30, 10,10);
            shapes.addElement(new ScreenShape(Color.blue,downOption, true, 2));
            s = createWordShape("lock", new Point(downPosition.x + 5, downPosition.y + 25));
            shapes.addElement(new ScreenShape(Color.white,s, true, 3));

            previousRect = rect;
            rect = new Rectangle(p.x - 90, p.y - 15, p.x + 25, p.y + 50);
          }
      }
      isModified = true;
  }

  protected Shape createWordShape(String word, Point startPos) {
      TextLayout wordLayout = new TextLayout(word, font, new FontRenderContext(null, false, false));
      AffineTransform textAt = new AffineTransform();
      textAt.translate(startPos.x, startPos.y);
      Shape s = wordLayout.getOutline(textAt);

      return s;
  }

  /* Stencil Object stuff */
  public Vector getShapes(){
    if (isShowing) {
      return shapes;
    } else return null;
  }
  public Rectangle getRectangle(){
    if (isShowing) {
      return rect;
    }
    return null;
  }
  public Rectangle getPreviousRectangle(){
    Rectangle pRect = previousRect;
    //previousRect = null;
    return pRect;
  }
  public boolean isModified(){
    if (isModified) {
      isModified = false;
      return true;
    } else return false;
  }
  public boolean intersectsRectangle( Rectangle rect ){
    Rectangle currentRect = this.getRectangle();
    if (currentRect != null) {
      return rect.intersects(currentRect);
    } else return false;
  }
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener){
    stencilObjectPositionListeners.addElement(posListener);
  }
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener){
    stencilObjectPositionListeners.remove(posListener);
  }
  public String getComponentID() {
    return null;
  }

  /* StencilPanelMessageListener Stuff */
  public void messageReceived(int messageID, Object data){
    if (this.writeEnabled) {
      isShowing = true;
      level = 0;
      updateShapes((Point)data);
      stencilManager.requestFocus(this);
    }
  }

  /* MouseListener stuff */
  public boolean contains( Point point ){
    if (isShowing) {
      if (writeEnabled) {
        return true;
      } else return false;
      /*  if (upOption.contains(point)) return true;
        else if (downOption.contains(point)) return true;
        else if (rightOption.contains(point)) return true;
        else if (leftOption.contains(point)) return true;
        else return false;
      } else {
        if (downOption.contains(point)) return true;
        else if (rightOption.contains(point)) return true;
        else return false;
      }*/
    } else return false;
  }
  public boolean mousePressed(MouseEvent e){
    return false;
  }
  public boolean mouseReleased(MouseEvent e){
    return false;
  }
  public boolean mouseClicked(MouseEvent e){
    isShowing = false;
    if (writeEnabled) {
      if (level == 0) {
        previousRect = rect;
        if (upOption.contains(e.getPoint())) {
          stencilManager.createNewNote(centerPoint);
        } else if (downOption.contains(e.getPoint())) {
          level = 1;
          isShowing = true;
          updateShapes(e.getPoint());
        } else if (rightOption.contains(e.getPoint())) {
          stencilManager.createNewFrame(centerPoint);
        } else if (leftOption.contains(e.getPoint())) {
          stencilManager.removeAllObjects();
        }
        isModified = true;
      } else {
        if (upOption.contains(e.getPoint())) {
          stencilManager.insertNewStencil();
        } else if (downOption.contains(e.getPoint())) {
          stencilManager.toggleLock();
        } else if (rightOption.contains(e.getPoint())) {
          stencilManager.loadStencilsFile();
        } else if (leftOption.contains(e.getPoint())) {
          stencilManager.saveStencilsFile();
        }
        previousRect = rect;
        isModified = true;
      }
    } else {
      if (downOption.contains(e.getPoint())) {
        stencilManager.toggleLock();
      } else if (rightOption.contains(e.getPoint())) {
        stencilManager.loadStencilsFile();
      }
      previousRect = rect;
      isModified = true;
    }
    return true;
  }
  public boolean mouseEntered(MouseEvent e){
    return false;
  }
  public boolean mouseExited(MouseEvent e){
    return false;
  }
  public boolean mouseMoved(MouseEvent e){
    return false;
  }
  public boolean mouseDragged(MouseEvent e){
    return false;
  }

  /* stencilfocus stuff */
  public void focusGained(){
    isShowing = true;
  }
  public void focusLost(){
    isShowing = false;
    previousRect = rect;
    isModified = true;
  }

  /* readwrite listener */
  public void setWriteEnabled(boolean enabled){
    this.writeEnabled = enabled;
  }
}