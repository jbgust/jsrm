package com.github.jbgust.jsrm.infra.pressure.solver;

import com.github.jbgust.jsrm.calculation.exception.DichotomicSolveFailedException;
import com.google.common.collect.Range;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import static com.github.jbgust.jsrm.infra.JSRMConstant.k;
import static com.google.common.collect.Range.closed;

public class CoreMachSpeedSolver {

    private static final String CORE_MACH_SPEED_VARIABLE = "ma";

    private static final double MIN_MACH_EXIT_SPEED = 0.01;
    private static final double MAX_MACH_EXIT_SPEED = 1.0;
    private static final double SOLVER_PRECISION = 0.0001;
    private static final int MAX_DICHOTOMIC_LOOP = 100;

    private final Range<Double> EXPECTED_RESULT_RANGE = closed(-SOLVER_PRECISION, SOLVER_PRECISION);
    private final Expression expression;
    private final double ratioOfSpecificHeats;


    public CoreMachSpeedSolver(double ratioOfSpecificHeats) {
        this.ratioOfSpecificHeats = ratioOfSpecificHeats;
        expression = new ExpressionBuilder("apt - (1/ma)*((2+(k-1)ma^2)/(1+k))^((k+1)/2/(k-1))")
                .variables(k.name(), CORE_MACH_SPEED_VARIABLE, "apt")
                .build();
    }

    public double solve(double portToThroatAreaRatio)  {
        try {
            return solveCoreMachSpeed(portToThroatAreaRatio);
        } catch (DichotomicSolveFailedException e) {
            return -1D;
        }
    }

    private double solveCoreMachSpeed(double portToThroatAreaRatio) throws DichotomicSolveFailedException {
        boolean isSolved = false;
        expression.setVariable(k.name(), ratioOfSpecificHeats);
        expression.setVariable("apt", portToThroatAreaRatio);

        Range<Double> initialMachSpeedRange = closed(MIN_MACH_EXIT_SPEED, MAX_MACH_EXIT_SPEED);

        return dichotomicSolve(isSolved, initialMachSpeedRange);

    }

    private double dichotomicSolve(boolean isSolved, Range<Double> initialMachSpeedRange) throws DichotomicSolveFailedException {
        double currentCoreMachNumber = -1;
        int iteration=0;
        while(!isSolved) {

            if(iteration > MAX_DICHOTOMIC_LOOP){
                throw new DichotomicSolveFailedException();
            }

            currentCoreMachNumber = initialMachSpeedRange.lowerEndpoint() + (initialMachSpeedRange.upperEndpoint() - initialMachSpeedRange.lowerEndpoint()) / 2;
            double result = expression.setVariable(CORE_MACH_SPEED_VARIABLE, currentCoreMachNumber).evaluate();

            if(EXPECTED_RESULT_RANGE.contains(result)){
                isSolved = true;
            } else if (result > 0){
                initialMachSpeedRange = closed(initialMachSpeedRange.lowerEndpoint(), currentCoreMachNumber);
            } else {
                initialMachSpeedRange = closed(currentCoreMachNumber, initialMachSpeedRange.upperEndpoint());
            }
            iteration++;
        }
        return currentCoreMachNumber;
    }
}
