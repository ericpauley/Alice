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

public class Triangulator {
	private java.util.Vector contours;
	public java.util.Vector points;

	public java.util.Vector triangles;

	public static PointComparator pc = new PointComparator();

	public Triangulator() {
		triangles = new java.util.Vector();
		contours = new java.util.Vector();
		points = new java.util.Vector();
	}

	// ********************
	// ** List Functions **
	// ********************
	private java.util.Vector linkedListToVector(PointNode head) {
		java.util.Vector returnVal = new java.util.Vector();

		PointNode cur = head;

		do {
			returnVal.add(cur);
			cur = cur.next;
		} while (cur != head && cur != null);
		return returnVal;
	}

	private void reverseContour(PointNode first) {
		PointNode cur = first.next;
		first.next = first.prev;
		first.prev = cur;

		while (cur != first && cur != null) {
			PointNode temp = cur.next;
			cur.next = cur.prev;
			cur.prev = temp;
			cur = temp;
		}
		contours.setElementAt(first.next, contours.indexOf(first));
	}

	public int indexOfPoint(Point2d tofind) {
		// return java.util.Arrays.binarySearch(points,tofind,new
		// PointComparator());
		java.util.ListIterator li = points.listIterator();
		for (int i = 0; li.hasNext(); i++) {
			if (pointCompare((Point2d) li.next(), tofind) == 0) {
				return i;
			}
		}
		return -1;
	}

	// *************************
	// ** Geometric Functions **
	// *************************
	public static int pointCompare(Point2d p1, Point2d p2) {
		if (p1.x < p2.x) {
			return -1;
		}
		if (p1.x > p2.x) {
			return 1;
		}
		if (p1.y < p2.y) {
			return -1;
		}
		if (p1.y > p2.y) {
			return 1;
		}
		return 0;
	}

	private boolean intersectsContour(PointNode head, SegmentBBox seg) {
		PointNode cur = head;

		do {
			if (seg.segmentOverlaps(cur.data, cur.next.data)) {
				return true;
			}
			cur = cur.next;
		} while (cur != head && cur != null);
		return false;
	}

	private double polygonArea(PointNode head) {
		PointNode cur = head.next;
		double area = 0;
		while (cur.next != head && cur != null) {
			area += Triangle.signedArea(head.data, cur.data, cur.next.data);
			cur = cur.next;
		}
		return area;
	}

	// *************************
	// ** Public Input Setter **
	// *************************
	public void addContour(Point2d[] contour) {
		if (contour.length < 1) {
			return;
		}

		int curpoint = points.size();

		PointNode first = new PointNode(contour[0]);
		first.next = first;
		first.prev = first;
		points.add(contour[0]);

		for (int i = 1; i < contour.length; i++) {
			PointNode newPoint = new PointNode(contour[i]);
			first.prev.insertAfter(newPoint);
			points.add(contour[i]);
		}

		contours.add(first);
	}

	public void addContour(java.util.Vector contour) {
		if (contour.isEmpty()) {
			return;
		}

		int curpoint = points.size();

		PointNode first = new PointNode((Point2d) contour.firstElement());
		first.next = first;
		first.prev = first;
		points.add(contour.firstElement());

		java.util.ListIterator li = contour.listIterator(1);
		while (li.hasNext()) {
			Point2d cur = (Point2d) li.next();
			PointNode newPoint = new PointNode(cur);
			first.prev.insertAfter(newPoint);
			points.add(cur);
		}

		contours.add(first);
	}

	// ********************
	// ** Debugging **
	// ********************

	public void debug(String str) {
		System.out.println("-------------------------------------");
		System.out.println(str);
		System.out.println("-------------------------------------");
		System.out.println("Points");
		for (int i = 0; i < points.size(); i++) {
			System.out.println(String.valueOf(i) + ": (" + String.valueOf(((Point2d) points.elementAt(i)).x) + "," + String.valueOf(((Point2d) points.elementAt(i)).y) + ")");
		}
		System.out.println();
		System.out.println("Contours");
		for (int i = 0; i < contours.size(); i++) {
			System.out.print("Contour " + String.valueOf(i) + ": ");
			PointNode cur = (PointNode) contours.elementAt(i);
			do {
				System.out.print(String.valueOf(indexOfPoint(cur.data)) + ",");
				cur = cur.next;
			} while (cur != contours.elementAt(i) && cur != null);
			System.out.println();
		}
	}

	public void debug2(String str) {
		System.out.println("-------------------------------------");
		System.out.println(str);
		System.out.println("-------------------------------------");
		System.out.println("Points");
		for (int i = 0; i < points.size(); i++) {
			System.out.println(String.valueOf(i) + ": (" + String.valueOf(((Point2d) points.elementAt(i)).x) + "," + String.valueOf(((Point2d) points.elementAt(i)).y) + ")");
		}
		System.out.println();
		System.out.println("Contours");
		for (int i = 0; i < contours.size(); i++) {
			System.out.println("Contour " + String.valueOf(i));
			PointNode cur = (PointNode) contours.elementAt(i);
			do {
				System.out.println(String.valueOf(indexOfPoint(cur.data)) + ": " + String.valueOf(isEar(cur)) + "," + String.valueOf(cur.convex()));
				cur = cur.next;
			} while (cur != contours.elementAt(i) && cur != null);
			System.out.println();
		}
	}

