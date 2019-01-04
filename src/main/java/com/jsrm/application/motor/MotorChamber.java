package com.jsrm.application.motor;

import com.jsrm.infra.function.CircleAreaFunction;

import lombok.Getter;
import net.objecthunter.exp4j.ExpressionBuilder;

@Getter
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
        return new ExpressionBuilder("CircleArea(dc) * lc")
                .variables("dc", "lc")
                .function(new CircleAreaFunction())
                .build()
                .setVariable("dc", chamberInnerDiameter)
                .setVariable("lc", chamberLength)
                .evaluate();
    }
}
