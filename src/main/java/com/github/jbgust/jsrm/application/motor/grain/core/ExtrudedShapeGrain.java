package com.github.jbgust.jsrm.application.motor.grain.core;

import java.awt.*;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.Map;

import com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public abstract class ExtrudedShapeGrain extends ExtrudedGrain implements GrainConfigutation {

	protected BurningShape xsection = new BurningShape();
	private double webThickness = -1;

	@Override
	public double getGrainEndSurface(double burnProgression) {
		double regression = burnProgression * webThickness();
		return getEndsBurningSurfaceArea(regression);
	}

	@Override
	public double getGrainVolume(double burnProgression) {
		return getNumberOfSegments() * volume(burnProgression*webThickness());
	}

	@Override
	public double getBurningArea(double burnProgression) {
		return getNumberOfSegments() * surfaceArea(burnProgression*webThickness());
	}

	@Override
	public double getXincp(int numberOfPoints) {
		return webThickness()/numberOfPoints;
	}

	private double surfaceArea(double regression) {
		double zero = 0d;

		if (regression>webThickness())
			return zero;

		double rLen = regressedLength(regression);

		if (rLen<0)
			return zero;

		Area burn = getCrossSection(regression);

		if (burn.isEmpty())
			return zero;

		burn.subtract(getCrossSection(regression + .001));

		double sides = ShapeUtil.perimeter(burn) / 2 * rLen;
		double ends = getEndsBurningSurfaceArea(regression)*numberOfBurningEnds(regression);
		return sides+ends;

	}

	private double getEndsBurningSurfaceArea(double regression) {
		return getCachedArea(regression);
	}

	private Map<Double, Double> areaCache = new HashMap<>();

	private double getCachedArea(double regression) {
		return areaCache.computeIfAbsent(regression, currentRegression -> ShapeUtil.area(getCrossSection(regression)));
	}

	private double volume(double regression) {
		double zero = 0d;

		double rLen = regressedLength(regression);

		if (rLen<0d)
			return zero;

		double xSection = getCachedArea(regression);

		return xSection*rLen;

	}

	public double webThickness() {
		if ( webThickness != -1 )
			return webThickness;
		Area a = getCrossSection(0d);
		Rectangle r = a.getBounds();
		double max = Math.max(r.getWidth(), r.getHeight()); // The max size
		double min = 0;
		double guess;
		do {
			guess = min + (max - min) / 2; // Guess halfway through

			a = getCrossSection(guess);
			if (a.isEmpty()) {
				// guess is too big
				max = guess;
			} else {
				// min is too big
				min = guess;
			}
		} while (!((max - min) < .01));

		webThickness = guess;

		int ends = numberOfBurningEnds(0d);
		if (ends != 0 && webThickness>(getLength()/ends))
			webThickness = getLength()/ends;

		return webThickness;
	}

	private Area getCrossSection(double regression) {
		return xsection.getShape(regression);
	}

	public abstract int getNumberOfSegments();

}
