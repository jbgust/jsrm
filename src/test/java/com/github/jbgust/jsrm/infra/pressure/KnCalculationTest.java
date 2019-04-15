package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.calculation.Calculator;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.jbgust.jsrm.infra.ConstantsExtractor.toCalculationFormat;
import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.throatArea;
import static com.github.jbgust.jsrm.infra.pressure.PressureFormulas.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

public class KnCalculationTest {


    private Map<JSRMConstant, Double> constants;
    private Map<Formula, Double> initialValues;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposed(){
        // GIVEN
        Map constants = new HashMap<>();
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), asList(237.7325)),
                        new KnDependenciesResultsProvider("endGrainSurface", asList(3425.1214)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", asList(20d)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", asList(69d)),
                        new KnDependenciesResultsProvider("grainLength", asList(460d)))
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(656.27, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyOuterSurfacesAreInhibited(){
        // GIVEN
        Map constants = new HashMap<>();
        constants.put(ei, 1d);
        constants.put(osi, 0d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), asList(237.7325)),
                        new KnDependenciesResultsProvider("endGrainSurface", asList(3425.1214)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", asList(20d)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", asList(69d)),
                        new KnDependenciesResultsProvider("grainLength", asList(460d)))
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(0, offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(236.84, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyEndSurfacesAreInhibited(){
        // GIVEN
        Map constants = new HashMap<>();
        constants.put(ei, 0d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), asList(237.7325)),
                        new KnDependenciesResultsProvider("endGrainSurface", asList(3425.1214)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", asList(20d)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", asList(69d)),
                        new KnDependenciesResultsProvider("grainLength", asList(460d)))
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(0, offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(541.01, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyCoreSurfacesAreInhibited(){
        // GIVEN
        Map constants = new HashMap<>();
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 0d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), asList(237.7325)),
                        new KnDependenciesResultsProvider("endGrainSurface", asList(3425.1214)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", asList(20d)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", asList(69d)),
                        new KnDependenciesResultsProvider("grainLength", asList(460d)))
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(0, offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(534.70, offset(0.01));
    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposedWith2Grains(){
        // GIVEN
        Map constants = new HashMap<>();
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 2d); //2 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), asList(237.7325)),
                        new KnDependenciesResultsProvider("endGrainSurface", asList(3425.1214)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", asList(20d)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", asList(69d)),
                        new KnDependenciesResultsProvider("grainLength", asList(230d)))
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(13700.49, offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(14451.33, offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(49857.08, offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(328.14, offset(0.01));
    }
}
