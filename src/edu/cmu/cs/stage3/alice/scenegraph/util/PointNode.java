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

public class PointNode implements Comparable {
    public Point2d data = null;
    public PointNode next = null;
    public PointNode prev = null;

    public PointNode(Point2d p) {
        data = p;
    }

    public void insertAfter(PointNode toAdd) {
        toAdd.next = next;
        toAdd.prev = this;
        if (next!=null)
            next.prev = toAdd;
        next = toAdd;
    }

    public Triangle triangle() {
        return new Triangle(prev.data,data,next.data);
    }

    public int convex() {
        return Triangle.convex(prev.data,data,next.data);
    }

    public boolean inCone(Point2d check) {
        return Triangle.inCone(prev.data,data,next.data,check);
    }

    public int compareTo(Object o) throws ClassCastException {
        if (o instanceof PointNode)
            return Triangulator.pointCompare(data,((PointNode)o).data);
        else if (o instanceof Point2d)
            return Triangulator.pointCompare(data,(Point2d)o);
        else
            throw new ClassCastException();
    }
}