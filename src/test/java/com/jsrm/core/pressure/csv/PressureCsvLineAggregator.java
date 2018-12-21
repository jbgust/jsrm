package com.jsrm.core.pressure.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.pressure.PressureFormulas.*;

public class PressureCsvLineAggregator implements ArgumentsAggregator {

    public static final String INTERVAL = "Interval";

    @Override
    public Map<String, Double> aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
        HashMap<String, Double> csvValues = new HashMap<>();

        csvValues.put(INTERVAL, argumentsAccessor.getDouble(0));

        csvValues.put(WEB_THICKNESS.getName(), argumentsAccessor.getDouble(2));
        csvValues.put(GRAIN_CORE_DIAMETER.getName(), argumentsAccessor.getDouble(3));
        csvValues.put(GRAIN_OUTSIDE_DIAMETER.getName(), argumentsAccessor.getDouble(4));
        csvValues.put(GRAIN_LENGTH.getName(), argumentsAccessor.getDouble(5));
        csvValues.put(THROAT_AREA.getName(), argumentsAccessor.getDouble(6));
        csvValues.put(NOZZLE_CRITICAL_PASSAGE_AREA.getName(), argumentsAccessor.getDouble(7));

        csvValues.put(EROSIVE_BURN_FACTOR.getName(), argumentsAccessor.getDouble(10));

        csvValues.put(TEMPORARY_CHAMBER_PRESSURE.getName(), argumentsAccessor.getDouble(11));
        csvValues.put(PROPELLANT_BURN_RATE.getName(), argumentsAccessor.getDouble(14));
        csvValues.put(TIME_SINCE_BURN_STARTS.getName(), argumentsAccessor.getDouble(15));

        csvValues.put(MASS_GENERATION_RATE.getName(), argumentsAccessor.getDouble(20));
        csvValues.put(NOZZLE_MASS_FLOW_RATE.getName(), argumentsAccessor.getDouble(21));
        csvValues.put(MASS_STORAGE_RATE.getName(), argumentsAccessor.getDouble(22));
        csvValues.put(MASS_COMBUSTION_PRODUCTS.getName(), argumentsAccessor.getDouble(23));
        csvValues.put(DENSITY_COMBUSTION_PRODUCTS.getName(), argumentsAccessor.getDouble(24));

        csvValues.put(CHAMBER_PRESSURE_MPA.getName(), argumentsAccessor.getDouble(26));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE.getName(), argumentsAccessor.getDouble(27));
        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE_PSIG.getName(), argumentsAccessor.getDouble(28));
        csvValues.put(AI.getName(), argumentsAccessor.getDouble(29));

        csvValues.put(GRAIN_VOLUME.getName(), argumentsAccessor.getDouble(16));

        csvValues.put(ABSOLUTE_CHAMBER_PRESSURE_PSIG.name(), argumentsAccessor.getDouble(28));

        return csvValues;
    }

}
