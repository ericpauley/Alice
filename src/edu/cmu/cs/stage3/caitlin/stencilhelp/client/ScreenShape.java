package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

/**
 * <p>Title: ScreenShape</p>
 * <p>Description: ScreenObjects provide a list of ScreenShapes for the
 * StencilPanel to draw </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Caitlin Kelleher
 * @version 1.0
 */

 import java.awt.Color;
 import java.awt.Shape;

public class ScreenShape {
  protected Color color = null;
  protected Shape shape = null;
  protected boolean isFilled = true;
  protected int index = -1;

  public ScreenShape() {}

  public ScreenShape(Color color, Shape shape, boolean isFilled, int index) {
    this.color = color;
    this.shape = shape;
    this.isFilled = isFilled;
    this.index = index;
  }

  public void setColor(Color color) { this.color = color; }
  public Color getColor() { return color; }

  public void setShape(Shape shape) { this.shape = shape; }
  public Shape getShape() { return shape; }

  public void setIsFilled(boolean isFilled) { this.isFilled = isFilled; }
  public boolean getIsFilled() { return isFilled; }

  public int getIndex() { return index; }
}