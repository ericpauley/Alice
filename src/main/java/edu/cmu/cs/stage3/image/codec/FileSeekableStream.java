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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A subclass of <code>SeekableStream</code> that takes its input
 * from a <code>File</code> or <code>RandomAccessFile</code>.
 * Backwards seeking is supported.  The <code>mark()</code> and
 * <code>resest()</code> methods are supported.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 */
public class FileSeekableStream extends SeekableStream {

    private RandomAccessFile file;
    private long markPos = -1;

    // Base 2 logarithm of the cache page size
    private static final int PAGE_SHIFT = 9;

    // The page size, derived from PAGE_SHIFT
    private static final int PAGE_SIZE = 1 << PAGE_SHIFT;

    // Binary mask to find the offset of a pointer within a cache page
    private static final int PAGE_MASK = PAGE_SIZE - 1;

    // Number of pages to cache
    private static final int NUM_PAGES = 32;

    // Reads longer than this bypass the cache
    private static final int READ_CACHE_LIMIT = PAGE_SIZE;

    // The page cache
    private byte[][] pageBuf = new byte[PAGE_SIZE][NUM_PAGES];

    // The index of the file page held in a given cache entry,
    // -1 = invalid.
    private int[] currentPage = new int[NUM_PAGES];

    private long length = 0L;

    private long pointer = 0L;

    /**
     * Constructs a <code>FileSeekableStream</code> from a
     * <code>RandomAccessFile</code>.
     */
    public FileSeekableStream(RandomAccessFile file) throws IOException {
        this.file = file;
        file.seek(0L);
        this.length = file.length();

        // Allocate the cache pages and mark them as invalid
        for (int i = 0; i < NUM_PAGES; i++) {
            pageBuf[i] = new byte[PAGE_SIZE];
            currentPage[i] = -1;
        }
    }

    /**
     * Constructs a <code>FileSeekableStream</code> from a
     * <code>File</code>.
     */
    public FileSeekableStream(File file) throws IOException {
        this(new RandomAccessFile(file, "r"));
    }

    /**
     * Constructs a <code>FileSeekableStream</code> from a
     * <code>String</code> path name.
     */
    public FileSeekableStream(String name) throws IOException {
        this(new RandomAccessFile(name, "r"));
    }

    /** Returns true since seeking backwards is supported. */
    
	public final boolean canSeekBackwards() {
        return true;
    }

    /**
     * Returns the current offset in this stream.
     *
     * @return     the offset from the beginning of the stream, in bytes,
     *             at which the next read occurs.
     * @exception  IOException  if an I/O error occurs.
     */
    
	public final long getFilePointer() throws IOException {
        return pointer;
    }

    
	public final void seek(long pos) throws IOException {
        if (pos < 0) {
            throw new IOException(JaiI18N.getString("FileSeekableStream0"));
        }
        pointer = pos;
    }

    public final int skip(int n) throws IOException {
        pointer += n;
        return n;
    }

    private byte[] readPage(long pointer) throws IOException {
        int page = (int)(pointer >> PAGE_SHIFT);

        for (int i = 0; i < NUM_PAGES; i++) {
            if (currentPage[i] == page) {
                return pageBuf[i];
            }
        }

        // Use random replacement for now
        int index = (int)(Math.random()*NUM_PAGES);
        currentPage[index] = page;

        long pos = page << PAGE_SHIFT;
        long remaining = length - pos;
        int len = PAGE_SIZE < remaining ? PAGE_SIZE : (int)remaining;
        file.seek(pos);
        file.readFully(pageBuf[index], 0, len);

        return pageBuf[index];
    }

    /** Forwards the request to the real <code>File</code>. */
    
	public final int read() throws IOException {
        if (pointer >= length) {
            return -1;
        }

        byte[] buf = readPage(pointer);
        return buf[(int)(pointer++ & PAGE_MASK)] & 0xff;
    }

    /** Forwards the request to the real <code>File</code>. */
    
	public final int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if ((off < 0) || (len < 0) || (off + len > b.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }

        len = (int)Math.min(len, length - pointer);
        if (len <= 0) {
            return -1;
        }

        // If the read is large, don't bother to cache it.
        if (len > READ_CACHE_LIMIT) {
            file.seek(pointer);
            int nbytes = file.read(b, off, len);
            pointer += nbytes;
            return nbytes;
        } else {
            byte[] buf = readPage(pointer);

            // Compute length to end of page
            int remaining = PAGE_SIZE - (int)(pointer & PAGE_MASK);
            int newLen = len < remaining ? len : remaining;
            System.arraycopy(buf, (int)(pointer & PAGE_MASK), b, off, newLen);

            pointer += newLen;
            return newLen;
        }
    }

    /** Forwards the request to the real <code>File</code>. */
    
	public final void close() throws IOException {
        file.close();
    }

    /**
     * Marks the current file position for later return using
     * the <code>reset()</code> method.
     */
    
	public synchronized final void mark(int readLimit) {
        markPos = pointer;
    }

    /**
     * Returns the file position to its position at the time of
     * the immediately previous call to the <code>mark()</code>
     * method.
     */
    
	public synchronized final void reset() throws IOException {
        if (markPos != -1) {
            pointer = markPos;
        }
    }

    /** Returns <code>true</code> since marking is supported. */
    
	public boolean markSupported() {
        return true;
    }
}
