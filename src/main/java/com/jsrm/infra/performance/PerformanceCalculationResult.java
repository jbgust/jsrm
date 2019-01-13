package com.jsrm.infra.performance;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class PerformanceCalculationResult {
    Map<PerformanceCalculation.Results, List<Double>> results;
    double optimalNozzleExpansionResult;
    private double initialNozzleExitSpeedInMach;
    private double finalNozzleExitSpeedInMach;
    private double optimalNozzleExitDiameterInMillimeter;
    private double nozzleExitDiameterInMillimeter;
}
