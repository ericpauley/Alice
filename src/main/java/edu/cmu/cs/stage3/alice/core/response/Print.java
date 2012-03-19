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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class Print extends edu.cmu.cs.stage3.alice.core.Response {
	public final StringProperty text = new StringProperty(this, "text", null);
	public final ObjectProperty object = new ObjectProperty(this, "object", null, Object.class) {

		@Override
		protected boolean getValueOfExpression() {
			return true;
		}
	};
	public final BooleanProperty addNewLine = new BooleanProperty(this, "addNewLine", Boolean.TRUE);

	@Override
	protected Number getDefaultDuration() {
		return new Double(0);
	}

	public String getPrefix() {
		String t = text.getStringValue();
		if (t != null) {
			return null;
		} else {
			Object o = object.get();
			if (o != null) {
				if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
					return "the value of " + ((edu.cmu.cs.stage3.alice.core.Element) o).getTrimmedKey() + " is ";
				} else {
					return "the value of " + o + " is ";
				}
			} else {
				return null;
			}
		}
	}

	public static String outputtext = null;
	public class RuntimePrint extends RuntimeResponse {

		@Override
		public void update(double t) {
			super.update(t);
			outputtext = null;
			String s = text.getStringValue();
			Object o = object.get();
			// Time
			if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart) {
				outputtext = "time elapsed is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year) {
				outputtext = "year is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear) {
				outputtext = "month of year is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear) {
				outputtext = "day of year is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth) {
				outputtext = "day of month is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek) {
				outputtext = "day of week is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth) {
				outputtext = "day of week in month is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM) {
				outputtext = "is AM is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM) {
				outputtext = "is PM is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM) {
				outputtext = "hour of AM or PM is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay) {
				outputtext = "hour of day is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour) {
				outputtext = "minutes of hour is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute) {
				outputtext = "seconds of minute is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge) {
				outputtext = "mouse distance from left edge is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge) {
				outputtext = "mouse distance from top edge is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber) {
				outputtext = "ask user for a number is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo) {
				outputtext = "ask user for yes or no is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString) {
				outputtext = "ask user for a string is ";
			} else if (o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber) {
				outputtext = "random number is ";
			}

			Object value = object.getValue();
			if (value instanceof Double) {
				java.text.NumberFormat formatter = new java.text.DecimalFormat("#.######");
				value = Double.valueOf(formatter.format(value));
			}

			String valueText = "None";
			if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
				valueText = ((edu.cmu.cs.stage3.alice.core.Element) value).getTrimmedKey();
			} else if (value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color) {
				double blue = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getBlue();
				double green = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getGreen();
				double red = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getRed();
				if (blue == 1 && green == 1 && red == 1) {
					valueText = "white";
				} else if (blue == 0 && green == 0 && red == 0) {
					valueText = "black";
				} else if (blue == 0 && green == 0 && red == 1) {
					valueText = "red";
				} else if (blue == 0 && green == 1 && red == 0) {
					valueText = "green";
				} else if (blue == 1 && green == 0 && red == 0) {
					valueText = "blue";
				} else if (blue == 0 && green == 1 && red == 1) {
					valueText = "yellow";
				} else if (blue == 0.501960813999176 && green == 0 && red == 0.501960813999176) {
					valueText = "purple";
				} else if (blue == 0 && green == 0.6470588445663452 && red == 1) {
					valueText = "orange";
				} else if (blue == 0.686274528503418 && green == 0.686274528503418 && red == 1) {
					valueText = "pink";
				} else if (blue == 0.16470588743686676 && green == 0.16470588743686676 && red == 0.6352941393852234) {
					valueText = "brown";
				} else if (blue == 1 && green == 1 && red == 0) {
					valueText = "cyan";
				} else if (blue == 1 && green == 0 && red == 1) {
					valueText = "magenta";
				} else if (blue == 0.501960813999176 && green == 0.501960813999176 && red == 0.501960813999176) {
					valueText = "gray";
				} else if (blue == 0.7529411911964417 && green == 0.7529411911964417 && red == 0.7529411911964417) {
					valueText = "light gray";
				} else if (blue == 0.250980406999588 && green == 0.250980406999588 && red == 0.250980406999588) {
					valueText = "dark gray";
				} else {
					valueText = value.toString();
					valueText = valueText.substring(valueText.indexOf("Color"), valueText.length());
				}
			} else if (value != null) {
				valueText = value.toString();
			}

			String output;
			if (s != null) {
				if (o != null) {
					output = s + valueText;
				} else {
					output = s;
				}
			} else {
				if (o != null) {
					output = getPrefix();
					if (outputtext != null) {
						output = output.substring(0, output.indexOf("__") - 1) + " " + outputtext + valueText;
					} else {
						output = output + valueText;
					}
				} else {
					output = valueText;
				}
			}
			if (addNewLine.booleanValue()) {
				System.out.println(output);
			} else {
				System.out.print(output);
			}
		}
	}
}
