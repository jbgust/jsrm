package com.jsrm.application.result;

import com.google.common.collect.Range;
import com.jsrm.application.exception.MotorClassificationOutOfBoundException;

import java.util.stream.Stream;

import static com.google.common.collect.Range.openClosed;

public enum MotorClassification {

    A(1.25, 2.50),
    B(2.50, 5.00),
    C(5.00, 10.0),
    D(10.0, 20.0),
    E(20.0, 40.0),
    F(40.0, 80.0),
    G(80.0, 160.0),
    H(160.0, 320.0),
    I(320.0, 640.0),
    J(640.0, 1280.0),
    K(1280.0, 2560.0),
    L(2560.0, 5120.0),
    M(5120.0, 10240.0),
    N(10240.0, 20560.0),
    O(20560.0, 40960.0),
    P(40960.0, 81920.0),
    Q(81920.0, 163840.0),
    R(163840.0, 327680.0),
    S(327680.0, 655360.0),
    T(655360.0, 1310000.0),
    U(1310000.0, 2620000.0),
    V(2620000.0, 5240000.0);

    private final Range<Double> totalImpulseRangeInNewtowSecond;

    MotorClassification(double minTotalImpulse, double maxTotalImpulse) {
        totalImpulseRangeInNewtowSecond = openClosed(minTotalImpulse, maxTotalImpulse);
    }

    public Range<Double> getTotalImpulseRangeInNewtowSecond() {
        return totalImpulseRangeInNewtowSecond;
    }

    public static MotorClassification getMotorClassification(double totalImpulseInNewtonSecond) {
        return Stream.of(MotorClassification.values())
                .filter(motorClassification -> motorClassification.getTotalImpulseRangeInNewtowSecond().contains(totalImpulseInNewtonSecond))
                .findFirst().orElseThrow(MotorClassificationOutOfBoundException::new);
    }
}
