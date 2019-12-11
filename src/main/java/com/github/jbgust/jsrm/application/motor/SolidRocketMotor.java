package com.github.jbgust.jsrm.application.motor;

/**
 * A solid rocket motor that use Hollow cylindrical grain only
 */

public class SolidRocketMotor {
    private PropellantGrain propellantGrain;
    private CombustionChamber combustionChamber;
    private Double throatDiameterInMillimeter;

    public SolidRocketMotor(PropellantGrain propellantGrain, CombustionChamber combustionChamber, Double throatDiameterInMillimeter) {
        this.propellantGrain = propellantGrain;
        this.combustionChamber = combustionChamber;
        this.throatDiameterInMillimeter = throatDiameterInMillimeter;
    }

    public PropellantGrain getPropellantGrain() {
        return propellantGrain;
    }

    public CombustionChamber getCombustionChamber() {
        return combustionChamber;
    }

    public Double getThroatDiameterInMillimeter() {
        return throatDiameterInMillimeter;
    }
}
