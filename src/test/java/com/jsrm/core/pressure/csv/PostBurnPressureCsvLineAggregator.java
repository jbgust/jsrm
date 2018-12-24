package com.jsrm.core.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.pressure.PostBurnPressureFormulas.*;

public class PostBurnPressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String LINE = "line number";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(LINE, argumentsAccessor.getDouble(0));

        csvValues.put(TIME_SINCE_BURN_STARTS.getName(), argumentsAccessor.getDouble(1));
        csvValues.put(CHAMBER_PRESSURE_MPA.getName(), argumentsAccessor.getDouble(2));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE.getName(), argumentsAccessor.getDouble(3));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE_PSIG.getName(), argumentsAccessor.getDouble(4));


        return csvValues;
    }

}
