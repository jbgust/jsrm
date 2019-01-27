package com.github.jbgust.jsrm.calculation;

import net.objecthunter.exp4j.Expression;

import java.util.Set;

public interface Formula {

    String PREVIOUS_VARIABLE_SUFFIX = "_previous";

    String getName();

    Expression getExpression();

    String getExpressionAsString();

    Set<Formula> getDependencies();

    Set<String> getVariablesNames();

}
