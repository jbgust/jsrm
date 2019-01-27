package com.github.jbgust.jsrm.infra.performance;

import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.FormulaConfiguration;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.performance.function.NozzleExitPressureFunction;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Set;
import java.util.stream.Collectors;

import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static java.util.stream.Stream.of;

public enum PerformanceFormulas implements Formula {

    CHAMBER_PRESSURE_PA(new FormulaConfiguration("chamberPressureMPA * 1000000")
            .withVariables(chamberPressureMPA.name())
    ),

    NOZZLE_EXPANSION_RATIO(new FormulaConfiguration("aexit/throatArea")
            .withConstants(JSRMConstant.aexit)
            .withVariables(throatArea.name())
    ),

    NOZZLE_EXIT_PRESSURE(new FormulaConfiguration("NozzleExitPressure(CHAMBER_PRESSURE_PA, k2ph, MACH_SPEED_AT_NOZZLE_EXIT, patm)")
            .withDependencies("MACH_SPEED_AT_NOZZLE_EXIT", "CHAMBER_PRESSURE_PA")
            .withConstants(JSRMConstant.k2ph, JSRMConstant.patm)
            .withFunctions(Functions.nozzleExitPressure)
    ),

    OPTIMUM_NOZZLE_EXPANSION_RATIO(new FormulaConfiguration("1/(((k2ph+1)/2)^(1/(k2ph-1))*(patm*1000000/CHAMBER_PRESSURE_PA)^(1/k2ph) * " +
            "sqrt((k2ph+1)/(k2ph-1)*(1-(patm*1000000/CHAMBER_PRESSURE_PA)^((k2ph-1)/k2ph))))")
            .withDependencies("CHAMBER_PRESSURE_PA")
            .withConstants(JSRMConstant.patm, JSRMConstant.k2ph)
    ),

    DELIVERED_THRUST_COEFFICIENT(new FormulaConfiguration("etanoz * " +
            "sqrt(2*k2ph^2/(k2ph-1)*(2/(k2ph+1))^((k2ph+1)/(k2ph-1)) * " +
            "(1-(NOZZLE_EXIT_PRESSURE / CHAMBER_PRESSURE_PA)^((k2ph-1)/k2ph)))+(NOZZLE_EXIT_PRESSURE - patm * 1000000) " +
            "/ CHAMBER_PRESSURE_PA * NOZZLE_EXPANSION_RATIO")
            .withConstants(JSRMConstant.etanoz, JSRMConstant.patm, JSRMConstant.k2ph)
            .withDependencies("NOZZLE_EXPANSION_RATIO", "NOZZLE_EXIT_PRESSURE", "CHAMBER_PRESSURE_PA")
    ),

    THRUST(new FormulaConfiguration("DELIVERED_THRUST_COEFFICIENT * nozzleCriticalPassageArea * CHAMBER_PRESSURE_PA")
            .withDependencies("DELIVERED_THRUST_COEFFICIENT", "CHAMBER_PRESSURE_PA")
            .withVariables(nozzleCriticalPassageArea.name())
    ),

    DELIVERED_IMPULSE(new FormulaConfiguration("(THRUST + THRUST_previous) / 2 * (timeSinceBurnStart - timeSinceBurnStart_previous)")
            .withDependencies("THRUST")
            .withVariables("THRUST_previous", "timeSinceBurnStart_previous", timeSinceBurnStart.name())
    ),

    MACH_SPEED_AT_NOZZLE_EXIT(new FormulaConfiguration("MACH_SPEED_AT_NOZZLE_EXIT_previous - 1 / 834 * (me - mef)")
            .withConstants(JSRMConstant.me, JSRMConstant.mef)
            .withVariables("MACH_SPEED_AT_NOZZLE_EXIT_previous")
    );

    private final Expression expression;
    private final Set<String> dependencies;
    private final String expressionAsString;

    PerformanceFormulas(FormulaConfiguration config) {

        expressionAsString = config.getFormula();
        ExpressionBuilder expressionBuilder = new ExpressionBuilder(expressionAsString);

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
    public String getExpressionAsString() {
        return expressionAsString;
    }

    @Override
    public Set<Formula> getDependencies() {
        return dependencies.stream().map(PerformanceFormulas::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }

    private static class Functions {
        private static final Function nozzleExitPressure = new NozzleExitPressureFunction();
    }

}
