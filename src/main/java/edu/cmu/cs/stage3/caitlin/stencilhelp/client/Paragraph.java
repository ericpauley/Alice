package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Vector;

public class Paragraph {
	protected int textWidth = 100;
	protected Point textOrigin = new Point(0, 0);

	protected int cursorIndex = 0;
	// protected StringBuffer text = new StringBuffer("");
	protected Shape caretShape = null;
	protected float lineHeight = 0;
	protected double lineAscent = 0;

	protected FontRenderContext frc = null;
	// protected LineBreakMeasurer measurer;
	// protected AttributedCharacterIterator iterator;

	protected StringBuffer currentText = new StringBuffer("");
	protected Vector buffers = new Vector();
	protected Vector colors = new Vector();
	protected Vector iterators = new Vector();
	protected Vector measurers = new Vector();

	private int fontSize = 14;
	// private java.awt.Font font = new Font("Arial",

	public Paragraph() {
		frc = new FontRenderContext(null, false, false);
		buffers.addElement(currentText);
		colors.addElement(Color.blue);
		regenerateLineBreakMeasurer();
	}

	public Paragraph(int textWidth, Point textOrigin) {
		this();
		this.textWidth = textWidth;
		this.textOrigin = textOrigin;
	}

	// set config values
	public void setTextWidth(int textWidth) {
		this.textWidth = textWidth;
	}

	public void setTextOrigin(Point textOrigin) {
		this.textOrigin = textOrigin;
	}

	// setText would probably take a vector of textStrings
	/*
	 * public void setText(String textString) { this.text = new
	 * StringBuffer(textString); regenerateLineBreakMeasurer(); }
	 */

	public void addText(String textString, Color textColor) {
		buffers.addElement(new StringBuffer(textString));
		colors.addElement(textColor);
		currentText = (StringBuffer) buffers.elementAt(0);
		cursorIndex = 0;
		regenerateLineBreakMeasurer();
	}

	public void clearText() {
		buffers.removeAllElements();
		colors.removeAllElements();
		regenerateLineBreakMeasurer();
	}

	public void createNewLine() {
		StringBuffer lastBuffer = new StringBuffer();
		StringBuffer newBuffer = new StringBuffer();

		if (cursorIndex == currentText.length()) {
			lastBuffer = currentText;
			newBuffer = new StringBuffer(" ");
		} else {
			lastBuffer = new StringBuffer(currentText.substring(0, cursorIndex));
			newBuffer = new StringBuffer(currentText.substring(cursorIndex));
		}
		// add the buffers in
		int index = buffers.indexOf(currentText);
		if (index != -1) {
			buffers.setElementAt(lastBuffer, index);
			currentText = newBuffer;
			cursorIndex = 0;
			buffers.insertElementAt(newBuffer, index + 1);

			Color textColor = (Color) colors.elementAt(index);
			colors.insertElementAt(textColor, index + 1);
		}
		regenerateLineBreakMeasurer();
	}

	public void insertChar(char c) {
		if (cursorIndex == currentText.length()) {
			currentText.append(c);
		} else {
			currentText.insert(cursorIndex, c);
		}
		cursorIndex += 1;
		regenerateLineBreakMeasurer();
	}

	// same - need a current textString
	public void deleteChar() {
		int lineIndex = buffers.indexOf(currentText);
		if (cursorIndex > 0) {
			currentText.deleteCharAt(cursorIndex - 1);
			cursorIndex -= 1;
			if (cursorIndex >= currentText.length()) {
				cursorIndex = currentText.length();
			} else if (cursorIndex < 0) {
				cursorIndex = 0;
			}
			regenerateLineBreakMeasurer();
		} else if (cursorIndex == 0 && lineIndex > 0) {
			StringBuffer previousLine = (StringBuffer) buffers.elementAt(lineIndex - 1);
			cursorIndex = previousLine.length();
			previousLine.append(currentText.toString());
			int index = buffers.indexOf(currentText);
			buffers.remove(currentText);
			colors.removeElementAt(index);
			currentText = previousLine;
			regenerateLineBreakMeasurer();
		}
	}

	// vector of text strings
	// vector of iterators
	// vector of measurers
	protected void regenerateLineBreakMeasurer() {
		iterators = new Vector();
		measurers = new Vector();
		for (int i = 0; i < buffers.size(); i++) {
			StringBuffer currBuffer = (StringBuffer) buffers.elementAt(i);
			java.text.AttributedString attrString = new java.text.AttributedString(currBuffer.toString());
			if (currBuffer.toString().length() > 0) {
				attrString.addAttribute(java.awt.font.TextAttribute.SIZE, new Float(fontSize));
				java.awt.Font font = new java.awt.Font("Comic Sans MS", 1, fontSize);
				attrString.addAttribute(java.awt.font.TextAttribute.FONT, font);
			}
			// iterator = attrString.getIterator();
			AttributedCharacterIterator itr = attrString.getIterator();
			iterators.addElement(itr);

			if (itr.getEndIndex() != 0) {
				// measurer = new LineBreakMeasurer(iterator, frc);
				measurers.addElement(new LineBreakMeasurer(itr, frc));
			} else {
				measurers.addElement(null);
			}
		}
	}

