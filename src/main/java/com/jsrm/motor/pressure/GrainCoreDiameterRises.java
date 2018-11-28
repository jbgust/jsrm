package com.jsrm.motor.pressure;

import com.jsrm.motor.GrainSurface;

public class GrainCoreDiameterRises extends GrainEvolution {

    public GrainCoreDiameterRises(WebRegression webRegression) {
        super(webRegression, Evolution.RISE);
    }

    @Override
    GrainSurface getSurface() {
        return getWebRegression().getPropellantGrain().getCoreSurface();
    }

    @Override
    double getInitialDiameter() {
        return getWebRegression().getPropellantGrain().getCoreDiameter();
    }
}
