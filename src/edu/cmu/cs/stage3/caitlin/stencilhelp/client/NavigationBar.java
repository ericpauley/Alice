package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class NavigationBar implements StencilObject, MouseEventListener, StencilStackChangeListener,
  LayoutChangeListener {
  protected StencilManager stencilManager = null;
  protected ObjectPositionManager positionManager = null;
  protected Vector shapes = new Vector();
  protected boolean isModified = true;
  protected Rectangle previousRect = null;

  private RoundRectangle2D.Double bgBar = null;
  private RoundRectangle2D.Double underBar = null;
  private RoundRectangle2D.Double bgClose = null;
  private RoundRectangle2D.Double underClose = null;
  private RoundRectangle2D.Double bgRestart = null;
  private RoundRectangle2D.Double underRestart = null;
  private RoundRectangle2D.Double bgNext = null;
  private RoundRectangle2D.Double underNext = null;
  private RoundRectangle2D.Double bgPrev = null;
  private RoundRectangle2D.Double underPrev = null;

  private Polygon forwardArrow = null;
  private Polygon backArrow = null;
  private Font font = new Font("Arial", 1, 16);

  private String titleString = null;
  private ScreenShape titleShape = null;
  private ScreenShape xShape = null;
  private ScreenShape nextShape = null;
  private ScreenShape prevShape = null;
  private ScreenShape restartShape = null;

  private boolean isError = false;

  protected Vector stencilObjectPositionListeners = new Vector();

  public NavigationBar(StencilManager stencilManager, ObjectPositionManager positionManager) {
    this.stencilManager = stencilManager;
    this.positionManager = positionManager;
    generateShapes();
  }

  public NavigationBar(StencilManager stencilManager, ObjectPositionManager positionManager, boolean isError) {
    this.isError = isError;
    this.stencilManager = stencilManager;
    this.positionManager = positionManager;
    generateShapes();
  }

  public void setTitleString(String titleString) {
    this.titleString = titleString;
    updateNavBar();
  }

  public String getTitleString() {
    return titleString;
  }

  protected void createArrows(Point topCenter, int width) {
    ScreenShape scrShape = null;
    width = 80;
    backArrow = new Polygon();
    backArrow.addPoint(topCenter.x - 133 + 5, 13 + 23);
    backArrow.addPoint(topCenter.x - 133 + 15, 3 + 23);
    backArrow.addPoint(topCenter.x - 133 + 15, 9 + 23);
    backArrow.addPoint(topCenter.x - 133 + 23, 9 + 23);
    backArrow.addPoint(topCenter.x - 133 + 23, 17 + 23);
    backArrow.addPoint(topCenter.x - 133 + 15, 17 + 23);
    backArrow.addPoint(topCenter.x - 133 + 15, 23 + 23);

    if (stencilManager.hasPrevious()) {
      scrShape = new ScreenShape(Color.blue, backArrow, true, 10);
    } else {
      scrShape = new ScreenShape(Color.lightGray, backArrow, true, 10);
    }
    shapes.addElement(scrShape);

    forwardArrow = new Polygon();
    forwardArrow.addPoint(topCenter.x + width/2 + 25 - 5, 13 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 15, 3 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 15, 9 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 23, 9 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 23, 17 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 15, 17 + 23);
    forwardArrow.addPoint(topCenter.x + width/2 + 25- 15, 23 + 23);

    if ( !(isError) && (stencilManager.hasNext()) ){
      scrShape = new ScreenShape(Color.blue, forwardArrow, true, 11);
    } else {
      scrShape = new ScreenShape(Color.lightGray, forwardArrow, true, 11);
    }
    shapes.addElement(scrShape);
  }

   protected int createShape(Point topCenter) { // returns the width of the text
      // save the current rectangle
      previousRect = this.getRectangle();

      Shape s = null;
      int width = 0;
      String word = "Page " + (stencilManager.getStencilNumber()+1) + " of " + stencilManager.getNumberOfStencils();
      if (titleString != null) word += ": " + titleString;
      String close = "Exit Tutorial";

      TextLayout wordLayout = new TextLayout(word, font, new FontRenderContext(null, false, false));
      AffineTransform textAt = new AffineTransform();
      width = (int)wordLayout.getBounds().getWidth()/2;
      textAt.translate(topCenter.x - width, topCenter.y + 18);
      s = wordLayout.getOutline(textAt);
      titleShape = new ScreenShape(new Color(0,0,180), s, true, 6);
      // this is a little ugly, may want to make more clear

      wordLayout = new TextLayout(close, font, new FontRenderContext(null, false, false));
      textAt = new AffineTransform();
      textAt.translate(topCenter.x + width  + 45, topCenter.y + 18);
      s = wordLayout.getOutline(textAt);
      xShape = new ScreenShape(new Color(0,0,180), s, true, 7); // temp

      wordLayout = new TextLayout("next", font, new FontRenderContext(null, false, false));
      textAt = new AffineTransform();
      textAt.translate(topCenter.x + 7, topCenter.y + 43);
      s = wordLayout.getOutline(textAt);
      nextShape = new ScreenShape(new Color(0,0,180), s, true, 8); // temp

      wordLayout = new TextLayout("back", font, new FontRenderContext(null, false, false));
      textAt = new AffineTransform();
      textAt.translate(topCenter.x + -110, topCenter.y + 43);
      s = wordLayout.getOutline(textAt);
      prevShape = new ScreenShape(new Color(0,0,180), s, true, 8); // temp

      wordLayout = new TextLayout("restart", font, new FontRenderContext(null, false, false));
      textAt = new AffineTransform();
      textAt.translate(topCenter.x - wordLayout.getBounds().getWidth() - 10, topCenter.y + 43);
      s = wordLayout.getOutline(textAt);
      restartShape = new ScreenShape(new Color(0,0,180), s, true, 9); // temp
      isModified = true;
      return width * 2;
  }

  protected void generateShapes() {
    // save the current rectangle
    previousRect = this.getRectangle();

    Point topCenter = new Point((int)positionManager.getScreenWidth() / 2, 0);
    int width = createShape(topCenter) + 60;

    //underBar = new RoundRectangle2D.Double(topCenter.x - 100+2, topCenter.y+2, 200,25, 10, 10);
    //underClose = new RoundRectangle2D.Double(topCenter.x + 110+2, topCenter.y+2, 20 ,25, 10, 10);

    underBar = new RoundRectangle2D.Double(topCenter.x - width/2 +2, topCenter.y+2, width, 25, 10, 10);
    underClose = new RoundRectangle2D.Double(topCenter.x + width/2 + 10 +2, topCenter.y+2, 100 ,25, 10, 10);
    underNext = new RoundRectangle2D.Double(topCenter.x + 2, topCenter.y+2 + 27, 60 ,20, 10, 10);
    underRestart = new RoundRectangle2D.Double(topCenter.x + 3 - 65, topCenter.y+2 + 27, 60 ,20, 10, 10);
    underPrev = new RoundRectangle2D.Double(topCenter.x + 3 - 130, topCenter.y+2 + 27, 60 ,20, 10, 10);

    Color transGray = new Color(255,200,240, 100);
    ScreenShape scrShape = new ScreenShape(transGray, underBar, true, 0);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(transGray, underClose, true, 1);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(transGray, underNext, true, 2);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(transGray, underRestart, true, 3);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(transGray, underPrev, true, 4);
    shapes.addElement(scrShape);

    bgBar = new RoundRectangle2D.Double(topCenter.x - width/2, topCenter.y, width, 25, 10, 10);
    bgClose = new RoundRectangle2D.Double(topCenter.x + width/2 + 10, topCenter.y, 100, 25, 10, 10);
    bgNext = new RoundRectangle2D.Double(topCenter.x, topCenter.y + 27, 60 ,20, 10, 10);
    bgRestart = new RoundRectangle2D.Double(topCenter.x - 65, topCenter.y + 27, 60 ,20, 10, 10);
    bgPrev = new RoundRectangle2D.Double(topCenter.x - 130, topCenter.y + 27, 60 ,20, 10, 10);

    scrShape = new ScreenShape(new Color(255,180,210,220), bgBar, true, 5);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(new Color(255,180,210,220), bgClose, true, 6);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(new Color(255,180,210,220), bgNext, true, 7);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(new Color(255,180,210,220), bgRestart, true, 8);
    shapes.addElement(scrShape);
    scrShape = new ScreenShape(new Color(255,180,210,220), bgPrev, true, 9);
    shapes.addElement(scrShape);

    createArrows(topCenter, width - 60);
    //insert the text shapes last
    if (shapes.size() > 12) {
        shapes.setElementAt(titleShape, 12);
    } else {
        shapes.addElement(titleShape);
    }
    if (shapes.size() > 13) {
        shapes.setElementAt(xShape, 13);
    } else {
        shapes.addElement(xShape);
    }
    if (shapes.size() > 14) {
        shapes.setElementAt(nextShape, 14);
    } else {
        shapes.addElement(nextShape);
    }
    if (shapes.size() > 15) {
        shapes.setElementAt(prevShape, 15);
    } else {
        shapes.addElement(prevShape);
    }
    if (shapes.size() > 16) {
        shapes.setElementAt(restartShape, 16);
    } else {
        shapes.addElement(restartShape);
    }

    isModified = true;
  }

  /* stencil object stuff */
  public Vector getShapes(){
    return shapes;
  }
  public Rectangle getRectangle(){
    if (bgBar != null) {
      return new Rectangle((int)bgBar.getBounds().getX(), (int)bgBar.getBounds().getY(),
        (int)bgBar.getBounds().getWidth() + (int)bgClose.getBounds().getWidth(),
        (int)bgBar.getBounds().getHeight() + (int)bgNext.getBounds().getHeight());
    } else return null;
  }
  public Rectangle getPreviousRectangle(){
    return previousRect;
  }
  public boolean isModified(){
    if (isModified) {
      isModified = false;
      return true;
    } else return false;
  }
  public boolean intersectsRectangle( Rectangle rect ){
    return (rect.intersects(this.getRectangle()));
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
  public Point getNotePoint() {
    Rectangle r = this.getRectangle();
    Point p = new Point(0,0);
    if (r != null) {
      p = new Point( (int)r.getX(), (int)r.getY() );
    }
    return p;
  }

  /* mouse event stuff */
  public boolean contains( Point point ){
    if (( bgBar.contains(point.getX(), point.getY()) ) || ( bgClose.contains(point.getX(), point.getY()) )
     || (bgNext.contains(point.getX(), point.getY())) || (bgRestart.contains(point.getX(), point.getY())
     || (bgPrev.contains(point.getX(), point.getY())) ) ) {
      return true;
    } else return false;
  }
  public boolean mousePressed(MouseEvent e){
    return false;
  }
  public boolean mouseReleased(MouseEvent e){
    return false;
  }
  public boolean mouseClicked(MouseEvent e){
    if ( (bgNext.contains(e.getPoint())) && (stencilManager.hasNext()) ){
      stencilManager.showNextStencil();
    } else if ( bgRestart.contains(e.getPoint())) { //( (bgRestart.contains(e.getPoint())) && (stencilManager.hasPrevious()) ) {
      stencilManager.reloadStencils();
    } else if ( bgClose.contains(e.getPoint()) ) {
      stencilManager.showStencils(false);
    } else if ( (bgPrev.contains(e.getPoint())) && (stencilManager.hasPrevious()) ){
      stencilManager.showPreviousStencil();
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

  /* stencil stack change stuff */
  protected void updateNavBar() {
    /*if (stencilManager.hasNext()) {
      ScreenShape s = (ScreenShape)shapes.elementAt(5);
      s.setColor(Color.blue);
    } else {
      ScreenShape s = (ScreenShape)shapes.elementAt(5);
      s.setColor(Color.lightGray);
    }
    if (stencilManager.hasPrevious()){
      ScreenShape s = (ScreenShape)shapes.elementAt(4);
      s.setColor(Color.blue);
    } else {
      ScreenShape s = (ScreenShape)shapes.elementAt(4);
      s.setColor(Color.lightGray);
    }
    Point topCenter = new Point((int)positionManager.getScreenWidth() / 2, 0);
    createShape(topCenter);*/
    shapes = new Vector();
    generateShapes();
  }
  public void numberOfStencilsChanged(int newNumberOfStencils){
    updateNavBar();
  }
  public void currentStencilChanged(int selectedStencil){
    updateNavBar();
  }

  /* layout change listener stuff */
  public boolean layoutChanged(){
    //COME BACK TO ME
    return true;
  }
}