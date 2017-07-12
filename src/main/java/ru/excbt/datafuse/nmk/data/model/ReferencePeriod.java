package ru.excbt.datafuse.nmk.data.model;

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

/**
 * Эталонный интервал
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.06.2015
 *
 */
@Entity
@Table(name = "reference_period")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ReferencePeriod extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 8039676534536052073L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_zpoint_id", insertable = false, updatable = false)
	@JsonIgnore
	private ContZPoint contZPoint;

	@Column(name = "cont_zpoint_id")
	private Long contZPointId;

	@Column(name = "time_detail_type")
	private String timeDetailType;

	@Column(name = "begin_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date beginDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Column(name = "is_auto")
	private Boolean isAuto;

	@Column(name = "period_begin_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date periodBeginDate;

	@Column(name = "period_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date periodEndDate;

	@Column(name = "period_description")
	private String periodDescription;

	@Column(name = "period_comment")
	private String periodComment;

	@Column(name = "is_active")
	private Boolean isActive;

	@Version
	private int version;

}
