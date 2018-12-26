package com.jsrm.core.pressure;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Map;
import java.util.Set;

import static com.jsrm.core.JSRMConstant.*;

/**
 * This class is used to solve the third 'solve button' in Excel file
 * it resolve the tbinc variable with is the time increment after all the propellant is consumed.
 */
class IncrementTimeBurstSolver {

    static final String NB_LINE_VARIABLE = "nbLine";
    Expression tbincExpression;

    IncrementTimeBurstSolver() {
         tbincExpression = new ExpressionBuilder("(vc log(expectedPfinal / pbout)) / (-rat * to * astarf * nbLine * (1000000000/cstar))")
                .variables(vc.name(), expectedPfinal.name(), pbout.name(), rat.name(), to.name(), astarf.name(), cstar.name())
                .variable(NB_LINE_VARIABLE)
                .build();
    }

    public double solve(Map<String, Double> variables) throws IllegalArgumentException {
        return tbincExpression
                .setVariables(variables)
                .evaluate();
    }

    public Set<String> getVariablesNames() {
        return tbincExpression.getVariableNames();
    }
}
