package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;

/**
 * Mapper for the entity CabinetMessage and its DTO CabinetMessageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CabinetMessageMapper extends EntityMapper <CabinetMessageDTO, CabinetMessage> {


    default CabinetMessage fromId(Long id) {
        if (id == null) {
            return null;
        }
        CabinetMessage cabinetMessage = new CabinetMessage();
        cabinetMessage.setId(id);
        return cabinetMessage;
    }
}
