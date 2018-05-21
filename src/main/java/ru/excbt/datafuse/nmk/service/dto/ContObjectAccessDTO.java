package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContObjectAccessDTO {

    private Long contObjectId;

    private Long subscriberId;

    private String contObjectName;

    private String contObjectFullName;

    private String contObjectFullAddress;

    private String contObjectNumber;

    private String accessType;

    private LocalDateTime accessTtl;
}
