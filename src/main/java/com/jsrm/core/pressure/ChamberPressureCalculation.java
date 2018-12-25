package com.jsrm.core.pressure;

import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;

import java.util.List;
import java.util.Map;

import static com.jsrm.core.pressure.PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG;
import static com.jsrm.core.pressure.PressureFormulas.*;
import static java.util.Collections.emptyMap;

public class ChamberPressureCalculation {

    public static final String throatArea = "THROAT_AREA";
    public static final String nozzleCriticalPassageArea = "NOZZLE_CRITICAL_PASSAGE_AREA";
    public static final String timeSinceBurnStart = "TIME_SINCE_BURN_STARTS";
    public static final String chamberPressureMPA = "CHAMBER_PRESSURE_MPA";
    public static final String absoluteChamberPressure = "ABSOLUTE_CHAMBER_PRESSURE";
    public static final String absoluteChamberPressurePSIG = "ABSOLUTE_CHAMBER_PRESSURE_PSIG";

    //836 line are computed during propellant burn (first line = 0)
    private static final int NB_LINE_IN_PRESSURE_SPREADSHEET = 835;
    public static final int START_LINE = 0;

    public Map<String, List<Double>> compute() {

        Map<String, Double> constants = emptyMap();
        Map<Formula, Double> initialValues = emptyMap();

        CalculatorResults pressureResults = new CalculatorBuilder(ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(constants)
                .withInitialValues(initialValues)
                .withResultsToSave(
                        THROAT_AREA, NOZZLE_CRITICAL_PASSAGE_AREA, TIME_SINCE_BURN_STARTS, CHAMBER_PRESSURE_MPA,
                        ABSOLUTE_CHAMBER_PRESSURE, ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .createCalculator()
                .compute(START_LINE, NB_LINE_IN_PRESSURE_SPREADSHEET);


        CalculatorResults postBurnPressureResults = new CalculatorBuilder(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withConstants(constants)
                .withInitialValues(initialValues)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .createCalculator()
                //TODO : save to constants and check values
                .compute(0, 47);

        //TODO : Merging both results


        return emptyMap();
    }
}
