package com.github.jbgust.jsrm.infra.pressure.resultprovider;

import com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation;

public class GrainVolumeResultProvider extends AbstractGrainResultProvider {

    public static final String GRAIN_VOLUME_VARIABLE = "grainVolume";

    public GrainVolumeResultProvider(GrainConfigutation grainConfigutation, int numberLineDuringBurnCalculation) {
        super(grainConfigutation, numberLineDuringBurnCalculation);
    }

    @Override
    public String getName() {
        return GRAIN_VOLUME_VARIABLE;
    }

    @Override
    public double getResult(int lineNumber) {
        return grainConfigutation.getGrainVolume(getProgression(lineNumber));
    }


}
