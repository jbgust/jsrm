package com.github.jbgust.jsrm.infra.pressure.csv;

import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.absoluteChamberPressure;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.absoluteChamberPressurePSIG;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.chamberPressureMPA;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.kn;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.massFlowRate;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.nozzleCriticalPassageArea;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.throatArea;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.timeSinceBurnStart;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String LINE = "line number";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(LINE, argumentsAccessor.getDouble(0));

        csvValues.put(throatArea.name(), argumentsAccessor.getDouble(6));
        csvValues.put(nozzleCriticalPassageArea.name(), argumentsAccessor.getDouble(7));

        csvValues.put(timeSinceBurnStart.name(), argumentsAccessor.getDouble(15));

        csvValues.put(massFlowRate.name(), argumentsAccessor.getDouble(21));

        csvValues.put(chamberPressureMPA.name(), argumentsAccessor.getDouble(26));
        csvValues.put(absoluteChamberPressure.name(), argumentsAccessor.getDouble(27));
        csvValues.put(absoluteChamberPressurePSIG.name(), argumentsAccessor.getDouble(28));

        csvValues.put(kn.name(), argumentsAccessor.getDouble(32));

        return csvValues;
    }

}
