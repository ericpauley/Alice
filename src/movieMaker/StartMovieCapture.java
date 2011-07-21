package movieMaker;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

/**
 * Class that is Runnable to start Movie Capture and stop it Copyright Georgia
 * Institute of Technology 2007
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class StartMovieCapture implements Runnable {
	/** the frame sequencer to use to write out the frames */
	private FrameSequencer frameSequencer = null;

	/** the number of frames per second to capture */
	private int framesPerSecond = 16;

	/** the region to capture */
	private Rectangle region = null;

	/** the active thread */
	private Thread active = null;

	private Rectangle reg = null;
	
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane renderContent = null;

	private AuthoringTool author = null;

	/**
	 * Constructor that takes the frame sequencer, number of frames per second,
	 * and the region to capture
	 * 
	 * @param sequencer
	 *            the frame sequencer
	 * @param framesPerSec
	 *            the number of frames per second
	 * @param area
	 *            the region to capture
	 */
	public StartMovieCapture(
			AuthoringTool a,
			edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane pane,
			FrameSequencer sequencer, int framesPerSec) {
		frameSequencer = sequencer;
		framesPerSecond = framesPerSec;
		author = a;
		renderContent = pane;

	}

	/**
	 * Method to capture a movie until the stop method is called and sets the
	 * active thread to null
	 */
	public void upDateRectangle()
	{
		reg = new Rectangle(1, 1, 1, 1);
		if (renderContent.getRenderPanel() != null)
			reg.setSize(renderContent.getRenderPanel().getSize());
		//TODO: Get rid of black space??
		//Would need to get configured values, which seem to wrong at the moment...
		
		Rectangle r = new Rectangle(1, 1, 1, 1);
		r = renderContent.getRenderPanelLocation() ;
		reg.y = r.y+3;
		int buttonPanelWidth = renderContent.getButtonPanel().getWidth();
		reg.x=r.x+(buttonPanelWidth-reg.width)/2;
		reg.height-=2;
	}
	
	public void captureMovie() {
		long startTime = 0;
		long endTime = 0;
		int timeToSleep = (1000 / framesPerSecond);
		
		Thread current = Thread.currentThread();
		
		upDateRectangle();
		while (current == active && renderContent.getEnd()) {
					
			if (author.getWorld().isRunning() && renderContent.getRunning()) {
				startTime = System.currentTimeMillis();
				try {
					upDateRectangle();
					BufferedImage bi = new Robot().createScreenCapture(reg);
					author.getSoundStorage().frameList.add(new Long(startTime));
					frameSequencer.addFrame(new Picture(bi));
					endTime = System.currentTimeMillis();
					if (endTime - startTime < timeToSleep)
						Thread.sleep(timeToSleep - (endTime - startTime));
				} catch (Exception ex) {
					System.err.println("Caught exception in StartMovieCapture");
					//done = true;
				}
			}
		}
	}

	/**
	 * Method to start the thread
	 */
	public void run() {
		active = Thread.currentThread();
		captureMovie();
	}

	/**
	 * Method to stop the thread
	 */
	public void stop() {
		active = null;
	}
}