	public Shape getCaretShape() {
		return caretShape;
	}

	public int getStartY() {
		return (int) textOrigin.getY() + (int) lineAscent;
	}

	public int getNextY(int currentY) {
		return currentY + (int) lineHeight;
	}

	// this has to loop over the iterators, measurers
	public Vector getShapes() {
		Vector shapes = new Vector();
		double line = 0;
		for (int i = 0; i < iterators.size(); i++) {
			AttributedCharacterIterator itr = (AttributedCharacterIterator) iterators.elementAt(i);
			StringBuffer bfr = (StringBuffer) buffers.elementAt(i); // PROBLEM
			Color color = (Color) colors.elementAt(i);
			int paragraphStart = itr.getBeginIndex();
			int paragraphEnd = itr.getEndIndex();

			LineBreakMeasurer msr = (LineBreakMeasurer) measurers.elementAt(i);
			if (msr != null) {
				msr.setPosition(paragraphStart);
				while (msr.getPosition() < paragraphEnd) {
					int begin = msr.getPosition();
					TextLayout layout = msr.nextLayout(textWidth);
					int end = begin + layout.getCharacterCount();

					// update the lineheight if that's necessary
					lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();
					lineAscent = layout.getAscent();

					// check to see if this is where the caret should be drawn
					// and draw it
					if (currentText == bfr && cursorIndex >= begin && cursorIndex <= end) {
						Shape[] carets = layout.getCaretShapes(cursorIndex - begin);
						AffineTransform at = new AffineTransform();
						at.translate(textOrigin.getX(), getStartY() + line * lineHeight);

						caretShape = at.createTransformedShape(carets[0]);
					}

					AffineTransform at = new AffineTransform();
					at.translate(textOrigin.getX(), getStartY() + line * lineHeight);
					Shape s = layout.getOutline(at);
					ScreenShape sShape = new ScreenShape(color, s, true, 5);
					shapes.addElement(sShape);
					line += 1;
				}
				line = line + 0.5;
			}
		}
		return shapes;
	}

	public Vector getText() {
		Vector strings = new Vector();
		for (int i = 0; i < buffers.size(); i++) {
			String st = ((StringBuffer) buffers.elementAt(i)).toString();
			if (st.length() > 0) {
				strings.addElement(st);
			}
		}
		return strings;
	}

	public Vector getColors() {
		return colors;
	}

	public void updateCaretPosition(Point clickPos) {
		float clickX = (float) (clickPos.getX() - textOrigin.getX());
		float clickY = (float) (clickPos.getY() - textOrigin.getY());

		float bottomBoundary = 0;
		float topBoundary = 0;

		for (int i = 0; i < iterators.size(); i++) {
			AttributedCharacterIterator iterator = (AttributedCharacterIterator) iterators.elementAt(i);
			LineBreakMeasurer measurer = (LineBreakMeasurer) measurers.elementAt(i);

			int paragraphStart = iterator.getBeginIndex();
			int paragraphEnd = iterator.getEndIndex();

			if (measurer != null) {
				measurer.setPosition(paragraphStart);
				// float bottomBoundary = 0;
				// float topBoundary = 0;
			}

			cursorIndex = 0;
			currentText = null;

			// Get lines from lineMeasurer until the entire
			// paragraph has been displayed.
			if (measurer != null) {
				while (measurer.getPosition() < paragraphEnd) {

					// Retrieve next layout.
					TextLayout layout = measurer.nextLayout(textWidth);
					bottomBoundary = topBoundary + lineHeight;

					if (clickY > topBoundary && clickY < bottomBoundary) {
						// Get the character position of the mouse click.
						TextHitInfo currentHit = layout.hitTestChar(clickX, clickY);
						if (currentHit != null) {
							currentText = (StringBuffer) buffers.elementAt(i);
							cursorIndex += currentHit.getInsertionIndex();
							return;
						}
					} else {
						cursorIndex = measurer.getPosition();
					}
					topBoundary = bottomBoundary;
				}
				topBoundary = topBoundary + lineHeight / 2;
			}
		}
		if (currentText == null) {
			currentText = (StringBuffer) buffers.elementAt(0);
			cursorIndex = 0;
		}
	}
}