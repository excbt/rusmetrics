package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrDataSourceDTO;

@Mapper(componentModel = "spring", uses = {DataSourceTypeMapper.class})
public interface SubscrDataSourceMapper extends EntityMapper<SubscrDataSourceDTO, SubscrDataSource> {
}
