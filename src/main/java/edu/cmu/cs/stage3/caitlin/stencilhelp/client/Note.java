package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Line2D;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import org.w3c.dom.*;

public class Note implements StencilObject, MouseEventListener, KeyEventListener, StencilObjectPositionListener,
  ReadWriteListener, LayoutChangeListener{
  protected Vector shapes = new Vector();
  protected Paragraph paragraph = null;
  protected Vector stencilObjectPositionListeners = new Vector();
  protected ObjectPositionManager positionManager = null;
  protected StencilManager stencilManager = null;
  protected Point clickPos = null;
  protected Point initialPos = null;
  protected StencilObject scrObject = null;
  private RoundRectangle2D.Double bgRect = null;
  private RoundRectangle2D.Double underRect = null;
  private RoundRectangle2D.Double tabRect = null;
  private Line2D.Double line = null, line2 = null, line3 = null, line4 = null, line5 = null;
  private java.awt.Polygon triangle = null;
  protected boolean writeEnabled = true;

  // experimental to restore specified positions in reading mode
  protected Point startClick = null;
  protected Point startInitial = null;

  private boolean hasNext = false;
  private RoundRectangle2D.Double bgNext = null;
  private RoundRectangle2D.Double underNext = null;
  private ScreenShape nextShape = null;
  private Font font = new Font("Arial", 1, 16);

  protected boolean isModified = true;
  protected Rectangle rect = null;
  protected Rectangle previousRect = null;
  private boolean prevBlank = false;

  private ScreenShape currentShape = null;
  private boolean startNewLine = false;
  protected boolean isInitialized = false;

  private Point startDragging = null;
  private Point currentDragPosition = null;
  private ScreenShape moveRect = null;
  private int dragInProgress = 0;

  private int fontSize = 20;
  private int dropOffset = 4;

  private int rectWidth = 240;
  private int rectHeight = 190;
  private int noteOpacity = 255;
  private int tabRectOffset = 15;
  private int tabRectWidth = 20;
  private int tabRectHeight = 30;

  private String noteNumber = "";
  private Color defaultColor = new Color(18,21,116);
  private Color accentColor = new Color(48, 52, 221);

  public Note(Point clickPos, Point initPos, StencilObject scrObject, ObjectPositionManager positionManager,
    StencilManager stencilManager, boolean hasNext) {
    this.positionManager = positionManager;
    this.stencilManager = stencilManager;
    this.clickPos = clickPos;
    this.startClick = clickPos;
    this.initialPos = initPos;
    this.startInitial = initPos;
    this.scrObject = scrObject;
    this.hasNext = hasNext;
  }

  public void addText(String text, String color) {
    if (text.length() > 0) {
      char c = text.charAt(0);
      if (Character.isDigit(c)) {
        noteNumber = new Character(c).toString();
        text = text.substring(1);
      }
      if (paragraph == null) {
        paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
      }
      if ( (color != null) && (color.equals("blue")) ) {
        paragraph.addText(text, accentColor);
      } else {
        paragraph.addText(text, defaultColor);
      }
      if (isInitialized) {
        generateShapes();
        this.setModified();
      }
    }
  }

  public void setText(Vector msgs) {
    paragraph.clearText();
    for (int i = 0; i < msgs.size(); i++) {
      String text = (String) msgs.elementAt(i);
      if (text.length() > 0) {
        char c = text.charAt(0);
        if (Character.isDigit(c)) {
          noteNumber = new Character(c).toString();
          text = text.substring(1);
        }
        paragraph.addText(text, defaultColor);
        if (isInitialized) {
          generateShapes();
          this.setModified();
        }
      }
    }
  }

  protected void drawArrow(Point holePoint, Point wordPoint) {
    double delX = wordPoint.getX() - holePoint.getX();
    double delY = wordPoint.getY() - holePoint.getY();
    double distance = Math.sqrt(delX * delX + delY * delY);
    delX /= distance;
    delY /= distance;

    Point basePt = new Point((int)(holePoint.getX() + 20*delX), (int)(holePoint.getY() + 20*delY));
    Point side1Pt = new Point((int)(basePt.getX() + 10*delY), (int)(basePt.getY() - 10*delX));
    Point side2Pt = new Point((int)(basePt.getX() - 10*delY), (int)(basePt.getY() + 10*delX));

    Point junkPt = new Point((int)(holePoint.getX() + 20), (int) holePoint.getY());

    triangle = new java.awt.Polygon();
    triangle.addPoint((int)holePoint.getX(), (int)holePoint.getY());
    triangle.addPoint((int)side2Pt.getX(), (int)side2Pt.getY());
    triangle.addPoint((int)side1Pt.getX(), (int)side1Pt.getY());

  }

  //public void updatePosition(StencilApplication stencilApp) {
  public void updatePosition() {
    if (stencilManager.writeEnabled) {
      if ((!isInitialized) && (scrObject == null)) {
        initializeNote();
      } else if ( (!isInitialized) && (scrObject != null) && (scrObject.getRectangle() != null) ) {
        initializeNote();
      }
    } else {
      if (scrObject == null) {
        initializeNote();
      } else if ( (scrObject != null) && (scrObject.getRectangle() != null) ) {
        initializeNote();
      }
    }

    if( (isInitialized) && (scrObject != null) ){
      if (scrObject instanceof Hole) {
        clickPos = ((Hole)scrObject).getNotePoint();
      } else if (scrObject instanceof Frame) {
        clickPos = ((Frame)scrObject).getNotePoint();
      } else if (scrObject instanceof NavigationBar) {
        clickPos = ((NavigationBar)scrObject).getNotePoint();
      }
      if (scrObject != null) {
        Point holePoint = this.getHolesPoint();
        clickPos = new Point(holePoint.x + initialPos.x, holePoint.y + initialPos.y);
      } else {
        clickPos = new Point(clickPos.x + initialPos.x, clickPos.y + initialPos.y);
      }
      underRect.setFrame(clickPos.x+dropOffset, clickPos.y-fontSize+dropOffset, bgRect.width, bgRect.height);
      bgRect.setFrame(clickPos.x, clickPos.y-fontSize, bgRect.width, bgRect.height);
      if (tabRect != null) tabRect.setFrame(clickPos.x - tabRectOffset, clickPos.y, tabRectWidth, tabRectHeight);
      if (hasNext) {
        underNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60 + 2, bgRect.getY() + bgRect.getHeight() + 2, underNext.getWidth(), underNext.getHeight());
        bgNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60, bgRect.getY() + bgRect.getHeight(), bgNext.getWidth(), bgNext.getHeight());
      }
      if (paragraph != null) paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y));
      if (line != null) {
        Point holePoint = this.getHolesPoint();
        //Point wordPoint = new Point(new Point(clickPos.x + (int)(bgRect.width/2), clickPos.y - fontSize));
        Point wordPoint = this.getWordsPoint();
        if ((holePoint != null) && (wordPoint != null)) {
          // determine whether or not we should be initializing now
          //line.setLine(holePoint, new Point(clickPos.x + (int)(bgRect.width/2), clickPos.y - fontSize));
          line.setLine(holePoint, wordPoint);
          line2.setLine(holePoint.x -1, holePoint.y, wordPoint.x-1, wordPoint.y);
          line3.setLine(holePoint.x +1, holePoint.y, wordPoint.x+1, wordPoint.y);
          line4.setLine(holePoint.x -2, holePoint.y, wordPoint.x-2, wordPoint.y);
          line5.setLine(holePoint.x +2, holePoint.y, wordPoint.x+2, wordPoint.y);
        }
      }
    }
    if (isInitialized) generateShapes();
  }

  protected Rectangle getBoundingRectangle() {
    double minX = 0;
    double minY = 0;
    double maxX = 0;
    double maxY = 0;

    Rectangle area = bgRect.getBounds().union(underRect.getBounds());
    if (tabRect != null) area = area.union(tabRect.getBounds());
    if (line != null) {
      area = area.union(new Rectangle(line.getBounds()) );
      if (triangle != null) area = area.union(triangle.getBounds());
    }
    if (moveRect != null) {
      Rectangle movingRect = moveRect.getShape().getBounds();
      movingRect.grow(10,10);
      area = area.union(movingRect);
    }
    return area;
  }

  protected void setModified() {
    if (isInitialized) {
      if (isModified && (previousRect != null) ) {
        previousRect = previousRect.union(this.rect);
        if (tabRect != null) previousRect = previousRect.union(tabRect.getBounds());
        if ((hasNext) && (bgNext != null)) previousRect = previousRect.union(bgNext.getBounds());
      } else {
        previousRect = this.rect;
        if (previousRect != null) {
          previousRect.grow(20,20);
        }
        if ((hasNext) && (bgNext != null) && (previousRect != null)) {
          previousRect = previousRect.union(bgNext.getBounds());
        }
        if (tabRect != null) previousRect = previousRect.union(tabRect.getBounds());
      }
      this.rect = this.getBoundingRectangle();
    }
    isModified = true;
  }

  public void initializeNote() {
    // if we are in read mode, then restore the initial values of clickPos and initialPos
    if (!stencilManager.writeEnabled ) {
      clickPos = this.startClick;
      initialPos = this.startInitial;
      shapes.removeAllElements();

      if (paragraph != null) {
        paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y));
      }
    }

    // create the background rectangles
    if (scrObject != null) {
      Point holePoint = getHolesPoint(initialPos);
      clickPos = new Point(holePoint.x + initialPos.x, holePoint.y + initialPos.y);
      underRect = new RoundRectangle2D.Double(clickPos.x+dropOffset, clickPos.y - fontSize + dropOffset , rectWidth, rectHeight, 10, 10);
      Color transGray = new Color(75, 75, 75, 100);
      ScreenShape sShape = new ScreenShape(transGray, underRect, true, 0);
      shapes.addElement(sShape);

      bgRect = new RoundRectangle2D.Double(clickPos.x , clickPos.y - fontSize, rectWidth, rectHeight, 10, 10);
      sShape = new ScreenShape(new Color(255,255,150,noteOpacity), bgRect, true, 1);
      shapes.addElement(sShape);
    } else {
      clickPos = new Point(clickPos.x + initialPos.x, clickPos.y + initialPos.y);
      underRect = new RoundRectangle2D.Double(clickPos.x+dropOffset, clickPos.y - fontSize + dropOffset , rectWidth, rectHeight, 10, 10);
      Color transGray = new Color(75, 75, 75, 100);
      ScreenShape sShape = new ScreenShape(transGray, underRect, true, 0);
      shapes.addElement(sShape);

      bgRect = new RoundRectangle2D.Double(clickPos.x , clickPos.y - fontSize, rectWidth, rectHeight, 10, 10);
      sShape = new ScreenShape(new Color(255,255,150,noteOpacity), bgRect, true, 1);
      shapes.addElement(sShape);
    }
    if (hasNext) {
      underNext = new RoundRectangle2D.Double(bgRect.getX() + bgRect.getWidth() - 60 + 2, bgRect.getY() + bgRect.getHeight() + 2, 60, 20, 10, 10);
      ScreenShape sShape = new ScreenShape( new Color(255,200,240, 100), underNext, true, 2);
      shapes.addElement(sShape);

      bgNext = new RoundRectangle2D.Double(bgRect.getX() + bgRect.getWidth() - 60, bgRect.getY() + bgRect.getHeight(), 60, 20, 10, 10);
      sShape = new ScreenShape(new Color(255,180,210,220), bgNext, true, 3);
      shapes.addElement(sShape);
    }

    // create line between words and holes
    if (scrObject != null) {
        Point holePt = getHolesPoint();
        Point wordsPt = getWordsPoint();
        if ((holePt != null) && (wordsPt != null)) {
          line = new Line2D.Double(holePt, wordsPt);
          line2 = new Line2D.Double(holePt.x -1, holePt.y, wordsPt.x-1, wordsPt.y);
          line3 = new Line2D.Double(holePt.x +1, holePt.y, wordsPt.x+1, wordsPt.y);
          line4 = new Line2D.Double(holePt.x -2, holePt.y, wordsPt.x-2, wordsPt.y);
          line5 = new Line2D.Double(holePt.x +2, holePt.y, wordsPt.x+2, wordsPt.y);
          ScreenShape sShape = new ScreenShape(Color.red, line, false, 2);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line2, false, 3);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line3, false, 4);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line4, false, 5);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line5, false, 6);
          shapes.addElement(sShape);
        } else {
          line = new Line2D.Double(0,0,0,0);
          line2 = new Line2D.Double(0,0,0,0);
          line3 = new Line2D.Double(0,0,0,0);
          line4 = new Line2D.Double(0,0,0,0);
          line5 = new Line2D.Double(0,0,0,0);
          ScreenShape sShape = new ScreenShape(Color.red, line, false, 2);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line2, false, 3);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line3, false, 4);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line4, false, 5);
          shapes.addElement(sShape);
          sShape = new ScreenShape(Color.red, line5, false, 6);
          shapes.addElement(sShape);
        }
    }
    isInitialized = true;
    this.setModified();
  }

   protected void generateShapes() {
    int index = 0;
    if (scrObject != null) {
      index = 7;
    } else index = 2;

    if (hasNext) index += 2;

    Vector newShapes = new Vector();
    for (int i = 0; i < index; i++) {
      newShapes.addElement(shapes.elementAt(i));
    }
    shapes = newShapes;

    if ( (scrObject != null) && (getHolesPoint() != null) && (getWordsPoint() != null)) {
      this.drawArrow(this.getHolesPoint(), this.getWordsPoint());
      ScreenShape sShape = new ScreenShape(Color.red, triangle, true, index);
      shapes.addElement(sShape);
      index++;
    }

     if (this.noteNumber.length() > 0) {
      tabRect = new RoundRectangle2D.Double(bgRect.getX() - this.tabRectOffset, this.bgRect.getY(), tabRectWidth,tabRectHeight,15,15);
      ScreenShape sShape = new ScreenShape(new Color(255,255,150,noteOpacity), tabRect, true, index);
      shapes.addElement(sShape);
      index++;
    }

    if (hasNext) {
      TextLayout wordLayout = new TextLayout("next", font, new FontRenderContext(null, false, false));
      AffineTransform textAt = new AffineTransform();
      textAt.translate(bgRect.getX() + bgRect.getWidth() - 50, bgRect.getY() + bgRect.getHeight() + 15);
      Shape s = wordLayout.getOutline(textAt);
      nextShape = new ScreenShape(new Color(0,0,180), s, true, 8); // temp
      shapes.addElement(nextShape);
      index++;
    }

    if (this.noteNumber.length() == 0) this.noteNumber = "-1";
    TextLayout tl = new TextLayout(this.noteNumber, new java.awt.Font("Comic Sans MS", 1, fontSize), new FontRenderContext(null, false, false));
    AffineTransform at = new AffineTransform();
    at.translate(bgRect.getX() - 10, bgRect.getY() + 20);
    Shape s = tl.getOutline(at);
    ScreenShape sShape = new ScreenShape(Color.blue, s, true, index);
    if (this.noteNumber.equals("-1")) {
      this.noteNumber = "";
    } else {
      shapes.addElement(sShape);
      index++;
    }

    if (paragraph != null) {
      Vector wordShapes = paragraph.getShapes();
      for (int i = 0; i < wordShapes.size(); i++) {
        ScreenShape wShape = (ScreenShape) wordShapes.elementAt(i);
        shapes.addElement(new ScreenShape(wShape.getColor(),wShape.getShape(), wShape.getIsFilled(), index));
        index++;
      }
    }

    if (paragraph != null) {
      if (writeEnabled) {
        Shape caret = paragraph.getCaretShape();
        if (caret != null) {
          shapes.addElement(new ScreenShape(Color.red, caret, false, index));
          index++;
        }
      }
    }

    if (moveRect != null) {
      shapes.addElement(moveRect);
    }
  }

  protected Point getClosestPoint(Rectangle rect, Point center) {
    if (rect == null) return new Point(0,0);
     else if( java.lang.Math.abs(rect.y - center.y) > Math.abs(rect.y+ rect.height - center.y) ){
        return new Point( (rect.x + rect.width/2), (rect.y + rect.height + 1));
    } else return new Point( (rect.x + rect.width/2), rect.y + 1);
  }

  protected Point getHolesPoint(Point offset) {
    if (scrObject != null) {
      Rectangle rect = scrObject.getRectangle();
      if (rect != null) {
        if (offset.getY() > 0) {
          return new Point( (rect.x + rect.width/2), (rect.y + rect.height + 1));
        } else {
          return new Point( (rect.x + rect.width/2), rect.y + 1);
        }
      } else return new Point(0,0);
    } else return new Point(0,0);
  }

  protected Point getHolesPoint() {
      return getClosestPoint( scrObject.getRectangle(), new Point((int)bgRect.getCenterX(), (int)bgRect.getCenterY()) );
  }

  protected Point getWordsPoint() {
      Rectangle holeRect = scrObject.getRectangle();
      if (holeRect != null) {
        return getClosestPoint( bgRect.getBounds(), new Point((int)holeRect.getCenterX(), (int)holeRect.getCenterY()) );
      } else return null;
  }

  /* screen object stuff */
  public Vector getShapes() {
    if ((scrObject != null) && (scrObject.getRectangle() == null)) {
      prevBlank = true;
      return null;
    } else {
      prevBlank = false;
      return shapes;
    }
  }
  public Rectangle getRectangle(){
    return rect;
  }
  public Rectangle getPreviousRectangle(){
    return previousRect;
  }
  public boolean isModified(){
    if (isModified) {
      isModified = false;
      //this.generateShapes();//maybe
      return true;
    } else return false;
  }
  public boolean intersectsRectangle( Rectangle testRect ){
    if (rect != null) {
      return rect.intersects(testRect);
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

  /* Mouse event listener stuff */
  public boolean contains( Point point ){
    if (bgNext != null) return (bgRect.contains(point.getX(), point.getY()) || bgNext.contains(point.getX(), point.getY()));
    else if (bgRect != null) return bgRect.contains(point.getX(), point.getY());
    else return false;
  }
  public boolean mousePressed(MouseEvent e){
    if ((dragInProgress == 0) && (bgRect.contains(e.getX(), e.getY())) ){
      startDragging = e.getPoint();
      RoundRectangle2D.Double rect = new RoundRectangle2D.Double(clickPos.x - 5 , clickPos.y - fontSize, rectWidth, rectHeight, 10, 10);
      moveRect = new ScreenShape( new Color(255,255,255,100), rect, true, shapes.size() );
      dragInProgress = 1;
      generateShapes();
      this.setModified();
    }
    return false;
  }
  public boolean mouseReleased(MouseEvent e){
    if (dragInProgress == 1) {
        moveRect = null;
        Point p = new Point(e.getPoint().x - startDragging.x, e.getPoint().y - startDragging.y);
        clickPos = new Point( clickPos.x + p.x, clickPos.y + p.y);
        Point scrObjAttach = null;
        if (scrObject instanceof Hole) {
          scrObjAttach = ((Hole)scrObject).getNotePoint();
        } else if (scrObject instanceof Frame) {
          scrObjAttach = ((Frame)scrObject).getNotePoint();
        } else if (scrObject instanceof NavigationBar) {
          scrObjAttach = ((NavigationBar)scrObject).getNotePoint();
        }
        if (scrObjAttach != null) {
          initialPos = new Point(clickPos.x - scrObjAttach.x, clickPos.y - scrObjAttach.y);
        }
        underRect.setFrame(clickPos.x-5+dropOffset, clickPos.y-fontSize+dropOffset, bgRect.width, bgRect.height);
        bgRect.setFrame(clickPos.x-5, clickPos.y-fontSize, bgRect.width, bgRect.height);

        if (tabRect != null) tabRect.setFrame(bgRect.getX()-tabRectOffset, bgRect.getY(), tabRectWidth, tabRectHeight);

        if (hasNext) {
          underNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60 + 2, bgRect.getY() + bgRect.getHeight() + 2, underNext.getWidth(), underNext.getHeight());
          bgNext.setFrame(bgRect.getX() + bgRect.getWidth() - 60, bgRect.getY() + bgRect.getHeight(), bgNext.getWidth(), bgNext.getHeight());
        }

        dragInProgress = 0;
        if (scrObject != null) {
          Point holePoint = getHolesPoint();
          Point wordPoint = getWordsPoint();
          line.setLine(holePoint, wordPoint);
          line2.setLine(holePoint.x -1, holePoint.y, wordPoint.x-1, wordPoint.y);
          line3.setLine(holePoint.x +1, holePoint.y, wordPoint.x+1, wordPoint.y);
          line4.setLine(holePoint.x -2, holePoint.y, wordPoint.x-2, wordPoint.y);
          line5.setLine(holePoint.x +2, holePoint.y, wordPoint.x+2, wordPoint.y);
        }
        if (paragraph != null) paragraph.setTextOrigin(new Point(clickPos.x + 10, clickPos.y));
        else paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));

        generateShapes();
        this.setModified();
    }
    return true;
  }
  public boolean mouseClicked(MouseEvent e){
    if (bgRect.contains(e.getX(), e.getY())) {
      if (paragraph != null) paragraph.updateCaretPosition(e.getPoint());
      generateShapes();
    } else if (bgNext.contains(e.getX(), e.getY())) {
      stencilManager.showNextStencil();
    }
    //generateShapes();
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
    if (dragInProgress == 1) {
      Point p = new Point(e.getPoint().x - startDragging.x, e.getPoint().y - startDragging.y);
      RoundRectangle2D.Double rect = (RoundRectangle2D.Double) moveRect.getShape();
      rect.setFrame(clickPos.x + p.x, clickPos.y- fontSize + p.y, rect.width, rect.height);
      moveRect.setShape(rect);
      if (line != null) {
        Point holePoint = new Point( (int)line.getP1().getX(), (int)line.getP1().getY() );
        Point wordPoint = new Point(new Point(clickPos.x + p.x + (int)(rect.width/2), clickPos.y + p.y - fontSize));
        line.setLine(line.getP1(), new Point(clickPos.x + p.x + (int)(rect.width/2), clickPos.y + p.y - fontSize));
        line2.setLine(holePoint.x -1, holePoint.y, wordPoint.x-1, wordPoint.y);
        line3.setLine(holePoint.x +1, holePoint.y, wordPoint.x+1, wordPoint.y);
        line4.setLine(holePoint.x -2, holePoint.y, wordPoint.x-2, wordPoint.y);
        line5.setLine(holePoint.x +2, holePoint.y, wordPoint.x+2, wordPoint.y);

        drawArrow(holePoint, wordPoint);
        ScreenShape sShape = new ScreenShape(Color.red, triangle, true, 6);
        shapes.setElementAt(sShape, 6);
      }
      this.setModified();
      return true;
    } else return false;
  }

  /* key event listener stuff */
  public boolean keyTyped(KeyEvent e){
    if ( Character.isISOControl(e.getKeyChar()) ){
      //ignore
      return false;
    } else {
      //processUpdate(new String() + e.getKeyChar());
      if (writeEnabled) {
        if (paragraph == null) {
          paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
        }
        paragraph.insertChar(e.getKeyChar());
        generateShapes();
        this.setModified();
        return true;
      } else return false;
    }
  }
  public boolean keyPressed(KeyEvent e){
    if ( Character.isISOControl(e.getKeyChar()) ){
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        // COME BACK
        if (paragraph == null) {
          paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
        }
        paragraph.createNewLine();
        generateShapes();
        this.setModified();
        return true;
      } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        if (writeEnabled) {
          if (paragraph == null) {
            paragraph = new Paragraph(rectWidth - 20, new Point(clickPos.x + 10, clickPos.y));
          }
          paragraph.deleteChar();
          generateShapes();
          this.setModified();
          return true;
        } else return false;
      } else return false;
    } else {
       return false;
    }
  }
  public boolean keyReleased(KeyEvent e){
    return false;
  }

  /* stencil object positionlistener stuff */
  public void stencilObjectMoved(Rectangle newPos){
  }

  public void write(Document document, Element element) {
    Element noteElement = document.createElement("note");

    // get an appropriate component id
    String id = "null";
    if (scrObject != null) {
      id = scrObject.getComponentID();
      if (id == null) {
        if (scrObject instanceof NavigationBar) {
          id = "navbar";
        } else id = "null";
      }
    }

    Element idNode = document.createElement("id");
    CDATASection idSection = document.createCDATASection(id);
    idNode.appendChild(idSection);
    noteElement.appendChild(idNode);

    Vector msgs = new Vector();
    Vector colors = new Vector();
    if (paragraph != null) {
      msgs = paragraph.getText();
      colors = paragraph.getColors();
      for (int i = 0; i < msgs.size(); i++) {
        Element messageNode = document.createElement("message");
        String msg = (String)msgs.elementAt(i); //paragraph.getText();
        if (colors.elementAt(i).equals(accentColor) ){
          messageNode.setAttribute("color", "blue");
        }
        if (noteNumber.length() > 0) msg = noteNumber + msg;
        CDATASection messageSection = document.createCDATASection(msg);
        messageNode.appendChild(messageSection);

        //noteElement.appendChild(idNode);
        noteElement.appendChild(messageNode);
      }
    }

    // determine the appropriate type
    String type = "null";
    if (scrObject instanceof Hole) type = "hole";
    else if (scrObject instanceof Frame) type = "frame";
    else if (scrObject instanceof NavigationBar) type = "navBar";
    noteElement.setAttribute("type", type);

    if (type.equals("hole")) {
      boolean autoAdvance = ((Hole)scrObject).getAutoAdvance();
      if (autoAdvance){
        noteElement.setAttribute("autoAdvance", "true");
      } else noteElement.setAttribute("autoAdvance", "false");
      int advanceEvent = ((Hole)scrObject).getAdvanceEvent();
      if (advanceEvent == Hole.ADVANCE_ON_PRESS) {
        noteElement.setAttribute("advanceEvent", "mousePress");
      } else if (advanceEvent == Hole.ADVANCE_ON_CLICK) {
        noteElement.setAttribute("advanceEvent", "mouseClick");
      } else {
        noteElement.setAttribute("advanceEvent", "enterKey");
      }
    }

    if (hasNext) {
      noteElement.setAttribute("hasNext", "true");
    } else noteElement.setAttribute("hasNext", "false");

    // determine the appropriate positions to save
    double x = 0;
    double y = 0;
    if (scrObject != null) {
      //Rectangle rect = scrObject.getRectangle();
      Point holePoint = getHolesPoint();
      if (rect != null) {
        x = clickPos.x - holePoint.getX();
        y = clickPos.y - holePoint.getY();
      }
    } else {
      x = clickPos.x / positionManager.getScreenWidth();
      y = clickPos.y / positionManager.getScreenHeight();
    }
    noteElement.setAttribute("xPos", new Double(x).toString());
    noteElement.setAttribute("yPos", new Double(y).toString());

    element.appendChild(noteElement);
  }

  /* readwrite listener */
  public void setWriteEnabled(boolean enabled){
    writeEnabled = enabled;
    if (isInitialized) this.generateShapes();
    this.setModified();
  }

  /* layout change listener */
  public boolean layoutChanged() {
    if (!(isInitialized)) {
      this.updatePosition();
    }
    if ((line != null) && (scrObject != null) ){
      Point holePoint = getHolesPoint();
      Point wordPoint = getWordsPoint();

      if ((holePoint != null) && (wordPoint != null) ) {
        line.setLine( new Point(holePoint.x, holePoint.y), new Point(wordPoint.x, wordPoint.y) );
        line2.setLine(holePoint.x -1, holePoint.y, wordPoint.x-1, wordPoint.y);
        line3.setLine(holePoint.x +1, holePoint.y, wordPoint.x+1, wordPoint.y);
        line4.setLine(holePoint.x -2, holePoint.y, wordPoint.x-2, wordPoint.y);
        line5.setLine(holePoint.x +2, holePoint.y, wordPoint.x+2, wordPoint.y);

        drawArrow(holePoint, wordPoint);
        ScreenShape sShape = new ScreenShape(Color.red, triangle, true, 6);
        shapes.setElementAt(sShape, 6);
      }
    }
    this.setModified();
    return true;
  }
}