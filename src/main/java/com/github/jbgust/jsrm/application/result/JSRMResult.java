package com.github.jbgust.jsrm.application.result;

import java.util.List;


public class JSRMResult {
    private final double maxThrustInNewton;
    private final double totalImpulseInNewtonSecond;
    private final double specificImpulseInSecond;
    private final double maxChamberPressureInMPa;
    private final double averageChamberPressure;
    private final double thrustTimeInSecond;
    private final MotorClassification motorClassification;
    private final List<ThrustResult> thrustResults;
    private final Nozzle nozzle;
    private final long averageThrustInNewton;

    public JSRMResult(double maxThrustInNewton, double totalImpulseInNewtonSecond, double specificImpulseInSecond, double maxChamberPressureInMPa, double averageChamberPressure, double thrustTimeInSecond, MotorClassification motorClassification, List<ThrustResult> thrustResults, Nozzle nozzle, long averageThrustInNewton) {
        this.maxThrustInNewton = maxThrustInNewton;
        this.totalImpulseInNewtonSecond = totalImpulseInNewtonSecond;
        this.specificImpulseInSecond = specificImpulseInSecond;
        this.maxChamberPressureInMPa = maxChamberPressureInMPa;
        this.averageChamberPressure = averageChamberPressure;
        this.thrustTimeInSecond = thrustTimeInSecond;
        this.motorClassification = motorClassification;
        this.thrustResults = thrustResults;
        this.nozzle = nozzle;
        this.averageThrustInNewton = averageThrustInNewton;
    }

    public double getMaxThrustInNewton() {
        return maxThrustInNewton;
    }

    public double getTotalImpulseInNewtonSecond() {
        return totalImpulseInNewtonSecond;
    }

    public double getSpecificImpulseInSecond() {
        return specificImpulseInSecond;
    }

    public double getMaxChamberPressureInMPa() {
        return maxChamberPressureInMPa;
    }

    public double getAverageChamberPressure() {
        return averageChamberPressure;
    }

    public double getThrustTimeInSecond() {
        return thrustTimeInSecond;
    }

    public MotorClassification getMotorClassification() {
        return motorClassification;
    }

    public List<ThrustResult> getThrustResults() {
        return thrustResults;
    }

    public Nozzle getNozzle() {
        return nozzle;
    }

    public long getAverageThrustInNewton() {
        return averageThrustInNewton;
    }
}
