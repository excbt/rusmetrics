package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLInsert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

/**
 * Учет показаний ГВС и Теплоснабжения
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 */
@Entity
@Table(name = "cont_service_data_hwater")
//@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"dataDate", "timeDetailType", "workTime", "failTime"})
@SQLInsert(
    sql = "insert into cont_service_data_hwater (created_by, last_modified_by, last_modified_date, cont_zpoint_id, data_date, data_mstatus, "
        + " deleted, device_object_id, fail_time, h_delta, h_in, h_out, m_delta, m_in, m_out, p_delta, p_in, p_out, t_cold, t_in, t_out, t_outdoor, "
        + " time_detail_type, v_delta, v_in, v_out, version, work_time, id) "
        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
    check = ResultCheckStyle.NONE)
@Getter
@Setter
public class ContServiceDataHWater extends AbstractAuditableModel implements DataDateFormatter, DeletedMarker {

    /**
     *
     */
    private static final long serialVersionUID = -6897555657365451006L;

    @Column(name = "data_date")
    private Date dataDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_object_id")
    @JsonIgnore
    private DeviceObject deviceObject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cont_zpoint_id", insertable = false, updatable = false)
    @JsonIgnore
    private ContZPoint contZPoint;

    @Column(name = "cont_zpoint_id")
    @JsonIgnore
    private Long contZPointId;

    @Column(name = "time_detail_type")
    private String timeDetailType;

    @JsonIgnore
    @Version
    private int version;

    @JsonIgnore
    @Column(name = "deleted")
    private int deleted;

    @Column(columnDefinition = "numeric")
    private Double t_in;

    @Column(columnDefinition = "numeric")
    private Double t_out;

    @Column(columnDefinition = "numeric")
    private Double t_cold;

    @Column(columnDefinition = "numeric")
    private Double t_outdoor;

    @Column(columnDefinition = "numeric")
    private Double m_in;

    @Column(columnDefinition = "numeric")
    private Double m_out;

    @Column(columnDefinition = "numeric")
    private Double m_delta;

    @Column(columnDefinition = "numeric")
    private Double v_in;

    @Column(columnDefinition = "numeric")
    private Double v_out;

    @Column(columnDefinition = "numeric")
    private Double v_delta;

    @Column(columnDefinition = "numeric")
    private Double h_in;

    @Column(columnDefinition = "numeric")
    private Double h_out;

    @Column(columnDefinition = "numeric")
    private Double h_delta;

    @Column(columnDefinition = "numeric")
    private Double p_in;

    @Column(columnDefinition = "numeric")
    private Double p_out;

    @Column(columnDefinition = "numeric")
    private Double p_delta;

    @Column(name = "work_time", columnDefinition = "numeric")
    private Double workTime;

    @Column(name = "fail_time", columnDefinition = "numeric")
    private Double failTime;

    //	@Column(name = "crc32_value", insertable = false, updatable = false)
    //	private Integer crc32Value;

    @Column(name = "crc32_valid", insertable = false, updatable = false)
    private Boolean crc32Valid;

    @Column(name = "data_mstatus")
    private Short dataMstatus;

    @Column(name = "data_changed", insertable = false, updatable = false)
    private Boolean dataChanged;

}
