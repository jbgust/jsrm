package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.FormulaConfiguration;
import com.github.jbgust.jsrm.infra.function.CircleAreaFunction;
import com.github.jbgust.jsrm.infra.function.HollowCircleAreaFunction;
import com.github.jbgust.jsrm.infra.pressure.function.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.infra.pressure.resultprovider.BurningSurfaceResultProvider.BURNING_SURFACE_VARIABLE;
import static com.github.jbgust.jsrm.infra.pressure.resultprovider.EndGrainSurfaceResultProvider.END_GRAIN_SURFACE_VARIABLE;
import static com.github.jbgust.jsrm.infra.pressure.resultprovider.GrainVolumeResultProvider.GRAIN_VOLUME_VARIABLE;
import static com.github.jbgust.jsrm.infra.pressure.resultprovider.ProgressionResultProvider.PROGRESSION_VARIABLE;
import static java.util.stream.Stream.of;


public enum PressureFormulas implements Formula {

    WEB_THICKNESS(new FormulaConfiguration("(GRAIN_OUTSIDE_DIAMETER - GRAIN_CORE_DIAMETER) / 2")
            .withDependencies("GRAIN_CORE_DIAMETER", "GRAIN_OUTSIDE_DIAMETER")),

    THROAT_AREA(new FormulaConfiguration("CircleArea(dto+erate*" + PROGRESSION_VARIABLE + ")")
            .withConstants(dto, erate)
            .withVariables(PROGRESSION_VARIABLE)
            .withFunctions(Functions.circleArea)),

    NOZZLE_CRITICAL_PASSAGE_AREA(new FormulaConfiguration("THROAT_AREA / 1000^2")
            .withDependencies("THROAT_AREA")),

    //Difference in chamber and grain cross-sectional area (flow area)
    EROSIVE_BURN_FACTOR(new FormulaConfiguration("ErosiveBurnFactor((CircleArea(dc)-" + END_GRAIN_SURFACE_VARIABLE + ")/THROAT_AREA, gstar)")
            .withDependencies("THROAT_AREA")
            .withVariables(END_GRAIN_SURFACE_VARIABLE)
            .withConstants(dc, gstar)
            .withFunctions(Functions.erosiveBurnFactor, Functions.circleArea)),

    TEMPORARY_CHAMBER_PRESSURE(new FormulaConfiguration("CHAMBER_PRESSURE_MPA_previous")
            .withVariables("CHAMBER_PRESSURE_MPA_previous")),

    PROPELLANT_BURN_RATE(new FormulaConfiguration("(1 + kv * EROSIVE_BURN_FACTOR) * BurnRateCharacteristic(propellantId, TEMPORARY_CHAMBER_PRESSURE)")
            .withConstants(kv, propellantId)
            .withFunctions(Functions.burnRateCharacteristic)
            .withDependencies("EROSIVE_BURN_FACTOR", "TEMPORARY_CHAMBER_PRESSURE")),

    TIME_SINCE_BURN_STARTS(new FormulaConfiguration("xincp / PROPELLANT_BURN_RATE + TIME_SINCE_BURN_STARTS_previous")
            .withConstants(xincp)
            .withDependencies("PROPELLANT_BURN_RATE")
            .withVariables("TIME_SINCE_BURN_STARTS_previous")),

    //Grain mass
    GRAIN_MASS(new FormulaConfiguration("GrainMass(rhopgrain, "+GRAIN_VOLUME_VARIABLE+")")
            .withVariables(GRAIN_VOLUME_VARIABLE)
            .withConstants(rhopgrain)
            .withFunctions(Functions.grainMass)),

    //Mass generation rate of combustion products
    MASS_GENERATION_RATE(new FormulaConfiguration("(GrainMass(rhopgrain, "+GRAIN_VOLUME_VARIABLE+"_previous)-GRAIN_MASS) / (TIME_SINCE_BURN_STARTS-TIME_SINCE_BURN_STARTS_previous)")
            .withDependencies("TIME_SINCE_BURN_STARTS", "GRAIN_MASS")
            .withVariables(GRAIN_VOLUME_VARIABLE+"_previous", "TIME_SINCE_BURN_STARTS_previous", GRAIN_VOLUME_VARIABLE)
            .withConstants(rhopgrain)
            .withFunctions(Functions.grainMass)),

    //Mass flow rate through nozzle
    NOZZLE_MASS_FLOW_RATE(new FormulaConfiguration("NozzleMassFlowRate(pbd, MASS_GENERATION_RATE, CHAMBER_PRESSURE_MPA_previous, AI)")
            .withConstants(pbd)
            .withDependencies("MASS_GENERATION_RATE", "AI")
            .withVariables("CHAMBER_PRESSURE_MPA_previous")
            .withFunctions(Functions.nozzleMassFlowRate)),

