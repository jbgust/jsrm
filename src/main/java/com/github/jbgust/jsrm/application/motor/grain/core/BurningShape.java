package com.github.jbgust.jsrm.application.motor.grain.core;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Set;

public class BurningShape {

	private static class RegressableShape {
		private Area a;
		public RegressableShape( Shape s ){
			if ( s instanceof Area )
				a = (Area)s;
			a = new Area(s);
		}

		public Area getRegressedShape(double regression){
			if ( regression == 0 )
				return a;

			//Build these separatly because intersecting the line
			//with the circle creates a small amount of error which
			//shows up when the resulting edge is no longer exactly
			//colinear with the original edge.
			Area rRect = new Area();
			Area rCirc = new Area();

			PathIterator i = a.getPathIterator(new AffineTransform(), .001);
			double[] last = {0,0};
			double[] first = {0,0};

			while (!i.isDone()) {
				double[] coords = new double[6];
				int type = i.currentSegment(coords);
				switch (type){
					case PathIterator.SEG_MOVETO:
						first[0] = last[0] = coords[0];
						first[1] = last[1] = coords[1];
						break;
					case PathIterator.SEG_CLOSE:
						coords[0] = first[0];
						coords[1] = first[1];
					case PathIterator.SEG_LINETO:
						//Calculate the normal to this edge
						double dx = coords[0]-last[0];
						double dy = coords[1]-last[1];
						double len = Math.sqrt(dx*dx + dy*dy);
						double[] normal = {-dy/len,dx/len};

						//Calculate the displacement of the endpoints
						//to create a rect
						double[] displacement = {regression*normal[0], regression*normal[1]};

						//Create that rect. Winding does not seem to matter...
						GeneralPath p = new GeneralPath();
						p.moveTo(last[0], last[1]);
						p.lineTo(last[0]+displacement[0], last[1]+displacement[1]);
						p.lineTo(coords[0]+displacement[0], coords[1]+displacement[1]);
						p.lineTo(coords[0], coords[1]);
						p.closePath();
						rRect.add( new Area(p));

						double er = Math.abs(regression);
						rCirc.add(new Area(new Ellipse2D.Double(coords[0]-er, coords[1]-er, 2*er, 2*er)));

						last[0] = coords[0];
						last[1] = coords[1];
						break;
					case PathIterator.SEG_CUBICTO:
					case PathIterator.SEG_QUADTO:
						throw new Error("Unflattend Geometry!");
				}
				i.next();
			}


			if ( regression > 0 ){
				//Combine all together
				rRect.add(a);
				rRect.add(rCirc);
			}else{
				Area acp = (Area)a.clone();
				acp.subtract(rRect);
				acp.subtract(rCirc);
				rRect = acp;
			}
			return rRect;
		}

	}
	private static class ShapeAndTrans{

		Shape shape;
		AffineTransform trans;
		ShapeAndTrans(Shape s, AffineTransform t){
			shape = s;
			trans = t;
		}
		ShapeAndTrans(Shape s){
			shape = s;
			trans = null;
		}
		@Override public int hashCode(){
			if ( trans != null )
				return shape.hashCode() * trans.hashCode();
			return shape.hashCode();
		}
		@Override public boolean equals(Object o){
			if ( o instanceof ShapeAndTrans ){
				ShapeAndTrans s = (ShapeAndTrans)o;
				if ( !s.shape.equals(shape) )
					return false;
				if ( s.trans == null && trans != null )
					return false;
				if ( s.trans != null && trans == null )
					return false;
				return trans == null || trans.equals(s.trans);
			}
			return false;
		}

	}
	public void add(Shape s){
		plus.add(new ShapeAndTrans(s));
	}

	public void subtract(Shape s){
		minus.add(new ShapeAndTrans(s));
	}

	public void subtract(Shape s, AffineTransform t){
		minus.add(new ShapeAndTrans(s,t));
	}

	public void inhibit(Shape s){
		inhibited.add(new ShapeAndTrans(s));
	}

	/*
	 * 9 times out of 10 we get asked for the same thing
	 * 2x in a row, for volume and for area
	 */
	private double lastRegression = -1;
	private Area lastArea = null;
	public Area getShape(double regression) {
			if ( regression==lastRegression ){
				return lastArea;
			}

		Area a = new Area();
		for (ShapeAndTrans st : plus){
			Shape s = st.shape;
			if ( !inhibited.contains(st) )
				s = regress(st.shape, regression, true);
			if ( st.trans != null )
				s = st.trans.createTransformedShape(s);
			a.add(new Area(s));
		}
		for (ShapeAndTrans st : minus){
			Shape s = st.shape;
			if ( !inhibited.contains(st) )
				s = regress(st.shape, regression, false);
			if ( st.trans != null )
				s = st.trans.createTransformedShape(s);
			a.subtract(new Area(s));
		}

			lastRegression = regression;
			lastArea = a;
		return a;
	}

	private Set<ShapeAndTrans> plus = new HashSet<>();
	private Set<ShapeAndTrans> minus = new HashSet<>();
	private Set<ShapeAndTrans> inhibited = new HashSet<>();

	private Shape regress(Shape s, double mm, boolean plus) {
		if (s instanceof Ellipse2D) {
			Ellipse2D e = (Ellipse2D) s;

			double d = plus ? -2 * mm : 2 * mm;

			double w = e.getWidth() + d;
			double h = e.getHeight() + d;
			double x = e.getX() - d / 2;
			double y = e.getY() - d / 2;

			return new Ellipse2D.Double(x, y, w, h);
		} else if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D) s;

			if ( plus ){
				double d = -2 * mm;
				double w = r.getWidth() + d;
				double h = r.getHeight() + d;
				double x = r.getX() - d / 2;
				double y = r.getY() - d / 2;
				return new Rectangle2D.Double(x, y, w, h);
			} else {
				//A rectangular hole gets rounded corners as it grows
				Area a = new Area();
				double d = 2 * mm;

				a.add(new Area(new RoundRectangle2D.Double(
						r.getX() - d / 2,
						r.getY() - d / 2,
						r.getWidth() + d,
						r.getHeight() + d,
						d,
						d
						)));

				return a;
			}

		} else {
			RegressableShape r = new RegressableShape(s);
			System.out.println("Warning: Complex (non circle / square) geometry slows me down.");
			return r.getRegressedShape(mm * (plus?-1:1));
		}

	}


}
