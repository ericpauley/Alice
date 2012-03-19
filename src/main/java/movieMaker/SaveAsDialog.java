/*
 * @(#)SaveAsDialog.java	1.13 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package movieMaker;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Duration;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.MonitorControl;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

import jmapps.ui.ImageArea;
import jmapps.ui.JMDialog;
import jmapps.ui.JMPanel;
import jmapps.ui.MessageDialog;
import jmapps.ui.ProgressDialog;
import jmapps.ui.ProgressThread;
import jmapps.util.JMAppsCfg;

import com.sun.media.ui.TabControl;
import com.sun.media.util.JMFI18N;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

/**
 * This class is used to create modeless SaveAs dialog. Instanciating the class
 * opens the dialog. It uses TabControl to allow user specify the parameters for
 * each audio and video track. Classes TrackPanelAudio and TrackPanelVideo are
 * used to compose the pages of the TabControl. When user presses "OK" button it
 * calls system FileDialog. After specifying the target file, it creates
 * Progress dialog, that allows to monitor the progress of saving media to the
 * file, pause, resume and abort the process.
 */
public class SaveAsDialog extends JMDialog implements ControllerListener, DataSinkListener, ItemListener {

	private JMAppsCfg cfgJMApps;
	private String inputURL;
	private DataSource dataSource = null;
	private Processor processor = null;
	private DataSink dataSink = null;
	private TrackControl arrTrackControls[];
	private int nAudioTrackCount = 0;
	private String strContentType = null;
	// private String strContentTypeExt = null;
	private boolean boolSaving = false;
	private ProgressDialog dlgProgress = null;
	private ProgressThread threadProgress = null;
	// private Format captureFormat = null;

	private TabControl tabControl;
	private Hashtable hashtablePanelsAudio = new Hashtable();
	private Choice comboContentType;

	private Image imageAudioEn = null;
	private Image imageAudioDis = null;
	private String strFailMessage = null;

	// private ContentDescriptor [] supportedCDs;

