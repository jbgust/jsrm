package com.jsrm.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.pressure.PressureFormulas.CORE_DIAMETER;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String INTERVAL = "Interval";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(INTERVAL, argumentsAccessor.getDouble(0));
        csvValues.put(CORE_DIAMETER.getName(), argumentsAccessor.getDouble(3));

        return csvValues;
    }

}
