package com.jsrm.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.pressure.PressureFormulas.GRAIN_CORE_DIAMETER;
import static com.jsrm.pressure.PressureFormulas.GRAIN_OUTSIDE_DIAMETER;
import static com.jsrm.pressure.PressureFormulas.WEB_THICKNESS;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String INTERVAL = "Interval";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(INTERVAL, argumentsAccessor.getDouble(0));
        csvValues.put(WEB_THICKNESS.getName(), argumentsAccessor.getDouble(2));
        csvValues.put(GRAIN_CORE_DIAMETER.getName(), argumentsAccessor.getDouble(3));
        csvValues.put(GRAIN_OUTSIDE_DIAMETER.getName(), argumentsAccessor.getDouble(4));

        return csvValues;
    }

}
