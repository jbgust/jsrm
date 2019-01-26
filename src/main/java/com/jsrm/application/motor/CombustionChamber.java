package com.jsrm.application.motor;

import com.jsrm.infra.function.CircleAreaFunction;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CombustionChamber
{
    private final double chamberInnerDiameterInMillimeter;

    /**
     * Length from throat to bulkhead
     */
    private final double chamberLengthInMillimeter;

    /**
     * Create a motor combustion chamber
     * @param chamberInnerDiameterInMillimeter diameter of the combustion chamber
     * @param chamberLengthInMillimeter Length from throat to bulkhead
     */
    public CombustionChamber(double chamberInnerDiameterInMillimeter, double chamberLengthInMillimeter) {

        this.chamberInnerDiameterInMillimeter = chamberInnerDiameterInMillimeter;
        this.chamberLengthInMillimeter = chamberLengthInMillimeter;
    }

    public double getVolume() {
        return new ExpressionBuilder("CircleArea(dc) * lc")
                .variables("dc", "lc")
                .function(new CircleAreaFunction())
                .build()
                .setVariable("dc", chamberInnerDiameterInMillimeter)
                .setVariable("lc", chamberLengthInMillimeter)
                .evaluate();
    }

    public double getChamberInnerDiameterInMillimeter() {
        return chamberInnerDiameterInMillimeter;
    }

    public double getChamberLengthInMillimeter() {
        return chamberLengthInMillimeter;
    }
}
