package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DeviceObjectShortInfoDTO {

    private Long id;

    private Long contObjectId;

    private Long deviceModelId;

    private String deviceModelName;

    private String number;

    private LocalDateTime verificationDate;

    private String deviceObjectName;

//    public String getVerificationDateStr() {
//        return verificationDate == null ? null : DateFormatUtils.formatLocalDateTime(verificationDate);
//    }
}
