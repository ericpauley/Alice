package movieMaker;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
//import javax.media.DataSink;
//import javax.media.Duration;
//import javax.media.Manager;
import javax.media.MediaLocator;
//import javax.media.NoPlayerException;
//import javax.media.Processor;
//import javax.media.Time;
//import javax.media.control.MonitorControl;
//import javax.media.protocol.DataSource;
//import javax.media.protocol.FileTypeDescriptor;

//import movieMaker.SaveAsDialog.StateListener;

//import jmapps.ui.MessageDialog;
//import jmapps.ui.ProgressDialog;
//import jmapps.ui.ProgressThread;

//import com.sun.media.util.JMFI18N;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

public class SoundStorage {
	 
	private ArrayList soundList = new ArrayList();

	public ArrayList frameList = new ArrayList();
	
	private boolean listening = false;
	
	private double totalLength =0.0;
	
	private Vector startCaptureTimes = new Vector();

	private Vector stopCaptureTimes = new Vector();

	//private boolean error = true;
	
	public void add(Long start, Double len,
			edu.cmu.cs.stage3.media.jmfmedia.DataSource ds, Object to,
			Object from, Object rate, Object volume) {
	 	soundList.add(new SoundData(start, len, ds, to, from,
				rate, volume));
	}

	public ArrayList getList() {
		return soundList;
	}
	
	public void setListening(boolean isListening, double time) {
		listening = isListening;

		if (listening)
			startCaptureTimes.add(new Double(time));
		else
			{
			stopCaptureTimes.add(new Double(time));
			totalLength += time-((Double)startCaptureTimes.get(startCaptureTimes.size()-1)).doubleValue();
			}
	}

	public void convertTimes(){

		long startNum = ((Long)frameList.get(0)).longValue();
		
		for(int x = 0; x<frameList.size(); x++){
			frameList.set(x, new Long(((Long)frameList.get(x)).longValue()-startNum));
		}
		
		for(int x = 0; x<soundList.size(); x++){
			((SoundData)soundList.get(x)).worldTime-=startNum;
		}
		
		for(int x =0; x<soundList.size(); x++){
			int index=0;
			long time1, time2;
				while(index<frameList.size() &&((Long)frameList.get(index)).longValue()<((SoundData)soundList.get(x)).worldTime){
					index++;
				}			
			if(index==frameList.size()){
					continue;
			}
			
			time2=((Long)frameList.get(index)).longValue();
			time1=((Long)frameList.get((index==0 ? 0: index-1))).longValue();
			//System.err.println("Time1 : " + time1+ " Time2 : " + time2);
			if(time1==time2){
				((SoundData)soundList.get(x)).worldTime = ((SoundData)soundList.get(x)).worldTime/1000.0;
			} else {	
				double percentage = (((SoundData)soundList.get(x)).worldTime - time1)/(time2-time1);
					((SoundData)soundList.get(x)).worldTime=((index-1) + percentage)/16.0;
			}
		}
	}
	
	public void convertCaptureTimes(){
		double start = ((Double)startCaptureTimes.get(0)).doubleValue();
		for(int x = 0; x<startCaptureTimes.size();x++){
			startCaptureTimes.set(x,new Double(((Double)startCaptureTimes.get(x)).doubleValue()-start));
			stopCaptureTimes.set(x,new Double(((Double)stopCaptureTimes.get(x)).doubleValue()-start));
			//print("Captures Times" + startCaptureTimes.get(x) + " " + stopCaptureTimes.get(x));
		}
		
	}
	
	public void convertNumbers(double length)
	{
		///convert Down by percentage
		for(int x = 0;x<soundList.size();x++)
		{
			SoundData sd = (SoundData)soundList.get(x);
			sd.worldTime*=length/totalLength;
			sd.duration*=length/totalLength;
			sd.clippedDuration *=length/totalLength;
		}
		
		for(int x =0;x<startCaptureTimes.size();x++){
			double d = ((Double)startCaptureTimes.get(x)).doubleValue();
			startCaptureTimes.set(x,new Double(d*length/totalLength));
			d = ((Double)stopCaptureTimes.get(x)).doubleValue();
			stopCaptureTimes.set(x,new Double(d*length/totalLength));
		}
		
	}
	Object stateLock = new Object();
	boolean stateFailed = false;

