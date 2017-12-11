package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.dto.DataSourceTypeDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;

@Mapper(componentModel = "spring", uses = {})
public interface DataSourceTypeMapper extends EntityMapper<DataSourceTypeDTO, DataSourceType> {
}
