package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "report_shedule_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class ReportSheduleType extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -8575914982173599701L;

	@Column(name="caption")
	private String caption;

	@Column(name="report_shedule_type_name")
	private String name;

	@Column(name="report_shedule_type_description")
	private String description;

	@Column(name="report_shedule_type_comment")
	private String comment;

	@Version
	private int version;

}
