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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.response.Print;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */

public class ToStringQuestion extends Question {
	public final ObjectProperty what = new ObjectProperty(this, "what", new String(""), Object.class) {

		@Override
		protected boolean getValueOfExpression() {
			return true;
		}
	};

	@Override
	public Class getValueClass() {
		return String.class;
	}

	@Override
	public Object getValue() {
		Object value = what.getValue();
		Object o = what.get();
		if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart) {
			Print.outputtext = "time elapsed as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year) {
			Print.outputtext = "year as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear) {
			Print.outputtext = "month of year as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear) {
			Print.outputtext = "day of year as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth) {
			Print.outputtext = "day of month as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek) {
			Print.outputtext = "day of week as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth) {
			Print.outputtext = "day of week in month as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM) {
			Print.outputtext = "is AM as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM) {
			Print.outputtext = "is PM as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM) {
			Print.outputtext = "hour of AM or PM as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay) {
			Print.outputtext = "hour of day as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour) {
			Print.outputtext = "minutes of hour as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute) {
			Print.outputtext = "seconds of minute as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge) {
			Print.outputtext = "mouse distance from left edge as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge) {
			Print.outputtext = "mouse distance from top edge as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber) {
			Print.outputtext = "ask user for a number as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo) {
			Print.outputtext = "ask user for yes or no as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString) {
			Print.outputtext = "ask user for a string as a string is ";
		} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber) {
			Print.outputtext = "random number as a string is ";
		} else if (Print.outputtext != null) {
			Print.outputtext = Print.outputtext.substring(0, Print.outputtext.length() - 4) + " as a string is ";
		}

		if (value instanceof Element) {
			return ((Element) value).getTrimmedKey();
		} else if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}
}