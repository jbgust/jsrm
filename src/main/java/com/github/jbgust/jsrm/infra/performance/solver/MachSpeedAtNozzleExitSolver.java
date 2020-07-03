package com.github.jbgust.jsrm.infra.performance.solver;

import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.calculation.exception.DichotomicSolveFailedException;
import com.google.common.collect.Range;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Range.closed;
import static com.github.jbgust.jsrm.infra.JSRMConstant.k;

public class MachSpeedAtNozzleExitSolver {

    private static final String INITIAL_MACH_SPEED_VARIABLE = "me";

    private static final double MIN_MACH_EXIT_SPEED = 0.2;
    private static final double MAX_MACH_EXIT_SPEED = 10.0;
    private static final double SOLVER_PRECISION = 0.0001;
    private static final int MAX_DICHOTOMIC_LOOP = 100;
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
        return nozzleExpansionRation < 2 ? slowMachSpeedSolver(nozzleExpansionRation) : fastMachSpeedSolverSolver(nozzleExpansionRation);
    }

    /**
     * This solver doesn't work for expand ratio near 1
     * @param nozzleExpansionRation
     * @return mach exit speed
     */
    private double fastMachSpeedSolverSolver(double nozzleExpansionRation) {
        boolean isSolved = false;
        expression.setVariable(k.name(), solidPropellant.getK());
        expression.setVariable(NOZZLE_EXPANSION_RATION_VARIABLE, nozzleExpansionRation);

        Range<Double> initialMachSpeedAtNozzleExitRange = closed(MIN_MACH_EXIT_SPEED, MAX_MACH_EXIT_SPEED);

        try {
            return dichotomicSolve(isSolved, initialMachSpeedAtNozzleExitRange);
        } catch (DichotomicSolveFailedException e) {
            //fallback if solver failed
            return slowMachSpeedSolver(nozzleExpansionRation);
        }
    }

    private double dichotomicSolve(boolean isSolved, Range<Double> initialMachSpeedAtNozzleExitRange) throws DichotomicSolveFailedException {
        double machSpeedAtNozzleExit = -1;
        int iteration=0;
        while(!isSolved) {

            if(iteration > MAX_DICHOTOMIC_LOOP){
                throw new DichotomicSolveFailedException();
            }

            machSpeedAtNozzleExit = initialMachSpeedAtNozzleExitRange.lowerEndpoint() + (initialMachSpeedAtNozzleExitRange.upperEndpoint() - initialMachSpeedAtNozzleExitRange.lowerEndpoint()) / 2;
            double result = expression.setVariable(INITIAL_MACH_SPEED_VARIABLE, machSpeedAtNozzleExit).evaluate();

            if(EXPECTED_RESULT_RANGE.contains(result)){
                isSolved = true;
            } else if (result < 0){
                initialMachSpeedAtNozzleExitRange = closed(initialMachSpeedAtNozzleExitRange.lowerEndpoint(), machSpeedAtNozzleExit);
            } else {
                initialMachSpeedAtNozzleExitRange = closed(machSpeedAtNozzleExit, initialMachSpeedAtNozzleExitRange.upperEndpoint());
            }
            iteration++;
        }
        return machSpeedAtNozzleExit;
    }

    private double slowMachSpeedSolver(double nozzleExpansionRation){

        if(nozzleExpansionRation == 1){
            return 1;
        }

        expression.setVariable(k.name(), solidPropellant.getK());
        expression.setVariable(NOZZLE_EXPANSION_RATION_VARIABLE, nozzleExpansionRation);
        Map<Double, Double> result = new LinkedHashMap<>();

        for(double machSpeedAtNozzleExit = 0.5 ; machSpeedAtNozzleExit < 10 ; machSpeedAtNozzleExit+=0.0001){
            result.put(machSpeedAtNozzleExit,
                    Math.abs(expression.setVariable(INITIAL_MACH_SPEED_VARIABLE, machSpeedAtNozzleExit).evaluate()));
        }

        return result.entrySet().stream().min(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey();
    }

}
