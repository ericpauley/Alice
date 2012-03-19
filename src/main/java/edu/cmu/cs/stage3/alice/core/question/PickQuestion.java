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
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class PickQuestion extends Question {
	public final BooleanProperty ascend = new BooleanProperty(this, "ascend", Boolean.TRUE);
	private java.awt.event.MouseEvent m_mouseEvent;
	public void setMouseEvent(java.awt.event.MouseEvent mouseEvent) {
		m_mouseEvent = mouseEvent;
	}
	private Model ascend(Model part) {
		if (part.doEventsStopAscending()) {
			return part;
		}
		Element parent = part.getParent();
		if (parent instanceof Model) {
			return ascend((Model) parent);
		} else {
			return part;
		}

	}

	@Override
	public Object getValue() {
		edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = RenderTarget.pick(m_mouseEvent);
		if (pickInfo != null && pickInfo.getCount() > 0) {
			Object o = pickInfo.getVisualAt(0).getBonus();
			if (o instanceof Model) {
				Model part = (Model) o;
				if (ascend.booleanValue()) {
					return ascend(part);
				} else {
					return part;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Class getValueClass() {
		return Model.class;
	}
}