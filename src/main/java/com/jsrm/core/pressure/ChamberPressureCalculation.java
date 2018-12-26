package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jsrm.core.JSRMConstant.*;
import static com.jsrm.core.pressure.IncrementTimeBurstSolver.NB_LINE_VARIABLE;
import static com.jsrm.core.pressure.PostBurnPressureFormulas.*;
import static com.jsrm.core.pressure.PressureFormulas.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ChamberPressureCalculation {

    public static final String throatArea = "THROAT_AREA";
    public static final String nozzleCriticalPassageArea = "NOZZLE_CRITICAL_PASSAGE_AREA";
    public static final String timeSinceBurnStart = "TIME_SINCE_BURN_STARTS";
    public static final String chamberPressureMPA = "CHAMBER_PRESSURE_MPA";
    public static final String absoluteChamberPressure = "ABSOLUTE_CHAMBER_PRESSURE";
    public static final String absoluteChamberPressurePSIG = "ABSOLUTE_CHAMBER_PRESSURE_PSIG";

    private static final int NB_LINE_IN_PRESSURE_SPREADSHEET = 835;
    private static final Double NB_LINE_POST_BURN_CALCULATION = 47d;

    private static final int START_LINE = 0;
    private static final double PMAXPERC = 0.02;

    private final Map<String, Double> constants;
    private final Map<Formula, Double> initialValues;

    public ChamberPressureCalculation(Map<String, Double> constants, Map<Formula, Double> initialValues) {
        this.constants = constants;
        this.initialValues = initialValues;
    }

    public Map<String, List<Double>> compute() {

        CalculatorResults pressureResults = computeChamberPressureDuringPropellantBurn();

        addNewConstantsFromPressureResults(pressureResults);

        CalculatorResults postBurnPressureResults = computePostBurnPressure();

        return buildResults(pressureResults, postBurnPressureResults);
    }

    private Map<String, List<Double>> buildResults(CalculatorResults pressureResults, CalculatorResults postBurnPressureResults) {
        int lastPressureResultsLine = NB_LINE_IN_PRESSURE_SPREADSHEET - 1;
        List<Double> throatAreaResults = new ArrayList<>(pressureResults.getResults(THROAT_AREA));
        IntStream.range(0, NB_LINE_POST_BURN_CALCULATION.intValue()+1)
                .forEach(value -> throatAreaResults.add(pressureResults.getResult(THROAT_AREA, lastPressureResultsLine)));

        List<Double> nozzlePassageAreaResults = new ArrayList<>(pressureResults.getResults(NOZZLE_CRITICAL_PASSAGE_AREA));
        IntStream.range(0, NB_LINE_POST_BURN_CALCULATION.intValue()+1)
                .forEach(value -> nozzlePassageAreaResults.add(pressureResults.getResult(NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine)));


        List<Double> timeSinceBurnStartResults = mergeResults(pressureResults, postBurnPressureResults, TIME_SINCE_BURN_STARTS, POST_BURN_TIME_SINCE_BURN_STARTS);
        List<Double> chamberPressureMPAResults = mergeResults(pressureResults, postBurnPressureResults, CHAMBER_PRESSURE_MPA, POST_BURN_CHAMBER_PRESSURE_MPA);
        List<Double> absoluteChamberPressureResults = mergeResults(pressureResults, postBurnPressureResults, ABSOLUTE_CHAMBER_PRESSURE, POST_BURN_ABSOLUTE_CHAMBER_PRESSURE);
        List<Double> absoluteChamberPressurePSIGResults = mergeResults(pressureResults, postBurnPressureResults, ABSOLUTE_CHAMBER_PRESSURE_PSIG, POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG);

        //feed last line
        timeSinceBurnStartResults.add(constants.get(tbinc.name()) + timeSinceBurnStartResults.get(timeSinceBurnStartResults.size()-1));
        chamberPressureMPAResults.add(0d);
        absoluteChamberPressureResults.add(0d);
        absoluteChamberPressurePSIGResults.add(0d);

        return ImmutableMap.<String, List<Double>>builder()
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

        initialValues.put(POST_BURN_TIME_SINCE_BURN_STARTS, constants.get(tbout.name())+constants.get(tbinc.name()));
        return new CalculatorBuilder(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(constants)
                .withInitialValues(initialValues)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .createCalculator()
                .compute(0, NB_LINE_POST_BURN_CALCULATION.intValue());
    }

    private CalculatorResults computeChamberPressureDuringPropellantBurn() {
        return new CalculatorBuilder(ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(constants)
                .withInitialValues(initialValues)
                .withResultsToSave(
                        THROAT_AREA, NOZZLE_CRITICAL_PASSAGE_AREA, TIME_SINCE_BURN_STARTS, CHAMBER_PRESSURE_MPA,
                        ABSOLUTE_CHAMBER_PRESSURE, ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .createCalculator()
                .compute(START_LINE, NB_LINE_IN_PRESSURE_SPREADSHEET);
    }

    private void addNewConstantsFromPressureResults(CalculatorResults pressureResults) {
        int lastPressureResultsLine = NB_LINE_IN_PRESSURE_SPREADSHEET - 1;

        constants.put(tbout.name(), pressureResults.getResult(TIME_SINCE_BURN_STARTS, lastPressureResultsLine));
        constants.put(pbout.name(), pressureResults.getResult(CHAMBER_PRESSURE_MPA, lastPressureResultsLine));
        constants.put(astarf.name(), pressureResults.getResult(NOZZLE_CRITICAL_PASSAGE_AREA, lastPressureResultsLine));

        constants.put(expectedPfinal.name(), 2 * constants.get(patm.name()) + PMAXPERC / 100 * getPmax(pressureResults));
        constants.put(tbinc.name(), getTbinc(constants));
    }

    private double getPmax(CalculatorResults pressureResults) {
        return pressureResults.getResults(ABSOLUTE_CHAMBER_PRESSURE).stream().max(Double::compareTo).get();
    }

    private Double getTbinc(Map<String, Double> constants) {
        Map<String, Double> tbincVariables = Stream.of(vc, expectedPfinal, pbout, rat, to, astarf, cstar)
                .collect(toMap(Enum::name, constant -> constants.get(constant.name())));

        tbincVariables.put(NB_LINE_VARIABLE, NB_LINE_POST_BURN_CALCULATION);

        return new IncrementTimeBurstSolver().solve(tbincVariables);
    }
}
