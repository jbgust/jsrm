package com.jsrm.application.result;

import lombok.Value;

@Value
public class Nozzle {
    private double optimalNozzleExpansionRatio;
    private double optimalNozzleExitDiameterInMillimeter;
    private double nozzleExpansionRatio;
    private double nozzleExitDiameterInMillimeter;
    private double initialNozzleExitSpeedInMach;
    private double finalNozzleExitSpeedInMach;
}
