package com.jsrm.motor;

import net.objecthunter.exp4j.ExpressionBuilder;

public class MotorChamber
{
    private final double chamberInnerDiameter;

    /**
     * Length from throat to bulkhead
     */
    private final double chamberLength;

    public MotorChamber(double chamberInnerDiameter, double chamberLength) {

        this.chamberInnerDiameter = chamberInnerDiameter;
        this.chamberLength = chamberLength;
    }

    public double getVolume() {
        return new ExpressionBuilder("pi * rc^2 * lc")
                .variables("rc", "lc")
                .build()
                .setVariable("rc", chamberInnerDiameter/2)
                .setVariable("lc", chamberLength)
                .evaluate();
    }
}