	/**
	 * This constructor creates object SaveAsDialog, fills it with controls does
	 * the layout, displays it on the screen, and returns. The dialog stays on
	 * the screen untill user presses button "OK" or "Cancel".
	 * 
	 * @param frame
	 *            parent frame
	 * @param inputURL
	 *            source of the media
	 * @param format
	 *            possible capture format
	 */
	public SaveAsDialog(Frame frame, String inputURL, Format format, JMAppsCfg cfgJMApps) {
		super(frame, JMFI18N.getResource("jmstudio.saveas.title"), false);

		this.cfgJMApps = cfgJMApps;
		this.inputURL = inputURL;
		// this.captureFormat = format;

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This constructor creates object SaveAsDialog, fills it with controls does
	 * the layout, displays it on the screen, and returns. The dialog stays on
	 * the screen untill user presses button "OK" or "Cancel".
	 * 
	 * @param frame
	 *            parent frame
	 * @param inputURL
	 *            source of the media
	 * @param format
	 *            possible capture format
	 */
	public SaveAsDialog(Frame frame, DataSource dataSource, JMAppsCfg cfgJMApps) {
		super(frame, JMFI18N.getResource("jmstudio.saveas.title"), false);

		this.cfgJMApps = cfgJMApps;
		this.dataSource = dataSource;
		inputURL = "Capture";

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is called from the constructor. It performs all required
	 * initialization, creates all controls, does the layout and puts the dialog
	 * on the screen.
	 * 
	 * @exception Exception
	 */
	private void init() throws Exception {
		int i;
		Panel panel;
		JMPanel panelBorder;
		MediaLocator mediaSource;
		Format format;
		boolean boolResult;
		Dimension dim;

		imageAudioEn = ImageArea.loadImage("audio.gif", this, true);
		imageAudioDis = ImageArea.loadImage("audio-disabled.gif", this, true);

		frameOwner.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		if (dataSource == null) {
			try {
				mediaSource = new MediaLocator(inputURL);
				dataSource = Manager.createDataSource(mediaSource);
			} catch (Exception exception) {
				AuthoringTool.showErrorDialog("Alice has encountered an error", exception);
				frameOwner.setCursor(Cursor.getDefaultCursor());
				throw exception;
			}
		}

		strContentType = dataSource.getContentType();
		try {
			processor = Manager.createProcessor(dataSource);
		} catch (NoPlayerException exception) {
			AuthoringTool.showErrorDialog("Alice has encountered an error", exception);
			frameOwner.setCursor(Cursor.getDefaultCursor());
			throw exception;
		}
		processor.addControllerListener(this);

		// wait for processor to be configured
		boolResult = waitForState(processor, Processor.Configured);
		if (boolResult == false) {
			frameOwner.setCursor(Cursor.getDefaultCursor());
			return;
		}

		arrTrackControls = processor.getTrackControls();
		for (i = 0; i < arrTrackControls.length; i++) {
			format = arrTrackControls[i].getFormat();
			if (format instanceof AudioFormat) {
				nAudioTrackCount++;
			}
		}

		setLayout(new BorderLayout());

		panelBorder = new JMPanel(new BorderLayout(6, 6));
		panelBorder.setEmptyBorder(6, 6, 6, 6);
		panelBorder.setBackground(Color.lightGray);
		this.add(panelBorder, BorderLayout.CENTER);

		panel = createPanelGeneral();
		panelBorder.add(panel, BorderLayout.NORTH);

		panel = createPanelProperties();
		panelBorder.add(panel, BorderLayout.CENTER);

		changeContentType();

		pack();
		dim = this.getSize();
		dim.width += 64;
		this.setSize(dim);

		addWindowListener(this);
		setResizable(false);
		// this.setVisible ( true );

		doSave();
		frameOwner.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * This method is used by method init() to create the panel that contains
	 * TabControl and its pages TrackPanelAudio or TrackPanelVideo for each
	 * track.
	 * 
	 * @return created panel
	 * @exception Exception
	 */
	private Panel createPanelProperties() throws Exception {
		int i;
		int nCount;

		TrackPanelAudio panelAudio;
		Format format;
		String strAudio = "Audio";

		JMAppsCfg.TrackData dataTrack;

		tabControl = new TabControl(TabControl.ALIGN_TOP);

		nCount = arrTrackControls.length;
		for (i = 0; i < nCount; i++) {
			format = arrTrackControls[i].getFormat();
			if (format instanceof AudioFormat) {
				panelAudio = new TrackPanelAudio(arrTrackControls[i], this);
				tabControl.addPage(panelAudio, strAudio, imageAudioEn);
				hashtablePanelsAudio.put(strAudio, panelAudio);

				if (cfgJMApps != null) {
					dataTrack = cfgJMApps.getLastSaveFileTrackData(strAudio);
					if (dataTrack != null) {
						panelAudio.setDefaults(dataTrack.boolEnable, dataTrack.format);
					}
				}
			}

		}

		return tabControl;
	}

	/**
	 * This method is used by method init() to create the panel that contains
	 * the choice of the media type for output.
	 * 
	 * @return created panel
	 * @exception Exception
	 */
	private Panel createPanelGeneral() throws Exception {
		Panel panelGeneral;
		Panel panelFormat;
		Label label;

		panelGeneral = new Panel(new GridLayout(0, 1, 4, 4));

		panelFormat = new Panel(new BorderLayout());
		panelGeneral.add(panelFormat);

		label = new Label("Format:");
		panelFormat.add(label, BorderLayout.WEST);

		label = new Label("Wave(wav)");
		panelFormat.add(label, BorderLayout.CENTER);

		return panelGeneral;
	}

	/**
	 * This method is called, when the user presses button "OK". It opens system
	 * FileDialog. After the user chooses the target file it initiates the
	 * saving process, puts the Progress dialog on the screen, and launches the
	 * thread to monitor the progress and update the Progress dialog.
	 */
	private void doSave() {
		int i;
		MediaLocator mediaDest;
		DataSource dataSource;
		Object arrControls[];
		MonitorControl monitorControl;
		boolean boolResult;
		String strFileContentType = null;
		AudioFormat formatAudio;
		FileDialog dlgFile;
		String strDirName = null;
		String strFileName = null;
		Enumeration enumKeys;
		String strPanel;
		TrackPanelAudio panelAudio;
		int nMediaDuration;
		Component monitor = null;
		String strValue;
		TrackControl trackControl;
		JMAppsCfg.TrackData dataTrack;

		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			// configure processor
			processor.setContentDescriptor(new FileTypeDescriptor(strContentType));

			// go through parameters ...
			enumKeys = hashtablePanelsAudio.keys();
			while (enumKeys.hasMoreElements()) {
				strPanel = (String) enumKeys.nextElement();
				panelAudio = (TrackPanelAudio) hashtablePanelsAudio.get(strPanel);
				panelAudio.updateTrack();

				if (cfgJMApps != null) {
					dataTrack = cfgJMApps.createTrackDataObject();
					trackControl = panelAudio.getTrackControl();
					dataTrack.boolEnable = trackControl.isEnabled();
					dataTrack.format = trackControl.getFormat();
					cfgJMApps.setLastSaveFileTrackData(dataTrack, strPanel);
				}
			}

			boolResult = waitForState(processor, Controller.Realized);
			if (boolResult == false) {
				setCursor(Cursor.getDefaultCursor());
				processor.close();
				dispose();
				return;
			}

			dataSource = processor.getDataOutput();

			inputURL = inputURL.substring(0, inputURL.lastIndexOf(".")) + "a.wav";
			mediaDest = new MediaLocator(inputURL);
			dataSink = Manager.createDataSink(dataSource, mediaDest);
			boolSaving = true;
			monitorControl = (MonitorControl) processor.getControl("javax.media.control.MonitorControl");
			if (monitorControl != null) {
				monitor = monitorControl.getControlComponent();
			}

			Time duration = processor.getDuration();
			nMediaDuration = (int) duration.getSeconds();

			dataSink.addDataSinkListener(this);
			try {
				dataSink.open();
			} catch (Exception e) {
				processor.close();
				throw e;
			}
			dataSink.start();
			processor.start();

			if (nMediaDuration > 0 && duration != Duration.DURATION_UNBOUNDED && duration != Duration.DURATION_UNKNOWN) {
				dlgProgress = new ProgressDialog(frameOwner, JMFI18N.getResource("jmstudio.saveprogress.title"), 0, nMediaDuration, this);
			} else {
				dlgProgress = new ProgressDialog(frameOwner, JMFI18N.getResource("jmstudio.saveprogress.title"), JMFI18N.getResource("jmstudio.saveprogress.label"), monitor, this);
			}
			// dlgProgress.setVisible ( true );

			threadProgress = new ProgressThread(processor, dlgProgress);
			threadProgress.start();

		} catch (Exception exception) {
			boolSaving = false;
			AuthoringTool.showErrorDialog("Alice has encountered an error. ", exception);
		}

		setCursor(Cursor.getDefaultCursor());
		dispose();
	}

	/**
	 * This method overwrites the ActionListener method to process events from
	 * buttons, track pages, and Progress dialog.
	 * 
	 * @param event
	 *            action event
	 */

	@Override
	public void actionPerformed(ActionEvent event) {
		String strCmd;
		Object objectSource;

		strCmd = event.getActionCommand();
		if (strCmd.equals(ACTION_CANCEL)) {
			stopSaving();
			dispose();
		} else if (strCmd.equals(ACTION_SAVE)) {
			doSave();
		} else if ((strCmd.equals(ProgressDialog.ACTION_ABORT) || strCmd.equals(ProgressDialog.ACTION_STOP)) && boolSaving == true) {
			stopSaving();
		} else if (strCmd.equals(ProgressDialog.ACTION_PAUSE) && boolSaving == true) {
			processor.stop();
			dlgProgress.setPauseButtonText(ProgressDialog.ACTION_RESUME);
			threadProgress.pauseThread();
		} else if (strCmd.equals(ProgressDialog.ACTION_RESUME) && boolSaving == true) {
			processor.start();
			dlgProgress.setPauseButtonText(ProgressDialog.ACTION_PAUSE);
			threadProgress.resumeThread();
		} else if (strCmd.equals(AudioFormatChooser.ACTION_TRACK_ENABLED)) {
			objectSource = event.getSource();
			if (objectSource instanceof TrackPanelAudio) {
				tabControl.setPageImage((Panel) objectSource, imageAudioEn);
			}
		} else if (strCmd.equals(AudioFormatChooser.ACTION_TRACK_DISABLED)) {
			objectSource = event.getSource();
			if (objectSource instanceof TrackPanelAudio) {
				tabControl.setPageImage((Panel) objectSource, imageAudioDis);
			}
		}
	}

	/**
	 * This method overwrites the ItemListener method to monitor the users
	 * choice of the media type, and notify track pages about the change.
	 * 
	 * @param event
	 *            item state changed event
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		Object objectSource;

		objectSource = event.getSource();
		if (objectSource == comboContentType) {
			changeContentType();
		}
	}

	/**
	 * If the user closes dialog using system menu, it does the cleanup.
	 * 
	 * @param event
	 *            window event
	 */

	@Override
	public void windowClosing(WindowEvent event) {
		stopSaving();
		dispose();
	}

	/**
	 * This method looks for ControllerErrorEvent, and displays the Error
	 * dialog.
	 * 
	 * @param event
	 *            controller event
	 */
	@Override
	public void controllerUpdate(ControllerEvent event) {
		if (event instanceof ControllerErrorEvent) {
			strFailMessage = ((ControllerErrorEvent) event).getMessage();

			if (boolSaving == true) {
				stopSaving();
				MessageDialog.createErrorDialogModeless(frameOwner, JMFI18N.getResource("jmstudio.error.processor.savefile") + "\n" + JMFI18N.getResource("jmstudio.error.controller") + "\n" + strFailMessage);
			} else {
				MessageDialog.createErrorDialogModeless(frameOwner, JMFI18N.getResource("jmstudio.error.controller") + "\n" + strFailMessage);
			}
		} else if (event instanceof EndOfMediaEvent) {
			if (boolSaving == true) {
				stopSaving();
			}
		}
	}

	/**
	 * This method monitors the process of saving file for end of file, and
	 * possible errors. It also does a cleanup, when one of those events occurs.
	 * 
	 * @param event
	 *            file write event
	 */
	@Override
	public void dataSinkUpdate(DataSinkEvent event) {
		if (event instanceof EndOfStreamEvent) {
			closeDataSink();
		} else if (event instanceof DataSinkErrorEvent) {
			stopSaving();
			MessageDialog.createErrorDialogModeless(frameOwner, JMFI18N.getResource("jmstudio.error.processor.writefile"));
		}
	}

	private void closeDataSink() {
		synchronized (this) {
			if (dataSink != null) {
				dataSink.close();
			}
			dataSink = null;
		}
	}

	/**
	 * This method cleans up after the completion of the file save procedure.
	 */
	private void stopSaving() {
		boolSaving = false;

		if (threadProgress != null) {
			threadProgress.terminateNormaly();
			threadProgress = null;
		}
		if (processor != null) {
			processor.stop();
			processor.close();
		}
		if (dlgProgress != null) {
			dlgProgress.dispose();
			dlgProgress = null;
		}
	}

	/**
	 * This method waits untill the processor enter the specified state, or some
	 * failure occurs.
	 * 
	 * @param nState
	 *            the state of processor (see Processor and Player constants)
	 * @return true if the state was reached, false otherwise
	 */

	Object stateLock = new Object();
	boolean stateFailed = false;

	private synchronized boolean waitForState(Processor p, int state) {
		p.addControllerListener(new StateListener());
		stateFailed = false;

		if (state == Processor.Configured) {
			p.configure();
		} else if (state == Controller.Realized) {
			p.realize();
		}

		while (p.getState() < state && !stateFailed) {
			synchronized (stateLock) {
				try {
					stateLock.wait();
				} catch (InterruptedException ie) {
					return false;
				}
			}
		}

		return !stateFailed;
	}

	/**
	 * This method is called whenever user makes the choice of the target media
	 * type. It notifies all track pages about the change.
	 */
	private void changeContentType() {
		Enumeration enumPanels;
		TrackPanelAudio panelAudio;

		strContentType = FileTypeDescriptor.WAVE;
		// strContentTypeExt = "wav";

		if (processor.setContentDescriptor(new FileTypeDescriptor(strContentType)) == null) {
			System.err.println("Error setting content descriptor on processor");
		}

		enumPanels = hashtablePanelsAudio.elements();
		while (enumPanels.hasMoreElements()) {
			panelAudio = (TrackPanelAudio) enumPanels.nextElement();
			panelAudio.setContentType(strContentType);
		}

	}

	class StateListener implements ControllerListener {

		@Override
		public void controllerUpdate(ControllerEvent ce) {
			if (ce instanceof ControllerClosedEvent) {
				stateFailed = true;
			}
			if (ce instanceof ControllerEvent) {
				synchronized (stateLock) {
					stateLock.notifyAll();
				}
			}
		}
	}

}
