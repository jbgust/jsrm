package com.jsrm.application.motor;

import com.jsrm.application.motor.propellant.PropellantGrain;
import lombok.Data;

/**
 * A solid rocket motor that use Hollow cylindrical grain only
 */
@Data
public class SolidRocketMotor {
    private final PropellantGrain propellantGrain;
    private final CombustionChamber combustionChamber;
    private final Double throatDiameterInMillimeter;
}
