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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

/**
 * Электричество - потребление
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
@Entity
@Table(name = "cont_service_data_el_cons")
@Getter
@Setter
public class ContServiceDataElCons extends AbstractAuditableModel implements DataDateFormatter, DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = -2765914644167033303L;

	@Column(name = "data_date", updatable = false)
	private Date dataDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id", updatable = false)
	@JsonIgnore
	private DeviceObject deviceObject;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_zpoint_id", insertable = false, updatable = false)
	@JsonIgnore
	private ContZPoint contZPoint;

	@Column(name = "cont_zpoint_id" ,updatable = false)
	@JsonIgnore
	private Long contZPointId;

	@Column(name = "time_detail_type" ,updatable = false)
	private String timeDetailType;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	// 1
	@Column(name = "p_ap_1", columnDefinition = "numeric")
	private Double p_Ap1;

	@Column(name = "p_an_1", columnDefinition = "numeric")
	private Double p_An1;

	@Column(name = "q_rp_1", columnDefinition = "numeric")
	private Double q_Rp1;

	@Column(name = "q_rn_1", columnDefinition = "numeric")
	private Double q_Rn1;

	// 2
	@Column(name = "p_ap_2", columnDefinition = "numeric")
	private Double p_Ap2;

	@Column(name = "p_an_2", columnDefinition = "numeric")
	private Double p_An2;

	@Column(name = "q_rp_2", columnDefinition = "numeric")
	private Double q_Rp2;

	@Column(name = "q_rn_2", columnDefinition = "numeric")
	private Double q_Rn2;

	// 3
	@Column(name = "p_ap_3", columnDefinition = "numeric")
	private Double p_Ap3;

	@Column(name = "p_an_3", columnDefinition = "numeric")
	private Double p_An3;

	@Column(name = "q_rp_3", columnDefinition = "numeric")
	private Double q_Rp3;

	@Column(name = "q_rn_3", columnDefinition = "numeric")
	private Double q_Rn3;

	// 4
	@Column(name = "p_ap_4", columnDefinition = "numeric")
	private Double p_Ap4;

	@Column(name = "p_an_4", columnDefinition = "numeric")
	private Double p_An4;

	@Column(name = "q_rp_4", columnDefinition = "numeric")
	private Double q_Rp4;

	@Column(name = "q_rn_4", columnDefinition = "numeric")
	private Double q_Rn4;

	// 5
	@Column(name = "p_ap_5", columnDefinition = "numeric")
	private Double p_Ap5;

	@Column(name = "p_an_5", columnDefinition = "numeric")
	private Double p_An5;

	@Column(name = "q_rp_5", columnDefinition = "numeric")
	private Double q_Rp5;

	@Column(name = "q_rn_5", columnDefinition = "numeric")
	private Double q_Rn5;

	// All
	@Column(name = "p_ap", columnDefinition = "numeric")
	private Double p_Ap;

	@Column(name = "p_an", columnDefinition = "numeric")
	private Double p_An;

	@Column(name = "q_rp", columnDefinition = "numeric")
	private Double q_Rp;

	@Column(name = "q_rn", columnDefinition = "numeric")
	private Double q_Rn;

	//	@Column(name = "crc32_value", insertable = false, updatable = false)
	//	private Integer crc32Value;

	@Column(name = "crc32_valid", insertable = false, updatable = false)
	private Boolean crc32Valid;

	@Column(name = "data_mstatus")
	private Short dataMstatus;

	@Column(name = "data_changed", insertable = false, updatable = false)
	private Boolean dataChanged;

}
