package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;

import java.util.Date;

@Getter
@Setter
public class ContServiceDataHWaterDTO implements DataDateFormatter {

    private Long id;

    private Date dataDate;

    private Long deviceObjectId;

    private Long contZPointId;

    private String timeDetailType;

    private Double t_in;

    private Double t_out;

    private Double t_cold;

    private Double t_outdoor;

    private Double m_in;

    private Double m_out;

    private Double m_delta;

    private Double v_in;

    private Double v_out;

    private Double v_delta;

    private Double h_in;

    private Double h_out;

    private Double h_delta;

    private Double p_in;

    private Double p_out;

    private Double p_delta;

    private Double workTime;

    private Double failTime;

    private Boolean crc32Valid;

    private Short dataMstatus;

    private Boolean dataChanged;
}
