package com.jsrm.motor.pressure;

public class WebThickness {

    public static double compute(double coreDiameter, double outerDiameter){
        return (outerDiameter-coreDiameter) / 2;
    }
}
