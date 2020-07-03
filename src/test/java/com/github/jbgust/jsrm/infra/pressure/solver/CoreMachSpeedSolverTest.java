package com.github.jbgust.jsrm.infra.performance.solver;

import com.github.jbgust.jsrm.infra.pressure.solver.CoreMachSpeedSolver;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * All data of this test comme from pdf :
 *      Erosive burning desing criteria for high power and ...
 *  see page 24-25
 */
class CoreMachSpeedSolverTest {

    @Test
    void shouldFindCoreMachNumberForGivenAreaPortToThroatRatio() {
        CoreMachSpeedSolver coreMachSpeedSolver = new CoreMachSpeedSolver(1.2);

        assertThat(coreMachSpeedSolver.solve(6D)).isCloseTo(0.1, Offset.offset(0.01));

        assertThat(coreMachSpeedSolver.solve(3D)).isCloseTo(0.2, Offset.offset(0.01));

        assertThat(coreMachSpeedSolver.solve(4D)).isCloseTo(0.15, Offset.offset(0.01));

        assertThat(coreMachSpeedSolver.solve(1D)).isCloseTo(1D, Offset.offset(0.01));
    }

    @Test
    void shouldManageInvalidRatio() {
        CoreMachSpeedSolver coreMachSpeedSolver = new CoreMachSpeedSolver(1.2);

        assertThat(coreMachSpeedSolver.solve(1000D)).isEqualTo(-1D);
        assertThat(coreMachSpeedSolver.solve(0.001D)).isEqualTo(-1D);
    }

}
