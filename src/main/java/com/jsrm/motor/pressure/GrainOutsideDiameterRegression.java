package com.jsrm.motor.pressure;

import com.jsrm.motor.GrainSurface;

public class GrainOutsideDiameterRegression extends GrainEvolution {

    public GrainOutsideDiameterRegression(WebRegression webRegression) {
        super(webRegression, Evolution.REGRESSION);
    }

    @Override
    GrainSurface getSurface() {
        return getWebRegression().getPropellantGrain().getOuterSurface();
    }

    @Override
    double getInitialDimension() {
        return getWebRegression().getPropellantGrain().getOuterDiameter();
    }
}
