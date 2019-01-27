package com.github.jbgust.jsrm.application.result;

import lombok.Value;

@Value
public class ThrustResult {
    private double thrustInNewton;
    private double timeSinceBurnStartInSecond;
}
