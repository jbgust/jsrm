package com.jsrm.core.pressure;

import com.jsrm.calculation.Formula;
import com.jsrm.calculation.function.CircleAreaFunction;
import com.jsrm.calculation.function.HollowCircleAreaFunction;
import com.jsrm.core.JSRMConstant;
import com.jsrm.core.pressure.function.BurnRateCharacteristicFunction;
import com.jsrm.core.pressure.function.ErosiveBurnFactorFunction;
import com.jsrm.core.pressure.function.GrainMassFunction;
import com.jsrm.core.pressure.function.NozzleMassFlowRateFunction;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Set;
import java.util.stream.Collectors;

import static com.jsrm.core.JSRMConstant.*;
import static java.util.stream.Stream.of;


public enum PressureFormulas implements Formula {

    GRAIN_CORE_DIAMETER(new Config("GRAIN_CORE_DIAMETER_previous + ci * 2 * xincp")
            .withConstants(ci, xincp)
            .withVariables("GRAIN_CORE_DIAMETER_previous")),

    GRAIN_OUTSIDE_DIAMETER(new Config("GRAIN_OUTSIDE_DIAMETER_previous - osi * 2 * xincp")
            .withConstants(osi, xincp)
            .withVariables("GRAIN_OUTSIDE_DIAMETER_previous")),

    GRAIN_LENGTH(new Config("GRAIN_LENGTH_previous-ei*n*2*xincp")
            .withConstants(ei, n, xincp)
            .withVariables("GRAIN_LENGTH_previous")),

    WEB_THICKNESS(new Config("(GRAIN_OUTSIDE_DIAMETER - GRAIN_CORE_DIAMETER) / 2")
            .withDependencies("GRAIN_CORE_DIAMETER", "GRAIN_OUTSIDE_DIAMETER")),

    THROAT_AREA(new Config("CircleArea(dto+erate*(two-WEB_THICKNESS)/two)")
            .withDependencies("WEB_THICKNESS")
            .withConstants(dto, erate, two)
            .withFunctions(Functions.circleArea)),

    NOZZLE_CRITICAL_PASSAGE_AREA(new Config("THROAT_AREA / 1000^2")
            .withDependencies("THROAT_AREA")),

    //Difference in chamber and grain cross-sectional area (flow area)
    EROSIVE_BURN_FACTOR(new Config("ErosiveBurnFactor((CircleArea(dc)-HollowCircleArea(GRAIN_OUTSIDE_DIAMETER, GRAIN_CORE_DIAMETER))/THROAT_AREA, gstar)")
            .withDependencies("GRAIN_OUTSIDE_DIAMETER", "GRAIN_CORE_DIAMETER", "THROAT_AREA")
            .withConstants(dc, gstar)
            .withFunctions(Functions.erosiveBurnFactor, Functions.hollowCircleArea, Functions.circleArea)),

    GRAIN_VOLUME(new Config("(HollowCircleArea(GRAIN_OUTSIDE_DIAMETER, GRAIN_CORE_DIAMETER) * GRAIN_LENGTH)")
            .withDependencies("GRAIN_OUTSIDE_DIAMETER", "GRAIN_CORE_DIAMETER", "GRAIN_LENGTH")
            .withFunctions(Functions.hollowCircleArea)),

    TEMPORARY_CHAMBER_PRESSURE(new Config("CHAMBER_PRESSURE_previous")
            //TODO : a virer les valeur _previous n'ont surement pas besoin d'être déclaré en dépendances => A TESTER
            .withDependencies("CHAMBER_PRESSURE")),

    PROPELLANT_BURN_RATE(new Config("(1 + kv * EROSIVE_BURN_FACTOR) * BurnRateCharacteristic(propellantId, TEMPORARY_CHAMBER_PRESSURE)")
            .withConstants(kv, propellantId)
            .withFunctions(Functions.burnRateCharacteristic)
            .withDependencies("EROSIVE_BURN_FACTOR", "TEMPORARY_CHAMBER_PRESSURE")),

    TIME_SINCE_BURN_STARTS(new Config("xincp / PROPELLANT_BURN_RATE + TIME_SINCE_BURN_STARTS_previous")
            .withConstants(xincp)
            .withDependencies("PROPELLANT_BURN_RATE")),

    //Mass generation rate of combustion products
    MASS_GENERATION_RATE(new Config("(GrainMass(GRAIN_VOLUME_previous)-GrainMass(GRAIN_VOLUME)) / (TIME_SINCE_BURN_STARTS-TIME_SINCE_BURN_STARTS_previous)")
            .withDependencies("GRAIN_VOLUME", "TIME_SINCE_BURN_STARTS")
            .withFunctions(Functions.grainMass)),

    //Mass flow rate through nozzle
    NOZZLE_MASS_FLOW_RATE(new Config("NozzleMassFlowRate(pbd, MASS_GENERATION_RATE, CHAMBER_PRESSURE_previous, AI)")
            .withConstants(pbd)
            .withDependencies("MASS_GENERATION_RATE", "AI")
            .withFunctions(Functions.nozzleMassFlowRate)),

    //TODO
    CHAMBER_PRESSURE(new Config("TODO")),
    //Strange column AI in Excel file, no more information about it
    AI(new Config("TODO"))
    ;

    private final Expression expression;
    private final Set<String> dependencies;

    PressureFormulas(Config config) {

        ExpressionBuilder expressionBuilder = new ExpressionBuilder(config.getFormula());

        this.dependencies = of(config.getDependencies())
                .peek(expressionBuilder::variable)
                .collect(Collectors.toSet());

        expressionBuilder.functions(config.getFunctions());

        of(config.getConstants()).map(Enum::toString).forEach(expressionBuilder::variable);

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
    }

    private static class Config {
        String formula;
        String[] dependencies = new String[0];
        JSRMConstant[]  constants = new JSRMConstant[0];
        String[]  variables = new String[0];
        Function[] functions = new Function[0];

        Config(String formula) {
            this.formula = formula;
        }

        Config withDependencies(String... dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        Config withConstants(JSRMConstant... constants) {
            this.constants = constants;
            return this;
        }

        Config withVariables(String... variables) {
            this.variables = variables;
            return this;
        }

        Config withFunctions(Function... functions) {
            this.functions = functions;
            return this;
        }

        public String getFormula() {
            return formula;
        }

        public Function[] getFunctions() {
            return functions;
        }

        public String[] getDependencies() {
            return dependencies;
        }

        public JSRMConstant[] getConstants() {
            return constants;
        }

        public String[] getVariables() {
            return variables;
        }
    }
}
