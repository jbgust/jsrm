package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.application.motor.grain.HollowCylinderGrain;
import com.github.jbgust.jsrm.calculation.Calculator;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.infra.pressure.resultprovider.BurningSurfaceResultProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.throatArea;
import static com.github.jbgust.jsrm.infra.pressure.PressureFormulas.KN;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class KnCalculationTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposed(){
        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), singletonList(237.7325)),
                        new BurningSurfaceResultProvider(new HollowCylinderGrain(69d, 20d, 115d, 4d, EXPOSED, EXPOSED, EXPOSED), 2))
                .withResultsToSave(KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        //THEN
        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(656.27, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyOuterSurfacesAreInhibited(){
        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), singletonList(237.7325)),
                        new BurningSurfaceResultProvider(new HollowCylinderGrain(69d, 20d, 115d, 4d, INHIBITED, EXPOSED, EXPOSED), 2))
                .withResultsToSave(KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(236.84, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyEndSurfacesAreInhibited(){
        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), singletonList(237.7325)),
                        new BurningSurfaceResultProvider(new HollowCylinderGrain(69d, 20d, 115d, 4d, EXPOSED, INHIBITED, EXPOSED), 2))
                .withResultsToSave( KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(541.01, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyCoreSurfacesAreInhibited(){
        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), singletonList(237.7325)),
                        new BurningSurfaceResultProvider(new HollowCylinderGrain(69d, 20d, 115d, 4d, EXPOSED, EXPOSED, INHIBITED), 2))
                .withResultsToSave(KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(534.70, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposedWith2Grains(){
        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), singletonList(237.7325)),
                        new BurningSurfaceResultProvider(new HollowCylinderGrain(69d, 20d, 115d, 2d, EXPOSED, EXPOSED, EXPOSED), 2))
                .withResultsToSave(KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(328.14, offset(0.01));
    }
}
