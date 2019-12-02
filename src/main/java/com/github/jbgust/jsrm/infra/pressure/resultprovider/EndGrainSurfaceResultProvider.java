package com.github.jbgust.jsrm.infra.pressure.resultprovider;

import com.github.jbgust.jsrm.application.motor.propellant.GrainConfigutation;

public class EndGrainSurfaceResultProvider extends AbstractGrainResultProvider {

    public static final String END_GRAIN_SURFACE_VARIABLE = "endGrainSurface";

    public EndGrainSurfaceResultProvider(GrainConfigutation grainConfigutation, int numberLineDuringBurnCalculation) {
        super(grainConfigutation, numberLineDuringBurnCalculation);
    }

    @Override
    public String getName() {
        return END_GRAIN_SURFACE_VARIABLE;
    }

    @Override
    public double getResult(int lineNumber) {
        return grainConfigutation.getGrainEndSurface(getProgression(lineNumber));
    }
}
