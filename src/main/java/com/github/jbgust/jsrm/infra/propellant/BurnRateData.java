package com.github.jbgust.jsrm.infra.propellant;

import lombok.Value;

@Value
public
class BurnRateData {
    double burnRateCoefficient;
    double pressureExponent;
}
