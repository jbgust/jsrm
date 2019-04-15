package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.calculation.Calculator;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import org.assertj.core.api.iterable.ThrowingExtractor;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static com.github.jbgust.jsrm.infra.ConstantsExtractor.toCalculationFormat;
import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.infra.pressure.PressureFormulas.*;
import static org.assertj.core.api.Assertions.assertThat;

public class KnCalculationTest {


    private Map<JSRMConstant, Double> constants;
    private Map<Formula, Double> initialValues;

    @BeforeEach
    void setUp() {
        constants = new HashMap<>();
        constants.put(dto, 17.398);
        constants.put(erate, 0d);
        constants.put(two, 25.50);
        constants.put(xincp, 24.50);

        initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, 20d);
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, 69d);
        initialValues.put(GRAIN_LENGTH, 460d);
    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposed(){
        // GIVEN
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, THROAT_AREA, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, Offset.offset(0.01));

        assertThat(results.getResults(THROAT_AREA).get(0))
                .describedAs("Throat area")
                .isEqualTo(237.73, Offset.offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(656.27, Offset.offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyOuterSurfacesAreInhibited(){
        // GIVEN
        constants.put(ei, 1d);
        constants.put(osi, 0d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, THROAT_AREA, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(0, Offset.offset(0.01));

        assertThat(results.getResults(THROAT_AREA).get(0))
                .describedAs("Throat area")
                .isEqualTo(237.73, Offset.offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(236.84, Offset.offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyEndSurfacesAreInhibited(){
        // GIVEN
        constants.put(ei, 0d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, THROAT_AREA, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(0, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(28902.65, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, Offset.offset(0.01));

        assertThat(results.getResults(THROAT_AREA).get(0))
                .describedAs("Throat area")
                .isEqualTo(237.73, Offset.offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(541.01, Offset.offset(0.01));
    }

    @Test
    void shouldComputeKNWhenOnlyCoreSurfacesAreInhibited(){
        // GIVEN
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 0d);
        constants.put(n, 4d); //4 grains

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, THROAT_AREA, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 1);

        // THEN
        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).get(0))
                .describedAs("End surfaces burning area")
                .isEqualTo(27400.97, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).get(0))
                .describedAs("Core surfaces burning area")
                .isEqualTo(0, Offset.offset(0.01));

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).get(0))
                .describedAs("Outer surfaces burning area")
                .isEqualTo(99714.15, Offset.offset(0.01));

        assertThat(results.getResults(THROAT_AREA).get(0))
                .describedAs("Throat area")
                .isEqualTo(237.73, Offset.offset(0.01));

        assertThat(results.getResults(KN).get(0))
                .describedAs("KN")
                .isEqualTo(534.70, Offset.offset(0.01));
    }

    @Test
    void shouldComputeKNWhenAllSurfacesAreExposedWith2Grains(){
        // GIVEN
        constants.put(ei, 1d);
        constants.put(osi, 1d);
        constants.put(ci, 1d);
        constants.put(n, 2d); //2 grains
        constants.put(xincp, 24.50/50);
        initialValues.put(GRAIN_LENGTH, 230d);

        // WHEN
        Calculator calculator = new CalculatorBuilder(KN)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(GRAIN_LENGTH, GRAIN_END_BURNING_SURFACE, GRAIN_CORE_BURNING_SURFACE, GRAIN_OUTER_BURNING_SURFACE, THROAT_AREA, KN)
                .createCalculator();

        CalculatorResults results = calculator.compute(0, 50);

        // THEN
        assertThat(results.getResults(GRAIN_LENGTH).stream().limit(2))
                .describedAs("Total grain length")
                .extracting(round())
                .containsExactly(230d, 228.04d);

        assertThat(results.getResults(GRAIN_END_BURNING_SURFACE).stream().limit(2))
                .describedAs("End surfaces burning area")
                .extracting(round())
                .containsExactly(13700.49, 13152.47);

        assertThat(results.getResults(GRAIN_CORE_BURNING_SURFACE).stream().limit(2))
                .describedAs("Core surfaces burning area")
                .extracting(round())
                .containsExactly(14451.33, 15030.26);

        assertThat(results.getResults(GRAIN_OUTER_BURNING_SURFACE).stream().limit(2))
                .describedAs("Outer surfaces burning area")
                .extracting(round())
                .containsExactly(49857.08, 48730.13);

        assertThat(results.getResults(KN).stream().limit(2))
                .describedAs("KN")
                .extracting(round())
                .containsExactly(328.14, 323.53);
    }

    private ThrowingExtractor<Double, Double, RuntimeException> round() {
        return aDouble -> new BigDecimal(aDouble).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
