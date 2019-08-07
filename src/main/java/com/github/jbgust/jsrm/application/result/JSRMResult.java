package com.github.jbgust.jsrm.application.result;

import java.util.List;


public class JSRMResult {
    private final double maxThrustInNewton;
    private final double totalImpulseInNewtonSecond;
    private final double specificImpulseInSecond;
    private final double maxChamberPressureInMPa;
    private final double averageChamberPressureInMPa;
    private final double thrustTimeInSecond;
    private final MotorClassification motorClassification;
    private final List<MotorParameters> motorParameters;
    private final Nozzle nozzle;
    private final long averageThrustInNewton;
    private Double grainMassInKg;
    private final long numberOfKNCorrection;

    public JSRMResult(double maxThrustInNewton, double totalImpulseInNewtonSecond,
                      double specificImpulseInSecond, double maxChamberPressureInMPa, double averageChamberPressureInMPa,
                      double thrustTimeInSecond, MotorClassification motorClassification,
                      List<MotorParameters> motorParameters, Nozzle nozzle,
                      long averageThrustInNewton, Double grainMassInKg, long numberOfKNCorrection) {
        this.maxThrustInNewton = maxThrustInNewton;
        this.totalImpulseInNewtonSecond = totalImpulseInNewtonSecond;
        this.specificImpulseInSecond = specificImpulseInSecond;
        this.maxChamberPressureInMPa = maxChamberPressureInMPa;
        this.averageChamberPressureInMPa = averageChamberPressureInMPa;
        this.thrustTimeInSecond = thrustTimeInSecond;
        this.motorClassification = motorClassification;
        this.motorParameters = motorParameters;
        this.nozzle = nozzle;
        this.averageThrustInNewton = averageThrustInNewton;
        this.grainMassInKg = grainMassInKg;
        this.numberOfKNCorrection = numberOfKNCorrection;
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

    public double getAverageChamberPressureInMPa() {
        return averageChamberPressureInMPa;
    }

    public double getThrustTimeInSecond() {
        return thrustTimeInSecond;
    }

    public MotorClassification getMotorClassification() {
        return motorClassification;
    }

    public List<MotorParameters> getMotorParameters() {
        return motorParameters;
    }

    public Nozzle getNozzle() {
        return nozzle;
    }

    public long getAverageThrustInNewton() {
        return averageThrustInNewton;
    }

    public long getNumberOfKNCorrection() {
        return numberOfKNCorrection;
    }

    public Double getGrainMassInKg() {
        return grainMassInKg;
    }
}