    //Mass storage rate of combustion products (in chamber)
    MASS_STORAGE_RATE(new FormulaConfiguration("lowKn(MASS_GENERATION_RATE - NOZZLE_MASS_FLOW_RATE, safeKN)")
            .withDependencies("MASS_GENERATION_RATE", "NOZZLE_MASS_FLOW_RATE")
            .withConstants(safeKN)
            .withFunctions(Functions.lowKNFunction)),

    //Mass of combustion products stored in chamber
    MASS_COMBUSTION_PRODUCTS(new FormulaConfiguration("MASS_STORAGE_RATE * (TIME_SINCE_BURN_STARTS-TIME_SINCE_BURN_STARTS_previous) + MASS_COMBUSTION_PRODUCTS_previous")
            .withVariables("TIME_SINCE_BURN_STARTS_previous", "MASS_COMBUSTION_PRODUCTS_previous")
            .withDependencies("MASS_STORAGE_RATE", "TIME_SINCE_BURN_STARTS")),

    //Density of combustion products in chamber
    DENSITY_COMBUSTION_PRODUCTS(new FormulaConfiguration("MASS_COMBUSTION_PRODUCTS / FreeVolumeInChamber(vc, "+GRAIN_VOLUME_VARIABLE+")")
            .withDependencies("MASS_COMBUSTION_PRODUCTS")
            .withVariables(GRAIN_VOLUME_VARIABLE)
            .withConstants(vc)
            .withFunctions(Functions.freeVolumeInChamber)),

    //(Mpa) mega Pascal
    CHAMBER_PRESSURE_MPA(new FormulaConfiguration("(DENSITY_COMBUSTION_PRODUCTS * rat * to + patm * 1000000) / 1000000")
            .withDependencies("DENSITY_COMBUSTION_PRODUCTS")
            .withConstants(rat, to, patm)),

    ABSOLUTE_CHAMBER_PRESSURE(new FormulaConfiguration("CHAMBER_PRESSURE_MPA - patm")
            .withDependencies("CHAMBER_PRESSURE_MPA")
            .withConstants(patm)),

    ABSOLUTE_CHAMBER_PRESSURE_PSIG(new FormulaConfiguration("ABSOLUTE_CHAMBER_PRESSURE * 1000000 / 6895")
            .withDependencies("ABSOLUTE_CHAMBER_PRESSURE")),

    //Strange column AI in Excel file, no more information about it
    AI(new FormulaConfiguration("(TEMPORARY_CHAMBER_PRESSURE - patm) * 1000000 * NOZZLE_CRITICAL_PASSAGE_AREA / sqrt(rat*to) * sqrt(k) * (2/(k+1))^((k+1)/2/(k-1))")
            .withDependencies("TEMPORARY_CHAMBER_PRESSURE", "NOZZLE_CRITICAL_PASSAGE_AREA")
            .withConstants(patm, rat, to, k)),

    KN(new FormulaConfiguration(BURNING_SURFACE_VARIABLE + " / throatArea")
            .withVariables("throatArea", BURNING_SURFACE_VARIABLE));

    private final Expression expression;
    private final Set<String> dependencies;
    private final String expressionAsString;

    PressureFormulas(FormulaConfiguration config) {

        expressionAsString = config.getFormula();
        ExpressionBuilder expressionBuilder = new ExpressionBuilder(expressionAsString);

        this.dependencies = of(config.getDependencies())
                .peek(expressionBuilder::variable)
                .collect(Collectors.toSet());

        expressionBuilder.functions(config.getFunctions());

        Stream.of(config.getConstants()).map(Enum::toString).forEach(expressionBuilder::variable);

        of(config.getVariables()).forEach(expressionBuilder::variable);

        expression = expressionBuilder.build();
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String getExpressionAsString() {
        return expressionAsString;
    }

    @Override
    public Set<Formula> getDependencies() {
        return dependencies.stream().map(PressureFormulas::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }

    private static class Functions {
        private static final CircleAreaFunction circleArea = new CircleAreaFunction();
        private static final ErosiveBurnFactorFunction erosiveBurnFactor = new ErosiveBurnFactorFunction();
        private static final HollowCircleAreaFunction hollowCircleArea = new HollowCircleAreaFunction();
        private static final BurnRateCharacteristicFunction burnRateCharacteristic = new BurnRateCharacteristicFunction();
        private static final GrainMassFunction grainMass = new GrainMassFunction();
        private static final NozzleMassFlowRateFunction nozzleMassFlowRate = new NozzleMassFlowRateFunction();
        private static final FreeVolumeInChamberFunction freeVolumeInChamber = new FreeVolumeInChamberFunction();
        private static final LowKnFunction lowKNFunction = new LowKnFunction();
    }
}
