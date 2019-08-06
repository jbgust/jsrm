package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.github.jbgust.jsrm.infra.pressure.function.LowKnFunction.LOW_KN_MASS_STORAGE_RATE;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ChamberPressureCalculation {

    private JSRMConfig config;
    private final Map<JSRMConstant, Double> constants;
    private final Map<Formula, Double> initialValues;

    public ChamberPressureCalculation(SolidRocketMotor motor, JSRMConfig config, Map<JSRMConstant, Double> constants) {
        this.config = config;
        this.constants = constants;
        initialValues = getInitialValues(motor, config);
    }

    public Map<Results, List<Double>> compute() {

        CalculatorResults pressureResults = computeChamberPressureDuringPropellantBurn();
        CalculatorResults knResults = computeKn(pressureResults);

        addNewConstantsFromPressureResults(pressureResults);

        CalculatorResults postBurnPressureResults = computePostBurnPressure();

        return buildResults(pressureResults, knResults, postBurnPressureResults);
    }

    private CalculatorResults computeKn(CalculatorResults pressureResults) {
        return new CalculatorBuilder(PressureFormulas.KN)
                .withResultsToSave(PressureFormulas.KN)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withResultLineProviders(
                        new KnDependenciesResultsProvider(throatArea.name(), pressureResults.getResults(PressureFormulas.THROAT_AREA)),
                        new KnDependenciesResultsProvider("endGrainSurface", pressureResults.getResults(PressureFormulas.END_GRAIN_SRUFACE)),
                        new KnDependenciesResultsProvider("grainCoreDiameter", pressureResults.getResults(PressureFormulas.GRAIN_CORE_DIAMETER)),
                        new KnDependenciesResultsProvider("grainOutsideDiameter", pressureResults.getResults(PressureFormulas.GRAIN_OUTSIDE_DIAMETER)),
                        new KnDependenciesResultsProvider("grainLength", pressureResults.getResults(PressureFormulas.GRAIN_LENGTH)))
                .createCalculator()
                .compute(0, config.getNumberLineDuringBurnCalculation());
    }

    private Map<Results, List<Double>> buildResults(CalculatorResults pressureResults, CalculatorResults knResults, CalculatorResults postBurnPressureResults) {
        int lastPressureResultsLine = config.getNumberLineDuringBurnCalculation() - 1;
        List<Double> throatAreaResults = new ArrayList<>(pressureResults.getResults(PressureFormulas.THROAT_AREA));
        IntStream.range(0, config.getNumberLineDuringPostBurnCalculation() + 1)
                .forEach(value -> throatAreaResults.add(pressureResults.getResult(PressureFormulas.THROAT_AREA, lastPressureResultsLine)));

        // During post burn pressure KN = 0 because burning surface = 0
        List<Double> knResultValues = new ArrayList<>(knResults.getResults(PressureFormulas.KN));
        IntStream.range(0, config.getNumberLineDuringPostBurnCalculation() + 1)
                .forEach(value -> knResultValues.add(0d));

        List<Double> massFlowRateValues = new ArrayList<>(pressureResults.getResults(PressureFormulas.NOZZLE_MASS_FLOW_RATE));

        // linear decrease of MassFlow rate during post burn phase to 0 kg/s
        double lastMassFlowRateComputed = massFlowRateValues.get(massFlowRateValues.size() - 1);
        double massflowIncrement = lastMassFlowRateComputed/(config.getNumberLineDuringPostBurnCalculation()+1);
        IntStream.range(0, config.getNumberLineDuringPostBurnCalculation() + 1)
                .forEach(value -> massFlowRateValues.add(lastMassFlowRateComputed - (massflowIncrement * (value+1))));

        List<Double> nozzlePassageAreaResults = new ArrayList<>(pressureResults.getResults(PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA));
        IntStream.range(0, config.getNumberLineDuringPostBurnCalculation() + 1)
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
                .put(kn, knResultValues)
                .put(massFlowRate, massFlowRateValues)
                .put(lowKNCorrection, singletonList(countLowKNFunctionUsage(pressureResults)))
                .build();
    }

    private double countLowKNFunctionUsage(CalculatorResults pressureResults) {
        if(constants.get(JSRMConstant.safeKN) == 1) {
            return new Long(pressureResults.getResults(PressureFormulas.MASS_STORAGE_RATE).stream()
                    .filter(massStorageRate -> Double.compare(LOW_KN_MASS_STORAGE_RATE, massStorageRate) == 0)
                    .count()).doubleValue();
        }

        return 0d;
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
                .compute(0, config.getNumberLineDuringPostBurnCalculation());
    }

    private CalculatorResults computeChamberPressureDuringPropellantBurn() {
        return new CalculatorBuilder(PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultsToSave(
                        PressureFormulas.THROAT_AREA,
                        PressureFormulas.NOZZLE_CRITICAL_PASSAGE_AREA,
                        PressureFormulas.TIME_SINCE_BURN_STARTS,
                        PressureFormulas.CHAMBER_PRESSURE_MPA,
                        PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE,
                        PressureFormulas.ABSOLUTE_CHAMBER_PRESSURE_PSIG,
                        PressureFormulas.NOZZLE_MASS_FLOW_RATE,
                        PressureFormulas.MASS_STORAGE_RATE,
                        //KN DEPENDENCIES
                        PressureFormulas.GRAIN_LENGTH,
                        PressureFormulas.END_GRAIN_SRUFACE,
                        PressureFormulas.GRAIN_CORE_DIAMETER,
                        PressureFormulas.GRAIN_OUTSIDE_DIAMETER
                )
                .createCalculator()
                .compute(JSRMConstant.START_CALCULATION_LINE, config.getNumberLineDuringBurnCalculation());
    }

    private void addNewConstantsFromPressureResults(CalculatorResults pressureResults) {
        int lastPressureResultsLine = config.getNumberLineDuringBurnCalculation() - 1;

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

        tbincVariables.put(IncrementTimeBurstSolver.NB_LINE_VARIABLE, (double) config.getNumberLineDuringPostBurnCalculation());

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
        absoluteChamberPressurePSIG,
        kn,
        massFlowRate,
        lowKNCorrection;
    }
}
