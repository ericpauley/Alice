/*
 * @(#)TrackPanelAudio.java	1.3 01/03/13
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.*;
import javax.media.control.*;
//import javax.media.Format;
import javax.media.format.*;
//import javax.media.protocol.*;
import jmapps.ui.*;

//import com.sun.media.ui.AudioFormatChooser;


public class TrackPanelAudio extends TrackPanel implements ActionListener {

    private AudioFormat         formatOld;
    private String              strContentType = null;
    private AudioFormatChooser  chooserAudioFormat;

    public TrackPanelAudio ( TrackControl trackControl, ActionListener listenerEnableTrack ) {
        super ( trackControl, listenerEnableTrack );

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContentType ( String strContentType ) {
        int                   i;
        int                   nSize;
        AudioFormat           formatAudio;

        arrSupportedFormats = trackControl.getSupportedFormats();
        this.strContentType = strContentType;
        nSize = arrSupportedFormats.length;
        vectorContSuppFormats = new Vector ();

        for ( i = 0;  i < nSize;  i++ ) {
            if ( !(arrSupportedFormats[i] instanceof AudioFormat) )
                continue;
            formatAudio = (AudioFormat) arrSupportedFormats[i];
            vectorContSuppFormats.addElement ( formatAudio );
        }

        chooserAudioFormat.setSupportedFormats ( vectorContSuppFormats );
        chooserAudioFormat.setCurrentFormat ( formatOld );
    }

    
	public boolean isTrackEnabled () {
        boolean     boolEnabled;
        boolEnabled = chooserAudioFormat.isTrackEnabled ();
        return ( boolEnabled );
    }

    
	public Format getFormat () {
        Format         format;
        format = chooserAudioFormat.getFormat ();
        return ( format );
    }

    public void setDefaults ( boolean boolTrackEnable, Format format ) {
        chooserAudioFormat.setTrackEnabled ( boolTrackEnable );
        if ( format instanceof AudioFormat ) {
            formatOld = (AudioFormat) format;
            chooserAudioFormat.setCurrentFormat ( formatOld );
        }
    }

    private void init () throws Exception {
        this.setLayout ( new BorderLayout() );
        formatOld = (AudioFormat) trackControl.getFormat ();
        chooserAudioFormat = new AudioFormatChooser ( arrSupportedFormats, formatOld, true, this );
        this.add ( chooserAudioFormat, BorderLayout.NORTH );
    }

    /**
     * This method overwrites the ActionListener method to process events
     * from buttons, track pages, and Progress dialog.
     * @param    event    action event
     */
    public void actionPerformed ( ActionEvent event ) {
        String        strCmd;
        ActionEvent   eventNew;

        strCmd = event.getActionCommand ();
        if ( strCmd.equals(AudioFormatChooser.ACTION_TRACK_ENABLED) ) {
            eventNew = new ActionEvent ( this, ActionEvent.ACTION_PERFORMED, event.getActionCommand() );
            listenerEnableTrack.actionPerformed ( eventNew );
        }
        else if ( strCmd.equals(AudioFormatChooser.ACTION_TRACK_DISABLED) ) {
            eventNew = new ActionEvent ( this, ActionEvent.ACTION_PERFORMED, event.getActionCommand() );
            listenerEnableTrack.actionPerformed ( eventNew );
        }
    }


}


