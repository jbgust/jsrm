package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.core.BurningShape;
import com.github.jbgust.jsrm.application.motor.grain.core.ExtrudedShapeGrain;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public class StarGrain extends ExtrudedShapeGrain {

	/**
	 * External grain diameter
	 */
	private final double outerDiameter;

	/**
	 * Distance from grain center to star inner points
	 */
	private final double innerDiameter;

	/**
	 * Distance from grain center to star outer point
	 */
	private final double pointDiameter;

	/**
	 * Number of star branches
	 */
	private final int pointCount;

	/**
	 * Number of segment
	 */
	private final int numberOfSegment;

	/**
	 *
	 * @param outerDiameter External grain diameter
	 * @param innerDiameter Distance from grain center to star inner points
	 * @param pointDiameter  Distance from grain center to star outer point
	 * @param pointCount Number of star branches
	 * @param numberOfSegment Number of segment
	 * @param segmentLength Length of the grain
	 * @param endSurface end segments surface are exposed to combustion or inhibited
	 */
	public StarGrain(double outerDiameter, double innerDiameter, double pointDiameter, int pointCount, int numberOfSegment, double segmentLength, GrainSurface endSurface) {
		this.outerDiameter = outerDiameter;
		this.innerDiameter = innerDiameter;
		this.pointDiameter = pointDiameter;
		this.pointCount = pointCount;
		this.numberOfSegment = numberOfSegment;

		setAftEndInhibited(endSurface);
		setForeEndInhibited(endSurface);
		setLength(segmentLength);
		generateGeometry();
	}


	private void generateGeometry(){
		double odmm = outerDiameter;
		double idmm = innerDiameter/2.0;
		double pdmm = pointDiameter/2.0;

		xsection = new BurningShape();
		Shape outside = new Ellipse2D.Double(-odmm/2, -odmm/2, odmm, odmm);
		xsection.add(outside);
		xsection.inhibit(outside);
		//xsection.subtract(new Ellipse2D.Double(-idmm/2, -idmm/2, idmm, idmm));
//		webThickness = null;

		GeneralPath p = new GeneralPath();
		double theta = 0;
		double dTheta = (2.0 * Math.PI) / ( pointCount * 2.0 );
		p.moveTo(0, idmm);
		for ( int i = 0; i < pointCount; i++ ){
			theta += dTheta;
			p.lineTo(pdmm*Math.sin(theta), pdmm*Math.cos(theta));
			theta += dTheta;
			p.lineTo(idmm*Math.sin(theta), idmm*Math.cos(theta));
		}
		p.closePath();
		xsection.subtract(new Area(p));
	}

	@Override
	public int getNumberOfSegments() {
		return numberOfSegment;
	}

	@Override
	public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
		if ( innerDiameter <= 0 ) {
			throw new InvalidMotorDesignException("Inner diameter should be > 0");
		}
		if ( outerDiameter <= 0 ) {
			throw new InvalidMotorDesignException("Outer diameter should be > 0");
		}
		if ( getLength() <= 0 ) {
			throw new InvalidMotorDesignException("Grain length should be > 0");
		}
		if ( innerDiameter > outerDiameter ) {
			throw new InvalidMotorDesignException("Inner diameter should be < than outer diameter");
		}
		if ( innerDiameter > pointDiameter ) {
			throw new InvalidMotorDesignException("Inner diameter should be < than point diameter");
		}
		if ( pointDiameter > outerDiameter ) {
			throw new InvalidMotorDesignException("Point diameter should be < than outer diameter");
		}

		if ( pointCount <= 0 ) {
			throw new InvalidMotorDesignException("Point count should be > 0");
		}

		if ( numberOfSegment <= 0 ) {
			throw new InvalidMotorDesignException("Number of segment should be > 0");
		}

		if(getLength() * getNumberOfSegments() > solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()) {
			throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
		}

		if(outerDiameter > solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()) {
			throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
		}
	}
}
