package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "local_place_temperature_sst")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class LocalPlaceTemperatureSst extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -1767119876044318836L;

	@Column(name = "local_place_id", updatable = false, insertable = false)
	private Long localPlaceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "local_place_id")
	private LocalPlace localPlace;

	@Temporal(TemporalType.DATE)
	@Column(name = "sst_date")
	private Date sstDate;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sst_date_time")
	private Date sstDateTime;

	@Column(name = "sst_value")
	private BigDecimal sstValue;

	@Column(name = "sst_calc_value")
	private BigDecimal sstCalcValue;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sst_calc_date_time")
	private Date sstCalcDateTime;

	@Column(name = "sst_comment")
	private String sstComment;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
