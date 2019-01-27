package com.github.jbgust.jsrm.application.result;

import lombok.Value;

import java.util.List;

@Value
public class JSRMResult {
    private double maxThrustInNewton;
    private double totalImpulseInNewtonSecond;
    private double specificImpulseInSecond;
    private double maxChamberPressureInMPa;
    private double thrustTimeInSecond;
    private MotorClassification motorClassification;
    private List<ThrustResult> thrustResults;
    private Nozzle nozzle;
    private long averageThrustInNewton;
}
