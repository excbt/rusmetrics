package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 04.07.2017.
 */
@Getter
@Setter
public class ObjectAccessDTO {

    public interface ObjectAccessInitializer {
        void objectAccess(ObjectAccessDTO.AccessType accessType, LocalDate date);
    }

    public enum  AccessType {TRIAL, POST_SUBSCRIPTION};

    private AccessType accessType;

    private LocalDate accessTTL;

}
