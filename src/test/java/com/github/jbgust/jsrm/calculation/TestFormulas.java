package com.github.jbgust.jsrm.calculation;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

public enum TestFormulas implements Formula {

    FORMULA_1("(4 - FORMULA_2) / constant1", of("FORMULA_2"), of("constant1")),
    FORMULA_2("e^2 * FORMULA_3", of("FORMULA_3"), of("e")),
    FORMULA_3("4 * FORMULA_3_previous", empty(), of("FORMULA_3_previous")),
    FORMULA_4("2 * FUNCTION_A", empty(), of("FUNCTION_A")),


    FORMULA_5("2 * FORMULA_6", of("FORMULA_6"), empty()),
    FORMULA_6("3", empty(), empty()),

    FORMULA_7("4 * PROVIDED_DATA_previous", empty(), of("PROVIDED_DATA_previous"));

    private final Expression expression;
    private final Set<String> dependencies;
    private final String expressionAsString;

    TestFormulas(String formula, Stream<String> dependencies, Stream<String>  variables) {

        expressionAsString = formula;
        ExpressionBuilder expressionBuilder = new ExpressionBuilder(expressionAsString);

        this.dependencies = dependencies
                .peek(expressionBuilder::variable)
                .collect(Collectors.toSet());

        variables.forEach(expressionBuilder::variable);

        expression = expressionBuilder.build();
    }

    @Override
    public String getExpressionAsString() {
        return expressionAsString;
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
        return dependencies.stream().map(TestFormulas::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }
}
