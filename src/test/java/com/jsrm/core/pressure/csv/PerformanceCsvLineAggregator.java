package com.jsrm.core.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.performance.PerformanceFormulas.*;
import static com.jsrm.core.pressure.ChamberPressureCalculation.*;

public class PerformanceCsvLineAggregator implements ArgumentsAggregator {


    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(chamberPressureMPA, argumentsAccessor.getDouble(0));
        csvValues.put(CHAMBER_PRESSUER_PA.getName(), argumentsAccessor.getDouble(1));
        csvValues.put(nozzleCriticalPassageArea, argumentsAccessor.getDouble(2));
        csvValues.put(throatArea, argumentsAccessor.getDouble(3));
        csvValues.put(NOZZLE_EXPANSION_RATIO.getName(), argumentsAccessor.getDouble(4));
        csvValues.put(NOZZLE_EXIT_PRESSURE.getName(), argumentsAccessor.getDouble(5));
        csvValues.put(OPTIMUM_NOZZLE_EXPANSION_RATIO.getName(), argumentsAccessor.getDouble(6));
        csvValues.put(DELIVERED_THRUST_COEFFICIENT.getName(), argumentsAccessor.getDouble(7));
        csvValues.put(THRUST.getName(), argumentsAccessor.getDouble(8));
        csvValues.put(timeSinceBurnStart, argumentsAccessor.getDouble(10));
        csvValues.put(DELIVERED_IMPULSE.getName(), argumentsAccessor.getDouble(11));
        csvValues.put(MACH_SPEED_AT_NOZZLE_EXIT.getName(), argumentsAccessor.getDouble(12));

        return csvValues;
    }

}
