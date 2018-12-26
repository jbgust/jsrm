package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.jsrm.core.JSRMConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class IncrementTimeBurstSolverTest {

    public static final double tbinc_FROM_SRM_2014_XLS = 0.00153227607773698;

    @Test
    void shouldSolveTbinc() throws Exception {
        //GIVEN
        IncrementTimeBurstSolver solver = new IncrementTimeBurstSolver();
        Map<String, Double> variables = ImmutableMap.<String, Double>builder()
                .put(vc.name(), 2076396.394482)
                .put(expectedPfinal.name(), 0.203044747800798)
                .put(pbout.name(), 3.89641961658439)
                .put(rat.name(), 196.131163010144)
                .put(to.name(), 1624.5)
                .put(astarf.name(), 0.000237746832219086)
                .put(cstar.name(), 889.279521360202)
                .put("nbLine", 47d)
                .build();

        //WHEN
        double tbinc = solver.solve(variables);

        //THEN
        assertThat(tbinc).isEqualTo(tbinc_FROM_SRM_2014_XLS, offset(0.000000000000001));
    }
}