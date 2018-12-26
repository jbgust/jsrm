package com.jsrm.core.performance.solver;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.jsrm.core.JSRMConstant.exprat;
import static com.jsrm.core.JSRMConstant.k;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class InitialMachSpeedAtNozzleExitSolverTest {

    @Test
    void shouldSolveInitialMachSpeedAtNozzleExit(){
        //GIVEN
        InitialMachSpeedAtNozzleExitSolver solver = new InitialMachSpeedAtNozzleExitSolver();
        Map<String, Double> variables = ImmutableMap.<String, Double>builder()
                .put(k.name(), KNDX.getK())
                .put(exprat.name(), 8d)
                .build();

        //WHEN
        double initialMachSpeedAtNozzleExit = solver.solve(variables);

        //THEN
        assertThat(initialMachSpeedAtNozzleExit).isEqualTo(2.95455756202289, offset(0.00001));
    }

}