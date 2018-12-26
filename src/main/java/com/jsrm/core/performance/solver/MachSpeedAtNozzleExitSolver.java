package com.jsrm.core.performance.solver;

import com.google.common.collect.Range;
import com.jsrm.motor.propellant.SolidPropellant;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import static com.google.common.collect.Range.closed;
import static com.jsrm.core.JSRMConstant.k;

public class MachSpeedAtNozzleExitSolver {

    private static final String INITIAL_MACH_SPEED_VARIABLE = "me";

    private static final double MIN_MACH_EXIT_SPEED = 0.2;
    private static final double MAX_MACH_EXIT_SPEED = 10.0;
    public static final double SOLVER_PRECISION = 0.0001;
    public static final String NOZZLE_EXPANSION_RATION_VARIABLE = "nozzleExpansionRation";

    private final Range<Double> EXPECTED_RESULT_RANGE = closed(-SOLVER_PRECISION, SOLVER_PRECISION);
    private final Expression expression;


    public MachSpeedAtNozzleExitSolver() {
        expression = new ExpressionBuilder("nozzleExpansionRation-1/me*((1+(k-1)/2*me^2)/(1+(k-1)/2))^((k+1)/2/(k-1))")
                .variables(NOZZLE_EXPANSION_RATION_VARIABLE, k.name(), INITIAL_MACH_SPEED_VARIABLE)
                .build();
    }

    public double solve(double nozzleExpansionRation, SolidPropellant solidPropellant) {
        boolean isSolved = false;
        expression.setVariable(k.name(), solidPropellant.getK());
        expression.setVariable(NOZZLE_EXPANSION_RATION_VARIABLE, nozzleExpansionRation);

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