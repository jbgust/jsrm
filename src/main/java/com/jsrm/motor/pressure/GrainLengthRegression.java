package com.jsrm.motor.pressure;

import com.jsrm.motor.GrainSurface;

import static com.jsrm.motor.pressure.GrainEvolution.Evolution.REGRESSION;

public class GrainLengthRegression extends GrainEvolution {

    public GrainLengthRegression(WebRegression webRegression) {
        super(webRegression, REGRESSION);
    }

    @Override
    public double compute(int interval) {
        return getWebRegression().getPropellantGrain().getNumberOfSegment() * super.compute(interval);
    }

    @Override
    GrainSurface getSurface() {
        return getWebRegression().getPropellantGrain().getEndsSurface();
    }

    @Override
    double getInitialDimension() {
        return getWebRegression().getPropellantGrain().getSegmentLength();
    }
}
