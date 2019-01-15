package com.jsrm.application.result;

import lombok.Value;
import net.objecthunter.exp4j.ExpressionBuilder;

@Value
public class Nozzle {
    private double nozzleThroatDiameterInMillimeter;
    private double chamberInsideDiameterInMillimeter;
    private double optimalNozzleExpansionRatio;
    private double optimalNozzleExitDiameterInMillimeter;
    private double nozzleExpansionRatio;
    private double nozzleExitDiameterInMillimeter;
    private double initialNozzleExitSpeedInMach;
    private double finalNozzleExitSpeedInMach;

    private static final String CROSS_SECTION_DIAMETER_VAR = "crossSectionDiameter";
    private static final String ANGLE_VAR = "angle";
    private static final ExpressionBuilder nozzleLenghtExpression = new ExpressionBuilder("crossSectionDiameter / 2 / tan(angle * pi / 180)")
            .variables(CROSS_SECTION_DIAMETER_VAR, ANGLE_VAR);

    /**
     * The length of the convergence in millimeter
     *
     * @param convergenceHalfAngleInDegree Suggested range: 25-50 degrees, 30 is typical
     * @return the length from the end of the motor chamber to the throat
     */
    public double getConvergenceLenghtInMillimeter(double convergenceHalfAngleInDegree) {
        return getLength(convergenceHalfAngleInDegree, chamberInsideDiameterInMillimeter - nozzleThroatDiameterInMillimeter);
    }

    /**
     * The length of the divergence in millimeter (can be the same of getOptimalDivergenceLenghtInMillimeter() if using optimal nozzle design)
     *
     * @param divergenceHalfAngleInDegree Suggested range: 10-15 degrees, 12 is typical
     * @return the length from the throat to the nozzle exit
     */
    public double getDivergenceLenghtInMillimeter(double divergenceHalfAngleInDegree) {
        return getLength(divergenceHalfAngleInDegree, nozzleExitDiameterInMillimeter - nozzleThroatDiameterInMillimeter);
    }

    /**
     * The optimal length of the divergence in millimeter (computed with optimalNozzleExitDiameterInMillimeter)
     *
     * @param divergenceHalfAngleInDegree Suggested range: 10-15 degrees, 12 is typical
     * @return the length from the throat to the nozzle exit
     */
    public double getOptimalDivergenceLenghtInMillimeter(double divergenceHalfAngleInDegree) {
        return getLength(divergenceHalfAngleInDegree, optimalNozzleExitDiameterInMillimeter - nozzleThroatDiameterInMillimeter);
    }

    private double getLength(double divergenceHalfAngleInDegree, double crossSectionDiameter) {
        return nozzleLenghtExpression
                .build()
                .setVariable(CROSS_SECTION_DIAMETER_VAR, crossSectionDiameter)
                .setVariable(ANGLE_VAR, divergenceHalfAngleInDegree)
                .evaluate();
    }
}
