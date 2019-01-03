package com.jsrm.infra;

import com.jsrm.motor.MotorClassification;
import com.jsrm.motor.Nozzle;
import lombok.Value;

import java.util.List;

@Value
public class JSRMResult {
    private double maxThrustInNewton;
    private double totalImpulseInNewtonSecond;
    private double specificImpulseInSecond;
    private double maxChamberPressureInMPa;
    private MotorClassification motorClassification;
    private List<ThrustResult> thrustResults;
    private Nozzle nozzle;
}
