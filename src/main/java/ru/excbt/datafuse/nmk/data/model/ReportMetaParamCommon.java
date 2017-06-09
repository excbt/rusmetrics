package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Общие параметры для типа отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 08.06.2015
 *
 */
@Entity
@Table(name = "report_meta_param_common")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ReportMetaParamCommon implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -569411608287416733L;

	//	@Id
	//	@OneToOne(fetch = FetchType.LAZY)
	//	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	//	@JsonIgnore
	//	private ReportType reportType;

	@Id
	@Column(name = "report_type")
	private String reportTypeKeyname;

	@Column(name = "one_date_required")
	private Boolean oneDateRequired;

	@Column(name = "start_date_required")
	private Boolean startDateRequired;

	@Column(name = "end_date_required")
	private Boolean endDateRequired;

	@Column(name = "one_cont_object_required")
	private Boolean oneContObjectRequired;

	@Column(name = "many_cont_objects_required")
	private Boolean manyContObjectsRequired;

	@Column(name = "many_cont_objects_zip_only")
	private Boolean manyContObjectsZipOnly;

	@Column(name = "no_cont_objects_required")
	private Boolean noContObjectsRequired;

	@Column(name = "is_special_id_param")
	private Boolean isSpecialIdParam;

	@JsonIgnore
	@Version
	private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportMetaParamCommon that = (ReportMetaParamCommon) o;

        return reportTypeKeyname != null ? reportTypeKeyname.equals(that.reportTypeKeyname) : that.reportTypeKeyname == null;
    }

    @Override
    public int hashCode() {
        return reportTypeKeyname != null ? reportTypeKeyname.hashCode() : 0;
    }
}
