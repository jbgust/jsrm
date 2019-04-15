package com.github.jbgust.jsrm.application.csv;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import com.github.jbgust.jsrm.application.result.MotorParameters;

class MotorPerformanceResultAggregator implements ArgumentsAggregator {

    @Override
    public MotorParameters aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        Double thrust = argumentsAccessor.getDouble(0);
        Double time = argumentsAccessor.getDouble(1);
        Double kn = argumentsAccessor.getDouble(2);
        Double chamberPressure = argumentsAccessor.getDouble(3);
        Double massFlowRate = argumentsAccessor.getDouble(4);

        return new MotorParameters(time, thrust, kn, chamberPressure, massFlowRate);
    }

}
