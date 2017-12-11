package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.dto.ActiveDataSourceInfoDTO;

@Mapper(componentModel = "spring", uses = {DataSourceTypeMapper.class})
public interface DeviceObjectDataSourceMapper {

    ActiveDataSourceInfoDTO toActive(DeviceObjectDataSource deviceObjectDataSource);

}
