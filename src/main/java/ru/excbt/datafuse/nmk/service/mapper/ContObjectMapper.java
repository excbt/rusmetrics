package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Mapper(componentModel = "spring")
public interface ContObjectMapper {

    ContObjectDTO contObjectToDto (ContObject contObject);

    ContObjectMonitorDTO contObjectToMonitorDto (ContObject contObject);


}
