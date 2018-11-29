package com.jsrm.motor.formula;

public class WebThickness {

    public static double compute(double coreDiameter, double outerDiameter){
        return (outerDiameter-coreDiameter) / 2;
    }
}
