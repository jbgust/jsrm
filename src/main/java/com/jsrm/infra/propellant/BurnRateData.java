package com.jsrm.infra.propellant;

import lombok.Value;

@Value
class BurnRateData {
    double burnRateCoefficient;
    double pressureExponent;
}
