package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;

public class WebRegression {


    private final PropellantGrain propellantGrain;
    private final int numberOfInterval;

    public WebRegression(PropellantGrain propellantGrain, int numberOfInterval) {

        this.propellantGrain = propellantGrain;
        this.numberOfInterval = numberOfInterval;
    }

    public double compute(int interval) {
        return propellantGrain.getInitialWebThickness()/numberOfInterval*interval;
    }
}
