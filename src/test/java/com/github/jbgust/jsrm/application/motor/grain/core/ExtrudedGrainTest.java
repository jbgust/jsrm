package com.github.jbgust.jsrm.application.motor.grain.core;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static org.assertj.core.api.Assertions.assertThat;

class ExtrudedGrainTest {

    @Test
    void shouldSetSurfaceASInhibited() {
        ExtrudedGrain grain = new TestGrain();
        grain.setForeEndInhibited(INHIBITED);
        grain.setAftEndInhibited(INHIBITED);

        assertThat(grain.isForeEndInhibited()).isTrue();
        assertThat(grain.isAftEndInhibited()).isTrue();
    }

    @Test
    void shouldSetSurfaceASExposed() {
        ExtrudedGrain grain = new TestGrain();
        grain.setForeEndInhibited(EXPOSED);
        grain.setAftEndInhibited(EXPOSED);

        assertThat(grain.isForeEndInhibited()).isFalse();
        assertThat(grain.isAftEndInhibited()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("testValues")
    void shouldTestNumberOfExposedSurface(GrainSurfaceValuesTest value) {
        ExtrudedGrain grain = new TestGrain();
        grain.setForeEndInhibited(value.getForeEndInhibited());
        grain.setAftEndInhibited(value.getAftEndInhibited());

        assertThat(grain.numberOfBurningEnds(0)).isEqualTo(value.getExpectedNumberOfSurfaceExposed());
    }

    private class TestGrain extends ExtrudedGrain {

    }

    private static class GrainSurfaceValuesTest{
        private final GrainSurface foreEndInhibited;
        private final GrainSurface aftEndInhibited;
        private final int expectedNumberOfSurfaceExposed;

        private GrainSurfaceValuesTest(GrainSurface grainSurface, GrainSurface aftEndInhibited, int expectedNumberOfSurfaceExposed) {
            foreEndInhibited = grainSurface;
            this.aftEndInhibited = aftEndInhibited;
            this.expectedNumberOfSurfaceExposed = expectedNumberOfSurfaceExposed;
        }

        public GrainSurface getForeEndInhibited() {
            return foreEndInhibited;
        }

        public GrainSurface getAftEndInhibited() {
            return aftEndInhibited;
        }

        public int getExpectedNumberOfSurfaceExposed() {
            return expectedNumberOfSurfaceExposed;
        }
    }

    static Stream<GrainSurfaceValuesTest> testValues() {
        return Stream.of(
                new GrainSurfaceValuesTest(EXPOSED, EXPOSED, 2),
                new GrainSurfaceValuesTest(EXPOSED, INHIBITED, 1),
                new GrainSurfaceValuesTest(INHIBITED, EXPOSED, 1),
                new GrainSurfaceValuesTest(INHIBITED, INHIBITED, 0)
        );
    }

}
