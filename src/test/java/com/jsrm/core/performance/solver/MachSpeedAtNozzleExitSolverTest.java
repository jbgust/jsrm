package com.jsrm.core.performance.solver;

import org.junit.jupiter.api.Test;

import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class MachSpeedAtNozzleExitSolverTest {

    @Test
    void shouldSolveMachSpeedAtNozzleExit(){
        //GIVEN
        MachSpeedAtNozzleExitSolver solver = new MachSpeedAtNozzleExitSolver();
        double nozzleExpansionRation = 8d;

        //WHEN
        double initialMachSpeedAtNozzleExit = solver.solve(nozzleExpansionRation, KNDX);

        //THEN
        assertThat(initialMachSpeedAtNozzleExit).isEqualTo(2.95455756202289, offset(0.00001));
    }

}