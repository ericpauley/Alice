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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class OneShotScheduler extends edu.cmu.cs.stage3.alice.core.Scheduler {
	public boolean isPropertyAffected( edu.cmu.cs.stage3.alice.core.Property property ) {
		edu.cmu.cs.stage3.alice.core.event.ScheduleListener[] scheduleListeners = getScheduleListeners();
		for( int i = 0; i < scheduleListeners.length; i++ ) {
			edu.cmu.cs.stage3.alice.core.event.ScheduleListener sl = scheduleListeners[i];
			if( sl instanceof edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior ) {
				edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = ((edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior)sl).getAffectedProperties();
				for( int j = 0; j < affectedProperties.length; j++ ) {
					if( property == affectedProperties[j] ) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
