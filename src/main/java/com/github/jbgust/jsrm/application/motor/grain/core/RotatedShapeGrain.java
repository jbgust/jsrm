package com.github.jbgust.jsrm.application.motor.grain.core;

import com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public abstract class RotatedShapeGrain implements GrainConfigutation {

	public enum Quality {
		High()
			{{
				 surfaceAreaStep = .001;
				 squareFlatteningError = 0.001;
				 squareSubdivide = .01;
				 areaFlatteningError = .001;
			}},
		Low()			{{
			 surfaceAreaStep = .001;
			 squareFlatteningError = .1;
			 squareSubdivide = .1;
			 areaFlatteningError = .1;
		}};

		double surfaceAreaStep = .001;
		double squareFlatteningError = 0.001;
		double squareSubdivide = .01;
		double areaFlatteningError = .001;
	}

	protected Quality quality = Quality.Low;

	protected BurningShape shape = new BurningShape();

	protected double web = -1;

	public double surfaceArea(double regression) {
		if (regression> webThickness())
			return 0;

		Area burn = shape.getShape(regression);

		if (burn.isEmpty())
			return 0;

		burn.subtract(shape.getShape(regression + quality.surfaceAreaStep));

		double sqmm = yRotatedSurfaceArea(burn);

		return sqmm / 2;
	}

	public double volume(double regression) {
		Shape squared = square(shape.getShape(regression));
		double sum = 0d;
		//for( Area a: ShapeUtil.separate(squared) ){
		//	sum = sum.plus( ShapeUtil.area(a) );
		//}
		sum = ShapeUtil.area(squared);
		double v = sum * Math.PI;
		return v;
	}

	public double webThickness() {
		if (web != -1)
			return web;

		Area a = shape.getShape(0);
		Rectangle r = a.getBounds();
		double max = r.getWidth() < r.getHeight() ? r.getHeight() : r
				.getWidth(); // The max size
		double min = 0;
		double guess;
		while (true) {
			guess = min + (max - min) / 2; // Guess halfway through
			a = shape.getShape(guess);
			if (a.isEmpty()) {
				// guess is too big
				max = guess;
			} else {
				// min is too big
				min = guess;
			}
			if ((max - min) < .01)
				break;
		}
		web = guess;

		return web;

	}

	private Shape square(Shape a) {
		PathIterator i = a.getPathIterator(new AffineTransform(), quality.squareFlatteningError);
		GeneralPath cur = new GeneralPath();

		double last[] = {0,0};
		while (!i.isDone()) {
			double coords[] = new double[6];
			int type = i.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_CLOSE:
				cur.closePath();
				break;
			case PathIterator.SEG_MOVETO:
				cur.moveTo(Math.pow(coords[0],2), coords[1]);
				last[0] = coords[0];
				last[1] = coords[1];
				break;
			case PathIterator.SEG_CUBICTO:
				throw new Error("Non-flattened geometry!");
			case PathIterator.SEG_LINETO:
				double x = last[0];
				double y = last[1];
				double len = Math.sqrt(Math.pow(last[0]-coords[0], 2) + Math.pow(last[1]-coords[1], 2));
				int steps = (int)(len / quality.squareSubdivide) + 5;
				for (int s = 0; s < steps; s++) {
					x += (coords[0] - last[0]) / steps;
					y += (coords[1] - last[1]) / steps;
					cur.lineTo(Math.pow(x, 2), y);
				}
				last[0] = coords[0];
				last[1] = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				throw new Error("Non-flattened geometry!");

			}
			i.next();
		}
		return cur;
	}

	private double yRotatedSurfaceArea(Shape a) {
		PathIterator i = a.getPathIterator(new AffineTransform(), quality.areaFlatteningError);
		double x = 0, y = 0;
		double mx = 0, my = 0;
		double len = 0;
		while (!i.isDone()) {
			double coords[] = new double[6];
			int type = i.currentSegment(coords);
			if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_CLOSE) {


				double nx = coords[0];
				double ny = coords[1];

				if ( type == PathIterator.SEG_CLOSE ){
					nx = mx;
					ny = my;
				}

				double dy = Math.abs(y-ny);
				double dx = Math.abs(x-nx);
				double xl = x>nx?x:nx;
				double xs = x<nx?x:nx;

				double add = 0;
				if ( dx == 0 ){
					//Cylender
					add = 2 * Math.PI * xl * dy;
				} else if ( dy == 0 ){
					//disk
					 add = Math.PI * xl * xl - Math.PI * xs * xs;
				}else{
					double h = xl/dx * dy;
					double s1 = Math.sqrt(xl*xl + h*h);
					double s2 = Math.sqrt(xs*xs + (h-dy)*(h-dy));
					add = Math.PI * (xl*s1 - xs*s2);
				}

				len += add;

				x = nx;
				y = ny;
			} else if (type == PathIterator.SEG_MOVETO) {
				mx = x = coords[0];
				my = y = coords[1];
			} else {
				throw new Error("Non-flattened geometry!");
			}
			i.next();
		}
		return len;
	}

}
