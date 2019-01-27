package com.github.jbgust.jsrm.infra.pressure.csv;

import org.junit.jupiter.params.aggregator.AggregateWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(DuringBurnPressureCsvLineAggregator.class)
public @interface CsvToDuringBurnPressureLine {
}
