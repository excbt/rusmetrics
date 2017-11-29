package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;

@Mapper(componentModel = "spring", uses = {})
public interface LocalPlaceMapper {

    LocalPlace.LocalPlaceInfo toLocalPlaceInfo(LocalPlace localPlace);

}
