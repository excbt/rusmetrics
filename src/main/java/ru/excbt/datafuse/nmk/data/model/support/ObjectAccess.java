package ru.excbt.datafuse.nmk.data.model.support;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 04.07.2017.
 */
@Getter
@Setter
public class ObjectAccess {

    public enum  AccessType {TRIAL, POST_SUBSCRIPTION};

    private AccessType accessType;

    private LocalDate accessTTL;

}
