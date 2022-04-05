package com.github.jbgust.jsrm.application.result;

public class MotorParameters {

    private final double timeSinceBurnStartInSecond;
    private final double thrustInNewton;
    private final double kn;
    private final double chamberPressureInMPa;
    private final double massFlowRateInKgPerSec;
    private final double grainMassInKg;

    public MotorParameters(double timeSinceBurnStartInSecond, double thrustInNewton, double kn, double chamberPressureInMPa, double massFlowRateInKgPerSec, double grainMassInKg) {
        this.thrustInNewton = thrustInNewton;
        this.timeSinceBurnStartInSecond = timeSinceBurnStartInSecond;
        this.kn = kn;
        this.chamberPressureInMPa = chamberPressureInMPa;
        this.massFlowRateInKgPerSec = massFlowRateInKgPerSec;
        this.grainMassInKg = grainMassInKg;
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

    public double getGrainMassInKg() {
        return grainMassInKg;
    }
}
