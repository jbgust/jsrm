package com.jsrm.calculation.exception;

import com.jsrm.calculation.Formula;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Comparator.comparing;

public class LineCalculatorException extends RuntimeException {
    public LineCalculatorException(Formula formula, Map<String, Double> variables, int lineNumber, Map<String, Double> previousLineResults, Map<Formula, Double> currentLineResults, Exception exception) {
        super(format("Failed to compute %s in line %s:\n" +
                        "formula :\n\t%s\n" +
                        "variables :\n\t%s\n" +
                        "current line results :\n\t%s\n" +
                        "previous line result :\n\t%s\n",
                    formula.getName(),
                    lineNumber,
                    formula.getExpressionAsString(),
                    mapToString(variables),
                    currentResultToString(currentLineResults),
                    mapToString(previousLineResults)),
             exception);
    }

    private static String currentResultToString(Map<Formula, Double> currentLineResults) {
        return currentLineResults.entrySet().stream()
                .sorted(comparing(formulaDoubleEntry -> formulaDoubleEntry.getKey().getName()))
                .map(entry -> entry.getKey()+" = "+entry.getValue())
                .collect(Collectors.joining("\n\t"));
    }

    private static String mapToString(Map<String, Double> variables) {
        return variables.entrySet().stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(entry -> entry.getKey()+" = "+entry.getValue())
                .collect(Collectors.joining("\n\t"));
    }
}
