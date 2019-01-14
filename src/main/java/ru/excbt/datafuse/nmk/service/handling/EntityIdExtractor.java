package ru.excbt.datafuse.nmk.service.handling;

@FunctionalInterface
public interface EntityIdExtractor<T> {
    Long getId(T e);
}
