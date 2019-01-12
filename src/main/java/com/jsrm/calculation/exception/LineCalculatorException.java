package com.jsrm.calculation.exception;

import com.jsrm.calculation.Formula;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class LineCalculatorException extends RuntimeException {
    public LineCalculatorException(Formula formula, Map<String, Double> variables, int lineNumber, Exception e) {
        super(format("Failed to compute %s in line %s:\nformula :\n\t%s\nvariables :\n\t%s",
                formula.getName(),
                lineNumber,
                formula.getExpressionAsString(),
                variables.entrySet().stream()
                        .map(entry -> entry.getKey()+" = "+entry.getValue())
                        .collect(Collectors.joining("\n\t"))), e);
    }
}
