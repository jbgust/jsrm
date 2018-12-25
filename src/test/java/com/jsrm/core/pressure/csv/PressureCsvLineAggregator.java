package com.jsrm.core.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.pressure.PressureFormulas.*;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String LINE = "line number";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(LINE, argumentsAccessor.getDouble(0));

        csvValues.put(THROAT_AREA.getName(), argumentsAccessor.getDouble(6));
        csvValues.put(NOZZLE_CRITICAL_PASSAGE_AREA.getName(), argumentsAccessor.getDouble(7));

        csvValues.put(TIME_SINCE_BURN_STARTS.getName(), argumentsAccessor.getDouble(15));

        csvValues.put(CHAMBER_PRESSURE_MPA.getName(), argumentsAccessor.getDouble(26));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE.getName(), argumentsAccessor.getDouble(27));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE_PSIG.getName(), argumentsAccessor.getDouble(28));

        return csvValues;
    }

}
