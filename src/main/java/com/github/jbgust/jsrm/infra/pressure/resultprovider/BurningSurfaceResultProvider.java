package com.github.jbgust.jsrm.infra.pressure.resultprovider;

import com.github.jbgust.jsrm.application.motor.propellant.GrainConfigutation;

public class BurningSurfaceResultProvider extends AbstractGrainResultProvider {

    public static final String BURNING_SURFACE_VARIABLE = "burningSurface";

    public BurningSurfaceResultProvider(GrainConfigutation grainConfigutation, int numberLineDuringBurnCalculation) {
        super(grainConfigutation, numberLineDuringBurnCalculation);
    }

    @Override
    public String getName() {
        return BURNING_SURFACE_VARIABLE;
    }

    @Override
    public double getResult(int lineNumber) {
        return grainConfigutation.getBurningArea(getProgression(lineNumber));
    }
}
