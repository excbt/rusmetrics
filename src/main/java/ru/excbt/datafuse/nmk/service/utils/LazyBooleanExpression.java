package ru.excbt.datafuse.nmk.service.utils;


import com.querydsl.core.types.dsl.BooleanExpression;

@FunctionalInterface
public interface LazyBooleanExpression
{
    BooleanExpression get();
}