	// ********************
	// ** public Compute **
	// ********************
	public java.util.Vector triangulate() {
		// debug("Input");
		sortData();
		// debug("Sorted");
		removeDuplicates();
		adjustOrientations();
		// TODO: geometric hashing
		buildBridges();
		// debug("W/ Bridges");

		triangles.clear();

		boolean changed = true;
		while (changed) {
			changed = false;

			java.util.Vector ears = determineEars();

			java.util.ListIterator li = ears.listIterator();
			while (li.hasNext()) {
				if (clipEar((PointNode) li.next())) {
					changed = true;
				}
			}
		}

		if (linkedListToVector((PointNode) contours.firstElement()).size() == 3) {
			triangles.add(((PointNode) contours.firstElement()).triangle());
		} else {
			// TODO: get really mad!!!!
		}

		return triangles;
	}

	// ****************
	// ** Steps **
	// ****************
	private void sortData() {
		if (points.isEmpty() || contours.isEmpty()) {
			return;
		}

		// sort points
		java.util.Collections.sort(points, pc);

		// find leftmost point in each contour
		java.util.ListIterator li = contours.listIterator();
		for (int i = 0; li.hasNext(); i++) {

			PointNode first = (PointNode) li.next();
			PointNode left = first;

			PointNode cur = first.next;

			while (cur != first && cur != null) {
				if (cur.compareTo(left) < 0) {
					left = cur;
				}
				cur = cur.next;
			}
			contours.setElementAt(left, i);
		}

		// sort contours left to right (leftmost must still be the outside one)
		java.util.Collections.sort(contours);
	}

	private void removeDuplicates() {
		Point2d prevPoint = (Point2d) points.firstElement();
		java.util.ListIterator li2 = points.listIterator(1);
		while (li2.hasNext()) {
			Point2d curPoint = (Point2d) li2.next();
			boolean makeChange = false;
			while (pointCompare(curPoint, prevPoint) == 0) {
				li2.remove();
				makeChange = true;
				if (!li2.hasNext()) {
					break;
				}
				curPoint = (Point2d) li2.next();
			}
			if (makeChange) {
				java.util.ListIterator li = contours.listIterator();
				while (li.hasNext()) {
					PointNode first = (PointNode) li.next();
					PointNode cur = first;

					do {
						if (cur.compareTo(prevPoint) == 0) {
							cur.data = prevPoint;
						}
						cur = cur.next;
					} while (cur != first && cur != null);
				}
			} else {
				prevPoint = curPoint;
			}
		}

	}

	private void adjustOrientations() {
		if (polygonArea((PointNode) contours.firstElement()) < 0) {
			reverseContour((PointNode) contours.firstElement());
		}
		java.util.ListIterator li = contours.listIterator(1);
		while (li.hasNext()) {
			PointNode cur = (PointNode) li.next();
			if (polygonArea(cur) > 0) {
				reverseContour(cur);
			}
		}
	}

	private void buildBridges() {
		DistanceComparator dc = new DistanceComparator();

		java.util.ListIterator li = contours.listIterator();
		while (li.hasNext()) {
			PointNode cur = (PointNode) li.next();

			java.util.Vector outer = linkedListToVector((PointNode) contours.firstElement());
			dc.start = cur.data;
			java.util.Collections.sort(outer, dc);

			java.util.ListIterator li2 = outer.listIterator();
			while (li2.hasNext()) {
				PointNode outerPoint = (PointNode) li2.next();
				if (outerPoint.compareTo(cur) == 0) {
					cur.next.prev = outerPoint;
					outerPoint.next.prev = cur;
					PointNode temp = cur.next;
					cur.next = outerPoint.next;
					outerPoint.next = temp;
					break;
				} else if (outerPoint.inCone(cur.data) && !intersectsContour((PointNode) contours.firstElement(), new SegmentBBox(cur.data, outerPoint.data))) {
					PointNode n1 = new PointNode(cur.data);
					PointNode n2 = new PointNode(outerPoint.data);

					n1.next = n2;
					n1.prev = cur.prev;
					n2.next = outerPoint.next;
					n2.prev = n1;
					n1.prev.next = n1;
					n2.next.prev = n2;
					outerPoint.next = cur;
					cur.prev = outerPoint;
					break;
				}
			}
		}
		PointNode first = (PointNode) contours.firstElement();
		contours = new java.util.Vector(1);
		contours.add(first);

	}

	private boolean isEar(PointNode ear) {
		return ear.convex() > 0 && !intersectsContour((PointNode) contours.firstElement(), new SegmentBBox(ear.prev.data, ear.next.data)) && ear.next.inCone(ear.prev.data) && ear.prev.inCone(ear.next.data);
	}

	private java.util.Vector determineEars() {
		java.util.Vector ears = new java.util.Vector();
		PointNode head = (PointNode) contours.firstElement();
		PointNode cur = head;

		do {
			if (isEar(cur)) {
				ears.add(cur);
			}

			cur = cur.next;
		} while (cur != head && cur != null);

		return ears;
	}

	private boolean clipEar(PointNode ear) {
		if (!isEar(ear)) {
			return false;
		}
		triangles.add(ear.triangle());

		ear.prev.next = ear.next;
		ear.next.prev = ear.prev;
		if (contours.firstElement() == ear) {
			contours.setElementAt(ear.next, 0);
		}

		return true;
	}
}