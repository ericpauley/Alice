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

import java.text.DecimalFormat;

public class FPXUtils {

//     /** Reads a 2-byte little-endian value. */
//     public static final int readInt2(SeekableStream file, int offset)
//         throws IOException {
//         file.seek(offset);
//         return file.readShortLE();
//     }

//     /** Reads a 4-byte little-endian value. */
//     public static final int readInt4(SeekableStream file, int offset)
//         throws IOException {
//         file.seek(offset);
//         return file.readIntLE();
//     }

//     /** Reads a 4-byte little-endian IEEE float. */
//     public static final float readFloat(SeekableStream file, int offset)
//         throws IOException {
//         file.seek(offset);
//         return file.readFloatLE();
//     }

//     /** Reads an 8-byte little-endian IEEE double. */
//     public static final double readDouble(SeekableStream file,
//                                           int offset)
//         throws IOException {
//         file.seek(offset);
//         return file.readDoubleLE();
//     }

    public static final short getShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;

        return (short)((b1 << 8) | b0);
    }

    public static final int getUnsignedShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;

        return (b1 << 8) | b0;
    }

    public static final int getIntLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xff;
        int b1 = data[offset + 1] & 0xff;
        int b2 = data[offset + 2] & 0xff;
        int b3 = data[offset + 3] & 0xff;

        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static final long getUnsignedIntLE(byte[] data, int offset) {
        long b0 = data[offset] & 0xff;
        long b1 = data[offset + 1] & 0xff;
        long b2 = data[offset + 2] & 0xff;
        long b3 = data[offset + 3] & 0xff;

        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static final String getString(byte[] data, int offset, int length) {
        if (length == 0) {
            return "<none>";
        } else {
            length = length/2 - 1; // workaround for Kodak bug
        }
        StringBuffer b = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            int c = getUnsignedShortLE(data, offset);
            b.append((char)c);
            offset += 2;
        }

        return b.toString();
    }

    private static void printDecimal(int i) {
        DecimalFormat d = new DecimalFormat("00000");
        System.out.print(d.format(i));
    }

    private static void printHex(byte b) {
        int i = b & 0xff;
        int hi = i/16;
        int lo = i % 16;

        if (hi < 10) {
            System.out.print((char)('0' + hi));
        } else {
            System.out.print((char)('a' + hi - 10));
        }

        if (lo < 10) {
            System.out.print((char)('0' + lo));
        } else {
            System.out.print((char)('a' + lo - 10));
        }
    }

    private static void printChar(byte b) {
        char c = (char)(b & 0xff);

        if (c >= '!' && c <= '~') {
            System.out.print(' ');
            System.out.print(c);
        } else if (c == 0) {
            System.out.print("^@");
        } else if (c < ' ') {
            System.out.print('^');
            System.out.print((char)('A' + c - 1));
        } else if (c == ' ') {
            System.out.print("__");
        } else {
            System.out.print("??");
        }
    }

    public static void dumpBuffer(byte[] buf, int offset, int length,
                                  int printOffset) {
        int lines = length/8;

        for (int j = 0; j < lines; j++) {
            printDecimal(printOffset);
            System.out.print(": ");

            for (int i = 0; i < 8; i++) {
                printHex(buf[offset + i]);
                System.out.print("  ");
            }
            for (int i = 0; i < 8; i++) {
                printChar(buf[offset + i]);
                System.out.print("  ");
            }

            offset += 8;
            printOffset += 8;
            System.out.println();
        }
    }
}
