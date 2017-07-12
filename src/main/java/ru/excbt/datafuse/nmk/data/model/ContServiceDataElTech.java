package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Электричество - технические характеристики
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
@Entity
@Table(name = "cont_service_data_el_tech")
@Getter
@Setter
public class ContServiceDataElTech extends AbstractAuditableModel implements DataDateFormatter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1354288648021141991L;

	private static final double DIV_VALUE = 1000.00;

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

	@Column(name = "cont_zpoint_id", updatable = false)
	@JsonIgnore
	private Long contZPointId;

	@Column(name = "time_detail_type", updatable = false)
	private String timeDetailType;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "u_1", columnDefinition = "numeric")
	private Double u1;

	@Column(name = "u_2", columnDefinition = "numeric")
	private Double u2;

	@Column(name = "u_3", columnDefinition = "numeric")
	private Double u3;

	@Column(name = "i_1", columnDefinition = "numeric")
	private Double i1;

	@Column(name = "i_2", columnDefinition = "numeric")
	private Double i2;

	@Column(name = "i_3", columnDefinition = "numeric")
	private Double i3;

	@Column(name = "k_1", columnDefinition = "numeric")
	private Double k1;

	@Column(name = "k_2", columnDefinition = "numeric")
	private Double k2;

	@Column(name = "k_3", columnDefinition = "numeric")
	private Double k3;

	@Column(name = "frequency", columnDefinition = "numeric")
	private Double frequency;

	@Column(name = "device_temp", columnDefinition = "numeric")
	private Double deviceTemp;


	public Double getI1_c() {
		return this.i1 == null ? null : (this.i1 / DIV_VALUE);// .divide(BigDecimal.valueOf(1000)).setScale(6, RoundingMode.CEILING);
	}

	public Double getI2_c() {
		return this.i2 == null ? null : (this.i2 / DIV_VALUE); //.divide(BigDecimal.valueOf(1000)).setScale(6, RoundingMode.CEILING);
	}

	public Double getI3_c() {
		return this.i3 == null ? null : (this.i3 / DIV_VALUE); //.divide(BigDecimal.valueOf(1000)).setScale(6, RoundingMode.CEILING);
	}

}
