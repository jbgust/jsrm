package com.github.jbgust.jsrm.application.result;

public class ThrustResult {
    private final double thrustInNewton;
    private final double timeSinceBurnStartInSecond;

    public ThrustResult(double thrustInNewton, double timeSinceBurnStartInSecond) {
        this.thrustInNewton = thrustInNewton;
        this.timeSinceBurnStartInSecond = timeSinceBurnStartInSecond;
    }

    public double getThrustInNewton() {
        return thrustInNewton;
    }

    public double getTimeSinceBurnStartInSecond() {
        return timeSinceBurnStartInSecond;
    }
}
