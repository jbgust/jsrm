package com.github.jbgust.jsrm.application.motor.grain.core;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public abstract class ExtrudedGrain {
	private boolean foreEndInhibited = false;
	private boolean aftEndInhibited = false;
	private double length =100d;

	//TODO : Bill what is that?
	private double endLight = 0d;

	protected int numberOfBurningEnds(double regression){
		if ( regression<endLight )
			return 0;
		return (foreEndInhibited?0:1) + (aftEndInhibited?0:1);
	}

	public double regressedLength(double regression){
		if ( regression<endLight )
			return length;
		return length-((regression-endLight)*(numberOfBurningEnds(regression)));
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setForeEndInhibited(GrainSurface grainSurface) {
		this.foreEndInhibited = grainSurface == INHIBITED;
	}

	public void setAftEndInhibited(GrainSurface grainSurface) {
		this.aftEndInhibited = grainSurface == INHIBITED;
	}

	public boolean isForeEndInhibited() {
		return foreEndInhibited;
	}

	public boolean isAftEndInhibited() {
		return aftEndInhibited;
	}
}
