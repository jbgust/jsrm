package com.github.jbgust.jsrm.application.result;

public class MotorParameters {
    private final double thrustInNewton;
    private final double kn;
    private final double timeSinceBurnStartInSecond;

    public MotorParameters(double timeSinceBurnStartInSecond, double thrustInNewton, double kn) {
        this.thrustInNewton = thrustInNewton;
        this.timeSinceBurnStartInSecond = timeSinceBurnStartInSecond;
        this.kn = kn;
    }

    public double getThrustInNewton() {
        return thrustInNewton;
    }

    public double getTimeSinceBurnStartInSecond() {
        return timeSinceBurnStartInSecond;
    }

    public double getKn() {
        return kn;
    }
}
