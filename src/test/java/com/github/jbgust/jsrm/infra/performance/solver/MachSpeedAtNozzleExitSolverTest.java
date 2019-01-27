package com.github.jbgust.jsrm.infra.performance.solver;

import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class MachSpeedAtNozzleExitSolverTest {

    @ParameterizedTest
    @MethodSource("testDatas")
    void shouldSolveMachSpeedAtNozzleExit(NozzleDataTest nozzleDataTest){
        //GIVEN
        MachSpeedAtNozzleExitSolver solver = new MachSpeedAtNozzleExitSolver(KNDX);

        //WHEN
        double initialMachSpeedAtNozzleExit = solver.solve(nozzleDataTest.getNozzleExpansionRatio());

        //THEN
        assertThat(initialMachSpeedAtNozzleExit).isEqualTo(nozzleDataTest.getExpectedMachSpeed(), offset(0.00001));
    }

    @Test
    void shouldUseSecondAlgorithmeIfFirstFailed(){
        //GIVEN
        MachSpeedAtNozzleExitSolver solver = new MachSpeedAtNozzleExitSolver(KNDX);

        //WHEN
        double initialMachSpeedAtNozzleExit = solver.solve(400000000);

        //THEN
        assertThat(initialMachSpeedAtNozzleExit).isEqualTo(9.999, offset(0.001));
    }

    @Value
    private static class NozzleDataTest {
        double nozzleExpansionRatio;
        double expectedMachSpeed;
    }

    static Stream<NozzleDataTest> testDatas() {
        return Stream.of(
                new NozzleDataTest(1, 1.0),
                new NozzleDataTest(4, 2.5181838989257814),
                new NozzleDataTest(8, 2.95455756202289),
                new NozzleDataTest(9.63, 3.06485061645),
                new NozzleDataTest(13, 3.23951816558)
        );
    }
}