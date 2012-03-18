package movieMaker;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.event.SoundEvent;
import edu.cmu.cs.stage3.alice.core.response.SoundResponse;

public class SoundHandler implements edu.cmu.cs.stage3.alice.core.event.SoundListener 
{
	SoundStorage mySS = null;
	AuthoringTool author = null;
	
	public SoundHandler( SoundStorage s, AuthoringTool a)
	{
	author =a;
	mySS = s;	
	}

	public void SoundStarted(SoundEvent soundEvent) {
	long t = System.currentTimeMillis();
		
	SoundResponse sr = soundEvent.getSoundResponse();
	
	if(mySS!=null)
		mySS.add(new Long(t), (Double)soundEvent.getDuration(), (edu.cmu.cs.stage3.media.jmfmedia.DataSource)soundEvent.getDataSource(),
		sr.toMarker.get(), sr.fromMarker.get(),sr.duration.get(), sr.volumeLevel.get());	
	
		
	}
	
}
