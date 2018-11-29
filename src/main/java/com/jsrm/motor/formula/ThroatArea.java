package com.jsrm.motor.formula;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class ThroatArea {


    private static Function throatErosion = new Function("throatErosion", 2)  {
        @Override
        public double apply(double... doubles) {
            double burnProgression = doubles[1];
            if(burnProgression < 0 || burnProgression > 1) {
                throw new IllegalArgumentException("burnProgresion should be in this range [0;1]");
            }
            return doubles[0] * burnProgression;
        }
    };


    public static double compute(double throatDiameter, double erosion, double burnProgression){
        return new ExpressionBuilder("pi / 4 * (throatDiameter + throatErosion(erosion, burnProgression))^2")
                .variables("throatDiameter", "erosion", "burnProgression")
                .function(throatErosion)
                .build()
                .setVariable("throatDiameter", throatDiameter)
                .setVariable("erosion", erosion)
                .setVariable("burnProgression", burnProgression)
                .evaluate();
    }
}
