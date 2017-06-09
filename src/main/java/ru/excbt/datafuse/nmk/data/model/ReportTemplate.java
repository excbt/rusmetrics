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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;

/**
 * Шаблон отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table(name = "report_template")
@SQLDelete(sql = "UPDATE report_template SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause = "deleted <> 1")
@Getter
@Setter
public class ReportTemplate extends JsonAbstractAuditableModel implements PersistableBuilder<ReportTemplate,Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 6041687194914761423L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "report_type")
	private String reportTypeKeyname;

	//	@ManyToOne(fetch = FetchType.EAGER)
	//	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	//	private ReportType reportType;

	@Column(name = "report_template_name")
	private String name;

	@Column(name = "report_template_description")
	private String description;

	@Column(name = "report_template_comment")
	private String comment;

	@Column(name = "is_default")
	private boolean _default = false;

	@Column(name = "is_active")
	private boolean _active = false;

	@Column(name = "active_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeStartDate;

	@Column(name = "active_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeEndDate;

	@Column(name = "src_report_template_id")
	private Long srcReportTemplateId;

	@Column(name = "is_integrator_included")
	private Boolean integratorIncluded;

	@Version
	private int version;

	@Column(name = "report_paramset_default_name")
	private String reportParamsetDefaultName;

}
