package com.jsrm.infra.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;
import com.jsrm.infra.JSRMConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.infra.pressure.IncrementTimeBurstSolver.NB_LINE_VARIABLE;
import static com.jsrm.infra.pressure.PostBurnPressureFormulas.*;
import static com.jsrm.infra.pressure.PressureFormulas.*;
import static com.jsrm.infra.ConstantsExtractor.toCalculationFormat;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ChamberPressureCalculation {

    private final Map<JSRMConstant, Double> constants;
    private final Map<Formula, Double> initialValues;

    public ChamberPressureCalculation(SolidRocketMotor motor, JSRMConfig config, Map<JSRMConstant, Double> constants) {
        this.constants = constants;
        initialValues = getInitialValues(motor, config);
    }

    public Map<Results, List<Double>> compute() {

        CalculatorResults pressureResults = computeChamberPressureDuringPropellantBurn();

        addNewConstantsFromPressureResults(pressureResults);

        CalculatorResults postBurnPressureResults = computePostBurnPressure();

        return buildResults(pressureResults, postBurnPressureResults);
    }

    private Map<Results, List<Double>> buildResults(CalculatorResults pressureResults, CalculatorResults postBurnPressureResults) {
        int lastPressureResultsLine = NUMBER_LINE_DURING_BURN_CALCULATION - 1;
        List<Double> throatAreaResults = new ArrayList<>(pressureResults.getResults(THROAT_AREA));
        IntStream.range(0, NUMBER_LINE_DURING_POST_BURN_CALCULATION + 1)
                .forEach(value -> throatAreaResults.add(pressureResults.getResult(THROAT_AREA, lastPressureResultsLine)));

        List<Double> nozzlePassageAreaResults = new ArrayList<>(pressureResults.getResults(NOZZLE_CRITICAL_PASSAGE_AREA));
        IntStream.range(0, NUMBER_LINE_DURING_POST_BURN_CALCULATION + 1)
                .forEach(value -> nozzlePassageAreaResults.add(pressureResults.getResult(NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine)));


        List<Double> timeSinceBurnStartResults = mergeResults(pressureResults, postBurnPressureResults, TIME_SINCE_BURN_STARTS, POST_BURN_TIME_SINCE_BURN_STARTS);
        List<Double> chamberPressureMPAResults = mergeResults(pressureResults, postBurnPressureResults, CHAMBER_PRESSURE_MPA, POST_BURN_CHAMBER_PRESSURE_MPA);
        List<Double> absoluteChamberPressureResults = mergeResults(pressureResults, postBurnPressureResults, ABSOLUTE_CHAMBER_PRESSURE, POST_BURN_ABSOLUTE_CHAMBER_PRESSURE);
        List<Double> absoluteChamberPressurePSIGResults = mergeResults(pressureResults, postBurnPressureResults, ABSOLUTE_CHAMBER_PRESSURE_PSIG, POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG);

        //feed last line
        timeSinceBurnStartResults.add(constants.get(tbinc) + timeSinceBurnStartResults.get(timeSinceBurnStartResults.size() - 1));
        chamberPressureMPAResults.add(0d);
        absoluteChamberPressureResults.add(0d);
        absoluteChamberPressurePSIGResults.add(0d);

        return ImmutableMap.<Results, List<Double>>builder()
                .put(throatArea, throatAreaResults)
                .put(nozzleCriticalPassageArea, nozzlePassageAreaResults)
                .put(timeSinceBurnStart, timeSinceBurnStartResults)
                .put(chamberPressureMPA, chamberPressureMPAResults)
                .put(absoluteChamberPressure, absoluteChamberPressureResults)
                .put(absoluteChamberPressurePSIG, absoluteChamberPressurePSIGResults)
                .build();
    }

    private List<Double> mergeResults(CalculatorResults pressureResults,
                                      CalculatorResults postBurnPressureResults,
                                      PressureFormulas pressureFormulas,
                                      PostBurnPressureFormulas postBurnPressureFormulas) {
        return Stream
                .concat(
                        pressureResults.getResults(pressureFormulas).stream(),
                        postBurnPressureResults.getResults(postBurnPressureFormulas).stream())
                .collect(toList());
    }

    private CalculatorResults computePostBurnPressure() {

        initialValues.put(POST_BURN_TIME_SINCE_BURN_STARTS, constants.get(tbout) + constants.get(tbinc));
        return new CalculatorBuilder(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .createCalculator()
                .compute(0, NUMBER_LINE_DURING_POST_BURN_CALCULATION);
    }

    private CalculatorResults computeChamberPressureDuringPropellantBurn() {
        return new CalculatorBuilder(ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(
                        THROAT_AREA, NOZZLE_CRITICAL_PASSAGE_AREA, TIME_SINCE_BURN_STARTS, CHAMBER_PRESSURE_MPA,
                        ABSOLUTE_CHAMBER_PRESSURE, ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .createCalculator()
                .compute(START_CALCULATION_LINE, NUMBER_LINE_DURING_BURN_CALCULATION);
    }

    private void addNewConstantsFromPressureResults(CalculatorResults pressureResults) {
        int lastPressureResultsLine = NUMBER_LINE_DURING_BURN_CALCULATION - 1;

        constants.put(tbout, pressureResults.getResult(TIME_SINCE_BURN_STARTS, lastPressureResultsLine));
        constants.put(pbout, pressureResults.getResult(CHAMBER_PRESSURE_MPA, lastPressureResultsLine));
        constants.put(astarf, pressureResults.getResult(NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine));
        constants.put(expectedPfinal, 2 * constants.get(patm) + PMAXPERC / 100 * getPmax(pressureResults));
        constants.put(tbinc, getTbinc(constants));
    }

    private double getPmax(CalculatorResults pressureResults) {
        return pressureResults.getResults(ABSOLUTE_CHAMBER_PRESSURE).stream().max(Double::compareTo).get();
    }

    private Double getTbinc(Map<JSRMConstant, Double> constants) {
        Map<String, Double> tbincVariables = Stream.of(vc, expectedPfinal, pbout, rat, to, astarf, cstar)
                .collect(toMap(Enum::name, constants::get));

        tbincVariables.put(NB_LINE_VARIABLE, Double.valueOf(NUMBER_LINE_DURING_POST_BURN_CALCULATION));

        return new IncrementTimeBurstSolver().solve(tbincVariables);
    }

    private  Map<Formula, Double> getInitialValues(SolidRocketMotor motor, JSRMConfig config) {
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, motor.getPropellantGrain().getCoreDiameter());
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, motor.getPropellantGrain().getOuterDiameter());
        initialValues.put(GRAIN_LENGTH, motor.getPropellantGrain().getGrainLength());
        initialValues.put(TEMPORARY_CHAMBER_PRESSURE, config.getAmbiantPressureInMPa());
        initialValues.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(MASS_GENERATION_RATE, 0d);
        initialValues.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(MASS_STORAGE_RATE, 0d);
        initialValues.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(DENSITY_COMBUSTION_PRODUCTS, 0d);
        return initialValues;
    }

    public enum Results {
        throatArea,
        nozzleCriticalPassageArea,
        timeSinceBurnStart,
        chamberPressureMPA,
        absoluteChamberPressure,
        absoluteChamberPressurePSIG;
    }
}
