package com.jsrm.motor;

import lombok.Data;

@Data
public class SolidRocketMotor {
    private final PropellantGrain propellantGrain;
    private final MotorChamber motorChamber;
    private final Double erosiveBurningArea;
    private final Double throatDiameter;
    private final Double nozzleErosion;
}
