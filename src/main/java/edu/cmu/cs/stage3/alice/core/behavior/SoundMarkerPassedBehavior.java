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

import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.media.SoundMarkerListener;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;

public class SoundMarkerPassedBehavior extends TriggerBehavior implements SoundMarkerListener {
	public final ElementProperty marker = new ElementProperty(this, "marker", null, SoundMarker.class);
	protected SoundMarker m_marker = null;

	@Override
	public void markerPassed(SoundMarker m) {
		if (m == m_marker) {
			trigger(System.currentTimeMillis() * 0.001);
		}
	}

	@Override
	protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.started(world, time);
		m_marker = (SoundMarker) marker.getValue();
		if (m_marker != null) {
			m_marker.addSoundMarkerListener(this);
		}
	}

	@Override
	protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
		super.stopped(world, time);
		if (m_marker != null) {
			m_marker.removeSoundMarkerListener(this);
		}
	}
}
