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

package edu.cmu.cs.stage3.alice.core;

public class List extends Collection {
	public boolean contains( Object item ) {
		return values.contains( item );
	}
	public boolean containsValue( Object item ) {
		return values.containsValue( item );
	}
	public int firstIndexOfItem( Object item ) {
		return values.indexOf( item );
	}
	public int firstIndexOfItemValue( Object item ) {
		return values.indexOfValue( item );
	}
	public int firstIndexOfItem( Object item, int startFrom ) {
		return values.indexOf( item, startFrom );
	}
	public int firstIndexOfItemValue( Object item, int startFrom ) {
		return values.indexOfValue( item, startFrom );
	}
	public Object itemAtBeginning() {
		return values.get( 0 );
	}
	public Object itemValueAtBeginning() {
		return values.getValue( 0 );
	}
	public Object itemAtEnd() {
		return values.get( -1 );
	}
	public Object itemValueAtEnd() {
		return values.getValue( -1 );
	}
	public Object itemAtIndex( int index ) {
		return values.get( index );
	}
	public Object itemValueAtIndex( int index ) {
		return values.getValue( index );
	}
	public Object itemAtRandomIndex() {
		int index = (int)( Math.random()*size() );
		return values.get( index );
	}
	public Object itemValueAtRandomIndex() {
		int index = (int)( Math.random()*size() );
		return values.getValue( index );
	}
	public int lastIndexOfItem( Object item ) {
		return values.lastIndexOf( item );
	}
	public int lastIndexOfItemValue( Object item ) {
		return values.lastIndexOfValue( item );
	}
	public int lastIndexOfItem( Object item, int startFrom ) {
		return values.lastIndexOf( item, startFrom );
	}
	public int lastIndexOfItemValue( Object item, int startFrom ) {
		return values.lastIndexOfValue( item, startFrom );
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}
    public int size() {
        return values.size();
    }
    public void clear() {
        values.clear();
    }
	public void insertItemAtBeginning( Object item ) {
		values.add( 0, item );
	}
	public void insertItemValueAtBeginning( Object item ) {
		values.addValue( 0, item );
	}
	public void insertItemAtEnd( Object item ) {
		values.add( -1, item );
	}
	public void insertItemValueAtEnd( Object item ) {
		values.addValue( -1, item );
	}
	public void insertItemAtIndex( int index, Object item ) {
		values.add( index, item );
	}
	public void insertItemValueAtIndex( int index, Object item ) {
		values.addValue( index, item );
	}
	public void removeItemFromBeginning() {
		values.remove( 0 );
	}
	public void removeItemFromEnd() {
		values.remove( -1 );
	}
	public void removeItemFromIndex( int index ) {
		values.remove( index );
	}
}
