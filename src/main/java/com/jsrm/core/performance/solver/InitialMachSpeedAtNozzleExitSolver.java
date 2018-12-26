package com.jsrm.core.performance.solver;

import com.google.common.collect.Range;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Map;

import static com.google.common.collect.Range.closed;
import static com.jsrm.core.JSRMConstant.exprat;
import static com.jsrm.core.JSRMConstant.k;

public class InitialMachSpeedAtNozzleExitSolver {

    private static final String INITIAL_MACH_SPEED_VARIABLE = "me";

    private static final double MIN_MACH_EXIT_SPEED = 0.2;
    private static final double MAX_MACH_EXIT_SPEED = 10.0;
    public static final double SOLVER_PRECISION = 0.0001;

    private final Range<Double> EXPECTED_RESULT_RANGE = closed(-SOLVER_PRECISION, SOLVER_PRECISION);
    private final Expression expression;


    public InitialMachSpeedAtNozzleExitSolver() {
        expression = new ExpressionBuilder("exprat-1/me*((1+(k-1)/2*me^2)/(1+(k-1)/2))^((k+1)/2/(k-1))")
                .variables(exprat.name(), k.name(), INITIAL_MACH_SPEED_VARIABLE)
                .build();
    }

    public double solve(Map<String, Double> variables) {
        boolean isSolved = false;
        expression.setVariables(variables);
        Range<Double> initialMachSpeedAtNozzleExitRange = closed(MIN_MACH_EXIT_SPEED, MAX_MACH_EXIT_SPEED);

        double initialMachSpeedAtNozzle = -1;
        while(!isSolved) {

            initialMachSpeedAtNozzle = initialMachSpeedAtNozzleExitRange.lowerEndpoint() + (initialMachSpeedAtNozzleExitRange.upperEndpoint() - initialMachSpeedAtNozzleExitRange.lowerEndpoint()) / 2;
            double result = expression.setVariable(INITIAL_MACH_SPEED_VARIABLE, initialMachSpeedAtNozzle).evaluate();

            if(EXPECTED_RESULT_RANGE.contains(result)){
                isSolved = true;
            } else if (result < 0){
                initialMachSpeedAtNozzleExitRange = closed(initialMachSpeedAtNozzleExitRange.lowerEndpoint(), initialMachSpeedAtNozzle);
            } else {
                initialMachSpeedAtNozzleExitRange = closed(initialMachSpeedAtNozzle, initialMachSpeedAtNozzleExitRange.upperEndpoint());
            }

        }

        return initialMachSpeedAtNozzle;
    }
}
