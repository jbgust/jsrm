package com.jsrm.motor.propellant;

import lombok.Value;

@Value
class BurnRateData {
    double burnRateCoefficient;
    double pressureExponent;
}