	public Vector encodeFiles(double length, String exportDirectory) // get
	{
		Vector newDS = new Vector();

		String 	orig_file = "", 
				final_sound = "", 
				sound_slice = "", 
				sound_cut = "",
				track_file = "",
				silence = exportDirectory + "silence.wav";

		int currentChunk = 0;
		int currentLength = 0;

		convertTimes();
		convertCaptureTimes();
		//print("size of length " + soundList.size());

		for (int y = 0; y < soundList.size(); y++) {

			double blankLength = 0;
			SoundData sd = (SoundData)soundList.get(y);

			if ((sd.duration < sd.clippedDuration) || (sd.clippedDuration == 0.0))
				sd.clippedDuration = sd.duration;
			
			sd.stopTime = sd.duration;

			
			while (sd.worldTime > ((Double)stopCaptureTimes.get(currentChunk)).doubleValue()) {
				currentLength += ((Double)stopCaptureTimes.get(currentChunk)).doubleValue();
				currentChunk++;
				if (currentChunk >= stopCaptureTimes.size())
					break;
			}
			
			// if no more capturing Chunks OR start+duration <startCapture
			if (currentChunk >= stopCaptureTimes.size())
				break;
			if(((Double)startCaptureTimes.get(currentChunk)).doubleValue() > (sd.worldTime + sd.clippedDuration))
				continue;
			
			// know that sound must intersect with capturing
			//print("Current Chunk Values: Start "+ startCaptureTimes.get(currentChunk) + " Stop: " + stopCaptureTimes.get(currentChunk));
			//print("Current Sound: Start " + sd.worldTime + " End: " + (sd.worldTime + sd.clippedDuration));

			if (sd.worldTime > ((Double)startCaptureTimes.get(currentChunk)).doubleValue()) {
				blankLength = sd.worldTime - ((Double)startCaptureTimes.get(currentChunk)).doubleValue() + currentLength;
			} else {
				blankLength = currentLength;
			}
			
			orig_file = exportDirectory + "sound" + y + "."	+ sd.data.getExtension();
			
			sound_slice = exportDirectory + "sound" + y + ".wav";
			sound_cut = exportDirectory + "soundCut" + y + ".wav";
			track_file = exportDirectory + "track" + y + ".wav";

			writeToFile(sd.data.getJMFDataSource(), createURL(orig_file));

			if (sd.data.getExtension().equals("MP3")){
				//print("About to try and Convert!" + sound_slice + " " + orig_file);
				SimpleSound.convert(orig_file, sound_slice);
			}
			
			//try to crop sound if necessary
			double beginning = 0.0;
			double ending = 0.0;
		
			beginning = cropBeginning(sd, length, currentChunk);
			ending = cropEnding(sd, length, currentChunk);

			//print("Trying to Cut");
			if (beginning != 0 || ending != 0) {
				sound_slice = tryToCut(length, sd, sound_slice, sound_cut, beginning, ending, blankLength);
			}

			//if ( sound_slice.endsWith(".wav") ){
			//	String filename = "file:" + sound_slice.replace('/', '\\');
			//	SaveAsDialog dlgSaveAs = new SaveAsDialog (new Frame(), filename, null, null);	
			//	sound_slice = sound_slice.substring(0, sound_slice.length() - 4) + "a.wav";
			//}
			
			//create sound
			try {			
				SimpleSound s = new SimpleSound();
				s.loadFromFile(sound_slice);
				
				//create silent sound of length blankLength, and type s
				SimpleSound blank = new SimpleSound(blankLength, s);
				blank.writeToFile(silence);
			} catch (SoundException e) {
				AuthoringTool.showErrorDialog( "Error encoding sound file. ", e);
				return null;
			}

			if(blankLength > 0.0){
				Vector v = new Vector();
				v.add(createURL(silence));
				v.add(createURL(sound_slice));
				final_sound = createURL(track_file);

				if(concat(v, final_sound)==false)
					return null;
			}
			else {
				//print("No SILENCE");
				final_sound = createURL(sound_slice);
			}
			//print("Sound " + y);
			newDS.add(final_sound);
		}
		
		// merging sounds!!
		//print(" Right before merging ");

		return newDS;
	}

	// need to check if either startOFSound is between the too or stopSound
	// between or stop <start and
	public double cropBeginning(SoundData sd, double length, int current) {
		if (((Double)startCaptureTimes.get(current)).doubleValue() < sd.worldTime)
			return 0.0;
		else if (((Double)startCaptureTimes.get(current)).doubleValue() > sd.worldTime)
			return ((Double)startCaptureTimes.get(current)).doubleValue() - sd.worldTime;
		return 0.0;
	}

