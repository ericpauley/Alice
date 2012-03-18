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

package edu.cmu.cs.stage3.util;

public class HowMuch extends Enumerable {
    public static final HowMuch INSTANCE = new HowMuch( false, false );
    public static final HowMuch INSTANCE_AND_PARTS = new HowMuch( true, true );
    public static final HowMuch INSTANCE_AND_ALL_DESCENDANTS = new HowMuch( true, false );
    private boolean m_descend;
    private boolean m_respectDescendant;
    private HowMuch( boolean descend, boolean respectDescendant ) {
        m_descend = descend;
        m_respectDescendant = respectDescendant;
    }
    public boolean getDescend() {
        return m_descend;
    }
    public boolean getRespectDescendant() {
        return m_respectDescendant;
    }
    public static HowMuch valueOf( String s ) {
        return (HowMuch)Enumerable.valueOf( s, HowMuch.class );
    }
}