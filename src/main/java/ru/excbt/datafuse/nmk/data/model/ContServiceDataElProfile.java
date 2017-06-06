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

/**
 * Электричество - профиль
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
@Entity
@Table(name = "cont_service_data_el_profile")
@Getter
@Setter
public class ContServiceDataElProfile extends AbstractAuditableModel implements DataDateFormatter {

	/**
	 *
	 */
	private static final long serialVersionUID = -7484466554217659824L;

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

	@Column(name = "profile_interval")
	private Integer profileInterval;

	@Column(name = "p_ap", columnDefinition = "numeric")
	private Double p_Ap;

	@Column(name = "p_an", columnDefinition = "numeric")
	private Double p_An;

	@Column(name = "q_rp", columnDefinition = "numeric")
	private Double q_Rp;

	@Column(name = "q_rn", columnDefinition = "numeric")
	private Double q_Rn;

}
