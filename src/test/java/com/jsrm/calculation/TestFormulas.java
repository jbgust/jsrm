package com.jsrm.calculation;

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
    FORMULA_3("4 * FORMULA_3_previous", empty(), of("FORMULA_3_previous"));

    private final Expression expression;
    private final Set<String> dependencies;

    TestFormulas(String formula, Stream<String> dependencies, Stream<String>  variables) {

        ExpressionBuilder expressionBuilder = new ExpressionBuilder(formula);

        this.dependencies = dependencies
                .peek(s1 -> expressionBuilder.variable(s1))
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
        return dependencies.stream().map(TestFormulas::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }
}
