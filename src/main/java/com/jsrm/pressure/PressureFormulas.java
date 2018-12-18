package com.jsrm.pressure;

import com.jsrm.calculation.Formula;
import com.jsrm.calculation.function.CircleAreaFunction;
import com.jsrm.calculation.function.HollowCircleAreaFunction;
import com.jsrm.pressure.function.ErosiveBurnFactorFunction;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

public enum PressureFormulas implements Formula {

    GRAIN_CORE_DIAMETER("GRAIN_CORE_DIAMETER_previous + ci * 2 * xincp",
            empty(),
            of("ci", "xincp", "GRAIN_CORE_DIAMETER_previous")),

    GRAIN_OUTSIDE_DIAMETER("GRAIN_OUTSIDE_DIAMETER_previous - osi * 2 * xincp",
            empty(),
            of("osi", "xincp", "GRAIN_OUTSIDE_DIAMETER_previous")),

    GRAIN_LENGTH("GRAIN_LENGTH_previous-ei*n*2*xincp",
            empty(),
            of("ei", "n", "xincp", "GRAIN_LENGTH_previous")),

    WEB_THICKNESS("(GRAIN_OUTSIDE_DIAMETER - GRAIN_CORE_DIAMETER) / 2",
            of("GRAIN_CORE_DIAMETER", "GRAIN_OUTSIDE_DIAMETER"),
            empty()),

    THROAT_AREA("CircleArea(dto+erate*(two-WEB_THICKNESS)/two)",
            of("WEB_THICKNESS"),
            of("dto", "erate", "two"),
            new CircleAreaFunction()),

    NOZZLE_CRITICAL_PASSAGE_AREA("THROAT_AREA / 1000^2",
            of("THROAT_AREA"),
            empty()),

    //Difference in chamber and grain cross-sectional area (flow area)
    EROSIVE_BURN_FACTOR("ErosiveBurnFactor((CircleArea(dc)-HollowCircleArea(GRAIN_OUTSIDE_DIAMETER, GRAIN_CORE_DIAMETER))/THROAT_AREA, gstar)",
            of("GRAIN_OUTSIDE_DIAMETER", "GRAIN_CORE_DIAMETER", "THROAT_AREA"),
            of("dc", "gstar"),
            new ErosiveBurnFactorFunction(), new HollowCircleAreaFunction(), new CircleAreaFunction()),

    GRAIN_VOLUME("(HollowCircleArea(GRAIN_OUTSIDE_DIAMETER, GRAIN_CORE_DIAMETER) * GRAIN_LENGTH)",
            of("GRAIN_OUTSIDE_DIAMETER", "GRAIN_CORE_DIAMETER", "GRAIN_LENGTH"),
            empty(),
            new HollowCircleAreaFunction()),
    ;

    private final Expression expression;
    private final Set<String> dependencies;

    PressureFormulas(String formula, Stream<String> dependencies, Stream<String>  variables, Function ... functions) {

        ExpressionBuilder expressionBuilder = new ExpressionBuilder(formula);

        this.dependencies = dependencies
                .peek(expressionBuilder::variable)
                .collect(Collectors.toSet());

        expressionBuilder.functions(functions);

        variables.forEach(expressionBuilder::variable);

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
}
