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

package edu.cmu.cs.stage3.alice.scenegraph.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import javax.vecmath.Point2d;

public class PointComparator implements java.util.Comparator {

    public PointComparator() {
    }

    public int compare(Object o1, Object o2) {
        Point2d p1 = (Point2d)o1;
        Point2d p2 = (Point2d)o2;

        if (p1.x<p2.x) return -1;
        if (p1.x>p2.x) return 1;
        if (p1.y<p2.y) return -1;
        if (p1.y>p2.y) return 1;
        return 0;

    }
}