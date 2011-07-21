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

package edu.cmu.cs.stage3.alice.authoringtool.util.event;

/**
 * @author Jason Pratt
 */
public class ConfigurationEvent {
	protected String keyName;
	protected boolean isList;
	protected String oldValue;
	protected String newValue;
	protected String[] oldValueList;
	protected String[] newValueList;

	public ConfigurationEvent( String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList ) {
		this.keyName = keyName;
		this.isList = isList;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.oldValueList = oldValueList;
		this.newValueList = newValueList;
	}

	public boolean isList() {
		return isList;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public String[] getOldValueList() {
		return oldValueList;
	}

	/**
	 * this method will not necessarily return the correct array if this event is produced by an
	 * <code>addToValueList</code> or <code>removeFromValueList</code> call.  Instead, it will return <code>null</code>.
	 */
	public String[] getNewValueList() {
		return newValueList;
	}
}