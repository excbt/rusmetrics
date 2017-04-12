package ru.excbt.datafuse.nmk.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.excbt.datafuse.nmk.data.model.modelmapper.ModelMapperUtil;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;

/**
 * Created by kovtonyk on 11.04.2017.
 */
public interface DTOModel<T> {
    @JsonIgnore
    T getDTO();
}
