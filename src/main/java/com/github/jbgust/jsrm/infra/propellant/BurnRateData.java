package com.github.jbgust.jsrm.infra.propellant;

public record BurnRateData(
    double burnRateCoefficient,
    double pressureExponent
){}
