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

import java.io.IOException;
import java.io.InputStream;

/**
 * A subclass of <code>SeekableStream</code> that may be used to wrap a regular
 * <code>InputStream</code> efficiently. Seeking backwards is not supported.
 * 
 * <p>
 * <b> This class is not a committed part of the JAI API. It may be removed or
 * changed in future releases of JAI.</b>
 */
public class ForwardSeekableStream extends SeekableStream {

	/** The source <code>InputStream</code>. */
	private InputStream src;

	/** The current position. */
	long pointer = 0L;

	/** The marked position. */
	long markPos = -1L;

	/**
	 * Constructs a <code>InputStreamForwardSeekableStream</code> from a regular
	 * <code>InputStream</code>.
	 */
	public ForwardSeekableStream(InputStream src) {
		this.src = src;
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public final int read() throws IOException {
		int result = src.read();
		if (result != -1) {
			++pointer;
		}
		return result;
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public final int read(byte[] b, int off, int len) throws IOException {
		int result = src.read(b, off, len);
		if (result != -1) {
			pointer += result;
		}
		return result;
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public final long skip(long n) throws IOException {
		long skipped = src.skip(n);
		pointer += skipped;
		return skipped;
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public final int available() throws IOException {
		return src.available();
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public final void close() throws IOException {
		src.close();
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public synchronized final void mark(int readLimit) {
		markPos = pointer;
		src.mark(readLimit);
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public synchronized final void reset() throws IOException {
		if (markPos != -1) {
			pointer = markPos;
		}
		src.reset();
	}

	/** Forwards the request to the real <code>InputStream</code>. */

	@Override
	public boolean markSupported() {
		return src.markSupported();
	}

	/** Returns <code>false</code> since seking backwards is not supported. */

	@Override
	public final boolean canSeekBackwards() {
		return false;
	}

	/** Returns the current position in the stream (bytes read). */

	@Override
	public final long getFilePointer() {
		return pointer;
	}

	/**
	 * Seeks forward to the given position in the stream. If <code>pos</code> is
	 * smaller than the current position as returned by
	 * <code>getFilePointer()</code>, nothing happens.
	 */

	@Override
	public final void seek(long pos) throws IOException {
		while (pos - pointer > 0) {
			pointer += src.skip(pos - pointer);
		}
	}
}
