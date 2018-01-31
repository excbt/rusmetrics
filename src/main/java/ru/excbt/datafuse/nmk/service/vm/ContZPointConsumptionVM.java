package ru.excbt.datafuse.nmk.service.vm;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.Tuple;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContZPointConsumptionVM {

    private Long contZPointId;
    private String dataType;
    private LocalDateTime consDateTime;
    private String timeDetailType;
    private String contServiceType;


}
