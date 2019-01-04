package com.jsrm.application.motor;

import com.jsrm.application.motor.propellant.PropellantGrain;
import lombok.Data;

@Data
public class SolidRocketMotor {
    private final PropellantGrain propellantGrain;
    private final MotorChamber motorChamber;
    private final Double erosiveBurningArea;
    private final Double throatDiameter;
    private final Double nozzleErosion;

    //TODO : erosiveBurningArea & nozzleErosion a virer car dans JSRMConfig
}
