package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.core.BurningShape;
import com.github.jbgust.jsrm.application.motor.grain.core.RotatedShapeGrain;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class EndBurner extends RotatedShapeGrain {

	private final double length;
	private final double outerDiameter;
	private final double holeDiameter;
	private final double holeDepth;


	public EndBurner(double length, double outerDiameter, double holeDiameter, double holeDepth) {
		this.length = length;
		this.outerDiameter = outerDiameter;
		this.holeDiameter = holeDiameter;
		this.holeDepth = holeDepth;

		generateGeometry();
	}

	private void generateGeometry(){
		Rectangle2D.Double grain, punt, end;
		grain = new Rectangle2D.Double(0,0,outerDiameter/2.0,length);
		punt = new Rectangle2D.Double(0,length-holeDepth, holeDiameter/2.0, holeDepth);
		end = new Rectangle2D.Double(0,length,outerDiameter,0);

		shape = new BurningShape();
		web = -1;

		shape.add(grain);
		shape.inhibit(grain);
		shape.subtract(punt);
		shape.subtract(end);
	}

	@Override
	public double getGrainEndSurface(double burnProgression) {
		return getCachedBurningArea(getRegression(burnProgression));
	}

	private double getRegression(double burnProgression) {
		return burnProgression * webThickness();
	}


	private Map<Double, Double> burningAreaCache = new HashMap<>();

	private double getCachedBurningArea(double regression) {
		return burningAreaCache.computeIfAbsent(regression, currentRegression -> surfaceArea(regression));
	}

	@Override
	public double getGrainVolume(double burnProgression) {
		return volume(getRegression(burnProgression));
	}

	@Override
	public double getBurningArea(double burnProgression) {
		return getCachedBurningArea(getRegression(burnProgression));
	}

	@Override
	public double getXincp(int numberOfPoints) {
		return webThickness()/numberOfPoints;
	}

	@Override
	public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
		if ( outerDiameter <= 0 ) {
			throw new InvalidMotorDesignException("Outer diameter should be > 0");
		}
		if ( length <= 0 ) {
			throw new InvalidMotorDesignException("Grain length should be > 0");
		}

		if ( holeDiameter > outerDiameter ) {
			throw new InvalidMotorDesignException("Hole diameter should be < than outer diameter");
		}

		if ( holeDepth > length ) {
			throw new InvalidMotorDesignException("Hole length diameter should be < than grain length");
		}
	}

}
