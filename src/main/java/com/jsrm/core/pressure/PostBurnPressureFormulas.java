package com.jsrm.core.pressure;

import com.jsrm.calculation.Formula;
import com.jsrm.core.FormulaConfiguration;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Set;
import java.util.stream.Collectors;

import static com.jsrm.core.JSRMConstant.*;
import static java.util.stream.Stream.of;


public enum PostBurnPressureFormulas implements Formula {

    TIME_SINCE_BURN_STARTS(new FormulaConfiguration("TIME_SINCE_BURN_STARTS_previous + tbinc")
            .withConstants(tbinc)
            .withVariables("TIME_SINCE_BURN_STARTS_previous")),

    CHAMBER_PRESSURE_MPA(new FormulaConfiguration("pbout*exp(-rat*to*astarf*(TIME_SINCE_BURN_STARTS-tbout)/vc*1000000000/cstar)")
            .withDependencies("TIME_SINCE_BURN_STARTS")
            .withConstants(pbout, rat, to, astarf, tbout, vc, cstar)),

    //TODO : formule  utile?
    ABSOLUTE_CHAMBER_PRESSURE(new FormulaConfiguration("CHAMBER_PRESSURE_MPA-patm")
            .withDependencies("CHAMBER_PRESSURE_MPA")
            .withConstants(patm)),

    //TODO : formule  utile?
    ABSOLUTE_CHAMBER_PRESSURE_PSIG(new FormulaConfiguration("ABSOLUTE_CHAMBER_PRESSURE*1000000/6895")
            .withDependencies("ABSOLUTE_CHAMBER_PRESSURE"));

    private final Expression expression;
    private final Set<String> dependencies;

    PostBurnPressureFormulas(FormulaConfiguration config) {

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
        return dependencies.stream().map(PostBurnPressureFormulas::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getVariablesNames() {
        return expression.getVariableNames();
    }


}
