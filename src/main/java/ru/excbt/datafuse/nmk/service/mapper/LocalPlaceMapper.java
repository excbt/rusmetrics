package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;

@Mapper(componentModel = "spring", uses = {})
public interface LocalPlaceMapper {

    @Mapping(target = "weatherPlaceName", source = "weatherPlace.placeName")
    LocalPlace.LocalPlaceInfo toLocalPlaceInfo(LocalPlace localPlace);

}
