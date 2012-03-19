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

package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;

public class ListItemResponse extends ListResponse {
	public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");

	private Variable m_variable = null;
	private List m_list = null;

	private PropertyListener m_variableValuePropertyListener = new PropertyListener() {
		@Override
		public void propertyChanging(PropertyEvent propertyEvent) {
		}
		@Override
		public void propertyChanged(PropertyEvent propertyEvent) {
			if (m_list != null) {
				m_list.valueClass.removePropertyListener(m_listValueClassPropertyListener);
			}
			m_list = (List) propertyEvent.getValue();
			if (m_list != null) {
				m_list.valueClass.addPropertyListener(m_listValueClassPropertyListener);
			}
			updateItemCollection();
		}
	};
	private PropertyListener m_listValueClassPropertyListener = new PropertyListener() {
		@Override
		public void propertyChanging(PropertyEvent propertyEvent) {
		}
		@Override
		public void propertyChanged(PropertyEvent propertyEvent) {
			updateItemCollection();
		}
	};
	private void updateItemCollection() {
		item.setCollection(list.getListValue());
	}

	@Override
	protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == list) {
			if (m_variable != null) {
				m_variable.value.removePropertyListener(m_variableValuePropertyListener);
			}
			if (m_list != null) {
				m_list.valueClass.removePropertyListener(m_listValueClassPropertyListener);
			}
			if (value instanceof Variable) {
				m_variable = (Variable) value;
				m_list = (List) m_variable.value.getValue();
			} else {
				m_variable = null;
				m_list = (List) value;
			}
			if (m_variable != null) {
				m_variable.value.addPropertyListener(m_variableValuePropertyListener);
			}
			if (m_list != null) {
				m_list.valueClass.addPropertyListener(m_listValueClassPropertyListener);
			}

			updateItemCollection();
		}
		super.propertyChanged(property, value);
	}
	public class RuntimeListItemResponse extends RuntimeListResponse {
		protected Object getItem() {
			return item.getValue();
		}
	}
}
