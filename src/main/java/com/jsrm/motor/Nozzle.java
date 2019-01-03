package com.jsrm.motor;

import lombok.Value;

@Value
public class Nozzle {
    private double optimalNozzleExpansionRatio;
    private double optimalNozzleExitDiameterInMillimeter;
    private double nozzleExpansionRatio;
    private double nozzleExitDiameterInMillimeter;
}
