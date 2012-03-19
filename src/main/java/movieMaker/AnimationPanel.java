package movieMaker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Class to show a frame-based animation Copyright Georgia Institute of
 * Technology 2007
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class AnimationPanel extends JComponent {
	// ///////////// fields /////////////////////

	/** list of image objects */
	private List imageList = new ArrayList();

	/** List of the file names */
	private List nameList = new ArrayList();

	/** index of currently displayed image */
	private int currIndex = 0;

	/** number of frames per second */
	private int framesPerSec = 16;

	// //////////// constructors /////////////////

	/**
	 * Constructor that takes no parameters
	 */
	public AnimationPanel() {
		this.setSize(new Dimension(100, 100));
	}

	/**
	 * Constructor that takes a list of pictures
	 * 
	 * @param pictList
	 *            the list of pictures
	 */
	public AnimationPanel(List pictList) {
		Image image = null;
		Picture picture = null;
		for (int i = 0; i < pictList.size(); i++) {
			picture = (Picture) pictList.get(i);
			nameList.add(picture.getFileName());
			image = picture.getImage();
			imageList.add(image);
		}

		BufferedImage bi = (BufferedImage) image;
		int width = bi.getWidth();
		int height = bi.getHeight();
		this.setSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
	}

	/**
	 * Constructor that takes the directory to read the frames from
	 * 
	 * @param directory
	 *            the directory to read from
	 */
	public AnimationPanel(String directory) {

		// get the list of files in the directory
		File dirObj = new File(directory);
		String[] fileArray = dirObj.list();
		ImageIcon imageIcon = null;
		Image image = null;

		// loop through the files
		for (String element : fileArray) {
			if (element.indexOf(".jpg") >= 0) {

				imageIcon = new ImageIcon(directory + element);
				nameList.add(directory + element);
				imageList.add(imageIcon.getImage());
			}
		}

		// set size of this panel
		if (imageIcon != null) {
			image = (Image) imageList.get(0);
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			this.setSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
			setPreferredSize(new Dimension(width, height));
		}
	}

	/**
	 * Constructor that takes the directory to read from and the number of
	 * frames per second
	 * 
	 * @param directory
	 *            the frame direcotry
	 * @param theFramesPerSec
	 *            the number of frames per second
	 */
	public AnimationPanel(String directory, int theFramesPerSec) {
		this(directory);
		framesPerSec = theFramesPerSec;
	}

	// //////////// methods /////////////////////////

	/**
	 * Method to get the current index
	 * 
	 * @return the current index
	 */
	public int getCurrIndex() {
		return currIndex;
	}

	/**
	 * Method to set the frames per second to show the movie
	 * 
	 * @param numFramesPerSec
	 *            the number of frames to show per second
	 */
	public void setFramesPerSec(int numFramesPerSec) {
		framesPerSec = numFramesPerSec;
	}

	/**
	 * Method to get the frames per second
	 * 
	 * @return the number of frames per second
	 */
	public int getFramesPerSec() {
		return framesPerSec;
	}

	/**
	 * Method to add a picture
	 * 
	 * @param picture
	 *            the picture to add
	 */
	public void add(Picture picture) {
		Image image = picture.getImage();
		imageList.add(image);
		nameList.add(picture.getFileName());
	}

	/**
	 * Method to show just the next frame
	 */
	public void showNext() {
		currIndex++;
		if (currIndex == imageList.size()) {
			currIndex = 0;
		}
		draw(getGraphics());
	}

	/**
	 * Method to show the previous frame
	 */
	public void showPrev() {
		currIndex--;
		if (currIndex < 0) {
			currIndex = imageList.size() - 1;
		}
		draw(getGraphics());
	}

	/**
	 * show all frames starting at 0
	 */
	public void showAll() {
		Graphics g = null;
		long startTime = 0;
		long endTime = 0;
		int timeToSleep = 1000 / framesPerSec;
		for (int i = 0; i < imageList.size(); i++) {
			startTime = System.currentTimeMillis();
			currIndex = i;
			g = getGraphics();
			draw(g);
			g.dispose();
			endTime = System.currentTimeMillis();

			// sleep
			try {
				if (endTime - startTime < timeToSleep) {
					Thread.sleep(timeToSleep - (endTime - startTime));
				}
			} catch (InterruptedException ex) {}
			// reset curr index
			currIndex = imageList.size() - 1;
		}
	}

	/**
	 * show animation from current index
	 */
	public void showAllFromCurrent() {
		Graphics g = null;
		int timeToSleep = 1000 / framesPerSec;
		for (; currIndex < imageList.size(); currIndex++) {
			// draw current image
			g = getGraphics();
			draw(g);
			g.dispose();

			// sleep
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException ex) {}
		}
		// reset curr index
		currIndex = imageList.size() - 1;
	}

	/**
	 * Method to remove all before current from list
	 */
	public void removeAllBefore() {
		File f = null;
		boolean result = false;
		for (int i = 0; i <= currIndex; i++) {
			f = new File((String) nameList.get(i));
			result = f.delete();
			if (result != true) {
				System.out.println("trouble deleting " + nameList.get(i));
			}
			imageList.remove(0);
		}
	}

	/**
	 * Method to remove all after the current index
	 */
	public void removeAllAfter() {
		int i = currIndex + 1;
		int index = i;
		File f = null;
		boolean result = false;
		while (i < imageList.size()) {
			f = new File((String) nameList.get(index++));
			result = f.delete();
			if (result != true) {
				System.out.println("trouble deleting " + nameList.get(index - 1));
			}
			imageList.remove(i);
		}
	}

	/**
	 * Method to paint the frames
	 * 
	 * @param g
	 *            the graphics context to draw to
	 */
	public void draw(Graphics g) {
		Image image = (Image) imageList.get(currIndex);
		g.drawImage(image, 0, 0, this);
	}

	/**
	 * Method to paint the component
	 */

	@Override
	public void paintComponent(Graphics g) {
		if (imageList.size() == 0) {
			g.drawString("No images yet!", 20, 20);
		} else {
			draw(g);
		}
	}

	/**
	 * Method to test
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		AnimationPanel panel = new AnimationPanel("c:/intro-prog-java/mediasources/fish/");
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		panel.showAll();
	}

}
