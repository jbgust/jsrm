package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;
import lombok.Getter;

@Getter
public class WebRegression {


    private final PropellantGrain propellantGrain;
    private final int numberOfInterval;

    public WebRegression(PropellantGrain propellantGrain, int numberOfInterval) {

        this.propellantGrain = propellantGrain;
        this.numberOfInterval = numberOfInterval;
    }

    //TODO : refaire le calcul car invalide sir outerSurface et coreSurface sont EXPOSED
    public double compute(int interval) {
        return propellantGrain.getInitialWebThickness()/numberOfInterval*interval;
    }
}
