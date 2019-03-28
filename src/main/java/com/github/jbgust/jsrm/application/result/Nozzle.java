package com.github.jbgust.jsrm.application.result;

import net.objecthunter.exp4j.ExpressionBuilder;

public class Nozzle {

    private static final String CROSS_SECTION_DIAMETER_VAR = "crossSectionDiameter";
    private static final String ANGLE_VAR = "angle";
    private static final ExpressionBuilder nozzleLenghtExpression = new ExpressionBuilder("crossSectionDiameter / 2 / tan(angle * pi / 180)")
            .variables(CROSS_SECTION_DIAMETER_VAR, ANGLE_VAR);

    private final double nozzleThroatDiameterInMillimeter;
    private final double chamberInsideDiameterInMillimeter;
    private final double optimalNozzleExpansionRatio;
    private final double optimalNozzleExitDiameterInMillimeter;
    private final double nozzleExpansionRatio;
    private final double nozzleExitDiameterInMillimeter;
    private final double initialNozzleExitSpeedInMach;
    private final double finalNozzleExitSpeedInMach;

    public Nozzle(double nozzleThroatDiameterInMillimeter, double chamberInsideDiameterInMillimeter, double optimalNozzleExpansionRatio, double optimalNozzleExitDiameterInMillimeter, double nozzleExpansionRatio, double nozzleExitDiameterInMillimeter, double initialNozzleExitSpeedInMach, double finalNozzleExitSpeedInMach) {
        this.nozzleThroatDiameterInMillimeter = nozzleThroatDiameterInMillimeter;
        this.chamberInsideDiameterInMillimeter = chamberInsideDiameterInMillimeter;
        this.optimalNozzleExpansionRatio = optimalNozzleExpansionRatio;
        this.optimalNozzleExitDiameterInMillimeter = optimalNozzleExitDiameterInMillimeter;
        this.nozzleExpansionRatio = nozzleExpansionRatio;
        this.nozzleExitDiameterInMillimeter = nozzleExitDiameterInMillimeter;
        this.initialNozzleExitSpeedInMach = initialNozzleExitSpeedInMach;
        this.finalNozzleExitSpeedInMach = finalNozzleExitSpeedInMach;
    }

    public double getNozzleThroatDiameterInMillimeter() {
        return nozzleThroatDiameterInMillimeter;
    }

    public double getChamberInsideDiameterInMillimeter() {
        return chamberInsideDiameterInMillimeter;
    }

    public double getOptimalNozzleExpansionRatio() {
        return optimalNozzleExpansionRatio;
    }

    public double getOptimalNozzleExitDiameterInMillimeter() {
        return optimalNozzleExitDiameterInMillimeter;
    }

    public double getNozzleExpansionRatio() {
        return nozzleExpansionRatio;
    }

    public double getNozzleExitDiameterInMillimeter() {
        return nozzleExitDiameterInMillimeter;
    }

    public double getInitialNozzleExitSpeedInMach() {
        return initialNozzleExitSpeedInMach;
    }

    public double getFinalNozzleExitSpeedInMach() {
        return finalNozzleExitSpeedInMach;
    }

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
