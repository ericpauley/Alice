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

public class DistanceComparator implements java.util.Comparator {
    public Point2d start = null;

    public DistanceComparator() {
    }

    public DistanceComparator(Point2d point) {
        start = point;
    }

    public int compare(Object o1, Object o2) throws ClassCastException {
        Point2d p1,p2;

        if (o1 instanceof Point2d)
            p1 = (Point2d)o1;
        else if (o1 instanceof PointNode)
            p1 = ((PointNode)o1).data;
        else
            throw new ClassCastException();
        if (o2 instanceof Point2d)
            p2 = (Point2d)o2;
        else if (o2 instanceof PointNode)
            p2 = ((PointNode)o2).data;
        else
            throw new ClassCastException();

        double a = start.distanceL1(p1);
        double b = start.distanceL1(p2);

        if (a<b) return -1;
        if (a>b) return 1;
        return 0;
    }

}