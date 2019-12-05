package com.github.jbgust.jsrm.application.motor.grain.core;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.HashSet;
import java.util.Set;

public class ShapeUtil {
	private ShapeUtil(){}

	/*
	 * Return the Area of a singular polygon (NO HOLES OR DISJOINT PARTS).
	 * Coordinates assumed to be in MM.
	 * http://valis.cs.uiuc.edu/~sariel/research/CG/compgeom/msg00831.html
	 * http://stackoverflow.com/questions/451426/how-do-i-calculate-the-surface-area-of-a-2d-polygon
	 * http://www.wikihow.com/Calculate-the-Area-of-a-Polygon
	 * According to http://www.geog.ubc.ca/courses/klink/gis.notes/ncgia/u33.html
	 * this algorithm works OK with holes, and it seems to (see test)
	 */
	public static double area(Shape a) {
		//if ( !a.isSingular() )
			//throw new IllegalArgumentException("Can not calculate area of non-singular shape!");
		PathIterator i = a.getPathIterator(new AffineTransform(), .001);


		double x = 0, y = 0, sx = 0, sy = 0;
		double nx, ny;
		double area = 0;
		while (!i.isDone()) {
			double[] coords = new double[6];
			int type = i.currentSegment(coords);
			switch( type ){
			case PathIterator.SEG_CLOSE:
				//Go back to the start
				nx = sx;
				ny = sy;
				area += x * ny;
				area -= y * nx;
				break;
			case PathIterator.SEG_LINETO:
				nx = coords[0];
				ny = coords[1];
				area += x * ny;
				area -= y * nx;

				//Remember the last points
				x = nx;
				y = ny;

				break;
			case PathIterator.SEG_MOVETO:
				//Remember the starting point
				x = sx = coords[0];
				y = sy = coords[1];
				break;
			default:
				throw new Error("Bad segment type from Flattening Path Iterator");
			}
			i.next();
		}

		area = area / 2.0; // Result so far is double the signed area

		if ( area < 0 ){ //Depending on winding it could be negative
			area = area * -1.0;
		}


		return area;
	}

	public static double perimeter(Shape a) {
		//TODO: I think I need to handle seg_close!!
		PathIterator i = a.getPathIterator(new AffineTransform(), .001);
		double x = 0, y = 0;
		double len = 0;
		while (!i.isDone()) {
			double[] coords = new double[6];
			int type = i.currentSegment(coords);
			if (type == PathIterator.SEG_LINETO) {
				double nx = coords[0];
				double ny = coords[1];
				len += Math.sqrt(Math.pow(x - nx, 2) + Math.pow(y - ny, 2));
				x = nx;
				y = ny;
			} else if (type == PathIterator.SEG_MOVETO) {
				x = coords[0];
				y = coords[1];
			}
			i.next();
		}
		return len;
	}

	/*
	 * Separate an area into multiple distinct area.
	 * Area CAN NOT HAVE HOLES. HOLES WILL BE RETURNED AS AREAS,
	 * SO A DONUT WILL TURN INTO TWO CIRCLES.
	 */
	public static Set<java.awt.geom.Area> separate(java.awt.geom.Area a) {
		Set<java.awt.geom.Area> res = new HashSet<java.awt.geom.Area>();
		PathIterator i = a.getPathIterator(new AffineTransform());
		GeneralPath cur = null;

		while (!i.isDone()) {
			double[] coords = new double[6];
			int type = i.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_CLOSE:
				cur.closePath();
				if (cur != null ){
					java.awt.geom.Area area = new java.awt.geom.Area(cur);
					if ( !a.isEmpty() )
						res.add(area);
				}
				cur = new GeneralPath(i.getWindingRule());
				break;
			case PathIterator.SEG_MOVETO:
				if (cur != null ){
					java.awt.geom.Area area = new java.awt.geom.Area(cur);
					if ( !a.isEmpty() )
						res.add(area);
				}
				cur = new GeneralPath(i.getWindingRule());
				cur.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO:
				cur.curveTo(coords[0], coords[1], coords[2], coords[3],
						coords[4], coords[5]);
				break;
			case PathIterator.SEG_LINETO:
				cur.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_QUADTO:
				cur.quadTo(coords[0], coords[1], coords[2], coords[3]);
				break;

			}
			i.next();
		}

		return res;
	}


}
