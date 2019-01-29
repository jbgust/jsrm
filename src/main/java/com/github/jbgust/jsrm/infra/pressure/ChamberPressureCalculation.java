package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
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
        int lastPressureResultsLine = JSRMConstant.NUMBER_LINE_DURING_BURN_CALCULATION - 1;
        List<Double> throatAreaResults = new ArrayList<>(pressureResults.getResults(PressureFormulas.THROAT_AREA));
        IntStream.range(0, JSRMConstant.NUMBER_LINE_DURING_POST_BURN_CALCULATION + 1)
                .forEach(value -> throatAreaResults.add(pressureResults.getResult(PressureFormulas.THROAT_AREA, lastPressureResultsLine)));

        List<Double> nozzlePassageAreaResults = new ArrayList<>(pressureResults.getResults(PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA));
        IntStream.range(0, JSRMConstant.NUMBER_LINE_DURING_POST_BURN_CALCULATION + 1)
                .forEach(value -> nozzlePassageAreaResults.add(pressureResults.getResult(PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine)));


        List<Double> timeSinceBurnStartResults = mergeResults(pressureResults, postBurnPressureResults, PressureFormulas.TIME_SINCE_BURN_STARTS, PostBurnPressureFormulas.POST_BURN_TIME_SINCE_BURN_STARTS);
        List<Double> chamberPressureMPAResults = mergeResults(pressureResults, postBurnPressureResults, PressureFormulas.CHAMBER_PRESSURE_MPA, PostBurnPressureFormulas.POST_BURN_CHAMBER_PRESSURE_MPA);
        List<Double> absoluteChamberPressureResults = mergeResults(pressureResults, postBurnPressureResults, PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE, PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE);
        List<Double> absoluteChamberPressurePSIGResults = mergeResults(pressureResults, postBurnPressureResults, PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE_PSIG, PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG);

        //feed last line
        timeSinceBurnStartResults.add(constants.get(JSRMConstant.tbinc) + timeSinceBurnStartResults.get(timeSinceBurnStartResults.size() - 1));
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

        initialValues.put(PostBurnPressureFormulas.POST_BURN_TIME_SINCE_BURN_STARTS, constants.get(JSRMConstant.tbout) + constants.get(JSRMConstant.tbinc));
        return new CalculatorBuilder(PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .createCalculator()
                .compute(0, JSRMConstant.NUMBER_LINE_DURING_POST_BURN_CALCULATION);
    }

    private CalculatorResults computeChamberPressureDuringPropellantBurn() {
        return new CalculatorBuilder(PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(
                        PressureFormulas.THROAT_AREA, PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA, PressureFormulas.TIME_SINCE_BURN_STARTS, PressureFormulas.CHAMBER_PRESSURE_MPA,
                        PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE, PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .createCalculator()
                .compute(JSRMConstant.START_CALCULATION_LINE, JSRMConstant.NUMBER_LINE_DURING_BURN_CALCULATION);
    }

    private void addNewConstantsFromPressureResults(CalculatorResults pressureResults) {
        int lastPressureResultsLine = JSRMConstant.NUMBER_LINE_DURING_BURN_CALCULATION - 1;

        constants.put(JSRMConstant.tbout, pressureResults.getResult(PressureFormulas.TIME_SINCE_BURN_STARTS, lastPressureResultsLine));
        constants.put(JSRMConstant.pbout, pressureResults.getResult(PressureFormulas.CHAMBER_PRESSURE_MPA, lastPressureResultsLine));
        constants.put(JSRMConstant.astarf, pressureResults.getResult(PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine));
        constants.put(JSRMConstant.expectedPfinal, 2 * constants.get(JSRMConstant.patm) + JSRMConstant.PMAXPERC / 100 * getPmax(pressureResults));
        constants.put(JSRMConstant.tbinc, getTbinc(constants));
    }

    private double getPmax(CalculatorResults pressureResults) {
        return pressureResults.getResults(PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE).stream().max(Double::compareTo).get();
    }

    private Double getTbinc(Map<JSRMConstant, Double> constants) {
        Map<String, Double> tbincVariables = Stream.of(JSRMConstant.vc, JSRMConstant.expectedPfinal, JSRMConstant.pbout, JSRMConstant.rat, JSRMConstant.to, JSRMConstant.astarf, JSRMConstant.cstar)
                .collect(toMap(Enum::name, constants::get));

        tbincVariables.put(IncrementTimeBurstSolver.NB_LINE_VARIABLE, Double.valueOf(JSRMConstant.NUMBER_LINE_DURING_POST_BURN_CALCULATION));

        return new IncrementTimeBurstSolver().solve(tbincVariables);
    }

    private  Map<Formula, Double> getInitialValues(SolidRocketMotor motor, JSRMConfig config) {
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(PressureFormulas.GRAIN_CORE_DIAMETER, motor.getPropellantGrain().getCoreDiameter());
        initialValues.put(PressureFormulas.GRAIN_OUTSIDE_DIAMETER, motor.getPropellantGrain().getOuterDiameter());
        initialValues.put(PressureFormulas.GRAIN_LENGTH, motor.getPropellantGrain().getGrainLength());
        initialValues.put(PressureFormulas.TEMPORARY_CHAMBER_PRESSURE, config.getAmbiantPressureInMPa());
        initialValues.put(PressureFormulas.TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(PressureFormulas.MASS_GENERATION_RATE, 0d);
        initialValues.put(PressureFormulas.NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(PressureFormulas.MASS_STORAGE_RATE, 0d);
        initialValues.put(PressureFormulas.MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(PressureFormulas.DENSITY_COMBUSTION_PRODUCTS, 0d);
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