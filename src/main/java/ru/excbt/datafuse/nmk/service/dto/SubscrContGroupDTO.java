package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.ModelId;

@Getter
@Setter
public class SubscrContGroupDTO implements ModelId<Long> {

    private Long id;

    private ContGroupTypeDTO contGroupType;

    private String contGroupTypeKey;

    private String contGroupName;

    private String contGroupComment;

    private String contGroupDescription;

    private int version;

    private Long subscriberId;

}
