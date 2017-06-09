package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Объект учета и управляющая огранизация
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Entity
@Table(name = "cont_management")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ContManagement extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1593276105973101936L;

	@Column(name = "agreement_nr")
	private String agreementNr;

	@Column(name = "agreement_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date agreementDate;

	@Column(name = "cm_begin_date")
	@Temporal(TemporalType.DATE)
	private Date beginDate;

	@Column(name = "cm_end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(name = "organization_id", insertable = false, updatable = false)
	private Long organizationId;

	@Column(name = "reports_path")
	private String reportsPath;

	@Version
	private int version;

}
