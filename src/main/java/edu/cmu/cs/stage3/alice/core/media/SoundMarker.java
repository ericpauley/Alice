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

package edu.cmu.cs.stage3.alice.core.media;

import edu.cmu.cs.stage3.alice.core.Element;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import edu.cmu.cs.stage3.alice.core.property.*;

public class SoundMarker extends Element {
    public final NumberProperty time = new NumberProperty(this,"time",new Double(0));

    protected java.util.Vector soundMarkerListeners = new java.util.Vector();

    public SoundMarker() {
    }

    public SoundMarker(String n, double t) {
        if (n!=null)
            this.name.set(n);
        time.set(new Double(t));
    }

    public SoundMarker(double t) {
        time.set(new Double(t));
    }

    public void setTime(double t) {
        time.set(new Double(t));
    }

    public double getTime() {
        return time.doubleValue();
    }

    public void addSoundMarkerListener(SoundMarkerListener sml) {
        if (soundMarkerListeners.contains(sml)) {
            // warn
        } else {
            soundMarkerListeners.addElement(sml);
        }
    }

    public void removeSoundMarkerListener(SoundMarkerListener sml) {
        soundMarkerListeners.removeElement(sml);
    }

/*
    public void passed() {
        java.util.Enumeration enum0 = soundMarkerListeners.elements();
        while( enum0.hasMoreElements() ) {
            SoundMarkerListener sml = (SoundMarkerListener)enum0.nextElement();
            sml.markerPassed( this );
        }
    }
*/
}