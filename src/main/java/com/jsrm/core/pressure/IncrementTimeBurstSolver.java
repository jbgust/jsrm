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
public class IncrementTimeBurstSolver {

    Expression tbincExpression;

    public IncrementTimeBurstSolver() {
         tbincExpression = new ExpressionBuilder("(vc log(pfinal / pbout)) / (-rat * to * astarf * nbLine * (1000000000/cstar))")
                .variables(vc.name(), pfinal.name(), pbout.name(), rat.name(), to.name(), astarf.name(), cstar.name())
                .variable("nbLine")
                .build();
    }

    public double solve(Map<String, Double> variables) throws Exception {
        return tbincExpression
                .setVariables(variables)
                .evaluate();
    }

    public Set<String> getVariablesNames() {
        return tbincExpression.getVariableNames();
    }
}
