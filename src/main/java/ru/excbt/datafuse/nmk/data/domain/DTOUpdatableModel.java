package ru.excbt.datafuse.nmk.data.domain;

/**
 * Created by kovtonyk on 11.04.2017.
 */
public interface DTOUpdatableModel <T> {
    void updateFromDTO(T dto);
}
