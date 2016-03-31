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

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "local_place_temperature_sst")
@JsonIgnoreProperties(ignoreUnknown = true)
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

	public Long getLocalPlaceId() {
		return localPlaceId;
	}

	public void setLocalPlaceId(Long localPlaceId) {
		this.localPlaceId = localPlaceId;
	}

	public LocalPlace getLocalPlace() {
		return localPlace;
	}

	public void setLocalPlace(LocalPlace localPlace) {
		this.localPlace = localPlace;
	}

	public Date getSstDate() {
		return sstDate;
	}

	public void setSstDate(Date sstDate) {
		this.sstDate = sstDate;
	}

	public BigDecimal getSstValue() {
		return sstValue;
	}

	public void setSstValue(BigDecimal sstValue) {
		this.sstValue = sstValue;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getSstComment() {
		return sstComment;
	}

	public void setSstComment(String sstComment) {
		this.sstComment = sstComment;
	}

	public Date getSstDateTime() {
		return sstDateTime;
	}

	public void setSstDateTime(Date sstDateTime) {
		this.sstDateTime = sstDateTime;
	}

	public BigDecimal getSstCalcValue() {
		return sstCalcValue;
	}

	public void setSstCalcValue(BigDecimal sstCalcValue) {
		this.sstCalcValue = sstCalcValue;
	}

	public Date getSstCalcDateTime() {
		return sstCalcDateTime;
	}

	public void setSstCalcDateTime(Date sstCalcDateTime) {
		this.sstCalcDateTime = sstCalcDateTime;
	}

}