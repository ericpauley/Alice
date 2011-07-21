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

package edu.cmu.cs.stage3.image.codec;

/*
 * The contents of this file are subject to the  JAVA ADVANCED IMAGING
 * SAMPLE INPUT-OUTPUT CODECS AND WIDGET HANDLING SOURCE CODE  License
 * Version 1.0 (the "License"); You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/software/imaging/JAI/index.html
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is JAVA ADVANCED IMAGING SAMPLE INPUT-OUTPUT CODECS
 * AND WIDGET HANDLING SOURCE CODE.
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 * Portions created by: _______________________________________
 * are Copyright (C): _______________________________________
 * All Rights Reserved.
 * Contributor(s): _______________________________________
 */


/**
 * A utility class representing a segment within a stream as a
 * <code>long</code> starting position and an <code>int</code>
 * length.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 */
public class StreamSegment {

    private long startPos = 0L;
    private int segmentLength = 0;

    /**
     * Constructs a <code>StreamSegment</code>.
     * The starting position and length are set to 0.
     */
    public StreamSegment() {}

    /**
     * Constructs a <code>StreamSegment</code> with a
     * given starting position and length.
     */
    public StreamSegment(long startPos, int segmentLength) {
        this.startPos = startPos;
        this.segmentLength = segmentLength;
    }

    /**
     * Returns the starting position of the segment.
     */
    public final long getStartPos() {
        return startPos;
    }

    /**
     * Sets the starting position of the segment.
     */
    public final void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    /**
     * Returns the length of the segment.
     */
    public final int getSegmentLength() {
        return segmentLength;
    }

    /**
     * Sets the length of the segment.
     */
    public final void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }
}
