/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core.behavior;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Controller;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.Renderer;
import javax.media.UnsupportedPlugInException;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;

public class SoundLevelBehavior extends TriggerBehavior implements ControllerListener, Renderer {

	public final edu.cmu.cs.stage3.alice.core.property.NumberProperty level = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "level", new Double(.3));

	private Processor player;
	private DataSource dataSource;
	private javax.media.ControllerErrorEvent m_errorEvent;

	Format inputFormat;
	Format[] inputFormats;

	public SoundLevelBehavior() {
		inputFormats = new Format[]{new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 16, Format.NOT_SPECIFIED, AudioFormat.LITTLE_ENDIAN, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.byteArray)};
		multipleRuntimeResponsePolicy.set(edu.cmu.cs.stage3.alice.core.behavior.MultipleRuntimeResponsePolicy.IGNORE_MULTIPLE);
	}

	@Override
	public void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);

		AudioFormat format = new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 16, 1);
		java.util.Vector captureDeviceList = CaptureDeviceManager.getDeviceList(format);
		if (captureDeviceList.size() <= 0) {
			System.err.println("No Audio Capture Devices Detected");
			return;
		}
		CaptureDeviceInfo captureDevice = (CaptureDeviceInfo) captureDeviceList.firstElement();
		MediaLocator inputLocator = captureDevice.getLocator();
		dataSource = null;
		try {
			dataSource = Manager.createDataSource(inputLocator);// new
																// MediaLocator("file://c:\\Docume~1\\ben\\MyDocu~1\\alien3.wav"));//
		} catch (NoDataSourceException ndse) {
			ndse.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		if (dataSource == null) {
			return;
		}

		player = null;
		try {
			player = Manager.createProcessor(dataSource);
		} catch (NoProcessorException npe) {
			npe.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		if (player == null) {
			return;
		}

		player.addControllerListener(this);
		m_errorEvent = null;
		player.configure();
		while (player.getState() == javax.media.Processor.Configuring && m_errorEvent == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		if (m_errorEvent != null || player.getState() != javax.media.Processor.Configured) {
			player.removeControllerListener(this);
			return;
		}

		player.setContentDescriptor(null);

		try {
			player.getTrackControls()[0].setRenderer(this);
		} catch (UnsupportedPlugInException upie) {
			upie.printStackTrace();
		}

		m_errorEvent = null;
		player.realize();
		while (player.getState() == Controller.Realizing && m_errorEvent == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		player.removeControllerListener(this);
		if (m_errorEvent != null || player.getState() != Controller.Realized) {
			return;
		}

		/*
		 * MediaLocator outputLocator = new
		 * MediaLocator("file://"+Manager.getCacheDirectory()+"\\temp.au");
		 * DataSource outputDataSource = player.getDataOutput(); dataSink =
		 * null; try { dataSink =
		 * Manager.createDataSink(outputDataSource,outputLocator); } catch
		 * (NoDataSinkException ndse) { ndse.printStackTrace(); } if
		 * (dataSink==null) return; dataSink.setOutputLocator(outputLocator);
		 * try { dataSink.open(); } catch (java.io.IOException ioe) {
		 * ioe.printStackTrace(); return; } catch (java.lang.SecurityException
		 * se) { se.printStackTrace(); return; } try { dataSink.start(); } catch
		 * (java.io.IOException ioe) { ioe.printStackTrace(); dataSink.close();
		 * return; }
		 */
		player.start();
	}

	@Override
	public void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.stopped(world, time);
		try {
			player.stop();
			dataSource.stop();
			// this.stop();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		dataSource.disconnect();
		// this.close();
	}

	// *************** implements javax.media.ControllerListner
	// ***********************

	@Override
	public void controllerUpdate(javax.media.ControllerEvent event) {
		if (event instanceof javax.media.ControllerErrorEvent) {
			m_errorEvent = (javax.media.ControllerErrorEvent) event;
		}
	}

	// *************** implements javax.media.Renderer ***********************

	@Override
	public Format[] getSupportedInputFormats() {
		return inputFormats;
	}

	@Override
	public Format setInputFormat(Format format) {
		inputFormat = format;
		return format;
	}

	@Override
	public String getName() {
		return "SoundLevelBehavior";
	}

	@Override
	public void close() {
	}

	@Override
	public void open() {
	}

	@Override
	public void reset() {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public Object[] getControls() {
		return new Object[0];
	}

	@Override
	public Object getControl(String name) {
		return null;
	}

	@Override
	public int process(Buffer input) {
		byte[] inData = (byte[]) input.getData();
		int inOffset = input.getOffset();
		int dataLength = input.getLength();

		int numSamples = dataLength / 2;

		long start_t = input.getTimeStamp() / 1000000;

		for (int i = 0; i < numSamples / ((AudioFormat) input.getFormat()).getChannels(); i++) {
			// Left Channel
			int tempL = inData[inOffset++];
			int tempH = inData[inOffset++];
			int lsample = tempH << 8 | tempL & 255;

			// Right Channel
			int rsample;
			if (((AudioFormat) input.getFormat()).getChannels() == 2) {
				tempL = inData[inOffset++];
				tempH = inData[inOffset++];
				rsample = tempH << 8 | tempL & 255;
			} else {
				rsample = lsample;
			}

			if (lsample / 65535.0 >= level.doubleValue() || rsample / 65535.0 >= level.doubleValue()) {
				trigger(System.currentTimeMillis() * 0.001);
			}
		}

		return BUFFER_PROCESSED_OK;
	}

}