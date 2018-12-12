package com.jsrm.pressure;

import com.jsrm.calculation.Formula;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

public enum PressureFormulas implements Formula {

    CORE_DIAMETER("CORE_DIAMETER_previous + ci * 2 * xincp", empty(), of("ci", "xincp", "CORE_DIAMETER_previous"));

    private final Expression expression;
    private final Set<Formula> dependencies;

    PressureFormulas(String formula, Stream<String> dependencies, Stream<String>  variables) {

        ExpressionBuilder expressionBuilder = new ExpressionBuilder(formula);

        this.dependencies = dependencies
                .peek(s1 -> expressionBuilder.variable(s1))
                .map(PressureFormulas::valueOf)
                .collect(Collectors.toSet());

        variables.forEach(expressionBuilder::variable);

        expression = expressionBuilder.build();
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public Set<Formula> getDependencies() {
        return dependencies;
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }
}
