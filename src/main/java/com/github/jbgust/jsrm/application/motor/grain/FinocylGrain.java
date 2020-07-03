package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.core.BurningShape;
import com.github.jbgust.jsrm.application.motor.grain.core.ExtrudedShapeGrain;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public class FinocylGrain extends ExtrudedShapeGrain {

	/**
	 * External grain diameter
	 */
	private final double outerDiameter;

	/**
	 * Internal grain diameter
	 */
	private final double innerDiameter;

	/**
	 * Fin width
	 */
	private final double finWidth;

	/**
	 * fin diameter = (length from grain center to the end of the fin) * 2
	 */
	private final double finDiameter;

	/**
	 * Number of fins
	 */
	private final int finCount;

	/**
	 * Number of segment
	 */
	private final int numberOfSegment;

	/**
	 *
	 * @param outerDiameter External grain diameter
	 * @param innerDiameter Internal grain diameter
	 * @param finWidth Fin width
	 * @param finDiameter fin diameter = (length from grain center to the end of the fin) * 2
	 * @param finCount Number of fins
	 * @param length Length of the grain
	 * @param numberOfSegment Number of segment
	 * @param endSurface end segments surface are exposed to combustion or inhibited
	 */
	public FinocylGrain(double outerDiameter, double innerDiameter, double finWidth, double finDiameter, int finCount, double length, int numberOfSegment, GrainSurface endSurface){
		this.outerDiameter = outerDiameter;
		this.innerDiameter = innerDiameter;
		this.finWidth = finWidth;
		this.finDiameter = finDiameter;
		this.finCount = finCount;
		this.numberOfSegment = numberOfSegment;
		setAftEndInhibited(endSurface);
		setForeEndInhibited(endSurface);
		setLength(length);
		generateGeometry();
	}

	private void generateGeometry(){

		xsection = new BurningShape();
		Shape outside = new Ellipse2D.Double(-outerDiameter /2, -outerDiameter /2, outerDiameter, outerDiameter);
		xsection.add(outside);
		xsection.inhibit(outside);
		xsection.subtract(new Ellipse2D.Double(-innerDiameter /2, -innerDiameter /2, innerDiameter, innerDiameter));

		for ( int i = 0; i < finCount; i++ ){
			Shape fin = new Rectangle2D.Double(-finWidth/2,0,finWidth,finDiameter/2);
			xsection.subtract(fin, AffineTransform.getRotateInstance(i*(2.0*Math.PI/finCount)));
		}
	}

	@Override
	public double getGrainOuterDiameter(double burnProgression) {
		return outerDiameter;
	}

	@Override
	public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
		if ( innerDiameter == 0 ) {
			throw new InvalidMotorDesignException("Inner diameter should be > 0");
		}
		if ( outerDiameter ==0 ) {
			throw new InvalidMotorDesignException("Outer diameter should be > 0");
		}
		if ( getLength()==0 ) {
			throw new InvalidMotorDesignException("Grain length should be > 0");
		}
		if ( numberOfSegment ==0 ) {
			throw new InvalidMotorDesignException("Number of segment should be > 0");
		}
		if ( innerDiameter > outerDiameter){
			throw new InvalidMotorDesignException("Inner diameter should be < than outer diameter");
		}
		if(getLength() * getNumberOfSegments() > solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()) {
			throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
		}
		if(outerDiameter > solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()) {
			throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
		}
		//TODO : check sur le nombre fin?
	}

	@Override
	public int getNumberOfSegments() {
		return numberOfSegment;
	}
}
