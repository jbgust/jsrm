package com.jsrm.calculation.formulas;

import com.jsrm.calculation.Formula;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

public class Formula3 implements Formula {

    private final Expression expression = new ExpressionBuilder("4 * formula3_previous")
            .variable("formula3_previous")
            .build();

    @Override
    public String getName() {
        return "formula3";
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public Set<Formula> getDependencies() {
        return emptySet();
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }
}
