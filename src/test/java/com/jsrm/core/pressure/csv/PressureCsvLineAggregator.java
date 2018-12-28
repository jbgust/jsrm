package com.jsrm.core.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.pressure.ChamberPressureCalculation.Results.*;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String LINE = "line number";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(LINE, argumentsAccessor.getDouble(0));

        csvValues.put(throatArea.name(), argumentsAccessor.getDouble(6));
        csvValues.put(nozzleCriticalPassageArea.name(), argumentsAccessor.getDouble(7));

        csvValues.put(timeSinceBurnStart.name(), argumentsAccessor.getDouble(15));

        csvValues.put(chamberPressureMPA.name(), argumentsAccessor.getDouble(26));
        csvValues.put(absoluteChamberPressure.name(), argumentsAccessor.getDouble(27));
        csvValues.put(absoluteChamberPressurePSIG.name(), argumentsAccessor.getDouble(28));

        return csvValues;
    }

}