	public double cropEnding(SoundData sd, double length, int current) {
		if (((Double)stopCaptureTimes.get(current)).doubleValue() < sd.duration+sd.worldTime)
			return sd.worldTime+sd.duration - ((Double)stopCaptureTimes.get(current)).doubleValue();
		else if (((Double)stopCaptureTimes.get(current)).doubleValue() > sd.duration+sd.worldTime)
			return 0.0;
		return 0.0;
	}

	public String tryToCut(double length, SoundData sd, String file3,
			String file4, double cropFromBeginning, double cropFromEnding, double blankLength) {
			Vector start = new Vector();
			Vector stop = new Vector();

			if (cropFromBeginning != 0.0 || cropFromEnding != 0.0
				|| sd.clippedDuration+sd.worldTime>length) { //possiblility
	
			if((sd.duration -sd.clippedDuration) >cropFromEnding)
					cropFromEnding = sd.duration-sd.clippedDuration;
			
			sd.startTime += cropFromBeginning;
			sd.stopTime -= cropFromEnding;
			
			//print("TRYING TO CUT");
			//print("WorldTime " + sd.worldTime);
			//print("Start Time " + sd.startTime + " End Time " + sd.stopTime);

			//print("Duration " + sd.duration + "Length " + length);

			
			 if ((blankLength + (sd.stopTime-sd.startTime)) > length)
				 sd.stopTime = (length - blankLength) + sd.startTime -.02;
			
			 if(sd.stopTime<sd.startTime)
				 return file3;
			
			 //print(" Going to cut ");
			 //print("Start Time " + sd.startTime + " End Time " + sd.stopTime);
			 /*print(" World Time" + sd.worldTime + "Clipped Duration " + sd.clippedDuration
					+ " Duration " + sd.duration + " Length " + length);
			  */
			start.add(new Long((int) (sd.startTime * 1000.0)));
			stop.add(new Long((int) (sd.stopTime * 1000.0)));

			//print("Start of cropped area "  + start.get(0) + " Stop of cropped area" + stop.get(0));
			
			movieMaker.Cut cut = new Cut();
			if(((Long)start.get(0)).longValue()!=0.0 || ((Long)stop.get(0)).longValue()!=sd.clippedDuration)
				 cut.doCut(createURL(file3), createURL(file4), start, stop);
			cut=null;
			//print("Done Cutting");
			return file4;

		}
		return file3;

	}

	public String createURL(String s) {
		String url;
		try {
			url=new java.io.File(s).toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
		//need to fix??
		String mod = url.replaceFirst("file:/", "file:///");
		return mod;

	}

	public void writeToFile(javax.media.protocol.DataSource ds, String fileName) {
		Merge m;

		m = new Merge(fileName);
		if(m!=null)
			m.doSingle(ds);
		m = null;
	}

	public boolean concat(java.util.Vector inputURL, String outputURL) {

		MediaLocator iml[] = new MediaLocator[2];
		MediaLocator oml;
		int i = 0;
		for (i = 0; i < inputURL.size(); i++) {
			if ((iml[i] = Concat.createMediaLocator((String) inputURL
					.elementAt(i))) == null) {
				//print("Cannot build media locator from: " + inputURL);
				// System.exit(0);
				return false;
			}
		}

		if ((oml = Concat.createMediaLocator(outputURL)) == null) {
			//print("Cannot build media locator from: " + outputURL);
			return false;
		}

		Concat concat = new Concat();

		if (!concat.doIt(iml, oml)) {
			//print("Failed to concatenate the inputs");
			return false;
		}
	
		return true;
	}

	class SoundData {

		double startTime = 0.0;

		double worldTime = 0.0;

		double duration = 0.0;

		double volume = 1.0;

		double clippedDuration = 11000;

		double stopTime = 0.0;

		edu.cmu.cs.stage3.media.jmfmedia.DataSource data = null;

		public SoundData(Long start, Double len,
				edu.cmu.cs.stage3.media.jmfmedia.DataSource ds, Object to,
				Object from, Object rate, Object vol) {
			
			//System.err.println("Set as " + start);
			worldTime = start.longValue();
			duration = len.doubleValue();
			data = ds;
			if (rate != null)
				clippedDuration = ((Double)rate).doubleValue();
			if (to != null)
				startTime = ((Double) to).doubleValue();
			if (from != null)
				stopTime = ((Double) from).doubleValue();
			if (vol != null)
				volume = ((Double) vol).doubleValue();

		}
	}
	    
    class StateListener implements ControllerListener {

        public void controllerUpdate ( ControllerEvent ce ) {
            if (ce instanceof ControllerClosedEvent)
                stateFailed = true;
            if (ce instanceof ControllerEvent)
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
        }
    }
}
