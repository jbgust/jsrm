package com.jsrm.calculation.formulas;

import com.jsrm.calculation.Formula;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.assertj.core.util.Sets;

import java.util.Set;
import java.util.stream.Collectors;

public class Formula2 implements Formula {

    private final Set<Formula> dependencies = Sets.newLinkedHashSet(new Formula3());

    private final Expression expression = new ExpressionBuilder("e^2 * formula3")
            .variables(getVariablesDependenciesNames())
            .variable("e")
            .build();

    @Override
    public String getName() {
        return "formula2";
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

    private Set<String> getVariablesDependenciesNames() {
        return getDependencies().stream().map(Formula::getName).collect(Collectors.toSet());
    }
}
