package ru.excbt.datafuse.nmk.service.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;
import java.util.function.Function;

public class WhereClauseBuilder implements Predicate, Cloneable
{
    private BooleanBuilder delegate;

    public WhereClauseBuilder()
    {
        this.delegate = new BooleanBuilder();
    }

    public WhereClauseBuilder(Predicate pPredicate)
    {
        this.delegate = new BooleanBuilder(pPredicate);
    }

    public WhereClauseBuilder and(Predicate right)
    {
        return new WhereClauseBuilder(delegate.and(right));
    }

    public <V> WhereClauseBuilder optionalAnd(@Nullable V pValue, LazyBooleanExpression pBooleanExpression)
    {
        return applyIfNotNull(pValue, this::and, pBooleanExpression);
    }

    private <V> WhereClauseBuilder applyIfNotNull(@Nullable V pValue, Function<Predicate, WhereClauseBuilder> pFunction, LazyBooleanExpression pBooleanExpression)
    {
        if (pValue != null)
        {
            return new WhereClauseBuilder(pFunction.apply(pBooleanExpression.get()));
        }

        return this;
    }

    @Override
    public Predicate not() {
        return delegate.not();
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
        return delegate.accept(visitor, c);
    }

    @Override
    public Class<? extends Boolean> getType() {
        return delegate.getType();
    }

    @Override
    public String toString() {
        return "WhereClauseBuilder{" +
            "delegate=" + delegate +
            '}';
    }
}
