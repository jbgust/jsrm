package com.jsrm.infra.performance.solver;

import com.google.common.collect.Range;
import com.jsrm.application.motor.propellant.SolidPropellant;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Range.closed;
import static com.jsrm.infra.JSRMConstant.k;

public class MachSpeedAtNozzleExitSolver {

    private static final String INITIAL_MACH_SPEED_VARIABLE = "me";

    private static final double MIN_MACH_EXIT_SPEED = 0.2;
    private static final double MAX_MACH_EXIT_SPEED = 10.0;
    private static final double SOLVER_PRECISION = 0.0001;
    private static final String NOZZLE_EXPANSION_RATION_VARIABLE = "nozzleExpansionRation";

    private final Range<Double> EXPECTED_RESULT_RANGE = closed(-SOLVER_PRECISION, SOLVER_PRECISION);
    private final Expression expression;
    private final SolidPropellant solidPropellant;


    public MachSpeedAtNozzleExitSolver(SolidPropellant solidPropellant) {
        this.solidPropellant = solidPropellant;
        expression = new ExpressionBuilder("nozzleExpansionRation-1/me*((1+(k-1)/2*me^2)/(1+(k-1)/2))^((k+1)/2/(k-1))")
                .variables(NOZZLE_EXPANSION_RATION_VARIABLE, k.name(), INITIAL_MACH_SPEED_VARIABLE)
                .build();
    }

    public double solve(double nozzleExpansionRation) {
        return nozzleExpansionRation < 2 ? under2RationSolver(nozzleExpansionRation) : over2RatioSolver(nozzleExpansionRation);
    }

    private double over2RatioSolver(double nozzleExpansionRation) {
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

    public double under2RationSolver(double nozzleExpansionRation){

        if(nozzleExpansionRation == 1){
            return 1;
        }

        expression.setVariable(k.name(), solidPropellant.getK());
        expression.setVariable(NOZZLE_EXPANSION_RATION_VARIABLE, nozzleExpansionRation);
        Map<Double, Double> result = new LinkedHashMap<>();

        for(double exprat = 0.5 ; exprat < 10 ; exprat+=0.0001){
            result.put(exprat,
                    Math.abs(expression.setVariable(INITIAL_MACH_SPEED_VARIABLE, exprat).evaluate()));
        }

        return result.entrySet().stream().min((doubleDoubleEntry, t1) -> Double.compare(doubleDoubleEntry.getValue(), t1.getValue())).get().getKey();
    }
}
