package com.github.jbgust.jsrm.application.csv;

import com.github.jbgust.jsrm.application.result.ThrustResult;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

class ThrustResultAggregator implements ArgumentsAggregator {

    @Override
    public ThrustResult aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {

        Double thrust = argumentsAccessor.getDouble(0);
        Double time = argumentsAccessor.getDouble(1);
        return new ThrustResult(thrust, time);
    }

}
