package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.core.BurningShape;
import com.github.jbgust.jsrm.application.motor.grain.core.ExtrudedShapeGrain;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public class MoonBurnerGrain extends ExtrudedShapeGrain {

	/**
	 * Grain outer diameter
	 */
	private final double outerDiameter;

	/**
	 * Grain core diameter (can be 0)
	 */
	private final double coreDiameter;

	/**
	 * Core offset
	 */
	private final double coreOffset;

	/**
	 * Number of segment
	 */
	private final int numberOfSegment;

	public MoonBurnerGrain(double outerDiameter, double coreDiameter, double coreOffset, int numberOfSegment, double length, GrainSurface endSurface){
		this.outerDiameter = outerDiameter;
		this.coreDiameter = coreDiameter;
		this.coreOffset = coreOffset;
		this.numberOfSegment = numberOfSegment;
		setAftEndInhibited(endSurface);
		setForeEndInhibited(endSurface);
		setLength(length);
		generateGeometry();
	}

	private void generateGeometry() {
		double odmm = outerDiameter;
		double idmm = coreDiameter;
		double offmm = coreOffset;
		xsection = new BurningShape();
		Shape outside = new Ellipse2D.Double(0, 0, odmm, odmm);
		xsection.add(outside);
		xsection.inhibit(outside);

		xsection.subtract(new Ellipse2D.Double(odmm/2 - idmm/2 + offmm, odmm/2 - idmm/2, idmm, idmm));
	}

	@Override
	public int getNumberOfSegments() {
		return numberOfSegment;
	}

	@Override
	public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {

		if (coreDiameter <= 0) {
			throw new InvalidMotorDesignException("Core diameter should be positive");
		}
		if (outerDiameter <= 0) {
			throw new InvalidMotorDesignException("Outer diameter should be > 0");
		}
		if (getLength() <= 0) {
			throw new InvalidMotorDesignException("Grain length should be > 0");
		}

		if (numberOfSegment <= 0) {
			throw new InvalidMotorDesignException("Number of segment should be > 0");
		}

		if (coreDiameter >= outerDiameter) {
			throw new InvalidMotorDesignException("Core diameter should be < than outer diameter");
		}
		if (getLength() * getNumberOfSegments() > solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()) {
			throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
		}
		if (outerDiameter > solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()) {
			throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
		}
		if (coreOffset < 0) {
			throw new InvalidMotorDesignException("Core offset should be positive");
		}

		if (coreOffset >= (coreDiameter + outerDiameter) / 2) {
			throw new InvalidMotorDesignException("Core offset should be inside the grain");
		}
	}

}
