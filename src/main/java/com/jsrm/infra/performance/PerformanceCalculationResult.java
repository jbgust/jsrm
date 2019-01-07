package com.jsrm.infra.performance;

import java.util.List;
import java.util.Map;

import lombok.Value;

@Value
public class PerformanceCalculationResult {
    Map<PerformanceCalculation.Results, List<Double>> results;
    double optimalNozzleExpansionResult;
}
