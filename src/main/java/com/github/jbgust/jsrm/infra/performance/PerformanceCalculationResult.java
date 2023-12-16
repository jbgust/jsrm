package com.github.jbgust.jsrm.infra.performance;

import java.util.List;
import java.util.Map;

public record PerformanceCalculationResult (
    Map<PerformanceCalculation.Results, List<Double>> results,
    double optimalNozzleExpansionResult,
    double initialNozzleExitSpeedInMach,
    double finalNozzleExitSpeedInMach,
    double optimalNozzleExitDiameterInMillimeter,
    double nozzleExitDiameterInMillimeter
){}
