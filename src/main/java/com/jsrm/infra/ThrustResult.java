package com.jsrm.infra;

import lombok.Value;

@Value
public class ThrustResult {
    private double thrustInNewton;
    private double timeSinceBurnStartInSecond;
}
