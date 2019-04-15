package com.github.jbgust.jsrm.application.result;

public class MotorParameters {

    private final double timeSinceBurnStartInSecond;
    private final double thrustInNewton;
    private final double kn;
    private final double chamberPressureInMPa;
    private final double massFlowRateInKgPerSec;

    public MotorParameters(double timeSinceBurnStartInSecond, double thrustInNewton, double kn, double chamberPressureInMPa, double massFlowRateInKgPerSec) {
        this.thrustInNewton = thrustInNewton;
        this.timeSinceBurnStartInSecond = timeSinceBurnStartInSecond;
        this.kn = kn;
        this.chamberPressureInMPa = chamberPressureInMPa;
        this.massFlowRateInKgPerSec = massFlowRateInKgPerSec;
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

    public double getChamberPressureInMPa() {
        return chamberPressureInMPa;
    }

    public double getMassFlowRateInKgPerSec() {
        return massFlowRateInKgPerSec;
    }
}
